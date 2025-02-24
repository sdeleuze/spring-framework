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

package org.springframework.context.registrar;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.BeanRegistrar;
import org.springframework.context.BeanRegistry;
import org.springframework.core.env.Environment;

import static org.springframework.context.BeanRegistry.Scope;

public class MyBeanRegistrar implements BeanRegistrar {

	@Override
	public void register(BeanRegistry registry, Environment env) {
		registry.registerBean(Foo.class);
		registry.registerBean(Bar.class,
				customizer -> {
					customizer.setLazyInit(true);
					customizer.setScope(Scope.PROTOTYPE);
					customizer.setDescription("Custom description");
				},
				context -> new Bar(context.getBean(Foo.class)));
		registry.registerBean(Init.class);
		if (env.matchesProfiles("baz")) {
			registry.registerBean(Baz.class, context -> new Baz("Hello World!"));
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
}
