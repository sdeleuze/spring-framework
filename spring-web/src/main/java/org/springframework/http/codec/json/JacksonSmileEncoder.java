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

package org.springframework.http.codec.json;

import java.util.Collections;
import java.util.List;
import java.util.ServiceLoader;

import org.jspecify.annotations.Nullable;
import reactor.core.publisher.Flux;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.cfg.MapperBuilder;
import tools.jackson.dataformat.smile.SmileMapper;

import org.springframework.http.MediaType;
import org.springframework.http.codec.AbstractJacksonEncoder;
import org.springframework.util.MimeType;

/**
 * Encode from an {@code Object} stream to a byte stream of Smile objects using Jackson 3.x.
 * For non-streaming use cases, {@link Flux} elements are collected into a {@link List}
 * before serialization for performance reason.
 *
 * @author Sebastien Deleuze
 * @since 7.0
 * @see JacksonSmileDecoder
 */
public class JacksonSmileEncoder extends AbstractJacksonEncoder {

	private static final MimeType[] DEFAULT_SMILE_MIME_TYPES = new MimeType[] {
			new MimeType("application", "x-jackson-smile"),
			new MimeType("application", "*+x-jackson-smile")};

	private static final byte[] STREAM_SEPARATOR = new byte[0];


	/**
	 * Construct a new {@link JacksonSmileEncoder} loading {@link tools.jackson.databind.JacksonModule}s
	 * using JDK {@link ServiceLoader} facility.
	 * @see MapperBuilder#findModules(ClassLoader)
	 */
	public JacksonSmileEncoder() {
		this(SmileMapper.builder()
				// TODO https://github.com/FasterXML/jackson-databind/issues/5112
				.addModules(MapperBuilder.findModules(JacksonSmileEncoder.class.getClassLoader()))
				// TODO https://github.com/FasterXML/jackson-databind/issues/1484#issuecomment-2834961559
				.disable(MapperFeature.DEFAULT_VIEW_INCLUSION)
				// TODO https://github.com/FasterXML/jackson-databind/issues/5133
				.disable(DeserializationFeature.FAIL_ON_UNEXPECTED_VIEW_PROPERTIES)
				.build());
	}

	/**
	 * Construct a new {@link JacksonSmileEncoder} with a custom {@link SmileMapper}.
	 * You can use {@link SmileMapper#builder()} to build it easily.
	 */
	public JacksonSmileEncoder(SmileMapper mapper) {
		super(mapper, DEFAULT_SMILE_MIME_TYPES);
		setStreamingMediaTypes(Collections.singletonList(new MediaType("application", "stream+x-jackson-smile")));
	}

	/**
	 * Construct a new {@link JacksonSmileEncoder} with custom {@link SmileMapper} and {@link MimeType}s.
	 * You can use {@link SmileMapper#builder()} to build it easily.
	 */
	public JacksonSmileEncoder(SmileMapper mapper, MimeType... mimeTypes) {
		super(mapper, mimeTypes);
		setStreamingMediaTypes(Collections.singletonList(new MediaType("application", "stream+x-jackson-smile")));
	}


	/**
	 * Return the separator to use for the given mime type.
	 * <p>By default, this method returns a single byte 0 if the given
	 * mime type is one of the configured {@link #setStreamingMediaTypes(List)
	 * streaming} mime types.
	 */
	@Override
	protected byte @Nullable [] getStreamingMediaTypeSeparator(@Nullable MimeType mimeType) {
		for (MediaType streamingMediaType : getStreamingMediaTypes()) {
			if (streamingMediaType.isCompatibleWith(mimeType)) {
				return STREAM_SEPARATOR;
			}
		}
		return null;
	}
}
