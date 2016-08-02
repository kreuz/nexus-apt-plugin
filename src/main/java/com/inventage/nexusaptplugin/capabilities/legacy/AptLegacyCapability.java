package com.inventage.nexusaptplugin.capabilities.legacy;

import java.util.Map;

import javax.inject.Named;

import org.sonatype.nexus.capability.support.CapabilitySupport;

@Named(AptLegacyCapabilityDescriptor.TYPE_ID)
public class AptLegacyCapability extends CapabilitySupport<AptLegacyCapabilityConfiguration> {

    @Override
    protected AptLegacyCapabilityConfiguration createConfig(Map<String, String> properties) throws Exception {
        return new AptLegacyCapabilityConfiguration(properties);
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
