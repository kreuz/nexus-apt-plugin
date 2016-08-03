package com.inventage.nexusaptplugin.capabilities.signing;

/**
 * Event representing the updating of apt configuration.
 *
 * @author Dominik Menzi
 */
public class AptSigningCapabilityUpdatedEvent extends AptSigningCapabilityEvent {

    private final AptSigningCapabilityConfiguration aptSigningCapabilityConfiguration;

    public AptSigningCapabilityUpdatedEvent(AptSigningCapability component, AptSigningCapabilityConfiguration aptSigningCapabilityConfiguration) {
        super(component);
        this.aptSigningCapabilityConfiguration = aptSigningCapabilityConfiguration;
    }

    /**
     * @return the new configuration
     */
    public AptSigningCapabilityConfiguration getAptSigningCapabilityConfiguration() {
        return aptSigningCapabilityConfiguration;
    }
}
