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

package org.springframework.http.converter;

import java.io.IOException;
import java.util.Map;

import org.springframework.core.ResolvableType;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;

/**
 * A specialization of {@link HttpMessageConverter} that can convert an HTTP request
 * into a target object of a specified {@link ResolvableType} and a source object of
 * a specified {@link ResolvableType} into an HTTP response with optional hints.
 *
 * @author Sebastien Deleuze
 * @since 6.2
 * @param <T> the converted object type
 */
public interface SmartHttpMessageConverter<T> extends HttpMessageConverter<T> {

	boolean canRead(ResolvableType type, @Nullable MediaType mediaType);

	boolean canWrite(ResolvableType targetType, Class<?> clazz, @Nullable MediaType mediaType);

	T read(ResolvableType type, HttpInputMessage inputMessage, @Nullable Map<String, Object> hints)
			throws IOException, HttpMessageNotReadableException;

	void write(T t, ResolvableType type, @Nullable MediaType contentType, HttpOutputMessage outputMessage,
			@Nullable Map<String, Object> hints) throws IOException, HttpMessageNotWritableException;

	@Override
	default boolean canRead(Class<?> clazz, @Nullable MediaType mediaType) {
		return canRead(ResolvableType.forClass(clazz), mediaType);
	}

	@Override
	default boolean canWrite(Class<?> clazz, @Nullable MediaType mediaType) {
		return canWrite(ResolvableType.forClass(clazz), clazz, mediaType);
	}

	@Override
	default T read(Class<? extends T> clazz, HttpInputMessage inputMessage)
			throws IOException, HttpMessageNotReadableException {
		return read(ResolvableType.forClass(clazz), inputMessage, null);
	}

	@Override
	default void write(T t, @Nullable MediaType contentType, HttpOutputMessage outputMessage)
			throws IOException, HttpMessageNotWritableException {
		write(t, ResolvableType.forInstance(t), contentType, outputMessage, null);
	}
}
