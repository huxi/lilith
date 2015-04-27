/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2013 Joern Huxhorn
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
import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import de.huxhorn.lilith.appender.InternalLilithAppender;
import de.huxhorn.lilith.cli.Cat;
import de.huxhorn.lilith.cli.CommandLineArgs;
import de.huxhorn.lilith.cli.Filter;
import de.huxhorn.lilith.cli.Help;
import de.huxhorn.lilith.cli.Index;
import de.huxhorn.lilith.cli.Md5;
import de.huxhorn.lilith.cli.Tail;
import de.huxhorn.lilith.handler.Slf4JHandler;
import de.huxhorn.lilith.swing.ApplicationPreferences;
import de.huxhorn.lilith.tools.FilterCommand;
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
import org.apache.commons.io.FileUtils;
import de.huxhorn.sulky.io.IOUtilities;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.JavaVersion;
import org.apache.commons.lang3.SystemUtils;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.simplericity.macify.eawt.Application;
import org.simplericity.macify.eawt.DefaultApplication;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Handler;

public class Lilith
{
	/**
	 * Application name
	 */
	public static final String APP_NAME;

	/**
	 * Version string *including* a -SNAPSHOT, if available
	 */
	public static final String APP_VERSION;

	/**
	 * Version string *excluding* the -SNAPSHOT
	 */
	public static final String APP_PLAIN_VERSION;

	/**
	 * true if APP_VERSION ends in -SNAPSHOT, false otherwise.
	 */
	public static final boolean APP_SNAPSHOT;

	/**
	 * The git revision of this version
	 */
	public static final String APP_REVISION;

	/**
	 * Long containing the timestamp of the build.
	 */
	public static final long APP_TIMESTAMP;

	/**
	 * The timestamp of the build formatted as a date.
	 */
	public static final String APP_TIMESTAMP_DATE;

	public static final VersionBundle APP_VERSION_BUNDLE;

	private static final String SNAPSHOT_POSTFIX = "-SNAPSHOT";
	
	private static final String JUNIQUE_MSG_SHOW = "Show";
	private static final String JUNIQUE_REPLY_OK = "OK";
	private static final String JUNIQUE_REPLY_UNKNOWN = "Unknown";

	private static Thread.UncaughtExceptionHandler uncaughtExceptionHandler;
	private static MainFrame mainFrame;
	private static DateTimeFormatter isoDateTimeFormat = ISODateTimeFormat.dateTime().withZoneUTC();
	private static DateTimeFormatter isoDateTimeFormatLocal = ISODateTimeFormat.dateTime();

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
		boolean snapshot=false;
		String plainVersion=APP_VERSION;
		if(plainVersion != null && plainVersion.endsWith(SNAPSHOT_POSTFIX))
		{
			snapshot = true;
			plainVersion = plainVersion.substring(0, plainVersion.length()-SNAPSHOT_POSTFIX.length());
		}
		APP_SNAPSHOT = snapshot;
		APP_PLAIN_VERSION = plainVersion;

		APP_REVISION = p.getProperty("application.revision");
		String tsStr = p.getProperty("application.timestamp");
		long ts = -1;
		String dateStr = null;
		if(tsStr != null)
		{
			try
			{
				ts = Long.parseLong(tsStr);
				dateStr = isoDateTimeFormat.print(ts);
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
		APP_TIMESTAMP_DATE = dateStr;
		APP_VERSION_BUNDLE = new VersionBundle(APP_PLAIN_VERSION, APP_TIMESTAMP);
		if(APP_VERSION != null)
		{
			System.setProperty("lilith.version", APP_VERSION);
		}
		if(APP_TIMESTAMP > -1)
		{
			System.setProperty("lilith.timestamp.milliseconds", ""+APP_TIMESTAMP);
		}
		if(APP_TIMESTAMP_DATE != null)
		{
			System.setProperty("lilith.timestamp", APP_TIMESTAMP_DATE);
		}
		if(APP_REVISION != null)
		{
			System.setProperty("lilith.revision", APP_REVISION);
		}
	}

	// TODO: - Shortcut in tooltip of toolbars...?
	// TODO: - check termination of every started thread

	public static void main(String[] argv)
	{
		{
			// initialize java.util.logging to use slf4j...
			Handler handler = new Slf4JHandler();
			java.util.logging.Logger rootLogger = java.util.logging.Logger.getLogger("");
			rootLogger.addHandler(handler);
			rootLogger.setLevel(java.util.logging.Level.WARNING);
		}

		StringBuilder appTitle = new StringBuilder();
		appTitle.append(APP_NAME).append(" V").append(APP_VERSION);
		if(APP_SNAPSHOT)
		{
			// always append timestamp for SNAPSHOT
			appTitle.append(" (").append(APP_TIMESTAMP_DATE).append(")");
		}

		CommandLineArgs cl=new CommandLineArgs();
		JCommander commander = new JCommander(cl);
		Cat cat = new Cat();
		commander.addCommand(Cat.NAME, cat);
		Tail tail = new Tail();
		commander.addCommand(Tail.NAME, tail);
		Filter filter = new Filter();
		commander.addCommand(Filter.NAME, filter);
		Index index = new Index();
		commander.addCommand(Index.NAME, index);
		Md5 md5 = new Md5();
		commander.addCommand(Md5.NAME, md5);
		Help help = new Help();
		commander.addCommand(Help.NAME, help);

		try
		{
			commander.parse(argv);
		}
		catch(ParameterException ex)
		{
			printAppInfo(appTitle.toString(), false);
			System.out.println(ex.getMessage()+"\n");
			printHelp(commander);
			System.exit(-1);
		}
		if(cl.verbose)
		{
			if(!APP_SNAPSHOT)
			{
				// timestamp is always appended for SNAPSHOT
				// don't append it twice
				appTitle.append(" (").append(APP_TIMESTAMP_DATE).append(")");
			}
			appTitle.append(" - ").append(APP_REVISION);
		}

		String appTitleString = appTitle.toString();
		if(cl.showHelp)
		{
			printAppInfo(appTitleString, false);
			printHelp(commander);
			System.exit(0);
		}

		String command = commander.getParsedCommand();
		if(!Tail.NAME.equals(command) && !Cat.NAME.equals(command) && !Filter.NAME.equals(command)) // don't print info in case of cat, tail or filter
		{
			printAppInfo(appTitleString, true);
		}

		if(cl.logbackConfig != null)
		{
			File logbackFile = new File(cl.logbackConfig);
			if(!logbackFile.isFile())
			{
				System.out.println(logbackFile.getAbsolutePath() + " is not a valid file.");
				System.exit(-1);
			}
			try
			{
				initLogbackConfig(logbackFile.toURI().toURL());
			}
			catch(MalformedURLException e)
			{
				System.out.println("Failed to convert "+logbackFile.getAbsolutePath()+" to URL. "+e);
				System.exit(-1);
			}
		}
		else if(cl.verbose)
		{
			initVerboseLogging();
		}

		if(cl.printBuildTimestamp)
		{
			System.out.println("Build-Timestamp: " + APP_TIMESTAMP);
			System.out.println("Build-Date     : " + APP_TIMESTAMP_DATE);
			System.out.println("Build-Revision : " + APP_REVISION);
			System.exit(0);
		}

		if(Help.NAME.equals(command))
		{
			commander.usage();
			if(help.commands == null || help.commands.size()==0)
			{
				commander.usage(Help.NAME);
			}
			else
			{
				Map<String, JCommander> commands = commander.getCommands();
				for(String current : help.commands)
				{
					if(commands.containsKey(current))
					{
						commander.usage(current);
					}
					else
					{
						System.out.println("Unknown command '"+current+"'!");
					}
				}
			}
			System.exit(0);
		}
		
		if(Md5.NAME.equals(command))
		{
			List<String> files = md5.files;
			if(files == null || files.size()==0)
			{
				printHelp(commander);
				System.exit(-1);
			}
			boolean error=false;
			for(String current:files)
			{
				if(!CreateMd5Command.createMd5(new File(current)))
				{
					error=true;
				}
			}
			if(error)
			{
				System.exit(-1);
			}
			System.exit(0);
		}

		if(Index.NAME.equals(command))
		{
			if(!cl.verbose && cl.logbackConfig == null)
			{
				initCLILogging();
			}
			List<String> files = index.files;
			if(files == null || files.size()==0)
			{
				printHelp(commander);
				System.exit(-1);
			}
			boolean error=false;
			for(String current:files)
			{
				if(!IndexCommand.indexLogFile(new File(current)))
				{
					error=true;
				}
			}
			if(error)
			{
				System.exit(-1);
			}
			System.exit(0);
		}

		if(Cat.NAME.equals(command))
		{
			if(!cl.verbose && cl.logbackConfig == null)
			{
				initCLILogging();
			}
			List<String> files = cat.files;
			if(files == null || files.size()!=1)
			{
				printHelp(commander);
				System.exit(-1);
			}
			if(CatCommand.catFile(new File(files.get(0)), cat.pattern, cat.numberOfLines))
			{
				System.exit(0);
			}
			System.exit(-1);
		}

		if(Tail.NAME.equals(command))
		{
			if(!cl.verbose && cl.logbackConfig == null)
			{
				initCLILogging();
			}
			List<String> files = tail.files;
			if(files == null || files.size()!=1)
			{
				printHelp(commander);
				System.exit(-1);
			}
			if(TailCommand.tailFile(new File(files.get(0)), tail.pattern, tail.numberOfLines, tail.keepRunning))
			{
				System.exit(0);
			}
			System.exit(-1);
		}

		if(Filter.NAME.equals(command))
		{
			if(!cl.verbose && cl.logbackConfig == null)
			{
				initCLILogging();
			}
			if(FilterCommand.filterFile(new File(filter.input), new File(filter.output), new File(filter.condition), filter.searchString, filter.pattern, filter.overwrite, filter.keepRunning, filter.exclusive))
			{
				System.exit(0);
			}
			System.exit(-1);
		}

		if(cl.flushPreferences)
		{
			flushPreferences();
		}

		if(cl.exportPreferencesFile != null)
		{
			exportPreferences(cl.exportPreferencesFile);
		}

		if(cl.importPreferencesFile != null)
		{
			importPreferences(cl.importPreferencesFile);
		}

		if(cl.exportPreferencesFile != null || cl.importPreferencesFile != null)
		{
			System.exit(0);
		}

		if(cl.flushLicensed)
		{
			flushLicensed();
		}

		startLilith(appTitleString, cl.enableBonjour);
	}

	private static void printHelp(JCommander commander)
	{
		commander.usage();

		String command = commander.getParsedCommand();
		if(command != null)
		{
			commander.usage(command);
		}
	}

	private static void printAppInfo(String appTitle, boolean printHelpInfo)
	{
		System.out.println(
			" _     _ _ _ _   _     \n" +
			"| |   (_) (_) |_| |__  \n" +
			"| |   | | | | __| '_ \\ \n" +
			"| |___| | | | |_| | | |\n" +
			"|_____|_|_|_|\\__|_| |_|");
		System.out.println(appTitle);
		System.out.println("http://lilithapp.com");
		System.out.println("\nCopyright (C) 2007-2013 Joern Huxhorn\n\n" +
			"This program comes with ABSOLUTELY NO WARRANTY!\n\n" +
			"This is free software, and you are welcome to redistribute it\n" +
			"under certain conditions.\n" +
			"You should have received a copy of the GNU General Public License\n" +
			"along with this program.  If not, see <http://www.gnu.org/licenses/>.\n");
		if(printHelpInfo)
		{
			System.out.println("Use commandline option -h to view help.\n");
		}
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
				if(logger.isErrorEnabled()) logger.error("Caught an uncaught exception from thread {}!", t, e);
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

	private static void initCLILogging()
	{
		initLogbackConfig(Lilith.class.getResource("/logbackCLI.xml"));
	}

	private static void initVerboseLogging()
	{
		initLogbackConfig(Lilith.class.getResource("/logbackVerbose.xml"));
	}

	private static void initLogbackConfig(URL configUrl)
	{
		ILoggerFactory loggerFactory = LoggerFactory.getILoggerFactory();
		if(loggerFactory instanceof LoggerContext)
		{
			LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
			// reset previous configuration initially loaded from logback.xml
			loggerContext.reset();
			JoranConfigurator configurator = new JoranConfigurator();
			configurator.setContext(loggerContext);
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
		if(SystemUtils.isJavaVersionAtLeast(JavaVersion.JAVA_1_6))
		{
			// Substance requires 1.6
			UIManager.installLookAndFeel("Substance Dark - Twilight", "org.pushingpixels.substance.api.skin.SubstanceTwilightLookAndFeel");
			UIManager.installLookAndFeel("Substance Light - Business", "org.pushingpixels.substance.api.skin.SubstanceBusinessLookAndFeel");
		}

		//UIManager.installLookAndFeel("Napkin", "net.sourceforge.napkinlaf.NapkinLookAndFeel");

		// This instance of application is only used to query some info. The real one is in MainFrame.
		Application application = new DefaultApplication();
		ApplicationPreferences applicationPreferences = new ApplicationPreferences();

		//final String[] defaultNames={"MenuBarUI", "MenuUI", "MenuItemUI", "CheckBoxMenuItemUI", "RadioButtonMenuItemUI", "PopupMenuUI"};

		HashMap<String, Object> storedDefaults = new HashMap<>();
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
				if(logger.isErrorEnabled()) logger.error("Exception while setting look & feel '{}'!", lookAndFeel, t);
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
					ps.println("Started " + APP_NAME + " V" + APP_VERSION + " at " + isoDateTimeFormatLocal.print(System.currentTimeMillis()));
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
