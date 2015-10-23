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

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

/**
 * Parse the project version and evaluate a release phase from it.
 *
 * <p>
 * The main idea behind this plugin is to create an RPM release number from the
 * version. With {@code -SNAPSHOT} builds having the lowest priority (
 * <q>0</q>).
 * </p>
 *
 * <p>
 * The default setup is that {@code -SNAPSHOT} builds evaluates to
 * <q>0</q> where all other versions evaluate to
 * <q>1</q>.
 * </p>
 *
 * <p>
 * Adding rule entries allows to customize this to evaluate e.g. {@code -m5} to
 * <q>0.m5</q> or
 * <q>0.5</q> for example.
 * </p>
 *
 * <p>
 * Rules are regular expressions which match against the qualifier and return a
 * phase which gets pattern references (e.g.
 * <q>$1</q>) replaced with the matched groups.
 *
 * </p>
 *
 * @author ctron
 *
 */
@Mojo(name = "phase", defaultPhase = LifecyclePhase.VALIDATE, requiresProject = true, threadSafe = true)
public class ReleasePhaseMojo extends AbstractMojo {

	/**
	 * The maven project
	 */
	@Parameter(property = "project", readonly = true, required = true)
	protected MavenProject project;

	/**
	 * The version string to process
	 */
	@Parameter(defaultValue = "${project.version}")
	private String version;

	/**
	 * The the the prefix of the output properties
	 */
	@Parameter(defaultValue = "releasePhase")
	private String prefix;

	/**
	 * The value for the phase when no other rule matches
	 */
	@Parameter(defaultValue = "1")
	private String defaultPhase;

	/**
	 * The value for the phase when this is a snapshot build
	 */
	@Parameter(defaultValue = "0")
	private String snapshotPhase;

	/**
	 * The version matching rules for evaluating a phase.
	 * <p>
	 * Each rule entry is a regular expression pattern, if it matches the
	 * qualifier the provided
	 * <q>phase</q> will be expanded with the regular expression groups and
	 * returned.
	 * </p>
	 * <p>
	 * Assume the pattern is {@code m([0-9]+)} and the phase is {@code 0.$1}
	 * then for the input version {@code 1.0.0-m5} the result will be
	 * {@code 0.5}.
	 * </p>
	 */
	@Parameter
	private List<PhaseRule> rules = new LinkedList<>();

	public void setRules(final List<PhaseRule> rules) {
		this.rules = rules;
	}

	public void setProject(final MavenProject project) {
		this.project = project;
	}

	public void setVersion(final String version) {
		this.version = version;
	}

	public void setSnapshotPhase(final String snapshotPhase) {
		this.snapshotPhase = snapshotPhase;
	}

	public void setDefaultPhase(final String defaultPhase) {
		this.defaultPhase = defaultPhase;
	}

	public void setPrefix(final String prefix) {
		this.prefix = prefix;
	}

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		final Version v = Version.parseVersion(this.version);

		final PhaseEvaluator pe = new PhaseEvaluator();

		pe.setDefaultPhase(this.defaultPhase);
		pe.setSnapshotPhase(this.snapshotPhase);

		for (final PhaseRule rule : this.rules) {
			pe.addRule(rule.getPhase(), Pattern.compile(rule.getPattern()), rule.getPriority());
		}

		final String phase = pe.eval(v);

		getLog().info("The relase phase is: " + phase);

		setProperty("phase", phase);
	}

	private void setProperty(final String name, final String value) {
		if (value == null) {
			return;
		}

		final String key;

		if (this.prefix != null && !this.prefix.isEmpty()) {
			key = this.prefix + "." + name;
		} else {
			key = name;
		}

		getLog().debug(String.format("Setting property '%s' = '%s'", key, value));

		this.project.getProperties().put(key, value);
	}

}
