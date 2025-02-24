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

package org.springframework.context;

import java.util.function.Consumer;
import java.util.function.Function;

import org.jspecify.annotations.Nullable;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.core.ResolvableType;
import org.springframework.core.env.Environment;

/**
 * Used in {@link BeanRegistrar#register(BeanRegistry, Environment)} to expose programmatic bean registration
 * capabilities.
 *
 * @author Sebastien Deleuze
 * @since 7.0
 * @see BeanRegistrar#register(BeanRegistry, Environment)
 */
public interface BeanRegistry {

	/**
	 * Register a bean from the given bean class, optionally providing explicit
	 * constructor arguments for consideration in the autowiring process.
	 * @param beanClass the class of the bean
	 * constructor resolution algorithm, resolving either all arguments or just
	 * specific ones, with the rest to be resolved through regular autowiring
	 * (may be {@code null} or empty)
	 */
	<T> void registerBean(Class<T> beanClass);

	/**
	 * Register a bean from the given bean class, optionally providing explicit
	 * constructor arguments for consideration in the autowiring process.
	 * @param beanName the name of the bean (may be {@code null})
	 * @param beanClass the class of the bean
	 * constructor resolution algorithm, resolving either all arguments or just
	 * specific ones, with the rest to be resolved through regular autowiring
	 * (may be {@code null} or empty)
	 */
	<T> void registerBean(@Nullable String beanName, Class<T> beanClass);

	/**
	 * Register a bean from the given bean class, optionally customizing its bean definition metadata.
	 * @param beanClass the class of the bean (resolving a public constructor
	 * to be autowired, possibly simply the default constructor)
	 * @param supplier a bean instance provider allowing to get bean references, setting a lazy-init or primary flag
	 * for example.
	 */
	<T> void registerBean(Class<T> beanClass, Function<InstanceSupplierContext, T> supplier);

	/**
	 * Register a bean from the given bean class, optionally customizing its bean definition metadata.
	 * @param beanClass the class of the bean (resolving a public constructor
	 * to be autowired, possibly simply the default constructor)
	 * @param supplier a bean instance supplier allowing to get bean references, setting a lazy-init or primary flag
	 * for example.
	 */
	<T> void registerBean(Class<T> beanClass, Consumer<Bean> customizer, Function<InstanceSupplierContext, T> supplier);

	/**
	 * Register a bean from the given bean class, optionally customizing its bean definition metadata.
	 * @param beanName the name of the bean (may be {@code null})
	 * @param beanClass the class of the bean (resolving a public constructor
	 * to be autowired, possibly simply the default constructor)
	 * @param instanceSupplier a bean instance supplier allowing to get bean references, setting a lazy-init or primary flag
	 * for example.
	 */
	<T> void registerBean(@Nullable String beanName, Class<T> beanClass, Function<InstanceSupplierContext, T> instanceSupplier);

	/**
	 * Register a bean from the given bean class, optionally customizing its bean definition metadata.
	 * @param beanName the name of the bean (may be {@code null})
	 * @param beanClass the class of the bean (resolving a public constructor
	 * to be autowired, possibly simply the default constructor)
	 * @param customizer callback to customize other bean properties than the name
	 * for example.
	 */
	<T> void registerBean(@Nullable String beanName, Class<T> beanClass, Consumer<Bean> customizer);

	/**
	 * Register a bean from the given bean class, optionally customizing its bean definition metadata.
	 * @param beanName the name of the bean (may be {@code null})
	 * @param beanClass the class of the bean (resolving a public constructor
	 * to be autowired, possibly simply the default constructor)
	 * @param customizer callback to customize other bean properties than the name
	 * @param instanceSupplier a bean instance supplier allowing to get bean references, setting a lazy-init or primary flag
	 * for example.
	 */
	<T> void registerBean(@Nullable String beanName, Class<T> beanClass, Consumer<Bean> customizer, Function<InstanceSupplierContext, T> instanceSupplier);


	interface Bean {
		/**
		 * Override the target scope of this bean.
		 */
		void setScope(Scope scope);

		/**
		 * Set whether this bean should be lazily initialized.
		 * <p>If {@code false}, the bean will get instantiated on startup by bean
		 * factories that perform eager initialization of singletons.
		 */
		void setLazyInit(boolean lazyInit);

		/**
		 * Set whether this bean is a primary autowire candidate.
		 * <p>If this value is {@code true} for exactly one bean among multiple
		 * matching candidates, it will serve as a tie-breaker.
		 * @see #setFallback
		 */
		void setPrimary(boolean primary);

		/**
		 * Set whether this bean is a fallback autowire candidate.
		 * <p>If this value is {@code true} for all beans but one among multiple
		 * matching candidates, the remaining bean will be selected.
		 * @see #setPrimary
		 */
		void setFallback(boolean fallback);

		/**
		 * Set whether this bean is a candidate for getting autowired into some other bean.
		 * <p>Note that this flag is designed to only affect type-based autowiring.
		 * It does not affect explicit references by name, which will get resolved even
		 * if the specified bean is not marked as an autowire candidate. As a consequence,
		 * autowiring by name will nevertheless inject a bean if the name matches.
		 */
		void setAutowireCandidate(boolean autowireCandidate);

		/**
		 * Return a human-readable description of this bean definition.
		 */
		void setDescription(@Nullable String description);

		/**
		 * Set the role hint for this bean. The role hint provides the
		 * frameworks as well as tools an indication of the role and
		 * importance of a particular bean.
		 */
		void setRole(Role role);

		/**
		 * The sort order for the targeted bean. This is analogous to the {@code @Order}
		 * annotation.
		 */
		void setOrder(int order);
	}

	interface InstanceSupplierContext {

		/**
		 * Return the bean instance that uniquely matches the given object type, if any.
		 * @param requiredType type the bean must match; can be an interface or superclass
		 * @return an instance of the single bean matching the required type
		 * @see BeanFactory#getBean(String)
		 */
		<T> T getBean(Class<T> requiredType) throws BeansException;

		/**
		 * Return an instance, which may be shared or independent, of the specified bean.
		 * @param name the name of the bean to retrieve
		 * @param requiredType type the bean must match; can be an interface or superclass
		 * @return an instance of the bean.
		 * @see BeanFactory#getBean(String, Class)
		 */
		<T> T getBean(String name, Class<T> requiredType) throws BeansException;

		/**
		 * Return a provider for the specified bean, allowing for lazy on-demand retrieval
		 * of instances, including availability and uniqueness options.
		 * <p>For matching a generic type, consider {@link #getBeanProvider(ResolvableType)}.
		 * @param requiredType type the bean must match; can be an interface or superclass
		 * @return a corresponding provider handle
		 * @see BeanFactory#getBeanProvider(Class)
		 */
		<T> ObjectProvider<T> getBeanProvider(Class<T> requiredType);

		/**
		 * Return a provider for the specified bean, allowing for lazy on-demand retrieval
		 * of instances, including availability and uniqueness options. This variant allows
		 * for specifying a generic type to match, similar to reflective injection points
		 * with generic type declarations in method/constructor parameters.
		 * <p>Note that collections of beans are not supported here, in contrast to reflective
		 * injection points. For programmatically retrieving a list of beans matching a
		 * specific type, specify the actual bean type as an argument here and subsequently
		 * use {@link ObjectProvider#orderedStream()} or its lazy streaming/iteration options.
		 * <p>Also, generics matching is strict here, as per the Java assignment rules.
		 * For lenient fallback matching with unchecked semantics (similar to the 'unchecked'
		 * Java compiler warning), consider calling {@link #getBeanProvider(Class)} with the
		 * raw type as a second step if no full generic match is
		 * {@link ObjectProvider#getIfAvailable() available} with this variant.
		 * @param requiredType type the bean must match; can be a generic type declaration
		 * @return a corresponding provider handle
		 * @see BeanFactory#getBeanProvider(ResolvableType)
		 */
		<T> ObjectProvider<T> getBeanProvider(ResolvableType requiredType);
	}

	/**
	 * Scope enum constants.
	 */
	enum Scope {

		/**
		 * Scope constant for the standard singleton scope.
		 * @see org.springframework.beans.factory.config.BeanDefinition#SCOPE_SINGLETON
		 */
		SINGLETON(BeanDefinition.SCOPE_SINGLETON),

		/**
		 * Scope constant for the standard prototype scope.
		 * @see org.springframework.beans.factory.config.BeanDefinition#SCOPE_PROTOTYPE
		 */
		PROTOTYPE(BeanDefinition.SCOPE_PROTOTYPE),

		/**
		 * Scope constant for the default scope.
		 * @see org.springframework.beans.factory.support.AbstractBeanDefinition#SCOPE_DEFAULT
		 */
		DEFAULT(AbstractBeanDefinition.SCOPE_DEFAULT);

		private final String name;

		Scope(String name) {
			this.name = name;
		}

		public String toString() {
			return this.name;
		}
	}

	/**
	 * Role enum constants.
	 */
	enum Role {

		/**
		 * Role hint indicating that a [BeanDefinition] is a major part
		 * of the application. Typically corresponds to a user-defined bean.
		 * @see org.springframework.beans.factory.config.BeanDefinition#ROLE_APPLICATION
		 */
		APPLICATION(BeanDefinition.ROLE_APPLICATION),

		/**
		 * Role hint indicating that a [BeanDefinition] is a supporting
		 * part of some larger configuration, typically an outer
		 * [org.springframework.beans.factory.parsing.ComponentDefinition].
		 * [SUPPORT] beans are considered important enough to be aware of
		 * when looking more closely at a particular
		 * [org.springframework.beans.factory.parsing.ComponentDefinition],
		 * but not when looking at the overall configuration of an application.
		 * @see org.springframework.beans.factory.config.BeanDefinition#ROLE_SUPPORT
		 */
		SUPPORT(BeanDefinition.ROLE_SUPPORT),

		/**
		 * Role hint indicating that a [BeanDefinition] is providing an
		 * entirely background role and has no relevance to the end-user. This hint is
		 * used when registering beans that are completely part of the internal workings
		 * of a [org.springframework.beans.factory.parsing.ComponentDefinition].
		 * @see org.springframework.beans.factory.config.BeanDefinition#ROLE_INFRASTRUCTURE
		 */
		INFRASTRUCTURE(BeanDefinition.ROLE_INFRASTRUCTURE);


		private final int value;

		Role(int value) {
			this.value = value;
		}

		public int getValue() {
			return this.value;
		}
	}
}
