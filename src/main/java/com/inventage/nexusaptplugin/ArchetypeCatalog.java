package com.inventage.nexusaptplugin;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

/**
 * @author Dominik Menzi
 */
enum ArchetypeCatalog {

    DEFAULT(new ImmutableMap.Builder<String, String>()
            .put("/Packages", PackagesContentGenerator.ID)
            .put("/Packages.gz", PackagesGzContentGenerator.ID)
            .put("/Release", ReleaseContentGenerator.ID)
            .build()),

    SIGNING(new ImmutableMap.Builder<String, String>()
            .put("/Release.gpg", ReleaseGPGContentGenerator.ID)
            .put("/apt-key.gpg.key", SignKeyContentGenerator.ID)
            .build());

    private final Map<String, String> catalogFiles;

    ArchetypeCatalog(Map<String, String> catalogFiles) {
        this.catalogFiles = catalogFiles;
    }

    Map<String, String> getCatalogFiles() {
        return catalogFiles;
    }
}
