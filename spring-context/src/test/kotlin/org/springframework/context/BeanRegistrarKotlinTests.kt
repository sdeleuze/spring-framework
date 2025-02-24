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

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.assertj.core.api.ThrowableAssert
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.NoSuchBeanDefinitionException
import org.springframework.context.BeanRegistry.Scope.PROTOTYPE
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

/**
 * Kotlin tests leveraging [BeanRegistrarDsl].
 *
 * @author Sebastien Deleuze
 */
class BeanRegistrarKotlinTests {

	@Test
	fun beanRegistrar() {
		val context = AnnotationConfigApplicationContext(MyConfiguration::class.java)
		assertThat(context.getBean(Bar::class.java).foo).isEqualTo(context.getBean(Foo::class.java))
		assertThatThrownBy(ThrowableAssert.ThrowingCallable { context.getBean(Baz::class.java) }).isInstanceOf(NoSuchBeanDefinitionException::class.java)
		assertThat(context.getBean(Init::class.java).initialized).isTrue()
	}

	@Test
	fun beanRegistrarWithProfile() {
		val context = AnnotationConfigApplicationContext()
		context.register(MyConfiguration::class.java)
		context.getEnvironment().addActiveProfile("baz")
		context.refresh()
		assertThat(context.getBean(Bar::class.java).foo).isEqualTo(context.getBean(Foo::class.java))
		assertThat(context.getBean(Baz::class.java).message).isEqualTo("Hello World!")
		assertThat(context.getBean(Init::class.java).initialized).isTrue()
	}
}

class Foo
data class Bar(val foo: Foo)
data class Baz(val message: String = "")
class Init  : InitializingBean {
	var initialized: Boolean = false

	override fun afterPropertiesSet() {
		initialized = true
	}

}

@Configuration(proxyBeanMethods = false)
@Import(MyBeanRegistrar::class)
class MyConfiguration

class MyBeanRegistrar : BeanRegistrarDsl({
	registerBean<Foo>()
	registerBean<Bar>(
		lazyInit = true,
		scope = PROTOTYPE,
		description = "Custom description") {
		Bar(bean<Foo>())
	}
	if (env.matchesProfiles("baz")) {
		registerBean { Baz("Hello World!") }
	}
	registerBean<Init>()
})