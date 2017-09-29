/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2017 Joern Huxhorn
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
import java.util.ArrayList;
import java.util.List;

@Parameters(commandDescription = "Tail the given file.")
@SuppressWarnings("PMD.ShortClassName")
public class Tail
{
	public static final String NAME = "tail";

	@Parameter(names = { "-f", "--keep-running" }, description = "keep tailing the given Lilith logfile.")
	public boolean keepRunning = false;

	@Parameter(names = { "-n", "--number-of-lines" }, description = "number of entries printed by 'tail'.")
	public Integer numberOfLines = 20;

	@Parameter(names = { "-p", "--pattern" }, description = "pattern used by 'tail'. See http://logback.qos.ch/manual/layouts.html#ClassicPatternLayout and http://logback.qos.ch/manual/layouts.html#AccessPatternLayout")
	public String pattern;

	@Parameter(description = "'tail' the given Lilith logfile.")
	public List<String> files=new ArrayList<>();
}
