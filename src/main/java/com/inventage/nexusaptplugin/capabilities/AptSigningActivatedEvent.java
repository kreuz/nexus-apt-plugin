package com.inventage.nexusaptplugin.capabilities;

/**
 * Event representing the activation of apt signing.
 *
 * @author Dominik Menzi
 */
public class AptSigningActivatedEvent extends AptSigningUpdatedEvent {

    public AptSigningActivatedEvent(AptCapability component, AptCapabilityConfiguration aptCapabilityConfiguration) {
        super(component, aptCapabilityConfiguration);
    }
}
