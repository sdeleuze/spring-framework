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

package org.springframework.context

import org.springframework.beans.factory.ObjectProvider
import org.springframework.context.BeanRegistry.InstanceSupplierContext
import org.springframework.context.BeanRegistry.Role
import org.springframework.context.BeanRegistry.Scope
import org.springframework.core.ParameterizedTypeReference
import org.springframework.core.ResolvableType
import org.springframework.core.env.Environment


@DslMarker
internal annotation class BeanRegistrarDslMarker

/**
 * Kotlin DSL for [BeanRegistrar].
 *
 * TODO Documentation
 *
 * @author Sebastien Deleuze
 * @since 7.0
 */
@BeanRegistrarDslMarker
open class BeanRegistrarDsl internal constructor (private val init: BeanRegistrarDsl.() -> Unit): BeanRegistrar {

	@PublishedApi
	internal lateinit var registry: BeanRegistry

	@PublishedApi
	internal lateinit var env: Environment

	inline fun <reified T : Any> registerBean(name: String? = null,
											  scope: Scope = Scope.DEFAULT,
											  lazyInit: Boolean = false,
											  primary: Boolean = false,
											  fallback: Boolean = false,
											  autowireCandidate: Boolean = false,
											  description: String? = "",
											  role: Role = Role.APPLICATION,
											  order: Int? = null) {

		val customizer: (BeanRegistry.Bean) -> Unit = {
			if (scope != Scope.DEFAULT) {
				it.setScope(scope)
			}
			if (lazyInit) {
				it.setLazyInit(true)
			}
			if (primary) {
				it.setPrimary(true)
			}
			if (fallback) {
				it.setFallback(true)
			}
			if (autowireCandidate) {
				it.setAutowireCandidate(true)
			}
			if (description != null) {
				it.setDescription(description)
			}
			if (role != Role.APPLICATION) {
				it.setRole(role)
			}
			if (order != null) {
				it.setOrder(order)
			}
		}
		registry.registerBean(name, T::class.java, customizer)
	}

	inline fun <reified T : Any> registerBean(name: String? = null,
											  scope: Scope = Scope.DEFAULT,
											  lazyInit: Boolean = false,
											  primary: Boolean = false,
											  fallback: Boolean = false,
											  autowireCandidate: Boolean = false,
											  description: String? = "",
											  role: Role = Role.APPLICATION,
											  order: Int? = null,
											  crossinline init: (InstanceSupplierContextDsl<T>.() -> T)) {

		val customizer: (BeanRegistry.Bean) -> Unit = {
			if (scope != Scope.DEFAULT) {
				it.setScope(scope)
			}
			if (lazyInit) {
				it.setLazyInit(true)
			}
			if (primary) {
				it.setPrimary(true)
			}
			if (fallback) {
				it.setFallback(true)
			}
			if (autowireCandidate) {
				it.setAutowireCandidate(true)
			}
			if (description != null) {
				it.setDescription(description)
			}
			if (role != Role.APPLICATION) {
				it.setRole(role)
			}
			if (order != null) {
				it.setOrder(order)
			}
		}
		registry.registerBean(name, T::class.java, customizer) {
			InstanceSupplierContextDsl<T>(it).init()
		}
	}

	@BeanRegistrarDslMarker
	open class InstanceSupplierContextDsl<T>(@PublishedApi internal val context: InstanceSupplierContext) {

		inline fun <reified T : Any> bean(name: String? = null) : T = when (name) {
			null -> context.getBean(T::class.java)
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
