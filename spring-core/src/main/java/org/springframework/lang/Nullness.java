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
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.function.Predicate;

import kotlin.Unit;
import kotlin.reflect.KFunction;
import kotlin.reflect.KParameter;
import kotlin.reflect.jvm.ReflectJvmMapping;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.NullUnmarked;
import org.jspecify.annotations.Nullable;

import org.springframework.core.KotlinDetector;
import org.springframework.core.MethodParameter;
import org.springframework.core.ResolvableType;
import org.springframework.util.Assert;

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
	 * Will not include null (Kotlin and JSpecify `@NullMarked` defaults).
	 */
	NON_NULL;

	public Nullness forMethodReturnType(Method method) {
		// Check Kotlin nullness
		if (KotlinDetector.isKotlinReflectPresent() && KotlinDetector.) {
			return (MethodParameter.KotlinDelegate.isNullable(this) ? Nullness.NULLABLE : Nullness.NON_NULL);
		}
		return Nullness.UNSPECIFIED;
	}

	public Nullness forParameter(Parameter parameter) {
		return Nullness.UNSPECIFIED;
	}

	public Nullness forMethodParameter(MethodParameter methodParameter) {
		return Nullness.UNSPECIFIED;
	}

	public Nullness forAnnotatedType(AnnotatedType annotatedType) {

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

	/**
	 * Inner class to avoid a hard dependency on Kotlin at runtime.
	 */
	private static class KotlinDelegate {


		public static Nullness forMethodReturnType(Method method) {
			KFunction<?> function = ReflectJvmMapping.getKotlinFunction(method);
			Assert.notNull();

		}

		/**
		 * Check whether the specified {@link MethodParameter} represents a nullable Kotlin type,
		 * an optional parameter (with a default value in the Kotlin declaration) or a
		 * {@code Continuation} parameter used in suspending functions.
		 */
		public static boolean isOptional(MethodParameter param) {
			Method method = param.getMethod();
			int index = param.getParameterIndex();
			if (method != null && index == -1) {
				KFunction<?> function = ReflectJvmMapping.getKotlinFunction(method);
				return (function != null && function.getReturnType().isMarkedNullable());
			}
			KFunction<?> function;
			Predicate<KParameter> predicate;
			if (method != null) {
				if (param.getParameterType().getName().equals("kotlin.coroutines.Continuation")) {
					return true;
				}
				function = ReflectJvmMapping.getKotlinFunction(method);
				predicate = p -> KParameter.Kind.VALUE.equals(p.getKind());
			}
			else {
				Constructor<?> ctor = param.getConstructor();
				Assert.state(ctor != null, "Neither method nor constructor found");
				function = ReflectJvmMapping.getKotlinFunction(ctor);
				predicate = p -> (KParameter.Kind.VALUE.equals(p.getKind()) ||
						KParameter.Kind.INSTANCE.equals(p.getKind()));
			}
			if (function != null) {
				int i = 0;
				for (KParameter kParameter : function.getParameters()) {
					if (predicate.test(kParameter)) {
						if (index == i++) {
							return (kParameter.getType().isMarkedNullable() || kParameter.isOptional());
						}
					}
				}
			}
			return false;
		}

		/**
		 * Return the generic return type of the method, with support of suspending
		 * functions via Kotlin reflection.
		 */
		private static Type getGenericReturnType(Method method) {
			try {
				KFunction<?> function = ReflectJvmMapping.getKotlinFunction(method);
				if (function != null && function.isSuspend()) {
					return ReflectJvmMapping.getJavaType(function.getReturnType());
				}
			}
			catch (UnsupportedOperationException ex) {
				// probably a synthetic class - let's use java reflection instead
			}
			return method.getGenericReturnType();
		}

		/**
		 * Return the return type of the method, with support of suspending
		 * functions via Kotlin reflection.
		 */
		private static Class<?> getReturnType(Method method) {
			try {
				KFunction<?> function = ReflectJvmMapping.getKotlinFunction(method);
				if (function != null && function.isSuspend()) {
					Type paramType = ReflectJvmMapping.getJavaType(function.getReturnType());
					if (paramType == Unit.class) {
						paramType = void.class;
					}
					return ResolvableType.forType(paramType).resolve(method.getReturnType());
				}
			}
			catch (UnsupportedOperationException ex) {
				// probably a synthetic class - let's use java reflection instead
			}
			return method.getReturnType();
		}
	}

}
