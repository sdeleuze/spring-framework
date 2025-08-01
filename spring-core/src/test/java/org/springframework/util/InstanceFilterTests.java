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

package org.springframework.util;

import java.util.List;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Stephane Nicoll
 */
class InstanceFilterTests {

	@Test
	void emptyFilterApplyMatchIfEmpty() {
		InstanceFilter<String> filter = new InstanceFilter<>(null, null);
		match(filter, "foo");
		match(filter, "bar");
	}

	@Test
	void includesFilter() {
		InstanceFilter<String> filter = new InstanceFilter<>(List.of("First", "Second"), null);
		match(filter, "Second");
		doNotMatch(filter, "foo");
	}

	@Test
	void excludesFilter() {
		InstanceFilter<String> filter = new InstanceFilter<>(null, List.of("First", "Second"));
		doNotMatch(filter, "Second");
		match(filter, "foo");
	}

	@Test
	void includesAndExcludesFilters() {
		InstanceFilter<String> filter = new InstanceFilter<>(List.of("foo", "Bar"), List.of("First", "Second"));
		doNotMatch(filter, "Second");
		match(filter, "foo");
	}

	@Test
	void includesAndExcludesFiltersConflict() {
		InstanceFilter<String> filter = new InstanceFilter<>(List.of("First"), List.of("First"));
		doNotMatch(filter, "First");
	}


	private static <T> void match(InstanceFilter<T> filter, T candidate) {
		assertThat(filter.match(candidate)).as("filter '" + filter + "' should match " + candidate).isTrue();
	}

	private static <T> void doNotMatch(InstanceFilter<T> filter, T candidate) {
		assertThat(filter.match(candidate)).as("filter '" + filter + "' should not match " + candidate).isFalse();
	}

}
