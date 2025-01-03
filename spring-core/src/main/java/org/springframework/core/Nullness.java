/*
 * Copyright 2002-2024 the original author or authors.
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

package org.springframework.core;

/**
 * Constants that indicate the nullness, typically for type usage defined with annotations
 * like <a href="https://jspecify.dev/">JSpecify</a> in Java or
 * <a href="https://kotlinlang.org/docs/null-safety.html">with Kotlin type system</a>.
 *
 * @author Sebastien Deleuze
 * @since 7.0
 */
public enum Nullness {

	/**
	 * Unspecified nullness (Java default).
	 */
	UNSPECIFIED,

	/**
	 * Can include null.
	 */
	NULLABLE,

	/**
	 * Will not include null (Kotlin default).
	 */
	NON_NULL
}
