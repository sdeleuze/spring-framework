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


package org.springframework.context.support;

import java.lang.reflect.Constructor;
import java.util.function.Consumer;
import java.util.function.Function;

import org.jspecify.annotations.Nullable;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.BeanRegistry;
import org.springframework.core.ResolvableType;

public class BeanRegistryAdapter implements BeanRegistry {

	private final BeanDefinitionRegistry beanRegistry;

	private final ListableBeanFactory beanFactory;

	public BeanRegistryAdapter(BeanDefinitionRegistry beanRegistry, ListableBeanFactory beanFactory) {
		this.beanRegistry = beanRegistry;
		this.beanFactory = beanFactory;
	}


	@Override
	public <T> void registerBean(Class<T> beanClass) {
		String beanName = BeanDefinitionReaderUtils.uniqueBeanName(beanClass.getName(), this.beanRegistry);
		registerBean(beanName, beanClass);
	}

	@Override
	public <T> void registerBean(@Nullable String beanName, Class<T> beanClass) {
		ClassDerivedBeanDefinition beanDefinition = new ClassDerivedBeanDefinition(beanClass);
		String nameToUse = (beanName != null ? beanName : beanClass.getName());
		this.beanRegistry.registerBeanDefinition(nameToUse, beanDefinition);
	}

	@Override
	public <T> void registerBean(Class<T> beanClass, Function<InstanceSupplierContext, T> provider) {
		String beanName = BeanDefinitionReaderUtils.uniqueBeanName(beanClass.getName(), this.beanRegistry);
		registerBean(beanName, beanClass, provider);
	}

	@Override
	public <T> void registerBean(Class<T> beanClass, Consumer<Bean> customizer, Function<InstanceSupplierContext, T> provider) {
		ClassDerivedBeanDefinition beanDefinition = new ClassDerivedBeanDefinition(beanClass);
		String beanName = BeanDefinitionReaderUtils.uniqueBeanName(beanClass.getName(), this.beanRegistry);
		customizer.accept(new BeanAdapter(beanDefinition));
		beanDefinition.setInstanceSupplier(() -> provider.apply(new InstanceSupplierContextAdapter(this.beanFactory)));
		this.beanRegistry.registerBeanDefinition(beanName, beanDefinition);
	}

	@Override
	public <T> void registerBean(@Nullable String beanName, Class<T> beanClass, Function<InstanceSupplierContext, T> provider) {
		ClassDerivedBeanDefinition beanDefinition = new ClassDerivedBeanDefinition(beanClass);
		String nameToUse = (beanName != null ? beanName : beanClass.getName());
		beanDefinition.setInstanceSupplier(() -> provider.apply(new InstanceSupplierContextAdapter(this.beanFactory)));
		this.beanRegistry.registerBeanDefinition(nameToUse, beanDefinition);
	}

	@Override
	public <T> void registerBean(@Nullable String beanName, Class<T> beanClass, Consumer<Bean> customizer) {
		ClassDerivedBeanDefinition beanDefinition = new ClassDerivedBeanDefinition(beanClass);
		String nameToUse = (beanName != null ? beanName : beanClass.getName());
		customizer.accept(new BeanAdapter(beanDefinition));
		this.beanRegistry.registerBeanDefinition(nameToUse, beanDefinition);
	}

	@Override
	public <T> void registerBean(@Nullable String beanName, Class<T> beanClass, Consumer<Bean> customizer, Function<InstanceSupplierContext, T> provider) {
		ClassDerivedBeanDefinition beanDefinition = new ClassDerivedBeanDefinition(beanClass);
		String nameToUse = (beanName != null ? beanName : beanClass.getName());
		customizer.accept(new BeanAdapter(beanDefinition));
		beanDefinition.setInstanceSupplier(() -> provider.apply(new InstanceSupplierContextAdapter(this.beanFactory)));
		this.beanRegistry.registerBeanDefinition(nameToUse, beanDefinition);
	}

	/**
	 * {@link RootBeanDefinition} subclass for {@code #registerBean} based
	 * registrations with flexible autowiring for public constructors.
	 */
	@SuppressWarnings("serial")
	private static class ClassDerivedBeanDefinition extends RootBeanDefinition {

		public ClassDerivedBeanDefinition(Class<?> beanClass) {
			super(beanClass);
		}

		public ClassDerivedBeanDefinition(ClassDerivedBeanDefinition original) {
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
			return new ClassDerivedBeanDefinition(this);
		}
	}

	static class BeanAdapter implements Bean {

		private final RootBeanDefinition beanDefinition;

		public BeanAdapter(RootBeanDefinition beanDefinition) {
			this.beanDefinition = beanDefinition;
		}

		@Override
		public void setScope(Scope scope) {
			this.beanDefinition.setScope(scope.toString());
		}

		@Override
		public void setLazyInit(boolean lazyInit) {
			this.beanDefinition.setLazyInit(lazyInit);
		}

		@Override
		public void setPrimary(boolean primary) {
			this.beanDefinition.setPrimary(true);
		}

		@Override
		public void setFallback(boolean fallback) {
			this.beanDefinition.setFallback(true);
		}

		@Override
		public void setAutowireCandidate(boolean autowireCandidate) {
			this.beanDefinition.setAutowireCandidate(autowireCandidate);
		}

		@Override
		public void setDescription(@Nullable String description) {
			this.beanDefinition.setDescription(description);
		}

		@Override
		public void setRole(Role role) {
			this.beanDefinition.setRole(role.getValue());
		}

		@Override
		public void setOrder(int order) {
			this.beanDefinition.setAttribute(AbstractBeanDefinition.ORDER_ATTRIBUTE, order);
		}
	}

	static class InstanceSupplierContextAdapter implements InstanceSupplierContext {

		private final ListableBeanFactory beanFactory;

		public InstanceSupplierContextAdapter(ListableBeanFactory beanFactory) {
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
