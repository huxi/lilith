/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2015 Joern Huxhorn
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
import de.huxhorn.sulky.io.IOUtilities;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImportExportCommand
{
	private static final int MAX_FILE_SIZE = 5 * 1024 * 1024;

	public static Map<String, byte[]> exportGroovyConditions(ApplicationPreferences prefs)
	{
		String[] files = prefs.getAllGroovyConditionScriptFiles();
		if(files == null)
		{
			return null;
		}
		return exportFiles(prefs.getGroovyConditionsPath(), files);
	}

	public static Map<String, byte[]> exportClipboardFormatterScriptFiles(ApplicationPreferences prefs)
	{
		String[] files = prefs.getClipboardFormatterScriptFiles();
		if(files == null)
		{
			return null;
		}
		return exportFiles(prefs.getGroovyConditionsPath(), files);
	}

	public static Map<String, byte[]> exportDetailsView(ApplicationPreferences prefs)
	{
		String[] files = new String[]
			{
				ApplicationPreferences.DETAILS_VIEW_CSS_FILENAME,
				ApplicationPreferences.DETAILS_VIEW_GROOVY_FILENAME,
			};
		return exportFiles(prefs.getDetailsViewRoot(), files);
	}

	public static Map<String, byte[]> exportRootFiles(ApplicationPreferences prefs)
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
		return exportFiles(prefs.getStartupApplicationPath(), files);
	}

	public static Map<String, byte[]> exportFiles(File basePath, String[] files)
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
				DataInputStream is=null;
				try
				{
					long length=currentFile.length();
					if(length > MAX_FILE_SIZE)
					{
						if(logger.isInfoEnabled()) logger.info("Ignoring '{}' because it's too big ({} bytes).", currentFile.getAbsolutePath(), length);
						continue;
					}
					byte[] bytes=new byte[(int) length];
					is=new DataInputStream(new FileInputStream(currentFile));
					is.readFully(bytes);
					result.put(current, bytes);
				}
				catch(IOException e)
				{
					if(logger.isWarnEnabled()) logger.warn("Exception while reading '"+currentFile.getAbsolutePath()+"'! Ignoring file...", e);
				}
				finally
				{
					IOUtilities.closeQuietly(is);
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
				msg.append("- ").append(current.getKey()).append("\n");
				msg.append("  ").append(current.getValue().length).append(" bytes\n");
			}
			logger.info(msg.toString());
		}

		return result;
	}

	public static void importFiles(File basePath, Map<String, byte[]> files)
	{
		final Logger logger = LoggerFactory.getLogger(ImportExportCommand.class);

		if(basePath.mkdirs())
		{
			if(logger.isInfoEnabled()) logger.info("Created directory '{}'.", basePath.getAbsolutePath());
		}

		for(Map.Entry<String, byte[]> current : files.entrySet())
		{
			String key=current.getKey();
			byte[] value=current.getValue();

			File currentFile = new File(basePath, key);

			if(!currentFile.isFile() || currentFile.canWrite())
			{
				DataOutputStream os=null;
				try
				{
					os=new DataOutputStream(new FileOutputStream(currentFile));
					os.write(value);
					if(logger.isInfoEnabled()) logger.info("Wrote {} bytes into '{}'.", value.length, currentFile.getAbsolutePath());
				}
				catch(IOException e)
				{
					if(logger.isWarnEnabled()) logger.warn("Exception while writing '"+currentFile.getAbsolutePath()+"'! Ignoring file...", e);
				}
				finally
				{
					IOUtilities.closeQuietly(os);
				}
			}
			else
			{
				if(logger.isWarnEnabled()) logger.warn("Can't write {}!", currentFile.getAbsolutePath());
			}
		}
	}

	public static LilithPreferences exportPersistence(ApplicationPreferences prefs)
	{
		LilithPreferences p=new LilithPreferences();
		p.setGroovyConditions(exportGroovyConditions(prefs));
		p.setGroovyClipboardFormatters(exportClipboardFormatterScriptFiles(prefs));
		p.setDetailsView(exportDetailsView(prefs));
		p.setRootFiles(exportRootFiles(prefs));

		// String
		p.setBlackListName(prefs.getBlackListName());
		p.setWhiteListName(prefs.getWhiteListName());
		p.setLookAndFeel(prefs.getLookAndFeel());

		// boolean
		p.setAskingBeforeQuit(prefs.isAskingBeforeQuit());
		p.setAutoClosing(prefs.isAutoClosing());
		p.setAutoFocusingWindow(prefs.isAutoFocusingWindow());
		p.setAutoOpening(prefs.isAutoOpening());
		p.setCheckingForUpdate(prefs.isCheckingForUpdate());
		p.setCheckingForSnapshot(prefs.isCheckingForSnapshot());
		p.setCleaningLogsOnExit(prefs.isCleaningLogsOnExit());
		p.setColoringWholeRow(prefs.isColoringWholeRow());
		p.setGlobalLoggingEnabled(prefs.isGlobalLoggingEnabled());
		p.setHidingOnClose(prefs.isHidingOnClose());
		p.setLoggingStatisticEnabled(prefs.isLoggingStatisticEnabled());
		p.setMaximizingInternalFrames(prefs.isMaximizingInternalFrames());
		p.setMute(prefs.isMute());
		p.setScrollingToBottom(prefs.isScrollingToBottom());
		p.setShowingFullCallstack(prefs.isShowingFullCallstack());
		p.setUsingWrappedExceptionStyle(prefs.isUsingWrappedExceptionStyle());
		p.setShowingFullRecentPath(prefs.isShowingFullRecentPath());
		p.setShowingPrimaryIdentifier(prefs.isShowingPrimaryIdentifier());
		p.setShowingSecondaryIdentifier(prefs.isShowingSecondaryIdentifier());
		p.setShowingStatusbar(prefs.isShowingStatusBar());
		p.setShowingStackTrace(prefs.isShowingStackTrace());
		p.setShowingTipOfTheDay(prefs.isShowingTipOfTheDay());
		p.setShowingToolbar(prefs.isShowingToolbar());
		p.setSplashScreenDisabled(prefs.isSplashScreenDisabled());
		p.setTrayActive(prefs.isTrayActive());
		p.setUsingInternalFrames(prefs.isUsingInternalFrames());
		p.setSourceFiltering(prefs.getSourceFiltering());
		return p;
	}

	public static void importPersistence(ApplicationPreferences prefs, LilithPreferences p)
	{
		if(p.getGroovyConditions() != null)
		{
			importFiles(prefs.getGroovyConditionsPath(), p.getGroovyConditions());
		}
		if(p.getGroovyConditions() != null)
		{
			importFiles(prefs.getGroovyClipboardFormattersPath(), p.getGroovyClipboardFormatters());
		}
		if(p.getDetailsView() != null)
		{
			importFiles(prefs.getDetailsViewRoot(), p.getDetailsView());
		}
		if(p.getRootFiles() != null)
		{
			importFiles(prefs.getStartupApplicationPath(), p.getRootFiles());
		}

		// String
		prefs.setBlackListName(p.getBlackListName());
		prefs.setWhiteListName(p.getWhiteListName());
		prefs.setLookAndFeel(p.getLookAndFeel());

		// boolean
		prefs.setAskingBeforeQuit(p.isAskingBeforeQuit());
		prefs.setAutoClosing(p.isAutoClosing());
		prefs.setAutoFocusingWindow(p.isAutoFocusingWindow());
		prefs.setAutoOpening(p.isAutoOpening());
		prefs.setCheckingForUpdate(p.isCheckingForUpdate());
		prefs.setCheckingForSnapshot(p.isCheckingForSnapshot());
		prefs.setCleaningLogsOnExit(p.isCleaningLogsOnExit());
		prefs.setColoringWholeRow(p.isColoringWholeRow());
		prefs.setGlobalLoggingEnabled(p.isGlobalLoggingEnabled());
		prefs.setHidingOnClose(p.isHidingOnClose());
		prefs.setLoggingStatisticEnabled(p.isLoggingStatisticEnabled());
		prefs.setMaximizingInternalFrames(p.isMaximizingInternalFrames());
		prefs.setMute(p.isMute());
		prefs.setScrollingToBottom(p.isScrollingToBottom());
		prefs.setShowingFullCallstack(p.isShowingFullCallstack());
		prefs.setUsingWrappedExceptionStyle(p.isUsingWrappedExceptionStyle());
		prefs.setShowingFullRecentPath(p.isShowingFullRecentPath());
		prefs.setShowingPrimaryIdentifier(p.isShowingPrimaryIdentifier());
		prefs.setShowingSecondaryIdentifier(p.isShowingSecondaryIdentifier());
		prefs.setShowingStatusBar(p.isShowingStatusbar());
		prefs.setShowingStackTrace(p.isShowingStackTrace());
		prefs.setShowingTipOfTheDay(p.isShowingTipOfTheDay());
		prefs.setShowingToolbar(p.isShowingToolbar());
		prefs.setSplashScreenDisabled(p.isSplashScreenDisabled());
		prefs.setTrayActive(p.isTrayActive());
		prefs.setUsingInternalFrames(p.isUsingInternalFrames());
		prefs.setSourceFiltering(p.getSourceFiltering());
	}

	public static void exportPreferences(File file)
	{
		final Logger logger = LoggerFactory.getLogger(ImportExportCommand.class);

		ApplicationPreferences prefs = new ApplicationPreferences();

		LilithPreferences p = exportPersistence(prefs);

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

		ApplicationPreferences prefs = new ApplicationPreferences();

		try
		{
			LilithPreferences p = readPersistence(file);
			importPersistence(prefs, p);
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
			os = new GZIPOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
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
			is = new GZIPInputStream(new BufferedInputStream(new FileInputStream(file)));
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
