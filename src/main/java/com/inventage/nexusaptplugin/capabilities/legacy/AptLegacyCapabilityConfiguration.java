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
package com.inventage.nexusaptplugin.capabilities.legacy;

import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.NotNull;

/**
 * Configuration adapter for {@link AptLegacyCapability}.
 *
 * @since 3.0
 */
public class AptLegacyCapabilityConfiguration {

    public static final String KEYRING = "keyring";
    public static final String KEY = "key";
    public static final String PASSPHRASE = "passphrase";

    private final String keyring;
    private final String key;
    private final String passphrase;

    public AptLegacyCapabilityConfiguration() {
        this(new HashMap<String, String>());
    }

    public AptLegacyCapabilityConfiguration(final Map<String, String> properties) {
        this.keyring = getOrDefault(properties, KEYRING, "");
        this.key = getOrDefault(properties, KEY, "");
        this.passphrase = getOrDefault(properties, PASSPHRASE, "");
    }

    @NotNull
    private String getOrDefault(Map<String, String> properties, String key, String defaultValue) {
        String value = properties.get(key);
        return value == null ? defaultValue : value;
    }

    public String getKeyring() {
        return keyring;
    }

    public String getKey() {
        return key;
    }

    public String getPassphrase() {
        return passphrase;
    }

    public Map<String, String> asMap() {
        Map<String, String> map = new HashMap<String, String>();
        map.put(KEYRING, keyring);
        map.put(KEY, key);
        map.put(PASSPHRASE, passphrase);

        return map;
    }

}
