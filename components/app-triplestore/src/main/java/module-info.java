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
module org.trellisldp.jpms.app.triplestore {
    exports org.trellisldp.app.triplestore;
    requires com.fasterxml.jackson.annotation;
    requires com.google.common;
    requires dropwizard.core;
    requires dropwizard.lifecycle;
    requires java.ws.rs;
    requires javax.inject;
    requires kafka.clients;
    requires metrics.healthchecks;
    requires org.apache.commons.lang3;
    requires org.apache.commons.rdf.api;
    requires org.apache.commons.rdf.jena;
    requires org.apache.jena.arq;
    requires org.apache.jena.core;
    requires org.apache.jena.dboe;
    requires org.apache.jena.rdfconnection;
    requires org.trellisldp.jpms.agent;
    requires org.trellisldp.jpms.api;
    requires org.trellisldp.jpms.app;
    requires org.trellisldp.jpms.audit;
    requires org.trellisldp.jpms.file;
    requires org.trellisldp.jpms.id;
    requires org.trellisldp.jpms.io;
    requires org.trellisldp.jpms.kafka;
    requires org.trellisldp.jpms.namespaces;
    requires org.trellisldp.jpms.rdfa;
    requires org.trellisldp.jpms.triplestore;
    requires org.trellisldp.jpms.vocabulary;
    requires slf4j.api;
    requires validation.api;
}