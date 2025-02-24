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

import java.lang.reflect.Constructor;
import java.util.function.Consumer;
import java.util.function.Function;

import org.jspecify.annotations.Nullable;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanRegistrar;
import org.springframework.beans.factory.BeanRegistry;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.core.ResolvableType;

/**
 * {@link BeanRegistry} implementation that delegates to
 * {@link BeanDefinitionRegistry} and {@link ListableBeanFactory}.
 *
 * @author Sebastien Deleuze
 * @since 7.0
 */
public class BeanRegistryAdapter implements BeanRegistry {

	private final BeanDefinitionRegistry beanRegistry;

	private final ListableBeanFactory beanFactory;

	private final Class<? extends BeanRegistrar> beanRegistrarClass;

	public BeanRegistryAdapter(BeanDefinitionRegistry beanRegistry, ListableBeanFactory beanFactory,
			Class<? extends BeanRegistrar> beanRegistrarClass) {

		this.beanRegistry = beanRegistry;
		this.beanFactory = beanFactory;
		this.beanRegistrarClass = beanRegistrarClass;
	}

	@Override
	public <T> String registerBean(Class<T> beanClass) {
		String beanName = BeanDefinitionReaderUtils.uniqueBeanName(beanClass.getName(), this.beanRegistry);
		registerBean(beanName, beanClass);
		return beanName;
	}

	@Override
	public <T> String registerBean(Class<T> beanClass, Consumer<BeanSpec<T>> customizer) {
		BeanRegistrarBeanDefinition beanDefinition = new BeanRegistrarBeanDefinition(beanClass, this.beanRegistrarClass);
		String beanName = BeanDefinitionReaderUtils.uniqueBeanName(beanClass.getName(), this.beanRegistry);
		customizer.accept(new BeanSpecAdapter<>(beanDefinition, this.beanFactory));
		this.beanRegistry.registerBeanDefinition(beanName, beanDefinition);
		return beanName;
	}

	@Override
	public <T> void registerBean(String beanName, Class<T> beanClass) {
		BeanRegistrarBeanDefinition beanDefinition = new BeanRegistrarBeanDefinition(beanClass, this.beanRegistrarClass);
		this.beanRegistry.registerBeanDefinition(beanName, beanDefinition);
	}

	@Override
	public <T> void registerBean(String beanName, Class<T> beanClass, Consumer<BeanSpec<T>> customizer) {
		BeanRegistrarBeanDefinition beanDefinition = new BeanRegistrarBeanDefinition(beanClass, this.beanRegistrarClass);
		customizer.accept(new BeanSpecAdapter<>(beanDefinition, this.beanFactory));
		this.beanRegistry.registerBeanDefinition(beanName, beanDefinition);
	}


	/**
	 * {@link RootBeanDefinition} subclass for {@code #registerBean} based
	 * registrations with flexible autowiring for public constructors inspired
	 * from {@code GenericApplicationContext.ClassDerivedBeanDefinition}. It
	 * also sets the bean registrar class as a source.
	 */
	@SuppressWarnings("serial")
	private static class BeanRegistrarBeanDefinition extends RootBeanDefinition {

		public BeanRegistrarBeanDefinition(Class<?> beanClass, Class<? extends BeanRegistrar> beanRegistrarClass) {
			super(beanClass);
			this.setSource(beanRegistrarClass);
			this.setAttribute("aotProcessingIgnoreRegistration", true);
		}

		public BeanRegistrarBeanDefinition(BeanRegistrarBeanDefinition original) {
			super(original);
		}

		@Override
		public Constructor<?> @Nullable [] getPreferredConstructors() {
			Constructor<?>[] fromAttribute = super.getPreferredConstructors();
			if (fromAttribute != null) {
				return fromAttribute;
			}
			Class<?> clazz = getBeanClass();
			Constructor<?> primaryCtor = BeanUtils.findPrimaryConstructor(clazz);
			if (primaryCtor != null) {
				return new Constructor<?>[] {primaryCtor};
			}
			Constructor<?>[] publicCtors = clazz.getConstructors();
			if (publicCtors.length > 0) {
				return publicCtors;
			}
			return null;
		}

		@Override
		public RootBeanDefinition cloneBeanDefinition() {
			return new BeanRegistrarBeanDefinition(this);
		}
	}

	static class BeanSpecAdapter<T> implements BeanSpec<T> {

		private final RootBeanDefinition beanDefinition;

		private final ListableBeanFactory beanFactory;

		public BeanSpecAdapter(RootBeanDefinition beanDefinition, ListableBeanFactory beanFactory) {
			this.beanDefinition = beanDefinition;
			this.beanFactory = beanFactory;
		}

		@Override
		public BeanSpec<T> fallback() {
			this.beanDefinition.setFallback(true);
			return this;
		}

		@Override
		public BeanSpec<T> primary() {
			this.beanDefinition.setPrimary(true);
			return this;
		}

		@Override
		public BeanSpec<T> description(String description) {
			this.beanDefinition.setDescription(description);
			return this;
		}

		@Override
		public BeanSpec<T> infrastructure() {
			this.beanDefinition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
			return this;
		}

		@Override
		public BeanSpec<T> lazyInitialized() {
			this.beanDefinition.setLazyInit(true);
			return this;
		}

		@Override
		public BeanSpec<T> order(int order) {
			this.beanDefinition.setAttribute(AbstractBeanDefinition.ORDER_ATTRIBUTE, order);
			return this;
		}

		@Override
		public BeanSpec<T> prototype() {
			this.beanDefinition.setScope(BeanDefinition.SCOPE_PROTOTYPE);
			return this;
		}

		@Override
		public BeanSpec<T> notAutowireCandidate() {
			this.beanDefinition.setAutowireCandidate(false);
			return this;
		}

		@Override
		public BeanSpec<T> supplier(Function<SupplierContext, T> supplier) {
			this.beanDefinition.setInstanceSupplier(() ->
					supplier.apply(new SupplierContextAdapter(this.beanFactory)));
			return this;
		}
	}

	static class SupplierContextAdapter implements SupplierContext {

		private final ListableBeanFactory beanFactory;

		public SupplierContextAdapter(ListableBeanFactory beanFactory) {
			this.beanFactory = beanFactory;
		}

		@Override
		public <T> T getBean(Class<T> requiredType) throws BeansException {
			return this.beanFactory.getBean(requiredType);
		}

		@Override
		public <T> T getBean(String name, Class<T> requiredType) throws BeansException {
			return this.beanFactory.getBean(name, requiredType);
		}

		@Override
		public <T> ObjectProvider<T> getBeanProvider(Class<T> requiredType) {
			return this.beanFactory.getBeanProvider(requiredType);
		}

		@Override
		public <T> ObjectProvider<T> getBeanProvider(ResolvableType requiredType) {
			return this.beanFactory.getBeanProvider(requiredType);
		}
	}
}
