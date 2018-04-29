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

package de.huxhorn.lilith;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.gaffer.GafferConfigurator;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.status.ErrorStatus;
import ch.qos.logback.core.status.Status;
import ch.qos.logback.core.status.StatusManager;
import ch.qos.logback.core.status.StatusUtil;
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
import de.huxhorn.lilith.logback.tools.ContextHelper;
import de.huxhorn.lilith.swing.ApplicationPreferences;
import de.huxhorn.lilith.swing.LicenseAgreementDialog;
import de.huxhorn.lilith.swing.MainFrame;
import de.huxhorn.lilith.swing.SplashScreen;
import de.huxhorn.lilith.tools.CatCommand;
import de.huxhorn.lilith.tools.CreateMd5Command;
import de.huxhorn.lilith.tools.FilterCommand;
import de.huxhorn.lilith.tools.ImportExportCommand;
import de.huxhorn.lilith.tools.IndexCommand;
import de.huxhorn.lilith.tools.TailCommand;
import de.huxhorn.sulky.formatting.SafeString;
import de.huxhorn.sulky.sounds.jlayer.JLayerSounds;
import de.huxhorn.sulky.swing.Windows;
import it.sauronsoftware.junique.AlreadyLockedException;
import it.sauronsoftware.junique.JUnique;
import java.awt.EventQueue;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Instant;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import javax.swing.UIManager;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;

@SuppressWarnings({"PMD.SystemPrintln", "PMD.DoNotCallSystemExit", "PMD.AvoidPrintStackTrace"})
public class Lilith
{
	static
	{
		SLF4JBridgeHandler.removeHandlersForRootLogger();  // (since SLF4J 1.6.5)
		SLF4JBridgeHandler.install();
	}

	// uncomment the code below to enable flying saucer logging......
	/*
	static
	{
		System.getProperties().setProperty("xr.util-logging.loggingEnabled", "true");
		XRLog.setLoggingEnabled(true);
	}
    */

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

	private static final String APPLE_SCREEN_MENU_BAR_SYSTEM_PROPERTY = "apple.laf.useScreenMenuBar";
	private static final String GROOVY_EXTENSION = ".groovy";

	private static final String[] STATUS_TEXT=
			{
					"INFO : ",
					"WARN : ",
					"ERROR: ",
			};

	private static Thread.UncaughtExceptionHandler uncaughtExceptionHandler;
	private static MainFrame mainFrame;

	static {
		new Lilith(); // stfu
	}

	private Lilith() {}

	private static class UncaughtExceptionHandler
		implements Thread.UncaughtExceptionHandler
	{
		private final Logger logger = LoggerFactory.getLogger(UncaughtExceptionHandler.class);

		@Override
		public void uncaughtException(Thread t, Throwable e)
		{
			if(logger.isErrorEnabled()) logger.error("Caught an uncaught exception from thread {}!", t, e);
			System.err.println("\n-----\nThread " + t.getName() + " threw an exception!");
			e.printStackTrace();
		}
	}

	static
	{
		// I access InternalLilithAppender *before* any Logger is used.
		// Otherwise an obscure ClassNotFoundException is thrown in MainFrame.
		InternalLilithAppender.getSourceIdentifier();

		final Logger logger = LoggerFactory.getLogger(Lilith.class);


		Properties p = new Properties();
		try(InputStream is = Lilith.class.getResourceAsStream("/app.properties"))
		{
			p.load(is);
		}
		catch(IOException ex)
		{
			if(logger.isErrorEnabled()) logger.error("Couldn't find app info resource!", ex);
			//ex.printStackTrace();
		}
		APP_NAME = p.getProperty("application.name");
		APP_VERSION = p.getProperty("application.version");
		boolean snapshot=false;
		String plainVersion=APP_VERSION != null ? APP_VERSION : "n/a";
		if(plainVersion.endsWith(SNAPSHOT_POSTFIX))
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
				dateStr = SafeString.toString(new Date(ts));
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
			System.setProperty("lilith.version.bundle", APP_VERSION_BUNDLE.toString());
		}
		if(APP_TIMESTAMP > -1)
		{
			System.setProperty("lilith.timestamp.milliseconds", Long.toString(APP_TIMESTAMP));
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

	public static void main(String[] args)
	{
		StringBuilder appTitle = new StringBuilder();
		appTitle.append(APP_NAME).append(" V").append(APP_VERSION);
		if(APP_SNAPSHOT)
		{
			// always append timestamp for SNAPSHOT
			appTitle.append(" (").append(APP_TIMESTAMP_DATE).append(')');
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
			commander.parse(args);
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
				appTitle.append(" (").append(APP_TIMESTAMP_DATE).append(')');
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
			System.out.println("Build-Date     : " + APP_TIMESTAMP_DATE);
			System.out.println("Build-Revision : " + APP_REVISION);
			System.out.println("Build-Timestamp: " + APP_TIMESTAMP);
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
			if(files == null || files.isEmpty())
			{
				printHelp(commander);
				System.out.println("Missing files!");
				System.exit(-1);
			}
			boolean error=false;
			for(String current:files)
			{
				if(!CreateMd5Command.createMd5(current))
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
			if(files == null || files.isEmpty())
			{
				printHelp(commander);
				System.exit(-1);
			}
			boolean error=false;
			for(String current:files)
			{
				if(!IndexCommand.indexLogFile(current))
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

		startLilith(appTitleString);
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
		System.out.println("\nCopyright (C) 2007-2017 Joern Huxhorn\n\n" +
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

	private static void startLilith(String appTitle)
	{
		final Logger logger = LoggerFactory.getLogger(Lilith.class);

		uncaughtExceptionHandler = new UncaughtExceptionHandler();

		Thread.setDefaultUncaughtExceptionHandler(uncaughtExceptionHandler);

		// preventing duplicate instances...
		try
		{
			JUnique.acquireLock(Lilith.class.getName(), Lilith::handleJUniqueMessage);
		}
		catch(AlreadyLockedException e)
		{
			if(logger.isInfoEnabled()) logger.info("Detected running instance, quitting.");
			String result=JUnique.sendMessage(Lilith.class.getName(),"Show");
			if(logger.isDebugEnabled()) logger.debug("JUnique result: {}", result);
			return;
		}
		// ok, we are the first instance this user has started...

		// install uncaught exception handler on event thread.
		EventQueue.invokeLater(() -> Thread.currentThread().setUncaughtExceptionHandler(uncaughtExceptionHandler));

		startUI(appTitle);
	}

	private static void initCLILogging()
	{
		initLogbackConfig(Lilith.class.getResource("/logbackCLI.groovy"));
	}

	private static void initVerboseLogging()
	{
		initLogbackConfig(Lilith.class.getResource("/logbackVerbose.groovy"));
	}

	private static void initLogbackConfig(URL configUrl)
	{
		ILoggerFactory loggerFactory = LoggerFactory.getILoggerFactory();
		if(loggerFactory instanceof LoggerContext)
		{
			LoggerContext loggerContext = (LoggerContext) loggerFactory;

			StatusManager sm = loggerContext.getStatusManager();
			sm.clear();

			// reset previous configuration initially loaded from logback.groovy
			loggerContext.reset();

			if(configUrl.toString().endsWith(GROOVY_EXTENSION))
			{
				// http://jira.qos.ch/browse/LOGBACK-1079
				GafferConfigurator configurator = new GafferConfigurator(loggerContext);
				try
				{
					configurator.run(configUrl);

					final Logger logger = LoggerFactory.getLogger(Lilith.class);
					if (logger.isDebugEnabled()) logger.debug("Configured logging with {}.", configUrl);
				}
				catch (RuntimeException ex)
				{
					sm.add(new ErrorStatus("Exception while configuring Logback!", configUrl, ex));
				}
			}
			else
			{
				JoranConfigurator configurator = new JoranConfigurator();
				configurator.setContext(loggerContext);
				try
				{
					configurator.doConfigure(configUrl);

					final Logger logger = LoggerFactory.getLogger(Lilith.class);
					if (logger.isDebugEnabled()) logger.debug("Configured logging with {}.", configUrl);
				}
				catch (JoranException ex)
				{
					sm.add(new ErrorStatus("Exception while configuring Logback!", configUrl, ex));
				}
			}

			int level = ContextHelper.getHighestLevel(loggerContext);
			long lastReset = ContextHelper.getTimeOfLastReset(loggerContext);
			if (level > Status.INFO)
			{
				List<Status> statusList = StatusUtil.filterStatusListByTimeThreshold(sm.getCopyOfStatusList(), lastReset);
				if (statusList != null)
				{
					System.err.println("############################################################");
					System.err.println("## Logback Status                                         ##");
					System.err.println("############################################################");
					StringBuilder statusBuilder = new StringBuilder();
					for (Status current : statusList)
					{
						appendStatus(statusBuilder, current, 0);
					}
					System.err.println(statusBuilder.toString());
					System.err.println("############################################################");
				}
			}

		}
	}

	@SuppressWarnings({"ThrowableResultOfMethodCallIgnored"})
	private static void appendStatus(StringBuilder builder, Status status, int indent)
	{
		int levelCode = status.getLevel();
		appendIndent(builder, indent);
		if(levelCode >= 0 && levelCode < STATUS_TEXT.length)
		{
			builder.append(STATUS_TEXT[levelCode]);
		}
		builder.append(status.getMessage()).append('\n');
		Throwable t = status.getThrowable();
		while(t != null)
		{
			appendIndent(builder, indent+1);
			builder.append(t.getClass().getName());
			String message = t.getMessage();
			if(message != null)
			{
				builder.append(": ").append(message);
			}
			builder.append('\n');
			// probably check for causes, too
			t=t.getCause();
		}
		if(status.hasChildren())
		{
			Iterator<Status> children = status.iterator();
			while(children.hasNext())
			{
				appendStatus(builder, children.next(), indent+1);
			}
		}
	}

	private static void appendIndent(StringBuilder builder, int indent)
	{
		for(int i=0;i<indent;i++)
		{
			builder.append('\t');
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
			EventQueue.invokeLater(() -> {

				if (frame.isVisible())
				{
					frame.setVisible(false);
				}
				Windows.showWindow(frame, null, false);
				frame.toFront();
			});
		}
	}

	private static void updateSplashStatus(final SplashScreen splashScreen, final String status)
	{
		if(splashScreen != null)
		{
			EventQueue.invokeLater(() -> {
				if(!splashScreen.isVisible())
				{
					Windows.showWindow(splashScreen, null, true);
				}
				splashScreen.toFront();
				splashScreen.setStatusText(status);
			});
		}
	}

	private static void hideSplashScreen(final SplashScreen splashScreen)
		throws InvocationTargetException, InterruptedException
	{
		if(splashScreen != null)
		{
			EventQueue.invokeAndWait(() -> splashScreen.setVisible(false));
		}
	}


	public static void startUI(final String appTitle)
	{
		final Logger logger = LoggerFactory.getLogger(Lilith.class);

		//UIManager.installLookAndFeel("Napkin", "net.sourceforge.napkinlaf.NapkinLookAndFeel");

		// look & feels must be installed before creation of ApplicationPreferences.
		ApplicationPreferences applicationPreferences = new ApplicationPreferences();

		// init look & feel
		String lookAndFeelName = applicationPreferences.getLookAndFeel();
		String systemLookAndFeelClassName = UIManager.getSystemLookAndFeelClassName();
		String lookAndFeelClassName = systemLookAndFeelClassName;
		if(lookAndFeelName != null)
		{
			for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels())
			{
				if (lookAndFeelName.equals(info.getName()))
				{
					lookAndFeelClassName = info.getClassName();
					break;
				}
			}
		}

		try
		{
			UIManager.setLookAndFeel(lookAndFeelClassName);
		}
		catch (Throwable e)
		{
			if(logger.isWarnEnabled()) logger.warn("Failed to set look&feel to '{}'.", lookAndFeelClassName, e);
			if(!lookAndFeelClassName.equals(systemLookAndFeelClassName))
			{
				try
				{
					UIManager.setLookAndFeel(systemLookAndFeelClassName);
					lookAndFeelClassName = systemLookAndFeelClassName;
				}
				catch (Throwable e2)
				{
					if(logger.isWarnEnabled()) logger.warn("Failed to set look&feel to '{}'.", systemLookAndFeelClassName, e);
					lookAndFeelClassName = null;
				}
			}
		}

		boolean screenMenuBar = false;
		if(isMac() && (
				isJava9OrHigher() // all L&F support screen menu bar with Java 9 or higher
				|| systemLookAndFeelClassName.equals(lookAndFeelClassName)
		))
		{
			// Use Apple Aqua L&F screen menu bar if available; set property before any frames created
			try
			{
				System.setProperty(APPLE_SCREEN_MENU_BAR_SYSTEM_PROPERTY, "true");
				screenMenuBar = true;
			}
			catch(Throwable e)
			{
				try
				{
					screenMenuBar = Boolean.parseBoolean(System.getProperty(APPLE_SCREEN_MENU_BAR_SYSTEM_PROPERTY, "false"));
				}
				catch(Throwable e2)
				{
					// ignore
				}
			}
		}

		applicationPreferences.setUsingScreenMenuBar(screenMenuBar);

		boolean splashScreenDisabled = applicationPreferences.isSplashScreenDisabled();
		try
		{
			SplashScreen splashScreen = null;
			if(!splashScreenDisabled)
			{
				CreateSplashRunnable createRunnable = new CreateSplashRunnable(appTitle);
				EventQueue.invokeAndWait(createRunnable);
				splashScreen = createRunnable.getSplashScreen();
				Thread.sleep(500); // so the splash gets the chance to get displayed :(
				updateSplashStatus(splashScreen, "Initialized application preferences…");
			}

			File startupApplicationPath = applicationPreferences.getStartupApplicationPath();
			if(startupApplicationPath.mkdirs())
			{
				if(logger.isDebugEnabled()) logger.debug("Created '{}'.", startupApplicationPath.getAbsolutePath()); // NOPMD
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
					OutputStream fos = Files.newOutputStream(errorLog.toPath(), CREATE, APPEND);
					PrintStream ps = new PrintStream(fos, true, StandardCharsets.UTF_8.name());
					if(!freshFile)
					{
						ps.println("----------------------------------------");
					}
					String currentDateTime = DateTimeFormatters.DATETIME_IN_SYSTEM_ZONE_SPACE.format(Instant.now());
					ps.println("Started " + APP_NAME + " V" + APP_VERSION + " at " + currentDateTime);
					System.setErr(ps);
					if(logger.isInfoEnabled()) logger.info("Writing System.err to '{}'.", errorLog.getAbsolutePath());
				}
				catch(IOException e)
				{
					e.printStackTrace();
				}
			}

			File prevPathFile = new File(startupApplicationPath, ApplicationPreferences.PREVIOUS_APPLICATION_PATH_FILENAME);
			if(prevPathFile.isFile())
			{
				updateSplashStatus(splashScreen, "Moving application path content…");
				moveApplicationPathContent(prevPathFile, startupApplicationPath);
			}
			if(!applicationPreferences.isLicensed())
			{
				hideSplashScreen(splashScreen);

				LicenseAgreementDialog licenseDialog = new LicenseAgreementDialog();
				licenseDialog.setAlwaysOnTop(true);
				licenseDialog.setAutoRequestFocus(true);
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

			updateSplashStatus(splashScreen, "Creating main window…");
			CreateMainFrameRunnable createMain = new CreateMainFrameRunnable(applicationPreferences, splashScreen, appTitle);
			EventQueue.invokeAndWait(createMain);
			final MainFrame frame = createMain.getMainFrame();
			if(logger.isDebugEnabled()) logger.debug("After show...");
			updateSplashStatus(splashScreen, "Initializing application…");
			EventQueue.invokeAndWait(frame::startUp);
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
		private final String appTitle;

		private SplashScreen splashScreen;

		CreateSplashRunnable(String appTitle)
		{
			this.appTitle = appTitle;
		}

		@Override
		public void run()
		{
			splashScreen = new SplashScreen(appTitle);
			Windows.showWindow(splashScreen, null, true);
		}

		SplashScreen getSplashScreen()
		{
			return splashScreen;
		}
	}

	static class CreateMainFrameRunnable
		implements Runnable
	{
		private final ApplicationPreferences applicationPreferences;
		private final SplashScreen splashScreen;
		private final String appTitle;

		private MainFrame mainFrame;

		CreateMainFrameRunnable(ApplicationPreferences applicationPreferences, SplashScreen splashScreen, String appTitle)
		{
			this.applicationPreferences = applicationPreferences;
			this.splashScreen = splashScreen;
			this.appTitle = appTitle;
		}

		@Override
		public void run()
		{
			mainFrame = new MainFrame(applicationPreferences, splashScreen, appTitle);
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

		String prevPathStr = null;
		try(InputStream is = Files.newInputStream(prevPathFile.toPath()))
		{
			prevPathStr = IOUtils.toString(is, StandardCharsets.UTF_8);
		}
		catch(IOException ex)
		{
			if(logger.isWarnEnabled()) logger.warn("Exception while reading previous application path!", ex);
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
			if(logger.isDebugEnabled()) logger.debug("Deleted {}.", prevPathFile.getAbsolutePath()); // NOPMD
		}
	}

	private static boolean isMac()
	{
		return System.getProperty("os.name").toLowerCase(Locale.US).startsWith("mac");
	}

	private static boolean isJava9OrHigher()
	{
		return !System.getProperty("java.version").startsWith("1.");
	}
}
