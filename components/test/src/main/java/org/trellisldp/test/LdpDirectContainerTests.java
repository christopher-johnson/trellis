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
package org.trellisldp.test;

import static javax.ws.rs.client.Entity.entity;
import static javax.ws.rs.core.HttpHeaders.LINK;
import static javax.ws.rs.core.Link.TYPE;
import static javax.ws.rs.core.Link.fromUri;
import static javax.ws.rs.core.Response.Status.Family.CLIENT_ERROR;
import static javax.ws.rs.core.Response.Status.Family.SUCCESSFUL;
import static org.apache.commons.rdf.api.RDFSyntax.TURTLE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.trellisldp.api.RDFUtils.getInstance;
import static org.trellisldp.http.domain.HttpConstants.PREFER;
import static org.trellisldp.http.domain.RdfMediaType.APPLICATION_SPARQL_UPDATE;
import static org.trellisldp.http.domain.RdfMediaType.TEXT_TURTLE;
import static org.trellisldp.test.TestUtils.getLinks;
import static org.trellisldp.test.TestUtils.getResourceAsString;
import static org.trellisldp.test.TestUtils.hasConstrainedBy;
import static org.trellisldp.test.TestUtils.hasType;
import static org.trellisldp.test.TestUtils.meanwhile;
import static org.trellisldp.test.TestUtils.readEntityAsGraph;

import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Response;

import org.apache.commons.rdf.api.Graph;
import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.RDF;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.trellisldp.vocabulary.DC;
import org.trellisldp.vocabulary.LDP;
import org.trellisldp.vocabulary.SKOS;
import org.trellisldp.vocabulary.Trellis;

/**
 * Run LDP-related tests on a Trellis application.
 */
@TestInstance(PER_CLASS)
@DisplayName("Direct Container Tests")
public interface LdpDirectContainerTests extends CommonTests {

    String MEMBER_RESOURCE1 = "/members1";
    String MEMBER_RESOURCE2 = "/members2";
    String MEMBER_RESOURCE_HASH = "#members";
    String BASIC_CONTAINER = "/basicContainer.ttl";
    String DIRECT_CONTAINER = "/directContainer.ttl";
    String SIMPLE_RESOURCE = "/simpleResource.ttl";

    /**
     * Set the location of the test resource.
     * @param location the location
     */
    void setMemberLocation(String location);

    /**
     * Get the location of the test resource.
     * @return the test resource location
     */
    String getMemberLocation();

    /**
     * Get the location of a child resource.
     * @return the location
     */
    String getChildLocation();

    /**
     * Set the location of a child resource.
     * @param location the location
     */
    void setChildLocation(String location);

    /**
     * Set the location of the test container.
     * @param location the location
     */
    void setContainerLocation(String location);

    /**
     * Get the location of the test container.
     * @return the test container location
     */
    String getContainerLocation();

    /**
     * Set the location of the direct container.
     * @param location the location
     */
    void setFirstDirectContainerLocation(String location);

    /**
     * Get the location of the direct container.
     * @return the test container location
     */
    String getFirstDirectContainerLocation();

    /**
     * Set the location of the other direct container.
     * @param location the location
     */
    void setSecondDirectContainerLocation(String location);

    /**
     * Get the location of the other direct container.
     * @return the test container location
     */
    String getSecondDirectContainerLocation();

    /**
     * Set the location of the other direct container.
     * @param location the location
     */
    void setThirdDirectContainerLocation(String location);

    /**
     * Get the location of the other direct container.
     * @return the test container location
     */
    String getThirdDirectContainerLocation();

    /**
     * Initialize Direct Container tests.
     */
    @BeforeAll
    @DisplayName("Initialize Direct Container tests")
    default void beforeAllTests() {
        final String containerContent = getResourceAsString(BASIC_CONTAINER);

        // POST an LDP-BC
        try (final Response res = target().request()
                .header(LINK, fromUri(LDP.BasicContainer.getIRIString()).rel(TYPE).build())
                .post(entity(containerContent, TEXT_TURTLE))) {
            assumeTrue(SUCCESSFUL.equals(res.getStatusInfo().getFamily()),
                    "Creation of BasicContainer appears to be unsupported");
            assumeTrue(getLinks(res).stream().anyMatch(hasType(LDP.BasicContainer)),
                    "New resource was not of expected BasicContainer type");

            setContainerLocation(res.getLocation().toString());
        }

        setMemberLocation(getContainerLocation() + MEMBER_RESOURCE1);

        final String content = getResourceAsString(DIRECT_CONTAINER)
            + membershipResource(getMemberLocation());

        // POST an LDP-DC
        try (final Response res = target(getContainerLocation()).request()
                .header(LINK, fromUri(LDP.DirectContainer.getIRIString()).rel(TYPE).build())
                .post(entity(content, TEXT_TURTLE))) {
            assumeTrue(SUCCESSFUL.equals(res.getStatusInfo().getFamily()),
                    "Creation of DirectContainer appears to be unsupported");
            assumeTrue(getLinks(res).stream().anyMatch(hasType(LDP.DirectContainer)),
                    "New resource was not of expected DirectContainer type");

            setFirstDirectContainerLocation(res.getLocation().toString());
        }

        final String memberContent = getResourceAsString(SIMPLE_RESOURCE);

        // PUT an LDP-RS
        try (final Response res = target(getMemberLocation()).request().put(entity(containerContent, TEXT_TURTLE))) {
            assumeTrue(SUCCESSFUL.equals(res.getStatusInfo().getFamily()),
                    "Creation of RDFSource appears to be unsupported");
            assumeTrue(getLinks(res).stream().anyMatch(hasType(LDP.RDFSource)),
                    "New resource was not of expected RDFSource type");
        }

        final String simpleContent = getResourceAsString(DIRECT_CONTAINER)
            + membershipResource(MEMBER_RESOURCE_HASH);

        // POST an LDP-DC
        try (final Response res = target(getContainerLocation()).request()
                .header(LINK, fromUri(LDP.DirectContainer.getIRIString()).rel(TYPE).build())
                .post(entity(simpleContent, TEXT_TURTLE))) {
            assumeTrue(SUCCESSFUL.equals(res.getStatusInfo().getFamily()),
                    "Creation of DirectContainer appears to be unsupported");
            assumeTrue(getLinks(res).stream().anyMatch(hasType(LDP.DirectContainer)),
                    "New resource was not of expected DirectContainer type");

            setSecondDirectContainerLocation(res.getLocation().toString());
        }

        // POST an LDP-RS
        try (final Response res = target(getSecondDirectContainerLocation()).request()
                .post(entity(memberContent, TEXT_TURTLE))) {
            assumeTrue(SUCCESSFUL.equals(res.getStatusInfo().getFamily()),
                    "Creation of RDFSource appears to be unsupported");
            assumeTrue(getLinks(res).stream().anyMatch(hasType(LDP.RDFSource)),
                    "New resource was not of expected RDFSource type");

            setChildLocation(res.getLocation().toString());
        }

        setThirdDirectContainerLocation(getContainerLocation() + "/other");

        // PUT an LDP-DC
        try (final Response res = target(getThirdDirectContainerLocation()).request()
                .header(LINK, fromUri(LDP.DirectContainer.getIRIString()).rel(TYPE).build())
                .put(entity(content, TEXT_TURTLE))) {
            assumeTrue(SUCCESSFUL.equals(res.getStatusInfo().getFamily()),
                    "Creation of DirectContainer appears to be unsupported");
            assumeTrue(getLinks(res).stream().anyMatch(hasType(LDP.DirectContainer)),
                    "New resource was not of expected DirectContainer type");
        }
    }

    /**
     * Test fetch a self-contained direct container.
     */
    @Test
    @DisplayName("Test fetch a self-contained direct container")
    default void testSimpleDirectContainer() {
        final RDF rdf = getInstance();
        // Fetch the member resource
        try (final Response res = target(getSecondDirectContainerLocation()).request().get()) {
            assertEquals(SUCCESSFUL, res.getStatusInfo().getFamily());
            assertTrue(getLinks(res).stream().anyMatch(hasType(LDP.RDFSource)));
            final Graph g = readEntityAsGraph(res.getEntity(), getBaseURL(), TURTLE);
            assertTrue(g.contains(rdf.createIRI(getSecondDirectContainerLocation()), LDP.contains,
                        rdf.createIRI(getChildLocation())));
            assertTrue(g.contains(rdf.createIRI(getSecondDirectContainerLocation() + MEMBER_RESOURCE_HASH), LDP.member,
                        rdf.createIRI(getChildLocation())));
        }
    }

    /**
     * Test adding resources to the direct container.
     */
    @Test
    @DisplayName("Test adding resources to the direct container")
    default void testAddingMemberResources() {
        final RDF rdf = getInstance();
        final String child1;
        final String child2;
        final EntityTag etag1;
        final EntityTag etag2;
        final EntityTag etag3;
        final EntityTag etag4;
        final EntityTag etag5;
        final EntityTag etag6;
        final String childContent = getResourceAsString(SIMPLE_RESOURCE);

        // Fetch the member resource
        try (final Response res = target(getMemberLocation()).request().get()) {
            assertEquals(SUCCESSFUL, res.getStatusInfo().getFamily());
            assertTrue(getLinks(res).stream().anyMatch(hasType(LDP.RDFSource)));
            final Graph g = readEntityAsGraph(res.getEntity(), getBaseURL(), TURTLE);
            assertFalse(g.contains(rdf.createIRI(getMemberLocation()), LDP.member, null));
            etag1 = res.getEntityTag();
            assertTrue(etag1.isWeak());
        }

        // Fetch the container resource
        try (final Response res = target(getFirstDirectContainerLocation()).request().get()) {
            assertEquals(SUCCESSFUL, res.getStatusInfo().getFamily());
            assertTrue(getLinks(res).stream().anyMatch(hasType(LDP.DirectContainer)));
            final Graph g = readEntityAsGraph(res.getEntity(), getBaseURL(), TURTLE);
            assertFalse(g.contains(rdf.createIRI(getFirstDirectContainerLocation()), LDP.contains, null));
            etag4 = res.getEntityTag();
            assertTrue(etag4.isWeak());
        }

        meanwhile();

        // POST an LDP-RS child
        try (final Response res = target(getFirstDirectContainerLocation()).request()
                .post(entity(childContent, TEXT_TURTLE))) {
            assertEquals(SUCCESSFUL, res.getStatusInfo().getFamily());
            assertTrue(getLinks(res).stream().anyMatch(hasType(LDP.RDFSource)));

            child1 = res.getLocation().toString();
            assertTrue(child1.startsWith(getFirstDirectContainerLocation()));
            assertTrue(child1.length() > getFirstDirectContainerLocation().length());
        }

        // POST an LDP-RS child
        try (final Response res = target(getFirstDirectContainerLocation()).request()
                .post(entity(childContent, TEXT_TURTLE))) {
            assertEquals(SUCCESSFUL, res.getStatusInfo().getFamily());
            assertTrue(getLinks(res).stream().anyMatch(hasType(LDP.RDFSource)));

            child2 = res.getLocation().toString();
            assertTrue(child2.startsWith(getFirstDirectContainerLocation()));
            assertTrue(child2.length() > getFirstDirectContainerLocation().length());
        }

        // Fetch the member resource
        try (final Response res = target(getMemberLocation()).request().get()) {
            assertEquals(SUCCESSFUL, res.getStatusInfo().getFamily());
            assertTrue(getLinks(res).stream().anyMatch(hasType(LDP.RDFSource)));
            final Graph g = readEntityAsGraph(res.getEntity(), getBaseURL(), TURTLE);
            final IRI identifier = rdf.createIRI(getMemberLocation());
            assertTrue(g.contains(identifier, LDP.member, rdf.createIRI(child1)));
            assertTrue(g.contains(identifier, LDP.member, rdf.createIRI(child2)));
            etag2 = res.getEntityTag();
            assertTrue(etag2.isWeak());
            assertNotEquals(etag1, etag2);
        }

        // Fetch the container resource
        try (final Response res = target(getFirstDirectContainerLocation()).request().get()) {
            assertEquals(SUCCESSFUL, res.getStatusInfo().getFamily());
            assertTrue(getLinks(res).stream().anyMatch(hasType(LDP.DirectContainer)));
            final Graph g = readEntityAsGraph(res.getEntity(), getBaseURL(), TURTLE);
            final IRI identifier = rdf.createIRI(getFirstDirectContainerLocation());
            assertTrue(g.contains(identifier, LDP.contains, rdf.createIRI(child1)));
            assertTrue(g.contains(identifier, LDP.contains, rdf.createIRI(child2)));
            etag5 = res.getEntityTag();
            assertTrue(etag5.isWeak());
            assertNotEquals(etag4, etag5);
        }

        meanwhile();

        // Delete one of the child resources
        try (final Response res = target(child1).request().delete()) {
            assertEquals(SUCCESSFUL, res.getStatusInfo().getFamily());
        }

        // Try fetching the deleted resource
        try (final Response res = target(child1).request().get()) {
            assertEquals(CLIENT_ERROR, res.getStatusInfo().getFamily());
        }

        // Fetch the member resource
        try (final Response res = target(getMemberLocation()).request().get()) {
            assertEquals(SUCCESSFUL, res.getStatusInfo().getFamily());
            assertTrue(getLinks(res).stream().anyMatch(hasType(LDP.RDFSource)));
            final Graph g = readEntityAsGraph(res.getEntity(), getBaseURL(), TURTLE);
            final IRI identifier = rdf.createIRI(getMemberLocation());
            assertFalse(g.contains(identifier, LDP.member, rdf.createIRI(child1)));
            assertTrue(g.contains(identifier, LDP.member, rdf.createIRI(child2)));
            etag3 = res.getEntityTag();
            assertTrue(etag3.isWeak());
            assertNotEquals(etag1, etag3);
            assertNotEquals(etag2, etag3);
        }

        // Fetch the container resource
        try (final Response res = target(getFirstDirectContainerLocation()).request().get()) {
            assertEquals(SUCCESSFUL, res.getStatusInfo().getFamily());
            assertTrue(getLinks(res).stream().anyMatch(hasType(LDP.DirectContainer)));
            final Graph g = readEntityAsGraph(res.getEntity(), getBaseURL(), TURTLE);
            final IRI identifier = rdf.createIRI(getFirstDirectContainerLocation());
            assertFalse(g.contains(identifier, LDP.contains, rdf.createIRI(child1)));
            assertTrue(g.contains(identifier, LDP.contains, rdf.createIRI(child2)));
            etag6 = res.getEntityTag();
            assertTrue(etag6.isWeak());
            assertNotEquals(etag5, etag6);
            assertNotEquals(etag4, etag6);
        }

        // Now change the membership property
        final String updateContent
            = "PREFIX dc: <http://purl.org/dc/terms/>\n"
            + "PREFIX ldp: <http://www.w3.org/ns/ldp#>\n\n"
            + "DELETE WHERE { <> ldp:hasMemberRelation ?o };"
            + "INSERT { <> ldp:hasMemberRelation dc:relation } WHERE {}";

        // Patch the direct container
        try (final Response res = target(getFirstDirectContainerLocation()).request()
                .method("PATCH", entity(updateContent, APPLICATION_SPARQL_UPDATE))) {
            assertEquals(SUCCESSFUL, res.getStatusInfo().getFamily());
            assertTrue(getLinks(res).stream().anyMatch(hasType(LDP.DirectContainer)));
        }

        // Fetch the member resource
        try (final Response res = target(getMemberLocation()).request().get()) {
            assertEquals(SUCCESSFUL, res.getStatusInfo().getFamily());
            assertTrue(getLinks(res).stream().anyMatch(hasType(LDP.RDFSource)));
            final Graph g = readEntityAsGraph(res.getEntity(), getBaseURL(), TURTLE);
            final IRI identifier = rdf.createIRI(getMemberLocation());
            assertTrue(g.contains(identifier, DC.relation, rdf.createIRI(child2)));
        }
    }

    /**
     * Test creating a direct container via PUT.
     */
    @Test
    @DisplayName("Test creating a direct container via PUT")
    default void testCreateDirectContainerViaPut() {
        final String other2 = getContainerLocation() + "/other2";
        final String content = getResourceAsString(DIRECT_CONTAINER)
            + membershipResource(getContainerLocation() + MEMBER_RESOURCE2);

        // PUT an LDP-DC
        try (final Response res = target(other2).request()
                .header(LINK, fromUri(LDP.DirectContainer.getIRIString()).rel(TYPE).build())
                .put(entity(content, TEXT_TURTLE))) {
            assertEquals(SUCCESSFUL, res.getStatusInfo().getFamily());
            assertTrue(getLinks(res).stream().anyMatch(hasType(LDP.DirectContainer)));
        }
    }

    /**
     * Test updating a direct container via PUT.
     */
    @Test
    @DisplayName("Test updating a direct container via PUT")
    default void testUpdateDirectContainerViaPut() {
        final String content = getResourceAsString("/directContainerIsPartOf.ttl")
            + membershipResource(getContainerLocation() + MEMBER_RESOURCE2);

        // PUT an LDP-DC
        try (final Response res = target(getThirdDirectContainerLocation()).request()
                .header(LINK, fromUri(LDP.DirectContainer.getIRIString()).rel(TYPE).build())
                .put(entity(content, TEXT_TURTLE))) {
            assertEquals(SUCCESSFUL, res.getStatusInfo().getFamily());
            assertTrue(getLinks(res).stream().anyMatch(hasType(LDP.DirectContainer)));
        }
    }

    /**
     * Test updating a direct container with too many member-related properties.
     */
    @Test
    @DisplayName("Test updating a direct container with too many member-related properties")
    default void testUpdateDirectContainerTooManyMemberProps() {
        final String content = getResourceAsString(DIRECT_CONTAINER)
            + membershipResource(getContainerLocation() + MEMBER_RESOURCE2)
            + "<> ldp:isMemberOfRelation dc:isPartOf .";

        // PUT an LDP-DC
        try (final Response res = target(getThirdDirectContainerLocation()).request()
                .header(LINK, fromUri(LDP.DirectContainer.getIRIString()).rel(TYPE).build())
                .put(entity(content, TEXT_TURTLE))) {
            assertEquals(CLIENT_ERROR, res.getStatusInfo().getFamily());
            assertTrue(getLinks(res).stream().anyMatch(hasConstrainedBy(Trellis.InvalidCardinality)));
        }
    }

    /**
     * Test updating a direct container with too many membership resources.
     */
    @Test
    @DisplayName("Test updating a direct container with too many membership resources")
    default void testUpdateDirectContainerMultipleMemberResources() {
        final String content = getResourceAsString("/directContainer.ttl")
            + membershipResource(getContainerLocation() + MEMBER_RESOURCE2)
            + membershipResource(getContainerLocation() + "/member3");

        // PUT an LDP-DC
        try (final Response res = target(getThirdDirectContainerLocation()).request()
                .header(LINK, fromUri(LDP.DirectContainer.getIRIString()).rel(TYPE).build())
                .put(entity(content, TEXT_TURTLE))) {
            assertEquals(CLIENT_ERROR, res.getStatusInfo().getFamily());
            assertTrue(getLinks(res).stream().anyMatch(hasConstrainedBy(Trellis.InvalidCardinality)));
        }
    }

    /**
     * Test updating a direct container with no member relation property.
     */
    @Test
    @DisplayName("Test updating a direct container with no member relation property")
    default void testUpdateDirectContainerMissingMemberRelation() {
        final String content = "PREFIX skos: <http://www.w3.org/2004/02/skos/core#> \n"
            + "PREFIX ldp: <http://www.w3.org/ns/ldp#> \n"
            + "PREFIX dc: <http://purl.org/dc/terms/> \n\n"
            + "<> skos:prefLabel \"Direct Container\"@eng ; "
            + "   ldp:membershipResource <" + getContainerLocation() + MEMBER_RESOURCE2 + "> ; "
            + "   dc:description \"This is a Direct Container for testing.\"@eng .";

        // PUT an LDP-DC
        try (final Response res = target(getThirdDirectContainerLocation()).request()
                .header(LINK, fromUri(LDP.DirectContainer.getIRIString()).rel(TYPE).build())
                .put(entity(content, TEXT_TURTLE))) {
            assertEquals(CLIENT_ERROR, res.getStatusInfo().getFamily());
            assertTrue(getLinks(res).stream().anyMatch(hasConstrainedBy(Trellis.InvalidCardinality)));
        }
    }

    /**
     * Test updating a direct container with no member resource.
     */
    @Test
    @DisplayName("Test updating a direct container with no member resource")
    default void testUpdateDirectContainerMissingMemberResource() {
        final String content = getResourceAsString("/directContainer.ttl");

        // PUT an LDP-DC
        try (final Response res = target(getThirdDirectContainerLocation()).request()
                .header(LINK, fromUri(LDP.DirectContainer.getIRIString()).rel(TYPE).build())
                .put(entity(content, TEXT_TURTLE))) {
            assertEquals(CLIENT_ERROR, res.getStatusInfo().getFamily());
            assertTrue(getLinks(res).stream().anyMatch(hasConstrainedBy(Trellis.InvalidCardinality)));
        }
    }

    /**
     * Test with ldp:PreferMinimalContainer Prefer header.
     */
    @Test
    @DisplayName("Test with ldp:PreferMinimalContainer Prefer header")
    default void testGetEmptyMember() {
        final RDF rdf = getInstance();
        try (final Response res = target(getMemberLocation()).request().header(PREFER,
                    "return=representation; include=\"" + LDP.PreferMinimalContainer.getIRIString() + "\"").get()) {
            assertEquals(SUCCESSFUL, res.getStatusInfo().getFamily());
            final Graph g = readEntityAsGraph(res.getEntity(), getBaseURL(), TURTLE);
            final IRI identifier = rdf.createIRI(getMemberLocation());
            assertTrue(g.contains(identifier, SKOS.prefLabel, null));
            assertFalse(g.contains(identifier, LDP.member, null));
            assertFalse(g.contains(identifier, DC.relation, null));
        }
    }

    /**
     * Test with ldp:PreferMinimalContainer Prefer header.
     */
    @Test
    @DisplayName("Test with ldp:PreferMinimalContainer Prefer header")
    default void testGetInverseEmptyMember() {
        final RDF rdf = getInstance();
        try (final Response res = target(getMemberLocation()).request().header(PREFER,
                    "return=representation; omit=\"" + LDP.PreferMinimalContainer.getIRIString() + "\"").get()) {
            assertEquals(SUCCESSFUL, res.getStatusInfo().getFamily());
            final Graph g = readEntityAsGraph(res.getEntity(), getBaseURL(), TURTLE);
            final IRI identifier = rdf.createIRI(getMemberLocation());
            assertFalse(g.contains(identifier, SKOS.prefLabel, null));
            assertTrue(g.contains(identifier, LDP.member, null) || g.contains(identifier, DC.relation, null));
        }
    }

    /**
     * Create an ldp:membershipResource triple.
     * @param iri the IRI
     * @return the triple
     */
    default String membershipResource(final String iri) {
        return "<> ldp:membershipResource <" + iri + ">.\n";
    }
}
