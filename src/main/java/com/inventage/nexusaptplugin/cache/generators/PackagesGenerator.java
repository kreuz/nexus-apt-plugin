package com.inventage.nexusaptplugin.cache.generators;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Map;

import org.apache.lucene.search.Query;
import org.apache.maven.index.ArtifactInfo;
import org.apache.maven.index.Field;
import org.apache.maven.index.Indexer;
import org.apache.maven.index.IteratorSearchRequest;
import org.apache.maven.index.IteratorSearchResponse;
import org.apache.maven.index.MAVEN;
import org.apache.maven.index.SearchType;
import org.apache.maven.index.context.IndexingContext;
import org.apache.maven.index.expr.SearchTypedStringSearchExpression;

import com.inventage.nexusaptplugin.DEBIAN;
import com.inventage.nexusaptplugin.cache.FileGenerator;
import com.inventage.nexusaptplugin.cache.RepositoryData;


public class PackagesGenerator
        implements FileGenerator {
    /* Taken from http://www.debian.org/doc/manuals/debian-reference/ch02.en.html#_package_dependencies */
    private static final Field[] PACKAGE_DEPENDENCIES_FIELD = new Field[]{
            DEBIAN.DEPENDS, DEBIAN.PRE_DEPENDS, DEBIAN.PROVIDES, DEBIAN.RECOMMENDS, DEBIAN.SUGGESTS, DEBIAN.ENHANCES,
            DEBIAN.BREAKS, DEBIAN.CONFLICTS, DEBIAN.REPLACES
    };

    public PackagesGenerator() {
    }

    @Override
    public byte[] generateFile(RepositoryData data)
            throws Exception {
        final Indexer indexer = data.getIndexer();
        final IndexingContext indexingContext = data.getIndexingContext();

        final Query pq = indexer.constructQuery(MAVEN.PACKAGING, new SearchTypedStringSearchExpression("deb", SearchType.EXACT));

        final IteratorSearchRequest sreq = new IteratorSearchRequest(pq, indexingContext);
        final IteratorSearchResponse hits = indexer.searchIterator(sreq);

        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final OutputStreamWriter w = new OutputStreamWriter(baos);

        for (ArtifactInfo hit : hits) {
            Map<String, String> attrs = hit.getAttributes();
            if (attrs.get("Package") == null || attrs.get("Version") == null
                    || attrs.get("Filename") == null) {
                // This won't produce a real artifact, ignore it
                continue;
            }

            String sha256 = attrs.get(DEBIAN.SHA256.getFieldName());
            String sha512 = attrs.get(DEBIAN.SHA512.getFieldName());

            // Verify that this is a valid artifact
            w.write("Package: " + attrs.get("Package") + "\n");
            w.write("Version: " + attrs.get("Version") + "\n");
            w.write("Architecture: " + attrs.get("Architecture") + "\n");
            w.write("Maintainer: " + attrs.get("Maintainer") + "\n");
            w.write("Installed-Size: " + attrs.get("Installed-Size") + "\n");
            /* Those are not mandatory */
            for (Field fieldName : PACKAGE_DEPENDENCIES_FIELD) {
                writeIfNonEmpty(w, hit, fieldName);
            }
            w.write("Filename: " + attrs.get("Filename") + "\n");
            w.write("Size: " + hit.size + "\n");
            w.write("MD5sum: " + hit.md5 + "\n");
            w.write("SHA1: " + hit.sha1 + "\n");

            if(sha256 != null) {
                w.write("SHA256: " + sha256 + "\n");
            }
            if(sha512 != null) {
                w.write("SHA512: " + sha512 + "\n");
            }

            w.write("Section: " + attrs.get("Section") + "\n");
            w.write("Priority: " + attrs.get("Priority") + "\n");
            w.write("Description: " + (attrs.get("Description") != null ? (attrs.get("Description").replace("\n", "\n ")) : "<no desc>") + "\n");
            w.write("\n");
        }
        w.close();
        return baos.toByteArray();
    }

    private void writeIfNonEmpty(OutputStreamWriter w, ArtifactInfo hit, Field field)
            throws IOException {
        String fieldValue = hit.getAttributes().get(field.getFieldName());
        if (fieldValue != null && !fieldValue.isEmpty()) {
            w.write(field.getFieldName() + ": " + fieldValue + "\n");
        }
    }

}
