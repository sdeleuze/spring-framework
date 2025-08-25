/*
 * Copyright 2002-present the original author or authors.
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

package org.springframework.aot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.springframework.aot.generate.GeneratedArtifact;
import org.springframework.core.NativeDetector;
import org.springframework.core.NativeDetector.Context;
import org.springframework.core.SpringProperties;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

/**
 * Utility for determining if AOT-processed optimizations must be used rather
 * than the regular runtime. Strictly for internal use within the framework.
 *
 * @author Stephane Nicoll
 * @author Sebastien Deleuze
 * @since 6.0
 */
public abstract class AotDetector {

	/**
	 * System property that indicates the application should run with AOT
	 * generated artifacts. If such optimizations are not available, it is
	 * recommended to throw an exception rather than fall back to the regular
	 * runtime behavior.
	 */
	public static final String AOT_ENABLED = "spring.aot.enabled";

	public static final String AOT_ARTIFACTS = "spring.aot.artifacts";

	private static final boolean inNativeImage = NativeDetector.inNativeImage(Context.RUN, Context.BUILD);

	private static final List<GeneratedArtifact> generatedArtifacts;

	static {
		ClassPathResource resource = new ClassPathResource("META-INF/spring/aot.properties");
		List<GeneratedArtifact> artifacts = new ArrayList<>(GeneratedArtifact.values().length);
		if (resource.exists()) {
			try {
				Properties properties = PropertiesLoaderUtils.loadAllProperties("META-INF/spring/aot.properties", AotDetector.class.getClassLoader());
				if (properties.containsKey(AOT_ARTIFACTS)) {
					String[] values = ((String)properties.get(AOT_ARTIFACTS)).split(",");
					for (String value : values) {
						artifacts.add(GeneratedArtifact.valueOf(value));
					}
				}
			}
			catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		generatedArtifacts = Collections.unmodifiableList(artifacts);
	}

	/**
	 * Determine whether bean registration AOT optimizations must be considered at runtime.
	 * This is mandatory in a native image but can be triggered on the JVM using the
	 * {@value #AOT_ENABLED} Spring property.
	 * <p>This is equivalent to {@code useGeneratedArtifacts(ArtifactType.BEAN_REGISTRATION)}.
	 * @return whether AOT optimizations must be considered
	 */
	public static boolean useGeneratedArtifacts() {
		return useGeneratedArtifacts(GeneratedArtifact.BEAN_REGISTRATION);
	}

	public static boolean useGeneratedArtifacts(GeneratedArtifact generatedArtifact) {
		return switch (generatedArtifact) {
			case BEAN_REGISTRATION -> AotDetector.generatedArtifacts.contains(GeneratedArtifact.BEAN_REGISTRATION)
					&& (inNativeImage || SpringProperties.getFlag(AOT_ENABLED));
			case PREDEFINED_CLASSES -> AotDetector.generatedArtifacts.contains(GeneratedArtifact.PREDEFINED_CLASSES);
			case CLASSPATH_INDEXES -> AotDetector.generatedArtifacts.contains(GeneratedArtifact.CLASSPATH_INDEXES);
			case REACHABILITY_METADATA -> AotDetector.generatedArtifacts.contains(GeneratedArtifact.REACHABILITY_METADATA);
		};
	}

}
