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

import de.huxhorn.lilith.prefs.LilithPreferences;
import de.huxhorn.lilith.prefs.protobuf.LilithPreferencesStreamingDecoder;
import de.huxhorn.lilith.prefs.protobuf.LilithPreferencesStreamingEncoder;
import de.huxhorn.lilith.swing.ApplicationPreferences;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ImportExportCommand
{
	private static final int MAX_FILE_SIZE = 5 * 1024 * 1024;

	static
	{
		new ImportExportCommand(); // stfu
	}

	private ImportExportCommand() {}

	private static Map<String, byte[]> exportGroovyConditions(ApplicationPreferences preferences)
	{
		String[] files = preferences.getAllGroovyConditionScriptFiles();
		if(files == null)
		{
			return null;
		}
		return exportFiles(preferences.getGroovyConditionsPath(), files);
	}

	private static Map<String, byte[]> exportClipboardFormatterScriptFiles(ApplicationPreferences preferences)
	{
		String[] files = preferences.getClipboardFormatterScriptFiles();
		if(files == null)
		{
			return null;
		}
		return exportFiles(preferences.getGroovyConditionsPath(), files);
	}

	private static Map<String, byte[]> exportDetailsView(ApplicationPreferences preferences)
	{
		String[] files = new String[]
			{
				ApplicationPreferences.DETAILS_VIEW_CSS_FILENAME,
				ApplicationPreferences.DETAILS_VIEW_GROOVY_FILENAME,
			};
		return exportFiles(preferences.getDetailsViewRoot(), files);
	}

	private static Map<String, byte[]> exportRootFiles(ApplicationPreferences preferences)
	{
		String[] files = new String[]
			{
				ApplicationPreferences.ACCESS_LAYOUT_GLOBAL_XML_FILENAME,
				ApplicationPreferences.ACCESS_LAYOUT_XML_FILENAME,
				ApplicationPreferences.LOGGING_LAYOUT_GLOBAL_XML_FILENAME,
				ApplicationPreferences.LOGGING_LAYOUT_XML_FILENAME,
				ApplicationPreferences.CONDITIONS_XML_FILENAME,
				ApplicationPreferences.SOUND_LOCATIONS_XML_FILENAME,
				ApplicationPreferences.SOURCE_LISTS_XML_FILENAME,
				ApplicationPreferences.SOURCE_NAMES_XML_FILENAME,
				ApplicationPreferences.STATUS_COLORS_XML_FILENAME,
				ApplicationPreferences.LEVEL_COLORS_XML_FILENAME,
			};
		return exportFiles(preferences.getStartupApplicationPath(), files);
	}

	@SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
	private static Map<String, byte[]> exportFiles(File basePath, String[] files)
	{
		final Logger logger = LoggerFactory.getLogger(ImportExportCommand.class);

		Map<String,byte[]> result=new HashMap<>();

		for(String current : files)
		{
			File currentFile;
			if(basePath != null)
			{
				currentFile = new File(basePath, current);
			}
			else
			{
				currentFile = new File(current);
			}

			if(!currentFile.exists())
			{
				if(logger.isInfoEnabled()) logger.info("Ignoring '{}' because it does not exist.", currentFile.getAbsolutePath());
			}
			else if(!currentFile.canRead())
			{
				if(logger.isWarnEnabled()) logger.warn("Can't read '{}'!", currentFile.getAbsolutePath());
			}
			else
			{
				long length=currentFile.length();
				if(length > MAX_FILE_SIZE)
				{
					if(logger.isInfoEnabled()) logger.info("Ignoring '{}' because it's too big ({} bytes).", currentFile.getAbsolutePath(), length);
					continue;
				}

				try(DataInputStream is=new DataInputStream(Files.newInputStream(currentFile.toPath())))
				{
					byte[] bytes=new byte[(int) length];
					is.readFully(bytes);
					result.put(current, bytes);
				}
				catch(IOException e)
				{
					if(logger.isWarnEnabled()) logger.warn("Exception while reading '"+currentFile.getAbsolutePath()+"'! Ignoring file...", e);
				}
			}
		}

		if(logger.isInfoEnabled())
		{
			SortedMap<String, byte[]> sortedResult=new TreeMap<>(result);
			StringBuilder msg=new StringBuilder();
			msg.append("Exported files:\n");
			for(Map.Entry<String, byte[]> current: sortedResult.entrySet())
			{
				msg.append("- ").append(current.getKey()).append("\n  ")
						.append(current.getValue().length).append(" bytes\n");
			}
			logger.info(msg.toString());
		}

		return result;
	}

	@SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
	private static void importFiles(File basePath, Map<String, byte[]> files)
	{
		final Logger logger = LoggerFactory.getLogger(ImportExportCommand.class);

		if(basePath.mkdirs())
		{
			if(logger.isInfoEnabled()) logger.info("Created directory '{}'.", basePath.getAbsolutePath()); // NOPMD
		}

		for(Map.Entry<String, byte[]> current : files.entrySet())
		{
			String key=current.getKey();
			byte[] value=current.getValue();

			File currentFile = new File(basePath, key);

			if(!currentFile.isFile() || currentFile.canWrite())
			{
				try(DataOutputStream os=new DataOutputStream(Files.newOutputStream(currentFile.toPath())))
				{
					os.write(value);
					if(logger.isInfoEnabled()) logger.info("Wrote {} bytes into '{}'.", value.length, currentFile.getAbsolutePath());
				}
				catch(IOException e)
				{
					if(logger.isWarnEnabled()) logger.warn("Exception while writing '"+currentFile.getAbsolutePath()+"'! Ignoring file...", e);
				}
			}
			else
			{
				if(logger.isWarnEnabled()) logger.warn("Can't write {}!", currentFile.getAbsolutePath());
			}
		}
	}

	private static LilithPreferences exportPersistence(ApplicationPreferences preferences)
	{
		LilithPreferences p=new LilithPreferences();
		p.setGroovyConditions(exportGroovyConditions(preferences));
		p.setGroovyClipboardFormatters(exportClipboardFormatterScriptFiles(preferences));
		p.setDetailsView(exportDetailsView(preferences));
		p.setRootFiles(exportRootFiles(preferences));

		// String
		p.setBlackListName(preferences.getBlackListName());
		p.setWhiteListName(preferences.getWhiteListName());
		p.setLookAndFeel(preferences.getLookAndFeel());

		// boolean
		p.setAskingBeforeQuit(preferences.isAskingBeforeQuit());
		p.setAutoClosing(preferences.isAutoClosing());
		p.setAutoFocusingWindow(preferences.isAutoFocusingWindow());
		p.setAutoOpening(preferences.isAutoOpening());
		p.setCheckingForUpdate(preferences.isCheckingForUpdate());
		p.setCheckingForSnapshot(preferences.isCheckingForSnapshot());
		p.setCleaningLogsOnExit(preferences.isCleaningLogsOnExit());
		p.setColoringWholeRow(preferences.isColoringWholeRow());
		p.setGlobalLoggingEnabled(preferences.isGlobalLoggingEnabled());
		p.setHidingOnClose(preferences.isHidingOnClose());
		p.setMaximizingInternalFrames(preferences.isMaximizingInternalFrames());
		p.setMute(preferences.isMute());
		p.setScrollingSmoothly(preferences.isScrollingSmoothly());
		p.setScrollingToBottom(preferences.isScrollingToBottom());
		p.setShowingFullCallStack(preferences.isShowingFullCallStack());
		p.setUsingWrappedExceptionStyle(preferences.isUsingWrappedExceptionStyle());
		p.setShowingFullRecentPath(preferences.isShowingFullRecentPath());
		p.setShowingPrimaryIdentifier(preferences.isShowingPrimaryIdentifier());
		p.setShowingSecondaryIdentifier(preferences.isShowingSecondaryIdentifier());
		p.setShowingStatusBar(preferences.isShowingStatusBar());
		p.setShowingStackTrace(preferences.isShowingStackTrace());
		p.setShowingTipOfTheDay(preferences.isShowingTipOfTheDay());
		p.setShowingToolbar(preferences.isShowingToolbar());
		p.setSplashScreenDisabled(preferences.isSplashScreenDisabled());
		p.setTrayActive(preferences.isTrayActive());
		p.setUsingInternalFrames(preferences.isUsingInternalFrames());
		p.setSourceFiltering(preferences.getSourceFiltering());
		return p;
	}

	private static void importPersistence(ApplicationPreferences preferences, LilithPreferences p)
	{
		if(p.getGroovyConditions() != null)
		{
			importFiles(preferences.getGroovyConditionsPath(), p.getGroovyConditions());
		}
		if(p.getGroovyConditions() != null)
		{
			importFiles(preferences.getGroovyClipboardFormattersPath(), p.getGroovyClipboardFormatters());
		}
		if(p.getDetailsView() != null)
		{
			importFiles(preferences.getDetailsViewRoot(), p.getDetailsView());
		}
		if(p.getRootFiles() != null)
		{
			importFiles(preferences.getStartupApplicationPath(), p.getRootFiles());
		}

		// String
		preferences.setBlackListName(p.getBlackListName());
		preferences.setWhiteListName(p.getWhiteListName());
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
		preferences.setUsingWrappedExceptionStyle(p.isUsingWrappedExceptionStyle());
		preferences.setShowingFullRecentPath(p.isShowingFullRecentPath());
		preferences.setShowingPrimaryIdentifier(p.isShowingPrimaryIdentifier());
		preferences.setShowingSecondaryIdentifier(p.isShowingSecondaryIdentifier());
		preferences.setShowingStatusBar(p.isShowingStatusBar());
		preferences.setShowingStackTrace(p.isShowingStackTrace());
		preferences.setShowingTipOfTheDay(p.isShowingTipOfTheDay());
		preferences.setShowingToolbar(p.isShowingToolbar());
		preferences.setSplashScreenDisabled(p.isSplashScreenDisabled());
		preferences.setTrayActive(p.isTrayActive());
		preferences.setUsingInternalFrames(p.isUsingInternalFrames());
		preferences.setSourceFiltering(p.getSourceFiltering());
	}

	public static void exportPreferences(File file)
	{
		final Logger logger = LoggerFactory.getLogger(ImportExportCommand.class);

		ApplicationPreferences preferences = new ApplicationPreferences();

		LilithPreferences p = exportPersistence(preferences);

		try
		{
			writePersistence(file, p);
		}
		catch(IOException e)
		{
			if(logger.isErrorEnabled()) logger.error("Exception while writing '"+file.getAbsolutePath()+"'!",e);
		}
	}

	public static void importPreferences(File file)
	{
		final Logger logger = LoggerFactory.getLogger(ImportExportCommand.class);

		ApplicationPreferences preferences = new ApplicationPreferences();

		try
		{
			LilithPreferences p = readPersistence(file);
			importPersistence(preferences, p);
		}
		catch(IOException e)
		{
			if(logger.isErrorEnabled()) logger.error("Exception while reading '"+file.getAbsolutePath()+"'!",e);
		}
	}

	private static void writePersistence(File file, LilithPreferences p)
		throws IOException
	{
		GZIPOutputStream os = null;
		try
		{
			os = new GZIPOutputStream(new BufferedOutputStream(Files.newOutputStream(file.toPath())));
			LilithPreferencesStreamingEncoder encoder = new LilithPreferencesStreamingEncoder();
			encoder.encode(p, os);
		}
		finally
		{
			if(os != null)
			{
				os.close();
			}
		}

	}

	private static LilithPreferences readPersistence(File file)
		throws IOException
	{
		GZIPInputStream is = null;
		try
		{
			is = new GZIPInputStream(new BufferedInputStream(Files.newInputStream(file.toPath())));
			LilithPreferencesStreamingDecoder decoder = new LilithPreferencesStreamingDecoder();
			return decoder.decode(is);
		}
		finally
		{
			if(is != null)
			{
				is.close();
			}
		}
	}
}
