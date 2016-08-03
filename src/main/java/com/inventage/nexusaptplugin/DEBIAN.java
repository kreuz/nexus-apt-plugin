package com.inventage.nexusaptplugin;

import org.apache.maven.index.Field;

/**
 * Index field descriptions of Debian package attributes.
 */
public interface DEBIAN {

    String DEBIAN_NAMESPACE = "urn:nexus:debian#";

    Field PACKAGE = new Field(null, DEBIAN_NAMESPACE, "Package", "Debian Package Name");
    Field VERSION = new Field(null, DEBIAN_NAMESPACE, "Version", "Debian Package Version");
    Field ARCHITECTURE = new Field(null, DEBIAN_NAMESPACE, "Architecture", "Debian Package Architecture");
    Field MAINTAINER = new Field(null, DEBIAN_NAMESPACE, "Maintainer", "Debian Package Maintainer");
    Field INSTALLED_SIZE = new Field(null, DEBIAN_NAMESPACE, "Installed-Size", "Debian Package Installed Size");
    Field DEPENDS = new Field(null, DEBIAN_NAMESPACE, "Depends", "Debian Package Depends");
    Field PRE_DEPENDS = new Field(null, DEBIAN_NAMESPACE, "Pre-Depends", "Debian Package Pre-Depends");
    Field PROVIDES = new Field(null, DEBIAN_NAMESPACE, "Provides", "Debian Package Provides");
    Field RECOMMENDS = new Field(null, DEBIAN_NAMESPACE, "Recommends", "Debian Package Recommends");
    Field SUGGESTS = new Field(null, DEBIAN_NAMESPACE, "Suggests", "Debian Package Suggests");
    Field ENHANCES = new Field(null, DEBIAN_NAMESPACE, "Enhances", "Debian Package Enhances");
    Field BREAKS = new Field(null, DEBIAN_NAMESPACE, "Breaks", "Debian Package Breaks");
    Field CONFLICTS = new Field(null, DEBIAN_NAMESPACE, "Conflicts", "Debian Package Conflicts");
    Field REPLACES = new Field(null, DEBIAN_NAMESPACE, "Replaces", "Debian Package Replaces");
    Field SECTION = new Field(null, DEBIAN_NAMESPACE, "Section", "Debian Package Section");
    Field PRIORITY = new Field(null, DEBIAN_NAMESPACE, "Priority", "Debian Package Priority");
    Field DESCRIPTION = new Field(null, DEBIAN_NAMESPACE, "Description", "Debian Package Description");

    Field MD5 = new Field( null, DEBIAN_NAMESPACE, "MD5sum", "MD5 checksum" );
    Field FILENAME = new Field( null, DEBIAN_NAMESPACE, "Filename", "Filename" );
    Field SHA256 = new Field( null, DEBIAN_NAMESPACE, "sha256", "SHA256 checksum" );
    Field SHA512 = new Field( null, DEBIAN_NAMESPACE, "sha512", "SHA512 checksum" );
}
