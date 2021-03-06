/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.trellisldp.namespaces;

import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigInteger;
import java.net.URL;
import java.security.SecureRandom;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

/**
 * @author acoburn
 */
public class NamespacesJsonContextTest {

    private static final String nsDoc = "/testNamespaces.json";
    private static final String JSONLD = "http://www.w3.org/ns/json-ld#";
    private static final String LDP = "http://www.w3.org/ns/ldp#";

    @AfterAll
    public static void cleanUp() throws IOException {
        final File readonly = new File(NamespacesJsonContext.class.getResource("/readonly.json").getFile());
        readonly.setWritable(true);
    }

    @Test
    public void testReadFromJson() {
        final URL res = NamespacesJsonContext.class.getResource(nsDoc);
        System.getProperties().setProperty(NamespacesJsonContext.NAMESPACES_PATH, res.getPath());
        final NamespacesJsonContext svc = new NamespacesJsonContext();
        assertEquals(2, svc.getNamespaces().size(), "Namespace mapping count is incorrect!");
        assertEquals(of(LDP), svc.getNamespace("ldp"), "LDP namespace is incorrect!");
        assertEquals(of("ldp"), svc.getPrefix(LDP), "LDP prefix is incorrect!");
        System.getProperties().remove(NamespacesJsonContext.NAMESPACES_PATH);
    }

    @Test
    public void testReadError() {
        final URL res = NamespacesJsonContext.class.getResource("/thisIsNot.json");
        System.getProperties().setProperty(NamespacesJsonContext.NAMESPACES_PATH, res.getPath());

        assertThrows(UncheckedIOException.class, NamespacesJsonContext::new, "Loaded namespaces from invalid file!");
        System.getProperties().remove(NamespacesJsonContext.NAMESPACES_PATH);
    }

    @Test
    public void testWriteError() throws Exception {
        final URL res = NamespacesJsonContext.class.getResource("/readonly.json");

        System.getProperties().setProperty(NamespacesJsonContext.NAMESPACES_PATH, res.getPath());

        final NamespacesJsonContext svc = new NamespacesJsonContext();
        final File file = new File(res.toURI());
        assumeTrue(file.setWritable(false), "Files couldn't be set as unwritable, so skipping this test!");
        assertThrows(UncheckedIOException.class, () ->
                svc.setPrefix("ex", "http://example.com/"), "Set prefix on unwritable namespace file!");
        System.getProperties().remove(NamespacesJsonContext.NAMESPACES_PATH);
    }

    @Test
    public void testWriteToJson() {
        final File file = new File(NamespacesJsonContext.class.getResource(nsDoc).getPath());
        final String filename = file.getParent() + "/" + randomFilename();

        System.getProperties().setProperty(NamespacesJsonContext.NAMESPACES_PATH, filename);

        final NamespacesJsonContext svc1 = new NamespacesJsonContext();
        assertEquals(15, svc1.getNamespaces().size(), "Incorrect namespace mapping count!");
        assertFalse(svc1.getNamespace("jsonld").isPresent(), "jsonld namespace unexpectedly found!");
        assertFalse(svc1.getPrefix(JSONLD).isPresent(), "jsonld prefix unexpectedly found!");
        assertTrue(svc1.setPrefix("jsonld", JSONLD), "unable to set jsonld mapping!");
        assertEquals(16, svc1.getNamespaces().size(), "Namespace count was not incremented!");
        assertEquals(of(JSONLD), svc1.getNamespace("jsonld"), "jsonld namespace not found in mapping!");
        assertEquals(of("jsonld"), svc1.getPrefix(JSONLD), "jsonld prefix not found in mapping!");

        final NamespacesJsonContext svc2 = new NamespacesJsonContext();
        assertEquals(16, svc2.getNamespaces().size(), "Incorrect namespace count when reloading from file!");
        assertEquals(of(JSONLD), svc2.getNamespace("jsonld"), "jsonld namespace not found in mapping!");
        assertFalse(svc2.setPrefix("jsonld", JSONLD), "unexpected response when trying to re-set jsonld mapping!");
        System.getProperties().remove(NamespacesJsonContext.NAMESPACES_PATH);
    }

    private static String randomFilename() {
        final SecureRandom random = new SecureRandom();
        final String filename = new BigInteger(50, random).toString(32);
        return filename + ".json";
    }
}
