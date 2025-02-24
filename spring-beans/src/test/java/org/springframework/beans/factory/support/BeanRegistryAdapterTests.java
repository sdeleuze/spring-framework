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

package org.springframework.beans.factory.support;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.BeanRegistrar;
import org.springframework.beans.factory.BeanRegistry;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.core.env.Environment;
import org.springframework.core.env.StandardEnvironment;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link BeanRegistryAdapter}.
 *
 * @author Sebastien Deleuze
 */
public class BeanRegistryAdapterTests {

	private final DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();

	private final Environment env = new StandardEnvironment();

	@Test
	void defaultScope() {
		BeanRegistryAdapter adapter = new BeanRegistryAdapter(this.beanFactory, this.beanFactory, DefaultBeanRegistrar.class);
		new DefaultBeanRegistrar().register(adapter, env);
		BeanDefinition beanDefinition = this.beanFactory.getBeanDefinition("foo");
		assertThat(beanDefinition.getScope()).isEqualTo(AbstractBeanDefinition.SCOPE_DEFAULT);
	}

	@Test
	void prototypeScope() {
		BeanRegistryAdapter adapter = new BeanRegistryAdapter(this.beanFactory, this.beanFactory, PrototypeScopeBeanRegistrar.class);
		new PrototypeScopeBeanRegistrar().register(adapter, env);
		BeanDefinition beanDefinition = this.beanFactory.getBeanDefinition("foo");
		assertThat(beanDefinition.getScope()).isEqualTo(AbstractBeanDefinition.SCOPE_PROTOTYPE);
	}

	// TODO Implement other tests

	private static class DefaultBeanRegistrar implements BeanRegistrar {

		@Override
		public void register(BeanRegistry registry, Environment env) {
			registry.registerBean("foo", Foo.class);
		}
	}

	private static class PrototypeScopeBeanRegistrar implements BeanRegistrar {

		@Override
		public void register(BeanRegistry registry, Environment env) {
			registry.registerBean("foo", Foo.class, BeanRegistry.BeanSpec::prototype);
		}
	}

	private static class Foo {}

}
