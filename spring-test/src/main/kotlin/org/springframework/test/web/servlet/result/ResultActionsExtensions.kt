package org.springframework.test.web.servlet.result

import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.MockMvcResultHandlersDsl

fun ResultActions.expect(dsl: MockMvcResultHandlersDsl.() -> Unit): MvcResult
		= MockMvcResultHandlersDsl(this).apply(dsl).execute()