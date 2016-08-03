package com.inventage.nexusaptplugin.capabilities.release;

import static org.sonatype.nexus.plugins.capabilities.CapabilityType.capabilityType;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.sonatype.nexus.capability.support.CapabilitySupport;
import org.sonatype.nexus.plugins.capabilities.CapabilityType;
import org.sonatype.sisu.goodies.eventbus.EventBus;

@Named(AptReleaseCapability.TYPE_ID)
public class AptReleaseCapability extends CapabilitySupport<AptReleaseCapabilityConfiguration> {

    public static final String TYPE_ID = "apt_release";
    public static final CapabilityType TYPE = capabilityType(TYPE_ID);

    private final EventBus eventBus;

    @Inject
    public AptReleaseCapability(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    protected AptReleaseCapabilityConfiguration createConfig(Map<String, String> properties) throws Exception {
        return new AptReleaseCapabilityConfiguration(properties);
    }

    @Override
    protected void onUpdate(AptReleaseCapabilityConfiguration config) throws Exception {
        eventBus.post(new AptReleaseCapabilityEvent(this, config));
    }

    @Override
    public void onActivate(AptReleaseCapabilityConfiguration config) {
        eventBus.post(new AptReleaseCapabilityEvent(this, config));
    }

    @Override
    public void onPassivate(AptReleaseCapabilityConfiguration config) throws Exception {
        eventBus.post(new AptReleaseCapabilityEvent(this, config));
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
