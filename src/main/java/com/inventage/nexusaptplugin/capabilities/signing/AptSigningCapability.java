package com.inventage.nexusaptplugin.capabilities.signing;

import static org.sonatype.nexus.plugins.capabilities.CapabilityType.capabilityType;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.sonatype.nexus.capability.support.CapabilitySupport;
import org.sonatype.nexus.plugins.capabilities.CapabilityType;
import org.sonatype.sisu.goodies.eventbus.EventBus;

@Named(AptSigningCapability.TYPE_ID)
public class AptSigningCapability extends CapabilitySupport<AptSigningCapabilityConfiguration> {

    public static final String TYPE_ID = "apt_signing";
    public static final CapabilityType TYPE = capabilityType(TYPE_ID);

    private final EventBus eventBus;

    @Inject
    public AptSigningCapability(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    protected AptSigningCapabilityConfiguration createConfig(Map<String, String> properties) throws Exception {
        return new AptSigningCapabilityConfiguration(properties);
    }

    @Override
    protected void onUpdate(AptSigningCapabilityConfiguration config) throws Exception {
        eventBus.post(new AptSigningCapabilityUpdatedEvent(this, config));
    }

    @Override
    public void onActivate(AptSigningCapabilityConfiguration config) {
        eventBus.post(new AptSigningCapabilityActivatedEvent(this, config));
    }

    @Override
    public void onPassivate(AptSigningCapabilityConfiguration config) throws Exception {
        eventBus.post(new AptSigningCapabilityDeactivatedEvent(this));
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
