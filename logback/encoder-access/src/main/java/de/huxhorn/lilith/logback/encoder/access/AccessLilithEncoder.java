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

package de.huxhorn.lilith.logback.encoder.access;

import ch.qos.logback.access.spi.AccessEvent;
import de.huxhorn.lilith.api.FileConstants;
import de.huxhorn.lilith.logback.encoder.core.LilithEncoderBase;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class AccessLilithEncoder
	extends LilithEncoderBase<AccessEvent>
{
	private static final Map<String, String> META_DATA_MAP;
	static
	{
		Map<String, String> metaDataMap = new HashMap<>();
		metaDataMap.put(FileConstants.CONTENT_TYPE_KEY, FileConstants.CONTENT_TYPE_VALUE_ACCESS);
		metaDataMap.put(FileConstants.CONTENT_FORMAT_KEY, FileConstants.CONTENT_FORMAT_VALUE_PROTOBUF);
		metaDataMap.put(FileConstants.COMPRESSION_KEY, FileConstants.COMPRESSION_VALUE_GZIP);

		META_DATA_MAP = Collections.unmodifiableMap(metaDataMap);
	}

	public AccessLilithEncoder()
	{
		super(META_DATA_MAP, new WrappingAccessEncoder());
	}

	@Override
	protected void preProcess(AccessEvent event)
	{
		// nothing
	}
}
