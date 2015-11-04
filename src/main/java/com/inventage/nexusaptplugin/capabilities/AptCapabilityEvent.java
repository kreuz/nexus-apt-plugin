package com.inventage.nexusaptplugin.capabilities;

import org.sonatype.nexus.events.AbstractEvent;

/**
 * Superclass for all events originating in the {@link AptCapability}.
 *
 * @author Dominik Menzi
 */
abstract class AptCapabilityEvent extends AbstractEvent<AptCapability> {
    public AptCapabilityEvent(AptCapability component) {
        super(component);
    }
}
