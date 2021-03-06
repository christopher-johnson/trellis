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
module org.trellisldp.jpms.rdfa {
    exports org.trellisldp.rdfa;
    requires org.apache.commons.rdf.jena;
    requires tamaya.api;
    requires compiler;
    requires javax.inject;
    requires org.apache.commons.rdf.api;
    requires org.trellisldp.jpms.api;
    requires org.trellisldp.jpms.vocabulary;
    opens org.trellisldp.rdfa to compiler;
}
