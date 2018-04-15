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
module org.trellisldp.event {
    exports org.trellisldp.event;
    opens org.trellisldp.event to com.fasterxml.jackson.databind;
    provides org.trellisldp.api.ActivityStreamService with org.trellisldp.event.EventSerializer;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.datatype.jsr310;
    requires org.apache.commons.rdf.api;
    requires org.trellisldp.api;
    requires org.trellisldp.vocabulary;
    requires slf4j.api;
    requires com.fasterxml.jackson.annotation;
}