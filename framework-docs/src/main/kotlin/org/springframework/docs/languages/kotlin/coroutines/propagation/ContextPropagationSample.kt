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

package org.springframework.docs.languages.kotlin.coroutines.propagation

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.springframework.core.PropagationContextElement

class ContextPropagationSample {

	companion object {
		private val logger: Log = LogFactory.getLog(
			ContextPropagationSample::class.java
		)
	}

	// tag::context[]
	fun main() {
		runBlocking(Dispatchers.IO + PropagationContextElement()) {
			suspendingFunction()
		}
	}

	suspend fun suspendingFunction() {
		delay(1)
		logger.info("Suspending function with traceId")
	}
	// end::context[]
}