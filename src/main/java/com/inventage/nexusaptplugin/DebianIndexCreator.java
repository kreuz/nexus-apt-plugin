package com.inventage.nexusaptplugin;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0    
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.inject.Named;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.maven.index.ArtifactContext;
import org.apache.maven.index.ArtifactInfo;
import org.apache.maven.index.IndexerField;
import org.apache.maven.index.IndexerFieldVersion;
import org.apache.maven.index.creator.AbstractIndexCreator;
import org.apache.maven.index.creator.MinimalArtifactInfoIndexCreator;

import com.inventage.nexusaptplugin.deb.DebControlParser;
import com.inventage.nexusaptplugin.deb.GetControl;


/**
 * A Maven Archetype index creator used to detect and correct the artifact packaging to "maven-archetype" if the
 * inspected JAR is an Archetype. Since packaging is already handled by Minimal creator, this Creator only alters the
 * supplied ArtifactInfo packaging field during processing, but does not interferes with Lucene document fill-up or the
 * ArtifactInfo fill-up (the update* methods are empty).
 *
 * @author cstamas
 */
@Named(DebianIndexCreator.ID)
@javax.inject.Singleton
public class DebianIndexCreator
        extends AbstractIndexCreator {

    public static final String ID = "debian-package";

    public static final IndexerField PACKAGE =
      new IndexerField(DEBIAN.PACKAGE, IndexerFieldVersion.V1, "deb_package",
            DEBIAN.PACKAGE.getDescription(), Field.Store.YES, Field.Index.NO);

    public static final IndexerField ARCHITECTURE =
      new IndexerField(DEBIAN.ARCHITECTURE,
                       IndexerFieldVersion.V1,
                       "deb_architecture",
                       DEBIAN.ARCHITECTURE.getDescription(),
                       Field.Store.YES,
                       Field.Index.NO);

    public static final IndexerField INSTALLED_SIZE =
      new IndexerField(DEBIAN.INSTALLED_SIZE,
                       IndexerFieldVersion.V1,
                       "deb_installed_size",
                       DEBIAN.INSTALLED_SIZE.getDescription(),
                       Field.Store.YES,
                       Field.Index.NO);

    public static final IndexerField MAINTAINER =
      new IndexerField(DEBIAN.MAINTAINER,
                       IndexerFieldVersion.V1,
                       "deb_maintainer",
                       DEBIAN.MAINTAINER.getDescription(),
                       Field.Store.YES,
                       Field.Index.NO);

    public static final IndexerField VERSION =
      new IndexerField(DEBIAN.VERSION,
                       IndexerFieldVersion.V1,
                       "deb_version",
                       DEBIAN.VERSION.getDescription(),
                       Field.Store.YES,
                       Field.Index.NO);

    public static final IndexerField DEPENDS =
      new IndexerField(DEBIAN.DEPENDS,
                       IndexerFieldVersion.V1,
                       "deb_depends",
                       DEBIAN.DEPENDS.getDescription(),
                       Field.Store.YES,
                       Field.Index.NO);

    public static final IndexerField PRE_DEPENDS =
      new IndexerField(DEBIAN.PRE_DEPENDS,
                       IndexerFieldVersion.V1,
                       "deb_pre_depends",
                       DEBIAN.PRE_DEPENDS.getDescription(),
                       Field.Store.YES,
                       Field.Index.NO);

    public static final IndexerField PROVIDES =
      new IndexerField(DEBIAN.PROVIDES,
                       IndexerFieldVersion.V1,
                       "deb_provides",
                       DEBIAN.PROVIDES.getDescription(),
                       Field.Store.YES,
                       Field.Index.NO);

    public static final IndexerField RECOMMENDS =
      new IndexerField(DEBIAN.RECOMMENDS,
                       IndexerFieldVersion.V1,
                       "deb_recommends",
                       DEBIAN.RECOMMENDS.getDescription(),
                       Field.Store.YES,
                       Field.Index.NO);

    public static final IndexerField SUGGESTS =
      new IndexerField(DEBIAN.SUGGESTS,
                       IndexerFieldVersion.V1,
                       "deb_suggests",
                       DEBIAN.SUGGESTS.getDescription(),
                       Field.Store.YES,
                       Field.Index.NO);

    public static final IndexerField ENHANCES =
      new IndexerField(DEBIAN.ENHANCES,
                       IndexerFieldVersion.V1,
                       "deb_enhances",
                       DEBIAN.ENHANCES.getDescription(),
                       Field.Store.YES,
                       Field.Index.NO);

    public static final IndexerField BREAKS =
      new IndexerField(DEBIAN.BREAKS,
                       IndexerFieldVersion.V1,
                       "deb_breaks",
                       DEBIAN.BREAKS.getDescription(),
                       Field.Store.YES,
                       Field.Index.NO);

    public static final IndexerField CONFLICTS =
      new IndexerField(DEBIAN.CONFLICTS,
                       IndexerFieldVersion.V1,
                       "deb_conflicts",
                       DEBIAN.CONFLICTS.getDescription(),
                       Field.Store.YES,
                       Field.Index.NO);

    public static final IndexerField REPLACES =
      new IndexerField(DEBIAN.REPLACES,
                       IndexerFieldVersion.V1,
                       "deb_replaces",
                       DEBIAN.REPLACES.getDescription(),
                       Field.Store.YES,
                       Field.Index.NO);

    public static final IndexerField SECTION =
      new IndexerField(DEBIAN.SECTION,
                       IndexerFieldVersion.V1,
                       "deb_section",
                       DEBIAN.SECTION.getDescription(),
                       Field.Store.YES,
                       Field.Index.NO);

    public static final IndexerField PRIORITY =
      new IndexerField(DEBIAN.PRIORITY,
                       IndexerFieldVersion.V1,
                       "deb_priority",
                       DEBIAN.PRIORITY.getDescription(),
                       Field.Store.YES,
                       Field.Index.NO);

    public static final IndexerField DESCRIPTION =
      new IndexerField(DEBIAN.DESCRIPTION,
                       IndexerFieldVersion.V1,
                       "deb_description",
                       DEBIAN.DESCRIPTION.getDescription(),
                       Field.Store.YES,
                       Field.Index.NO);

    public static final IndexerField MD5 =
      new IndexerField(DEBIAN.MD5,
                       IndexerFieldVersion.V1,
                       "deb_md5",
                       DEBIAN.MD5.getDescription(),
                       Field.Store.YES,
                       Field.Index.NO);

    public static final IndexerField FILENAME =
      new IndexerField(DEBIAN.FILENAME,
                       IndexerFieldVersion.V1,
                       "deb_filename",
                       DEBIAN.FILENAME.getDescription(),
                       Field.Store.YES,
                       Field.Index.NO);

    public static final IndexerField SHA256 =
      new IndexerField(DEBIAN.SHA256,
                       IndexerFieldVersion.V1,
                       "deb_sha256",
                       DEBIAN.SHA256.getDescription(),
                       Field.Store.YES,
                       Field.Index.NO);

    public static final IndexerField SHA512 =
      new IndexerField(DEBIAN.SHA512,
                       IndexerFieldVersion.V1,
                       "deb_sha512",
                       DEBIAN.SHA512.getDescription(),
                       Field.Store.YES,
                       Field.Index.NO);

    private final List<IndexerField> indexerFields =
      Arrays.asList(PACKAGE, ARCHITECTURE, INSTALLED_SIZE, MAINTAINER, VERSION,
                    DEPENDS, PRE_DEPENDS, PROVIDES, RECOMMENDS, SUGGESTS,
                    ENHANCES, BREAKS, CONFLICTS, REPLACES, SECTION, PRIORITY,
                    DESCRIPTION, FILENAME, SHA256, SHA512);

    public DebianIndexCreator() {
        super(ID, Arrays.asList(MinimalArtifactInfoIndexCreator.ID));
    }

    @Override
    public void populateArtifactInfo(ArtifactContext ac) throws IOException {
        if (ac.getArtifact() != null && "deb".equals(ac.getArtifactInfo().packaging)) {
            List<String> control = GetControl.doGet(ac.getArtifact());
            ac.getArtifactInfo().getAttributes().putAll(DebControlParser.parse(control));
            ac.getArtifactInfo().getAttributes().put("Filename", getRelativeFileNameOfArtifact(ac));

            FileInputStream is = null;
            try {
                is = new FileInputStream(ac.getArtifact());
                MessageDigest md5d = DigestUtils.getMd5Digest();
                MessageDigest sha256 = DigestUtils.getSha256Digest();
                MessageDigest sha512 = DigestUtils.getSha512Digest();

                int count;
                byte[] b = new byte[512];
                while( (count = is.read(b)) >= 0) {
                    md5d.update(b, 0, count);
                    sha256.update(b, 0, count);
                    sha512.update(b, 0, count);
                }

                ac.getArtifactInfo().md5 = Hex.encodeHexString(md5d.digest());
                ac.getArtifactInfo().getAttributes().put(DEBIAN.SHA256.getFieldName(), Hex.encodeHexString(sha256.digest()));
                ac.getArtifactInfo().getAttributes().put(DEBIAN.SHA512.getFieldName(), Hex.encodeHexString(sha512.digest()));
            } finally {
                is.close();
            }
        }
    }

    private String getRelativeFileNameOfArtifact(ArtifactContext ac) {
        return "./" + ac.getArtifactInfo().groupId.replace(".", "/") + "/" + ac.getArtifactInfo().artifactId + "/" + ac.getArtifactInfo().version + "/" + ac.getArtifactInfo().fname;
    }

    @Override
    public void updateDocument(ArtifactInfo ai, Document doc) {
        if ("deb".equals(ai.packaging)) {
            for (IndexerField indexerField : indexerFields) {
                updateOneDocumentField(ai, doc, indexerField);
            }
            if (ai.md5 != null) {
                doc.add(MD5.toField(ai.md5));
            }
        }
    }

    private void updateOneDocumentField(ArtifactInfo ai, Document doc, IndexerField indexerField) {
        if (ai.getAttributes().get(indexerField.getOntology().getFieldName()) != null) {
            doc.add(indexerField.toField(ai.getAttributes().get(indexerField.getOntology().getFieldName())));
        }
    }

    @Override
    public boolean updateArtifactInfo(Document doc, ArtifactInfo ai) {
        String filename = doc.get(FILENAME.getKey());
        if (filename != null && filename.endsWith(".deb")) {
            for (IndexerField indexerField : indexerFields) {
                updateOneArtifactInfoAttribute(doc, ai, indexerField);
            }
            ai.md5 = doc.get(MD5.getKey());
            return true;
        }
        return false;
    }

    private void updateOneArtifactInfoAttribute(Document doc, ArtifactInfo ai, IndexerField indexerField) {
        ai.getAttributes().put(indexerField.getOntology().getFieldName(), doc.get(indexerField.getKey()));
    }

    // ==

    @Override
    public String toString() {
        return ID;
    }

    public Collection<IndexerField> getIndexerFields() {
        return Arrays.asList(SHA256, SHA512);
    }
}
