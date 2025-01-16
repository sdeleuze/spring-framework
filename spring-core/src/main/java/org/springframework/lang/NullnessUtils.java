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

package org.springframework.lang;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Method;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.NullUnmarked;
import org.jspecify.annotations.Nullable;

import org.springframework.core.KotlinDetector;
import org.springframework.core.MethodParameter;

/**
 * Helper class for runtime detection of {@link Nullness nullness}, typically for type usage defined with annotations
 * like <a href="https://jspecify.dev/">JSpecify</a> in Java or
 * <a href="https://kotlinlang.org/docs/null-safety.html">with Kotlin type system</a>.
 *
 * @author Sebastien Deleuze
 * @since 7.0
 */
public abstract class NullnessUtils {

	Nullness forMethodReturnType(Method method) {
		return Nullness.UNSPECIFIED;
	}

	Nullness forMethodParameter(MethodParameter methodParameter) {
		return Nullness.UNSPECIFIED;
	}

	/**
	 * Return the {@link Nullness nullness} of this {@code MethodParameter} based on JSpecify
	 * annotations (at type, method, class or package level), other variants of
	 * {@code @Nullable} annotations or language-level nullable types in Kotlin.
	 * @since 7.0
	 */
	public Nullness getNullness() {
		// Check Kotlin nullness
		if (KotlinDetector.isKotlinReflectPresent() && KotlinDetector.isKotlinType(getContainingClass())) {
			return (MethodParameter.KotlinDelegate.isNullable(this) ? Nullness.NULLABLE : Nullness.NON_NULL);
		}
		// Check @Nullable annotations regardless of the package (cover also Spring and JSR 305 annotations)
		for (Annotation ann : getParameterAnnotations()) {
			if ("Nullable".equals(ann.annotationType().getSimpleName())) {
				return Nullness.NULLABLE;
			}
		}
		// Check JSpecify annotations
		AnnotatedType annotatedType = (this.parameterIndex < 0 ?
				this.executable.getAnnotatedReturnType() :
				this.executable.getAnnotatedParameterTypes()[this.parameterIndex]);
		if (annotatedType.isAnnotationPresent(Nullable.class)) {
			return Nullness.NULLABLE;
		}
		if (annotatedType.isAnnotationPresent(NonNull.class)) {
			return Nullness.NON_NULL;
		}
		return getDefaultNullness();
	}

	private Nullness getDefaultNullness() {
		Nullness nullness = Nullness.UNSPECIFIED;
		// Package level
		Class<?> declaringClass = this.executable.getDeclaringClass();
		Package declaringPackage = declaringClass.getPackage();
		if (declaringPackage.isAnnotationPresent(NullMarked.class)) {
			nullness = Nullness.NON_NULL;
		}
		// Class level
		if (declaringClass.isAnnotationPresent(NullMarked.class)) {
			nullness = Nullness.NON_NULL;
		}
		else if (declaringClass.isAnnotationPresent(NullUnmarked.class)) {
			nullness = Nullness.UNSPECIFIED;
		}
		// Method level
		if (this.executable.isAnnotationPresent(NullMarked.class)) {
			nullness = Nullness.NON_NULL;
		}
		else if (this.executable.isAnnotationPresent(NullUnmarked.class)) {
			nullness = Nullness.UNSPECIFIED;
		}
		return nullness;
	}
}
