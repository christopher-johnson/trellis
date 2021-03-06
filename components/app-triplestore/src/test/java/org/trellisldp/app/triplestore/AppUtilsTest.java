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

package org.trellisldp.app.triplestore;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import io.dropwizard.configuration.YamlConfigurationFactory;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.jersey.validation.Validators;
import io.dropwizard.lifecycle.setup.LifecycleEnvironment;
import io.dropwizard.setup.Environment;

import java.io.File;
import java.util.Properties;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.trellisldp.api.EventService;
import org.trellisldp.api.NoopEventService;
import org.trellisldp.api.RuntimeTrellisException;
import org.trellisldp.app.config.NotificationsConfiguration;
import org.trellisldp.kafka.KafkaPublisher;

/**
 * @author acoburn
 */
public class AppUtilsTest {

    @Mock
    private Environment mockEnv;

    @Mock
    private LifecycleEnvironment mockLifecycle;

    @BeforeEach
    public void setUp() {
        initMocks(this);
        when(mockEnv.lifecycle()).thenReturn(mockLifecycle);
    }

    @Test
    public void testGetRDFConnection() throws Exception {
        final AppConfiguration config = new YamlConfigurationFactory<>(AppConfiguration.class,
                Validators.newValidator(), Jackson.newMinimalObjectMapper(), "")
            .build(new File(getClass().getResource("/config1.yml").toURI()));

        assertNotNull(AppUtils.getRDFConnection(config), "Missing RDFConnection, using in-memory dataset!");
        assertFalse(AppUtils.getRDFConnection(config).isClosed(), "RDFConnection has been closed!");

        config.setResources("http://localhost/sparql");

        assertNotNull(AppUtils.getRDFConnection(config), "Missing RDFConnection, using local HTTP!");
        assertFalse(AppUtils.getRDFConnection(config).isClosed(), "RDFConnection has been closed!");

        config.setResources("https://localhost/sparql");
        assertNotNull(AppUtils.getRDFConnection(config), "Missing RDFConnection, using local HTTPS!");
        assertFalse(AppUtils.getRDFConnection(config).isClosed(), "RDFConnection has been closed!");

        final File dir = new File(new File(getClass().getResource("/data").toURI()), "resources");
        config.setResources(dir.getAbsolutePath());
        assertNotNull(AppUtils.getRDFConnection(config), "Missing RDFConnection, using local file!");
        assertFalse(AppUtils.getRDFConnection(config).isClosed(), "RDFConnection has been closed!");
    }

    @Test
    public void testEventServiceNone() throws Exception {
        final NotificationsConfiguration c = new NotificationsConfiguration();
        c.setConnectionString("localhost");
        c.setEnabled(true);
        c.setType(NotificationsConfiguration.Type.NONE);
        final EventService svc = AppUtils.getNotificationService(c, mockEnv);
        assertNotNull(svc, "Missing EventService!");
        assertTrue(svc instanceof NoopEventService, "EventService isn't a NoopEvenService!");
    }

    @Test
    public void testEventServiceDisabled() throws Exception {
        final NotificationsConfiguration c = new NotificationsConfiguration();
        c.set("batch.size", "1000");
        c.set("retries", "10");
        c.set("key.serializer", "some.bogus.key.serializer");
        c.setConnectionString("localhost:9092");
        c.setEnabled(false);
        c.setType(NotificationsConfiguration.Type.KAFKA);
        final EventService svc = AppUtils.getNotificationService(c, mockEnv);
        assertNotNull(svc, "Missing EventService!");
        assertTrue(svc instanceof NoopEventService, "EventService didn't default to No-op service!");
    }

    @Test
    public void testEventServiceKafka() throws Exception {
        final NotificationsConfiguration c = new NotificationsConfiguration();
        c.set("batch.size", "1000");
        c.set("retries", "10");
        c.set("key.serializer", "some.bogus.key.serializer");
        c.setConnectionString("localhost:9092");
        c.setEnabled(true);
        c.setType(NotificationsConfiguration.Type.KAFKA);
        final EventService svc = AppUtils.getNotificationService(c, mockEnv);
        assertNotNull(svc, "Missing EventService!");
        assertTrue(svc instanceof KafkaPublisher, "EventService isn't a KafkaPublisher!");
    }

    @Test
    public void testGetKafkaProps() {
        final NotificationsConfiguration c = new NotificationsConfiguration();
        c.set("batch.size", "1000");
        c.set("retries", "10");
        c.set("some.other", "value");
        c.set("key.serializer", "some.bogus.key.serializer");
        c.setConnectionString("localhost:9092");
        final Properties p = AppUtils.getKafkaProperties(c);
        assertEquals("all", p.getProperty("acks"), "Incorrect kafka acks property!");
        assertEquals("value", p.getProperty("some.other"), "Incorrect custom property!");
        assertEquals("org.apache.kafka.common.serialization.StringSerializer", p.getProperty("key.serializer"),
                "Incorrect serializer class property!");
        assertEquals("localhost:9092", p.getProperty("bootstrap.servers"), "Incorrect bootstrap.servers property!");
    }

    @Disabled
    @Test
    public void testEventServiceJms() throws Exception {
        final NotificationsConfiguration c = new NotificationsConfiguration();
        c.setConnectionString("tcp://localhost:61616");
        c.setEnabled(true);
        c.setType(NotificationsConfiguration.Type.JMS);
        assertThrows(RuntimeTrellisException.class, () ->
                AppUtils.getNotificationService(c, mockEnv), "No exception when JMS client doesn't connect!");
    }
}
