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


import static java.util.Objects.nonNull;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.apache.jena.arq.query.DatasetFactory.wrap;

import java.util.Optional;

import org.apache.commons.rdf.api.BlankNodeOrIRI;
import org.apache.commons.rdf.api.Dataset;
import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.RDFTerm;
import org.apache.commons.rdf.api.Triple;
import org.apache.commons.rdf.jena.JenaDataset;
import org.apache.commons.rdf.jena.JenaRDF;
import org.apache.jena.arq.query.QuerySolution;
import org.apache.jena.arq.sparql.core.DatasetGraph;
import org.apache.jena.arq.sparql.core.DatasetGraphFactory;
import org.apache.jena.arq.sparql.core.Var;
import org.apache.jena.core.rdf.model.RDFNode;

/**
 * Utilities for the Triplestore resource service.
 */
final class TriplestoreUtils {

    private static final JenaRDF rdf = new JenaRDF();

    public static final Var SUBJECT = Var.alloc("subject");
    public static final Var PREDICATE = Var.alloc("predicate");
    public static final Var OBJECT = Var.alloc("object");

    public static JenaRDF getInstance() {
        return rdf;
    }

    public static BlankNodeOrIRI getSubject(final QuerySolution qs) {
        return (BlankNodeOrIRI) rdf.asRDFTerm(qs.get("subject").asNode());
    }

    public static IRI getPredicate(final QuerySolution qs) {
        return (IRI) rdf.asRDFTerm(qs.get("predicate").asNode());
    }

    public static RDFTerm getObject(final QuerySolution qs) {
        return rdf.asRDFTerm(qs.get("object").asNode());
    }

    public static RDFTerm getBaseIRI(final RDFTerm object) {
        if (object instanceof IRI) {
            final String iri = ((IRI) object).getIRIString().split("#")[0];
            return rdf.createIRI(iri);
        }
        return object;
    }

    public static Optional<Triple> nodesToTriple(final RDFNode s, final RDFNode p, final RDFNode o) {
        if (nonNull(s) && nonNull(p) && nonNull(o)) {
            return of(rdf.createTriple((BlankNodeOrIRI) rdf.asRDFTerm(s.asNode()),
                            (IRI) rdf.asRDFTerm(p.asNode()), rdf.asRDFTerm(o.asNode())));
        }
        return empty();
    }

    /**
     * TODO Replace when COMMONSRDF-74 is released.
     *
     * @param dataset a Commons RDF {@link Dataset}
     * @return a Jena {@link org.apache.jena.arq.query.Dataset}
     */
    public static org.apache.jena.arq.query.Dataset asJenaDataset(final Dataset dataset) {
        final DatasetGraph dsg;
        if (dataset instanceof JenaDataset) {
            dsg = ((JenaDataset) dataset).asJenaDatasetGraph();
        } else {
            dsg = DatasetGraphFactory.createGeneral();
            dataset.stream().map(rdf::asJenaQuad).forEach(dsg::add);
        }
        return wrap(dsg);
    }

    private TriplestoreUtils() {
        // prevent instantiation
    }
}
