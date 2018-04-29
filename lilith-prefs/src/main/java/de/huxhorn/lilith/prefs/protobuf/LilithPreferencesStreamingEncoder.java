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
	@Override
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
		PrefsProto.LilithPreferences.Builder preferences = PrefsProto.LilithPreferences.newBuilder();

		if(p.getGroovyConditions() != null)
		{
			preferences.setGroovyConditions(convert(p.getGroovyConditions()));
		}
		if(p.getGroovyClipboardFormatters() != null)
		{
			preferences.setGroovyClipboardFormatters(convert(p.getGroovyClipboardFormatters()));
		}
		if(p.getDetailsView() != null)
		{
			preferences.setDetailsView(convert(p.getDetailsView()));
		}
		if(p.getRootFiles() != null)
		{
			preferences.setRootFiles(convert(p.getRootFiles()));
		}

		// String
		preferences.setBlacklistName(p.getBlackListName());
		preferences.setWhitelistName(p.getWhiteListName());
		preferences.setLookAndFeel(p.getLookAndFeel());

		// boolean
		preferences.setAskingBeforeQuit(p.isAskingBeforeQuit());
		preferences.setAutoClosing(p.isAutoClosing());
		preferences.setAutoFocusingWindow(p.isAutoFocusingWindow());
		preferences.setAutoOpening(p.isAutoOpening());
		preferences.setCheckingForUpdate(p.isCheckingForUpdate());
		preferences.setCheckingForSnapshot(p.isCheckingForSnapshot());
		preferences.setCleaningLogsOnExit(p.isCleaningLogsOnExit());
		preferences.setColoringWholeRow(p.isColoringWholeRow());
		preferences.setGlobalLoggingEnabled(p.isGlobalLoggingEnabled());
		preferences.setHidingOnClose(p.isHidingOnClose());
		preferences.setMaximizingInternalFrames(p.isMaximizingInternalFrames());
		preferences.setMute(p.isMute());
		preferences.setScrollingSmoothly(p.isScrollingSmoothly());
		preferences.setScrollingToBottom(p.isScrollingToBottom());
		preferences.setShowingFullCallStack(p.isShowingFullCallStack());
		preferences.setShowingFullRecentPath(p.isShowingFullRecentPath());
		preferences.setShowingPrimaryIdentifier(p.isShowingPrimaryIdentifier());
		preferences.setShowingSecondaryIdentifier(p.isShowingSecondaryIdentifier());
		preferences.setShowingStatusBar(p.isShowingStatusBar());
		preferences.setShowingStacktrace(p.isShowingStackTrace());
		preferences.setUsingWrappedExceptionStyle(p.isUsingWrappedExceptionStyle());
		preferences.setShowingTipOfTheDay(p.isShowingTipOfTheDay());
		preferences.setShowingToolbar(p.isShowingToolbar());
		preferences.setSplashScreenDisabled(p.isSplashScreenDisabled());
		preferences.setTrayActive(p.isTrayActive());
		preferences.setUsingInternalFrames(p.isUsingInternalFrames());
		preferences.setDefaultConditionName(p.getDefaultConditionName());

		LilithPreferences.SourceFiltering sf = p.getSourceFiltering();
		if(sf != null)
		{
			switch(sf)
			{
				case BLACKLIST:
					preferences.setSourceFiltering(PrefsProto.SourceFiltering.BLACKLIST);
					break;
				case WHITELIST:
					preferences.setSourceFiltering(PrefsProto.SourceFiltering.WHITELIST);
					break;
				default:
					preferences.setSourceFiltering(PrefsProto.SourceFiltering.NONE);
					break;
			}
		}
		else
		{
			preferences.setSourceFiltering(PrefsProto.SourceFiltering.NONE);
		}

		return preferences.build();
	}
}
