/*
 * Sonatype Nexus (TM) Open Source Version
 * Copyright (c) 2007-2013 Sonatype, Inc.
 * All rights reserved. Includes the third-party code listed at http://links.sonatype.com/products/nexus/oss/attributions.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse Public License Version 1.0,
 * which accompanies this distribution and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Sonatype Nexus (TM) Professional Version is available from Sonatype, Inc. "Sonatype" and "Sonatype Nexus" are trademarks
 * of Sonatype, Inc. Apache Maven is a trademark of the Apache Software Foundation. M2eclipse is a trademark of the
 * Eclipse Foundation. All other trademarks are the property of their respective owners.
 */
package com.inventage.nexusaptplugin.capabilities.release;

import static com.inventage.nexusaptplugin.capabilities.release.AptReleaseCapabilityDescriptor.VALID_UNTIL_DURATION;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jetbrains.annotations.NotNull;
import org.joda.time.Days;
import org.joda.time.Duration;
import org.joda.time.Hours;
import org.joda.time.Minutes;
import org.joda.time.Period;
import org.joda.time.Seconds;

import com.inventage.nexusaptplugin.capabilities.signing.AptSigningCapability;

/**
 * Configuration adapter for {@link AptSigningCapability}.
 *
 * @since 3.0
 */
public class AptReleaseCapabilityConfiguration {

    private final Map<String, String> properties;

    AptReleaseCapabilityConfiguration(final Map<String, String> properties) {
        this.properties = properties;
    }

    public String getDescription() {
        return getOrDefault(properties, AptReleaseCapabilityDescriptor.DESCRIPTION, null);
    }

    public String getOrigin() {
        return getOrDefault(properties, AptReleaseCapabilityDescriptor.ORIGIN, null);
    }

    public String getLabel() {
        return getOrDefault(properties, AptReleaseCapabilityDescriptor.LABEL, null);
    }

    public String getSuite() {
        return getOrDefault(properties, AptReleaseCapabilityDescriptor.SUITE, null);
    }

    public String getCodename() {
        return getOrDefault(properties, AptReleaseCapabilityDescriptor.CODENAME, null);
    }

    public Duration getValidUntilDuration() {
        final String validUntil = properties.get(VALID_UNTIL_DURATION);
        final Pattern pattern = Pattern.compile("(?:(\\d+)d)?\\s*(?:(\\d+)h)?\\s*(?:(\\d+)m)?\\s*(?:(\\d+)s)?");
        final Matcher matcher = pattern.matcher(validUntil);
        if (matcher.matches()) {
            Period period = Period.ZERO;
            if (matcher.group(1) != null) {
                final int days = Integer.parseInt(matcher.group(1));
                period = period.plus(Days.days(days));
            }
            if (matcher.group(2) != null) {
                final int hours = Integer.parseInt(matcher.group(2));
                period = period.plus(Hours.hours(hours));
            }
            if (matcher.group(3) != null) {
                final int minutes = Integer.parseInt(matcher.group(3));
                period = period.plus(Minutes.minutes(minutes));
            }
            if (matcher.group(4) != null) {
                final int seconds = Integer.parseInt(matcher.group(4));
                period = period.plus(Seconds.seconds(seconds));
            }
            return period.toStandardDuration();
        } else {
            return null;
        }
    }

    @NotNull
    private String getOrDefault(Map<String, String> properties, String key, String defaultValue) {
        String value = properties.get(key);
        return value == null ? defaultValue : value;
    }
}
