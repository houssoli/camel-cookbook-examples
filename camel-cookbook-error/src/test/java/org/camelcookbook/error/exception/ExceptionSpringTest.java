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

package org.camelcookbook.error.exception;

import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ExceptionSpringTest extends CamelSpringTestSupport {
    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("META-INF/spring/exception-context.xml");
    }

    @Test
    public void testException() throws Exception {
        final MockEndpoint mockResult = getMockEndpoint("mock:result");
        mockResult.expectedMessageCount(1);

        final MockEndpoint mockError = getMockEndpoint("mock:error");
        mockError.expectedMessageCount(1);

        try {
            template.sendBody("direct:start", "Foo");
        } catch (Throwable e) {
            fail("Shouldn't get here");
        }

        mockResult.assertIsSatisfied();

        boolean threwException = false;
        try {
            template.sendBody("direct:start", "KaBoom");
        } catch (Throwable e) {
            threwException = true;
        }
        assertTrue(threwException);

        assertMockEndpointsSatisfied();
    }

    @Test
    public void testExceptionHandled() throws Exception {
        final MockEndpoint mockResult = getMockEndpoint("mock:result");
        mockResult.expectedBodiesReceived("All Good");

        final MockEndpoint mockError = getMockEndpoint("mock:error");
        mockError.expectedBodiesReceived("Something Bad Happened!");

        String response;

        try {
            response = template.requestBody("direct:handled", "Foo", String.class);
            assertEquals("All Good", response);
        } catch (Throwable e) {
            fail("Shouldn't get here");
        }

        mockResult.assertIsSatisfied();

        try {
            response = template.requestBody("direct:handled", "KaBoom", String.class);
            assertEquals("Something Bad Happened!", response);
        } catch (Throwable e) {
            fail("Shouldn't get here either");
        }

        assertMockEndpointsSatisfied();
    }

    @Test
    public void testExceptionContinue() throws Exception {
        final MockEndpoint mockResult = getMockEndpoint("mock:result");
        mockResult.expectedBodiesReceived("All Good", "All Good");

        final MockEndpoint mockError = getMockEndpoint("mock:ignore");
        mockError.expectedMessageCount(1);

        String response;

        try {
            response = template.requestBody("direct:continue", "Foo", String.class);
            assertEquals("All Good", response);
        } catch (Throwable e) {
            fail("Shouldn't get here");
        }

        try {
            response = template.requestBody("direct:continue", "KaBoom", String.class);
            assertEquals("All Good", response);
        } catch (Throwable e) {
            fail("Shouldn't get here either");
        }

        assertMockEndpointsSatisfied();
    }
}
