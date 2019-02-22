package org.springframework.test.web.servlet

import org.springframework.http.HttpMethod
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import java.net.URI

@DslMarker
internal annotation class MockMvcDslMarker

/**
 * Provide a [MockMvc] Kotlin DSL in order to be able to write idiomatic Kotlin code.
 *
 * @author Sebastien Deleuze
 * @author Clint Checketts
 * @author Petr Balat
 * @since 5.2
 */
@MockMvcDslMarker
open class MockMvcDsl(private val mockMvc: MockMvc) {

	/**
	 * @see MockMvcRequestBuilders.get
	 */
	fun get(urlTemplate: String, vararg vars: Any?, dsl: MockMvcPerformDsl.() -> Unit): MvcResult {
		val requestBuilder = MockMvcRequestBuilders.get(urlTemplate, vars)
		return MockMvcPerformDsl(requestBuilder).apply(dsl).invoke(mockMvc)
	}

	/**
	 * @see MockMvcRequestBuilders.get
	 */
	fun get(uri: URI, dsl: MockMvcPerformDsl.() -> Unit): MvcResult {
		val requestBuilder = MockMvcRequestBuilders.get(uri)
		return MockMvcPerformDsl(requestBuilder).apply(dsl).invoke(mockMvc)
	}

	/**
	 * @see MockMvcRequestBuilders.post
	 */
	fun post(urlTemplate: String, vararg vars: Any?, dsl: MockMvcPerformDsl.() -> Unit): MvcResult {
		val requestBuilder = MockMvcRequestBuilders.post(urlTemplate, vars)
		return MockMvcPerformDsl(requestBuilder).apply(dsl).invoke(mockMvc)
	}

	/**
	 * @see MockMvcRequestBuilders.post
	 */
	fun post(uri: URI, dsl: MockMvcPerformDsl.() -> Unit): MvcResult {
		val requestBuilder = MockMvcRequestBuilders.post(uri)
		return MockMvcPerformDsl(requestBuilder).apply(dsl).invoke(mockMvc)
	}

	/**
	 * @see MockMvcRequestBuilders.put
	 */
	fun put(urlTemplate: String, vararg vars: Any?, dsl: MockMvcPerformDsl.() -> Unit): MvcResult {
		val requestBuilder = MockMvcRequestBuilders.put(urlTemplate, vars)
		return MockMvcPerformDsl(requestBuilder).apply(dsl).invoke(mockMvc)
	}

	/**
	 * @see MockMvcRequestBuilders.put
	 */
	fun put(uri: URI, dsl: MockMvcPerformDsl.() -> Unit): MvcResult {
		val requestBuilder = MockMvcRequestBuilders.put(uri)
		return MockMvcPerformDsl(requestBuilder).apply(dsl).invoke(mockMvc)
	}

	/**
	 * @see MockMvcRequestBuilders.patch
	 */
	fun patch(urlTemplate: String, vararg vars: Any?, dsl: MockMvcPerformDsl.() -> Unit): MvcResult {
		val requestBuilder = MockMvcRequestBuilders.patch(urlTemplate, vars)
		return MockMvcPerformDsl(requestBuilder).apply(dsl).invoke(mockMvc)
	}

	/**
	 * @see MockMvcRequestBuilders.patch
	 */
	fun patch(uri: URI, dsl: MockMvcPerformDsl.() -> Unit): MvcResult {
		val requestBuilder = MockMvcRequestBuilders.patch(uri)
		return MockMvcPerformDsl(requestBuilder).apply(dsl).invoke(mockMvc)
	}

	/**
	 * @see MockMvcRequestBuilders.delete
	 */
	fun delete(urlTemplate: String, vararg vars: Any?, dsl: MockMvcPerformDsl.() -> Unit): MvcResult {
		val requestBuilder = MockMvcRequestBuilders.delete(urlTemplate, vars)
		return MockMvcPerformDsl(requestBuilder).apply(dsl).invoke(mockMvc)
	}

	/**
	 * @see MockMvcRequestBuilders.delete
	 */
	fun delete(uri: URI, dsl: MockMvcPerformDsl.() -> Unit): MvcResult {
		val requestBuilder = MockMvcRequestBuilders.delete(uri)
		return MockMvcPerformDsl(requestBuilder).apply(dsl).invoke(mockMvc)
	}

	/**
	 * @see MockMvcRequestBuilders.options
	 */
	fun options(urlTemplate: String, vararg vars: Any?, dsl: MockMvcPerformDsl.() -> Unit): MvcResult {
		val requestBuilder = MockMvcRequestBuilders.options(urlTemplate, vars)
		return MockMvcPerformDsl(requestBuilder).apply(dsl).invoke(mockMvc)
	}

	/**
	 * @see MockMvcRequestBuilders.options
	 */
	fun options(uri: URI, dsl: MockMvcPerformDsl.() -> Unit): MvcResult {
		val requestBuilder = MockMvcRequestBuilders.options(uri)
		return MockMvcPerformDsl(requestBuilder).apply(dsl).invoke(mockMvc)
	}

	/**
	 * @see MockMvcRequestBuilders.request
	 */
	fun request(method: HttpMethod, urlTemplate: String, vararg vars: Any?, dsl: MockMvcPerformDsl.() -> Unit): MvcResult {
		val requestBuilder = MockMvcRequestBuilders.request(method, urlTemplate, vars)
		return MockMvcPerformDsl(requestBuilder).apply(dsl).invoke(mockMvc)
	}

	/**
	 * @see MockMvcRequestBuilders.request
	 */
	fun request(method: HttpMethod, uri: URI, dsl: MockMvcPerformDsl.() -> Unit): MvcResult {
		val requestBuilder = MockMvcRequestBuilders.request(method, uri)
		return MockMvcPerformDsl(requestBuilder).apply(dsl).invoke(mockMvc)
	}

	/**
	 * @see MockMvcRequestBuilders.multipart
	 */
	fun multipart(urlTemplate: String, vararg vars: Any?, dsl: MockMvcPerformDsl.() -> Unit): MvcResult {
		val requestBuilder = MockMvcRequestBuilders.multipart(urlTemplate, vars)
		return MockMvcPerformDsl(requestBuilder).apply(dsl).invoke(mockMvc)
	}

	/**
	 * @see MockMvcRequestBuilders.multipart
	 */
	fun multipart(uri: URI, dsl: MockMvcPerformDsl.() -> Unit): MvcResult {
		val requestBuilder = MockMvcRequestBuilders.multipart(uri)
		return MockMvcPerformDsl(requestBuilder).apply(dsl).invoke(mockMvc)
	}
}


operator fun MockMvc.invoke(dsl: MockMvcDsl.() -> Unit) = MockMvcDsl(this)
