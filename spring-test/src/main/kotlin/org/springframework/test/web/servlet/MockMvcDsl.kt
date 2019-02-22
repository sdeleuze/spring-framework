package org.springframework.test.web.servlet

import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import java.net.URI

/**
 * @author Sebastien Deleuze
 */
class MockMvcDsl(private val mockMvc: MockMvc) {

	fun GET(urlTemplate: String, vararg vars: Any, dsl: MockMvcPerformDsl.() -> Unit): MvcResult {
		val requestBuilder = MockMvcRequestBuilders.get(urlTemplate, vars)
		return MockMvcPerformDsl(requestBuilder).apply(dsl).invoke(mockMvc)
	}

	fun GET(uri: URI, dsl: MockMvcPerformDsl.() -> Unit): MvcResult {
		val requestBuilder = MockMvcRequestBuilders.get(uri)
		return MockMvcPerformDsl(requestBuilder).apply(dsl).invoke(mockMvc)
	}

	fun POST(urlTemplate: String, vararg vars: Any, dsl: MockMvcPerformDsl.() -> Unit): MvcResult {
		val requestBuilder = MockMvcRequestBuilders.post(urlTemplate, vars)
		return MockMvcPerformDsl(requestBuilder).apply(dsl).invoke(mockMvc)
	}

	fun POST(uri: URI, dsl: MockMvcPerformDsl.() -> Unit): MvcResult {
		val requestBuilder = MockMvcRequestBuilders.post(uri)
		return MockMvcPerformDsl(requestBuilder).apply(dsl).invoke(mockMvc)
	}

	fun PUT(urlTemplate: String, vararg vars: Any, dsl: MockMvcPerformDsl.() -> Unit): MvcResult {
		val requestBuilder = MockMvcRequestBuilders.put(urlTemplate, vars)
		return MockMvcPerformDsl(requestBuilder).apply(dsl).invoke(mockMvc)
	}

	fun PUT(uri: URI, dsl: MockMvcPerformDsl.() -> Unit): MvcResult {
		val requestBuilder = MockMvcRequestBuilders.put(uri)
		return MockMvcPerformDsl(requestBuilder).apply(dsl).invoke(mockMvc)
	}

	fun PATCH(urlTemplate: String, vararg vars: Any, dsl: MockMvcPerformDsl.() -> Unit): MvcResult {
		val requestBuilder = MockMvcRequestBuilders.patch(urlTemplate, vars)
		return MockMvcPerformDsl(requestBuilder).apply(dsl).invoke(mockMvc)
	}

	fun PATCH(uri: URI, dsl: MockMvcPerformDsl.() -> Unit): MvcResult {
		val requestBuilder = MockMvcRequestBuilders.patch(uri)
		return MockMvcPerformDsl(requestBuilder).apply(dsl).invoke(mockMvc)
	}

	fun DELETE(urlTemplate: String, vararg vars: Any, dsl: MockMvcPerformDsl.() -> Unit): MvcResult {
		val requestBuilder = MockMvcRequestBuilders.delete(urlTemplate, vars)
		return MockMvcPerformDsl(requestBuilder).apply(dsl).invoke(mockMvc)
	}

	fun DELETE(uri: URI, dsl: MockMvcPerformDsl.() -> Unit): MvcResult {
		val requestBuilder = MockMvcRequestBuilders.delete(uri)
		return MockMvcPerformDsl(requestBuilder).apply(dsl).invoke(mockMvc)
	}

	fun OPTIONS(urlTemplate: String, vararg vars: Any, dsl: MockMvcPerformDsl.() -> Unit): MvcResult {
		val requestBuilder = MockMvcRequestBuilders.options(urlTemplate, vars)
		return MockMvcPerformDsl(requestBuilder).apply(dsl).invoke(mockMvc)
	}

	fun OPTIONS(uri: URI, dsl: MockMvcPerformDsl.() -> Unit): MvcResult {
		val requestBuilder = MockMvcRequestBuilders.options(uri)
		return MockMvcPerformDsl(requestBuilder).apply(dsl).invoke(mockMvc)
	}
}

operator fun MockMvc.invoke(dsl: MockMvcDsl.() -> Unit) = MockMvcDsl(this)
