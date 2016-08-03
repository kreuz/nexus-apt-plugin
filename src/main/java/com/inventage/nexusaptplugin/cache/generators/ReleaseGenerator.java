package com.inventage.nexusaptplugin.cache.generators;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.google.common.eventbus.Subscribe;

import com.inventage.nexusaptplugin.cache.DebianFileManager;
import com.inventage.nexusaptplugin.cache.FileGenerator;
import com.inventage.nexusaptplugin.cache.RepositoryData;
import com.inventage.nexusaptplugin.capabilities.release.AptReleaseCapabilityConfiguration;
import com.inventage.nexusaptplugin.capabilities.release.AptReleaseCapabilityEvent;

public class ReleaseGenerator
        implements FileGenerator {
    /**
     * Enum for the hash algorithms to include included
     */
    private static enum Algorithm {
        MD5("MD5Sum", "MD5"),
        SHA1("SHA1", "SHA1"),
        SHA256("SHA256", "SHA256"),
        SHA384("SHA384", "SHA384"),
        SHA512("SHA512", "SHA512");

        final String heading;

        final String name;

        private Algorithm(String heading, String name) {
            this.heading = heading;
            this.name = name;
        }
    }

    private final String[] FILES = new String[]{"Packages", "Packages.gz"};

    private final DebianFileManager fileManager;
    private final AptReleaseConfiguration aptReleaseConfiguration;

    @Inject
    public ReleaseGenerator(DebianFileManager fileManager, AptReleaseConfiguration aptReleaseConfiguration) {
        this.fileManager = fileManager;
        this.aptReleaseConfiguration = aptReleaseConfiguration;
    }

    @Subscribe
    public void onCapabilityUpdated(AptReleaseCapabilityEvent event) {
        final AptReleaseCapabilityConfiguration aptReleaseCapabilityConfiguration =
                event.getAptReleaseCapabilityConfiguration();
    }

    @Override
    public byte[] generateFile(RepositoryData data)
            throws Exception {
        // Gather files
        List<File> files = new LinkedList<File>();
        int maxSizeLength = 0;
        for (String name : FILES) {
            byte[] contents = fileManager.getFile(name, data);
            File file = new File();
            file.name = name;
            file.contents = contents;
            file.size = String.valueOf(contents.length);
            files.add(file);

            maxSizeLength = Math.max(maxSizeLength, file.size.length());
        }

        // Create Releases
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        OutputStreamWriter w = new OutputStreamWriter(baos);

        // write date to fix apt-get update on version 1.1.10 or newer
        DateTime now = DateTime.now();

        writeHeader(w, "Date", formatDate(now));
        writeHeader(w, "Components", "main");
        writeOptionalHeader(w, "Description", aptReleaseConfiguration.getDescription());
        writeOptionalHeader(w, "Origin", aptReleaseConfiguration.getOrigin());
        writeOptionalHeader(w, "Label", aptReleaseConfiguration.getLabel());
        writeOptionalHeader(w, "Suite", aptReleaseConfiguration.getSuite());
        writeOptionalHeader(w, "Codename", aptReleaseConfiguration.getCodename());
        final DateTime validUntil = aptReleaseConfiguration.getValidUntil(now);
        if (validUntil != null) {
            writeHeader(w, "Valid-Until", formatDate(validUntil));
        }

        String hash_fmt = " %s %" + maxSizeLength + "s %s\n";
        for (Algorithm algorithm : Algorithm.values()) {
            try {
                MessageDigest md = MessageDigest.getInstance(algorithm.name);
                w.write(algorithm.heading + ":\n");

                for (File file : files) {
                    md.reset();
                    md.update(file.contents);

                    w.write(String.format(hash_fmt, Hex.encodeHexString(md.digest()), file.size, file.name));
                }
            }
            catch (NoSuchAlgorithmException e) {
                // guess there's not much we can do...
                throw new RuntimeException(e);
            }
        }
        w.close();

        return baos.toByteArray();
    }

    private void writeOptionalHeader(OutputStreamWriter w, String key, String value) throws IOException {
        if (StringUtils.isNotBlank(value)) {
            writeHeader(w, key, value);
        }
    }

    private void writeHeader(OutputStreamWriter w, String key, String value) throws IOException {
        String hdrfmt = "%s: %s";
        w.write(String.format(hdrfmt, key, value));
        w.write('\n');
    }

    private String formatDate(DateTime dateNew) {
        // RFC 2822 format
        final DateTimeFormatter dateTimeFormatter =
                DateTimeFormat
                        .forPattern("EEE, d MMM yyyy HH:mm:ss Z")
                        .withLocale(Locale.ENGLISH);
        return dateTimeFormatter.print(dateNew);
    }

    private static final class File {
        private String name;

        private byte[] contents;

        private String size;
    }

}
