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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class VersionParserTest {

	@Test
	public void testPlain() {
		assertVersion(Version.parseVersion("1"), 1, null, null, null);
		assertVersion(Version.parseVersion("1.0"), 1, 0, null, null);
		assertVersion(Version.parseVersion("1.0.0"), 1, 0, 0, null);
		assertVersion(Version.parseVersion("1.0.0.0"), 1, 0, 0, "0");
		assertVersion(Version.parseVersion("1.0.0.0.0"), 1, 0, 0, "0.0");
	}

	@Test
	public void testPlainSnapshot() {
		assertVersion(Version.parseVersion("1-SNAPSHOT"), 1, null, null, "SNAPSHOT");
		assertVersion(Version.parseVersion("1.0-SNAPSHOT"), 1, 0, null, "SNAPSHOT");
		assertVersion(Version.parseVersion("1.0.0-SNAPSHOT"), 1, 0, 0, "SNAPSHOT");
		assertVersion(Version.parseVersion("1.0.0.0-SNAPSHOT"), 1, 0, 0, "0-SNAPSHOT");
	}

	@Test
	public void testEarlyString() {
		assertVersion(Version.parseVersion("1.Final"), 1, null, null, "Final");
	}

	@Test
	public void testInvalid() {
		assertVersion(Version.parseVersion("-1"), null, null, null, "1");
	}

	protected static void assertVersion(final Version actual, final Integer major, final Integer minor,
			final Integer micro, final String qualifier) {

		assertEquals(major, actual.getMajor());
		assertEquals(minor, actual.getMinor());
		assertEquals(micro, actual.getMicro());
		assertEquals(qualifier, actual.getQualifier());

	}

}
