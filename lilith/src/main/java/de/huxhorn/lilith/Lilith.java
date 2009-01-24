/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2009 Joern Huxhorn
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

import de.huxhorn.lilith.appender.InternalLilithAppender;
import de.huxhorn.lilith.handler.Slf4JHandler;
import de.huxhorn.lilith.swing.ApplicationPreferences;
import de.huxhorn.lilith.swing.LicenseAgreementDialog;
import de.huxhorn.lilith.swing.MainFrame;
import de.huxhorn.lilith.swing.SplashScreen;
import de.huxhorn.lilith.swing.callables.IndexingCallable;
import de.huxhorn.sulky.sounds.jlayer.JLayerSounds;
import de.huxhorn.sulky.swing.ProgressingCallable;
import de.huxhorn.sulky.swing.Windows;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.simplericity.macify.eawt.Application;
import org.simplericity.macify.eawt.DefaultApplication;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Handler;

import javax.swing.*;

public class Lilith
{
	public static final String APP_NAME;
	public static final String APP_VERSION;
	public static final String APP_BUILD_NUMBER;
	public static final long APP_TIMESTAMP;

	private static final String VERBOSE_SHORT = "v";
	private static final String PRINT_HELP_SHORT = "h";
	private static final String FLUSH_PREFERENCES_SHORT = "F";
	private static final String FLUSH_LICENSED_SHORT = "L";
	private static final String INDEX_SHORT = "i";
	private static final String ENABLE_BONJOUR_SHORT = "b";
	private static final String CREATE_MD5_SHORT = "m";

	private static final String VERBOSE = "verbose";
	private static final String PRINT_HELP = "help";
	private static final String FLUSH_PREFERENCES = "flushPrefs";
	private static final String FLUSH_LICENSED = "flushLicensed";
	private static final String INDEX = "indexFile";
	private static final String ENABLE_BONJOUR = "bonjour";
	private static final String CREATE_MD5 = "md5";


	private static Thread.UncaughtExceptionHandler uncaughtExceptionHandler;

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
			IOUtils.closeQuietly(is);
		}
		APP_NAME = p.getProperty("application.name");
		APP_VERSION = p.getProperty("application.version");
		APP_BUILD_NUMBER = p.getProperty("application.buildNumber");
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
		final Logger logger = LoggerFactory.getLogger(Lilith.class);

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
		options.addOption(CREATE_MD5_SHORT, CREATE_MD5, false, "create an MD% checksum for the given file.");
		boolean verbose = false;
		boolean flushPrefs = false;
		boolean flushLicensed = false;
		boolean enableBonjour = false;
		boolean indexFileOpt = false;
		boolean createMd5 = false;
		boolean printHelp;
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
			args = line.getArgs(); // remaining unparsed args...
		}
		catch(ParseException exp)
		{
			exitCode = -1;
			printHelp = true;
		}

		String appTitle = APP_NAME + " V" + APP_VERSION + "." + APP_BUILD_NUMBER;
		if(verbose)
		{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			Date d = new Date(APP_TIMESTAMP);

			appTitle += " - " + sdf.format(d);
		}
		System.out.println(appTitle);
		System.out.println("\nCopyright (C) 2007-2008  Joern Huxhorn\n\n" +
			"This program comes with ABSOLUTELY NO WARRANTY!\n\n" +
			"This is free software, and you are welcome to redistribute it\n" +
			"under certain conditions.\n" +
			"You should have received a copy of the GNU General Public License\n" +
			"along with this program.  If not, see <http://www.gnu.org/licenses/>.\n");
		System.out.println("Use commandline option -h to view help.\n\n");

		if(createMd5)
		{
			File input = new File(args[0]);

			if(!input.isFile())
			{
				if(logger.isWarnEnabled()) logger.warn("{} isn't a file!", input.getAbsolutePath());
				return;
			}
			File output = new File(input.getParentFile(), input.getName() + ".md5");

			try
			{

				FileInputStream fis = new FileInputStream(input);
				byte[] md5 = ApplicationPreferences.getMD5(fis);
				if(md5 == null)
				{
					if(logger.isWarnEnabled())
					{
						logger.warn("Couldn't calculate checksum for {}!", input.getAbsolutePath());
					}
					return;
				}
				FileOutputStream fos = new FileOutputStream(output);
				fos.write(md5);
				fos.close();
				if(logger.isInfoEnabled())
				{
					logger.info("Wrote checksum of {} to {}.", input.getAbsolutePath(), output.getAbsolutePath());
				}
			}
			catch(IOException e)
			{
				if(logger.isWarnEnabled()) logger.warn("Exception while creating checksum!", e);
			}
			return;
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
		}

		ILoggerFactory loggerFactory = LoggerFactory.getILoggerFactory();
		if(loggerFactory instanceof LoggerContext)
		{
			LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
			if(verbose)
			{
				// reset previous configuration initially loaded from logback.xml
				loggerContext.reset();
				JoranConfigurator configurator = new JoranConfigurator();
				configurator.setContext(loggerContext);
				URL configUrl;
				configUrl = Lilith.class.getResource("/logbackVerbose.xml");
				try
				{
					configurator.doConfigure(configUrl);
					if(logger.isDebugEnabled()) logger.debug("Configured logging with {}.", configUrl);
					StatusPrinter.print(loggerContext);
				}
				catch(JoranException ex)
				{
					if(logger.isErrorEnabled()) logger.error("Error configuring logging framework!", ex);
					StatusPrinter.print(loggerContext);
				}
			}
		}

		if(flushPrefs)
		{
			ApplicationPreferences prefs = new ApplicationPreferences();
			prefs.reset();
			prefs.setLicensed(false);
			if(logger.isInfoEnabled()) logger.info("Flushed preferences...");
			return;
		}

		if(flushLicensed)
		{
			ApplicationPreferences prefs = new ApplicationPreferences();
			prefs.setLicensed(false);
			if(logger.isInfoEnabled()) logger.info("Flushed licensed...");
			return;
		}

		if(printHelp)
		{
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("lilith", options);
			System.exit(exitCode);
		}

		if(indexFileOpt)
		{
			if(args.length >= 2)
			{
				String logFileStr = args[0];
				String indexFileStr = args[1];
				File logFile = new File(logFileStr);
				File indexFile = new File(indexFileStr);
				IndexingCallable callable = new IndexingCallable(logFile, indexFile);
				callable.addPropertyChangeListener(new IndexingChangeListener());
				try
				{
					int count = callable.call();
					if(logger.isInfoEnabled())
					{
						logger.info("Finished indexing {}. Number of events: {}", logFile.getAbsolutePath(), count);
					}
					System.exit(0);
				}
				catch(Exception e)
				{
					if(logger.isErrorEnabled())
					{
						logger.error("Exception while indexing '" + logFile.getAbsolutePath() + "'!", e);
					}
					System.exit(-1);
				}

			}
			if(logger.isErrorEnabled()) logger.error("Missing file argument!");
			System.exit(-1);
		}

		uncaughtExceptionHandler = new Thread.UncaughtExceptionHandler()
		{
			public void uncaughtException(Thread t, Throwable e)
			{
				System.err.println("\n-----\nThread " + t.getName() + " threw an exception!");
				e.printStackTrace(System.err);
			}
		};

		Thread.setDefaultUncaughtExceptionHandler(uncaughtExceptionHandler);

		SwingUtilities.invokeLater(new Runnable()
		{

			public void run()
			{
				Thread.currentThread().setUncaughtExceptionHandler(uncaughtExceptionHandler);
			}
		});
		startUI(appTitle, enableBonjour);
	}

	private static void updateSplashStatus(final SplashScreen splashScreen, final String status)
		throws InvocationTargetException, InterruptedException
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

	private static void hideSplashScreen(final SplashScreen splashScreen)
		throws InvocationTargetException, InterruptedException
	{
		SwingUtilities.invokeAndWait(new Runnable()
		{
			public void run()
			{
				splashScreen.setVisible(false);
			}
		});
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
		if(application.isMac())
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
						break;
					}
				}
			}
			catch(UnsupportedLookAndFeelException e)
			{
				// ignore
			}
			catch(ClassNotFoundException e)
			{
				// ignore
			}
			catch(InstantiationException e)
			{
				// ignore
			}
			catch(IllegalAccessException e)
			{
				// ignore
			}
		}

		try
		{
			CreateSplashRunnable createRunnable = new CreateSplashRunnable(appTitle);
			SwingUtilities.invokeAndWait(createRunnable);
			SplashScreen splashScreen = createRunnable.getSplashScreen();
			Thread.sleep(500); // so the splash gets the chance to get displayed :(
			updateSplashStatus(splashScreen, "Initialized application preferences...");

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
			CreateMainFrameRunnable createMain = new CreateMainFrameRunnable(applicationPreferences, appTitle, enableBonjour);
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
		private MainFrame mainFrame;
		private ApplicationPreferences applicationPreferences;
		private String appTitle;
		private boolean enableBonjour;

		public CreateMainFrameRunnable(ApplicationPreferences applicationPreferences, String appTitle, boolean enableBonjour)
		{
			this.enableBonjour = enableBonjour;
			this.appTitle = appTitle;
			this.applicationPreferences = applicationPreferences;
		}

		public void run()
		{
			mainFrame = new MainFrame(applicationPreferences, appTitle, enableBonjour);
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
			IOUtils.closeQuietly(is);
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
		prevPathFile.delete();
	}


	private static class IndexingChangeListener
		implements PropertyChangeListener
	{
		private final Logger logger = LoggerFactory.getLogger(Lilith.class);

		/**
		 * This method gets called when a bound property is changed.
		 *
		 * @param evt A PropertyChangeEvent object describing the event source
		 *            and the property that has changed.
		 */

		public void propertyChange(PropertyChangeEvent evt)
		{
			if(ProgressingCallable.PROGRESS_PROPERTY_NAME.equals(evt.getPropertyName()))
			{
				if(logger.isInfoEnabled()) logger.info("Progress: {}%", evt.getNewValue());
			}
		}
	}

}
