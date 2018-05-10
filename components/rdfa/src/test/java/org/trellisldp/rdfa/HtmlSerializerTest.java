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
package org.trellisldp.rdfa;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Optional.empty;
import static java.util.stream.Stream.of;
import static org.apache.jena.core.graph.NodeFactory.createBlankNode;
import static org.apache.jena.core.graph.NodeFactory.createLiteral;
import static org.apache.jena.core.graph.NodeFactory.createURI;
import static org.apache.jena.core.graph.Triple.create;
import static org.apache.jena.core.vocabulary.DCTerms.spatial;
import static org.apache.jena.core.vocabulary.DCTerms.subject;
import static org.apache.jena.core.vocabulary.DCTerms.title;
import static org.apache.jena.core.vocabulary.DCTypes.Text;
import static org.apache.jena.core.vocabulary.RDF.Nodes.type;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.apache.commons.rdf.api.Triple;
import org.apache.commons.rdf.jena.JenaRDF;
import org.apache.jena.core.graph.Node;
import org.apache.jena.core.vocabulary.DCTerms;
import org.apache.jena.core.vocabulary.RDF;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.trellisldp.api.NamespaceService;
import org.trellisldp.api.RDFaWriterService;

/**
 * @author acoburn
 */
public class HtmlSerializerTest {

    private static final JenaRDF rdf = new JenaRDF();

    @Mock
    private NamespaceService mockNamespaceService;

    @Mock
    private OutputStream mockOutputStream;

    private RDFaWriterService service;

    @BeforeEach
    @SuppressWarnings("unchecked")
    public void setUp() {
        initMocks(this);
        final Map<String, String> namespaces = new HashMap<>();
        namespaces.put("dcterms", DCTerms.NS);
        namespaces.put("rdf", RDF.uri);

        service = new HtmlSerializer(mockNamespaceService);

        when(mockNamespaceService.getNamespaces()).thenReturn(namespaces);
        when(mockNamespaceService.getPrefix(eq("http://purl.org/dc/terms/"))).thenReturn(Optional.of("dc"));
        when(mockNamespaceService.getPrefix(eq("http://sws.geonames.org/4929022/"))).thenReturn(empty());
        when(mockNamespaceService.getPrefix(eq("http://www.w3.org/1999/02/22-rdf-syntax-ns#")))
            .thenReturn(Optional.of("rdf"));
        when(mockNamespaceService.getPrefix(eq("http://purl.org/dc/dcmitype/")))
            .thenReturn(Optional.of("dcmitype"));
    }

    @Test
    public void testWriteError() throws IOException {
        doThrow(new IOException()).when(mockOutputStream).write(any(byte[].class), anyInt(), anyInt());
        assertThrows(UncheckedIOException.class, () -> service.write(getTriples(), mockOutputStream, ""));
    }

    @Test
    public void testHtmlSerializer() {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        service.write(getTriples(), out, null);
        final String html = new String(out.toByteArray(), UTF_8);
        assertTrue(html.contains("<title>A title</title>"));
        assertTrue(html.contains("_:B"));
        assertTrue(html.contains("<a href=\"http://sws.geonames.org/4929022/\">http://sws.geonames.org/4929022/</a>"));
        assertTrue(html.contains("<a href=\"http://purl.org/dc/terms/title\">dc:title</a>"));
        assertTrue(html.contains("<a href=\"http://purl.org/dc/terms/spatial\">dc:spatial</a>"));
        assertTrue(html.contains("<a href=\"http://purl.org/dc/dcmitype/Text\">dcmitype:Text</a>"));
        assertTrue(html.contains("<h1>A title</h1>"));
    }

    @Test
    public void testHtmlSerializer2() {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        service.write(getTriples(), out, "http://example.org/");
        final String html = new String(out.toByteArray(), UTF_8);
        assertTrue(html.contains("<title>A title</title>"));
        assertTrue(html.contains("_:B"));
        assertTrue(html.contains("<a href=\"http://sws.geonames.org/4929022/\">http://sws.geonames.org/4929022/</a>"));
        assertTrue(html.contains("<a href=\"http://purl.org/dc/terms/title\">dc:title</a>"));
        assertTrue(html.contains("<a href=\"http://purl.org/dc/terms/spatial\">dc:spatial</a>"));
        assertTrue(html.contains("<a href=\"http://purl.org/dc/dcmitype/Text\">dcmitype:Text</a>"));
        assertTrue(html.contains("<h1>A title</h1>"));
    }

    @Test
    public void testHtmlSerializer3() {
        final RDFaWriterService service4 = new HtmlSerializer(mockNamespaceService, "/resource-test.mustache",
                "//www.trellisldp.org/assets/css/trellis.css", "", "//www.trellisldp.org/assets/img/trellis.png");

        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        service4.write(getTriples(), out, "http://example.org/");
        final String html = new String(out.toByteArray(), UTF_8);
        assertTrue(html.contains("<title>A title</title>"));
        assertTrue(html.contains("_:B"));
        assertTrue(html.contains("<a href=\"http://sws.geonames.org/4929022/\">http://sws.geonames.org/4929022/</a>"));
        assertTrue(html.contains("<a href=\"http://purl.org/dc/terms/title\">dc:title</a>"));
        assertTrue(html.contains("<a href=\"http://purl.org/dc/terms/spatial\">dc:spatial</a>"));
        assertTrue(html.contains("<a href=\"http://purl.org/dc/dcmitype/Text\">dcmitype:Text</a>"));
        assertTrue(html.contains("<h1>A title</h1>"));
    }

    @Test
    public void testHtmlSerializer4() throws Exception {
        final String path = getClass().getResource("/resource-test.mustache").toURI().getPath();

        final RDFaWriterService service4 = new HtmlSerializer(mockNamespaceService, path,
                "//www.trellisldp.org/assets/css/trellis.css", "", null);

        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        service4.write(getTriples(), out, "http://example.org/");
        final String html = new String(out.toByteArray(), UTF_8);
        assertTrue(html.contains("<title>A title</title>"));
        assertTrue(html.contains("_:B"));
        assertTrue(html.contains("<a href=\"http://sws.geonames.org/4929022/\">http://sws.geonames.org/4929022/</a>"));
        assertTrue(html.contains("<a href=\"http://purl.org/dc/terms/title\">dc:title</a>"));
        assertTrue(html.contains("<a href=\"http://purl.org/dc/terms/spatial\">dc:spatial</a>"));
        assertTrue(html.contains("<a href=\"http://purl.org/dc/dcmitype/Text\">dcmitype:Text</a>"));
        assertTrue(html.contains("<h1>A title</h1>"));
    }

    @Test
    public void testHtmlSerializer5() throws Exception {
        final String path = getClass().getResource("/resource-test.mustache").toURI().getPath();

        final RDFaWriterService service4 = new HtmlSerializer(null, path,
                "//www.trellisldp.org/assets/css/trellis.css", "", null);

        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        service4.write(getTriples2(), out, "http://example.org/");
        final String html = new String(out.toByteArray(), UTF_8);
        assertTrue(html.contains("<title>http://example.org/</title>"));
        assertTrue(html.contains("_:B"));
        assertTrue(html.contains("<a href=\"http://sws.geonames.org/4929022/\">http://sws.geonames.org/4929022/</a>"));
        assertTrue(html.contains("<a href=\"http://purl.org/dc/terms/spatial\">http://purl.org/dc/terms/spatial</a>"));
        assertTrue(html.contains("<a href=\"http://purl.org/dc/dcmitype/Text\">http://purl.org/dc/dcmitype/Text</a>"));
        assertTrue(html.contains("<a href=\"mailto:user@host.com\">mailto:user@host.com</a>"));
        assertTrue(html.contains("<h1>http://example.org/</h1>"));
    }

    private static Stream<Triple> getTriples() {
        final Node sub = createURI("trellis:repository/resource");
        final Node bn = createBlankNode();
        return of(
                create(sub, title.asNode(), createLiteral("A title")),
                create(sub, subject.asNode(), bn),
                create(bn, title.asNode(), createLiteral("Other title")),
                create(sub, spatial.asNode(), createURI("http://sws.geonames.org/4929022/")),
                create(sub, spatial.asNode(), createURI("http://sws.geonames.org/4929023/")),
                create(sub, spatial.asNode(), createURI("http://sws.geonames.org/4929024/")),
                create(sub, type, Text.asNode()))
            .map(rdf::asTriple);

    }

    private static Stream<Triple> getTriples2() {
        final Node sub = createURI("trellis:repository/resource");
        final Node other = createURI("mailto:user@host.com");
        final Node bn = createBlankNode();
        return of(
                create(sub, subject.asNode(), bn),
                create(sub, spatial.asNode(), createURI("http://sws.geonames.org/4929022/")),
                create(bn, type, Text.asNode()),
                create(bn, subject.asNode(), other),
                create(sub, type, Text.asNode()))
            .map(rdf::asTriple);

    }
}
