package org.springframework.test.web.servlet

import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.mock.web.MockHttpSession
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import org.springframework.util.MultiValueMap
import java.security.Principal
import java.util.*
import javax.servlet.http.Cookie

/**
 * @author Sebastien Deleuze
 */
class MockHttpServletRequestDsl(private val builder: MockHttpServletRequestBuilder) {

	var contextPath: String = ""
		set(value) = builder.contextPath(value).let { value }

	var servletPath: String = ""
		set(value) = builder.servletPath(value).let { value }

	var pathInfo: String? = null
		set(value) = builder.pathInfo(value).let { value }

	var secure: Boolean = false
		set(value) = builder.secure(value).let { value }

	var characterEncoding: String = ""
		set(value) = builder.characterEncoding(value).let { value }

	var contentAsByte: ByteArray = ByteArray(0)
		set(value) = builder.content(value).let { value }

	var content: String = ""
		set(value) = builder.content(value).let { value }

	var contentTypeAsString: String = ""
		set(value) = builder.contentType(value).let { value }

	fun headers(headers: HttpHeaders.() -> Unit) {
		builder.headers(HttpHeaders().apply(headers))
	}

	fun header(name: String, vararg values: Any) {
		builder.header(name, *values)
	}

	fun param(name: String, vararg values: String) {
		builder.param(name, *values)
	}

	fun params(params: MultiValueMap<String, String>) {
		builder.params(params)
	}

	fun cookie(vararg cookies: Cookie) {
		builder.cookie(*cookies)
	}

	fun locale(vararg locales: Locale) {
		builder.locale(*locales)
	}

	fun requestAttr(name: String, value: Any) {
		builder.requestAttr(name, value)
	}

	fun sessionAttr(name: String, value: Any) {
		builder.sessionAttr(name, value)
	}

	fun sessionAttrs(sessionAttributes: Map<String, Any>) {
		builder.sessionAttrs(sessionAttributes)
	}

	fun flashAttr(name: String, value: Any) {
		builder.flashAttr(name, value)
	}

	fun flashAttrs(flashAttributes: Map<String, Any>) {
		builder.flashAttrs(flashAttributes)
	}

	var session: MockHttpSession = MockHttpSession()
		set(value) = builder.session(value).let { value }

	var principal: Principal = Principal { "" }
		set(value) = builder.principal(value).let { value }

}