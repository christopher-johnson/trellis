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
package org.trellisldp.http.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

/**
 * @author acoburn
 */
public class VersionTest {

    @Test
    public void testVersion() {
        final Version v = Version.valueOf("1493646202676");
        assertEquals("2017-05-01T13:43:22.676Z", v.getInstant().toString(), "Check datetime string");
        assertEquals("2017-05-01T13:43:22.676Z", v.toString(), "Check stringified version");
    }

    @Test
    public void testInvalidVersion() {
        assertNull(Version.valueOf("blah"), "Check parsing an invalid version");
    }

    @Test
    public void testBadValue() {
        assertNull(Version.valueOf("-13.12"), "Check parsing an invalid date");
    }

    @Test
    public void testNullValue() {
        assertNull(Version.valueOf(null), "Check parsing a null value");
    }
}
