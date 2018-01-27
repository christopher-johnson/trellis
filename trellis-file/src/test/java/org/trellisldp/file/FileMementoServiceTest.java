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
package org.trellisldp.file;

import static java.time.Instant.MAX;
import static java.time.Instant.now;
import static java.time.Instant.parse;
import static org.apache.commons.io.FileUtils.deleteDirectory;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.trellisldp.api.RDFUtils.TRELLIS_DATA_PREFIX;

import java.io.File;
import java.io.IOException;
import java.time.Instant;

import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.RDF;
import org.apache.commons.rdf.jena.JenaRDF;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.trellisldp.api.MementoService;
import org.trellisldp.api.Resource;

/**
 * Test a file-based memento service.
 */
@RunWith(JUnitPlatform.class)
public class FileMementoServiceTest {

    private static final RDF rdf = new JenaRDF();

    @AfterAll
    public static void cleanUp() throws IOException {
        final File dir = new File(FileMementoServiceTest.class.getResource("/versions").getFile()).getParentFile();
        final File vDir = new File(dir, "versions2");
        if (vDir.exists()) {
            deleteDirectory(vDir);
        }

        final File readonly = new File(FileMementoServiceTest.class.getResource(
                    "/readonly/35/97/1a/f68d4d5afced3770fc13fb8e560dc253/").getFile());
        readonly.setWritable(true);

        final File unreadable = new File(FileMementoServiceTest.class.getResource(
                    "/unreadable/35/97/1a/f68d4d5afced3770fc13fb8e560dc253/").getFile());
        unreadable.setReadable(true);
    }

    @Test
    public void testList() {
        final Instant time = parse("2017-02-16T11:15:01Z");
        final Instant future = parse("2283-04-25T16:35:01Z");
        final IRI identifier = rdf.createIRI(TRELLIS_DATA_PREFIX + "resource");
        final File dir = new File(getClass().getResource("/versions").getFile());
        assertTrue(dir.exists());
        assertTrue(dir.isDirectory());
        final MementoService svc = new FileMementoService(dir.getAbsolutePath());

        assertEquals(2L, svc.list(identifier).size());
        assertTrue(svc.get(identifier, now()).isPresent());
        svc.get(identifier, now()).ifPresent(res -> assertEquals(time, res.getModified()));
        assertTrue(svc.get(identifier, time).isPresent());
        svc.get(identifier, time).ifPresent(res -> assertEquals(time, res.getModified()));
        assertFalse(svc.get(identifier, parse("2015-02-16T10:00:00Z")).isPresent());
        assertTrue(svc.get(identifier, future).isPresent());
        svc.get(identifier, future).ifPresent(res -> assertEquals(future, res.getModified()));
        assertTrue(svc.get(identifier, MAX).isPresent());
        svc.get(identifier, MAX).ifPresent(res -> assertEquals(future, res.getModified()));
    }

    @Test
    public void testListNonExistent() {
        final IRI identifier = rdf.createIRI(TRELLIS_DATA_PREFIX + "nonexistent");
        final File dir = new File(getClass().getResource("/versions").getFile());
        assertTrue(dir.exists());
        assertTrue(dir.isDirectory());
        final MementoService svc = new FileMementoService(dir.getAbsolutePath());

        assertTrue(svc.list(identifier).isEmpty());
        assertFalse(svc.get(identifier, now()).isPresent());
    }

    @Test
    public void testNewVersionSystem() {
        final IRI identifier = rdf.createIRI(TRELLIS_DATA_PREFIX + "resource");
        final File dir = new File(getClass().getResource("/versions").getFile()).getParentFile();
        assertTrue(dir.exists());
        assertTrue(dir.isDirectory());
        final File versionDir = new File(dir, "versions2");
        assertFalse(versionDir.exists());
        final MementoService svc = new FileMementoService(versionDir.getAbsolutePath());

        assertTrue(svc.list(identifier).isEmpty());
        final File file = new File(getClass().getResource("/resource.nq").getFile());
        assertTrue(file.exists());
        final Resource res = new FileResource(identifier, file);
        final Instant time = now();
        svc.put(identifier, time, res.stream());

        assertEquals(1L, svc.list(identifier).size());
        svc.put(identifier, time.plusSeconds(10), res.stream());
        assertEquals(2L, svc.list(identifier).size());
        assertFalse(svc.delete(identifier, time.plusSeconds(15)));
        assertTrue(svc.delete(identifier, time.plusSeconds(10)));
        assertEquals(1L, svc.list(identifier).size());

    }

    @Test
    public void testUnwritableVersionSystem() {
        final IRI identifier = rdf.createIRI(TRELLIS_DATA_PREFIX + "resource");
        final File dir = new File(getClass().getResource("/readonly").getFile());

        final File readonly = new File(getClass().getResource(
                    "/readonly/35/97/1a/f68d4d5afced3770fc13fb8e560dc253/").getFile());
        assumeTrue(readonly.setReadOnly());

        final MementoService svc = new FileMementoService(dir.getAbsolutePath());
        assertEquals(2L, svc.list(identifier).size());
        final File file = new File(getClass().getResource("/resource.nq").getFile());
        assertTrue(file.exists());
        final Resource res = new FileResource(identifier, file);

        final Instant time = parse("2017-02-16T11:15:01Z");
        svc.put(identifier, time.plusSeconds(10), res.stream());

        assertEquals(2L, svc.list(identifier).size());
        assertFalse(svc.delete(identifier, time));
        assertFalse(svc.delete(identifier, time.plusSeconds(10)));
        assertEquals(2L, svc.list(identifier).size());
    }

    @Test
    public void testAccessUnreadable() {
        final IRI identifier = rdf.createIRI(TRELLIS_DATA_PREFIX + "resource");

        final File dir = new File(getClass().getResource("/unreadable").getFile());
        final File unreadable = new File(getClass().getResource(
                    "/unreadable/35/97/1a/f68d4d5afced3770fc13fb8e560dc253/").getFile());
        assumeTrue(unreadable.setReadable(false));

        final MementoService svc = new FileMementoService(dir.getAbsolutePath());

        assertTrue(svc.list(identifier).isEmpty());
    }

}