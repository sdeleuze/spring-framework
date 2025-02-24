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

package org.springframework.context.testfixture.context;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.BeanRegistrar;
import org.springframework.beans.factory.BeanRegistry;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.env.Environment;

public class SampleBeanRegistrar implements BeanRegistrar {

	@Override
	public void register(BeanRegistry registry, Environment env) {
		registry.registerBean("foo", Foo.class);
		registry.registerBean("bar", Bar.class,
				bean -> bean.prototype()
							.lazyInitialized()
							.description("Custom description")
							.supplier(context -> new Bar(context.getBean(Foo.class))));
		registry.registerBean(Init.class);
		registry.registerBean(Init2.class);
		if (env.matchesProfiles("baz")) {
			registry.registerBean(Baz.class, customizer -> customizer.supplier(
					context -> new Baz("Hello World!")));
		}
	}

	public record Foo() {}
	public record Bar(Foo foo) {}
	public record Baz(String message) {}

	public static class Init implements InitializingBean {

		public boolean initialized = false;

		@Override
		public void afterPropertiesSet() throws Exception {
			initialized = true;
		}
	}

	public static class Init2 {

		public boolean initialized = false;

		@PostConstruct
		public void after() {
			initialized = true;
		}
	}
}
