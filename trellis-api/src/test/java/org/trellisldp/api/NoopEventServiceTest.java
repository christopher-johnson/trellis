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

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

/**
 * Test the no-op event service.
 */
public class NoopEventServiceTest {

    @Mock
    private Event mockEvent;

    @BeforeEach
    public void setUp() {
        initMocks(this);
    }

    @Test
    final void testNoopEventSvc() {
        final EventService svc = new NoopEventService();
        svc.emit(mockEvent);
        // no exception above, so test passes if we make it this far.
        assertTrue(true);
    }
}
