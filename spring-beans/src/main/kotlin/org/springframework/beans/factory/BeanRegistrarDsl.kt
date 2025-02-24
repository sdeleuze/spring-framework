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
 * Kotlin DSL for [BeanRegistrar].
 *
 * TODO Documentation
 *
 * @author Sebastien Deleuze
 * @since 7.0
 */
@BeanRegistrarDslMarker
open class BeanRegistrarDsl(private val init: BeanRegistrarDsl.() -> Unit): BeanRegistrar {

	lateinit var registry: BeanRegistry

	lateinit var env: Environment

	inline fun <reified T : Any> registerBean(name: String,
											  autowireCandidate: Boolean = true,
											  description: String? = "",
											  fallback: Boolean = false,
											  infrastructure: Boolean = false,
											  lazyInitialized: Boolean = false,
											  order: Int? = null,
											  primary: Boolean = false,
											  prototype: Boolean = false) {

		val beanSpec: (BeanRegistry.BeanSpec<T>) -> Unit = {
			if (!autowireCandidate) {
				it.notAutowireCandidate()
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
			if (lazyInitialized) {
				it.lazyInitialized()
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
		registry.registerBean(name, T::class.java, beanSpec)
	}

	inline fun <reified T : Any> registerBean(autowireCandidate: Boolean = true,
											  description: String? = "",
											  fallback: Boolean = false,
											  infrastructure: Boolean = false,
											  lazyInitialized: Boolean = false,
											  order: Int? = null,
											  primary: Boolean = false,
											  prototype: Boolean = false): String {

		val beanSpec: (BeanRegistry.BeanSpec<T>) -> Unit = {
			if (!autowireCandidate) {
				it.notAutowireCandidate()
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
			if (lazyInitialized) {
				it.lazyInitialized()
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
		return registry.registerBean(T::class.java, beanSpec)
	}


	inline fun <reified T : Any> registerBean(name: String,
											  autowireCandidate: Boolean = true,
											  description: String? = "",
											  fallback: Boolean = false,
											  infrastructure: Boolean = false,
											  lazyInitialized: Boolean = false,
											  order: Int? = null,
											  primary: Boolean = false,
											  prototype: Boolean = false,
											  crossinline supplier: (SupplierContextDsl<T>.() -> T)) {

		val beanSpec: (BeanRegistry.BeanSpec<T>) -> Unit = {
			if (!autowireCandidate) {
				it.notAutowireCandidate()
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
			if (lazyInitialized) {
				it.lazyInitialized()
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
		registry.registerBean(name, T::class.java, beanSpec)
	}

	inline fun <reified T : Any> registerBean(autowireCandidate: Boolean = true,
											  description: String? = "",
											  fallback: Boolean = false,
											  infrastructure: Boolean = false,
											  lazyInitialized: Boolean = false,
											  order: Int? = null,
											  primary: Boolean = false,
											  prototype: Boolean = false,
											  crossinline supplier: (SupplierContextDsl<T>.() -> T)): String {

		val beanSpec: (BeanRegistry.BeanSpec<T>) -> Unit = {
			if (!autowireCandidate) {
				it.notAutowireCandidate()
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
			if (lazyInitialized) {
				it.lazyInitialized()
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
		return registry.registerBean(T::class.java, beanSpec)
	}

	@BeanRegistrarDslMarker
	open class SupplierContextDsl<T>(@PublishedApi internal val context: SupplierContext) {

		inline fun <reified T : Any> bean(name: String? = null) : T = when (name) {
			null -> beanProvider<T>().getObject()
			else -> context.getBean(name, T::class.java)
		}

		inline fun <reified T : Any> beanProvider() : ObjectProvider<T> =
			context.getBeanProvider(ResolvableType.forType((object : ParameterizedTypeReference<T>() {}).type))
	}

	override fun register(registry: BeanRegistry, env: Environment) {
		this.registry = registry
		this.env = env
		init()
	}

}

@DslMarker
internal annotation class BeanRegistrarDslMarker
