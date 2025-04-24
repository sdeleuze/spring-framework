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

package org.springframework.http.converter.xml;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.cfg.MapperBuilder;
import tools.jackson.dataformat.xml.XmlMapper;

import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractJacksonHttpMessageConverter;

/**
 * Implementation of {@link org.springframework.http.converter.HttpMessageConverter HttpMessageConverter}
 * that can read and write XML using <a href="https://github.com/FasterXML/jackson-dataformat-xml">
 * Jackson 3.x extension component for reading and writing XML encoded data</a>.
 *
 * <p>By default, this converter supports {@code application/xml}, {@code text/xml}, and
 * {@code application/*+xml} with {@code UTF-8} character set. This can be overridden by
 * setting the {@link #setSupportedMediaTypes supportedMediaTypes} property.
 *
 * <p>The default constructor loads {@link tools.jackson.databind.JacksonModule}s
 * using JDK {@link java.util.ServiceLoader} facility.
 *
 * <p>The following hint entries are supported:
 * <ul>
 *     <li>A JSON view can be specified with a <code>com.fasterxml.jackson.annotation.JsonView</code> key and the class name of the JSON view as value.</li>
 *     <li>A filter provider can be specified with a <code>tools.jackson.databind.ser.FilterProvider</code> key and the filter provider class name as value.</li>
 * </ul>
 *
 * @author Sebastien Deleuze
 * @since 7.0
 */
public class JacksonXmlHttpMessageConverter extends AbstractJacksonHttpMessageConverter {

	private static final List<MediaType> problemDetailMediaTypes =
			Collections.singletonList(MediaType.APPLICATION_PROBLEM_XML);


	/**
	 * Construct a new {@link JacksonXmlHttpMessageConverter} loading
	 * {@link tools.jackson.databind.JacksonModule}s using JDK {@link java.util.ServiceLoader} facility.
	 * @see MapperBuilder#findModules(ClassLoader)
	 */
	public JacksonXmlHttpMessageConverter() {
		this(XmlMapper.builder()
				// TODO https://github.com/FasterXML/jackson-databind/issues/5112
				.addModules(MapperBuilder.findModules(JacksonXmlHttpMessageConverter.class.getClassLoader()))
				// TODO https://github.com/FasterXML/jackson-databind/issues/1484#issuecomment-2834961559
				.disable(MapperFeature.DEFAULT_VIEW_INCLUSION)
				// TODO https://github.com/FasterXML/jackson-databind/issues/5133
				.disable(DeserializationFeature.FAIL_ON_UNEXPECTED_VIEW_PROPERTIES)
				.build());
	}

	/**
	 * Construct a new {@link JacksonXmlHttpMessageConverter} with a custom {@link XmlMapper}.
	 * You can use {@link XmlMapper#builder()} to build it easily.
	 */
	public JacksonXmlHttpMessageConverter(XmlMapper xmlMapper) {
		super(xmlMapper, new MediaType("application", "xml", StandardCharsets.UTF_8),
				new MediaType("text", "xml", StandardCharsets.UTF_8),
				new MediaType("application", "*+xml", StandardCharsets.UTF_8));
	}

	@Override
	protected List<MediaType> getMediaTypesForProblemDetail() {
		return problemDetailMediaTypes;
	}

}
