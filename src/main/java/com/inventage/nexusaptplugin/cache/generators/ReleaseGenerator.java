package com.inventage.nexusaptplugin.cache.generators;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.codec.binary.Hex;

import com.inventage.nexusaptplugin.cache.DebianFileManager;
import com.inventage.nexusaptplugin.cache.FileGenerator;
import com.inventage.nexusaptplugin.cache.RepositoryData;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import java.util.Calendar;
import java.util.GregorianCalendar;

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

    @Inject
    public ReleaseGenerator(DebianFileManager fileManager) {
        this.fileManager = fileManager;
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
        Date now = new Date();

        // Compute Valid-Until date
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(now);
        calendar.add(Calendar.DATE, 7);
        Date nextWeek = new Date( calendar.getTime().getTime() );

        String hdrfmt = new String("%s: %s\n");

        w.write(String.format(hdrfmt, "Date", formatDate(now)) +
                String.format(hdrfmt, "Valid-Until", formatDate(nextWeek)) +
                String.format(hdrfmt, "Origin", "Nexus") +
                String.format(hdrfmt, "Label", "Nexus") +
                String.format(hdrfmt, "Suite", "testing") +
                String.format(hdrfmt, "Codename", "stretch") +
                String.format(hdrfmt, "Components", "main") +
                String.format(hdrfmt, "Description", "Debian x.y Testing distribution - Not Released") +
                String.format(hdrfmt, "Changelogs", "http://metadata.ftp-master.debian.org/changelogs/@CHANGEPATH@_changelog")
                );

        String hash_fmt = new String(" %s %" + maxSizeLength + "s %s\n");

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

    private String formatDate(Date date) {
        // RFC 2822 format
        final DateFormat format = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
        return format.format(date);
    }

    private static final class File {
        private String name;

        private byte[] contents;

        private String size;
    }

}
