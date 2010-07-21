/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2010 Joern Huxhorn
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
package de.huxhorn.lilith;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.converters.FileConverter;

import java.io.File;

public class CommandLineArgs
{
	@Parameter(names = { "-v", "--verbose"  }, description = "print verbose log messages.")
	public boolean verbose = false;

	@Parameter(names = { "-h", "--help"  }, description = "show this help.")
	public boolean showHelp = false;

	@Parameter(names = { "-F", "--flush-preferences"  }, description = "flush gui preferences.")
	public boolean flushPreferences = false;

	@Parameter(names = { "-L", "--flush-licensed"  }, description = "flush licensed.")
	public boolean flushLicensed = false;

	@Parameter(names = { "-I", "--index-file"  }, description = "indexes the given file.")
	public String indexFile;

	@Parameter(names = { "-b", "--bonjour"  }, description = "enable Bonjour.")
	public boolean enableBonjour = false;

	@Parameter(names = { "-m", "--md5"  }, description = "create an MD5 checksum for the given file.", converter = FileConverter.class)
	public File md5File;

	@Parameter(names = { "-c", "--cat"  }, description = "'cat' the given Lilith logfile.", converter = FileConverter.class)
	public File catFile;

	@Parameter(names = { "-t", "--tail"  }, description = "'tail' the given Lilith logfile.", converter = FileConverter.class)
	public File tailFile;

	@Parameter(names = { "-p", "--pattern"  }, description = "pattern used by 'cat' or 'tail'.")
	public String pattern;

	@Parameter(names = { "-f", "--keep-running"  }, description = "keep tailing the given Lilith logfile.")
	public boolean keepRunning = false;

	@Parameter(names = { "-n", "--number-of-lines"  }, description = "number of entries printed by cat or tail")
	public Integer numberOfLines = -1;

	@Parameter(names = { "-e", "--export-preferences"  }, description = "export preferences into the given file.")
	public String exportPreferencesFile;

	@Parameter(names = { "-i", "--import-preferences"  }, description = "import preferences from the given file.")
	public String importPreferencesFile;
}
