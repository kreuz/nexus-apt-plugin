package com.inventage.nexusaptplugin.deb;

import org.apache.commons.compress.archivers.ArchiveException;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: sannies
 * Date: 6/30/12
 * Time: 11:24 AM
 * To change this template use File | Settings | File Templates.
 */
public class GetControlTest {

    @Test
    public void test() throws IOException, ArchiveException {
        File deb = new File(getClass().getProtectionDomain().getCodeSource().getLocation().getFile(), "php5_5.3.10-1ubuntu3.2_all.deb");
        System.err.println( GetControl.doGet(deb));
    }
}
