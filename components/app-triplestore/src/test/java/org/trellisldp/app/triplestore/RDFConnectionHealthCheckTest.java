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
package org.trellisldp.app.triplestore;

import static org.apache.jena.arq.query.DatasetFactory.wrap;
import static org.apache.jena.rdfconnection.RDFConnectionFactory.connect;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.codahale.metrics.health.HealthCheck;

import org.apache.commons.rdf.jena.JenaDataset;
import org.apache.commons.rdf.jena.JenaRDF;
import org.apache.jena.rdfconnection.RDFConnection;
import org.junit.jupiter.api.Test;

/**
 * @author acoburn
 */
public class RDFConnectionHealthCheckTest {

    private static final JenaRDF rdf = new JenaRDF();

    @Test
    public void testIsConnected() throws Exception {
        final JenaDataset dataset = rdf.createDataset();
        final RDFConnection rdfConnection = connect(wrap(dataset.asJenaDatasetGraph()));
        final HealthCheck check = new RDFConnectionHealthCheck(rdfConnection);
        assertTrue(check.execute().isHealthy(), "RDFConnection isn't healthy!");
    }

    @Test
    public void testNonConnected() throws Exception {
        final JenaDataset dataset = rdf.createDataset();
        final RDFConnection rdfConnection = connect(wrap(dataset.asJenaDatasetGraph()));
        rdfConnection.close();
        final HealthCheck check = new RDFConnectionHealthCheck(rdfConnection);
        assertFalse(check.execute().isHealthy(), "Closed RDFConnection doesn't report as unhealthy!");
    }
}
