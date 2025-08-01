/*
 * Copyright 2002-present the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.test.context.bean.override.mockito.integration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.bean.override.example.ExampleGenericServiceCaller;
import org.springframework.test.context.bean.override.example.IntegerExampleGenericService;
import org.springframework.test.context.bean.override.example.StringExampleGenericService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.mockito.MockitoAssertions.assertIsMock;
import static org.springframework.test.mockito.MockitoAssertions.assertMockName;

/**
 * Tests that {@link MockitoBean @MockitoBean} can be used to mock a bean when
 * there are multiple candidates and one is primary.
 *
 * @author Sam Brannen
 * @author Phillip Webb
 * @since 6.2
 * @see MockitoBeanWithMultipleExistingBeansAndOnePrimaryAndOneConflictingQualifierIntegrationTests
 * @see MockitoBeanWithMultipleExistingBeansAndExplicitBeanNameIntegrationTests
 * @see MockitoBeanWithMultipleExistingBeansAndExplicitQualifierIntegrationTests
 */
@ExtendWith(SpringExtension.class)
class MockitoBeanWithMultipleExistingBeansAndOnePrimaryIntegrationTests {

	@MockitoBean
	StringExampleGenericService mock;

	@Autowired
	ExampleGenericServiceCaller caller;


	@Test
	void test() {
		assertIsMock(mock);
		assertMockName(mock, "two");

		given(mock.greeting()).willReturn("mocked");
		assertThat(caller.sayGreeting()).isEqualTo("I say mocked 123");
		then(mock).should().greeting();
	}


	@Configuration(proxyBeanMethods = false)
	@Import({ ExampleGenericServiceCaller.class, IntegerExampleGenericService.class })
	static class Config {

		@Bean
		StringExampleGenericService one() {
			return new StringExampleGenericService("one");
		}

		@Bean
		@Primary
		StringExampleGenericService two() {
			return new StringExampleGenericService("two");
		}
	}

}
