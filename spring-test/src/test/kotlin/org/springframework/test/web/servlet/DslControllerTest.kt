package org.springframework.test.web.servlet

import junit.framework.TestCase.assertTrue
import org.hamcrest.CoreMatchers
import org.junit.Assert.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.util.AssertionErrors
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.ResultMatcher
import org.springframework.test.web.servlet.result.JsonPathResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.result.ModelResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import javax.servlet.http.Cookie

@ExtendWith(SpringExtension::class)
@ActiveProfiles("test")
class DslControllerTest {

	@Autowired
	lateinit var context: WebApplicationContext

	val mockMvc: MockMvc  by lazy {
		MockMvcBuilders.webAppContextSetup(context).build()
	}

	class ClintMatchers(val matchers: ResultActions) : MockMvcResultMatchersDsl(matchers) {
		infix fun String.jsonPath(block: JsonPathResultMatchers.() -> ResultMatcher) {
			matchers.andExpect(MockMvcResultMatchers.jsonPath(this).block())
		}

		infix fun String.jsonPathIs(value: Any?) {
			matchers.andExpect(MockMvcResultMatchers.jsonPath(this, CoreMatchers.`is`(value)))
		}


		fun <T> model(name: String, modelInit: T.() -> Unit) {
			matchers.andDo { mvcResult ->
				val model = mvcResult.modelAndView?.model?.get(name) as T?
				model?.modelInit() ?: throw AssertionError("Model attribute $name was not found")
			}
		}
	}


	@Test
	fun `hello json`() {
		val name = "Petr"
		mockMvc.get("/hello/$name").andExpectCustom(::ClintMatchers) {

			content { json("""{"surname":"Petr"}""", false) }  //JsonAssert support (non-strict is the default)
			"$.surname" jsonPathIs name //JsonPath

			"surname" jsonPath { value("Petr")}
			jsonPath("surname") { value("Petr") }

			match(HandlerMethod("helloJson"))
		}
	}

	@Test
	fun helloGet() {
		mockMvc.get("/hello?name=Petr") andDo {
			handle(MockMvcResultHandlers.print())
		} andExpect {

			match(status().isOk)

			content { contentTypeCompatibleWith(MediaType.TEXT_HTML) }
			view { name("hello") }

			model {
				size<Any>(1)
				attribute("name", "Petr")
			}

			xpath("//h1") {
				nodeCount(1)
			}

			xpath("""//span[@class="name"]""") {
				nodeCount(1)
				string("Petr")
			}
		} andHandle {
			response.contentAsString.contains("Hello world")
		}
	}

	@Test
	fun `test get page with a bunch of andExpect blocks`() = mockMvc.get("/hello?name={0}", "Petr")
			.andExpect { status { isOk } }
			.andExpect { content { contentTypeCompatibleWith(MediaType.TEXT_HTML) } }
			.andExpect { view { name("hello") } }

			.andExpect {
				model {
					size<Any>(1)
					attribute("name", "Petr")
				}
			}

			.andExpect {
				xpath("//h1") {
					nodeCount(1)
				}
			}

			.andExpect {
				xpath("""//span[@class="name"]""") {
					nodeCount(1)
					string("Petr")
				}
			}

	@Test
	fun `test post page`() = mockMvc.post("/hello") {
		contentType = MediaType.APPLICATION_FORM_URLENCODED
		param("surname", "Balat")
	} andExpect {
		xpath("//h1") {
			nodeCount(1)
		}
		xpath("""//span[@class="name"]""") {
			nodeCount(1)
			string("Balat")
		}

//		expectCustom(::ClintMatchers)
//		{
//			model<HelloPostDto>("helloPostDto") {
//				assertEquals("Balat", surname)
//			}
//		}
	}


	@Test
	fun `hello put with required parameters of method and url`() {
		mockMvc.put("/hello") {
			contentType = MediaType.APPLICATION_JSON
			content = """{"surname": "Jack"}"""
			cookie(Cookie("cookieName", "Extra Things"))
		} andDo {
			print()
		} andExpect {
			status { isBadRequest }
			content { json("""{"surname":"Jack"}""") }
			content { json("""{"surname":"Jack", "extraName":"Extra Things"}""", true) }
		}
//				expectCustom({ ClintMatchers(it) }) { "$.surname" jsonPathIs "Jack" } //builder,actions, and expects can be called multiple times
	}

	@Test
	fun `minimal call, builder, and expectation`() = mockMvc.get("/hello") {
		param("name", "world")
	} andExpect {
		status { isOk }
	}

}

/** Silly example matcher to demonstrate how to add custom matchers that aren't in the DSL using unary operator */
class HandlerMethod(private val name: String) : ResultMatcher {

	override fun match(result: MvcResult) {
		val handler = result.handler
		if (handler is org.springframework.web.method.HandlerMethod) {
			AssertionErrors.assertEquals("Handler name", name, handler.method.name)
		}
	}
}
