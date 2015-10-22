/*
 * Copyright 2015 Jens Reimann.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.dentrassi.build.release.version;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PhaseEvaluator {

	private static class Entry implements Comparable<Entry> {
		private final int priority;
		private final Pattern pattern;
		private final String phase;

		public Entry(final int priority, final Pattern pattern, final String phase) {
			this.priority = priority;
			this.pattern = pattern;
			this.phase = phase;
		}

		@Override
		public int compareTo(final Entry o) {
			return Integer.compare(this.priority, o.priority);
		}
	}

	private String snapshotPhase = "0";
	private String defaultPhase = "1";

	private int maxPriority = 0;

	private final List<Entry> entries = new LinkedList<>();

	public PhaseEvaluator() {
	}

	public void setSnapshotPhase(final String snapshotPhase) {
		this.snapshotPhase = snapshotPhase;
	}

	public void setDefaultPhase(final String defaultPhase) {
		this.defaultPhase = defaultPhase;
	}

	public void addRule(final String phase, final Pattern pattern) {
		addRule(phase, pattern, null);
	}

	public void addRule(final String phase, final Pattern pattern, Integer priority) {
		if (priority == null) {
			priority = this.maxPriority + 1;
		}

		this.maxPriority = Math.max(this.maxPriority, priority);
		this.entries.add(new Entry(priority, pattern, phase));
	}

	public String eval(final Version version) {

		if (version.isSnapshot()) {
			return this.snapshotPhase;
		}

		final String q = version.getQualifier();

		if (q == null) {
			return this.defaultPhase;
		}

		Collections.sort(this.entries);

		for (final Entry entry : this.entries) {
			final Matcher m = entry.pattern.matcher(q);
			if (m.matches()) {
				return m.replaceAll(entry.phase);
			}
		}

		return this.defaultPhase;
	}
}
