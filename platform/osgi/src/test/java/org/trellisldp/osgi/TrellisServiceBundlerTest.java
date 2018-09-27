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
package org.trellisldp.osgi;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.trellisldp.api.AgentService;
import org.trellisldp.api.BinaryService;
import org.trellisldp.api.EventService;
import org.trellisldp.api.IOService;
import org.trellisldp.api.MementoService;
import org.trellisldp.api.ServiceBundler;
import org.trellisldp.triplestore.TriplestoreResourceService;

@RunWith(MockitoJUnitRunner.class)
public class TrellisServiceBundlerTest {
    @Mock
    private AgentService agentService;

    @Mock
    private EventService eventService;

    @Mock
    private IOService ioService;

    @Mock
    private TriplestoreResourceService service;

    @Mock
    private BinaryService binaryService;

    @Mock
    private MementoService mementoService;

    @Test
    public void testOSGiClass() {
        final ServiceBundler bundler = new TrellisServiceBundler(mementoService, binaryService,
                ioService, agentService, eventService, service);
        assertEquals(ioService, bundler.getIOService());
        assertEquals(service, bundler.getAuditService());
        assertEquals(service, bundler.getResourceService());
    }
}
