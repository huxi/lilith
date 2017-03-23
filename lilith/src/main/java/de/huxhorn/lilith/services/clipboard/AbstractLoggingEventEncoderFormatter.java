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

package de.huxhorn.lilith.services.clipboard;

import de.huxhorn.lilith.data.logging.LoggingEvent;
import de.huxhorn.lilith.swing.LilithActionId;
import de.huxhorn.sulky.codec.Encoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static de.huxhorn.lilith.services.clipboard.FormatterTools.resolveLoggingEvent;

public abstract class AbstractLoggingEventEncoderFormatter
		extends AbstractNativeClipboardFormatter
{
	private static final long serialVersionUID = -7781395686178042636L;

	private final Encoder<LoggingEvent> encoder;

	protected AbstractLoggingEventEncoderFormatter(LilithActionId id, Encoder<LoggingEvent> encoder)
	{
		super(id);
		Objects.requireNonNull(encoder, "encoder must not be null!");
		this.encoder = encoder;
	}

	@Override
	public boolean isCompatible(Object object)
	{
		return resolveLoggingEvent(object).isPresent();
	}

	@Override
	public String toString(Object object)
	{
		return resolveLoggingEvent(object).map(this::encode).orElse(null);
	}

	private String encode(LoggingEvent event)
	{
		byte[] bytes = encoder.encode(event);

		if(bytes == null)
		{
			return null;
		}

		return new String(bytes, StandardCharsets.UTF_8);
	}
}
