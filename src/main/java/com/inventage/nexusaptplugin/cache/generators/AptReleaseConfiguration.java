package com.inventage.nexusaptplugin.cache.generators;

import javax.inject.Named;
import javax.inject.Singleton;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.sonatype.nexus.events.EventSubscriber;

import com.google.common.eventbus.Subscribe;

import com.inventage.nexusaptplugin.capabilities.release.AptReleaseCapabilityConfiguration;
import com.inventage.nexusaptplugin.capabilities.release.AptReleaseCapabilityEvent;
import com.inventage.nexusaptplugin.capabilities.signing.AptSigningCapabilityDeactivatedEvent;

@Named
@Singleton
public class AptReleaseConfiguration implements EventSubscriber {

    private String description;
    private String origin;
    private String label;
    private String suite;
    private String codename;
    private Duration validUntilDuration;

    @Subscribe
    public void onCapabilityUpdated(AptReleaseCapabilityEvent event) {
        final AptReleaseCapabilityConfiguration aptReleaseCapabilityConfiguration =
                event.getAptReleaseCapabilityConfiguration();

        description = aptReleaseCapabilityConfiguration.getDescription();
        origin = aptReleaseCapabilityConfiguration.getOrigin();
        label = aptReleaseCapabilityConfiguration.getLabel();
        suite = aptReleaseCapabilityConfiguration.getSuite();
        codename = aptReleaseCapabilityConfiguration.getCodename();
        validUntilDuration = aptReleaseCapabilityConfiguration.getValidUntilDuration();
    }

    @Subscribe
    public void onCapabilityDeactivated(AptSigningCapabilityDeactivatedEvent event) {
        description = null;
        origin = null;
        label = null;
        suite = null;
        codename = null;
        validUntilDuration = null;
    }

    public String getDescription() {
        return description;
    }

    public String getOrigin() {
        return origin;
    }

    public String getLabel() {
        return label;
    }

    public String getSuite() {
        return suite;
    }

    public String getCodename() {
        return codename;
    }

    public DateTime getValidUntil(DateTime now) {
        if (validUntilDuration != null) {
            return now.plus(validUntilDuration);
        } else {
            return null;
        }
    }
}
