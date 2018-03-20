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
package org.trellisldp.event;

import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.slf4j.LoggerFactory.getLogger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.util.Optional;

import org.slf4j.Logger;
import org.trellisldp.api.ActivityStreamService;
import org.trellisldp.api.Event;


/**
 * An {@link ActivityStreamService} that serializes an {@link Event} object
 * into an ActivityStream-compliant JSON string.
 *
 * @author acoburn
 */
public class EventSerializer implements ActivityStreamService {

    private static final Logger LOGGER = getLogger(EventSerializer.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();

    static {
        MAPPER.configure(WRITE_DATES_AS_TIMESTAMPS, false);
        MAPPER.registerModule(new JavaTimeModule());
    }

    @Override
    public Optional<String> serialize(final Event event) {
        try {
            final String message = MAPPER.writeValueAsString(ActivityStreamMessage.from(event));
            LOGGER.debug("Serializing Event {}", message);
            return of(message);
        } catch (final JsonProcessingException ex) {
            LOGGER.debug(ex.getMessage());
            return empty();
        }
    }
}
