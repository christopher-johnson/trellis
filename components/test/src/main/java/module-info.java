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
module org.trellisldp.jpms.test {
    exports org.trellisldp.test;
    requires awaitility;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires java.ws.rs;
    requires java.naming;
    requires javax.jms.api;
    requires jjwt;
    requires org.apache.commons.codec;
    requires org.apache.commons.io;
    requires org.apache.commons.rdf.api;
    requires org.apache.commons.rdf.jena;
    requires org.junit.jupiter.api;
    requires org.trellisldp.jpms.api;
    requires org.trellisldp.jpms.http;
    requires org.trellisldp.jpms.io;
    requires org.trellisldp.jpms.vocabulary;
    requires slf4j.api;
}