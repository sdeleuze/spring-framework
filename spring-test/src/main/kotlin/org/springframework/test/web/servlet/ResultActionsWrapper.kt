package org.springframework.test.web.servlet

class ResultActionsWrapper(val actions: ResultActions) {

	infix fun andExpect(dsl: MockMvcResultMatchersDsl.() -> Unit): ResultActionsWrapper {
		MockMvcResultMatchersDsl(actions).dsl()
		return this
	}

	infix fun andDo(dsl: MockMvcResultHandlersDsl.() -> Unit): ResultActionsWrapper {
		MockMvcResultHandlersDsl(actions).dsl()
		return this
	}

	infix fun andHandle(handler:MvcResult.()-> Unit): ResultActionsWrapper {
		actions.andDo { it.handler() }
		return this
	}

	fun andReturn() = actions.andReturn()
}