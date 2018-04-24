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

import org.apache.commons.rdf.api.IRI;

/**
 * This service provides a mechanism for converting an agent {@link String}
 * into an {@link IRI}.
 *
 * <p>Depending on the implementation used, this conversion may involve a mapping,
 * a prefixing or a simple conversion of a string-based IRI into an {@link IRI} object.
 *
 * @author acoburn
 */
public interface AgentService {

    /**
     * Convert an agent {@link String} into an {@link IRI}.
     *
     * <p>Implementations of this interface should gracefully handle {@code null} and
     * empty values, possibly mapping them to an agent such as {@code trellis:AnonymousAgent}.
     *
     * @param agent the agent as a string
     * @return the agent as an IRI
     */
    IRI asAgent(String agent);
}
