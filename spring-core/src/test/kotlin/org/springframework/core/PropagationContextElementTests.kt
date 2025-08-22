/*
 * Copyright 2002-present the original author or authors.
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

package org.springframework.core

import io.micrometer.observation.Observation
import io.micrometer.observation.tck.TestObservationRegistry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import reactor.core.publisher.Hooks
import reactor.test.StepVerifier
import kotlin.coroutines.Continuation
import kotlin.coroutines.coroutineContext

/**
 * Kotlin tests for [PropagationContextElement].
 *
 * @author Brian Clozel
 */
class PropagationContextElementTests {

	private val observationRegistry = TestObservationRegistry.create()

	// maybe not useful
	companion object {

		@BeforeAll
		@JvmStatic
		fun init() {
			Hooks.enableAutomaticContextPropagation()
		}

		@AfterAll
		@JvmStatic
		fun cleanup() {
			Hooks.disableAutomaticContextPropagation()
		}

	}

	@Test
	fun restoresFromThreadLocal() {
		val observation = Observation.createNotStarted("coroutine", observationRegistry)
		observation.observe {
			val result = runBlocking(Dispatchers.IO) {
                suspendingFunction("test")
            }
			Assertions.assertThat(result).isEqualTo("coroutine")
		}
	}

	@Test
	fun restoresFromReactorContext() {
		val method = PropagationContextElementTests::class.java.getDeclaredMethod("suspendingFunction", String::class.java, Continuation::class.java)
		val publisher = CoroutinesUtils.invokeSuspendingFunction(method, this, "test", null)

		val observation = Observation.createNotStarted("coroutine", observationRegistry)
		observation.observe {
			StepVerifier.create(publisher)
				.expectNext("coroutine")
				.verifyComplete()
		}
	}


	suspend fun suspendingFunction(value: String): String? {
		return withContext(PropagationContextElement(coroutineContext)) {
            val currentObservation = observationRegistry.currentObservation
            Assertions.assertThat(currentObservation).isNotNull
            currentObservation?.context?.name
        }
	}

}