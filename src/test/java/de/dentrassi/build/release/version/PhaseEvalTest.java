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

import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Test;

public class PhaseEvalTest {
	@Test
	public void test1() {
		final PhaseEvaluator pe = new PhaseEvaluator();

		pe.setSnapshotPhase("0");
		pe.setDefaultPhase("1");

		assertEval(pe, "1", "1");
		assertEval(pe, "1", "1.0");
		assertEval(pe, "1", "1.0.0");
		assertEval(pe, "1", "1.0.0.0");
		assertEval(pe, "1", "1.0.0.0.0");

		assertEval(pe, "0", "1-SNAPSHOT");
		assertEval(pe, "0", "1.0-SNAPSHOT");
		assertEval(pe, "0", "1.0.0-SNAPSHOT");
		assertEval(pe, "0", "1.0.0.0-SNAPSHOT");
		assertEval(pe, "0", "1.0.0.0.0-SNAPSHOT");
	}

	@Test
	public void test2() {
		final PhaseEvaluator pe = new PhaseEvaluator();

		pe.setSnapshotPhase("0");
		pe.setDefaultPhase("2");
		pe.setDefaultPhase("3");

		pe.addRule("1", Pattern.compile("^alpha-[0-9]+$"));
		pe.addRule("2.$1", Pattern.compile("^beta-([0-9]+)$"));

		assertEval(pe, "0", "1.0.0-SNAPSHOT");
		assertEval(pe, "1", "1.0.0-alpha-1");
		assertEval(pe, "2.5", "1.0.0-beta-5");
		assertEval(pe, "3", "1.0.0");
	}

	private static void assertEval(final PhaseEvaluator pe, final String phase, final String version) {
		Assert.assertEquals(phase, pe.eval(Version.parseVersion(version)));
	}
}
