/*
 * Copyright 2025 the original author or authors.
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

package org.springframework.context.annotation

import org.springframework.context.FunctionalInitializer
import org.springframework.context.RegistrableApplicationContext
import org.springframework.context.support.beans

class FunctionalConfigurationKotlinTests {

	class Foo
	class Bar(private val foo: Foo)
	class Baz(var message: String = "")
	class Boo(var message: String = "")

	@Configuration
	class FunctionalConfiguration : FunctionalInitializer {

		@Bean
		fun baz() = Baz("Hello World!")

		override fun initialize(context: RegistrableApplicationContext) = beans(context) {
			bean<Foo>()
			bean<Bar>() // Autowiring of parameters supported by the Kotlin bean DSL
			profile("myProfile") {
				bean("customName") {
					baz()
				}
			}
		}
	}

}