/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.test.web.servlet

import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import org.springframework.test.web.servlet.result.*
import java.io.OutputStream
import java.io.Writer

/**
 * @author Sebastien Deleuze
 */
@MockMvcDslMarker
open class MockMvcPerformDsl(private val requestBuilder: MockHttpServletRequestBuilder): (MockMvc) -> MvcResult, MockHttpServletRequestDsl(requestBuilder) {

	private val resultHandlers = mutableListOf<ResultHandler>()

	private val resultMatchers = mutableListOf<ResultMatcher>()

	fun print() {
		resultHandlers.add(MockMvcResultHandlers.print())
	}

	fun print(stream: OutputStream) {
		resultHandlers.add(MockMvcResultHandlers.print(stream))
	}

	fun print(writer: Writer) {
		resultHandlers.add(MockMvcResultHandlers.print(writer))
	}

	fun log() {
		resultHandlers.add(MockMvcResultHandlers.log())
	}

	fun expect(dsl: MockMvcResultMatchersDsl.() -> Unit) {
		MockMvcResultMatchersDsl(resultMatchers).dsl()
	}

	fun handler(resultHandler: ResultHandler) {
		resultHandlers.add(resultHandler)
	}

	override fun invoke(mockMvc: MockMvc): MvcResult {
		val resultActions = mockMvc.perform(requestBuilder)
		for (resultHandler in resultHandlers) {
			resultActions.andDo(resultHandler)
		}
		for (resultMatcher in resultMatchers) {
			resultActions.andExpect(resultMatcher)
		}
		return resultActions.andReturn()
	}
}