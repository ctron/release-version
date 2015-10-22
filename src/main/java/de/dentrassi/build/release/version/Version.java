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

public class Version {

	private final Integer major;
	private final Integer minor;
	private final Integer micro;
	private final String qualifier;

	public Version(final Integer major, final Integer minor, final Integer micro, final String qualifier) {
		this.major = major;
		this.minor = minor;
		this.micro = micro;
		this.qualifier = qualifier;
	}

	public Integer getMajor() {
		return this.major;
	}

	public Integer getMinor() {
		return this.minor;
	}

	public Integer getMicro() {
		return this.micro;
	}

	public String getQualifier() {
		return this.qualifier;
	}

	public int getMajorOrZero() {
		return this.major != null ? this.major : 0;
	}

	public int getMinorOrZero() {
		return this.minor != null ? this.minor : 0;
	}

	public int getMicroOrZero() {
		return this.micro != null ? this.micro : 0;
	}

	public static Version parseVersion(final String version) {
		boolean qualifier = false;

		StringBuilder sb = new StringBuilder();
		Integer major = null;
		Integer minor = null;
		Integer micro = null;

		int tok = 0;

		for (int i = 0; i < version.length(); i++) {
			final char c = version.charAt(i);

			if (!qualifier) {
				switch (c) {
				case '.':
					switch (tok) {
					case 0:
						major = toInteger(sb);
						break;
					case 1:
						minor = toInteger(sb);
						break;
					case 2:
						micro = toInteger(sb);
						break;
					}
					tok++;

					sb = new StringBuilder();
					if (tok >= 3) {
						qualifier = true;
					}
					break;

				case '0':
				case '1':
				case '2':
				case '3':
				case '4':
				case '5':
				case '6':
				case '7':
				case '8':
				case '9':
					sb.append(c);
					break;

				default:
					switch (tok) {
					case 0:
						major = toInteger(sb);
						break;
					case 1:
						minor = toInteger(sb);
						break;
					case 2:
						micro = toInteger(sb);
						break;
					}
					tok++;
					sb = new StringBuilder();
					if (c != '-') {
						// if it is not a dash, add it to the qualifier
						sb.append(c);
					}
					qualifier = true;
					break;

				}
			} else {
				sb.append(c);
			}
		}

		String qualifierString = null;

		if (qualifier) {

			if (sb.length() > 0) {
				qualifierString = sb.toString();
			}
		} else {
			switch (tok) {
			case 0:
				major = toInteger(sb);
				break;
			case 1:
				minor = toInteger(sb);
				break;
			case 2:
				micro = toInteger(sb);
				break;
			}
		}

		return new Version(major, minor, micro, qualifierString);
	}

	private static Integer toInteger(final StringBuilder sb) {
		if (sb.length() <= 0) {
			return null;
		}
		return Integer.parseInt(sb.toString());
	}

	public boolean isSnapshot() {
		if (this.qualifier == null) {
			return false;
		}

		return this.qualifier.endsWith("SNAPSHOT");
	}
}
