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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

import org.jspecify.annotations.Nullable;
import reactor.core.publisher.Flux;
import tools.jackson.core.PrettyPrinter;
import tools.jackson.core.util.DefaultIndenter;
import tools.jackson.core.util.DefaultPrettyPrinter;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectWriter;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.cfg.MapperBuilder;
import tools.jackson.databind.json.JsonMapper;

import org.springframework.core.ResolvableType;
import org.springframework.http.MediaType;
import org.springframework.http.codec.AbstractJacksonEncoder;
import org.springframework.util.MimeType;

/**
 * Encode from an {@code Object} stream to a byte stream of JSON objects using
 * <a href="https://github.com/FasterXML/jackson">Jackson 3.x</a>. For non-streaming
 * use cases, {@link Flux} elements are collected into a {@link List} before
 * serialization for performance reason.
 *
 * <p>The default constructor loads {@link tools.jackson.databind.JacksonModule}s
 * using JDK {@link ServiceLoader} facility.
 *
 * @author Sebastien Deleuze
 * @since 7.0
 * @see JacksonJsonDecoder
 */
public class JacksonJsonEncoder extends AbstractJacksonEncoder {

	private static final List<MimeType> problemDetailMimeTypes =
			Collections.singletonList(MediaType.APPLICATION_PROBLEM_JSON);


	private final @Nullable PrettyPrinter ssePrettyPrinter;


	/**
	 * Construct a new {@link JacksonJsonEncoder} loading {@link tools.jackson.databind.JacksonModule}s
	 * using JDK {@link ServiceLoader} facility.
	 * @see MapperBuilder#findModules(ClassLoader)
	 */
	public JacksonJsonEncoder() {
		this(JsonMapper.builder()
				// TODO https://github.com/FasterXML/jackson-databind/issues/5112
				.addModules(MapperBuilder.findModules(JacksonJsonEncoder.class.getClassLoader()))
				// TODO https://github.com/FasterXML/jackson-databind/issues/1484
				.disable(MapperFeature.DEFAULT_VIEW_INCLUSION)
				// TODO https://github.com/FasterXML/jackson-databind/issues/5133
				.disable(DeserializationFeature.FAIL_ON_UNEXPECTED_VIEW_PROPERTIES)
				.build());
	}

	/**
	 * Construct a new {@link JacksonJsonEncoder} with custom {@link ObjectMapper} and {@link MimeType}s.
	 * @see JsonMapper#builder()
	 */
	public JacksonJsonEncoder(ObjectMapper mapper, MimeType... mimeTypes) {
		super(mapper, mimeTypes);
		setStreamingMediaTypes(Arrays.asList(MediaType.APPLICATION_NDJSON));
		this.ssePrettyPrinter = initSsePrettyPrinter();
	}

	private static PrettyPrinter initSsePrettyPrinter() {
		DefaultPrettyPrinter printer = new DefaultPrettyPrinter();
		printer.indentObjectsWith(new DefaultIndenter("  ", "\ndata:"));
		return printer;
	}


	@Override
	protected List<MimeType> getMediaTypesForProblemDetail() {
		return problemDetailMimeTypes;
	}

	@Override
	protected ObjectWriter customizeWriter(ObjectWriter writer, @Nullable MimeType mimeType,
			ResolvableType elementType, @Nullable Map<String, Object> hints) {

		return (this.ssePrettyPrinter != null &&
				MediaType.TEXT_EVENT_STREAM.isCompatibleWith(mimeType) &&
				writer.getConfig().isEnabled(SerializationFeature.INDENT_OUTPUT) ?
				writer.with(this.ssePrettyPrinter) : writer);
	}

}
