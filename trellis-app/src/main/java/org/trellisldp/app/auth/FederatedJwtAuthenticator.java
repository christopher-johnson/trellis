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

import static java.util.Objects.isNull;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.SigningKeyResolverAdapter;

import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.util.List;

/**
 * A Federated JWT-based authenticator.
 */
public class FederatedJwtAuthenticator extends AbstractJwtAuthenticator {

    private final KeyStore keyStore;
    private final List<String> keyIds;

    /**
     * Create a Federated JWT-based authenticator.
     * @param keyStore a keystore
     * @param keyIds a list of keyIds to use
     */
    public FederatedJwtAuthenticator(final KeyStore keyStore, final List<String> keyIds) {
        this.keyStore = keyStore;
        this.keyIds = keyIds;
    }

    @Override
    protected Claims parse(final String credentials) {
        // Parse the JWT claims
        return Jwts.parser().setSigningKeyResolver(new SigningKeyResolverAdapter() {
            @Override
            public Key resolveSigningKey(final JwsHeader header, final Claims claims) {
                if (isNull(header.getKeyId())) {
                    throw new JwtException("Missing Key ID (kid) header field");
                }
                try {
                    if (keyIds.contains(header.getKeyId()) && keyStore.containsAlias(header.getKeyId())) {
                        return keyStore.getCertificate(header.getKeyId()).getPublicKey();
                    }
                } catch (final KeyStoreException ex) {
                    throw new SignatureException("Error retrieving key from keystore", ex);
                }
                throw new SignatureException("Could not locate key in keystore: " + header.getKeyId());
            }
        }).parseClaimsJws(credentials).getBody();
    }
}
