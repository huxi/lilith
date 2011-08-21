/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2011 Joern Huxhorn
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
 * Copyright 2007-2011 Joern Huxhorn
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
	public LilithPreferences decode(InputStream from) throws IOException
	{
		PrefsProto.LilithPreferences prefs=PrefsProto.LilithPreferences.parseFrom(from);
		return convert(prefs);
	}

	private static Map<String, byte[]> convert(PrefsProto.DirectoryContent dir)
	{
		if(dir == null)
		{
			return null;
		}

		Map<String,byte[]> result=new HashMap<String,byte[]>();

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
		LilithPreferences prefs = new LilithPreferences();

		if(p.hasGroovyConditions())
		{
			prefs.setGroovyConditions(convert(p.getGroovyConditions()));
		}
		if(p.hasGroovyClipboardFormatters())
		{
			prefs.setGroovyClipboardFormatters(convert(p.getGroovyClipboardFormatters()));
		}
		if(p.hasDetailsView())
		{
			prefs.setDetailsView(convert(p.getDetailsView()));
		}
		if(p.hasRootFiles())
		{
			prefs.setRootFiles(convert(p.getRootFiles()));
		}

		// String
		if(p.hasBlacklistName())
		{
			prefs.setBlackListName(p.getBlacklistName());
		}
		if(p.hasWhitelistName())
		{
			prefs.setWhiteListName(p.getWhitelistName());
		}
		if(p.hasLookAndFeel())
		{
			prefs.setLookAndFeel(p.getLookAndFeel());
		}

		// boolean
		if(p.hasAskingBeforeQuit())
		{
			prefs.setAskingBeforeQuit(p.getAskingBeforeQuit());
		}
		if(p.hasAutoClosing())
		{
			prefs.setAutoClosing(p.getAutoClosing());
		}
		if(p.hasAutoFocusingWindow())
		{
			prefs.setAutoFocusingWindow(p.getAutoFocusingWindow());
		}
		if(p.hasAutoOpening())
		{
			prefs.setAutoOpening(p.getAutoOpening());
		}
		if(p.hasCheckingForUpdate())
		{
			prefs.setCheckingForUpdate(p.getCheckingForUpdate());
		}
		if(p.hasCheckingForSnapshot())
		{
			prefs.setCheckingForSnapshot(p.getCheckingForSnapshot());
		}
		if(p.hasCleaningLogsOnExit())
		{
			prefs.setCleaningLogsOnExit(p.getCleaningLogsOnExit());
		}
		if(p.hasColoringWholeRow())
		{
			prefs.setColoringWholeRow(p.getColoringWholeRow());
		}
		if(p.hasGlobalLoggingEnabled())
		{
			prefs.setGlobalLoggingEnabled(p.getGlobalLoggingEnabled());
		}
		if(p.hasHidingOnClose())
		{
			prefs.setHidingOnClose(p.getHidingOnClose());
		}
		if(p.hasLoggingStatisticEnabled())
		{
			prefs.setLoggingStatisticEnabled(p.getLoggingStatisticEnabled());
		}
		if(p.hasMaximizingInternalFrames())
		{
			prefs.setMaximizingInternalFrames(p.getMaximizingInternalFrames());
		}
		if(p.hasMute())
		{
			prefs.setMute(p.getMute());
		}
		if(p.hasScrollingToBottom())
		{
			prefs.setScrollingToBottom(p.getScrollingToBottom());
		}
		if(p.hasShowingFullCallstack())
		{
			prefs.setShowingFullCallstack(p.getShowingFullCallstack());
		}
		if(p.hasShowingFullRecentPath())
		{
			prefs.setShowingFullRecentPath(p.getShowingFullRecentPath());
		}
		if(p.hasShowingIdentifier())
		{
			prefs.setShowingIdentifier(p.getShowingIdentifier());
		}
		if(p.hasShowingStatusbar())
		{
			prefs.setShowingStatusbar(p.getShowingStatusbar());
		}
		if(p.hasShowingStacktrace())
		{
			prefs.setShowingStackTrace(p.getShowingStacktrace());
		}
		if(p.hasUsingWrappedExceptionStyle())
		{
			prefs.setUsingWrappedExceptionStyle(p.getUsingWrappedExceptionStyle());
		}
		if(p.hasShowingTipOfTheDay())
		{
			prefs.setShowingTipOfTheDay(p.getShowingTipOfTheDay());
		}
		if(p.hasShowingToolbar())
		{
			prefs.setShowingToolbar(p.getShowingToolbar());
		}
		if(p.hasSplashScreenDisabled())
		{
			prefs.setSplashScreenDisabled(p.getSplashScreenDisabled());
		}
		if(p.hasTrayActive())
		{
			prefs.setTrayActive(p.getTrayActive());
		}
		if(p.hasUsingInternalFrames())
		{
			prefs.setUsingInternalFrames(p.getUsingInternalFrames());
		}
		if(p.hasSourceFiltering())
		{
			PrefsProto.SourceFiltering sf = p.getSourceFiltering();
			switch(sf)
			{
				case BLACKLIST:
					prefs.setSourceFiltering(LilithPreferences.SourceFiltering.BLACKLIST);
					break;
				case WHITELIST:
					prefs.setSourceFiltering(LilithPreferences.SourceFiltering.WHITELIST);
					break;
				default:
					prefs.setSourceFiltering(LilithPreferences.SourceFiltering.NONE);
			}
		}
		else
		{
			prefs.setSourceFiltering(LilithPreferences.SourceFiltering.NONE);			
		}
		if(p.hasDefaultConditionName())
		{
			prefs.setDefaultConditionName(p.getDefaultConditionName());
		}
		return prefs;
	}

}
