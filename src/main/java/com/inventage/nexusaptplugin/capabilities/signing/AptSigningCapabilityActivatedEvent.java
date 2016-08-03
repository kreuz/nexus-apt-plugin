package com.inventage.nexusaptplugin.capabilities.signing;

/**
 * Event representing the activation of apt signing.
 *
 * @author Dominik Menzi
 */
public class AptSigningCapabilityActivatedEvent extends AptSigningCapabilityUpdatedEvent {

    public AptSigningCapabilityActivatedEvent(AptSigningCapability component, AptSigningCapabilityConfiguration aptSigningCapabilityConfiguration) {
        super(component, aptSigningCapabilityConfiguration);
    }
}
