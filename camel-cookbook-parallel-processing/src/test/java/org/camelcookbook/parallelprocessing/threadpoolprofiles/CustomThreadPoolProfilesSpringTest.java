/*
 * Copyright (C) Scott Cranton and Jakub Korab
 * https://github.com/CamelCookbook
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.camelcookbook.parallelprocessing.threadpoolprofiles;

import org.apache.camel.Exchange;
import org.apache.camel.Navigate;
import org.apache.camel.Processor;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.processor.ThreadsProcessor;
import org.apache.camel.support.SynchronizationAdapter;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;

/**
 * Test class that exercises a custom thread pool created from a profile using the threads DSL.
 * @author jkorab
 */
public class CustomThreadPoolProfilesSpringTest extends CamelSpringTestSupport {

    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("/META-INF/spring/threadPoolProfiles-context.xml");
    }

    @Test
    public void testProcessedByCustomThreadPoolProfile() throws InterruptedException {
        final int messageCount = 50;
        MockEndpoint mockOut = getMockEndpoint("mock:out");
        mockOut.setExpectedMessageCount(messageCount);
        mockOut.setResultWaitTime(6000);

        for (int i = 0; i < messageCount; i++) {
            template.asyncSendBody("direct:in", "Message[" + i + "]");
        }

        assertMockEndpointsSatisfied();
        // no way to check programatically whether the profile was actually engaged, as Camel uses the
        // default naming strategy for threads
    }

}
