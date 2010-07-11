/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2010 Joern Huxhorn
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
package de.huxhorn.lilith;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;
import de.huxhorn.lilith.appender.InternalLilithAppender;
import de.huxhorn.lilith.handler.Slf4JHandler;
import de.huxhorn.lilith.swing.ApplicationPreferences;
import de.huxhorn.lilith.tools.ImportExportCommand;
import de.huxhorn.lilith.swing.LicenseAgreementDialog;
import de.huxhorn.lilith.swing.MainFrame;
import de.huxhorn.lilith.swing.SplashScreen;
import de.huxhorn.lilith.tools.CatCommand;
import de.huxhorn.lilith.tools.CreateMd5Command;
import de.huxhorn.lilith.tools.IndexCommand;
import de.huxhorn.lilith.tools.TailCommand;
import de.huxhorn.sulky.sounds.jlayer.JLayerSounds;
import de.huxhorn.sulky.swing.Windows;
import it.sauronsoftware.junique.AlreadyLockedException;
import it.sauronsoftware.junique.JUnique;
import it.sauronsoftware.junique.MessageHandler;
import org.apache.commons.cli.*;
import org.apache.commons.io.FileUtils;
import de.huxhorn.sulky.io.IOUtilities;
import org.apache.commons.io.IOUtils;
import org.simplericity.macify.eawt.Application;
import org.simplericity.macify.eawt.DefaultApplication;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Handler;

public class Lilith
{
	public static final String APP_NAME;
	public static final String APP_VERSION;
	//public static final String APP_BUILD_NUMBER;
	public static final long APP_TIMESTAMP;

	private static final String VERBOSE_SHORT = "v";
	private static final String PRINT_HELP_SHORT = "h";
	private static final String FLUSH_PREFERENCES_SHORT = "F";
	private static final String FLUSH_LICENSED_SHORT = "L";
	private static final String INDEX_SHORT = "i";
	private static final String ENABLE_BONJOUR_SHORT = "b";
	private static final String CREATE_MD5_SHORT = "m";
	private static final String CAT_SHORT = "c";
	private static final String TAIL_SHORT = "t";
	private static final String KEEP_RUNNING_SHORT = "f"; // tail
	private static final String NUMBER_OF_ENTRIES_SHORT = "n"; // cat & tail
	private static final String EXPORT_PREFERENCES_SHORT = "e";
	private static final String IMPORT_PREFERENCES_SHORT = "i";

	private static final String VERBOSE = "verbose";
	private static final String PRINT_HELP = "help";
	private static final String FLUSH_PREFERENCES = "flushPrefs";
	private static final String FLUSH_LICENSED = "flushLicensed";
	private static final String INDEX = "indexFile";
	private static final String ENABLE_BONJOUR = "bonjour";
	private static final String CREATE_MD5 = "md5";
	private static final String CAT = "cat";
	private static final String TAIL = "tail";
	private static final String KEEP_RUNNING = "keep-running"; // tail
	private static final String NUMBER_OF_ENTRIES = "numberOfLines"; // cat & tail
	private static final String EXPORT_PREFERENCES = "export-preferences";
	private static final String IMPORT_PREFERENCES = "import-preferences";

	private static final String JUNIQUE_MSG_SHOW = "Show";
	private static final String JUNIQUE_REPLY_OK = "OK";
	private static final String JUNIQUE_REPLY_UNKNOWN = "Unknown";


	private static Thread.UncaughtExceptionHandler uncaughtExceptionHandler;
	private static MainFrame mainFrame;

	static
	{
		// I access InternalLilithAppender *before* any Logger is used.
		// Otherwise an obscure ClassNotFoundException is thrown in MainFrame.
		InternalLilithAppender.getSourceIdentifier();

		final Logger logger = LoggerFactory.getLogger(Lilith.class);

		InputStream is = Lilith.class.getResourceAsStream("/app.properties");
		Properties p = new Properties();
		try
		{
			p.load(is);
		}
		catch(IOException ex)
		{
			if(logger.isErrorEnabled()) logger.error("Couldn't find app info resource!", ex);
			//ex.printStackTrace();
		}
		finally
		{
			IOUtilities.closeQuietly(is);
		}
		APP_NAME = p.getProperty("application.name");
		APP_VERSION = p.getProperty("application.version");
		//APP_BUILD_NUMBER = p.getProperty("application.buildNumber");
		String tsStr = p.getProperty("application.timestamp");
		long ts = -1;
		if(tsStr != null)
		{
			try
			{
				ts = Long.parseLong(tsStr);
			}
			catch(NumberFormatException ex)
			{
				if(logger.isErrorEnabled()) logger.error("Exception while reading timestamp!", ex);
			}
		}
		else
		{
			if(logger.isErrorEnabled()) logger.error("Application-timestamp not found!");
		}

		APP_TIMESTAMP = ts;
	}

	// TODO: - Shortcut in tooltip of toolbars...?
	// TODO: - check termination of every started thread

	public static void main(String args[])
	{
		{
			// initialize java.util.logging to use slf4j...
			Handler handler = new Slf4JHandler();
			java.util.logging.Logger rootLogger = java.util.logging.Logger.getLogger("");
			rootLogger.addHandler(handler);
			rootLogger.setLevel(java.util.logging.Level.WARNING);
		}

		CommandLineParser parser = new PosixParser();

		Options options = new Options();
		options.addOption(PRINT_HELP_SHORT, PRINT_HELP, false, "show this help.");
		options.addOption(VERBOSE_SHORT, VERBOSE, false, "show more info.");
		options.addOption(FLUSH_PREFERENCES_SHORT, FLUSH_PREFERENCES, false, "flush gui preferences.");
		options.addOption(FLUSH_LICENSED_SHORT, FLUSH_LICENSED, false, "flush licensed.");
		options.addOption(ENABLE_BONJOUR_SHORT, ENABLE_BONJOUR, false, "disable Bonjor.");
		options.addOption(INDEX_SHORT, INDEX, false, "indexes the given file.");
		options.addOption(CREATE_MD5_SHORT, CREATE_MD5, false, "create an MD5 checksum for the given file.");
		options.addOption(CAT_SHORT, CAT, false, "'cat' the given Lilith logfile.");
		options.addOption(TAIL_SHORT, TAIL, false, "'tail' the given Lilith logfile.");
		options.addOption(KEEP_RUNNING_SHORT, KEEP_RUNNING, false, "keep tailing the given Lilith logfile.");
		Option numberOfEntriesOption =
			new Option(NUMBER_OF_ENTRIES_SHORT, NUMBER_OF_ENTRIES, true, "number of entries printed by cat or tail");
		numberOfEntriesOption.setArgName("numberOfEntries");
		options.addOption(numberOfEntriesOption);

		Option exportFileOption =
			new Option(EXPORT_PREFERENCES_SHORT, EXPORT_PREFERENCES, true, "export preferences into the given file");
		exportFileOption.setArgName("exportFile");
		options.addOption(exportFileOption);

		Option importFileOption =
			new Option(IMPORT_PREFERENCES_SHORT, IMPORT_PREFERENCES, true, "import preferences from the given file");
		importFileOption.setArgName("importFile");
		options.addOption(importFileOption);

		boolean verbose = false;
		boolean flushPrefs = false;
		boolean flushLicensed = false;
		boolean enableBonjour = false;
		boolean indexFileOpt = false;
		boolean createMd5 = false;
		boolean cat = false;
		boolean tail = false;
		boolean keepRunning = false;
		boolean printHelp;
		String importFile=null;
		String exportFile=null;
		int numberOfEntries = -1;
		String[] originalArgs = args;
		int exitCode = 0;
		try
		{
			// parse the command line arguments
			CommandLine line = parser.parse(options, args);

			verbose = line.hasOption(VERBOSE_SHORT);
			printHelp = line.hasOption(PRINT_HELP_SHORT);
			flushPrefs = line.hasOption(FLUSH_PREFERENCES_SHORT);
			flushLicensed = line.hasOption(FLUSH_LICENSED_SHORT);
			enableBonjour = line.hasOption(ENABLE_BONJOUR_SHORT);
			indexFileOpt = line.hasOption(INDEX_SHORT);
			createMd5 = line.hasOption(CREATE_MD5_SHORT);
			cat = line.hasOption(CAT_SHORT);
			tail = line.hasOption(TAIL_SHORT);
			keepRunning = line.hasOption(KEEP_RUNNING_SHORT);
			if(line.hasOption(NUMBER_OF_ENTRIES_SHORT))
			{
				String numStr = line.getOptionValue(NUMBER_OF_ENTRIES_SHORT);
				try
				{
					numberOfEntries =Integer.parseInt(numStr);
				}
				catch(NumberFormatException ex)
				{
					final Logger logger = LoggerFactory.getLogger(Lilith.class);

					if(logger.isWarnEnabled()) logger.warn("Invalid value for option -"+ NUMBER_OF_ENTRIES_SHORT +": "+numStr);
				}
			}

			if(line.hasOption(IMPORT_PREFERENCES_SHORT))
			{
				importFile = line.getOptionValue(IMPORT_PREFERENCES_SHORT);
			}

			if(line.hasOption(EXPORT_PREFERENCES_SHORT))
			{
				exportFile = line.getOptionValue(EXPORT_PREFERENCES_SHORT);
			}

			args = line.getArgs(); // remaining unparsed args...
		}
		catch(ParseException exp)
		{
			final Logger logger = LoggerFactory.getLogger(Lilith.class);

			if(logger.isErrorEnabled()) logger.error("Exception while parsing options! {}", exp.getMessage());
			exitCode = -1;
			printHelp = true;
		}

		String appTitle = APP_NAME + " V" + APP_VERSION;// + "." + APP_BUILD_NUMBER;
		if(verbose)
		{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			Date d = new Date(APP_TIMESTAMP);

			appTitle += " - " + sdf.format(d);
		}
		if(!cat && !tail) // don't print info in case of cat or tail
		{
			System.out.println(" _     _ _ _ _   _     \n" +
				"| |   (_) (_) |_| |__  \n" +
				"| |   | | | | __| '_ \\ \n" +
				"| |___| | | | |_| | | |\n" +
				"|_____|_|_|_|\\__|_| |_|");
			System.out.println(appTitle);
			System.out.println("\nCopyright (C) 2007-2010  Joern Huxhorn\n\n" +
				"This program comes with ABSOLUTELY NO WARRANTY!\n\n" +
				"This is free software, and you are welcome to redistribute it\n" +
				"under certain conditions.\n" +
				"You should have received a copy of the GNU General Public License\n" +
				"along with this program.  If not, see <http://www.gnu.org/licenses/>.\n");
			System.out.println("Use commandline option -h to view help.\n\n");
		}

		if(verbose)
		{
			for(int i = 0; i < originalArgs.length; i++)
			{
				System.out.println("originalArgs[" + i + "]: " + originalArgs[i]);
			}
			for(int i = 0; i < args.length; i++)
			{
				System.out.println("args[" + i + "]: " + args[i]);
			}
			System.out.println("\n");

			initVerboseLogging();
		}

		final Logger logger = LoggerFactory.getLogger(Lilith.class);

		if(createMd5)
		{
			if(args.length < 1)
			{
				if(logger.isErrorEnabled()) logger.error("Missing file argument! -"+CREATE_MD5_SHORT+" requires an additional file!");
				System.exit(-1);
			}
			File input = new File(args[0]);

			if(CreateMd5Command.createMd5(input))
			{
				System.exit(0);
			}
			System.exit(-1);
		}

		if(cat)
		{
			if(args.length < 1)
			{
				if(logger.isErrorEnabled()) logger.error("Missing file argument! -"+CAT_SHORT+" requires an additional file!");
				System.exit(-1);
			}
			File input = new File(args[0]);
			String pattern=null;
			if(args.length > 1)
			{
				pattern = args[1];
			}
			if(CatCommand.catFile(input, pattern, numberOfEntries))
			{
				System.exit(0);
			}
			System.exit(-1);
		}

		if(tail)
		{
			if(args.length < 1)
			{
				if(logger.isErrorEnabled()) logger.error("Missing file argument! -"+TAIL_SHORT+" requires an additional file!");
				System.exit(-1);
			}
			File input = new File(args[0]);
			String pattern=null;
			if(args.length > 1)
			{
				pattern = args[1];
			}
			if(TailCommand.tailFile(input, pattern, numberOfEntries, keepRunning))
			{
				System.exit(0);
			}
			System.exit(-1);
		}

		if(exportFile != null)
		{
			exportPreferences(exportFile);
		}

		if(importFile != null)
		{
			importPreferences(importFile);
		}

		if(exportFile != null || importFile != null)
		{
			System.exit(0);
		}

		if(flushPrefs)
		{
			flushPreferences();
		}

		if(flushLicensed)
		{
			flushLicensed();
		}

		if(printHelp)
		{
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("lilith", options);
			System.exit(exitCode);
		}

		if(indexFileOpt)
		{
			if(args.length < 1)
			{
				if(logger.isErrorEnabled()) logger.error("Missing file argument! Usage: -"+INDEX_SHORT+" <logFile> [<indexFile>]");
				System.exit(-1);
			}

			String logFile=args[0];
			String indexFile=null;
			if(args.length >= 2)
			{
				indexFile=args[1];
			}
			else
			{
				IndexCommand.indexLogFile(args[0], null);
			}
			if(IndexCommand.indexLogFile(logFile, indexFile))
			{
				System.exit(0);
			}
			System.exit(-1);
		}

		startLilith(appTitle, enableBonjour);
	}

	private static void importPreferences(String file)
	{
		ImportExportCommand.importPreferences(new File(file));
	}

	private static void exportPreferences(String file)
	{
		ImportExportCommand.exportPreferences(new File(file));
	}

	private static void startLilith(String appTitle, boolean enableBonjour)
	{
		final Logger logger = LoggerFactory.getLogger(Lilith.class);

		uncaughtExceptionHandler = new Thread.UncaughtExceptionHandler()
		{
			public void uncaughtException(Thread t, Throwable e)
			{
				if(logger.isErrorEnabled()) logger.error("Caught an uncaught exception from thread "+t+"!", e);
				System.err.println("\n-----\nThread " + t.getName() + " threw an exception!");
				e.printStackTrace(System.err);
			}
		};

		Thread.setDefaultUncaughtExceptionHandler(uncaughtExceptionHandler);

		// preventing duplicate instances...
		try
		{
			JUnique.acquireLock(Lilith.class.getName(), new MessageHandler()
			{
				public String handle(String message)
				{
					return handleJUniqueMessage(message);
				}
			});
		}
		catch(AlreadyLockedException e)
		{
			if(logger.isInfoEnabled()) logger.info("Detected running instance, quitting.");
			String result=JUnique.sendMessage(Lilith.class.getName(),"Show");
			if(logger.isDebugEnabled()) logger.debug("JUnique result: {}", result);
			return;
		}
		// ok, we are the first instance this user has started...

		SwingUtilities.invokeLater(new Runnable()
		{

			public void run()
			{
				Thread.currentThread().setUncaughtExceptionHandler(uncaughtExceptionHandler);
			}
		});
		startUI(appTitle, enableBonjour);
	}

	private static void initVerboseLogging()
	{
		ILoggerFactory loggerFactory = LoggerFactory.getILoggerFactory();
		if(loggerFactory instanceof LoggerContext)
		{
			LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
			// reset previous configuration initially loaded from logback.xml
			loggerContext.reset();
			JoranConfigurator configurator = new JoranConfigurator();
			configurator.setContext(loggerContext);
			URL configUrl;
			configUrl = Lilith.class.getResource("/logbackVerbose.xml");
			try
			{
				configurator.doConfigure(configUrl);
				final Logger logger = LoggerFactory.getLogger(Lilith.class);

				if(logger.isDebugEnabled()) logger.debug("Configured logging with {}.", configUrl);
				StatusPrinter.print(loggerContext);
			}
			catch(JoranException ex)
			{
				final Logger logger = LoggerFactory.getLogger(Lilith.class);

				if(logger.isErrorEnabled()) logger.error("Error configuring logging framework!", ex);
				StatusPrinter.print(loggerContext);
			}
		}
	}


	private static void flushLicensed()
	{
		final Logger logger = LoggerFactory.getLogger(Lilith.class);

		ApplicationPreferences prefs = new ApplicationPreferences();
		prefs.setLicensed(false);
		if(logger.isInfoEnabled()) logger.info("Flushed licensed...");
		System.exit(0);
	}

	private static void flushPreferences()
	{
		final Logger logger = LoggerFactory.getLogger(Lilith.class);
		ApplicationPreferences prefs = new ApplicationPreferences();
		prefs.reset();
		prefs.setLicensed(false);
		if(logger.isInfoEnabled()) logger.info("Flushed preferences...");
		System.exit(0);
	}

	private static String handleJUniqueMessage(String msg)
	{
		if(JUNIQUE_MSG_SHOW.equals(msg))
		{
			showMainFrame();
			return JUNIQUE_REPLY_OK;
		}
		return JUNIQUE_REPLY_UNKNOWN;
	}

	private static void showMainFrame()
	{
		if(mainFrame != null)
		{
			final MainFrame frame = mainFrame;
			SwingUtilities.invokeLater(new Runnable()
			{

				public void run()
				{

					if(frame.isVisible())
					{
						frame.setVisible(false);
					}
					Windows.showWindow(frame, null, false);
					frame.toFront();
				}
			});
		}
	}

	private static void updateSplashStatus(final SplashScreen splashScreen, final String status)
		throws InvocationTargetException, InterruptedException
	{
		if(splashScreen != null)
		{
			SwingUtilities.invokeAndWait(new Runnable()
			{

				public void run()
				{
					if(!splashScreen.isVisible())
					{
						Windows.showWindow(splashScreen, null, true);
					}
					splashScreen.toFront();
					splashScreen.setStatusText(status);
				}
			});
		}
	}

	private static void hideSplashScreen(final SplashScreen splashScreen)
		throws InvocationTargetException, InterruptedException
	{
		if(splashScreen != null)
		{
			SwingUtilities.invokeAndWait(new Runnable()
			{
				public void run()
				{
					splashScreen.setVisible(false);
				}
			});
		}
	}

	public static void startUI(final String appTitle, boolean enableBonjour)
	{
		final Logger logger = LoggerFactory.getLogger(Lilith.class);
		UIManager.installLookAndFeel("JGoodies Windows", "com.jgoodies.looks.windows.WindowsLookAndFeel");
		UIManager.installLookAndFeel("JGoodies Plastic", "com.jgoodies.looks.plastic.PlasticLookAndFeel");
		UIManager.installLookAndFeel("JGoodies Plastic 3D", "com.jgoodies.looks.plastic.Plastic3DLookAndFeel");
		UIManager.installLookAndFeel("JGoodies Plastic XP", "com.jgoodies.looks.plastic.PlasticXPLookAndFeel");
		//UIManager.installLookAndFeel("Napkin", "net.sourceforge.napkinlaf.NapkinLookAndFeel");
		Application application = new DefaultApplication();
		ApplicationPreferences applicationPreferences = new ApplicationPreferences();

		//final String[] defaultNames={"MenuBarUI", "MenuUI", "MenuItemUI", "CheckBoxMenuItemUI", "RadioButtonMenuItemUI", "PopupMenuUI"};

		HashMap<String, Object> storedDefaults = new HashMap<String, Object>();
		if(application.isMac())
		{
			// Use Apple Aqua L&F screen menu bar if available; set property before any frames created
			try
			{
				//System.setProperty("apple.awt.brushMetalLook", "true");
				System.setProperty("apple.laf.useScreenMenuBar", "true");
			}
			catch(Exception e)
			{
				// try the older menu bar property
				System.setProperty("com.apple.macos.useScreenMenuBar", "true");
				// this shouldn't happen since we only run on 1.5+
			}

			// this is part 1 of Mac Menu for all PLAFs.
			// Thanks to Kirill Grouchnikov - http://www.pushing-pixels.org/?p=366
			/*
			Does not work, exception while displaying popup menu:
			java.lang.NullPointerException
				at com.apple.laf.AquaMenuPainter.paintSelectedMenuItemBackground(AquaMenuPainter.java:147)
				at com.apple.laf.AquaMenuItemUI.paintBackground(AquaMenuItemUI.java:93)
				at com.apple.laf.AquaMenuPainter.paintMenuItem(AquaMenuPainter.java:192)
				at com.apple.laf.AquaMenuItemUI.paintMenuItem(AquaMenuItemUI.java:66)
				at javax.swing.plaf.basic.BasicMenuItemUI.paint(BasicMenuItemUI.java:594)
				at com.apple.laf.AquaMenuItemUI.update(AquaMenuItemUI.java:82)
				at javax.swing.JComponent.paintComponent(JComponent.java:763)
				at javax.swing.JComponent.paint(JComponent.java:1027)
				at javax.swing.JComponent.paintChildren(JComponent.java:864)
				at javax.swing.JComponent.paint(JComponent.java:1036)
				at javax.swing.JComponent.paintChildren(JComponent.java:864)
				at javax.swing.JComponent.paint(JComponent.java:1036)
				at javax.swing.JComponent.paintChildren(JComponent.java:864)
				at javax.swing.JComponent.paint(JComponent.java:1036)
				at javax.swing.JLayeredPane.paint(JLayeredPane.java:564)
				at javax.swing.JComponent.paintChildren(JComponent.java:864)
				at javax.swing.JComponent.paint(JComponent.java:1036)
				at javax.swing.JComponent._paintImmediately(JComponent.java:5096)
				at javax.swing.JComponent.paintImmediately(JComponent.java:4880)
				at javax.swing.RepaintManager.paintDirtyRegions(RepaintManager.java:829)
				at javax.swing.RepaintManager.paintDirtyRegions(RepaintManager.java:714)
				at javax.swing.RepaintManager.seqPaintDirtyRegions(RepaintManager.java:694)
				at javax.swing.SystemEventQueueUtilities$ComponentWorkRequest.run(SystemEventQueueUtilities.java:128)
				at java.awt.event.InvocationEvent.dispatch(InvocationEvent.java:209)
				at java.awt.EventQueue.dispatchEvent(EventQueue.java:633)
				at java.awt.EventDispatchThread.pumpOneEventForFilters(EventDispatchThread.java:296)
				at java.awt.EventDispatchThread.pumpEventsForFilter(EventDispatchThread.java:211)
				at java.awt.EventDispatchThread.pumpEventsForHierarchy(EventDispatchThread.java:201)
				at java.awt.EventDispatchThread.pumpEvents(EventDispatchThread.java:196)
				at java.awt.EventDispatchThread.pumpEvents(EventDispatchThread.java:188)
				at java.awt.EventDispatchThread.run(EventDispatchThread.java:122)
			*/
			/*
			try
			{
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

				for(String current : defaultNames)
				{
					storedDefaults.put(current, UIManager.get(current));
				}
			}
			catch(Throwable t)
			{
				if(logger.isErrorEnabled()) logger.error("Exception while setting SystemLookAndFeel!!", t);
			}
			*/
		}

		// init look & feel
		String lookAndFeel = applicationPreferences.getLookAndFeel();
		if(lookAndFeel != null)
		{
			try
			{
				for(UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels())
				{
					if(lookAndFeel.equals(info.getName()))
					{
						String lafClassName = info.getClassName();
						if(logger.isDebugEnabled()) logger.debug("Setting look&feel to {}.", lafClassName);
						UIManager.setLookAndFeel(lafClassName);

						if(application.isMac() && lafClassName.equals(UIManager.getSystemLookAndFeelClassName()))
						{
							// Use Apple Aqua L&F screen menu bar if available; set property before any frames created
							try
							{
								System.setProperty("apple.laf.useScreenMenuBar", "true");
							}
							catch(Exception e)
							{
								// try the older menu bar property
								System.setProperty("com.apple.macos.useScreenMenuBar", "true");
								// this shouldn't happen since we only run on 1.5+
							}
						}


						break;
					}
				}
			}
			catch(Throwable t)
			{
				if(logger.isErrorEnabled())
				{
					logger.error("Exception while setting look & feel '" + lookAndFeel + "'!", t);
				}
			}

			// this is part 2 of Mac Menu for all PLAFs.
			// Thanks to Kirill Grouchnikov - http://www.pushing-pixels.org/?p=366
			if(logger.isDebugEnabled()) logger.debug("storedDefaults: {}", storedDefaults);
			for(Map.Entry<String, Object> current : storedDefaults.entrySet())
			{
				UIManager.put(current.getKey(), current.getValue());
			}
		}

		boolean splashScreenDisabled = applicationPreferences.isSplashScreenDisabled();
		try
		{
			SplashScreen splashScreen = null;
			if(!splashScreenDisabled)
			{
				CreateSplashRunnable createRunnable = new CreateSplashRunnable(appTitle);
				SwingUtilities.invokeAndWait(createRunnable);
				splashScreen = createRunnable.getSplashScreen();
				Thread.sleep(500); // so the splash gets the chance to get displayed :(
				updateSplashStatus(splashScreen, "Initialized application preferences...");
			}

			File startupApplicationPath = applicationPreferences.getStartupApplicationPath();
			if(startupApplicationPath.mkdirs())
			{
				if(logger.isDebugEnabled()) logger.debug("Created '{}'.", startupApplicationPath.getAbsolutePath());
			}

			// System.err redirection
			{
				File errorLog = new File(startupApplicationPath, "errors.log");
				boolean freshFile = false;
				if(!errorLog.isFile())
				{
					freshFile = true;
				}
				try
				{
					FileOutputStream fos = new FileOutputStream(errorLog, true);
					PrintStream ps = new PrintStream(fos, true);
					if(!freshFile)
					{
						ps.println("----------------------------------------");
					}
					SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
					ps.println("Started " + APP_NAME + " V" + APP_VERSION + " at " + format.format(new Date()));
					System.setErr(ps);
					if(logger.isInfoEnabled()) logger.info("Writing System.err to '{}'.", errorLog.getAbsolutePath());
				}
				catch(FileNotFoundException e)
				{
					e.printStackTrace();
				}
			}

			File prevPathFile = new File(startupApplicationPath, ApplicationPreferences.PREVIOUS_APPLICATION_PATH_FILENAME);
			if(prevPathFile.isFile())
			{
				updateSplashStatus(splashScreen, "Moving application path content...");
				moveApplicationPathContent(prevPathFile, startupApplicationPath);
			}
			if(!applicationPreferences.isLicensed())
			{
				hideSplashScreen(splashScreen);

				LicenseAgreementDialog licenseDialog = new LicenseAgreementDialog();
				Windows.showWindow(licenseDialog, null, true);
				if(licenseDialog.isLicenseAgreed())
				{
					applicationPreferences.setLicensed(true);
				}
				else
				{
					if(logger.isWarnEnabled()) logger.warn("Didn't accept license! Exiting...");
					System.exit(-1);
				}
			}

			updateSplashStatus(splashScreen, "Creating main window...");
			CreateMainFrameRunnable createMain = new CreateMainFrameRunnable(applicationPreferences, splashScreen, appTitle, enableBonjour);
			SwingUtilities.invokeAndWait(createMain);
			final MainFrame frame = createMain.getMainFrame();
			if(logger.isDebugEnabled()) logger.debug("After show...");
			updateSplashStatus(splashScreen, "Initializing application...");
			SwingUtilities.invokeAndWait(new Runnable()
			{

				public void run()
				{
					frame.startUp();
				}
			});
			hideSplashScreen(splashScreen);
			mainFrame=frame;

		}
		catch(InterruptedException ex)
		{
			if(logger.isInfoEnabled()) logger.info("Interrupted...", ex);
		}
		catch(InvocationTargetException ex)
		{
			if(logger.isWarnEnabled()) logger.warn("InvocationTargetException...", ex);
			if(logger.isWarnEnabled()) logger.warn("Target-Exception: ", ex.getTargetException());

		}
	}


	static class CreateSplashRunnable
		implements Runnable
	{
		private SplashScreen splashScreen;
		private String appTitle;

		public CreateSplashRunnable(String appTitle)
		{
			this.appTitle = appTitle;
		}

		public void run()
		{
			splashScreen = new SplashScreen(appTitle);
			Windows.showWindow(splashScreen, null, true);
		}

		public SplashScreen getSplashScreen()
		{
			return splashScreen;
		}
	}

	static class CreateMainFrameRunnable
		implements Runnable
	{
		private SplashScreen splashScreen;
		private MainFrame mainFrame;
		private ApplicationPreferences applicationPreferences;
		private String appTitle;
		private boolean enableBonjour;

		public CreateMainFrameRunnable(ApplicationPreferences applicationPreferences, SplashScreen splashScreen, String appTitle, boolean enableBonjour)
		{
			this.splashScreen = splashScreen;
			this.enableBonjour = enableBonjour;
			this.appTitle = appTitle;
			this.applicationPreferences = applicationPreferences;
		}

		public void run()
		{
			mainFrame = new MainFrame(applicationPreferences, splashScreen, appTitle, enableBonjour);
			mainFrame.setSounds(new JLayerSounds());
			mainFrame.setSize(1024, 768);
			Windows.showWindow(mainFrame, null, false);
		}

		public MainFrame getMainFrame()
		{
			return mainFrame;
		}
	}

	/**
	 * @param prevPathFile           the file that contains (!!!) the previous application path - not the previous application path itself!
	 * @param startupApplicationPath the current application path, i.e. the destination path.
	 */
	private static void moveApplicationPathContent(File prevPathFile, File startupApplicationPath)
	{
		final Logger logger = LoggerFactory.getLogger(Lilith.class);

		InputStream is = null;
		String prevPathStr = null;
		try
		{
			is = new FileInputStream(prevPathFile);
			prevPathStr = IOUtils.toString(is);
		}
		catch(IOException ex)
		{
			if(logger.isWarnEnabled()) logger.warn("Exception while reading previous application path!", ex);
		}
		finally
		{
			IOUtilities.closeQuietly(is);
		}
		if(prevPathStr != null)
		{
			File prevPath = new File(prevPathStr);
			try
			{
				FileUtils.copyDirectory(prevPath, startupApplicationPath);
				FileUtils.deleteDirectory(prevPath);
			}
			catch(IOException ex)
			{
				if(logger.isWarnEnabled())
				{
					logger.warn("Exception while moving content of previous application path '" + prevPath
						.getAbsolutePath() + "' to new one '" + startupApplicationPath.getAbsolutePath() + "'!", ex);
				}
			}
			if(logger.isInfoEnabled())
			{
				logger
					.info("Moved content from previous application path '{}' to new application path '{}'.", prevPath.getAbsolutePath(), startupApplicationPath.getAbsolutePath());
			}
		}
		if(prevPathFile.delete())
		{
			if(logger.isDebugEnabled()) logger.debug("Deleted {}.", prevPathFile.getAbsolutePath());
		}
	}
}
