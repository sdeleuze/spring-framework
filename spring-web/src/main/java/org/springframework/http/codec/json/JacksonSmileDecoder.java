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

import java.util.ServiceLoader;

import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.cfg.MapperBuilder;
import tools.jackson.dataformat.smile.SmileMapper;

import org.springframework.http.codec.AbstractJacksonDecoder;
import org.springframework.util.MimeType;

/**
 * Decode a byte stream into Smile and convert to Object's with Jackson 3.x,
 * leveraging non-blocking parsing.
 *
 * @author Sebastien Deleuze
 * @since 7.0
 * @see JacksonSmileEncoder
 */
public class JacksonSmileDecoder extends AbstractJacksonDecoder {

	private static final MimeType[] DEFAULT_SMILE_MIME_TYPES = new MimeType[] {
					new MimeType("application", "x-jackson-smile"),
					new MimeType("application", "*+x-jackson-smile")};

	/**
	 * Construct a new {@link JacksonSmileDecoder} loading {@link tools.jackson.databind.JacksonModule}s
	 * using JDK {@link ServiceLoader} facility.
	 * @see MapperBuilder#findModules(ClassLoader)
	 */
	public JacksonSmileDecoder() {
		this(SmileMapper.builder()
				// TODO https://github.com/FasterXML/jackson-databind/issues/5112
				.addModules(MapperBuilder.findModules(JacksonSmileDecoder.class.getClassLoader()))
				// TODO https://github.com/FasterXML/jackson-databind/issues/1484#issuecomment-2834961559
				.disable(MapperFeature.DEFAULT_VIEW_INCLUSION)
				// TODO https://github.com/FasterXML/jackson-databind/issues/5133
				.disable(DeserializationFeature.FAIL_ON_UNEXPECTED_VIEW_PROPERTIES)
				.build());
	}

	/**
	 * Construct a new {@link JacksonSmileDecoder} with a custom {@link SmileMapper}.
	 * @see SmileMapper#builder()
	 */
	public JacksonSmileDecoder(SmileMapper mapper) {
		this(mapper, DEFAULT_SMILE_MIME_TYPES);
	}

	/**
	 * Construct a new {@link JacksonSmileDecoder} with custom {@link SmileMapper} and {@link MimeType}s.
	 * @see SmileMapper#builder()
	 */
	public JacksonSmileDecoder(SmileMapper mapper, MimeType... mimeTypes) {
		super(mapper, mimeTypes);
	}

}
