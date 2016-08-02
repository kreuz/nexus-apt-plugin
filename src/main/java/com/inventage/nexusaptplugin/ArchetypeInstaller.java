
package com.inventage.nexusaptplugin;

import java.io.IOException;
import java.util.EnumSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.sonatype.nexus.events.EventSubscriber;
import org.sonatype.nexus.proxy.AccessDeniedException;
import org.sonatype.nexus.proxy.IllegalOperationException;
import org.sonatype.nexus.proxy.ItemNotFoundException;
import org.sonatype.nexus.proxy.RepositoryNotAvailableException;
import org.sonatype.nexus.proxy.ResourceStoreRequest;
import org.sonatype.nexus.proxy.events.RepositoryConfigurationUpdatedEvent;
import org.sonatype.nexus.proxy.events.RepositoryItemEventDelete;
import org.sonatype.nexus.proxy.events.RepositoryItemEventStore;
import org.sonatype.nexus.proxy.events.RepositoryRegistryEventAdd;
import org.sonatype.nexus.proxy.events.RepositoryRegistryEventRemove;
import org.sonatype.nexus.proxy.item.DefaultStorageFileItem;
import org.sonatype.nexus.proxy.item.StorageItem;
import org.sonatype.nexus.proxy.item.StringContentLocator;
import org.sonatype.nexus.proxy.registry.ContentClass;
import org.sonatype.nexus.proxy.repository.GroupRepository;
import org.sonatype.nexus.proxy.repository.HostedRepository;
import org.sonatype.nexus.proxy.repository.ProxyRepository;
import org.sonatype.nexus.proxy.repository.Repository;
import org.sonatype.nexus.proxy.storage.UnsupportedStorageOperationException;

import com.google.common.eventbus.Subscribe;

import com.inventage.nexusaptplugin.capabilities.signing.AptSigningCapabilityActivatedEvent;
import com.inventage.nexusaptplugin.capabilities.signing.AptSigningCapabilityDeactivatedEvent;

/**
 * EventInspector that listens to registry events, repo addition and removal, and simply "hooks" in the generated
 * Packages.gz file to their root.
 *
 * @author cstamas
 */
@Singleton
@Named("deb")
public class ArchetypeInstaller implements EventSubscriber {

    private final Logger logger;

    private final ContentClass maven2ContentClass;

    private final EnumSet<ArchetypeCatalog> activeCatalogs = EnumSet.of(ArchetypeCatalog.DEFAULT);
    private final ConcurrentMap<String, Repository> configuredRepositories = new ConcurrentHashMap<String, Repository>();

    @Inject
    public ArchetypeInstaller(Logger logger, @Named("maven2") ContentClass maven2ContentClass) {
        this.logger = logger;
        this.maven2ContentClass = maven2ContentClass;
    }

    @Subscribe
    public void onAptSigningActivated(AptSigningCapabilityActivatedEvent event) {
        activeCatalogs.add(ArchetypeCatalog.SIGNING);
        for (Repository repository : configuredRepositories.values()) {
            installArchetypeCatalog(repository, ArchetypeCatalog.SIGNING);
        }
    }

    @Subscribe
    public void onAptSigningDeactivated(AptSigningCapabilityDeactivatedEvent event) {
        activeCatalogs.remove(ArchetypeCatalog.SIGNING);
        for (Repository repository : configuredRepositories.values()) {
            removeArcheTypeCatalogIfPresent(repository, ArchetypeCatalog.SIGNING);
        }
    }

    @Subscribe
    public void onRepositoryRegistryEventAdd(RepositoryRegistryEventAdd evt) {
        registerNewRepo(evt.getRepository());
    }

    @Subscribe
    public void onRepositoryRegistryEventRemove(RepositoryRegistryEventRemove evt) {
        deregisterRepo(evt.getRepository());
    }

    @Subscribe
    public void onRepositoryConfigurationUpdatedEvent(RepositoryConfigurationUpdatedEvent evt) {
        if (isRepositoryInService(evt.getRepository())) {
            registerNewRepo(evt.getRepository());
        } else {
            deregisterRepo(evt.getRepository());
        }
    }

    private boolean isRepositoryInService(Repository repository) {
        return repository.getLocalStatus().shouldServiceRequest();
    }

    private void registerNewRepo(Repository repository) {
        installArcheTypeCatalogIfCompatible(repository);
        configuredRepositories.put(repository.getId(), repository);
    }

    private void deregisterRepo(Repository repository) {
        final Repository registeredRepo = configuredRepositories.remove(repository.getId());
        if (registeredRepo == null) {
            return;
        }
        removeAllArcheTypeCatalogsIfPresent(registeredRepo);
    }

    private void installArcheTypeCatalogIfCompatible(Repository repository) {
        if (!isRepositoryCompatible(repository)) {
            return;
        }
        installActiveArchetypeCatalogs(repository);
    }

    private boolean isRepositoryCompatible(Repository repository) {
        // check is it a maven2 content, and either a "hosted", "proxy" or "group" repository
        return maven2ContentClass.isCompatible(repository.getRepositoryContentClass())
                && (repository.getRepositoryKind().isFacetAvailable(HostedRepository.class)
                || repository.getRepositoryKind().isFacetAvailable(ProxyRepository.class) || repository.getRepositoryKind().isFacetAvailable(
                GroupRepository.class));
    }

    private void installActiveArchetypeCatalogs(Repository repository) {
        for (ArchetypeCatalog activeCatalog : activeCatalogs) {
            installArchetypeCatalog(repository, activeCatalog);
        }
    }

    private void installArchetypeCatalog(Repository repository, ArchetypeCatalog archetypeCatalog) {
        // new repo added or enabled, "install" the archetype catalogs
        try {
            for (Map.Entry<String, String> entry :  archetypeCatalog.getCatalogFiles().entrySet()) {
                final String filePath = entry.getKey();
                final String generatorId = entry.getValue();
                storeItem(repository, filePath, generatorId);
            }
        }
        catch (RepositoryNotAvailableException e) {
            logger.info("Unable to install the generated archetype catalog, repository \""
                    + e.getRepository().getId() + "\" is out of service.");
        }
        catch (Exception e) {
            if (logger.isDebugEnabled()) {
                logger.info("Unable to install the generated archetype catalog!", e);
            }
            else {
                logger.info("Unable to install the generated archetype catalog:" + e.getMessage());
            }
        }
    }

    private void removeAllArcheTypeCatalogsIfPresent(Repository repository) {
        for (ArchetypeCatalog activeCatalog : ArchetypeCatalog.values()) {
            removeArcheTypeCatalogIfPresent(repository, activeCatalog);
        }
    }

    private void removeArcheTypeCatalogIfPresent(Repository repository, ArchetypeCatalog archetypeCatalog) {
        for (Map.Entry<String, String> entry : archetypeCatalog.getCatalogFiles().entrySet()) {
            final String filePath = entry.getKey();
            final ResourceStoreRequest request = new ResourceStoreRequest(filePath);
            try {
                repository.deleteItem(request);
            }
            catch (UnsupportedStorageOperationException e) {
                logger.error("e", e);
            }
            catch (ItemNotFoundException e) {
                logger.error("e", e);
            }
            catch (IllegalOperationException e) {
                logger.error("e", e);
            }
            catch (IOException e) {
                logger.error("e", e);
            }
            catch (AccessDeniedException e) {
                logger.error("e", e);
            }
        }
    }

    @Subscribe
    public void onRepositoryItemEventStore(RepositoryItemEventStore evt) {
        updateDebItemInRepository(evt.getRepository(), evt.getItem());
    }

    @Subscribe
    public void onRepositoryItemEventDelete(RepositoryItemEventDelete evt) {
        updateDebItemInRepository(evt.getRepository(), evt.getItem());
    }

    private void updateDebItemInRepository(Repository repository, StorageItem item) {
        if (item.getName().toLowerCase().endsWith(".deb") && configuredRepositories.containsKey(repository.getId())) {
            updateFileModificationDates(repository);
        }
    }

    /**
     * Update the modification time of all items stored by the plugin
     *
     * @param repository The repository to update files in
     */
    private void updateFileModificationDates(Repository repository) {
        for (ArchetypeCatalog activeCatalog : activeCatalogs) {
            for (Map.Entry<String, String> entry : activeCatalog.getCatalogFiles().entrySet()) {
                try {
                    updateOrInstallStorageItem(repository, entry.getKey(), entry.getValue());
                }
                catch (AccessDeniedException e) {
                    logger.error("e", e);
                }
                catch (IllegalOperationException e) {
                    logger.error("e", e);
                }
                catch (UnsupportedStorageOperationException e) {
                    logger.error("e", e);
                }
                catch (IOException e) {
                    logger.error("e", e);
                }
            }
        }
    }

    private void updateOrInstallStorageItem(Repository repository, String path, String generatorId) throws IllegalOperationException, IOException, AccessDeniedException, UnsupportedStorageOperationException {
        try {
            // Retrieve the item, update the modification time and save it
            final ResourceStoreRequest request = new ResourceStoreRequest(path);
            StorageItem item = repository.retrieveItem(request);
            item.getRepositoryItemAttributes().setModified(System.currentTimeMillis());
            repository.storeItem(false, item);
        }
        catch (ItemNotFoundException e) {
            logger.info("Storage item not found, creating new item");
            storeItem(repository, path, generatorId);
        }
    }

    private void storeItem(Repository repository, String filePath, String generatorId) throws UnsupportedStorageOperationException, IllegalOperationException, IOException {
        DefaultStorageFileItem file =
                new DefaultStorageFileItem(repository,
                        new ResourceStoreRequest(filePath), true, false,
                        new StringContentLocator(generatorId));
        file.setContentGeneratorId(generatorId);
        repository.storeItem(false, file);
    }
}
