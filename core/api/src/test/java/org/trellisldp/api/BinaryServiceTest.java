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
package org.trellisldp.api;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Collections.emptyMap;
import static java.util.concurrent.CompletableFuture.completedFuture;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.RDF;
import org.apache.commons.rdf.simple.SimpleRDF;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

/**
 * @author acoburn
 */
public class BinaryServiceTest {

    private static final RDF rdf = new SimpleRDF();

    private final IRI identifier = rdf.createIRI("trellis:data/resource");

    @Mock
    private BinaryService mockBinaryService;

    @Mock
    private InputStream mockInputStream;

    @BeforeEach
    public void setUp() {
        initMocks(this);
        doCallRealMethod().when(mockBinaryService).setContent(any(), any());
    }

    @Test
    public void testDefaultMethods() {
        when(mockBinaryService.getContent(any())).thenReturn(completedFuture(mockInputStream));
        mockBinaryService.setContent(identifier, mockInputStream);
        verify(mockBinaryService).setContent(eq(identifier), eq(mockInputStream),
                eq(emptyMap()));
        assertEquals(mockInputStream, mockBinaryService.getContent(identifier).join(),
                "getContent returns wrong input stream");
    }

    @Test
    public void testGetContent() throws IOException {
        when(mockBinaryService.getContent(eq(identifier), any(), any()))
            .thenReturn(completedFuture(new ByteArrayInputStream("FooBar".getBytes(UTF_8))));
        final InputStream content = mockBinaryService.getContent(identifier, 0, 6).join();
        assertEquals("FooBar", IOUtils.toString(content, UTF_8), "Binary content did not match");
    }
}
