package com.inventage.nexusaptplugin.capabilities;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.sonatype.nexus.capability.support.CapabilitySupport;
import org.sonatype.sisu.goodies.eventbus.EventBus;

import com.inventage.nexusaptplugin.sign.AptSigningConfiguration;

@Named(AptCapabilityDescriptor.TYPE_ID)
public class AptCapability extends CapabilitySupport<AptCapabilityConfiguration> {

    private final EventBus eventBus;

    @Inject
    public AptCapability(EventBus eventBus, AptSigningConfiguration aptSigningConfiguration) {
        this.eventBus = eventBus;
    }

    @Override
    protected AptCapabilityConfiguration createConfig(Map<String, String> properties) throws Exception {
        return new AptCapabilityConfiguration(properties);
    }

    @Override
    protected void onUpdate(AptCapabilityConfiguration config) throws Exception {
        eventBus.post(new AptSigningUpdatedEvent(this, config));
    }

    @Override
    public void onActivate(AptCapabilityConfiguration config) {
        eventBus.post(new AptSigningActivatedEvent(this, config));
    }

    @Override
    public void onPassivate(AptCapabilityConfiguration config) throws Exception {
        eventBus.post(new AptSigningDeactivatedEvent(this));
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
