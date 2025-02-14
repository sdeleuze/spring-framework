/*
 * Copyright 2002-2025 the original author or authors.
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

package org.springframework.context.annotation;

import org.springframework.context.FunctionalInitializer;
import org.springframework.context.RegistrableApplicationContext;

public class FunctionalConfigurationTests {

	record Foo() {}
	record Bar(Foo foo) {}
	record Baz(String message) {}
	record Boo(String message) {}

	@Configuration
	class FunctionalConfiguration implements FunctionalInitializer {

		@Bean
		Baz baz() {
			return new Baz("Hello World!");
		}

		@Override
		public void initialize(RegistrableApplicationContext context) {
			context.registerBean(Foo.class);
			context.registerBean(Bar.class, () -> new Bar(context.getBean(Foo.class)));
			if (context.getEnvironment().matchesProfiles("myProfile")) {
				context.registerBean("customName", Baz.class, this::baz);
			}
		}
	}
}
