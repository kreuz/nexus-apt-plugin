package com.inventage.nexusaptplugin.capabilities.signing;

/**
 * Event representing the deactivation of apt signing.
 *
 * @author Dominik Menzi
 */
public class AptSigningCapabilityDeactivatedEvent extends AptSigningCapabilityEvent {
    public AptSigningCapabilityDeactivatedEvent(AptSigningCapability component) {
        super(component);
    }
}
