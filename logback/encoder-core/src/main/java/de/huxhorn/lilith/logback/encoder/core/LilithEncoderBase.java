/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2017 Joern Huxhorn
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * Copyright 2007-2017 Joern Huxhorn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.huxhorn.lilith.logback.encoder.core;

import ch.qos.logback.core.FileAppender;
import ch.qos.logback.core.OutputStreamAppender;
import ch.qos.logback.core.encoder.EncoderBase;
import ch.qos.logback.core.recovery.ResilientFileOutputStream;
import de.huxhorn.lilith.api.FileConstants;
import de.huxhorn.sulky.codec.filebuffer.DefaultFileHeaderStrategy;
import de.huxhorn.sulky.codec.filebuffer.MetaData;
import de.huxhorn.sulky.codec.filebuffer.MetaDataCodec;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.Objects;

public abstract class LilithEncoderBase<E>
	extends EncoderBase<E>
{
	private static final byte[] EMPTY = new byte[0];
	private final Map<String, String> metaDataMap;
	private final ResettableEncoder<E> encoder;
	// suggested LOGBACK-1257 workaround below
	private OutputStreamAppender<?> parent;

	protected LilithEncoderBase(Map<String, String> metaDataMap, ResettableEncoder<E> encoder)
	{
		Objects.requireNonNull(metaDataMap, "metaDataMap must not be null!");
		Objects.requireNonNull(encoder, "encoder must not be null!");
		this.metaDataMap = metaDataMap;
		this.encoder = encoder;
	}

	@Override
	public byte[] headerBytes()
	{
		// suggested LOGBACK-1257 workaround below
		if(!isSupposedToGenerateHeader())
		{
			return null;
		}
		// suggested LOGBACK-1257 workaround above
		MetaData metaData = new MetaData(metaDataMap, false);

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
		try
		{
			dataOutputStream.writeInt(DefaultFileHeaderStrategy.CODEC_FILE_HEADER_MAGIC_VALUE);
			dataOutputStream.writeInt(FileConstants.MAGIC_VALUE);

			MetaDataCodec metaCodec = new MetaDataCodec();
			byte[] buffer = metaCodec.encode(metaData);
			if (buffer != null)
			{
				dataOutputStream.writeInt(buffer.length);
				dataOutputStream.write(buffer);
			}
			else
			{
				dataOutputStream.writeInt(0);
			}
			dataOutputStream.flush();
		}
		catch (IOException e)
		{
			addError("Failed to create header!", e);
			return null;
		}

		return byteArrayOutputStream.toByteArray();
	}

	@Override
	public byte[] encode(E event)
	{
		preProcess(event);
		byte[] buffer = encoder.encode(event);
		if(buffer == null)
		{
			addError("Couldn't encode event " + event + "!");
			return EMPTY;
		}
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
		try
		{
			dataOutputStream.writeInt(buffer.length);
			dataOutputStream.write(buffer);
			dataOutputStream.flush();
		}
		catch (IOException e)
		{
			addError("Failed to encode event!", e);
			return EMPTY;
		}
		return byteArrayOutputStream.toByteArray();
	}

	@Override
	@SuppressWarnings("PMD.ReturnEmptyArrayRatherThanNull")
	public byte[] footerBytes()
	{
		return null;
	}

	protected abstract void preProcess(E event);

	@Override
	public void start()
	{
		encoder.reset();
		super.start();
	}

	public void setParent(OutputStreamAppender<?> parent)
	{
		this.parent = parent;
	}

	private boolean isSupposedToGenerateHeader()
	{
		if(parent instanceof FileAppender)
		{
			FileAppender fileAppender = (FileAppender) parent;
			if(!fileAppender.isAppend())
			{
				return true;
			}
			OutputStream outputStream = fileAppender.getOutputStream();
			if(outputStream instanceof ResilientFileOutputStream)
			{
				ResilientFileOutputStream fileOutputStream = (ResilientFileOutputStream) outputStream;
				File file = fileOutputStream.getFile();
				if(file.length() != 0)
				{
					// appending and file with already existing content => no header.
					return false;
				}
			}
		}
		return true;
	}

	// not necessary for this encoder since it doesn't require a footer
	/*
	private boolean isSupposedToGenerateFooter()
	{
		if(parent instanceof FileAppender)
		{
			FileAppender fileAppender = (FileAppender) parent;
			if(fileAppender.isAppend())
			{
				return false;
			}
		}
		return true;
	}
	*/
	// suggested LOGBACK-1257 workaround above
}
