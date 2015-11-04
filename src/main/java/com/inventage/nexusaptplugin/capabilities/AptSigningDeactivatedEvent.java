package com.inventage.nexusaptplugin.capabilities;

/**
 * Event representing the deactivation of apt signing.
 *
 * @author Dominik Menzi
 */
public class AptSigningDeactivatedEvent extends AptCapabilityEvent {
    public AptSigningDeactivatedEvent(AptCapability component) {
        super(component);
    }
}
