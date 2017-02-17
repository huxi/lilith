/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2011 Joern Huxhorn
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.huxhorn.lilith.cli;

import com.beust.jcommander.Parameter;

public class CommandLineArgs
{
	@Parameter(names = { "-v", "--verbose" }, description = "print verbose log messages.")
	public boolean verbose = false;

	@Parameter(names = { "-l", "--logback-config" }, description = "use given logback configuration.")
	public String logbackConfig;

	@Parameter(names = { "-h", "-?", "--help" }, description = "show this help.")
	public boolean showHelp = false;

	@Parameter(names = { "-F", "--flush-preferences" }, description = "flush gui preferences.")
	public boolean flushPreferences = false;

	@Parameter(names = { "-L", "--flush-licensed" }, description = "flush licensed.")
	public boolean flushLicensed = false;

	@Parameter(names = { "-e", "--export-preferences" }, description = "export preferences into the given file.")
	public String exportPreferencesFile;

	@Parameter(names = { "-i", "--import-preferences" }, description = "import preferences from the given file.")
	public String importPreferencesFile;

	@Parameter(names = { "-T", "--print-timestamp" }, description = "prints the build timestamp of this Lilith version.")
	public boolean printBuildTimestamp=false;
}
