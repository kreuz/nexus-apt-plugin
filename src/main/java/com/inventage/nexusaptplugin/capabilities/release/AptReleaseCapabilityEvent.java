package com.inventage.nexusaptplugin.capabilities.release;

import org.sonatype.nexus.events.AbstractEvent;

/**
 * Update event originating in the {@link AptReleaseCapability}.
 *
 * @author Dominik Menzi
 */
public class AptReleaseCapabilityEvent extends AbstractEvent<AptReleaseCapability> {

    private final AptReleaseCapabilityConfiguration aptReleaseCapabilityConfiguration;

    public AptReleaseCapabilityEvent(AptReleaseCapability component, AptReleaseCapabilityConfiguration aptReleaseCapabilityConfiguration) {
        super(component);
        this.aptReleaseCapabilityConfiguration = aptReleaseCapabilityConfiguration;
    }

    /**
     * @return the new configuration
     */
    public AptReleaseCapabilityConfiguration getAptReleaseCapabilityConfiguration() {
        return aptReleaseCapabilityConfiguration;
    }
}
