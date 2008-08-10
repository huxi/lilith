/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2008 Joern Huxhorn
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
package de.huxhorn.lilith.debug;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogSkullRunnable
	extends AbstractDebugRunnable
{
	private final Logger logger = LoggerFactory.getLogger(LogStuffRunnable.class);

	public LogSkullRunnable(int delay)
	{
		super(delay);
	}

	public void runIt() throws InterruptedException
	{
		if (logger.isDebugEnabled())
		{
			logger.debug("Skull!\n" +
			"     _.--\"\"\"\"\"--._\n" +
			"   .'             '.\n" +
			"  /                 \\\n" +
			" ;                   ;\n" +
			" |                   |\n" +
			" |                   |\n" +
			" ;                   ;\n" +
			"  \\ (`'--,    ,--'`) /\n" +
			"   \\ \\  _ )  ( _  / /\n" +
			"    ) )(')/  \\(')( (\n" +
			"   (_ `\"\"` /\\ `\"\"` _)\n" +
			"    \\`\"-, /  \\ ,-\"`/\n" +
			"     `\\ / `\"\"` \\ /`\n" +
			"      |/\\/\\/\\/\\/\\|\n" +
			"      |\\        /|\n" +
			"      ; |/\\/\\/\\| ;\n" +
			"       \\`-`--`-`/\n" +
			"        \\      /\n" +
			"         ',__,'\n" +
			"          q__p\n" +
			"          q__p\n" +
			"          q__p\n" +
			"          q__p\n");
		}
	}
}