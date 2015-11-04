package com.inventage.nexusaptplugin.capabilities;

/**
 * Event representing the updating of apt signing, e.g. a configuration change.
 *
 * @author Dominik Menzi
 */
public class AptSigningUpdatedEvent extends AptCapabilityEvent {

    private final AptCapabilityConfiguration aptCapabilityConfiguration;

    public AptSigningUpdatedEvent(AptCapability component, AptCapabilityConfiguration aptCapabilityConfiguration) {
        super(component);
        this.aptCapabilityConfiguration = aptCapabilityConfiguration;
    }

    /**
     * @return the new configuration
     */
    public AptCapabilityConfiguration getAptCapabilityConfiguration() {
        return aptCapabilityConfiguration;
    }
}
