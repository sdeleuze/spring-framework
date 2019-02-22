package org.springframework.test.web.servlet

class ResultActionsWrapper(private val actions: ResultActions) {

	infix fun andExpect(dsl: MockMvcResultMatchersDsl.() -> Unit): ResultActionsWrapper {
		MockMvcResultMatchersDsl(actions).dsl()
		return this
	}

	infix fun andDo(dsl: MockMvcResultHandlersDsl.() -> Unit): ResultActionsWrapper {
		MockMvcResultHandlersDsl(actions).dsl()
		return this
	}

	fun <T : MockMvcResultMatchersDsl> andExpectCustom(matcher: (ResultActions)->T,dsl: T.() -> Unit): ResultActionsWrapper {
		matcher(actions).dsl()
		return this
	}

	fun andReturn() = actions.andReturn()
}