/*
 * Copyright 2002-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.beans.factory;

import org.springframework.core.env.Environment;

/**
 * Contract for registering programmatically beans when
 * processing {@code @Configuration} classes.
 *
 * <p>Along with {@code @Configuration}, classes of this type may
 * be provided to the {@code @Import} annotation.
 *
 * @author Sebastien Deleuze
 * @since 7.0
 */
@FunctionalInterface
public interface BeanRegistrar {

	/**
	 * Register beans in a programmatic and functional way.
	 * @param registry the bean registry
	 * @param env the environment that can be used to get the active profile or some properties
	 */
	void register(BeanRegistry registry, Environment env);
}
