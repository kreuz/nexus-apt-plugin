package com.inventage.nexusaptplugin.capabilities.legacy;

import java.util.Collection;
import java.util.HashMap;

import javax.annotation.Nullable;
import javax.inject.Named;

import org.eclipse.sisu.EagerSingleton;
import org.sonatype.nexus.plugins.capabilities.CapabilityReference;
import org.sonatype.nexus.plugins.capabilities.CapabilityRegistry;
import org.sonatype.nexus.plugins.capabilities.support.CapabilityBooterSupport;

import com.google.common.base.Predicate;

import com.inventage.nexusaptplugin.capabilities.signing.AptSigningCapability;
import com.inventage.nexusaptplugin.capabilities.signing.AptSigningCapabilityConfiguration;

@Named
@EagerSingleton
public class AptLegacyCapabilitiesConverter
        extends CapabilityBooterSupport {

    @Override
    protected void boot(final CapabilityRegistry registry) throws Exception {
        final Collection<? extends CapabilityReference> legacyCaps = registry.get(new Predicate<CapabilityReference>() {
            @Override
            public boolean apply(@Nullable CapabilityReference input) {
                if (input == null) {
                    return false;
                } else {
                    return AptLegacyCapabilityDescriptor.TYPE.equals(input.context().type());
                }
            }
        });
        // Prevent ConcurrentModificationException by fetching eagerly.
        final CapabilityReference[] legacyCapsArray = legacyCaps.toArray(new CapabilityReference[0]);
        for (CapabilityReference legacyCap : legacyCapsArray) {
            final AptLegacyCapability aptLegacyCapability = legacyCap.capabilityAs(AptLegacyCapability.class);
            final AptLegacyCapabilityConfiguration aptLegacyCapabilityConfiguration = aptLegacyCapability.getConfig();
            final HashMap<String, String> newConfig = new HashMap<String, String>();
            newConfig.put(AptSigningCapabilityConfiguration.KEYRING, aptLegacyCapabilityConfiguration.getKeyring());
            newConfig.put(AptSigningCapabilityConfiguration.KEY, aptLegacyCapabilityConfiguration.getKey());
            newConfig.put(AptSigningCapabilityConfiguration.PASSPHRASE, aptLegacyCapabilityConfiguration.getPassphrase());
            maybeAddCapability(
                    registry,
                    AptSigningCapability.TYPE,
                    true, // enabled
                    null, // no notes
                    newConfig
            );
            registry.disable(legacyCap.context().id());
        }
    }

}
