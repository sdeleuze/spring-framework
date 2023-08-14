/*
 * Copyright 2002-2023 the original author or authors.
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

package org.springframework.orm.jpa.vendor;

import org.springframework.aot.hint.ReflectionHints;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.aot.hint.TypeReference;
import org.springframework.util.ClassUtils;

public class SpringServiceInitiatorsRuntimeHints implements RuntimeHintsRegistrar {

	@Override
	public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
		ReflectionHints reflectionHints = hints.reflection();
		for (String className : new String[] {
				SpringServiceInitiators.JSI_CLASS_NAME,
				SpringServiceInitiators.QTFI_CLASS_NAME,
				SpringServiceInitiators.MIGFI_CLASS_NAME,
				SpringServiceInitiators.SIGFI_CLASS_NAME,
				SpringServiceInitiators.SSEI_CLASS_NAME,
				SpringServiceInitiators.ISCEI_CLASS_NAME,
				SpringServiceInitiators.MESI_CLASS_NAME,
				SpringServiceInitiators.SSLI_CLASS_NAME,
				SpringServiceInitiators.JVMPPI_CLASS_NAME,
				SpringServiceInitiators.SMTMSPI_CLASS_NAME,
				SpringServiceInitiators.PMSI_CLASS_NAME,
				SpringServiceInitiators.BLFI_CLASS_NAME}) {
			if (ClassUtils.isPresent(className, classLoader)) {
				reflectionHints.registerType(TypeReference.of(className),
						builder -> builder.withField(SpringServiceInitiators.SSI_FIELD_NAME));
			}
		}
	}

}
