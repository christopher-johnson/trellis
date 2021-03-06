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
package org.trellisldp.app.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;

import java.security.Principal;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.trellisldp.vocabulary.Trellis;

/**
 * @author acoburn
 */
public class AnonymousAuthenticatorTest {

    @Test
    public void testAuthenticate() throws AuthenticationException {
        final Authenticator<String, Principal> authenticator = new AnonymousAuthenticator();

        final Optional<Principal> res = authenticator.authenticate("blahblah");
        assertTrue(res.isPresent(), "Missing principal!");
        res.ifPresent(p -> assertEquals(Trellis.AnonymousAgent.getIRIString(), p.getName(), "Incorrect agent name!"));
    }
}
