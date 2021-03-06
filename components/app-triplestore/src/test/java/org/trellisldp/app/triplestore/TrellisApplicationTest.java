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

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.dropwizard.Application;

import org.junit.jupiter.api.Test;

/**
 * LDP-related tests for Trellis.
 */
public class TrellisApplicationTest {

    @Test
    public void testGetName() {
        final Application<AppConfiguration> app = new TrellisApplication();
        assertEquals("Trellis LDP", app.getName(), "Incorrect application name!");
    }
}
