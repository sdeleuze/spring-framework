/*
 * Copyright 2002-2015 the original author or authors.
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

import org.hamcrest.CoreMatchers
import org.junit.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.http.MediaType
import org.springframework.stereotype.Controller
import org.springframework.test.web.Person
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import java.security.Principal

/**
 * MockMvcDsl tests that verify builder, actions, and expect blocks
 *
 * @author Clint Checketts
 * @author Petr Balat
 * @author Sebastien Deleuze
 */
class MockMvcDslTests {

	private val mockMvc = MockMvcBuilders.standaloneSetup(PersonController()).build()

	@Test
	fun json() {
		mockMvc {
			GET("/person/{name}", "Lee") {
				print()
				request {
					secure = true
					headers {
						accept = listOf(MediaType.APPLICATION_JSON)
					}
					principal = Principal { "foo" }
				}
				expect {
					status { isOk }
					content { contentType("application/json;charset=UTF-8") }
					jsonPath("$.name") { value("Lee") }
					content { json("""{"someBoolean": false}""", false) }
				}
			}
			POST("/person") {
				request {
					content = """{ "name": "foo" }"""
					headers {
						accept = listOf(MediaType.APPLICATION_JSON)
						contentType = MediaType.APPLICATION_JSON
					}
				}
				expect {
					status {
						isCreated
					}
				}
			}
		}
	}

	@Test
	fun `negative assertion tests to verify the matchers throw errors when expected`() {
		val name = "Petr"
		mockMvc {
			GET("/person/$name") {
				request {
					headers {
						accept = listOf(MediaType.APPLICATION_JSON)
					}
					print()
					expect {
						assertThrows<AssertionError> { content { contentType(MediaType.APPLICATION_ATOM_XML) } }
						assertThrows<AssertionError> { content { string("Wrong") } }
						assertThrows<AssertionError> { jsonPath("name", CoreMatchers.`is`("Wrong")) }
						assertThrows<AssertionError> { content { json("""{"name":"wrong"}""") } }
						assertThrows<AssertionError> { jsonPath("name") { value("wrong") } }
						assertThrows<AssertionError> { cookie { value("name", "wrong") } }
						assertThrows<AssertionError> { flash { attribute<String>("name", "wrong") } }
						assertThrows<AssertionError> { header { stringValues("name", "wrong") } }
						assertThrows<AssertionError> { model { attributeExists("name", "wrong") } }
						assertThrows<AssertionError> { redirectedUrl("wrong/Url") }
						assertThrows<AssertionError> { redirectedUrlPattern("wrong/Url") }
						assertThrows<AssertionError> { redirectedUrlPattern("wrong/Url") }
						assertThrows<AssertionError> { status { isAccepted } }
						assertThrows<AssertionError> { view { name("wrongName") } }
						assertThrows<AssertionError> { jsonPath("name") { value("wrong") } }
					}
				}
		}
		}
	}

	@Test
	fun `negative assertion tests for xpath`() {
		mockMvc {
			GET("/person/Clint") {
				request {
					headers {
						accept = listOf(MediaType.APPLICATION_XML)
					}
				}
				print()
				expect {
					status {
						isOk
					}
					print()
					expect {
						assertThrows<AssertionError> { xpath("//wrong") { nodeCount(1) } }
					}
				}
			}
		}
	}


	@Controller
	private inner class PersonController {

		@RequestMapping("/person/{name}")
		@ResponseBody
		operator fun get(@PathVariable name: String): Person {
			return Person(name)
		}
	}

}
