package com.inventage.nexusaptplugin.deb;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.io.IOUtils;

import com.inventage.nexusaptplugin.ar.ArReader;
import com.inventage.nexusaptplugin.ar.ReadableArFile;

public class GetControl {
    public static List<String> doGet(File deb) throws IOException {
        ArReader arReader = new ArReader(deb);
        for (ReadableArFile readableArFile : arReader) {
            if ("control.tar.gz".equals(readableArFile.getName())) {
                ArchiveInputStream input = null;
                try {
                    input = new ArchiveStreamFactory()
                            .createArchiveInputStream(new BufferedInputStream(new GzipCompressorInputStream(readableArFile.open())));
                }
                catch (ArchiveException e) {
                    throw new IOException(e);
                }
                ArchiveEntry ae;
                while ((ae = input.getNextEntry()) != null) {
                    if (ae.getName().endsWith("control")) {
                    	arReader.close();
                        return IOUtils.readLines(input);
                    }
                }
            }
        }
        arReader.close();
        return null;
    }
}
