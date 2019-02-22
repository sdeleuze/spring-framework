package org.springframework.test.web.servlet

import org.springframework.http.HttpMethod
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import java.net.URI

@DslMarker
internal annotation class MockMvcDslMarker

/**
 * @author Sebastien Deleuze
 */
@MockMvcDslMarker
open class MockMvcDsl(private val mockMvc: MockMvc) {

	fun get(urlTemplate: String, vararg vars: Any, dsl: MockMvcPerformDsl.() -> Unit): MvcResult {
		val requestBuilder = MockMvcRequestBuilders.get(urlTemplate, vars)
		return MockMvcPerformDsl(requestBuilder).apply(dsl).invoke(mockMvc)
	}

	fun get(uri: URI, dsl: MockMvcPerformDsl.() -> Unit): MvcResult {
		val requestBuilder = MockMvcRequestBuilders.get(uri)
		return MockMvcPerformDsl(requestBuilder).apply(dsl).invoke(mockMvc)
	}

	fun post(urlTemplate: String, vararg vars: Any, dsl: MockMvcPerformDsl.() -> Unit): MvcResult {
		val requestBuilder = MockMvcRequestBuilders.post(urlTemplate, vars)
		return MockMvcPerformDsl(requestBuilder).apply(dsl).invoke(mockMvc)
	}

	fun post(uri: URI, dsl: MockMvcPerformDsl.() -> Unit): MvcResult {
		val requestBuilder = MockMvcRequestBuilders.post(uri)
		return MockMvcPerformDsl(requestBuilder).apply(dsl).invoke(mockMvc)
	}

	fun put(urlTemplate: String, vararg vars: Any, dsl: MockMvcPerformDsl.() -> Unit): MvcResult {
		val requestBuilder = MockMvcRequestBuilders.put(urlTemplate, vars)
		return MockMvcPerformDsl(requestBuilder).apply(dsl).invoke(mockMvc)
	}

	fun put(uri: URI, dsl: MockMvcPerformDsl.() -> Unit): MvcResult {
		val requestBuilder = MockMvcRequestBuilders.put(uri)
		return MockMvcPerformDsl(requestBuilder).apply(dsl).invoke(mockMvc)
	}

	fun patch(urlTemplate: String, vararg vars: Any, dsl: MockMvcPerformDsl.() -> Unit): MvcResult {
		val requestBuilder = MockMvcRequestBuilders.patch(urlTemplate, vars)
		return MockMvcPerformDsl(requestBuilder).apply(dsl).invoke(mockMvc)
	}

	fun patch(uri: URI, dsl: MockMvcPerformDsl.() -> Unit): MvcResult {
		val requestBuilder = MockMvcRequestBuilders.patch(uri)
		return MockMvcPerformDsl(requestBuilder).apply(dsl).invoke(mockMvc)
	}

	fun delete(urlTemplate: String, vararg vars: Any, dsl: MockMvcPerformDsl.() -> Unit): MvcResult {
		val requestBuilder = MockMvcRequestBuilders.delete(urlTemplate, vars)
		return MockMvcPerformDsl(requestBuilder).apply(dsl).invoke(mockMvc)
	}

	fun delete(uri: URI, dsl: MockMvcPerformDsl.() -> Unit): MvcResult {
		val requestBuilder = MockMvcRequestBuilders.delete(uri)
		return MockMvcPerformDsl(requestBuilder).apply(dsl).invoke(mockMvc)
	}

	fun options(urlTemplate: String, vararg vars: Any, dsl: MockMvcPerformDsl.() -> Unit): MvcResult {
		val requestBuilder = MockMvcRequestBuilders.options(urlTemplate, vars)
		return MockMvcPerformDsl(requestBuilder).apply(dsl).invoke(mockMvc)
	}

	fun options(uri: URI, dsl: MockMvcPerformDsl.() -> Unit): MvcResult {
		val requestBuilder = MockMvcRequestBuilders.options(uri)
		return MockMvcPerformDsl(requestBuilder).apply(dsl).invoke(mockMvc)
	}

	fun request(method: HttpMethod, urlTemplate: String, vararg vars: Any, dsl: MockMvcPerformDsl.() -> Unit): MvcResult {
		val requestBuilder = MockMvcRequestBuilders.request(method, urlTemplate, vars)
		return MockMvcPerformDsl(requestBuilder).apply(dsl).invoke(mockMvc)
	}

	fun request(method: HttpMethod, uri: URI, dsl: MockMvcPerformDsl.() -> Unit): MvcResult {
		val requestBuilder = MockMvcRequestBuilders.request(method, uri)
		return MockMvcPerformDsl(requestBuilder).apply(dsl).invoke(mockMvc)
	}

	fun multipart(urlTemplate: String, vararg vars: Any, dsl: MockMvcPerformDsl.() -> Unit): MvcResult {
		val requestBuilder = MockMvcRequestBuilders.multipart(urlTemplate, vars)
		return MockMvcPerformDsl(requestBuilder).apply(dsl).invoke(mockMvc)
	}

	fun multipart(uri: URI, dsl: MockMvcPerformDsl.() -> Unit): MvcResult {
		val requestBuilder = MockMvcRequestBuilders.multipart(uri)
		return MockMvcPerformDsl(requestBuilder).apply(dsl).invoke(mockMvc)
	}
}

operator fun MockMvc.invoke(dsl: MockMvcDsl.() -> Unit) = MockMvcDsl(this)
