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
import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.HttpHeaders.LINK;
import static javax.ws.rs.core.Link.TYPE;
import static javax.ws.rs.core.Link.fromUri;
import static javax.ws.rs.core.Response.Status.Family.SUCCESSFUL;
import static org.apache.commons.codec.binary.Base64.encodeBase64String;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.trellisldp.http.domain.RdfMediaType.APPLICATION_SPARQL_UPDATE;
import static org.trellisldp.http.domain.RdfMediaType.TEXT_TURTLE;
import static org.trellisldp.test.TestUtils.buildJwt;
import static org.trellisldp.test.TestUtils.getLinks;
import static org.trellisldp.test.TestUtils.getResourceAsString;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.Response;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.TestInstance;
import org.trellisldp.vocabulary.LDP;
import org.trellisldp.vocabulary.Trellis;

/**
 * A convenience class for running the Auth tests.
 */
public abstract class AbstractApplicationAuthTests {

    /**
     * Get the HTTP client.
     * @return the HTTP client
     */
    public abstract Client getClient();

    /**
     * Get the baseURL for the LDP server.
     * @return the base URL
     */
    public abstract String getBaseURL();

    /**
     * Get the JWT secret.
     * @return the JWT secret
     */
    public abstract String getJwtSecret();

    /**
     * Get the credentials for the first user.
     * @return the credentials
     */
    public abstract String getUser1Credentials();

    /**
     * Get the credentials for the second user.
     * @return the credentials
     */
    public abstract String getUser2Credentials();

    @Nested
    @DisplayName("Administrator JWT Auth tests")
    @TestInstance(PER_CLASS)
    public class AdministratorTests extends BasicTests implements AuthAdministratorTests {

        @Override
        public String getAuthorizationHeader() {
            return buildJwt(Trellis.AdministratorAgent.getIRIString(),
                    AbstractApplicationAuthTests.this.getJwtSecret());
        }
    }

    @Nested
    @DisplayName("User JWT Auth tests")
    @TestInstance(PER_CLASS)
    public class UserTests extends BasicTests implements AuthUserTests {

        @Override
        public String getAuthorizationHeader() {
            return buildJwt("https://people.apache.org/~acoburn/#i",
                    AbstractApplicationAuthTests.this.getJwtSecret());
        }
    }

    @Nested
    @DisplayName("User Basic Auth tests")
    @TestInstance(PER_CLASS)
    public class UserBasicAuthTests extends BasicTests implements AuthUserTests {
        @Override
        public String getAuthorizationHeader() {
            return "Basic " + encodeBase64String(AbstractApplicationAuthTests.this.getUser1Credentials().getBytes());
        }
    }

    @Nested
    @DisplayName("Other user JWT Auth tests")
    @TestInstance(PER_CLASS)
    public class OtherUserTests extends BasicTests implements AuthOtherUserTests {

        @Override
        public String getAuthorizationHeader() {
            return buildJwt("https://madison.example.com/profile/#me",
                    AbstractApplicationAuthTests.this.getJwtSecret());
        }
    }

    @Nested
    @DisplayName("Other user Basic Auth tests")
    @TestInstance(PER_CLASS)
    public class OtherUserBasicAuthTests extends BasicTests implements AuthOtherUserTests {
        @Override
        public String getAuthorizationHeader() {
            return "Basic " + encodeBase64String(AbstractApplicationAuthTests.this.getUser2Credentials().getBytes());
        }
    }

    @Nested
    @DisplayName("Anonymous Auth tests")
    @TestInstance(PER_CLASS)
    public class AnonymousTests extends BasicTests implements AuthAnonymousTests {
        @Override
        public String getAuthorizationHeader() {
            return null;
        }
    }

    private abstract class BasicTests implements AuthCommonTests {

        private String publicContainer;
        private String publicContainerChild;
        private String protectedContainer;
        private String protectedContainerChild;
        private String privateContainer;
        private String privateContainerChild;
        private String groupContainer;
        private String groupContainerChild;
        private String defaultContainer;
        private String defaultContainerChild;

        @Override
        public String getBaseURL() {
            return AbstractApplicationAuthTests.this.getBaseURL();
        }

        @Override
        public Client getClient() {
            return AbstractApplicationAuthTests.this.getClient();
        }

        @Override
        public String getPublicContainer() {
            return publicContainer;
        }

        @Override
        public void setPublicContainer(final String location) {
            this.publicContainer = location;
        }

        @Override
        public String getPublicContainerChild() {
            return publicContainerChild;
        }

        @Override
        public void setPublicContainerChild(final String location) {
            this.publicContainerChild = location;
        }

        @Override
        public String getProtectedContainer() {
            return protectedContainer;
        }

        @Override
        public void setProtectedContainer(final String location) {
            this.protectedContainer = location;
        }

        @Override
        public String getProtectedContainerChild() {
            return protectedContainerChild;
        }

        @Override
        public void setProtectedContainerChild(final String location) {
            this.protectedContainerChild = location;
        }

        @Override
        public String getPrivateContainer() {
            return privateContainer;
        }

        @Override
        public void setPrivateContainer(final String location) {
            this.privateContainer = location;
        }

        @Override
        public String getPrivateContainerChild() {
            return privateContainerChild;
        }

        @Override
        public void setPrivateContainerChild(final String location) {
            this.privateContainerChild = location;
        }

        @Override
        public String getDefaultContainer() {
            return defaultContainer;
        }

        @Override
        public void setDefaultContainer(final String location) {
            this.defaultContainer = location;
        }

        @Override
        public String getDefaultContainerChild() {
            return defaultContainerChild;
        }

        @Override
        public void setDefaultContainerChild(final String location) {
            this.defaultContainerChild = location;
        }

        @Override
        public String getGroupContainer() {
            return groupContainer;
        }

        @Override
        public void setGroupContainer(final String location) {
            this.groupContainer = location;
        }

        @Override
        public String getGroupContainerChild() {
            return groupContainerChild;
        }

        @Override
        public void setGroupContainerChild(final String location) {
            this.groupContainerChild = location;
        }

        @BeforeAll
        @DisplayName("Initialize Auth tests")
        protected void setUp() {
            final String acl = "acl";
            final String prefixAcl = "PREFIX acl: <http://www.w3.org/ns/auth/acl#>\n\n";
            final String jwt = buildJwt(Trellis.AdministratorAgent.getIRIString(),
                    AbstractApplicationAuthTests.this.getJwtSecret());

            final String containerContent = getResourceAsString("/basicContainer.ttl");
            final String container;
            final String groupResource;

            // POST an LDP-BC
            try (final Response res = target().request()
                    .header(LINK, fromUri(LDP.BasicContainer.getIRIString()).rel(TYPE).build())
                    .header(AUTHORIZATION, jwt).post(entity(containerContent, TEXT_TURTLE))) {
                assertEquals(SUCCESSFUL, res.getStatusInfo().getFamily());
                container = res.getLocation().toString();
            }

            // POST a public container
            try (final Response res = target(container).request()
                    .header(LINK, fromUri(LDP.BasicContainer.getIRIString()).rel(TYPE).build())
                    .header(AUTHORIZATION, jwt).post(entity(containerContent, TEXT_TURTLE))) {
                assertEquals(SUCCESSFUL, res.getStatusInfo().getFamily());
                setPublicContainer(res.getLocation().toString());
            }

            // Add a child to the public container
            try (final Response res = target(publicContainer).request().header(AUTHORIZATION, jwt)
                    .post(entity("", TEXT_TURTLE))) {
                assertEquals(SUCCESSFUL, res.getStatusInfo().getFamily());
                setPublicContainerChild(res.getLocation().toString());
                final String publicContainerAcl = getLinks(res).stream().filter(link -> link.getRel().equals(acl))
                    .map(link -> link.getUri().toString()).findFirst().orElse("");
                assertEquals(getPublicContainer() + EXT_ACL, publicContainerAcl);
            }

            final String publicAcl = "PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n"
                + prefixAcl
                + "INSERT DATA { [acl:accessTo <" + publicContainer + ">; acl:mode acl:Read; "
                + "   acl:agentClass foaf:Agent ] }; \n"
                + prefixAcl
                + "INSERT DATA { [acl:accessTo <" + publicContainer + ">; acl:mode acl:Read, acl:Write;"
                + "   acl:agentClass acl:AuthenticatedAgent ] }";

            // Add an ACL for the public container
            try (final Response res = target(getPublicContainer() + EXT_ACL).request().header(AUTHORIZATION, jwt)
                    .method(PATCH, entity(publicAcl, APPLICATION_SPARQL_UPDATE))) {
                assertEquals(SUCCESSFUL, res.getStatusInfo().getFamily());
            }

            // POST a protected container
            try (final Response res = target(container).request()
                    .header(LINK, fromUri(LDP.BasicContainer.getIRIString()).rel(TYPE).build())
                    .header(AUTHORIZATION, jwt).post(entity(containerContent, TEXT_TURTLE))) {
                assertEquals(SUCCESSFUL, res.getStatusInfo().getFamily());
                setProtectedContainer(res.getLocation().toString());
            }

            // Add a child to the protected container
            try (final Response res = target(protectedContainer).request().header(AUTHORIZATION, jwt)
                    .post(entity("", TEXT_TURTLE))) {
                assertEquals(SUCCESSFUL, res.getStatusInfo().getFamily());
                setProtectedContainerChild(res.getLocation().toString());
                final String protectedContainerAcl = getLinks(res).stream().filter(link -> link.getRel().equals(acl))
                    .map(link -> link.getUri().toString()).findFirst().orElse("");
                assertEquals(getProtectedContainer() + EXT_ACL, protectedContainerAcl);
            }

            final String protectedAcl = prefixAcl
                + "INSERT DATA { \n"
                + "[acl:accessTo  <" + protectedContainer + ">;  acl:mode acl:Read, acl:Write;"
                + "   acl:agent <https://people.apache.org/~acoburn/#i> ] };"
                + prefixAcl
                + "INSERT DATA { \n"
                + "[acl:accessTo  <" + protectedContainer + ">; acl:mode acl:Read, acl:Append; "
                + "   acl:agent <https://madison.example.com/profile/#me> ] }";

            // Add an ACL for the protected container
            try (final Response res = target(getProtectedContainer() + EXT_ACL).request().header(AUTHORIZATION, jwt)
                    .method(PATCH, entity(protectedAcl, APPLICATION_SPARQL_UPDATE))) {
                assertEquals(SUCCESSFUL, res.getStatusInfo().getFamily());
            }

            // POST a private container
            try (final Response res = target(container).request()
                    .header(LINK, fromUri(LDP.BasicContainer.getIRIString()).rel(TYPE).build())
                    .header(AUTHORIZATION, jwt).post(entity(containerContent, TEXT_TURTLE))) {
                assertEquals(SUCCESSFUL, res.getStatusInfo().getFamily());
                setPrivateContainer(res.getLocation().toString());
            }

            // Add a child to the private container
            try (final Response res = target(privateContainer).request().header(AUTHORIZATION, jwt)
                    .post(entity("", TEXT_TURTLE))) {
                assertEquals(SUCCESSFUL, res.getStatusInfo().getFamily());
                setPrivateContainerChild(res.getLocation().toString());
                final String privateContainerAcl = getLinks(res).stream().filter(link -> link.getRel().equals(acl))
                    .map(link -> link.getUri().toString()).findFirst().orElse("");
                assertEquals(getPrivateContainer() + EXT_ACL, privateContainerAcl);
            }

            final String privateAcl = prefixAcl
                + "INSERT DATA { "
                + "[acl:accessTo  <" + privateContainer + ">; acl:mode acl:Read, acl:Write; "
                + "   acl:agent <http://example.com/administrator> ] }";

            // Add an ACL for the private container
            try (final Response res = target(getPrivateContainer() + EXT_ACL).request().header(AUTHORIZATION, jwt)
                    .method(PATCH, entity(privateAcl, APPLICATION_SPARQL_UPDATE))) {
                assertEquals(SUCCESSFUL, res.getStatusInfo().getFamily());
            }

            final String groupContent
                = "@prefix acl: <http://www.w3.org/ns/auth/acl#>.\n"
                + "@prefix vcard: <http://www.w3.org/2006/vcard/ns#> .\n"
                + "<> a acl:GroupListing.\n"
                + "<#Developers> a vcard:Group;\n"
                + "  vcard:hasUID <urn:uuid:8831CBAD-1111-2222-8563-F0F4787E5398:ABGroup>;\n"
                + "  vcard:hasMember <https://pat.example.com/profile/card#me>;\n"
                + "  vcard:hasMember <https://people.apache.org/~acoburn/#i>.\n"
                + "<#Management> a vcard:Group;\n"
                + "  vcard:hasUID <urn:uuid:8831CBAD-3333-4444-8563-F0F4787E5398:ABGroup>;\n"
                + "  vcard:hasMember <https://madison.example.com/profile/#me>.";

            // POST a group listing
            try (final Response res = target(container).request()
                    .header(AUTHORIZATION, jwt).post(entity(groupContent, TEXT_TURTLE))) {
                assertEquals(SUCCESSFUL, res.getStatusInfo().getFamily());
                groupResource = res.getLocation().toString();
            }

            // POST a group-controlled container
            try (final Response res = target(container).request()
                    .header(LINK, fromUri(LDP.BasicContainer.getIRIString()).rel(TYPE).build())
                    .header(AUTHORIZATION, jwt).post(entity(containerContent, TEXT_TURTLE))) {
                assertEquals(SUCCESSFUL, res.getStatusInfo().getFamily());
                setGroupContainer(res.getLocation().toString());
            }

            // Add a child to the group container
            try (final Response res = target(groupContainer).request().header(AUTHORIZATION, jwt)
                    .post(entity("", TEXT_TURTLE))) {
                assertEquals(SUCCESSFUL, res.getStatusInfo().getFamily());
                setGroupContainerChild(res.getLocation().toString());
                final String groupContainerAcl = getLinks(res).stream().filter(link -> link.getRel().equals(acl))
                    .map(link -> link.getUri().toString()).findFirst().orElse("");
                assertEquals(getGroupContainer() + EXT_ACL, groupContainerAcl);
            }

            final String groupAcl = prefixAcl
                + "INSERT DATA {  "
                + "[acl:accessTo <" + groupContainer + ">; acl:mode acl:Read, acl:Write; "
                + " acl:agentGroup <" + groupResource + "#Developers> ] };\n"
                + prefixAcl
                + "INSERT DATA {  "
                + "[acl:accessTo <" + groupContainer + ">; acl:mode acl:Read; "
                + " acl:agentGroup <" + groupResource + "#Management> ] }";

            // Add an ACL for the private container
            try (final Response res = target(groupContainer + EXT_ACL).request().header(AUTHORIZATION, jwt)
                    .method(PATCH, entity(groupAcl, APPLICATION_SPARQL_UPDATE))) {
                assertEquals(SUCCESSFUL, res.getStatusInfo().getFamily());
            }

            // POST a container with a default ACL
            try (final Response res = target(container).request()
                    .header(LINK, fromUri(LDP.BasicContainer.getIRIString()).rel(TYPE).build())
                    .header(AUTHORIZATION, jwt).post(entity(containerContent, TEXT_TURTLE))) {
                assertEquals(SUCCESSFUL, res.getStatusInfo().getFamily());
                defaultContainer = res.getLocation().toString();
            }

            // Add a child to the public container
            try (final Response res = target(defaultContainer).request().header(AUTHORIZATION, jwt)
                    .post(entity("", TEXT_TURTLE))) {
                assertEquals(SUCCESSFUL, res.getStatusInfo().getFamily());
                defaultContainerChild = res.getLocation().toString();
                final String defaultContainerAcl = getLinks(res).stream().filter(link -> link.getRel().equals(acl))
                    .map(link -> link.getUri().toString()).findFirst().orElse("");
                assertEquals(getDefaultContainer() + EXT_ACL, defaultContainerAcl);
            }

            final String defaultAcl = "PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n"
                + prefixAcl
                + "INSERT DATA {  "
                + "[acl:accessTo <" + defaultContainer + ">; acl:mode acl:Read; acl:agentClass foaf:Agent ] }; \n"
                + prefixAcl
                + "INSERT DATA { [acl:accessTo <" + defaultContainer + ">; acl:mode acl:Read, acl:Write; \n"
                + "   acl:default <" + defaultContainer + ">; \n"
                + "   acl:agent <https://people.apache.org/~acoburn/#i> ] }";

            // Add an ACL for the public container
            try (final Response res = target(getDefaultContainer() + EXT_ACL).request().header(AUTHORIZATION, jwt)
                    .method(PATCH, entity(defaultAcl, APPLICATION_SPARQL_UPDATE))) {
                assertEquals(SUCCESSFUL, res.getStatusInfo().getFamily());
            }
        }
    }
}
