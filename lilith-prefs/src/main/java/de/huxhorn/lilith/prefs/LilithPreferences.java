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

package de.huxhorn.lilith.prefs;

import java.util.Map;

public class LilithPreferences
{
	private Map<String, byte[]> groovyConditions;
	private Map<String, byte[]> groovyClipboardFormatters;
	private Map<String, byte[]> detailsView;
	private Map<String, byte[]> rootFiles;
	private String blackListName;
	private String whiteListName;
	private String lookAndFeel;
	private boolean askingBeforeQuit = false;
	private boolean autoClosing = false;
	private boolean autoFocusingWindow = false;
	private boolean autoOpening = true;
	private boolean checkingForUpdate = true;
	private boolean checkingForSnapshot = false;
	private boolean cleaningLogsOnExit = false;
	private boolean coloringWholeRow = false;
	private boolean globalLoggingEnabled = true;
	private boolean hidingOnClose = true;
	private boolean maximizingInternalFrames = false;
	private boolean mute = false;
	private boolean scrollingSmoothly = true;
	private boolean scrollingToBottom = true;
	private boolean showingFullCallStack = false;
	private boolean showingFullRecentPath = false;
	private boolean showingPrimaryIdentifier = false;
	private boolean showingSecondaryIdentifier = false;
	private boolean showingStatusBar = true;
	private boolean showingStackTrace = true;
	private boolean showingTipOfTheDay = true;
	private boolean showingToolbar = true;
	private boolean trayActive = true;
	private boolean splashScreenDisabled = false;
	private boolean usingInternalFrames = true;
	private SourceFiltering sourceFiltering;
	private String defaultConditionName;
	private boolean usingWrappedExceptionStyle = false;

	public Map<String, byte[]> getGroovyConditions()
	{
		return groovyConditions;
	}

	public void setGroovyConditions(Map<String, byte[]> groovyConditions)
	{
		this.groovyConditions = groovyConditions;
	}

	public Map<String, byte[]> getGroovyClipboardFormatters()
	{
		return groovyClipboardFormatters;
	}

	public void setGroovyClipboardFormatters(Map<String, byte[]> groovyClipboardFormatters)
	{
		this.groovyClipboardFormatters = groovyClipboardFormatters;
	}

	public Map<String, byte[]> getDetailsView()
	{
		return detailsView;
	}

	public void setDetailsView(Map<String, byte[]> detailsView)
	{
		this.detailsView = detailsView;
	}

	public Map<String, byte[]> getRootFiles()
	{
		return rootFiles;
	}

	public void setRootFiles(Map<String, byte[]> rootFiles)
	{
		this.rootFiles = rootFiles;
	}

	public String getBlackListName()
	{
		return blackListName;
	}

	public void setBlackListName(String blackListName)
	{
		this.blackListName = blackListName;
	}

	public String getWhiteListName()
	{
		return whiteListName;
	}

	public void setWhiteListName(String whiteListName)
	{
		this.whiteListName = whiteListName;
	}

	public String getLookAndFeel()
	{
		return lookAndFeel;
	}

	public void setLookAndFeel(String lookAndFeel)
	{
		this.lookAndFeel = lookAndFeel;
	}

	public boolean isAskingBeforeQuit()
	{
		return askingBeforeQuit;
	}

	public void setAskingBeforeQuit(boolean askingBeforeQuit)
	{
		this.askingBeforeQuit = askingBeforeQuit;
	}

	public boolean isAutoClosing()
	{
		return autoClosing;
	}

	public void setAutoClosing(boolean autoClosing)
	{
		this.autoClosing = autoClosing;
	}

	public boolean isAutoFocusingWindow()
	{
		return autoFocusingWindow;
	}

	public void setAutoFocusingWindow(boolean autoFocusingWindow)
	{
		this.autoFocusingWindow = autoFocusingWindow;
	}

	public boolean isAutoOpening()
	{
		return autoOpening;
	}

	public void setAutoOpening(boolean autoOpening)
	{
		this.autoOpening = autoOpening;
	}

	public boolean isCheckingForUpdate()
	{
		return checkingForUpdate;
	}

	public void setCheckingForUpdate(boolean checkingForUpdate)
	{
		this.checkingForUpdate = checkingForUpdate;
	}

	public boolean isCheckingForSnapshot()
	{
		return checkingForSnapshot;
	}

	public void setCheckingForSnapshot(boolean checkingForSnapshot)
	{
		this.checkingForSnapshot = checkingForSnapshot;
	}

	public boolean isCleaningLogsOnExit()
	{
		return cleaningLogsOnExit;
	}

	public void setCleaningLogsOnExit(boolean cleaningLogsOnExit)
	{
		this.cleaningLogsOnExit = cleaningLogsOnExit;
	}

	public boolean isColoringWholeRow()
	{
		return coloringWholeRow;
	}

	public void setColoringWholeRow(boolean coloringWholeRow)
	{
		this.coloringWholeRow = coloringWholeRow;
	}

	public boolean isGlobalLoggingEnabled()
	{
		return globalLoggingEnabled;
	}

	public void setGlobalLoggingEnabled(boolean globalLoggingEnabled)
	{
		this.globalLoggingEnabled = globalLoggingEnabled;
	}

	public boolean isMaximizingInternalFrames()
	{
		return maximizingInternalFrames;
	}

	public void setMaximizingInternalFrames(boolean maximizingInternalFrames)
	{
		this.maximizingInternalFrames = maximizingInternalFrames;
	}

	public boolean isMute()
	{
		return mute;
	}

	public void setMute(boolean mute)
	{
		this.mute = mute;
	}


	public boolean isScrollingSmoothly()
	{
		return scrollingSmoothly;
	}

	public void setScrollingSmoothly(boolean scrollingSmoothly)
	{
		this.scrollingSmoothly = scrollingSmoothly;
	}

	public boolean isScrollingToBottom()
	{
		return scrollingToBottom;
	}

	public void setScrollingToBottom(boolean scrollingToBottom)
	{
		this.scrollingToBottom = scrollingToBottom;
	}

	public boolean isShowingFullCallStack()
	{
		return showingFullCallStack;
	}

	public void setShowingFullCallStack(boolean showingFullCallStack)
	{
		this.showingFullCallStack = showingFullCallStack;
	}

	public boolean isShowingPrimaryIdentifier()
	{
		return showingPrimaryIdentifier;
	}

	public void setShowingPrimaryIdentifier(boolean showingPrimaryIdentifier)
	{
		this.showingPrimaryIdentifier = showingPrimaryIdentifier;
	}

	public boolean isShowingSecondaryIdentifier()
	{
		return showingSecondaryIdentifier;
	}

	public void setShowingSecondaryIdentifier(boolean showingSecondaryIdentifier)
	{
		this.showingSecondaryIdentifier = showingSecondaryIdentifier;
	}

	public boolean isShowingStatusBar()
	{
		return showingStatusBar;
	}

	public void setShowingStatusBar(boolean showingStatusBar)
	{
		this.showingStatusBar = showingStatusBar;
	}

	public boolean isShowingStackTrace()
	{
		return showingStackTrace;
	}

	public void setShowingStackTrace(boolean showingStackTrace)
	{
		this.showingStackTrace = showingStackTrace;
	}

	public boolean isShowingTipOfTheDay()
	{
		return showingTipOfTheDay;
	}

	public void setShowingTipOfTheDay(boolean showingTipOfTheDay)
	{
		this.showingTipOfTheDay = showingTipOfTheDay;
	}

	public boolean isShowingToolbar()
	{
		return showingToolbar;
	}

	public void setShowingToolbar(boolean showingToolbar)
	{
		this.showingToolbar = showingToolbar;
	}

	public boolean isSplashScreenDisabled()
	{
		return splashScreenDisabled;
	}

	public void setSplashScreenDisabled(boolean splashScreenDisabled)
	{
		this.splashScreenDisabled = splashScreenDisabled;
	}

	public boolean isUsingInternalFrames()
	{
		return usingInternalFrames;
	}

	public void setUsingInternalFrames(boolean usingInternalFrames)
	{
		this.usingInternalFrames = usingInternalFrames;
	}

	public SourceFiltering getSourceFiltering()
	{
		return sourceFiltering;
	}

	public void setSourceFiltering(SourceFiltering sourceFiltering)
	{
		this.sourceFiltering = sourceFiltering;
	}

	public String getDefaultConditionName()
	{
		return defaultConditionName;
	}

	public void setDefaultConditionName(String defaultConditionName)
	{
		this.defaultConditionName = defaultConditionName;
	}

	public boolean isTrayActive()
	{
		return trayActive;
	}

	public void setTrayActive(boolean trayActive)
	{
		this.trayActive = trayActive;
	}

	public boolean isShowingFullRecentPath()
	{
		return showingFullRecentPath;
	}

	public void setShowingFullRecentPath(boolean showingFullRecentPath)
	{
		this.showingFullRecentPath = showingFullRecentPath;
	}

	public boolean isHidingOnClose()
	{
		return hidingOnClose;
	}

	public void setHidingOnClose(boolean hidingOnClose)
	{
		this.hidingOnClose = hidingOnClose;
	}

	public boolean isUsingWrappedExceptionStyle()
	{
		return usingWrappedExceptionStyle;
	}

	public void setUsingWrappedExceptionStyle(boolean usingWrappedExceptionStyle)
	{
		this.usingWrappedExceptionStyle = usingWrappedExceptionStyle;
	}

	public enum SourceFiltering
	{
		NONE, BLACKLIST, WHITELIST
	}
}
