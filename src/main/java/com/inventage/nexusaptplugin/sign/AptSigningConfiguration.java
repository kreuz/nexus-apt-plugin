package com.inventage.nexusaptplugin.sign;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;

import javax.inject.Named;
import javax.inject.Singleton;

import org.bouncycastle.openpgp.PGPException;
import org.sonatype.nexus.events.EventSubscriber;

import com.google.common.eventbus.Subscribe;

import com.inventage.nexusaptplugin.capabilities.signing.AptSigningCapabilityConfiguration;
import com.inventage.nexusaptplugin.capabilities.signing.AptSigningCapabilityDeactivatedEvent;
import com.inventage.nexusaptplugin.capabilities.signing.AptSigningCapabilityUpdatedEvent;

@Named
@Singleton
public class AptSigningConfiguration implements EventSubscriber {

    private String keyring;
    private String key;
    private String passphrase;

    @Subscribe
    public void onCapabilityUpdated(AptSigningCapabilityUpdatedEvent event) {
        final AptSigningCapabilityConfiguration aptSigningCapabilityConfiguration = event.getAptSigningCapabilityConfiguration();
        keyring = aptSigningCapabilityConfiguration.getKeyring();
        key = aptSigningCapabilityConfiguration.getKey();
        passphrase = aptSigningCapabilityConfiguration.getPassphrase();
    }

    @Subscribe
    public void onCapabilityDeactivated(AptSigningCapabilityDeactivatedEvent event) {
        keyring = null;
        key = null;
        passphrase = null;
    }

    public PGPSigner getSigner() throws IOException, PGPException {
        String ring = keyring;
        if (ring == null || ring.isEmpty()) {
            ring = null;
            LinkedList<String> possibleLocations = new LinkedList<String>(getPossiblePGPSecureRingLocations());
            for (String location : possibleLocations) {
                if (new File(location).exists()) {
                    ring = location;
                    break;
                }
            }
            // If it's still null, throw a FileNotFoundException
            if (ring == null) {
                throw new FileNotFoundException("Keyring location is not set and it could not be found automatically");
            }
        }
        FileInputStream stream = new FileInputStream(ring);
        return new PGPSigner(stream, key, passphrase);
    }

    /**
     * Get the possible locations where the secure keyring can be located.
     * Looks through known locations of the GNU PG secure keyring.
     * return null;
     *
     * @return The location of the PGP secure keyring if it was found,
     * null otherwise
     */
    private static Collection<String> getPossiblePGPSecureRingLocations() {
        LinkedHashSet<String> locations = new LinkedHashSet<String>();

        // The user's roaming profile on Windows, via environment
        String windowsRoaming = System.getenv("APPDATA");
        if (windowsRoaming != null) {
            locations.add(joinPaths(windowsRoaming, "gnupg", "secring.gpg"));
        }

        // The user's local profile on Windows, via environment
        String windowsLocal = System.getenv("LOCALAPPDATA");
        if (windowsLocal != null) {
            locations.add(joinPaths(windowsLocal, "gnupg", "secring.gpg"));
        }

        // The user's home directory
        String home = System.getProperty("user.home");
        if (home != null) {
            // *nix, including OS X
            locations.add(joinPaths(home, ".gnupg", "secring.gpg"));

            // These are for various flavours of Windows if the environment variables above should fail
            locations.add(joinPaths(home, "AppData", "Roaming", "gnupg", "secring.gpg")); // Roaming profile on Vista and later
            locations.add(joinPaths(home, "AppData", "Local", "gnupg", "secring.gpg")); // Local profile on Vista and later
            locations.add(joinPaths(home, "Application Data", "gnupg", "secring.gpg")); // Roaming profile on 2000 and XP
            locations.add(joinPaths(home, "Local Settings", "Application Data", "gnupg", "secring.gpg")); // Local profile on 2000 and XP
        }

        // The Windows installation directory
        String windir = System.getProperty("WINDIR");
        if (windir != null) {
            // Local Profile on Windows 98 and ME
            locations.add(joinPaths(windir, "Application Data", "gnupg", "secring.gpg"));
        }

        return locations;
    }

    /**
     * Join together path elements with File.separator. Filters out null
     * elements.
     *
     * @param elements The path elements to join
     * @return elements concatenated together with File.separator
     */
    private static String joinPaths(String... elements) {
        StringBuilder builder = new StringBuilder();
        boolean first = true;
        for (int i = 0; i < elements.length; i++) {
            // Skip null elements
            if (elements[i] == null) {
                // This won't change the value of first if we skip elements
                // in the beginning of the array
                continue;
            }
            if (!first) {
                builder.append(File.separatorChar);
            }
            builder.append(elements[i]);
            first = false;
        }
        return builder.toString();
    }

}
