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
class MockMvcPerformDsl(private val requestBuilder: MockHttpServletRequestBuilder): (MockMvc) -> MvcResult {

	private val resultHandlers = mutableListOf<ResultHandler>()

	private val resultMatchers = mutableListOf<ResultMatcher>()

	fun print(stream: OutputStream? = null, writer: Writer? = null) {
		resultHandlers.add(when {
			stream != null && writer != null -> throw IllegalArgumentException("Stream and writer parameters should not be both specified")
			stream != null -> MockMvcResultHandlers.print(stream)
			writer != null -> MockMvcResultHandlers.print(writer)
			else -> MockMvcResultHandlers.print()
		})
	}

	fun log() {
		resultHandlers.add(MockMvcResultHandlers.log())
	}

	fun request(dsl: MockHttpServletRequestDsl.() -> Unit) {
		MockHttpServletRequestDsl(requestBuilder).dsl()
	}

	fun expect(dsl: MockMvcResultMatchersDsl.() -> Unit) {
		MockMvcResultMatchersDsl(resultMatchers).dsl()
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