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
package org.trellisldp.http;

import static java.util.Arrays.asList;
import static org.mockito.MockitoAnnotations.initMocks;

import javax.ws.rs.core.Application;

import org.glassfish.jersey.server.ResourceConfig;

/**
 * @author acoburn
 */
public class LdpAdminResourceTest extends AbstractLdpResourceTest {

    @Override
    protected String getBaseUrl() {
        return getBaseUri().toString();
    }

    @Override
    public Application configure() {

        initMocks(this);

        final String baseUri = getBaseUri().toString();
        final String origin = baseUri.substring(0, baseUri.length() - 1);

        final AgentAuthorizationFilter agentFilter = new AgentAuthorizationFilter(mockAgentService);
        agentFilter.setAdminUsers(asList("testUser"));

        final ResourceConfig config = new ResourceConfig();
        config.register(new TrellisHttpResource(mockBundler, baseUri));
        config.register(new TestAuthenticationFilter("testUser", ""));
        config.register(new WebAcFilter(mockAccessControlService));
        config.register(agentFilter);
        config.register(new CacheControlFilter(86400, true, false));
        config.register(new WebSubHeaderFilter(HUB));
        config.register(new CrossOriginResourceSharingFilter(asList(origin),
                    asList("PATCH", "POST", "PUT"),
                    asList("Link", "Content-Type", "Accept", "Accept-Datetime"),
                    asList("Link", "Content-Type", "Pragma", "Memento-Datetime"), true, 100));
        return config;
    }
}
