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
package org.trellisldp.app;

import io.dropwizard.setup.Environment;

import org.trellisldp.api.ServiceBundler;
import org.trellisldp.app.config.TrellisConfiguration;

/**
 * A simple test app.
 */
public class SimpleTrellisApp extends AbstractTrellisApplication<TrellisConfiguration> {

    private ServiceBundler serviceBundler;

    @Override
    protected ServiceBundler getServiceBundler() {
        return serviceBundler;
    }

    @Override
    protected void initialize(final TrellisConfiguration config, final Environment env) {
        super.initialize(config, env);
        this.serviceBundler = new SimpleServiceBundler();
    }
}
