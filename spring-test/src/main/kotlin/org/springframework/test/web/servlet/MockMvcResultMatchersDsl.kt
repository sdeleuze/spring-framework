/*
 * Copyright 2002-2019 the original author or authors.
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

import org.hamcrest.Matcher
import org.springframework.test.web.servlet.result.*

/**
 * @author Sebastien Deleuze
 */
open class MockMvcResultMatchersDsl(private val actions: ResultActions) {

	fun request(matcher: RequestResultMatchers.() -> ResultMatcher) {
		actions.andExpect(MockMvcResultMatchers.request().matcher())
	}

	fun view(matcher: ViewResultMatchers.() -> ResultMatcher) {
		actions.andExpect(MockMvcResultMatchers.view().matcher())
	}

	fun model(matcher: ModelResultMatchers.() -> ResultMatcher) {
		actions.andExpect(MockMvcResultMatchers.model().matcher())
	}

	fun flash(matcher: FlashAttributeResultMatchers.() -> ResultMatcher) {
		actions.andExpect(MockMvcResultMatchers.flash().matcher())
	}

	fun forwardedUrl(expectedUrl: String?) {
		actions.andExpect(MockMvcResultMatchers.forwardedUrl(expectedUrl))
	}

	fun forwardedUrlTemplate(urlTemplate: String, vararg uriVars: Any) {
		actions.andExpect(MockMvcResultMatchers.forwardedUrlTemplate(urlTemplate, *uriVars))
	}

	fun forwardedUrlPattern(urlPattern: String) {
		actions.andExpect(MockMvcResultMatchers.forwardedUrlPattern(urlPattern))
	}

	fun redirectedUrl(expectedUrl: String) {
		actions.andExpect(MockMvcResultMatchers.redirectedUrl(expectedUrl))
	}

	fun redirectedUrlPattern(redirectedUrlPattern: String) {
		actions.andExpect(MockMvcResultMatchers.redirectedUrlPattern(redirectedUrlPattern))
	}

	fun status(matcher: StatusResultMatchers.() -> ResultMatcher) {
		actions.andExpect(MockMvcResultMatchers.status().matcher())
	}

	fun header(matcher: HeaderResultMatchers.() -> ResultMatcher) {
		actions.andExpect(MockMvcResultMatchers.header().matcher())
	}

	fun content(matcher: ContentResultMatchers.() -> ResultMatcher) {
		actions.andExpect(MockMvcResultMatchers.content().matcher())
	}

	fun <T> jsonPath(expression: String, matcher: Matcher<T>) {
		actions.andExpect(MockMvcResultMatchers.jsonPath(expression, matcher))
	}

	fun jsonPath(expression: String, vararg args: Any, block: JsonPathResultMatchers.() -> ResultMatcher) {
		actions.andExpect(MockMvcResultMatchers.jsonPath(expression, *args).block())
	}

	fun xpath(expression: String, vararg args: Any, namespaces: Map<String, String>? = null, xpathInit: XpathResultMatchers.() -> ResultMatcher) {
		actions.andExpect(MockMvcResultMatchers.xpath(expression, namespaces, args).xpathInit())
	}

	fun cookie(cookieInit: CookieResultMatchers.() -> ResultMatcher) {
		val cookie = MockMvcResultMatchers.cookie().cookieInit()
		actions.andExpect(cookie)
	}

	fun match(matcher: ResultMatcher) {
		actions.andExpect(matcher)
	}
}
