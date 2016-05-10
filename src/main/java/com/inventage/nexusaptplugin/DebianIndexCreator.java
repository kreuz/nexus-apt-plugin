package com.inventage.nexusaptplugin;

import java.io.File;

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
import java.util.Map;

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
        File artifact = ac.getArtifact();
        if (artifact == null){
          // TODO: Throw an exception?
          return;
        }
        ArtifactInfo info = ac.getArtifactInfo();
        if( ! "deb".equals(info.packaging) ) {
          // TODO: Throw an exception?
          return;
        }

        Map<String,String> artifact_attr = info.getAttributes();

        List<String> control = GetControl.doGet(artifact);

        Map<String,String> debControlMap = DebControlParser.parse(control);

        artifact_attr.putAll(debControlMap);
        artifact_attr.put("Filename", getRelativeFileNameOfArtifact(ac));

        MessageDigest md5d   = DigestUtils.getMd5Digest();
        MessageDigest sha256 = DigestUtils.getSha256Digest();
        MessageDigest sha512 = DigestUtils.getSha512Digest();

        int count;
        byte[] b = new byte[512];
        FileInputStream is = new FileInputStream(artifact);
        try{
          while( (count = is.read(b)) >= 0) {
            md5d.update(b, 0, count);
            sha256.update(b, 0, count);
            sha512.update(b, 0, count);
          }
        }catch(Exception e){
          // TODO: re-throw the exception?
          is.close();
        }finally{
          is.close();
        }

        info.md5 = Hex.encodeHexString(md5d.digest());
        artifact_attr.put(DEBIAN.SHA256.getFieldName(),
                          Hex.encodeHexString(sha256.digest()));
        artifact_attr.put(DEBIAN.SHA512.getFieldName(),
                          Hex.encodeHexString(sha512.digest()));

    }

    private String getRelativeFileNameOfArtifact(ArtifactContext ac) {
      /* TODO: this should be something like the following:
$ apt-cache show selinux-utils | grep 'File'
Filename: pool/main/libs/libselinux/selinux-utils_2.5-2_amd64.deb
       */
    	ArtifactInfo info = ac.getArtifactInfo();
        return "./" +
          info.groupId.replace(".", "/") + "/" +
          info.artifactId + "/" +
          info.version + "/" +
          info.fname;

    }

    /**
     * Updates a document with information from artifact
     *
     * @param  ai  a map of artifact info
     * @param  doc the document to update
     * @return     nothing it seems
     */
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

    /**

     * Updates field **indexerField** of document **doc** for artifact
     * info **ai**

     *
     * @param  ai           a map of artifact info
     * @param  doc          the document to update
     * @param  indexerField the field to update
     * @return     nothing again it seems
     */
    private void updateOneDocumentField(ArtifactInfo ai,
                                        Document doc,
                                        IndexerField indexerField) {
        String fieldName = indexerField.getOntology().getFieldName();
        String fieldVal  = ai.getAttributes().get(fieldName);
        if ( fieldVal == null ){
          // TODO: throw exception?
          return;
        }
        Field field = indexerField.toField(fieldVal);
        doc.add(field);
    }

    @Override
    public boolean updateArtifactInfo(Document doc, ArtifactInfo ai) {
        String filename = doc.get(FILENAME.getKey());
        if ( filename == null ){
          // TODO: throw an exception?
          return false;
        }
        if ( ! filename.endsWith(".deb")) {
          // TODO: throw an exception?
          return false;
        }

        for (IndexerField indexerField : indexerFields) {
          try { 
            updateOneArtifactInfoAttribute(doc, ai, indexerField);
          }catch(Exception e){
            // TODO: rethrow?
            return false;
          }
        }
        ai.md5 = doc.get(MD5.getKey());
        // TODO: sha1? sha256?  sha384?  sha512?
        return true;
    }

    private void updateOneArtifactInfoAttribute(Document doc,
                                                ArtifactInfo ai,
                                                IndexerField indexerField) {

        String fieldName = indexerField.getOntology().getFieldName();
        String docKey = indexerField.getKey();

        ai.getAttributes().put(fieldName, doc.get(docKey));
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
