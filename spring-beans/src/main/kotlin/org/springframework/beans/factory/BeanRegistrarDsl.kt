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

package org.springframework.beans.factory

import org.springframework.beans.factory.BeanRegistry.SupplierContext
import org.springframework.core.ParameterizedTypeReference
import org.springframework.core.ResolvableType
import org.springframework.core.env.Environment

/**
 * Contract for registering programmatically beans.
 *
 * Typically imported with an `@Import` annotation on `@Configuration` classes.
 * ```
 * @Configuration
 * @Import(MyBeanRegistrar::class)
 * class MyConfiguration {
 * }
 * ```
 *
 * The bean registrar implementation uses [BeanRegistry] and [Environment]
 * APIs to register bean programmatically in a concise and flexible way.
 * ```
 * class SampleBeanRegistrar : BeanRegistrarDsl({
 * 	    registerBean<Foo>()
 * 	    registerBean<Bar>(
 * 	    	    name = "bar",
 * 	    	    prototype = true,
 * 	    	    lazyInit = true,
 * 	    	    description = "Custom description") {
 * 	    	    Bar(bean<Foo>())
 * 	    }
 * 	    if (env.matchesProfiles("baz")) {
 * 	    	    registerBean { Baz("Hello World!") }
 * 	    }
 * 	    registerBean<Init>()
 * 	})
 * ```
 *
 * @author Sebastien Deleuze
 * @since 7.0
 */
@BeanRegistrarDslMarker
open class BeanRegistrarDsl(private val init: BeanRegistrarDsl.() -> Unit): BeanRegistrar {

	@PublishedApi
	internal lateinit var registry: BeanRegistry

	/**
	 * The environment that can be used to get the active profile or some properties.
	 */
	lateinit var env: Environment

	/**
	 * Register a bean from the given bean class, which will be instantiated
	 * using the related [org.springframework.beans.BeanUtils.getResolvableConstructor]
	 * resolvable constructor if any.
	 * @param T the bean type
	 * @param name the name of the bean
	 * @param autowirable set whether this bean is a candidate for getting
	 * autowired into some other bean
	 * @param backgroundInit set whether this bean allows for instantiation
	 * on a background thread
	 * @param description a human-readable description of this bean
	 * @param fallback set whether this bean is a fallback autowire candidate
	 * @param infrastructure set whether this bean has an infrastructure role,
	 * meaning it has no relevance to the end-user
	 * @param lazyInit set whether this bean is lazily initialized
	 * @param order the sort order for the targeted bean
	 * @param primary set whether this bean is a primary autowire candidate
	 * @param prototype set whether this bean has a prototype scope
	 */
	inline fun <reified T : Any> registerBean(name: String,
											  autowirable: Boolean = true,
											  backgroundInit: Boolean = false,
											  description: String? = null,
											  fallback: Boolean = false,
											  infrastructure: Boolean = false,
											  lazyInit: Boolean = false,
											  order: Int? = null,
											  primary: Boolean = false,
											  prototype: Boolean = false) {

		val spec: (BeanRegistry.Spec<T>) -> Unit = {
			if (!autowirable) {
				it.notAutowirable()
			}
			if (backgroundInit) {
				it.backgroundInit()
			}
			if (description != null) {
				it.description(description)
			}
			if (fallback) {
				it.fallback()
			}
			if (infrastructure) {
				it.infrastructure()
			}
			if (lazyInit) {
				it.lazyInit()
			}
			if (order != null) {
				it.order(order)
			}
			if (primary) {
				it.primary()
			}
			if (prototype) {
				it.prototype()
			}
		}
		registry.registerBean(name, T::class.java, spec)
	}

	/**
	 * Register a bean from the given bean class, which will be instantiated
	 * using the related [org.springframework.beans.BeanUtils.getResolvableConstructor]
	 * resolvable constructor if any.
	 * @param T the bean type
	 * @param autowirable set whether this bean is a candidate for getting
	 * autowired into some other bean
	 * @param backgroundInit set whether this bean allows for instantiation
	 * on a background thread
	 * @param description a human-readable description of this bean
	 * @param fallback set whether this bean is a fallback autowire candidate
	 * @param infrastructure set whether this bean has an infrastructure role,
	 * meaning it has no relevance to the end-user
	 * @param lazyInit set whether this bean is lazily initialized
	 * @param order the sort order for the targeted bean
	 * @param primary set whether this bean is a primary autowire candidate
	 * @param prototype set whether this bean has a prototype scope
	 * @return the generated bean name
	 */
	inline fun <reified T : Any> registerBean(autowirable: Boolean = true,
											  backgroundInit: Boolean = false,
											  description: String? = null,
											  fallback: Boolean = false,
											  infrastructure: Boolean = false,
											  lazyInit: Boolean = false,
											  order: Int? = null,
											  primary: Boolean = false,
											  prototype: Boolean = false): String {

		val spec: (BeanRegistry.Spec<T>) -> Unit = {
			if (!autowirable) {
				it.notAutowirable()
			}
			if (backgroundInit) {
				it.backgroundInit()
			}
			if (description != null) {
				it.description(description)
			}
			if (fallback) {
				it.fallback()
			}
			if (infrastructure) {
				it.infrastructure()
			}
			if (lazyInit) {
				it.lazyInit()
			}
			if (order != null) {
				it.order(order)
			}
			if (primary) {
				it.primary()
			}
			if (prototype) {
				it.prototype()
			}
		}
		return registry.registerBean(T::class.java, spec)
	}

	/**
	 * Register a bean from the given bean class, which will be instantiated
	 * using the provided [supplier].
	 * @param T the bean type
	 * @param name the name of the bean
	 * @param autowirable set whether this bean is a candidate for getting
	 * autowired into some other bean
	 * @param backgroundInit set whether this bean allows for instantiation
	 * on a background thread
	 * @param description a human-readable description of this bean
	 * @param fallback set whether this bean is a fallback autowire candidate
	 * @param infrastructure set whether this bean has an infrastructure role,
	 * meaning it has no relevance to the end-user
	 * @param lazyInit set whether this bean is lazily initialized
	 * @param order the sort order for the targeted bean
	 * @param primary set whether this bean is a primary autowire candidate
	 * @param prototype set whether this bean has a prototype scope
	 * @param supplier the supplier to construct a bean instance
	 */
	inline fun <reified T : Any> registerBean(name: String,
											  autowirable: Boolean = true,
											  backgroundInit: Boolean = false,
											  description: String? = null,
											  fallback: Boolean = false,
											  infrastructure: Boolean = false,
											  lazyInit: Boolean = false,
											  order: Int? = null,
											  primary: Boolean = false,
											  prototype: Boolean = false,
											  crossinline supplier: (SupplierContextDsl<T>.() -> T)) {

		val spec: (BeanRegistry.Spec<T>) -> Unit = {
			if (!autowirable) {
				it.notAutowirable()
			}
			if (backgroundInit) {
				it.backgroundInit()
			}
			if (description != null) {
				it.description(description)
			}
			if (fallback) {
				it.fallback()
			}
			if (infrastructure) {
				it.infrastructure()
			}
			if (lazyInit) {
				it.lazyInit()
			}
			if (order != null) {
				it.order(order)
			}
			if (primary) {
				it.primary()
			}
			if (prototype) {
				it.prototype()
			}
			it.supplier {
				SupplierContextDsl<T>(it).supplier()
			}
		}
		registry.registerBean(name, T::class.java, spec)
	}

	inline fun <reified T : Any> registerBean(autowirable: Boolean = true,
											  backgroundInit: Boolean = false,
											  description: String? = null,
											  fallback: Boolean = false,
											  infrastructure: Boolean = false,
											  lazyInit: Boolean = false,
											  order: Int? = null,
											  primary: Boolean = false,
											  prototype: Boolean = false,
											  crossinline supplier: (SupplierContextDsl<T>.() -> T)): String {
		/**
		 * Register a bean from the given bean class, which will be instantiated
		 * using the provided [supplier].
		 * @param T the bean type
		 * @param autowirable set whether this bean is a candidate for getting
		 * autowired into some other bean
		 * @param backgroundInit set whether this bean allows for instantiation
		 * on a background thread
		 * @param description a human-readable description of this bean
		 * @param fallback set whether this bean is a fallback autowire candidate
		 * @param infrastructure set whether this bean has an infrastructure role,
		 * meaning it has no relevance to the end-user
		 * @param lazyInit set whether this bean is lazily initialized
		 * @param order the sort order for the targeted bean
		 * @param primary set whether this bean is a primary autowire candidate
		 * @param prototype set whether this bean has a prototype scope
		 * @param supplier the supplier to construct a bean instance
		 */

		val spec: (BeanRegistry.Spec<T>) -> Unit = {
			if (!autowirable) {
				it.notAutowirable()
			}
			if (backgroundInit) {
				it.backgroundInit()
			}
			if (description != null) {
				it.description(description)
			}
			if (infrastructure) {
				it.infrastructure()
			}
			if (fallback) {
				it.fallback()
			}
			if (lazyInit) {
				it.lazyInit()
			}
			if (order != null) {
				it.order(order)
			}
			if (primary) {
				it.primary()
			}
			if (prototype) {
				it.prototype()
			}
			it.supplier {
				SupplierContextDsl<T>(it).supplier()
			}
		}
		return registry.registerBean(T::class.java, spec)
	}

	/**
	 * Context available from the bean instance supplier designed to give access
	 * to bean dependencies.
	 */
	@BeanRegistrarDslMarker
	open class SupplierContextDsl<T>(@PublishedApi internal val context: SupplierContext) {

		/**
		 * Return the bean instance that uniquely matches the given object type,
		 * and potentially the name if provided, if any.
		 * @param T the bean type
		 * @param name the name of the bean
		 */
		inline fun <reified T : Any> bean(name: String? = null) : T = when (name) {
			null -> beanProvider<T>().getObject()
			else -> context.bean(name, T::class.java)
		}

		/**
		 * Return a provider for the specified bean, allowing for lazy on-demand
		 * retrieval of instances, including availability and uniqueness options.
		 * @param T type the bean must match; can be an interface or superclass
		 * @return a corresponding provider handle
		 */
		inline fun <reified T : Any> beanProvider() : ObjectProvider<T> =
			context.beanProvider(ResolvableType.forType((object : ParameterizedTypeReference<T>() {}).type))
	}

	override fun register(registry: BeanRegistry, env: Environment) {
		this.registry = registry
		this.env = env
		init()
	}

}

@DslMarker
internal annotation class BeanRegistrarDslMarker
