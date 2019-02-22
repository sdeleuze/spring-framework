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
class MockMvcResultMatchersDsl(private val matchers: MutableList<ResultMatcher>) {

	fun request(matcher: RequestResultMatchers.() -> ResultMatcher) {
		matchers.add(MockMvcResultMatchers.request().matcher())
	}

	fun view(matcher: ViewResultMatchers.() -> ResultMatcher) {
		matchers.add(MockMvcResultMatchers.view().matcher())
	}

	fun model(matcher: ModelResultMatchers.() -> ResultMatcher) {
		matchers.add(MockMvcResultMatchers.model().matcher())
	}

	fun flash(matcher: FlashAttributeResultMatchers.() -> ResultMatcher) {
		matchers.add(MockMvcResultMatchers.flash().matcher())
	}

	fun forwardedUrl(expectedUrl: String?) {
		matchers.add(MockMvcResultMatchers.forwardedUrl(expectedUrl))
	}

	fun forwardedUrlTemplate(urlTemplate: String, vararg uriVars: Any) {
		matchers.add(MockMvcResultMatchers.forwardedUrlTemplate(urlTemplate, *uriVars))
	}

	fun forwardedUrlPattern(urlPattern: String) {
		matchers.add(MockMvcResultMatchers.forwardedUrlPattern(urlPattern))
	}

	fun redirectedUrl(expectedUrl: String) {
		matchers.add(MockMvcResultMatchers.redirectedUrl(expectedUrl))
	}

	fun redirectedUrlPattern(redirectedUrlPattern: String) {
		matchers.add(MockMvcResultMatchers.redirectedUrlPattern(redirectedUrlPattern))
	}

	fun status(matcher: StatusResultMatchers.() -> ResultMatcher) {
		matchers.add(MockMvcResultMatchers.status().matcher())
	}

	fun header(matcher: HeaderResultMatchers.() -> ResultMatcher) {
		matchers.add(MockMvcResultMatchers.header().matcher())
	}

	fun content(matcher: ContentResultMatchers.() -> ResultMatcher) {
		matchers.add(MockMvcResultMatchers.content().matcher())
	}

	fun <T> jsonPath(expression: String, matcher: Matcher<T>) {
		matchers.add(MockMvcResultMatchers.jsonPath(expression, matcher))
	}

	fun jsonPath(expression: String, vararg args: Any, block: JsonPathResultMatchers.() -> ResultMatcher) {
		matchers.add(MockMvcResultMatchers.jsonPath(expression, *args).block())
	}

	fun xpath(expression: String, vararg args: Any, namespaces: Map<String, String>? = null, xpathInit: XpathResultMatchers.() -> ResultMatcher) {
		matchers.add(MockMvcResultMatchers.xpath(expression, namespaces, args).xpathInit())
	}

	fun cookie(cookieInit: CookieResultMatchers.() -> ResultMatcher) {
		val cookie = MockMvcResultMatchers.cookie().cookieInit()
		matchers.add(cookie)
	}

	fun match(matcher: ResultMatcher) {
		matchers.add(matcher)
	}
}
