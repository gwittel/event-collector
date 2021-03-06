/*
 * Copyright 2011-2014 Proofpoint, Inc.
 *
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
package com.proofpoint.event.collector;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.proofpoint.event.collector.validation.ValidUri;
import org.testng.annotations.Test;

import javax.validation.constraints.Size;
import java.util.Map;

import static com.proofpoint.configuration.testing.ConfigAssertions.assertFullMapping;
import static com.proofpoint.configuration.testing.ConfigAssertions.assertLegacyEquivalence;
import static com.proofpoint.configuration.testing.ConfigAssertions.assertRecordedDefaults;
import static com.proofpoint.configuration.testing.ConfigAssertions.recordDefaults;
import static com.proofpoint.event.collector.QosDelivery.BEST_EFFORT;
import static com.proofpoint.event.collector.QosDelivery.RETRY;
import static com.proofpoint.testing.ValidationAssertions.assertFailsValidation;
import static com.proofpoint.testing.ValidationAssertions.assertValidates;
import static org.testng.Assert.assertEquals;

public class TestPerFlowStaticEventTapConfig
{
    private static final String URIS = "http://1.2.3.40  ,  https://1.2.3.41:8333";

    @Test
    public void testDefaults()
    {
        assertRecordedDefaults(
                recordDefaults(PerFlowStaticEventTapConfig.class)
                        .setQosDelivery(BEST_EFFORT)
                        .setUris("")
        );
        assertEquals(new PerFlowStaticEventTapConfig().getUris(), ImmutableSet.<String>of());
    }

    @Test
    public void testExplicitPropertyMappings()
    {
        Map<String, String> properties = ImmutableMap.of(
                "qos-delivery", "RETRY",
                "uris", "http://1.2.3.40  ,  https://1.2.3.41:8333"
        );

        PerFlowStaticEventTapConfig expected = new PerFlowStaticEventTapConfig()
                .setQosDelivery(RETRY)
                .setUris(ImmutableSet.of("http://1.2.3.40", "https://1.2.3.41:8333"));

        assertFullMapping(properties, expected);
    }

    @Test
    public void testLegacyProperties()
    {
        Map<String, String> properties = ImmutableMap.of(
                "uris", URIS
        );

        assertLegacyEquivalence(PerFlowStaticEventTapConfig.class, properties);
    }

    @Test
    public void testInvalidUriStringFailsValidation()
    {
        assertFailsValidation(new PerFlowStaticEventTapConfig().setUris("http://1.2.3.4/path|dummy"), "uris", "Invalid URIs: Invalid syntax: http://1.2.3.4/path|dummy", ValidUri.class);
    }

    @Test
    public void testEmptyURIsFailsValidation()
    {
        PerFlowStaticEventTapConfig config = new PerFlowStaticEventTapConfig();
        assertFailsValidation(config, "uris", "may not be empty", Size.class);
    }

    @Test
    public void testValidation()
    {
        PerFlowStaticEventTapConfig config = new PerFlowStaticEventTapConfig().setUris(URIS).setQosDelivery(RETRY);
        assertValidates(config);
    }
}
