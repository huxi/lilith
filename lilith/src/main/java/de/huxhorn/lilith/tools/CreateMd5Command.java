/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2018 Joern Huxhorn
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

package de.huxhorn.lilith.tools;

import de.huxhorn.lilith.swing.ApplicationPreferences;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("PMD.ClassNamingConventions")
public final class CreateMd5Command
{
	static
	{
		new CreateMd5Command(); // stfu
	}

	private CreateMd5Command() {}

	public static boolean createMd5(String inputFileString)
	{
		final Logger logger = LoggerFactory.getLogger(CreateMd5Command.class);

		File input = new File(inputFileString);

		if(!input.isFile())
		{
			if(logger.isWarnEnabled()) logger.warn("{} isn't a file!", input.getAbsolutePath());
			return false;
		}
		File output = new File(input.getParentFile(), input.getName() + ".md5");

		try
		{

			InputStream fis = Files.newInputStream(input.toPath());
			byte[] md5 = ApplicationPreferences.getMD5(fis);
			if(md5 == null)
			{
				if(logger.isWarnEnabled()) logger.warn("Couldn't calculate checksum for {}!", input.getAbsolutePath());
				return false;
			}
			OutputStream fos = Files.newOutputStream(output.toPath());
			fos.write(md5);
			fos.close();
			if(logger.isInfoEnabled())
			{
				logger.info("Wrote checksum of {} to {}.", input.getAbsolutePath(), output.getAbsolutePath());
				logger.info("MD5: {}", Hex.encodeHexString(md5));
			}
		}
		catch(IOException e)
		{
			if(logger.isWarnEnabled()) logger.warn("Exception while creating checksum!", e);
			return false;
		}
		return true;
	}
}
