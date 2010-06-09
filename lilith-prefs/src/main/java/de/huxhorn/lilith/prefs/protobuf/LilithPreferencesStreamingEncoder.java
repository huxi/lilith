/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2010 Joern Huxhorn
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
 * Copyright 2007-2010 Joern Huxhorn
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

package de.huxhorn.lilith.prefs.protobuf;

import com.google.protobuf.ByteString;
import de.huxhorn.lilith.prefs.LilithPreferences;
import de.huxhorn.lilith.prefs.protobuf.generated.PrefsProto;
import de.huxhorn.sulky.codec.streaming.StreamingEncoder;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

public class LilithPreferencesStreamingEncoder
	implements StreamingEncoder<LilithPreferences>
{
	public void encode(LilithPreferences obj, OutputStream into) throws IOException
	{
		PrefsProto.LilithPreferences converted = convert(obj);
		if(converted != null)
		{
			converted.writeTo(into);
		}
	}

	private static PrefsProto.DirectoryContent.Builder convert(Map<String, byte[]> dir)
	{
		if(dir == null)
		{
			return null;
		}
		PrefsProto.DirectoryContent.Builder builder = PrefsProto.DirectoryContent.newBuilder();
		for(Map.Entry<String, byte[]> current:dir.entrySet())
		{
			builder.addEntry(convert(current));
		}
		return builder;
	}

	private static PrefsProto.ByteArrayMapEntry.Builder convert(Map.Entry<String, byte[]> current)
	{
		PrefsProto.ByteArrayMapEntry.Builder builder=PrefsProto.ByteArrayMapEntry.newBuilder();
		builder.setKey(current.getKey());
		builder.setValue(ByteString.copyFrom(current.getValue()));
		return builder;
	}

	private static PrefsProto.LilithPreferences convert(LilithPreferences p)
	{
		if(p == null)
		{
			return null;
		}
		PrefsProto.LilithPreferences.Builder prefs = PrefsProto.LilithPreferences.newBuilder();

		if(p.getGroovyConditions() != null)
		{
			prefs.setGroovyConditions(convert(p.getGroovyConditions()));
		}
		if(p.getDetailsView() != null)
		{
			prefs.setDetailsView(convert(p.getDetailsView()));
		}
		if(p.getRootFiles() != null)
		{
			prefs.setRootFiles(convert(p.getRootFiles()));
		}

		// String
		prefs.setBlacklistName(p.getBlackListName());
		prefs.setWhitelistName(p.getWhiteListName());
		prefs.setLookAndFeel(p.getLookAndFeel());

		// boolean
		prefs.setAskingBeforeQuit(p.isAskingBeforeQuit());
		prefs.setAutoClosing(p.isAutoClosing());
		prefs.setAutoFocusingWindow(p.isAutoFocusingWindow());
		prefs.setAutoOpening(p.isAutoOpening());
		prefs.setCheckingForUpdate(p.isCheckingForUpdate());
		prefs.setCleaningLogsOnExit(p.isCleaningLogsOnExit());
		prefs.setColoringWholeRow(p.isColoringWholeRow());
		prefs.setGlobalLoggingEnabled(p.isGlobalLoggingEnabled());
		prefs.setLicensed(p.isLicensed());
		prefs.setLoggingStatisticEnabled(p.isLoggingStatisticEnabled());
		prefs.setMaximizingInternalFrames(p.isMaximizingInternalFrames());
		prefs.setMute(p.isMute());
		prefs.setScrollingToBottom(p.isScrollingToBottom());
		prefs.setShowingFullCallstack(p.isShowingFullCallstack());
		prefs.setShowingIdentifier(p.isShowingIdentifier());
		prefs.setShowingStatusbar(p.isShowingStatusbar());
		prefs.setShowingStacktrace(p.isShowingStackTrace());
		prefs.setShowingTipOfTheDay(p.isShowingTipOfTheDay());
		prefs.setShowingToolbar(p.isShowingToolbar());
		prefs.setSplashScreenDisabled(p.isSplashScreenDisabled());
		prefs.setUsingInternalFrames(p.isUsingInternalFrames());
		prefs.setDefaultConditionName(p.getDefaultConditionName());

		LilithPreferences.SourceFiltering sf = p.getSourceFiltering();
		if(sf != null)
		{
			switch(sf)
			{
				case BLACKLIST:
					prefs.setSourceFiltering(PrefsProto.SourceFiltering.BLACKLIST);
					break;
				case WHITELIST:
					prefs.setSourceFiltering(PrefsProto.SourceFiltering.WHITELIST);
					break;
				default:
					prefs.setSourceFiltering(PrefsProto.SourceFiltering.NONE);
			}
		}
		else
		{
			prefs.setSourceFiltering(PrefsProto.SourceFiltering.NONE);
		}
		
		return prefs.build();
	}
}
