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

import de.huxhorn.lilith.prefs.LilithPreferences;
import de.huxhorn.lilith.prefs.protobuf.generated.PrefsProto;
import de.huxhorn.sulky.codec.streaming.StreamingDecoder;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LilithPreferencesStreamingDecoder
	implements StreamingDecoder<LilithPreferences>
{
	@Override
	public LilithPreferences decode(InputStream from) throws IOException
	{
		PrefsProto.LilithPreferences preferences=PrefsProto.LilithPreferences.parseFrom(from);
		return convert(preferences);
	}

	private static Map<String, byte[]> convert(PrefsProto.DirectoryContent dir)
	{
		if(dir == null)
		{
			return null;
		}

		Map<String,byte[]> result=new HashMap<>();

		List<PrefsProto.ByteArrayMapEntry> entries = dir.getEntryList();
		for(PrefsProto.ByteArrayMapEntry current:entries)
		{
			result.put(current.getKey(), current.getValue().toByteArray());
		}

		return result;
	}

	private static LilithPreferences convert(PrefsProto.LilithPreferences p)
	{
		if(p == null)
		{
			return null;
		}
		LilithPreferences preferences = new LilithPreferences();

		if(p.hasGroovyConditions())
		{
			preferences.setGroovyConditions(convert(p.getGroovyConditions()));
		}
		if(p.hasGroovyClipboardFormatters())
		{
			preferences.setGroovyClipboardFormatters(convert(p.getGroovyClipboardFormatters()));
		}
		if(p.hasDetailsView())
		{
			preferences.setDetailsView(convert(p.getDetailsView()));
		}
		if(p.hasRootFiles())
		{
			preferences.setRootFiles(convert(p.getRootFiles()));
		}

		// String
		if(p.hasBlacklistName())
		{
			preferences.setBlackListName(p.getBlacklistName());
		}
		if(p.hasWhitelistName())
		{
			preferences.setWhiteListName(p.getWhitelistName());
		}
		if(p.hasLookAndFeel())
		{
			preferences.setLookAndFeel(p.getLookAndFeel());
		}

		// boolean
		if(p.hasAskingBeforeQuit())
		{
			preferences.setAskingBeforeQuit(p.getAskingBeforeQuit());
		}
		if(p.hasAutoClosing())
		{
			preferences.setAutoClosing(p.getAutoClosing());
		}
		if(p.hasAutoFocusingWindow())
		{
			preferences.setAutoFocusingWindow(p.getAutoFocusingWindow());
		}
		if(p.hasAutoOpening())
		{
			preferences.setAutoOpening(p.getAutoOpening());
		}
		if(p.hasCheckingForUpdate())
		{
			preferences.setCheckingForUpdate(p.getCheckingForUpdate());
		}
		if(p.hasCheckingForSnapshot())
		{
			preferences.setCheckingForSnapshot(p.getCheckingForSnapshot());
		}
		if(p.hasCleaningLogsOnExit())
		{
			preferences.setCleaningLogsOnExit(p.getCleaningLogsOnExit());
		}
		if(p.hasColoringWholeRow())
		{
			preferences.setColoringWholeRow(p.getColoringWholeRow());
		}
		if(p.hasGlobalLoggingEnabled())
		{
			preferences.setGlobalLoggingEnabled(p.getGlobalLoggingEnabled());
		}
		if(p.hasHidingOnClose())
		{
			preferences.setHidingOnClose(p.getHidingOnClose());
		}
		if(p.hasMaximizingInternalFrames())
		{
			preferences.setMaximizingInternalFrames(p.getMaximizingInternalFrames());
		}
		if(p.hasMute())
		{
			preferences.setMute(p.getMute());
		}
		if(p.hasScrollingSmoothly())
		{
			preferences.setScrollingSmoothly(p.getScrollingSmoothly());
		}
		if(p.hasScrollingToBottom())
		{
			preferences.setScrollingToBottom(p.getScrollingToBottom());
		}
		if(p.hasShowingFullCallStack())
		{
			preferences.setShowingFullCallStack(p.getShowingFullCallStack());
		}
		if(p.hasShowingFullRecentPath())
		{
			preferences.setShowingFullRecentPath(p.getShowingFullRecentPath());
		}
		if(p.hasShowingPrimaryIdentifier())
		{
			preferences.setShowingPrimaryIdentifier(p.getShowingPrimaryIdentifier());
		}
		if(p.hasShowingSecondaryIdentifier())
		{
			preferences.setShowingSecondaryIdentifier(p.getShowingSecondaryIdentifier());
		}
		if(p.hasShowingStatusBar())
		{
			preferences.setShowingStatusBar(p.getShowingStatusBar());
		}
		if(p.hasShowingStacktrace())
		{
			preferences.setShowingStackTrace(p.getShowingStacktrace());
		}
		if(p.hasUsingWrappedExceptionStyle())
		{
			preferences.setUsingWrappedExceptionStyle(p.getUsingWrappedExceptionStyle());
		}
		if(p.hasShowingTipOfTheDay())
		{
			preferences.setShowingTipOfTheDay(p.getShowingTipOfTheDay());
		}
		if(p.hasShowingToolbar())
		{
			preferences.setShowingToolbar(p.getShowingToolbar());
		}
		if(p.hasSplashScreenDisabled())
		{
			preferences.setSplashScreenDisabled(p.getSplashScreenDisabled());
		}
		if(p.hasTrayActive())
		{
			preferences.setTrayActive(p.getTrayActive());
		}
		if(p.hasUsingInternalFrames())
		{
			preferences.setUsingInternalFrames(p.getUsingInternalFrames());
		}
		if(p.hasSourceFiltering())
		{
			PrefsProto.SourceFiltering sf = p.getSourceFiltering();
			switch(sf)
			{
				case BLACKLIST:
					preferences.setSourceFiltering(LilithPreferences.SourceFiltering.BLACKLIST);
					break;
				case WHITELIST:
					preferences.setSourceFiltering(LilithPreferences.SourceFiltering.WHITELIST);
					break;
				default:
					preferences.setSourceFiltering(LilithPreferences.SourceFiltering.NONE);
					break;
			}
		}
		else
		{
			preferences.setSourceFiltering(LilithPreferences.SourceFiltering.NONE);
		}
		if(p.hasDefaultConditionName())
		{
			preferences.setDefaultConditionName(p.getDefaultConditionName());
		}
		return preferences;
	}

}
