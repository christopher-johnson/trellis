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
package org.trellisldp.triplestore;

import static org.apache.jena.core.rdf.model.ResourceFactory.createProperty;
import static org.apache.jena.core.rdf.model.ResourceFactory.createResource;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.trellisldp.vocabulary.RDF.type;
import static org.trellisldp.vocabulary.Trellis.PreferUserManaged;

import org.apache.commons.rdf.api.Dataset;
import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.Literal;
import org.apache.commons.rdf.api.RDF;
import org.apache.commons.rdf.jena.JenaRDF;
import org.apache.commons.rdf.simple.SimpleRDF;
import org.apache.jena.core.rdf.model.Property;
import org.apache.jena.core.rdf.model.Resource;
import org.junit.jupiter.api.Test;
import org.trellisldp.vocabulary.AS;
import org.trellisldp.vocabulary.DC;
import org.trellisldp.vocabulary.SKOS;

/**
 * ResourceService tests.
 */
public class TriplestoreUtilsTest {

    private static final RDF simpleRdf = new SimpleRDF();
    private static final RDF jenaRdf = new JenaRDF();

    private static final IRI subject = simpleRdf.createIRI("http://example.com");
    private static final Literal literal = simpleRdf.createLiteral("title");

    @Test
    public void testDatasetNoConversion() {
        final Dataset dataset = jenaRdf.createDataset();

        dataset.add(jenaRdf.createQuad(PreferUserManaged, subject, SKOS.prefLabel, literal));
        dataset.add(jenaRdf.createQuad(PreferUserManaged, subject, type, SKOS.Concept));
        dataset.add(jenaRdf.createQuad(PreferUserManaged, subject, DC.subject, AS.Activity));
        assertEquals(3L, dataset.size());

        assertTrue(TriplestoreUtils.asJenaDataset(dataset).containsNamedModel(PreferUserManaged.getIRIString()));
        assertEquals(TriplestoreUtils.asJenaDataset(dataset).asDatasetGraph(),
                TriplestoreUtils.asJenaDataset(dataset).asDatasetGraph());
    }

    @Test
    public void testDatasetConversion() {
        final Dataset dataset = simpleRdf.createDataset();

        dataset.add(simpleRdf.createQuad(PreferUserManaged, subject, SKOS.prefLabel, literal));
        dataset.add(simpleRdf.createQuad(PreferUserManaged, subject, type, SKOS.Concept));
        dataset.add(simpleRdf.createQuad(PreferUserManaged, subject, DC.subject, AS.Activity));
        assertEquals(3L, dataset.size());

        assertTrue(TriplestoreUtils.asJenaDataset(dataset).containsNamedModel(PreferUserManaged.getIRIString()));
        assertNotEquals(TriplestoreUtils.asJenaDataset(dataset).asDatasetGraph(),
                TriplestoreUtils.asJenaDataset(dataset).asDatasetGraph());
    }

    @Test
    public void testBaseIRItruncate() {
        final IRI iri = jenaRdf.createIRI("http://example.com/resource#i");
        assertEquals(jenaRdf.createIRI("http://example.com/resource"), TriplestoreUtils.getBaseIRI(iri));
    }

    @Test
    public void testBaseIRInoTruncate() {
        final IRI iri = jenaRdf.createIRI("http://example.com/resource");
        assertEquals(iri, TriplestoreUtils.getBaseIRI(iri));
    }

    @Test
    public void testNodeConversion() {
        final Resource s = createResource("http://example.com/Resource");
        final Property p = createProperty("http://example.com/prop");
        final Resource o = createResource("http://example.com/Other");
        assertFalse(TriplestoreUtils.nodesToTriple(null, null, null).isPresent());
        assertFalse(TriplestoreUtils.nodesToTriple(s, null, null).isPresent());
        assertFalse(TriplestoreUtils.nodesToTriple(null, p, null).isPresent());
        assertFalse(TriplestoreUtils.nodesToTriple(null, null, o).isPresent());
        assertFalse(TriplestoreUtils.nodesToTriple(s, p, null).isPresent());
        assertFalse(TriplestoreUtils.nodesToTriple(s, null, o).isPresent());
        assertFalse(TriplestoreUtils.nodesToTriple(null, p, o).isPresent());
        assertTrue(TriplestoreUtils.nodesToTriple(s, p, o).isPresent());
    }

    @Test
    public void testBaseIRIliteral() {
        final Literal l = jenaRdf.createLiteral("a literal");
        assertEquals(l, TriplestoreUtils.getBaseIRI(l));
    }
}
