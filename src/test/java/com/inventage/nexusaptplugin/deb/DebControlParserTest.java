package com.inventage.nexusaptplugin.deb;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.junit.Test;


public class DebControlParserTest {

    @Test
    public void test() throws IOException {
        List<String> control = IOUtils.readLines(getClass().getResourceAsStream("/example-control"));
        Map<String, String> values = DebControlParser.parse(control);
        assertThat("php5", is(values.get("Package")));
        assertThat("5.3.10-1ubuntu3.2", is(values.get("Version")));
        assertThat("all", is(values.get("Architecture")));
        assertThat("Ubuntu Developers <ubuntu-devel-discuss@lists.ubuntu.com>", is(values.get("Maintainer")));
        assertThat("server-side, HTML-embedded scripting language (metapackage)\n" +
                "This package is a metapackage that, when installed, guarantees that you\n" +
                "have at least one of the four server-side versions of the PHP5 interpreter\n" +
                "installed. Removing this package won't remove PHP5 from your system, however\n" +
                "it may remove other packages that depend on this one.\n" +
                ".\n" +
                "PHP5 is a widely-used general-purpose scripting language that is\n" +
                "especially suited for Web development and can be embedded into HTML.\n" +
                "The goal of the language is to allow web developers to write\n" +
                "dynamically generated pages quickly.", is(values.get("Description")));

    }

}
