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
package org.trellisldp.http.impl;

import static javax.ws.rs.core.Link.fromUri;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * @author acoburn
 */
public class MementoResourceTest {

    private final String url = "http://example.com/resource/memento";

    @Test
    public void testIsMementoLink() {
        assertTrue(MementoResource.isMementoLink(fromUri(url).rel("memento")
                    .param("datetime", "Fri, 11 May 2018 15:29:25 GMT").build()), "Valid Memento Link header skipped!");
        assertFalse(MementoResource.isMementoLink(fromUri(url).rel("foo")
                    .param("datetime", "Fri, 11 May 2018 15:29:25 GMT").build()), "Invalid Memento header accepted!");
        assertFalse(MementoResource.isMementoLink(fromUri(url).rel("memento")
                    .param("bar", "Fri, 11 May 2018 15:29:25 GMT").build()), "Invalid Memento header accepted!");
    }
}
