package org.springframework.test.web.servlet

import org.springframework.http.HttpMethod
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import java.net.URI

/**
 * @see MockMvcRequestBuilders.get
 * @author Sebastien Deleuze
 * @since 5.2
 */
fun MockMvc.get(urlTemplate: String, vararg vars: Any?, dsl: MockHttpServletRequestDsl.() -> Unit = {}): ResultActionsWrapper {
	val requestBuilder = MockMvcRequestBuilders.get(urlTemplate, vars)
	return MockHttpServletRequestDsl(requestBuilder).apply(dsl).execute(this)
}

/**
 * @see MockMvcRequestBuilders.get
 * @author Sebastien Deleuze
 * @since 5.2
 */
fun MockMvc.get(uri: URI, dsl: MockHttpServletRequestDsl.() -> Unit): ResultActionsWrapper {
	val requestBuilder = MockMvcRequestBuilders.get(uri)
	return MockHttpServletRequestDsl(requestBuilder).apply(dsl).execute(this)
}

/**
 * @see MockMvcRequestBuilders.post
 * @author Sebastien Deleuze
 * @since 5.2
 */
fun MockMvc.post(urlTemplate: String, vararg vars: Any?, dsl: MockHttpServletRequestDsl.() -> Unit): ResultActionsWrapper {
	val requestBuilder = MockMvcRequestBuilders.post(urlTemplate, vars)
	return MockHttpServletRequestDsl(requestBuilder).apply(dsl).execute(this)
}

/**
 * @see MockMvcRequestBuilders.post
 * @author Sebastien Deleuze
 * @since 5.2
 */
fun MockMvc.post(uri: URI, dsl: MockHttpServletRequestDsl.() -> Unit): ResultActionsWrapper {
	val requestBuilder = MockMvcRequestBuilders.post(uri)
	return MockHttpServletRequestDsl(requestBuilder).apply(dsl).execute(this)
}

/**
 * @see MockMvcRequestBuilders.put
 * @author Sebastien Deleuze
 * @since 5.2
 */
fun MockMvc.put(urlTemplate: String, vararg vars: Any?, dsl: MockHttpServletRequestDsl.() -> Unit): ResultActionsWrapper {
	val requestBuilder = MockMvcRequestBuilders.put(urlTemplate, vars)
	return MockHttpServletRequestDsl(requestBuilder).apply(dsl).execute(this)
}

/**
 * @see MockMvcRequestBuilders.put
 * @author Sebastien Deleuze
 * @since 5.2
 */
fun MockMvc.put(uri: URI, dsl: MockHttpServletRequestDsl.() -> Unit): ResultActionsWrapper {
	val requestBuilder = MockMvcRequestBuilders.put(uri)
	return MockHttpServletRequestDsl(requestBuilder).apply(dsl).execute(this)
}

/**
 * @see MockMvcRequestBuilders.patch
 * @author Sebastien Deleuze
 * @since 5.2
 */
fun MockMvc.patch(urlTemplate: String, vararg vars: Any?, dsl: MockHttpServletRequestDsl.() -> Unit): ResultActionsWrapper {
	val requestBuilder = MockMvcRequestBuilders.patch(urlTemplate, vars)
	return MockHttpServletRequestDsl(requestBuilder).apply(dsl).execute(this)
}

/**
 * @see MockMvcRequestBuilders.patch
 * @author Sebastien Deleuze
 * @since 5.2
 */
fun MockMvc.patch(uri: URI, dsl: MockHttpServletRequestDsl.() -> Unit): ResultActionsWrapper {
	val requestBuilder = MockMvcRequestBuilders.patch(uri)
	return MockHttpServletRequestDsl(requestBuilder).apply(dsl).execute(this)
}

/**
 * @see MockMvcRequestBuilders.delete
 * @author Sebastien Deleuze
 * @since 5.2
 */
fun MockMvc.delete(urlTemplate: String, vararg vars: Any?, dsl: MockHttpServletRequestDsl.() -> Unit): ResultActionsWrapper {
	val requestBuilder = MockMvcRequestBuilders.delete(urlTemplate, vars)
	return MockHttpServletRequestDsl(requestBuilder).apply(dsl).execute(this)
}

/**
 * @see MockMvcRequestBuilders.delete
 * @author Sebastien Deleuze
 * @since 5.2
 */
fun MockMvc.delete(uri: URI, dsl: MockHttpServletRequestDsl.() -> Unit): ResultActionsWrapper {
	val requestBuilder = MockMvcRequestBuilders.delete(uri)
	return MockHttpServletRequestDsl(requestBuilder).apply(dsl).execute(this)
}

/**
 * @see MockMvcRequestBuilders.options
 * @author Sebastien Deleuze
 * @since 5.2
 */
fun MockMvc.options(urlTemplate: String, vararg vars: Any?, dsl: MockHttpServletRequestDsl.() -> Unit): ResultActionsWrapper {
	val requestBuilder = MockMvcRequestBuilders.options(urlTemplate, vars)
	return MockHttpServletRequestDsl(requestBuilder).apply(dsl).execute(this)
}

/**
 * @see MockMvcRequestBuilders.options
 * @author Sebastien Deleuze
 * @since 5.2
 */
fun MockMvc.options(uri: URI, dsl: MockHttpServletRequestDsl.() -> Unit): ResultActionsWrapper {
	val requestBuilder = MockMvcRequestBuilders.options(uri)
	return MockHttpServletRequestDsl(requestBuilder).apply(dsl).execute(this)
}

/**
 * @see MockMvcRequestBuilders.request
 * @author Sebastien Deleuze
 * @since 5.2
 */
fun MockMvc.request(method: HttpMethod, urlTemplate: String, vararg vars: Any?, dsl: MockHttpServletRequestDsl.() -> Unit): ResultActionsWrapper {
	val requestBuilder = MockMvcRequestBuilders.request(method, urlTemplate, vars)
	return MockHttpServletRequestDsl(requestBuilder).apply(dsl).execute(this)
}

/**
 * @see MockMvcRequestBuilders.request
 * @author Sebastien Deleuze
 * @since 5.2
 */
fun MockMvc.request(method: HttpMethod, uri: URI, dsl: MockHttpServletRequestDsl.() -> Unit): ResultActionsWrapper {
	val requestBuilder = MockMvcRequestBuilders.request(method, uri)
	return MockHttpServletRequestDsl(requestBuilder).apply(dsl).execute(this)
}

/**
 * TODO Expose [org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder] capabilities
 *
 * @see MockMvcRequestBuilders.multipart
 * @author Sebastien Deleuze
 * @since 5.2
 */
fun MockMvc.multipart(urlTemplate: String, vararg vars: Any?, dsl: MockHttpServletRequestDsl.() -> Unit): ResultActionsWrapper {
	val requestBuilder = MockMvcRequestBuilders.multipart(urlTemplate, vars)
	return MockHttpServletRequestDsl(requestBuilder).apply(dsl).execute(this)
}

/**
 * TODO Expose [org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder] capabilities
 * @see MockMvcRequestBuilders.multipart
 * @author Sebastien Deleuze
 * @since 5.2
 */
fun MockMvc.multipart(uri: URI, dsl: MockHttpServletRequestDsl.() -> Unit): ResultActionsWrapper {
	val requestBuilder = MockMvcRequestBuilders.multipart(uri)
	return MockHttpServletRequestDsl(requestBuilder).apply(dsl).execute(this)
}

