package com.inventage.nexusaptplugin.capabilities.signing;

import org.sonatype.nexus.events.AbstractEvent;

/**
 * Superclass for all events originating in the {@link AptSigningCapability}.
 *
 * @author Dominik Menzi
 */
abstract class AptSigningCapabilityEvent extends AbstractEvent<AptSigningCapability> {
    public AptSigningCapabilityEvent(AptSigningCapability component) {
        super(component);
    }
}
