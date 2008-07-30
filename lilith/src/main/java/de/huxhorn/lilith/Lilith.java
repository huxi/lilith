/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2008 Joern Huxhorn
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
import de.huxhorn.lilith.handler.Slf4JHandler;
import de.huxhorn.lilith.swing.ApplicationPreferences;
import de.huxhorn.lilith.swing.LicenseAgreementDialog;
import de.huxhorn.lilith.swing.MainFrame;
import de.huxhorn.lilith.swing.SplashScreen;
import de.huxhorn.lilith.swing.callables.IndexingCallable;
import de.huxhorn.sulky.sounds.jlayer.JLayerSounds;
import de.huxhorn.sulky.swing.ProgressingCallable;
import de.huxhorn.sulky.swing.Windows;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.methods.GetMethod;
import org.simplericity.macify.eawt.Application;
import org.simplericity.macify.eawt.DefaultApplication;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.SwingUtilities;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.FileFilter;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Properties;
import java.util.Date;
import java.util.Arrays;
import java.util.Comparator;
import java.util.logging.Handler;
import java.text.SimpleDateFormat;

public class Lilith
{
	public static final String APP_NAME;
	public static final String APP_VERSION;
	public static final String APP_TIMESTAMP;

	private static final String VERBOSE_SHORT = "v";
	//private static final String GUI_SHORT = "G";
	private static final String PRINT_HELP_SHORT = "h";
	private static final String FLUSH_PREFERENCES_SHORT = "F";
	private static final String INDEX_SHORT = "i";
	//private static final String NAPKIN_LAF_SHORT = "n";
	private static final String DISABLE_BONJOUR_SHORT = "b";

	private static final String VERBOSE = "verbose";
	//private static final String GUI = "GUI";
	private static final String PRINT_HELP = "help";
	private static final String FLUSH_PREFERENCES = "flushPrefs";
	private static final String INDEX = "indexFile";
	//private static final String NAPKIN_LAF = "NapkinLaf";
	private static final String DISABLE_BONJOUR = "bonjour";

	static
	{
		final Logger logger = LoggerFactory.getLogger(Lilith.class);
		InputStream is = Lilith.class.getResourceAsStream("/app.properties");
		Properties p = new Properties();
		try
		{
			p.load(is);
		}
		catch (IOException ex)
		{
			if (logger.isErrorEnabled()) logger.error("Couldn't find app info resource!", ex);
			//ex.printStackTrace();
		}
		finally
		{
			IOUtils.closeQuietly(is);
		}
		APP_NAME = p.getProperty("application.name");
		APP_VERSION = p.getProperty("application.version");
		APP_TIMESTAMP = p.getProperty("application.timestamp");
	}

	// TODO: - Shortcut in tooltip of toolbars...
	// TODO: [- Clear all]
	// TODO: - Filter
	// TODO:  - Filter construction/management dialog(s) for NamedFilters.
	// TODO:  - Clear of filtered views... disable Clear in filtered views? Rethink...
	// TODO: - check termination of every started thread
	//
	// TODO: - special mac support?


	private static void updateSplashStatus(final SplashScreen splashScreen, final String status) throws InvocationTargetException, InterruptedException
	{
		SwingUtilities.invokeAndWait(new Runnable()
		{

			public void run()
			{
				if (!splashScreen.isVisible())
				{
					Windows.showWindow(splashScreen, null, true);
				}
				splashScreen.toFront();
				splashScreen.setStatusText(status);
			}
		});
	}

	private static void hideSplashScreen(final SplashScreen splashScreen) throws InvocationTargetException, InterruptedException
	{
		SwingUtilities.invokeAndWait(new Runnable()
		{
			public void run()
			{
				splashScreen.setVisible(false);
			}
		});
	}

	public static void startUI(final String appTitle, /*boolean noNapkinLaf,*/ boolean enableBonjour)//, final SourceManager sourceManager, final Sounds sounds)
	{
		final Logger logger = LoggerFactory.getLogger(Lilith.class);

		Application application = new DefaultApplication();
		if (application.isMac())
		{
			// Use Apple Aqua L&F screen menu bar if available; set property before any frames created
			try
			{
				System.setProperty("apple.laf.useScreenMenuBar", "true");
			}
			catch (Exception e)
			{
				// try the older menu bar property
				System.setProperty("com.apple.macos.useScreenMenuBar", "true");
				// this shouldn't happen since we only run on 1.5+
			}
		}
		/*
		else if (!noNapkinLaf)
		{
			String defaultLaf = System.getProperty("swing.defaultlaf");
			if (defaultLaf == null)
			{
				System.setProperty("swing.defaultlaf", "net.sourceforge.napkinlaf.NapkinLookAndFeel");
			}
		}
        */

		try
		{
			CreateSplashRunnable createRunnable = new CreateSplashRunnable(appTitle);
			SwingUtilities.invokeAndWait(createRunnable);
			SplashScreen splashScreen = createRunnable.getSplashScreen();
			Thread.sleep(500); // so the splash gets the chance to get displayed :(
			updateSplashStatus(splashScreen, "Initializing application preferences...");

			ApplicationPreferences applicationPreferences = new ApplicationPreferences();
			File startupApplicationPath = applicationPreferences.getStartupApplicationPath();
			if (startupApplicationPath.mkdirs())
			{
				if (logger.isDebugEnabled()) logger.debug("Created '{}'.", startupApplicationPath.getAbsolutePath());
			}

			// System.err redirection
			{
				File errorLog = new File(startupApplicationPath, "errors.log");
				boolean freshFile = false;
				if (!errorLog.isFile())
				{
					freshFile = true;
				}
				try
				{
					FileOutputStream fos = new FileOutputStream(errorLog, true);
					PrintStream ps = new PrintStream(fos, true);
					if (!freshFile)
					{
						ps.println("----------------------------------------");
					}
					SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
					ps.println("Started at " + format.format(new Date()));
					System.setErr(ps);
					if (logger.isInfoEnabled()) logger.info("Writing System.err to '{}'.", errorLog.getAbsolutePath());
				}
				catch (FileNotFoundException e)
				{
					e.printStackTrace();
				}
			}

			File prevPathFile = new File(startupApplicationPath, ApplicationPreferences.PREVIOUS_APPLICATION_PATH_FILENAME);
			if (prevPathFile.isFile())
			{
				updateSplashStatus(splashScreen, "Moving application path content...");
				moveApplicationPathContent(prevPathFile, startupApplicationPath);
			}
			if (!applicationPreferences.isLicensed())
			{
				hideSplashScreen(splashScreen);
				// so non-primitive prefs are initialized
				applicationPreferences.reset();
				LicenseAgreementDialog licenseDialog = new LicenseAgreementDialog();
				Windows.showWindow(licenseDialog, null, true);
				if (licenseDialog.isLicenseAgreed())
				{
					applicationPreferences.setLicensed(true);
				}
				else
				{
					if (logger.isWarnEnabled()) logger.warn("Didn't accept license! Exiting...");
					System.exit(-1);
				}
			}

			updateSplashStatus(splashScreen, "Creating main window...");
			CreateMainFrameRunnable createMain = new CreateMainFrameRunnable(applicationPreferences, appTitle, enableBonjour);
			SwingUtilities.invokeAndWait(createMain);
			final MainFrame frame = createMain.getMainFrame();
			if (logger.isInfoEnabled()) logger.info("After show...");
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
		catch (InterruptedException ex)
		{
			if (logger.isInfoEnabled()) logger.info("Interrupted...", ex);
		}
		catch (InvocationTargetException ex)
		{
			if (logger.isWarnEnabled()) logger.warn("InvocationTargetException...", ex);
			if (logger.isWarnEnabled()) logger.warn("Target-Exception: ", ex.getTargetException());

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
	 * @param prevPathFile		   the file that contains (!!!) the previous application path - not the previous application path itself!
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
		catch (IOException ex)
		{
			if (logger.isWarnEnabled()) logger.warn("Exception while reading previous application path!", ex);
		}
		finally
		{
			IOUtils.closeQuietly(is);
		}
		if (prevPathStr != null)
		{
			File prevPath = new File(prevPathStr);
			try
			{
				FileUtils.copyDirectory(prevPath, startupApplicationPath);
				FileUtils.deleteDirectory(prevPath);
			}
			catch (IOException ex)
			{
				if (logger.isWarnEnabled())
					logger.warn("Exception while moving content of previous application path '" + prevPath.getAbsolutePath() + "' to new one '" + startupApplicationPath.getAbsolutePath() + "'!", ex);
			}
			if (logger.isInfoEnabled())
				logger.info("Moved content from previous application path '{}' to new application path '{}'.", prevPath.getAbsolutePath(), startupApplicationPath.getAbsolutePath());
		}
		prevPathFile.delete();
	}


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
		//options.addOption(GUI_SHORT, GUI, false, "show gui.");
		options.addOption(FLUSH_PREFERENCES_SHORT, FLUSH_PREFERENCES, false, "flush gui preferences.");
		//options.addOption(NAPKIN_LAF_SHORT, NAPKIN_LAF, false, "use NapkinLAF.");
		options.addOption(DISABLE_BONJOUR_SHORT, DISABLE_BONJOUR, false, "disable Bonjor.");
		options.addOption(INDEX_SHORT, INDEX, false, "indexes the given file.");
		boolean verbose = false;
//		boolean showGui = true;
		boolean flushPrefs = false;
		//boolean noNapkinLaf = false;
		boolean enableBonjour = false;
		boolean indexFileOpt = false;
		boolean printHelp;
		int exitCode = 0;
		try
		{
			// parse the command line arguments
			CommandLine line = parser.parse(options, args);

			verbose = line.hasOption(VERBOSE_SHORT);
			printHelp = line.hasOption(PRINT_HELP_SHORT);
			flushPrefs = line.hasOption(FLUSH_PREFERENCES_SHORT);
			//noNapkinLaf = !line.hasOption(NAPKIN_LAF_SHORT);
			enableBonjour = !line.hasOption(DISABLE_BONJOUR_SHORT);
			indexFileOpt = line.hasOption(INDEX_SHORT);
//			if(indexFileOpt)
//			{
//				showGui=false;
//			}
			args = line.getArgs(); // remaining unparsed args...
		}
		catch (ParseException exp)
		{
			exitCode = -1;
			printHelp = true;
		}

		String appTitle = APP_NAME + " V" + APP_VERSION;
		if (verbose)
		{
			appTitle += " - build: " + APP_TIMESTAMP;
		}
		System.out.println(appTitle);
		System.out.println("Copyright (C) 2007-2008  Joern Huxhorn\n" +
				"This program comes with ABSOLUTELY NO WARRANTY!\n" +
				"This is free software, and you are welcome to redistribute it\n" +
				"under certain conditions.\n" +
				"You should have received a copy of the GNU General Public License\n" +
				"along with this program.  If not, see <http://www.gnu.org/licenses/>.\n");
		System.out.println("Use commandline option -h to view help.\n\n");

		ILoggerFactory loggerFactory = LoggerFactory.getILoggerFactory();
		if (loggerFactory instanceof LoggerContext)
		{
			LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
			if (verbose)
			{
				// reset previous configuration initially loaded from logback.xml
				loggerContext.shutdownAndReset();
				JoranConfigurator configurator = new JoranConfigurator();
				configurator.setContext(loggerContext);
				URL configUrl;
				configUrl = Lilith.class.getResource("/logbackVerbose.xml");
				try
				{
					configurator.doConfigure(configUrl);
					if (logger.isDebugEnabled()) logger.debug("Configured logging with {}.", configUrl);
					StatusPrinter.print(loggerContext);
				}
				catch (JoranException ex)
				{
					if (logger.isErrorEnabled()) logger.error("Error configuring logging framework!", ex);
					StatusPrinter.print(loggerContext);
				}
			}
		}

		if (flushPrefs)
		{
			ApplicationPreferences prefs=new ApplicationPreferences();
			prefs.reset();
			prefs.setLicensed(false);
			if (logger.isInfoEnabled()) logger.info("Flushed preferences...");
		}

		if (printHelp)
		{
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("lilith", options);
			System.exit(exitCode);
		}

		if (indexFileOpt)
		{
			if (args.length >= 2)
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
					if (logger.isInfoEnabled())
						logger.info("Finished indexing {}. Number of events: {}", logFile.getAbsolutePath(), count);
					System.exit(0);
				}
				catch (Exception e)
				{
					if (logger.isErrorEnabled())
						logger.error("Exception while indexing '" + logFile.getAbsolutePath() + "'!", e);
					System.exit(-1);
				}

			}
			if (logger.isErrorEnabled()) logger.error("Missing file argument!");
			System.exit(-1);
		}

//		if(showGui)
//		{
		startUI(appTitle, /*noNapkinLaf,*/ enableBonjour);
//		}
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
			if (ProgressingCallable.PROGRESS_PROPERTY_NAME.equals(evt.getPropertyName()))
			{
				if (logger.isInfoEnabled()) logger.info("Progress: {}%", evt.getNewValue());
			}
		}
	}

}
