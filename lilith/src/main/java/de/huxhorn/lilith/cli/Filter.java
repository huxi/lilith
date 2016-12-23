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
import com.beust.jcommander.Parameters;

@Parameters(commandDescription = "Filter the given file.")
public class Filter
{
	public static final String NAME = "filter";

	@Parameter(names = { "-f", "--keep-running" }, description = "keep filtering the given Lilith logfile.")
	public boolean keepRunning = false;

	@Parameter(names = { "-e", "--exclusive" }, description = "open input in exclusive read mode.")
	public boolean exclusive = false;

	@Parameter(names = { "-o", "--overwrite" }, description = "overwrite existing output files.")
	public boolean overwrite = false;

	@Parameter(names = { "-p", "--pattern" }, description = "pattern used by 'filter'. See http://logback.qos.ch/manual/layouts.html#ClassicPatternLayout and http://logback.qos.ch/manual/layouts.html#AccessPatternLayout")
	public String pattern;

	@Parameter(names = { "-in", "--input-file" }, description = "The input file to be filtered.", required = true)
	public String input;

	@Parameter(names = { "-out", "--output-file" }, description = "The output file filtered events are written to..", required = true)
	public String output;

	@Parameter(names = { "-c", "--condition-file" }, description = "The condition file to be used for filtering.", required = true)
	public String condition;

	@Parameter(names = { "-s", "--search-string" }, description = "search string to be used in case of Script (in contrast to Condition instance) in condition file.")
	public String searchString;
}
