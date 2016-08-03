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

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.sonatype.nexus.capability.support.CapabilityDescriptorSupport;
import org.sonatype.nexus.formfields.FormField;
import org.sonatype.nexus.formfields.StringTextFormField;
import org.sonatype.nexus.plugins.capabilities.CapabilityDescriptor;
import org.sonatype.nexus.plugins.capabilities.CapabilityIdentity;
import org.sonatype.nexus.plugins.capabilities.CapabilityType;
import org.sonatype.nexus.plugins.capabilities.Validator;
import org.sonatype.nexus.plugins.capabilities.support.validator.Validators;

/**
 * @since 3.0
 */
@Singleton
@Named(AptReleaseCapability.TYPE_ID)
public class AptReleaseCapabilityDescriptor
        extends CapabilityDescriptorSupport
        implements CapabilityDescriptor {

    public static final String DESCRIPTION = "description";
    public static final String ORIGIN = "origin";
    public static final String LABEL = "label";
    public static final String SUITE = "suite";
    public static final String CODENAME = "codename";
    public static final String VALID_UNTIL_DURATION = "validUntilDuration";

    private final Validators validators;

    @Inject
    public AptReleaseCapabilityDescriptor(final Validators validators) {
        this.validators = validators;
    }

    @Override
    public CapabilityType type() {
        return AptReleaseCapability.TYPE;
    }

    @Override
    public String name() {
        return "APT: Releases file configuration";
    }

    @Override
    public String about() {
        return "APT plugin configuration.";
    }

    @Override
    public List<FormField> formFields() {
        return Arrays.<FormField>asList(
                new StringTextFormField(
                        DESCRIPTION,
                        "Description",
                        "The value of the \"Description\" field in the Releases file",
                        FormField.OPTIONAL
                ),
                new StringTextFormField(
                        ORIGIN,
                        "Origin",
                        "The value of the \"Origin\" field in the Releases file",
                        FormField.OPTIONAL
                ),
                new StringTextFormField(
                        LABEL,
                        "Label",
                        "The value of the \"Label\" field in the Releases file",
                        FormField.OPTIONAL
                ),
                new StringTextFormField(
                        SUITE,
                        "Suite",
                        "The value of the \"Suite\" field in the Releases file",
                        FormField.OPTIONAL
                ),
                new StringTextFormField(
                        CODENAME,
                        "Codename",
                        "The value of the \"Codename\" field in the Releases file",
                        FormField.OPTIONAL
                ),
                new StringTextFormField(
                        VALID_UNTIL_DURATION,
                        "Valid-Until Duration",
                        "The duration used to calculate the \"Valid-Until\" field in the Releases file",
                        FormField.OPTIONAL
                ));
    }

    @Override
    public Validator validator() {
        return validators.capability().uniquePer(AptReleaseCapability.TYPE);
    }

    @Override
    public Validator validator(final CapabilityIdentity id) {
        return validators.capability().uniquePerExcluding(id, AptReleaseCapability.TYPE);
    }

}
