/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2020 Joern Huxhorn
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

package de.huxhorn.lilith.swing;

import de.huxhorn.lilith.DateTimeFormatters;
import de.huxhorn.lilith.Lilith;
import de.huxhorn.lilith.LilithBuffer;
import de.huxhorn.lilith.LilithSounds;
import de.huxhorn.lilith.VersionBundle;
import de.huxhorn.lilith.api.FileConstants;
import de.huxhorn.lilith.appender.InternalLilithAppender;
import de.huxhorn.lilith.data.access.AccessEvent;
import de.huxhorn.lilith.data.access.HttpStatus;
import de.huxhorn.lilith.data.access.logback.converter.LogbackAccessConverter;
import de.huxhorn.lilith.data.converter.ConverterRegistry;
import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.lilith.data.eventsource.SourceIdentifier;
import de.huxhorn.lilith.data.logging.ExtendedStackTraceElement;
import de.huxhorn.lilith.data.logging.LoggingEvent;
import de.huxhorn.lilith.data.logging.logback.converter.LogbackLoggingConverter;
import de.huxhorn.lilith.debug.DebugDialog;
import de.huxhorn.lilith.engine.AccessFileBufferFactory;
import de.huxhorn.lilith.engine.EventHandler;
import de.huxhorn.lilith.engine.EventSource;
import de.huxhorn.lilith.engine.EventSourceListener;
import de.huxhorn.lilith.engine.EventSourceProducer;
import de.huxhorn.lilith.engine.FileBufferFactory;
import de.huxhorn.lilith.engine.LogFileFactory;
import de.huxhorn.lilith.engine.LoggingFileBufferFactory;
import de.huxhorn.lilith.engine.SourceManager;
import de.huxhorn.lilith.engine.impl.EventSourceImpl;
import de.huxhorn.lilith.engine.impl.LogFileFactoryImpl;
import de.huxhorn.lilith.engine.impl.eventproducer.LoggingEventSourceIdentifierUpdater;
import de.huxhorn.lilith.engine.impl.sourcemanager.SourceManagerImpl;
import de.huxhorn.lilith.engine.impl.sourceproducer.AccessEventProtobufServerSocketEventSourceProducer;
import de.huxhorn.lilith.engine.impl.sourceproducer.LoggingEventProtobufServerSocketEventSourceProducer;
import de.huxhorn.lilith.engine.impl.sourceproducer.SerializableServerSocketEventSourceProducer;
import de.huxhorn.lilith.engine.json.sourceproducer.LilithJsonMessageLoggingServerSocketEventSourceProducer;
import de.huxhorn.lilith.engine.json.sourceproducer.LilithJsonStreamLoggingServerSocketEventSourceProducer;
import de.huxhorn.lilith.engine.jul.sourceproducer.JulXmlStreamLoggingServerSocketEventSourceProducer;
import de.huxhorn.lilith.engine.xml.sourceproducer.LilithXmlMessageLoggingServerSocketEventSourceProducer;
import de.huxhorn.lilith.engine.xml.sourceproducer.LilithXmlStreamLoggingServerSocketEventSourceProducer;
import de.huxhorn.lilith.eventhandlers.AlarmSoundAccessEventHandler;
import de.huxhorn.lilith.eventhandlers.AlarmSoundLoggingEventHandler;
import de.huxhorn.lilith.eventhandlers.FileDumpEventHandler;
import de.huxhorn.lilith.eventhandlers.FileSplitterEventHandler;
import de.huxhorn.lilith.jul.xml.importing.JulImportCallable;
import de.huxhorn.lilith.log4j.converter.Log4jLoggingConverter;
import de.huxhorn.lilith.log4j.xml.Log4jImportCallable;
import de.huxhorn.lilith.log4j2.converter.Log4j2LoggingConverter;
import de.huxhorn.lilith.log4j2.producer.Log4j2JsonServerSocketEventSourceProducer;
import de.huxhorn.lilith.log4j2.producer.Log4j2Ports;
import de.huxhorn.lilith.log4j2.producer.Log4j2XmlServerSocketEventSourceProducer;
import de.huxhorn.lilith.log4j2.producer.Log4j2YamlServerSocketEventSourceProducer;
import de.huxhorn.lilith.logback.appender.ClassicMultiplexSocketAppender;
import de.huxhorn.lilith.logback.appender.access.AccessMultiplexSocketAppender;
import de.huxhorn.lilith.logback.appender.json.ClassicJsonMultiplexSocketAppender;
import de.huxhorn.lilith.logback.appender.json.ZeroDelimitedClassicJsonMultiplexSocketAppender;
import de.huxhorn.lilith.logback.appender.xml.ClassicXmlMultiplexSocketAppender;
import de.huxhorn.lilith.logback.appender.xml.ZeroDelimitedClassicXmlMultiplexSocketAppender;
import de.huxhorn.lilith.prefs.LilithPreferences;
import de.huxhorn.lilith.services.details.AbstractHtmlFormatter;
import de.huxhorn.lilith.services.details.GroovyEventWrapperHtmlFormatter;
import de.huxhorn.lilith.services.details.ThymeleafEventWrapperHtmlFormatter;
import de.huxhorn.lilith.services.gotosrc.GoToSource;
import de.huxhorn.lilith.services.gotosrc.SerializingGoToSource;
import de.huxhorn.lilith.swing.callables.CheckFileChangeCallable;
import de.huxhorn.lilith.swing.callables.CleanAllInactiveCallable;
import de.huxhorn.lilith.swing.callables.CleanObsoleteCallable;
import de.huxhorn.lilith.swing.callables.ExportCallable;
import de.huxhorn.lilith.swing.callables.IndexingCallable;
import de.huxhorn.lilith.swing.filefilters.DirectoryFilter;
import de.huxhorn.lilith.swing.filefilters.LilithFileFilter;
import de.huxhorn.lilith.swing.filefilters.LogFileFilter;
import de.huxhorn.lilith.swing.filefilters.XmlImportFileFilter;
import de.huxhorn.lilith.swing.preferences.PreferencesDialog;
import de.huxhorn.lilith.swing.preferences.SavedCondition;
import de.huxhorn.lilith.swing.table.ColorScheme;
import de.huxhorn.lilith.swing.table.Colors;
import de.huxhorn.lilith.swing.taskmanager.TaskManagerInternalFrame;
import de.huxhorn.lilith.swing.transfer.MainFrameTransferHandler;
import de.huxhorn.lilith.swing.uiprocessors.ConditionNamesActionsProcessor;
import de.huxhorn.lilith.swing.uiprocessors.ConditionNamesContainerProcessor;
import de.huxhorn.lilith.swing.uiprocessors.PreviousSearchStringsContainerProcessor;
import de.huxhorn.lilith.swing.uiprocessors.ResetContainerProcessor;
import de.huxhorn.lilith.swing.uiprocessors.UpdateRecentFilesProcessor;
import de.huxhorn.lilith.swing.uiprocessors.UpdateScaleContainerProcessor;
import de.huxhorn.lilith.swing.uiprocessors.UpdateViewsContainerProcessor;
import de.huxhorn.lilith.swing.uiprocessors.UpdateWindowMenuProcessor;
import de.huxhorn.lilith.swing.uiprocessors.ViewActionsProcessor;
import de.huxhorn.lilith.swing.uiprocessors.ViewContainerProcessor;
import de.huxhorn.lilith.swing.uiprocessors.VisibleContainerProcessor;
import de.huxhorn.lilith.tray.TraySupport;
import de.huxhorn.sulky.buffers.AppendOperation;
import de.huxhorn.sulky.buffers.BlockingCircularBuffer;
import de.huxhorn.sulky.buffers.Buffer;
import de.huxhorn.sulky.buffers.FileBuffer;
import de.huxhorn.sulky.buffers.Reset;
import de.huxhorn.sulky.codec.filebuffer.CodecFileBuffer;
import de.huxhorn.sulky.codec.filebuffer.DefaultFileHeaderStrategy;
import de.huxhorn.sulky.codec.filebuffer.FileHeader;
import de.huxhorn.sulky.codec.filebuffer.FileHeaderStrategy;
import de.huxhorn.sulky.codec.filebuffer.MetaData;
import de.huxhorn.sulky.conditions.Condition;
import de.huxhorn.sulky.conditions.Or;
import de.huxhorn.sulky.formatting.SimpleXml;
import de.huxhorn.sulky.sounds.Sounds;
import de.huxhorn.sulky.swing.MemoryStatus;
import de.huxhorn.sulky.swing.Windows;
import de.huxhorn.sulky.tasks.Task;
import de.huxhorn.sulky.tasks.TaskListener;
import de.huxhorn.sulky.tasks.TaskManager;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;
import javax.swing.Icon;
import javax.swing.JDesktopPane;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.border.EtchedBorder;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainFrame
	extends JFrame
{
	private static final long serialVersionUID = 6138189654024239738L;

	private static final String LOGS_SUBDIRECTORY = "logs";
	private static final String LOGGING_FILE_SUBDIRECTORY = LOGS_SUBDIRECTORY + "/logging";
	private static final String ACCESS_FILE_SUBDIRECTORY = LOGS_SUBDIRECTORY + "/access";
	private static final String GLOBAL_SOURCE_IDENTIFIER_NAME ="global";
	private static final String HELP_URI_PREFIX = "help://";
	private static final String PREFERENCES_URI_PREFIX = "prefs://";
	private static final String STACK_TRACE_ELEMENT_URI_PREFIX = "ste://";
	private static final ViewContainerProcessor UPDATE_VIEWS_CONTAINER_PROCESSOR = new UpdateViewsContainerProcessor();
	private static final ViewActionsProcessor UPDATE_RECENT_FILES_ACTIONS_PROCESSOR = new UpdateRecentFilesProcessor();
	private static final ViewActionsProcessor UPDATE_WINDOW_MENU_ACTIONS_PROCESSOR = new UpdateWindowMenuProcessor();
	private static final ViewContainerProcessor RESET_CONTAINER_PROCESSOR = new ResetContainerProcessor();
	private static final double SCALE_FACTOR = 0.05d;
	private static final int EXPORT_WARNING_SIZE = 20_000;
	private static final boolean IS_MAC;
	private static final boolean IS_WINDOWS;

	private static final String[] MAC_OPEN_URL_ARRAY =
			{
					// Mac: open http://www.heise.de
					"open",
					null,
			};

	private static final String[] WINDOWS_OPEN_URL_ARRAY =
			{
					// Windows: cmd /C start http://www.heise.de
					"cmd",
					"/C",
					"start",
					null,
			};


	private final Logger logger = LoggerFactory.getLogger(MainFrame.class);

	private final File startupApplicationPath;
	private final GroovyEventWrapperHtmlFormatter groovyFormatter;
	private final ThymeleafEventWrapperHtmlFormatter thymeleafFormatter;

	private final LogFileFactory loggingFileFactory;
	private final FileBufferFactory<LoggingEvent> loggingFileBufferFactory;
	private final EventSourceListener<LoggingEvent> loggingSourceListener;
	private final LoggingEventViewManager loggingEventViewManager;

	private final LogFileFactory accessFileFactory;
	private final FileBufferFactory<AccessEvent> accessFileBufferFactory;
	private final EventSourceListener<AccessEvent> accessSourceListener;
	private final AccessEventViewManager accessEventViewManager;

	private final JDesktopPane desktop;

	private final PreferencesDialog preferencesDialog;
	private final JDialog aboutDialog;
	private final JLabel statusLabel;
	private final ApplicationPreferences applicationPreferences;
	private final DebugDialog debugDialog;
	private final TaskManager<Long> longTaskManager;
	private final ViewActions viewActions;
	private final OpenPreviousDialog openInactiveLogsDialog;
	private final HelpFrame helpFrame;
	private final List<AutostartRunnable> autostartProcesses;
	private final SplashScreen splashScreen;
	private final TaskManagerInternalFrame taskManagerFrame;
	private final JLabel taskStatusLabel;
	private final JFileChooser openFileChooser;
	private final JFileChooser importFileChooser;
	private final JFileChooser exportFileChooser;

	private final JToolBar toolbar;
	private final JPanel statusBar;
	private final TipOfTheDayDialog tipOfTheDayDialog;
	private final CheckForUpdateDialog checkForUpdateDialog;

	private final SourceTitleContainerProcessor sourceTitleContainerProcessor=new SourceTitleContainerProcessor();
	private final ScrollingSmoothlyContainerProcessor scrollingSmoothlyContainerProcessor = new ScrollingSmoothlyContainerProcessor();

	private GoToSource gotoSourceProvider;
	private SourceManager<LoggingEvent> loggingEventSourceManager;
	private SourceManager<AccessEvent> accessEventSourceManager;
	private Sounds sounds;
	private int activeCounter;
	private List<SavedCondition> activeConditions;
	private Map<LoggingEvent.Level, Colors> levelColors;
	private Map<HttpStatus.Type, Colors> statusColors;
	private int previousNumberOfTasks;
	private boolean coloringWholeRow;
	private FileDumpEventHandler<LoggingEvent> loggingFileDump;
	private FileDumpEventHandler<AccessEvent> accessFileDump;
	private Condition findActiveCondition;
	private TraySupport traySupport; // may be null
	private boolean usingThymeleaf;

	static
	{
		String osName = System.getProperty("os.name").toLowerCase(Locale.US);
		IS_WINDOWS = osName.startsWith("windows");
		IS_MAC = osName.startsWith("mac");
	}

	public MainFrame(ApplicationPreferences applicationPreferences, SplashScreen splashScreen, String appName)
	{
		super(appName);
		this.applicationPreferences = Objects.requireNonNull(applicationPreferences, "applicationPreferences must not be null!");
		this.coloringWholeRow = this.applicationPreferences.isColoringWholeRow();
		this.splashScreen = splashScreen;
		setSplashStatusText("Creating icons…");
		// Executing any Icons method triggers initialisation of class
		setIconImages(Icons.resolveFrameIconImages(LilithFrameId.MAIN));

		// Executing any LilithKeyStrokes method triggers initialisation of class
		setSplashStatusText("Creating keystrokes…");
		LilithKeyStrokes.getActionNames();

		setSplashStatusText("Creating main frame…");

		groovyFormatter = new GroovyEventWrapperHtmlFormatter(applicationPreferences);
		thymeleafFormatter = new ThymeleafEventWrapperHtmlFormatter(applicationPreferences);


		autostartProcesses = new ArrayList<>();

		addWindowListener(new MainWindowListener());
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE); // fixes ticket #79
		Runtime runtime = Runtime.getRuntime();
		Thread shutdownHook = new Thread(new ShutdownRunnable());
		runtime.addShutdownHook(shutdownHook);

		/*
		if(application.isMac())
		{
			setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		}
		else
		{
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		}
        */

		longTaskManager = new TaskManager<>();
		longTaskManager.setUsingEventQueue(true);
		longTaskManager.startUp();
		longTaskManager.addTaskListener(new MainTaskListener());

		startupApplicationPath = this.applicationPreferences.getStartupApplicationPath();

		loggingFileFactory = new LogFileFactoryImpl(new File(startupApplicationPath, LOGGING_FILE_SUBDIRECTORY));
		accessFileFactory = new LogFileFactoryImpl(new File(startupApplicationPath, ACCESS_FILE_SUBDIRECTORY));

		Map<String, String> loggingMetaData = new HashMap<>();
		loggingMetaData.put(FileConstants.CONTENT_TYPE_KEY, FileConstants.CONTENT_TYPE_VALUE_LOGGING);
		loggingMetaData.put(FileConstants.CONTENT_FORMAT_KEY, FileConstants.CONTENT_FORMAT_VALUE_PROTOBUF);
		loggingMetaData.put(FileConstants.COMPRESSION_KEY, FileConstants.COMPRESSION_VALUE_GZIP);
		// TODO: configurable format and compressed

		loggingFileBufferFactory = new LoggingFileBufferFactory(loggingFileFactory, loggingMetaData);

		Map<String, String> accessMetaData = new HashMap<>();
		accessMetaData.put(FileConstants.CONTENT_TYPE_KEY, FileConstants.CONTENT_TYPE_VALUE_ACCESS);
		accessMetaData.put(FileConstants.CONTENT_FORMAT_KEY, FileConstants.CONTENT_FORMAT_VALUE_PROTOBUF);
		accessMetaData.put(FileConstants.COMPRESSION_KEY, FileConstants.COMPRESSION_VALUE_GZIP);
		// TODO: configurable format and compressed

		accessFileBufferFactory = new AccessFileBufferFactory(accessFileFactory, accessMetaData);

		loggingEventViewManager = new LoggingEventViewManager(this);
		accessEventViewManager = new AccessEventViewManager(this);
		this.applicationPreferences.addPropertyChangeListener(new PreferencesChangeListener());
		loggingSourceListener = new LoggingEventSourceListener();
		accessSourceListener = new AccessEventSourceListener();
		// this.cleanupWindowChangeListener = new CleanupWindowChangeListener();
		desktop = new JDesktopPane();
		statusBar = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		statusLabel = new JLabel();
		statusLabel.setText("Starting…");

		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 0.0;
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.insets = new Insets(0, 5, 0, 0);

		statusBar.add(statusLabel, gbc);

		taskStatusLabel = new JLabel();
		taskStatusLabel.setText("");
		taskStatusLabel.setForeground(Color.BLUE);
		taskStatusLabel.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent mouseEvent)
			{
				showTaskManager();
			}

			@Override
			public void mouseEntered(MouseEvent mouseEvent)
			{
				taskStatusLabel.setForeground(Color.RED);
			}

			@Override
			public void mouseExited(MouseEvent mouseEvent)
			{
				taskStatusLabel.setForeground(Color.BLUE);
			}
		});
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.insets = new Insets(0, 5, 0, 0);
		statusBar.add(taskStatusLabel, gbc);


		MemoryStatus memoryStatus = new MemoryStatus();
		memoryStatus.setBackground(Color.WHITE);
		memoryStatus.setOpaque(true);
		memoryStatus.setUsingBinaryUnits(true);
		memoryStatus.setUsingTotal(false);
		memoryStatus.setBorder(new EtchedBorder(EtchedBorder.LOWERED));

		gbc.fill = GridBagConstraints.NONE;
		gbc.gridx = 2;
		gbc.gridy = 0;
		gbc.weightx = 0.0;
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.insets = new Insets(0, 0, 0, 0);

		statusBar.add(memoryStatus, gbc);
		add(desktop, BorderLayout.CENTER);
		add(statusBar, BorderLayout.SOUTH);

		setSplashStatusText("Creating about dialog…");
		aboutDialog = new AboutDialog(this, "About " + appName + "…", appName);

		setSplashStatusText("Creating update dialog…");
		checkForUpdateDialog = new CheckForUpdateDialog(this);

		setSplashStatusText("Creating debug dialog…");
		debugDialog = new DebugDialog(this, this);

		setSplashStatusText("Creating preferences dialog…");
		if(logger.isDebugEnabled()) logger.debug("Before creation of preferences-dialog...");
		preferencesDialog = new PreferencesDialog(this);
		if(logger.isDebugEnabled()) logger.debug("After creation of preferences-dialog...");

		setSplashStatusText("Creating \"Open inactive\" dialog…");
		openInactiveLogsDialog = new OpenPreviousDialog(this);

		setSplashStatusText("Creating help frame…");
		helpFrame = new HelpFrame(this);
		helpFrame.setTitle("Help Topics");

		setSplashStatusText("Creating file choosers…");
		openFileChooser = new JFileChooser();
		openFileChooser.setFileFilter(new LilithFileFilter());
		openFileChooser.setFileHidingEnabled(false);
		openFileChooser.setCurrentDirectory(this.applicationPreferences.getPreviousOpenPath());

		importFileChooser = new JFileChooser();
		importFileChooser.setFileFilter(new XmlImportFileFilter());
		importFileChooser.setFileHidingEnabled(false);
		importFileChooser.setCurrentDirectory(this.applicationPreferences.getPreviousImportPath());

		exportFileChooser = new JFileChooser();
		exportFileChooser.setFileFilter(new LilithFileFilter());
		exportFileChooser.setFileHidingEnabled(false);
		exportFileChooser.setCurrentDirectory(this.applicationPreferences.getPreviousExportPath());

		setSplashStatusText("Creating task manager frame…");
		taskManagerFrame = new TaskManagerInternalFrame(this);
		taskManagerFrame.setTitle("Task Manager");
		taskManagerFrame.setDefaultCloseOperation(JInternalFrame.HIDE_ON_CLOSE);
		taskManagerFrame.setBounds(0, 0, 320, 240);

		desktop.add(taskManagerFrame);
		desktop.validate();

		// the following code must be executed after desktop has been initialized...
		new MainFrameTransferHandler(this).attach();

		setSplashStatusText("Creating Tip of the Day dialog…");
		tipOfTheDayDialog = new TipOfTheDayDialog(this);

		setSplashStatusText("Creating actions and menus…");
		viewActions = new ViewActions(this, null);
		viewActions.getPopupMenu(); // initialize popup once in main frame only.

		JMenuBar menuBar = viewActions.getMenuBar();
		toolbar = viewActions.getToolBar();
		add(toolbar, BorderLayout.NORTH);
		setJMenuBar(menuBar);

		setShowingToolbar(applicationPreferences.isShowingToolbar());
		setShowingStatusBar(applicationPreferences.isShowingStatusBar());
	}

	public boolean isUsingThymeleaf()
	{
		return usingThymeleaf;
	}

	public void setUsingThymeleaf(boolean usingThymeleaf)
	{
		this.usingThymeleaf = usingThymeleaf;
	}

	/**
	 * To be called after setVisible(true)...
	 */
	public void startUp()
	{
		//Thread referenceCollection=new Thread(new ColorsCollectionRunnable(), "ColorCacheCleanupThread");
		//referenceCollection.setDaemon(true);
		//referenceCollection.start();

		setSplashStatusText("Executing autostart items…");
		// Autostart
		{
			File autostartDir = new File(startupApplicationPath, "autostart");
			if(autostartDir.mkdirs())
			{
				if(logger.isDebugEnabled()) logger.debug("Created '{}'.", autostartDir.getAbsolutePath()); // NOPMD
			}
			File[] autoFiles = autostartDir.listFiles(File::isFile);

			if(autoFiles != null && autoFiles.length > 0)
			{
				Arrays.sort(autoFiles, Comparator.comparing(File::getAbsolutePath));
				for(File current : autoFiles)
				{
					AutostartRunnable r = new AutostartRunnable(current); // NOPMD - AvoidInstantiatingObjectsInLoops
					autostartProcesses.add(r);
					Thread t = new Thread(r, current.getAbsolutePath()); // NOPMD - AvoidInstantiatingObjectsInLoops
					t.setDaemon(true);
					t.start();
				}
			}
			else
			{
				if(logger.isInfoEnabled()) logger.info("No autostart files defined in '{}'.", autostartDir.getAbsolutePath());
			}
		}

		// go to source
		{
			gotoSourceProvider = new SerializingGoToSource();
			//gotoSource.start() started when needed...
		}

		setSplashStatusText("Creating global views…");
		SourceIdentifier globalSourceIdentifier = new SourceIdentifier(GLOBAL_SOURCE_IDENTIFIER_NAME, null);

		loggingFileDump = new FileDumpEventHandler<>(loggingFileBufferFactory.createActiveBuffer(globalSourceIdentifier));
		accessFileDump = new FileDumpEventHandler<>(accessFileBufferFactory.createActiveBuffer(globalSourceIdentifier));

		setGlobalLoggingEnabled(applicationPreferences.isGlobalLoggingEnabled());

		BlockingCircularBuffer<EventWrapper<LoggingEvent>> loggingEventQueue = new LilithBuffer<>(applicationPreferences, 1000);
		BlockingCircularBuffer<EventWrapper<AccessEvent>> accessEventQueue = new LilithBuffer<>(applicationPreferences, 1000);

		SourceManagerImpl<LoggingEvent> lsm = new SourceManagerImpl<>(loggingEventQueue);
		// add global view
		EventSource<LoggingEvent> globalLoggingEventSource = new EventSourceImpl<>(globalSourceIdentifier, loggingFileDump.getBuffer(), true);
		lsm.addSource(globalLoggingEventSource);

		setSplashStatusText("Creating internal view…");
		// add internal lilith logging
		EventSource<LoggingEvent> lilithLoggingEventSource = new EventSourceImpl<>(InternalLilithAppender.getSourceIdentifier(), InternalLilithAppender.getBuffer(), false);
		lsm.addSource(lilithLoggingEventSource);

		setLoggingEventSourceManager(lsm);

		SourceManagerImpl<AccessEvent> asm = new SourceManagerImpl<>(accessEventQueue);
		// add global view
		EventSource<AccessEvent> globalAccessEventSource = new EventSourceImpl<>(globalSourceIdentifier, accessFileDump.getBuffer(), true);
		asm.addSource(globalAccessEventSource);
		setAccessEventSourceManager(asm);

		ConverterRegistry<LoggingEvent> loggingConverterRegistry = new ConverterRegistry<>();
		loggingConverterRegistry.addConverter(new LogbackLoggingConverter());
		loggingConverterRegistry.addConverter(new Log4jLoggingConverter());
		loggingConverterRegistry.addConverter(new Log4j2LoggingConverter());

		ConverterRegistry<AccessEvent> accessConverterRegistry = new ConverterRegistry<>();
		accessConverterRegistry.addConverter(new LogbackAccessConverter());

		setSplashStatusText("Starting event receivers…");

		try
		{
			EventSourceProducer<LoggingEvent> producer
					= new SerializableServerSocketEventSourceProducer<>(4560, loggingConverterRegistry, new LoggingEventSourceIdentifierUpdater());
			loggingEventSourceManager.addEventSourceProducer(producer);
		}
		catch(IOException ex)
		{
			if(logger.isWarnEnabled()) logger.warn("Exception while creating event producer!", ex);
		}

		try
		{
			EventSourceProducer<LoggingEvent> producer
					= new SerializableServerSocketEventSourceProducer<>(4445, loggingConverterRegistry, new LoggingEventSourceIdentifierUpdater());
			loggingEventSourceManager.addEventSourceProducer(producer);
		}
		catch(IOException ex)
		{
			if(logger.isWarnEnabled()) logger.warn("Exception while creating event producer!", ex);
		}

		try
		{
			EventSourceProducer<LoggingEvent> producer
				= new LoggingEventProtobufServerSocketEventSourceProducer
				(ClassicMultiplexSocketAppender.COMPRESSED_DEFAULT_PORT, true);

			loggingEventSourceManager.addEventSourceProducer(producer);
			// TODO: senderService.addLoggingProducer(producer);
		}
		catch(IOException ex)
		{
			if(logger.isWarnEnabled()) logger.warn("Exception while creating event producer!", ex);
		}

		try
		{
			EventSourceProducer<LoggingEvent> producer
				= new LoggingEventProtobufServerSocketEventSourceProducer
				(ClassicMultiplexSocketAppender.UNCOMPRESSED_DEFAULT_PORT, false);

			loggingEventSourceManager.addEventSourceProducer(producer);
			// TODO: senderService.addLoggingProducer(producer);
		}
		catch(IOException ex)
		{
			if(logger.isWarnEnabled()) logger.warn("Exception while creating event producer!", ex);
		}


		try
		{
			EventSourceProducer<LoggingEvent> producer
				= new LilithXmlMessageLoggingServerSocketEventSourceProducer
				(ClassicXmlMultiplexSocketAppender.UNCOMPRESSED_DEFAULT_PORT, false);

			loggingEventSourceManager.addEventSourceProducer(producer);
		}
		catch(IOException ex)
		{
			if(logger.isWarnEnabled()) logger.warn("Exception while creating event producer!", ex);
		}

		try
		{
			EventSourceProducer<LoggingEvent> producer
				= new LilithXmlMessageLoggingServerSocketEventSourceProducer
				(ClassicXmlMultiplexSocketAppender.COMPRESSED_DEFAULT_PORT, true);

			loggingEventSourceManager.addEventSourceProducer(producer);
		}
		catch(IOException ex)
		{
			if(logger.isWarnEnabled()) logger.warn("Exception while creating event producer!", ex);
		}


		try
		{
			EventSourceProducer<LoggingEvent> producer
				= new LilithJsonMessageLoggingServerSocketEventSourceProducer
				(ClassicJsonMultiplexSocketAppender.UNCOMPRESSED_DEFAULT_PORT, false);

			loggingEventSourceManager.addEventSourceProducer(producer);
		}
		catch(IOException ex)
		{
			if(logger.isWarnEnabled()) logger.warn("Exception while creating event producer!", ex);
		}

		try
		{
			EventSourceProducer<LoggingEvent> producer
				= new LilithJsonMessageLoggingServerSocketEventSourceProducer
				(ClassicJsonMultiplexSocketAppender.COMPRESSED_DEFAULT_PORT, true);

			loggingEventSourceManager.addEventSourceProducer(producer);
		}
		catch(IOException ex)
		{
			if(logger.isWarnEnabled()) logger.warn("Exception while creating event producer!", ex);
		}

		try
		{
			EventSourceProducer<LoggingEvent> producer
				= new LilithXmlStreamLoggingServerSocketEventSourceProducer
				(ZeroDelimitedClassicXmlMultiplexSocketAppender.DEFAULT_PORT);

			loggingEventSourceManager.addEventSourceProducer(producer);
		}
		catch(IOException ex)
		{
			if(logger.isWarnEnabled()) logger.warn("Exception while creating event producer!", ex);
		}

		try
		{
			EventSourceProducer<LoggingEvent> producer
				= new LilithJsonStreamLoggingServerSocketEventSourceProducer
				(ZeroDelimitedClassicJsonMultiplexSocketAppender.DEFAULT_PORT);

			loggingEventSourceManager.addEventSourceProducer(producer);
		}
		catch(IOException ex)
		{
			if(logger.isWarnEnabled()) logger.warn("Exception while creating event producer!", ex);
		}

		try
		{
			EventSourceProducer<LoggingEvent> producer
				= new JulXmlStreamLoggingServerSocketEventSourceProducer(11_020);

			loggingEventSourceManager.addEventSourceProducer(producer);
		}
		catch(IOException ex)
		{
			if(logger.isWarnEnabled()) logger.warn("Exception while creating event producer!", ex);
		}

		try
		{
			EventSourceProducer<LoggingEvent> producer
					= new Log4j2JsonServerSocketEventSourceProducer(Log4j2Ports.JSON);

			loggingEventSourceManager.addEventSourceProducer(producer);
		}
		catch(IOException ex)
		{
			if(logger.isWarnEnabled()) logger.warn("Exception while creating event producer!", ex);
		}

		try
		{
			EventSourceProducer<LoggingEvent> producer
					= new Log4j2YamlServerSocketEventSourceProducer(Log4j2Ports.YAML);

			loggingEventSourceManager.addEventSourceProducer(producer);
		}
		catch(IOException ex)
		{
			if(logger.isWarnEnabled()) logger.warn("Exception while creating event producer!", ex);
		}

		try
		{
			EventSourceProducer<LoggingEvent> producer
					= new Log4j2XmlServerSocketEventSourceProducer(Log4j2Ports.XML);

			loggingEventSourceManager.addEventSourceProducer(producer);
		}
		catch(IOException ex)
		{
			if(logger.isWarnEnabled()) logger.warn("Exception while creating event producer!", ex);
		}

		try
		{
			EventSourceProducer<AccessEvent> producer
					= new SerializableServerSocketEventSourceProducer<>(4570, accessConverterRegistry, null);
			accessEventSourceManager.addEventSourceProducer(producer);
		}
		catch(IOException ex)
		{
			if(logger.isWarnEnabled()) logger.warn("Exception while creating event producer!", ex);
		}
		try
		{
			EventSourceProducer<AccessEvent> producer
				= new AccessEventProtobufServerSocketEventSourceProducer
				(AccessMultiplexSocketAppender.COMPRESSED_DEFAULT_PORT, true);

			accessEventSourceManager.addEventSourceProducer(producer);
		}
		catch(IOException ex)
		{
			if(logger.isWarnEnabled()) logger.warn("Exception while creating event producer!", ex);
		}

		try
		{
			EventSourceProducer<AccessEvent> producer
				= new AccessEventProtobufServerSocketEventSourceProducer
				(AccessMultiplexSocketAppender.UNCOMPRESSED_DEFAULT_PORT, false);

			accessEventSourceManager.addEventSourceProducer(producer);
		}
		catch(IOException ex)
		{
			if(logger.isWarnEnabled()) logger.warn("Exception while creating event producer!", ex);
		}

		setSplashStatusText("Setting up event handlers…");

		AlarmSoundLoggingEventHandler loggingEventAlarmSound = new AlarmSoundLoggingEventHandler();
		loggingEventAlarmSound.setSounds(sounds);

		FileSplitterEventHandler<LoggingEvent> fileSplitterLoggingEventHandler =
			new FileSplitterEventHandler<>(loggingFileBufferFactory, loggingEventSourceManager);

		List<EventHandler<LoggingEvent>> loggingHandlers = new ArrayList<>();

		loggingHandlers.add(loggingEventAlarmSound);
		loggingHandlers.add(fileSplitterLoggingEventHandler);
		loggingHandlers.add(loggingFileDump);

		loggingEventSourceManager.setEventHandlers(loggingHandlers);
		loggingEventSourceManager.start();

		List<EventHandler<AccessEvent>> accessHandlers = new ArrayList<>();

		FileSplitterEventHandler<AccessEvent> fileSplitterAccessEventHandler =
			new FileSplitterEventHandler<>(accessFileBufferFactory, accessEventSourceManager);
		AlarmSoundAccessEventHandler accessEventAlarmSound = new AlarmSoundAccessEventHandler();
		accessEventAlarmSound.setSounds(sounds);
		accessHandlers.add(accessEventAlarmSound);
		accessHandlers.add(fileSplitterAccessEventHandler);
		accessHandlers.add(accessFileDump);

		accessEventSourceManager.setEventHandlers(accessHandlers);
		accessEventSourceManager.start();

		viewActions.updateWindowMenu();

		if(Lilith.APP_SNAPSHOT || applicationPreferences.isCheckingForUpdate())
		{
			// always check for update in case of SNAPSHOT!
			checkForUpdate(false);
		}
		updateConditions(); // to initialize active conditions.

		if(applicationPreferences.isShowingTipOfTheDay())
		{
			showTipOfTheDayDialog();
		}

		cleanObsoleteFiles();

		traySupport = TraySupport.getInstance();
		if(traySupport != null)
		{
			traySupport.setMainFrame(this);
		}
		setSplashStatusText("Done!");
	}

	public PreferencesDialog getPreferencesDialog()
	{
		return preferencesDialog;
	}

	public ViewActions getViewActions()
	{
		return viewActions;
	}

	public JDesktopPane getDesktop()
	{
		return desktop;
	}

	void showTipOfTheDayDialog()
	{
		Windows.showWindow(tipOfTheDayDialog, this, true);
	}

	private void updateTaskStatus()
	{
		int numberOfTasks = longTaskManager.getNumberOfTasks();
		if(numberOfTasks != previousNumberOfTasks)
		{
			previousNumberOfTasks = numberOfTasks;
			String text = "";
			Icon icon = null;
			if(numberOfTasks == 1)
			{
				text = "1 active task.";
				icon = Icons.PROGRESS_ICON;
			}
			else if(numberOfTasks > 1)
			{
				text = Integer.toString(numberOfTasks) + " active tasks.";
				icon = Icons.PROGRESS_ICON;
			}
			taskStatusLabel.setText(text);
			taskStatusLabel.setIcon(icon);
		}
	}

	private void setSplashStatusText(String text)
	{
		if(splashScreen != null)
		{
			splashScreen.setStatusText(text);
		}
	}

	public void updateWindowMenus()
	{
		processViewActions(UPDATE_WINDOW_MENU_ACTIONS_PROCESSOR);

		JInternalFrame selected = desktop.getSelectedFrame();
		if(logger.isDebugEnabled()) logger.debug("Selected IFrame: {}", selected);
		if(selected instanceof ViewContainerInternalFrame)
		{
			ViewContainerInternalFrame internalFrame = (ViewContainerInternalFrame) selected;
			viewActions.setViewContainer(internalFrame.getViewContainer());
		}
		else
		{
			viewActions.setViewContainer(null); // no frame or task manager
		}

	}


	void closeLoggingConnection(SourceIdentifier id)
	{
		loggingEventSourceManager.removeEventProducer(id);
	}

	void closeAccessConnection(SourceIdentifier id)
	{
		accessEventSourceManager.removeEventProducer(id);
	}

	void goToSource(StackTraceElement stackTraceElement)
	{
		if(stackTraceElement == null)
		{
			return;
		}
		if(gotoSourceProvider != null)
		{
			gotoSourceProvider.goToSource(stackTraceElement);
		}
	}

	void setActiveConnectionsCounter(int activeCounter)
	{
		this.activeCounter = activeCounter;
		updateStatus();
	}

	void checkForUpdate(boolean showAlways)
	{
		Thread t = new Thread(new CheckForUpdateRunnable(showAlways, applicationPreferences.isCheckingForSnapshot()));
		t.start();
	}

	public Colors getColors(HttpStatus.Type status)
	{
		if(statusColors == null)
		{
			initStatusColors();
		}
		return cloneColors(statusColors.get(status));
	}

	public Colors getColors(LoggingEvent.Level level)
	{
		if(levelColors == null)
		{
			initLevelColors();
		}
		return cloneColors(levelColors.get(level));
	}

	private static Colors cloneColors(Colors c)
	{
		if(c != null)
		{
			try
			{
				c = c.clone();
			}
			catch(CloneNotSupportedException e)
			{
				final Logger logger = LoggerFactory.getLogger(MainFrame.class);
				if(logger.isErrorEnabled()) logger.error("Exception while cloning Colors!!", e);
			}
		}
		return c;
	}

	public Colors getColors(EventWrapper eventWrapper)
	{
		if(!EventQueue.isDispatchThread())
		{
			if(logger.isErrorEnabled()) logger.error("Not on EventDispatchThread!"); // NOPMD
		}

		ColorScheme result=null;
		if(activeConditions != null)
		{
			for(SavedCondition current : activeConditions)
			{
				Condition condition = current.getCondition();
				if(condition != null && condition.isTrue(eventWrapper))
				{
					if(result == null)
					{
						result=current.getColorScheme();
						if(result != null)
						{
							try
							{
								result = result.clone();
							}
							catch(CloneNotSupportedException e)
							{
								if(logger.isErrorEnabled()) logger.error("Exception while cloning ColorScheme!!", e);
							}
						}
					}
					else
					{
						result.mergeWith(current.getColorScheme());
					}

				}
				if(result != null && result.isAbsolute())
				{
					return new Colors(result, false);
				}
			}
		}


		if(coloringWholeRow)
		{
			Object eventObj = eventWrapper.getEvent();
			if(eventObj instanceof LoggingEvent)
			{
				Colors c=getColors(((LoggingEvent) eventObj).getLevel());
				if(result != null)
				{
					return new Colors(result.mergeWith(c.getColorScheme()), false);
				}
				return c;
			}
			if(eventObj instanceof AccessEvent)
			{
				Colors c=getColors(HttpStatus.getType(((AccessEvent) eventObj).getStatusCode()));
				if(result != null)
				{
					return new Colors(result.mergeWith(c.getColorScheme()), false);
				}
				return c;
			}
		}

		// return the previously found ColorScheme even though it's not absolute
		if(result != null)
		{
			return  new Colors(result, false);
		}
		return null;
	}

	public void open()
	{
		int returnVal = openFileChooser.showOpenDialog(this);

		if(returnVal == JFileChooser.APPROVE_OPTION)
		{
			open(openFileChooser.getSelectedFile());
		}
	}

	void importFile()
	{
		int returnVal = importFileChooser.showOpenDialog(this);

		if(returnVal == JFileChooser.APPROVE_OPTION)
		{
			File importFile = importFileChooser.getSelectedFile();
			String fileName = importFile.getAbsolutePath();
			if(fileName.toLowerCase(Locale.US).endsWith(FileConstants.FILE_EXTENSION))
			{
				open(importFile);
				return;
			}
			importFile(importFile);
		}
	}

	void exportFile(EventWrapperViewPanel view)
	{
		long size=view.getEventSource().getBuffer().getSize();
		if(size == 0)
		{
			return;
		}
		int returnVal = exportFileChooser.showSaveDialog(this);

		if(returnVal == JFileChooser.APPROVE_OPTION)
		{
			if(size > EXPORT_WARNING_SIZE)
			{
				String dialogTitle = "Large export! Are you sure?";
				String message = "You are about to export "+size+" events. This could take some time.\nAre you sure you want to export?";
				int result = JOptionPane.showConfirmDialog(this, message, dialogTitle,
					JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
				if(JOptionPane.OK_OPTION != result)
				{
					return;
				}
			}
			File file = exportFileChooser.getSelectedFile();
			String fileName = file.getAbsolutePath();
			String baseName;
			if(fileName.toLowerCase(Locale.US).endsWith(FileConstants.FILE_EXTENSION))
			{
				baseName=fileName.substring(0, fileName.length()-FileConstants.FILE_EXTENSION.length());
			}
			else
			{
				baseName=fileName;
			}

			File dataFile=new File(baseName+FileConstants.FILE_EXTENSION);
			File indexFile=new File(baseName+FileConstants.INDEX_FILE_EXTENSION);

			if(dataFile.isFile())
			{
				String dialogTitle = "Overwrite file?";
				String message = "Data file does already exist!\nOverwrite data file?";
				int result = JOptionPane.showConfirmDialog(this, message, dialogTitle,
					JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
				if(JOptionPane.OK_OPTION != result)
				{
					return;
				}
				if(!dataFile.delete())
				{
					if(logger.isWarnEnabled()) logger.warn("Couldn't delete existing file {}!", dataFile.getAbsolutePath()); // NOPMD
				}
				if(indexFile.isFile() && !indexFile.delete())
				{
					if(logger.isWarnEnabled()) logger.warn("Couldn't delete existing file {}!", indexFile.getAbsolutePath()); // NOPMD
				}
			}

			if(view instanceof AccessEventViewPanel)
			{
				exportFile(dataFile, indexFile, (AccessEventViewPanel) view);
			}
			else if(view instanceof LoggingEventViewPanel)
			{
				exportFile(dataFile, indexFile, (LoggingEventViewPanel) view);
			}
		}
	}

	private void exportFile(File dataFile, File indexFile, AccessEventViewPanel viewPanel)
	{
		Map<String, String> metaData = new HashMap<>();
		metaData.put(FileConstants.CONTENT_FORMAT_KEY, FileConstants.CONTENT_FORMAT_VALUE_PROTOBUF);
		metaData.put(FileConstants.CONTENT_TYPE_KEY, FileConstants.CONTENT_TYPE_VALUE_ACCESS);
		metaData.put(FileConstants.COMPRESSION_KEY, FileConstants.COMPRESSION_VALUE_GZIP);

		FileBuffer<EventWrapper<AccessEvent>> outputBuffer =
			accessFileBufferFactory.createBuffer(dataFile, indexFile, metaData);

		Buffer<EventWrapper<AccessEvent>> inputBuffer = viewPanel.getEventSource().getBuffer();

		String name="Export to "+dataFile.getName();
		String description="Exporting "+inputBuffer.getSize()+" access events into file '"+dataFile.getAbsolutePath()+"'.";
		Task<Long> task = longTaskManager.startTask(new ExportCallable<>(inputBuffer, outputBuffer), name, description);
		if(logger.isInfoEnabled()) logger.info("Task-Name: {}", task.getName());
	}

	private void exportFile(File dataFile, File indexFile, LoggingEventViewPanel viewPanel)
	{
		Map<String, String> metaData = new HashMap<>();
		metaData.put(FileConstants.CONTENT_FORMAT_KEY, FileConstants.CONTENT_FORMAT_VALUE_PROTOBUF);
		metaData.put(FileConstants.CONTENT_TYPE_KEY, FileConstants.CONTENT_TYPE_VALUE_LOGGING);
		metaData.put(FileConstants.COMPRESSION_KEY, FileConstants.COMPRESSION_VALUE_GZIP);

		FileBuffer<EventWrapper<LoggingEvent>> outputBuffer =
			loggingFileBufferFactory.createBuffer(dataFile, indexFile, metaData);

		Buffer<EventWrapper<LoggingEvent>> inputBuffer = viewPanel.getEventSource().getBuffer();

		String name="Export to "+dataFile.getName();
		String description="Exporting "+inputBuffer.getSize()+" logging events into file '"+dataFile.getAbsolutePath()+"'.";
		Task<Long> task = longTaskManager.startTask(new ExportCallable<>(inputBuffer, outputBuffer), name, description);
		if(logger.isInfoEnabled()) logger.info("Task-Name: {}", task.getName());
	}

	public void open(File dataFile)
	{
		if(logger.isInfoEnabled()) logger.info("Open file: {}", dataFile.getAbsolutePath());
		if(!dataFile.isFile())
		{
			String message = "'" + dataFile.getAbsolutePath() + "' is not a file!";
			JOptionPane.showMessageDialog(this, message, "Can't open file…", JOptionPane.ERROR_MESSAGE);
			return;
		}
		if(!dataFile.canRead())
		{
			String message = "Can't read from '" + dataFile.getAbsolutePath() + "'!";
			JOptionPane.showMessageDialog(this, message, "Can't open file…", JOptionPane.ERROR_MESSAGE);
			return;
		}
		if(isViewAlreadyOpen(dataFile))
		{
			return;
		}
		String fileName = dataFile.getAbsolutePath();
		String indexFileName;
		if(fileName.toLowerCase(Locale.US).endsWith(FileConstants.FILE_EXTENSION))
		{
			indexFileName = fileName.substring(0, fileName.length() - FileConstants.FILE_EXTENSION.length());
		}
		else
		{
			indexFileName = fileName;
		}
		indexFileName = indexFileName + FileConstants.INDEX_FILE_EXTENSION;


		File indexFile = new File(indexFileName);
		if(!indexFile.isFile())
		{
			// Index file does not exist. Ask if it should be indexed.
			String dialogTitle = "Index file?";
			String message = "Index file does not exist!\nIndex data file right now?";
			int result = JOptionPane.showConfirmDialog(this, message, dialogTitle,
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
			if(JOptionPane.OK_OPTION != result)
			{
				// file will not be opened.
				return;
			}
			String name = "Indexing Lilith file";
			String description = "Indexing '" + dataFile.getAbsolutePath() + "'…";
			Task<Long> task = longTaskManager.startTask(new IndexingCallable(dataFile, indexFile), name, description);
			if(logger.isInfoEnabled()) logger.info("Task-Name: {}", task.getName());
			// opening of view will be done by the task
			return;
		}

		// Previous index file was found
		long dataModified=dataFile.lastModified();
		long indexModified=indexFile.lastModified();
		if(indexModified < dataModified)
		{
			// Index file is outdated. Ask if it should be re-indexed.
			String dialogTitle = "Index outdated. Re-index file?";
			String message = "The index file is older than the data file!\nRe-index data file right now?";
			int result = JOptionPane.showConfirmDialog(this, message, dialogTitle,
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
			if(JOptionPane.OK_OPTION == result)
			{
				if(indexFile.delete()) // NOPMD
				{
					if(logger.isInfoEnabled()) logger.info("Deleted previous index file {}.", indexFile.getAbsolutePath());
					String name = "Re-indexing Lilith file";
					String description = "Re-indexing '" + dataFile.getAbsolutePath() + "'…";
					Task<Long> task = longTaskManager.startTask(new IndexingCallable(dataFile, indexFile), name, description);
					if(logger.isInfoEnabled()) logger.info("Task-Name: {}", task.getName());
					// opening of view will be done by the task
					return;
				}
			}
			// It's fine to use the outdated index.
		}
		// use existing index file
		createViewFor(dataFile, indexFile, true);
	}

	private boolean isViewAlreadyOpen(File dataFile)
	{
		ViewContainer<?> viewContainer = resolveViewContainer(dataFile);
		if(viewContainer != null)
		{
			showView(viewContainer);
			String message = "File '" + dataFile.getAbsolutePath() + "' is already open.";
			JOptionPane.showMessageDialog(this, message, "File is already open…", JOptionPane.INFORMATION_MESSAGE);
			return true;
		}
		return false;
	}

	private void importFile(File importFile)
	{
		if(logger.isInfoEnabled()) logger.info("Import file: {}", importFile.getAbsolutePath());

		if(!importFile.isFile())
		{
			String message = "'" + importFile.getAbsolutePath() + "' is not a file!";
			JOptionPane.showMessageDialog(this, message, "Can't import file…", JOptionPane.ERROR_MESSAGE);
			return;
		}
		if(!importFile.canRead())
		{
			String message = "Can't read from '" + importFile.getAbsolutePath() + "'!";
			JOptionPane.showMessageDialog(this, message, "Can't import file…", JOptionPane.ERROR_MESSAGE);
			return;
		}

		final String inputName = importFile.getName();
		final File parentFile = importFile.getParentFile();

		final File dataFile = new File(parentFile, inputName + FileConstants.FILE_EXTENSION);
		// check if file exists and warn in that case
		if(dataFile.isFile())
		{
			// check if file is already open
			if(isViewAlreadyOpen(dataFile))
			{
				return;
			}
			String dialogTitle = "Re-import file?";
			String message = "Data file does already exist!\nRe-import data file right now?";
			int result = JOptionPane.showConfirmDialog(this, message, dialogTitle,
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
			if(JOptionPane.OK_OPTION != result)
			{
				return;
			}

			if(dataFile.delete())
			{
				if(logger.isInfoEnabled()) logger.info("Deleted file '{}'.", dataFile.getAbsolutePath()); // NOPMD
			}
		}

		File indexFile = new File(parentFile, inputName + FileConstants.INDEX_FILE_EXTENSION);
		if(indexFile.isFile() && indexFile.delete())
		{
			if(logger.isInfoEnabled()) logger.info("Deleted file '{}'.", indexFile.getAbsolutePath()); // NOPMD
		}

		Map<String, String> metaData = new HashMap<>();
		metaData.put(FileConstants.CONTENT_FORMAT_KEY, FileConstants.CONTENT_FORMAT_VALUE_PROTOBUF);
		metaData.put(FileConstants.CONTENT_TYPE_KEY, FileConstants.CONTENT_TYPE_VALUE_LOGGING);
		metaData.put(FileConstants.COMPRESSION_KEY, FileConstants.COMPRESSION_VALUE_GZIP);

		FileBuffer<EventWrapper<LoggingEvent>> buffer =
			loggingFileBufferFactory.createBuffer(dataFile, indexFile, metaData);

		ImportType type = resolveType(importFile);
		if(type == ImportType.LOG4J)
		{
			String name = "Importing Log4J XML file";
			String description = "Importing Log4J XML file '" + importFile.getAbsolutePath() + "'…";
			Task<Long> task = longTaskManager.startTask(new Log4jImportCallable(importFile, buffer), name, description);
			if(logger.isInfoEnabled()) logger.info("Task-Name: {}", task.getName());
			return;
		}
		if(type == ImportType.JUL)
		{
			String name = "Importing java.util.logging XML file";
			String description = "Importing java.util.logging XML file '" + importFile.getAbsolutePath() + "'…";
			Task<Long> task = longTaskManager.startTask(new JulImportCallable(importFile, buffer), name, description);
			if(logger.isInfoEnabled()) logger.info("Task-Name: {}", task.getName());
			return;
		}

		// show warning "Unknown type"
		String message = "Couldn't detect type of file '" + importFile.getAbsolutePath() + "'.\nFile is unsupported.";
		JOptionPane.showMessageDialog(this, message, "Unknown file type…", JOptionPane.WARNING_MESSAGE);
	}

	@SuppressWarnings("PMD.CloseResource")
	private ImportType resolveType(File inputFile)
	{
		BufferedReader br = null;
		try
		{
			InputStream fis = Files.newInputStream(inputFile.toPath());

			String fileName=inputFile.getName().toLowerCase(Locale.US);
			if(fileName.endsWith(".gz"))
			{
				br = new BufferedReader(new InputStreamReader(new GZIPInputStream(fis), StandardCharsets.UTF_8));
			}
			else
			{
				br = new BufferedReader(new InputStreamReader(fis, StandardCharsets.UTF_8));
			}
			for(int i = 0; i < 5; i++)
			{
				String line = br.readLine();
				if(line == null)
				{
					break;
				}
				if(line.contains("<log4j:"))
				{
					return ImportType.LOG4J;
				}
				if(line.contains("<log>") || line.contains("<record>"))
				{
					return ImportType.JUL;
				}
			}
		}
		catch(IOException ex)
		{
			if(logger.isWarnEnabled()) logger.warn("Exception while resolving type of file!", ex);
		}
		finally
		{
			if(br != null)
			{
				try
				{
					br.close();
				}
				catch(IOException e)
				{
					// ignore
				}
			}
		}
		return null;
	}

	void zoomOut()
	{
		double scale = applicationPreferences.getScaleFactor() - SCALE_FACTOR;
		if(scale < 0.1d)
		{
			scale = 0.1d;
		}
		applicationPreferences.setScaleFactor(scale);
	}

	void zoomIn()
	{
		double scale = applicationPreferences.getScaleFactor() + SCALE_FACTOR;
		applicationPreferences.setScaleFactor(scale);
	}

	void resetZoom()
	{
		applicationPreferences.setScaleFactor(1.0d);
	}

	void troubleshooting()
	{
		preferencesDialog.showPane(PreferencesDialog.Panes.Troubleshooting);
	}

	void openHelp(String help)
	{
		if(logger.isInfoEnabled()) logger.info("Opening help: {}", help);
		helpFrame.setHelpUrl(help);
		if(!helpFrame.isVisible())
		{
			Windows.showWindow(helpFrame, this, false);
		}
		helpFrame.toFront();
	}


	public void deleteAllLogs()
	{
		processViewContainers(RESET_CONTAINER_PROCESSOR);
		cleanAllInactiveLogs();
	}

	public boolean openUriString(String uri)
	{
		if(uri.startsWith(HELP_URI_PREFIX))
		{
			String value = uri.substring(HELP_URI_PREFIX.length());
			openHelp(value);
			return true;
		}

		if(uri.startsWith(PREFERENCES_URI_PREFIX))
		{
			String value = uri.substring(PREFERENCES_URI_PREFIX.length());
			int hashIndex = value.indexOf('#');
			String paneName;
			String actionName;
			if(hashIndex >= 0)
			{
				paneName = value.substring(0, hashIndex);
				actionName = value.substring(hashIndex+1);
			}
			else
			{
				paneName = value;
				actionName = null;
			}
			if(actionName != null)
			{
				try
				{
					preferencesDialog.executeAction(PreferencesDialog.Actions.valueOf(actionName));
					return true;
				}
				catch(IllegalArgumentException ex)
				{
					if(logger.isWarnEnabled()) logger.warn("Couldn't resolve preferences action '{}'!", actionName);
				}
			}
			try
			{
				PreferencesDialog.Panes pane = PreferencesDialog.Panes.valueOf(paneName);
				preferencesDialog.showPane(pane);
			}
			catch(IllegalArgumentException ex)
			{
				if(logger.isWarnEnabled()) logger.warn("Couldn't resolve preferences pane '{}'!", paneName);
			}
			return true;
		}

		if(uri.startsWith(STACK_TRACE_ELEMENT_URI_PREFIX))
		{
			String steStr = uri.substring(STACK_TRACE_ELEMENT_URI_PREFIX.length());
			steStr = SimpleXml.unescape(steStr);
			ExtendedStackTraceElement extendedStackTraceElement = ExtendedStackTraceElement.parseStackTraceElement(steStr);
			if(logger.isDebugEnabled()) logger.debug("STE: {}", extendedStackTraceElement);
			if(extendedStackTraceElement != null)
			{
				goToSource(extendedStackTraceElement.getStackTraceElement());
			}
			return true;
		}

		if(uri.contains("://"))
		{
			try
			{
				openUrl(new URL(uri));
			}
			catch(MalformedURLException e)
			{
				if(logger.isInfoEnabled()) logger.info("Couldn't create URL for uri-string {}!", uri, e);
			}
			return true;
		}

		if(uri.contains("coin:") || uri.startsWith("mailto:"))
		{
			try
			{
				openUri(new URI(uri));
			}
			catch(URISyntaxException e)
			{
				if(logger.isInfoEnabled()) logger.info("Couldn't create URI for uri-string {}!", uri, e);
			}
			return true;
		}
		return false;
	}

	public enum ImportType
	{
		LOG4J, JUL
	}

	private ViewContainer<?> resolveViewContainer(File dataFile)
	{
		{ // logging
			Map<EventSource<LoggingEvent>, ViewContainer<LoggingEvent>> views = loggingEventViewManager.getViews();
			for(Map.Entry<EventSource<LoggingEvent>, ViewContainer<LoggingEvent>> current : views.entrySet())
			{

				ViewContainer<LoggingEvent> view = current.getValue();
				EventWrapperViewPanel<LoggingEvent> defaultView = view.getDefaultView();
				EventSource<LoggingEvent> es = defaultView.getEventSource();
				if(es != null)
				{
					Buffer<EventWrapper<LoggingEvent>> buffer = es.getBuffer();
					if(buffer instanceof CodecFileBuffer)
					{
						CodecFileBuffer cfb = (CodecFileBuffer) buffer;
						if(dataFile.equals(cfb.getDataFile()))
						{
							return view;
						}
					}
				}
			}
		}

		{ // access
			Map<EventSource<AccessEvent>, ViewContainer<AccessEvent>> views = accessEventViewManager.getViews();
			for(Map.Entry<EventSource<AccessEvent>, ViewContainer<AccessEvent>> current : views.entrySet())
			{

				ViewContainer<AccessEvent> view = current.getValue();
				EventWrapperViewPanel<AccessEvent> defaultView = view.getDefaultView();
				EventSource<AccessEvent> es = defaultView.getEventSource();
				if(es != null)
				{
					Buffer<EventWrapper<AccessEvent>> buffer = es.getBuffer();
					if(buffer instanceof CodecFileBuffer)
					{
						CodecFileBuffer cfb = (CodecFileBuffer) buffer;
						if(dataFile.equals(cfb.getDataFile()))
						{
							return view;
						}
					}
				}
			}
		}

		return null;
	}

	private void createViewFor(File dataFile, File indexFile, boolean keepUpdating)
	{
		// create view for dataFile and indexFile.
		if(logger.isInfoEnabled())
		{
			logger
				.info("Create view for dataFile '{}' and indexFile '{}'.", dataFile.getAbsolutePath(), indexFile.getAbsolutePath());
		}

		FileHeaderStrategy fileHeaderStrategy = new DefaultFileHeaderStrategy();
		try
		{
			FileHeader header = fileHeaderStrategy.readFileHeader(dataFile);
			if(header == null)
			{
				if(logger.isWarnEnabled())
				{
					logger.warn("Couldn't read file header from '{}'!", dataFile.getAbsolutePath());
				}
				return;
			}
			if(header.getMagicValue() != FileConstants.MAGIC_VALUE)
			{
				if(logger.isWarnEnabled()) logger.warn("Invalid magic value! {}", Integer.toHexString(header.getMagicValue()));
				return;
			}
			MetaData metaData = header.getMetaData();
			if(metaData == null /*|| metaData.getData() == null*/)
			{
				if(logger.isWarnEnabled())
				{
					logger.warn("Couldn't read meta data from '{}'!", dataFile.getAbsolutePath());
				}
				return;
			}
			Map<String, String> data = metaData.getData();
			String contentType = data.get(FileConstants.CONTENT_TYPE_KEY);
			Map<String, String> usedMetaData = new HashMap<>();
			SourceIdentifier si = new SourceIdentifier(dataFile.getAbsolutePath());

			switch (contentType)
			{
				case FileConstants.CONTENT_TYPE_VALUE_LOGGING:
				{
					FileBuffer<EventWrapper<LoggingEvent>> buffer = loggingFileBufferFactory.createBuffer(dataFile, indexFile, usedMetaData);
					EventSource<LoggingEvent> eventSource = new EventSourceImpl<>(si, buffer, false);
					ViewContainer<LoggingEvent> viewContainer = loggingEventViewManager.retrieveViewContainer(eventSource);
					EventWrapperViewPanel<LoggingEvent> panel = viewContainer.getDefaultView();
					if (keepUpdating)
					{
						panel.setState(LoggingViewState.UPDATING_FILE);
						viewContainer.setUpdateCallable(new CheckFileChangeCallable(dataFile, indexFile, viewContainer));
					}
					else
					{
						panel.setState(LoggingViewState.STALE_FILE);
					}
					showLoggingView(eventSource);
					applicationPreferences.addRecentFile(dataFile);
					break;
				}
				case FileConstants.CONTENT_TYPE_VALUE_ACCESS:
				{
					FileBuffer<EventWrapper<AccessEvent>> buffer = accessFileBufferFactory.createBuffer(dataFile, indexFile, usedMetaData);
					EventSource<AccessEvent> eventSource = new EventSourceImpl<>(si, buffer, false);
					ViewContainer<AccessEvent> viewContainer = accessEventViewManager.retrieveViewContainer(eventSource);
					EventWrapperViewPanel<AccessEvent> panel = viewContainer.getDefaultView();
					if (keepUpdating)
					{
						panel.setState(LoggingViewState.UPDATING_FILE);
						viewContainer.setUpdateCallable(new CheckFileChangeCallable(dataFile, indexFile, viewContainer));
					}
					else
					{
						panel.setState(LoggingViewState.STALE_FILE);
					}
					showAccessView(eventSource);
					applicationPreferences.addRecentFile(dataFile);
					break;
				}
				default:
					if (logger.isWarnEnabled()) logger.warn("Unexpected content type {}.", contentType);
					applicationPreferences.removeRecentFile(dataFile);
					break;
			}
		}
		catch(IOException e)
		{
			if(logger.isWarnEnabled()) logger.warn("Exception while creating view form file!", e);
		}
	}


	/*
	// implement cache?
	private static class SoftColorsReference
		extends SoftReference<Colors>
	{
		private static final Colors NULL_COLORS = new Colors();

		private EventIdentifier id;

		public SoftColorsReference(EventIdentifier id, Colors o, ReferenceQueue<Colors> referenceQueue)
		{
			super(o != null ? o : new Colors(), referenceQueue);
			this.id = id;
		}

		public EventIdentifier getId()
		{
			return id;
		}

		public Colors getColors()
		{
			Colors result = get();
			if(NULL_COLORS.equals(result))
			{
				return null;
			}
			return result;
		}
	}
    */


	private void initStatusColors()
	{
		Map<HttpStatus.Type, ColorScheme> prefValue = applicationPreferences.getStatusColors();
		Map<HttpStatus.Type, Colors> colors = new HashMap<>();
		for(Map.Entry<HttpStatus.Type, ColorScheme> current : prefValue.entrySet())
		{
			colors.put(current.getKey(), new Colors(current.getValue(), false)); // NOPMD - AvoidInstantiatingObjectsInLoops
		}
		statusColors = colors;
	}


	private void initLevelColors()
	{
		Map<LoggingEvent.Level, ColorScheme> prefValue = applicationPreferences.getLevelColors();
		Map<LoggingEvent.Level, Colors> colors = new HashMap<>();
		for(Map.Entry<LoggingEvent.Level, ColorScheme> current : prefValue.entrySet())
		{
			colors.put(current.getKey(), new Colors(current.getValue(), false)); // NOPMD - AvoidInstantiatingObjectsInLoops
		}
		levelColors = colors;
	}

	private void setLoggingEventSourceManager(SourceManager<LoggingEvent> loggingEventSourceManager)
	{
		if(this.loggingEventSourceManager != null)
		{
			this.loggingEventSourceManager.removeEventSourceListener(loggingSourceListener);
		}
		this.loggingEventSourceManager = loggingEventSourceManager;
		if(this.loggingEventSourceManager != null)
		{
			this.loggingEventSourceManager.addEventSourceListener(loggingSourceListener);

			List<EventSource<LoggingEvent>> sources = this.loggingEventSourceManager.getSources();
			sources.forEach(loggingEventViewManager::retrieveViewContainer);
		}
	}

	private void setAccessEventSourceManager(SourceManager<AccessEvent> accessEventSourceManager)
	{
		if(this.accessEventSourceManager != null)
		{
			this.accessEventSourceManager.removeEventSourceListener(accessSourceListener);
		}
		this.accessEventSourceManager = accessEventSourceManager;
		if(this.accessEventSourceManager != null)
		{
			this.accessEventSourceManager.addEventSourceListener(accessSourceListener);

			List<EventSource<AccessEvent>> sources = this.accessEventSourceManager.getSources();
			sources.forEach(accessEventViewManager::retrieveViewContainer);
		}
	}

	public void setSounds(Sounds sounds)
	{
		if(sounds != null)
		{
			sounds.setSoundLocations(applicationPreferences.getSoundLocations());
			sounds.setMute(applicationPreferences.isMute());
		}
		this.sounds = sounds;
	}

	private ViewContainer<LoggingEvent> retrieveLoggingViewContainer(EventSource<LoggingEvent> eventSource)
	{
		return loggingEventViewManager.retrieveViewContainer(eventSource);
	}

	private ViewContainer<AccessEvent> retrieveAccessViewContainer(EventSource<AccessEvent> eventSource)
	{
		return accessEventViewManager.retrieveViewContainer(eventSource);
	}

	public ApplicationPreferences getApplicationPreferences()
	{
		return applicationPreferences;
	}

	public TaskManager<Long> getLongWorkManager()
	{
		return longTaskManager;
	}

	public FileBufferFactory<AccessEvent> getAccessFileBufferFactory()
	{
		return accessFileBufferFactory;
	}

	public FileBufferFactory<LoggingEvent> getLoggingFileBufferFactory()
	{
		return loggingFileBufferFactory;
	}

	void showLoggingView(EventSource<LoggingEvent> eventSource)
	{
		ViewContainer<LoggingEvent> container = retrieveLoggingViewContainer(eventSource);
		showView(container);
	}

	void showAccessView(EventSource<AccessEvent> eventSource)
	{
		ViewContainer<AccessEvent> container = retrieveAccessViewContainer(eventSource);
		showView(container);
	}

	private void showView(ViewContainer<?> container)
	{
		// we need this since this method might also be called by a different thread
		ShowViewRunnable runnable = new ShowViewRunnable(container);
		if(EventQueue.isDispatchThread())
		{
			runnable.run();
		}
		else
		{
			EventQueue.invokeLater(runnable);
		}
	}

	void openPreviousLogging(SourceIdentifier si)
	{
		FileBuffer<EventWrapper<LoggingEvent>> buffer = loggingFileBufferFactory.createBuffer(si);
		EventSource<LoggingEvent> eventSource = new EventSourceImpl<>(si, buffer, false);

		ViewContainer<LoggingEvent> container = retrieveLoggingViewContainer(eventSource);
		EventWrapperViewPanel<LoggingEvent> panel = container.getDefaultView();
		panel.setState(LoggingViewState.INACTIVE);
		showLoggingView(eventSource);
	}

	void openPreviousAccess(SourceIdentifier si)
	{
		FileBuffer<EventWrapper<AccessEvent>> buffer = accessFileBufferFactory.createBuffer(si);
		EventSource<AccessEvent> eventSource = new EventSourceImpl<>(si, buffer, false);

		ViewContainer<AccessEvent> container = retrieveAccessViewContainer(eventSource);
		EventWrapperViewPanel<AccessEvent> panel = container.getDefaultView();
		panel.setState(LoggingViewState.INACTIVE);
		showAccessView(eventSource);
	}

	private void updateStatus()
	{
		StringBuilder statusText = new StringBuilder(100);

		LilithPreferences.SourceFiltering filtering = applicationPreferences.getSourceFiltering();
		switch(filtering)
		{
			case BLACKLIST:
				statusText.append("Blacklisting on '");
				statusText.append(applicationPreferences.getBlackListName());
				statusText.append("'.  ");
				break;
			case WHITELIST:
				statusText.append("Whitelisting on '");
				statusText.append(applicationPreferences.getWhiteListName());
				statusText.append("'.  ");
				break;
			default: // NONE
				break;
		}

		if(activeCounter == 0)
		{
			statusText.append("No active connections.");
		}
		else if(activeCounter == 1)
		{
			statusText.append("One active connection.");
		}
		else if(activeCounter > 1)
		{
			statusText.append(activeCounter).append(" active connections.");
		}
		String status = statusText.toString();

		statusLabel.setText(status);
		if(traySupport != null)
		{
			traySupport.setToolTip(status);
		}
	}


	public String createMessage(EventWrapper wrapper)
	{
		if(wrapper == null)
		{
			return "<html><body></body></html>";
		}

		String result;
		if(usingThymeleaf)
		{
			result = thymeleafFormatter.toString(wrapper);
		}
		else
		{
			result = groovyFormatter.toString(wrapper);
		}
		if(result == null)
		{
			if(logger.isWarnEnabled()) logger.warn("createMessage with usingThymeleaf={} failed for {}!", usingThymeleaf, wrapper);
			return AbstractHtmlFormatter.createErrorHtml("Failed to create details view!", "Formatter returned null.", null);
		}
		return result;
	}

	SortedMap<EventSource<LoggingEvent>, ViewContainer<LoggingEvent>> getSortedLoggingViews()
	{
		Map<String, String> sourceNames = applicationPreferences.getSourceNames();
		boolean showingPrimaryIdentifier = applicationPreferences.isShowingPrimaryIdentifier();
		EventSourceComparator<LoggingEvent> loggingComparator = new EventSourceComparator<>(sourceNames, showingPrimaryIdentifier);
		SortedMap<EventSource<LoggingEvent>, ViewContainer<LoggingEvent>> sortedLoggingViews;
		sortedLoggingViews = new TreeMap<>(loggingComparator);
		if(loggingEventViewManager != null)
		{
			sortedLoggingViews.putAll(loggingEventViewManager.getViews());
		}
		return sortedLoggingViews;
	}

	SortedMap<EventSource<AccessEvent>, ViewContainer<AccessEvent>> getSortedAccessViews()
	{
		Map<String, String> sourceNames = applicationPreferences.getSourceNames();
		boolean showingPrimaryIdentifier = applicationPreferences.isShowingPrimaryIdentifier();
		EventSourceComparator<AccessEvent> accessComparator = new EventSourceComparator<>(sourceNames, showingPrimaryIdentifier);
		SortedMap<EventSource<AccessEvent>, ViewContainer<AccessEvent>> sortedAccessViews;
		sortedAccessViews = new TreeMap<>(accessComparator);
		if(accessEventViewManager != null)
		{
			sortedAccessViews.putAll(accessEventViewManager.getViews());
		}
		return sortedAccessViews;
	}

	void closeAllViews(ViewContainer beside)
	{
		loggingEventViewManager.closeAllViews(beside);
		accessEventViewManager.closeAllViews(beside);
	}

	void minimizeAllViews(ViewContainer beside)
	{
		loggingEventViewManager.minimizeAllViews(beside);
		accessEventViewManager.minimizeAllViews(beside);
	}

	void removeInactiveViews(boolean onlyClosed)
	{
		loggingEventViewManager.removeInactiveViews(onlyClosed);
		accessEventViewManager.removeInactiveViews(onlyClosed);
	}

	public void toggleVisible()
	{
		setFramesVisible(!isVisible());
	}

	private void setFramesVisible(boolean visible)
	{
		setVisible(visible);

		processViewContainers(new VisibleContainerProcessor(visible));
	}

	void openInactiveLogs()
	{
		if(logger.isInfoEnabled()) logger.info("Open inactive log...");
		Windows.showWindow(openInactiveLogsDialog, this, true);

	}

	void showDebugDialog()
	{
		Windows.showWindow(debugDialog, this, true);
	}

	public void showPreferencesDialog()
	{
		Windows.showWindow(preferencesDialog, this, true);
	}

	void showHelp()
	{
		openHelp("index.xhtml");
	}

	void showAboutDialog()
	{
		Windows.showWindow(aboutDialog, this, true);
	}

	void cleanAllInactiveLogs()
	{
		loggingEventViewManager.removeInactiveViews(false);
		accessEventViewManager.removeInactiveViews(false);
		longTaskManager.startTask(new CleanAllInactiveCallable(this), "Clean all inactive…");
		updateWindowMenus();
	}

	class LoggingEventSourceListener
		implements EventSourceListener<LoggingEvent>
	{
		@Override
		public void eventSourceAdded(EventSource<LoggingEvent> eventSource)
		{
			EventQueue.invokeLater(new LoggingSourceAddedRunnable(eventSource));
		}

		@Override
		public void eventSourceRemoved(EventSource<LoggingEvent> eventSource)
		{
			EventQueue.invokeLater(new LoggingSourceRemovedRunnable(eventSource));
		}

		private class LoggingSourceAddedRunnable
			implements Runnable
		{
			EventSource<LoggingEvent> eventSource;

			LoggingSourceAddedRunnable(EventSource<LoggingEvent> eventSource)
			{
				this.eventSource = eventSource;
			}

			@Override
			public void run()
			{
				ViewContainer<LoggingEvent> container = retrieveLoggingViewContainer(eventSource);
				EventWrapperViewPanel<LoggingEvent> panel = container.getDefaultView();
				panel.setState(LoggingViewState.ACTIVE);
				if(!applicationPreferences.isMute() && sounds != null)
				{
					sounds.play(LilithSounds.SOURCE_ADDED);
				}
				String primary = eventSource.getSourceIdentifier().getIdentifier();
				Map<String, String> sourceNames = applicationPreferences.getSourceNames();

				if(!sourceNames.containsKey(primary))
				{
					sourceNames = new HashMap<>(sourceNames);
					sourceNames.put(primary, primary);
					applicationPreferences.setSourceNames(sourceNames);
				}

				if(applicationPreferences.isAutoOpening())
				{
					showLoggingView(eventSource);
				}
				else
				{
					updateWindowMenus();
				}
			}
		}

		private class LoggingSourceRemovedRunnable
			implements Runnable
		{
			EventSource<LoggingEvent> eventSource;

			LoggingSourceRemovedRunnable(EventSource<LoggingEvent> eventSource)
			{
				this.eventSource = eventSource;
			}

			@Override
			public void run()
			{
				ViewContainer<LoggingEvent> container = retrieveLoggingViewContainer(eventSource);
				EventWrapperViewPanel<LoggingEvent> panel = container.getDefaultView();
				panel.setState(LoggingViewState.INACTIVE);
				if(!applicationPreferences.isMute() && sounds != null)
				{
					sounds.play(LilithSounds.SOURCE_REMOVED);
				}
				if(applicationPreferences.isAutoClosing())
				{
					loggingEventViewManager.closeViewContainer(container);
				}
				loggingEventSourceManager.removeEventProducer(eventSource.getSourceIdentifier());
				updateWindowMenus();
			}
		}
	}

	class AccessEventSourceListener
		implements EventSourceListener<AccessEvent>
	{
		@Override
		public void eventSourceAdded(EventSource<AccessEvent> eventSource)
		{
			EventQueue.invokeLater(new AccessSourceAddedRunnable(eventSource));
		}

		@Override
		public void eventSourceRemoved(EventSource<AccessEvent> eventSource)
		{
			EventQueue.invokeLater(new AccessSourceRemovedRunnable(eventSource));
		}

		private class AccessSourceAddedRunnable
			implements Runnable
		{
			EventSource<AccessEvent> eventSource;

			AccessSourceAddedRunnable(EventSource<AccessEvent> eventSource)
			{
				this.eventSource = eventSource;
			}

			@Override
			public void run()
			{
				ViewContainer<AccessEvent> container = retrieveAccessViewContainer(eventSource);
				EventWrapperViewPanel<AccessEvent> panel = container.getDefaultView();
				panel.setState(LoggingViewState.ACTIVE);
				if(!applicationPreferences.isMute() && sounds != null)
				{
					sounds.play(LilithSounds.SOURCE_ADDED);
				}

				String primary = eventSource.getSourceIdentifier().getIdentifier();
				Map<String, String> sourceNames = applicationPreferences.getSourceNames();

				if(!sourceNames.containsKey(primary))
				{
					sourceNames = new HashMap<>(sourceNames);
					sourceNames.put(primary, primary);
					applicationPreferences.setSourceNames(sourceNames);
				}

				if(applicationPreferences.isAutoOpening())
				{
					showAccessView(eventSource);
				}
				else
				{
					updateWindowMenus();
				}
			}
		}

		private class AccessSourceRemovedRunnable
			implements Runnable
		{
			EventSource<AccessEvent> eventSource;

			AccessSourceRemovedRunnable(EventSource<AccessEvent> eventSource)
			{
				this.eventSource = eventSource;
			}

			@Override
			public void run()
			{
				ViewContainer<AccessEvent> container = retrieveAccessViewContainer(eventSource);
				EventWrapperViewPanel<AccessEvent> panel = container.getDefaultView();
				panel.setState(LoggingViewState.INACTIVE);
				if(!applicationPreferences.isMute() && sounds != null)
				{
					sounds.play(LilithSounds.SOURCE_REMOVED);
				}
				if(applicationPreferences.isAutoClosing())
				{
					accessEventViewManager.closeViewContainer(container);
				}
				accessEventSourceManager.removeEventProducer(eventSource.getSourceIdentifier());
				updateWindowMenus();
			}
		}
	}

	static void openUrl(URL url)
	{
		final Logger logger = LoggerFactory.getLogger(MainFrame.class);

		if(logger.isInfoEnabled()) logger.info("Opening URL {}. ", url);
		if(url == null)
		{
			return;
		}
		String[] cmdArray = resolveOpenCommandArray(url.toString());
		if(cmdArray == null)
		{
			if(logger.isInfoEnabled()) logger.info("Can't open URL {} because no open command is defined for the current system.", url);
			return;
		}
		executeCommand(cmdArray);
	}

	private static void openUri(URI uri)
	{
		final Logger logger = LoggerFactory.getLogger(MainFrame.class);

		if(logger.isInfoEnabled()) logger.info("Opening URI {}. ", uri);
		if(uri == null)
		{
			return;
		}
		String[] cmdArray = resolveOpenCommandArray(uri.toString());
		if(cmdArray == null)
		{
			if(logger.isInfoEnabled()) logger.info("Can't open URI {} because no open command is defined for the current system.", uri);
			return;
		}
		executeCommand(cmdArray);
	}

	private static void executeCommand(String[] cmdArray)
	{
		if(cmdArray == null)
		{
			return;
		}
		Runtime runtime = Runtime.getRuntime();
		String commandString = Arrays.asList(cmdArray).toString();
		try
		{
			Process process = runtime.exec(cmdArray);
			ProcessConsumerRunnable consumer = new ProcessConsumerRunnable(process);
			Thread t = new Thread(consumer, "Consuming command: " + commandString);
			t.setDaemon(true);
			t.start();
		}
		catch(IOException e)
		{
			final Logger logger = LoggerFactory.getLogger(MainFrame.class);
			if(logger.isWarnEnabled()) logger.warn("Exception while trying to execute command {}!", commandString, e);
		}
	}

	private static String[] resolveOpenCommandArray(String value)
	{
		if(value == null)
		{
			return null;
		}
		String[] result = null;
		if(IS_WINDOWS)
		{
			result = new String[WINDOWS_OPEN_URL_ARRAY.length];
			System.arraycopy(WINDOWS_OPEN_URL_ARRAY, 0, result, 0, WINDOWS_OPEN_URL_ARRAY.length);
		}
		else if(IS_MAC)
		{
			result = new String[MAC_OPEN_URL_ARRAY.length];
			System.arraycopy(MAC_OPEN_URL_ARRAY, 0, result, 0, MAC_OPEN_URL_ARRAY.length);
		}
		if(result != null)
		{
			for(int i = 0; i < result.length; i++)
			{
				if(result[i] == null)
				{
					result[i] = value;
				}
			}
		}
		return result;
	}

	void showFrame(ViewContainer container)
	{
		ViewContainerFrame frame = new ViewContainerFrame(this, container);
		frame.setShowingToolbar(applicationPreferences.isShowingToolbar());
		frame.setShowingStatusBar(applicationPreferences.isShowingStatusBar());
		frame.setSize(800, 600);

		sourceTitleContainerProcessor.updateSourceNameSettings();
		sourceTitleContainerProcessor.process(container);

		Windows.showWindow(frame, null, false);
		executeScrollToBottom(frame);
	}

	void showInternalFrame(ViewContainer container)
	{
		ViewContainerInternalFrame frame = new ViewContainerInternalFrame(this, container);
		frame.setShowingStatusBar(applicationPreferences.isShowingStatusBar());

		int count = desktop.getComponentCount();
		final int titleBarHeight = resolveInternalTitleBarHeight(/*frame*/);
		frame.setBounds(titleBarHeight * (count % 10), titleBarHeight * (count % 10), 640, 480);
		// set bounds in any case
		desktop.add(frame);

		boolean maximize = applicationPreferences.isMaximizingInternalFrames();
		if(maximize)
		{
			try
			{
				// must call after adding to the desktop
				frame.setMaximum(true);
			}
			catch(PropertyVetoException ex)
			{
				if(logger.isErrorEnabled()) logger.error("Vetoed maximizing!", ex);
			}
		}

		viewActions.setViewContainer(container);

		sourceTitleContainerProcessor.updateSourceNameSettings();
		sourceTitleContainerProcessor.process(container);

		frame.setVisible(true);
		executeScrollToBottom(frame);
	}

	void showTaskManager()
	{
		// don't add twice
		if(taskManagerFrame.isClosed())
		{
			desktop.add(taskManagerFrame);
			desktop.validate();
		}
		if(taskManagerFrame.isIcon())
		{
			try
			{
				taskManagerFrame.setIcon(false);
			}
			catch(PropertyVetoException e)
			{
				// ignore
			}
		}
		if(!taskManagerFrame.isVisible())
		{
			taskManagerFrame.setVisible(true);
		}
		taskManagerFrame.moveToFront();
		try
		{
			taskManagerFrame.setSelected(true);
		}
		catch(PropertyVetoException e)
		{
			// ignore
		}
	}

	/**
	 * Initial scroll to bottom must be executed slightly after making it visible so
	 * it's using invokeLater, now.
	 *
	 * @param window the window that should scroll to bottom is configured that way.
	 */
	private void executeScrollToBottom(ViewWindow window)
	{
		if(window != null)
		{
			ScrollToBottomRunnable runnable = new ScrollToBottomRunnable(window);
			EventQueue.invokeLater(runnable);
		}
	}

	private static class ScrollToBottomRunnable
		implements Runnable
	{
		private final ViewWindow window;

		ScrollToBottomRunnable(ViewWindow window)
		{
			this.window = window;
		}

		@Override
		public void run()
		{
			ViewContainer viewContainer = window.getViewContainer();
			if(viewContainer != null)
			{
				viewContainer.scrollToEvent();
			}
		}
	}

	/**
	 * This is only a heuristic and probably won't be correct for non-metal l&f...
	 *
	 * @return the height of the internal frames title bar...
	 */
	private int resolveInternalTitleBarHeight(/*JInternalFrame frame*/)
	{
		int result = 24;
		/*
		InternalFrameUI ui = frame.getUI();
		if(ui instanceof BasicInternalFrameUI)
		{
			BasicInternalFrameUI bui=(BasicInternalFrameUI) ui;
			result=bui.getNorthPane().getPreferredSize().height;
			if(logger.isDebugEnabled()) logger.debug("Resolved height of title bar: {}", result);
		}
        */
		if(logger.isDebugEnabled()) logger.debug("Height of title bar: {}", result);
		return result;
	}

	private void showApplicationPathChangedDialog()
	{
		if(logger.isInfoEnabled()) logger.info("showApplicationPathChangedDialog()");
		final Object[] options = {"Exit", "Cancel"};
		int result = JOptionPane.showOptionDialog(preferencesDialog,
			"You have changed the application path.\n" +
				"You need to restart for this change to take effect.\n\n" +
				"Exit now?",
			"Exit now?",
			JOptionPane.DEFAULT_OPTION,
			JOptionPane.WARNING_MESSAGE,
			Icons.DIALOG_WARNING_ICON,
			options,
			options[0]);
		if(result == 0)
		{
			exit();
		}
	}

	private void showLookAndFeelChangedDialog()
	{
		if(logger.isInfoEnabled()) logger.info("showLookAndFeelChangedDialog()");
		final Object[] options = {"Exit", "Cancel"};
		int result = JOptionPane.showOptionDialog(preferencesDialog,
			"You have changed the look & feel.\n" +
				"You need to restart for this change to take effect.\n\n" +
				"Exit now?",
			"Exit now?",
			JOptionPane.DEFAULT_OPTION,
			JOptionPane.WARNING_MESSAGE,
			Icons.DIALOG_WARNING_ICON,
			options,
			options[0]);
		if(result == 0)
		{
			exit();
		}
	}

	@SuppressWarnings("PMD.DoNotTerminateVM")
	public void exit()
	{
		if(applicationPreferences.isAskingBeforeQuit())
		{
			// yes, I hate apps that ask this question...
			String dialogTitle = "Exit now?";
			String message = "Are you really 100% sure that you want to quit?\nPlease do yourself a favour and think about it before you answer...\nExit now?";
			int result = JOptionPane.showConfirmDialog(this, message, dialogTitle,
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
			if(JOptionPane.OK_OPTION != result)
			{
				return;
			}
		}
		if(logger.isInfoEnabled()) logger.info("Exiting...");
		if(applicationPreferences.isCleaningLogsOnExit())
		{
			deleteInactiveLogs();
		}
		applicationPreferences.setPreviousImportPath(importFileChooser.getCurrentDirectory());
		applicationPreferences.setPreviousExportPath(exportFileChooser.getCurrentDirectory());
		applicationPreferences.setPreviousOpenPath(openFileChooser.getCurrentDirectory());
		applicationPreferences.flush();
		longTaskManager.shutDown();
		System.exit(0);
	}

	class ShowViewRunnable
		implements Runnable
	{
		private final ViewContainer<?> container;

		ShowViewRunnable(ViewContainer<?> container)
		{
			this.container = container;
		}

		@Override
		public void run()
		{
			boolean isNew = false;
			if(container.getParent() == null)
			{
				isNew = true;
				if(!applicationPreferences.isUsingInternalFrames())
				{
					showFrame(container);
				}
				else
				{
					showInternalFrame(container);
				}
			}
			updateWindowMenus();
			ViewWindow window = container.resolveViewWindow();

			if(!isNew || applicationPreferences.isAutoFocusingWindow())
			{
				// reselected existing views should *always* be focused!
				window.focusWindow();
			}
		}
	}


	private class PreferencesChangeListener
		implements PropertyChangeListener
	{
		@Override
		@SuppressWarnings({"unchecked"})
		public void propertyChange(PropertyChangeEvent evt)
		{
			String propName = evt.getPropertyName();

			if(ApplicationPreferences.SOUND_LOCATIONS_PROPERTY.equals(propName))
			{
				if(sounds != null)
				{
					sounds.setSoundLocations((Map<String, String>) evt.getNewValue());
				}
				return;
			}

			if(ApplicationPreferences.SOURCE_NAMES_PROPERTY.equals(propName)
				|| ApplicationPreferences.SHOWING_PRIMARY_IDENTIFIER_PROPERTY.equals(propName)
				|| ApplicationPreferences.SHOWING_SECONDARY_IDENTIFIER_PROPERTY.equals(propName))
			{
				updateSourceTitles();
				return;
			}

			if(ApplicationPreferences.SOURCE_FILTERING_PROPERTY.equals(propName))
			{
				updateStatus();
				return;
			}

			if(ApplicationPreferences.BLACK_LIST_NAME_PROPERTY.equals(propName))
			{
				updateStatus();
				return;
			}

			if(ApplicationPreferences.WHITE_LIST_NAME_PROPERTY.equals(propName))
			{
				updateStatus();
				return;
			}

			if(ApplicationPreferences.MUTE_PROPERTY.equals(propName))
			{
				if(sounds != null)
				{
					sounds.setMute((Boolean) evt.getNewValue());
				}
				return;
			}

			if(ApplicationPreferences.APPLICATION_PATH_PROPERTY.equals(propName))
			{
				File newPath = (File) evt.getNewValue();
				File oldPath = applicationPreferences.getStartupApplicationPath();
				if(oldPath != null)
				{
					File previousApplicationPathFile = new File(newPath, ApplicationPreferences.PREVIOUS_APPLICATION_PATH_FILENAME);

					try(OutputStreamWriter writer = new OutputStreamWriter(Files.newOutputStream(previousApplicationPathFile.toPath()), StandardCharsets.UTF_8))
					{
						writer.append(oldPath.getAbsolutePath());
					}
					catch(IOException ex)
					{
						if(logger.isWarnEnabled()) logger.warn("Exception while writing previous application path to file '{}'!", previousApplicationPathFile.getAbsolutePath(), ex);
					}
				}
				showApplicationPathChangedDialog();
				return;
			}

			if(ApplicationPreferences.LOOK_AND_FEEL_PROPERTY.equals(propName))
			{
				showLookAndFeelChangedDialog();
				return;
			}

			if(ApplicationPreferences.CONDITIONS_PROPERTY.equals(propName))
			{
				updateConditions();
				return;
			}

			if(ApplicationPreferences.PREVIOUS_SEARCH_STRINGS_PROPERTY.equals(propName))
			{
				updatePreviousSearchStrings();
				return;
			}

			if(ApplicationPreferences.RECENT_FILES_PROPERTY.equals(propName))
			{
				updateRecentFiles();
				return;
			}

			if(ApplicationPreferences.SHOWING_FULL_RECENT_PATH_PROPERTY.equals(propName))
			{
				updateRecentFiles();
				return;
			}

			if(ApplicationPreferences.LEVEL_COLORS_PROPERTY.equals(propName))
			{
				levelColors = null;
				processViewContainers(UPDATE_VIEWS_CONTAINER_PROCESSOR);
				return;
			}

			if(ApplicationPreferences.STATUS_COLORS_PROPERTY.equals(propName))
			{
				statusColors = null;
				processViewContainers(UPDATE_VIEWS_CONTAINER_PROCESSOR);
				return;
			}

			if(ApplicationPreferences.SHOWING_FULL_CALL_STACK_PROPERTY.equals(propName))
			{
				processViewContainers(UPDATE_VIEWS_CONTAINER_PROCESSOR);
				return;
			}

			if(ApplicationPreferences.USING_WRAPPED_EXCEPTION_STYLE_PROPERTY.equals(propName))
			{
				processViewContainers(UPDATE_VIEWS_CONTAINER_PROCESSOR);
				return;
			}

			if(ApplicationPreferences.SHOWING_STACKTRACE_PROPERTY.equals(propName))
			{
				processViewContainers(UPDATE_VIEWS_CONTAINER_PROCESSOR);
				return;
			}

			if(ApplicationPreferences.SCALE_FACTOR_PROPERTY.equals(propName))
			{
				updateViewScale(applicationPreferences.getScaleFactor());
				return;
			}

			if(ApplicationPreferences.SHOWING_TOOLBAR_PROPERTY.equals(propName))
			{
				setShowingToolbar(applicationPreferences.isShowingToolbar());
				return;
			}

			if(ApplicationPreferences.SHOWING_STATUS_BAR_PROPERTY.equals(propName))
			{
				setShowingStatusBar(applicationPreferences.isShowingStatusBar());
				return;
			}

			if(ApplicationPreferences.SHOWING_TIP_OF_THE_DAY_PROPERTY.equals(propName))
			{
				setShowingTipOfTheDay(applicationPreferences.isShowingTipOfTheDay());
				return;
			}

			if(ApplicationPreferences.CHECKING_FOR_UPDATE_PROPERTY.equals(propName))
			{
				setCheckingForUpdate(applicationPreferences.isCheckingForUpdate());
				return;
			}

			if(ApplicationPreferences.CHECKING_FOR_SNAPSHOT_PROPERTY.equals(propName))
			{
				setCheckingForSnapshot(applicationPreferences.isCheckingForSnapshot());
				return;
			}

			if(ApplicationPreferences.GLOBAL_LOGGING_ENABLED_PROPERTY.equals(propName))
			{
				setGlobalLoggingEnabled(applicationPreferences.isGlobalLoggingEnabled());
				return;
			}

			if(ApplicationPreferences.TRAY_ACTIVE_PROPERTY.equals(propName))
			{
				if(traySupport != null)
				{
					traySupport.setActive(applicationPreferences.isTrayActive());
				}
				return;
			}

			if(ApplicationPreferences.SCROLLING_SMOOTHLY_PROPERTY.equals(propName))
			{
				updateScrollingSmoothly();
				return;
			}

			if(ApplicationPreferences.COLORING_WHOLE_ROW_PROPERTY.equals(propName))
			{
				coloringWholeRow = applicationPreferences.isColoringWholeRow();
				processViewContainers(UPDATE_VIEWS_CONTAINER_PROCESSOR);
				//return;
			}
		}


		private void updateSourceTitles()
		{
			updateWindowMenus();
			sourceTitleContainerProcessor.updateSourceNameSettings();
			processViewContainers(sourceTitleContainerProcessor);
		}

		private void updateScrollingSmoothly()
		{
			scrollingSmoothlyContainerProcessor.updateScrollingSmoothly();
			processViewContainers(scrollingSmoothlyContainerProcessor);
		}
	}

	private void setGlobalLoggingEnabled(boolean globalLoggingEnabled)
	{
		setGlobalLoggingEnabled(loggingFileDump, loggingEventViewManager, globalLoggingEnabled);
		setGlobalLoggingEnabled(accessFileDump, accessEventViewManager, globalLoggingEnabled);

		viewActions.updateWindowMenu();
	}

	private static <T extends Serializable> void setGlobalLoggingEnabled(FileDumpEventHandler<T> fileDumpEventHandler, ViewManager<T> viewManager, boolean globalLoggingEnabled)
	{
		fileDumpEventHandler.setEnabled(globalLoggingEnabled);

		ViewContainer<T> viewContainer = resolveGlobalView(viewManager);
		if(viewContainer != null)
		{
			ViewWindow viewWindow = viewContainer.resolveViewWindow();
			if (viewWindow instanceof ViewContainerFrame)
			{
				viewWindow.getViewActions().updateWindowMenu();
			}
		}

		if(!globalLoggingEnabled)
		{
			// close view
			viewManager.closeViewContainer(viewContainer);
			// delete data file
			Reset.reset(fileDumpEventHandler.getBuffer());
		}
	}

	private static <T extends Serializable> ViewContainer<T> resolveGlobalView(ViewManager<T> viewManager)
	{
		Map<EventSource<T>, ViewContainer<T>> views = viewManager.getViews();
		for (Map.Entry<EventSource<T>, ViewContainer<T>> entry : views.entrySet())
		{
			EventSource<T> key = entry.getKey();
			if (key.isGlobal())
			{
				return entry.getValue();
			}
		}
		return null;
	}

	private void setCheckingForUpdate(boolean checkingForUpdate)
	{
		preferencesDialog.setCheckingForUpdate(checkingForUpdate);
		checkForUpdateDialog.setCheckingForUpdate(checkingForUpdate);
	}

	private void setCheckingForSnapshot(boolean checkingForSnapshot)
	{
		preferencesDialog.setCheckingForSnapshot(checkingForSnapshot);
	}

	private void setShowingStatusBar(boolean showingStatusBar)
	{
		statusBar.setVisible(showingStatusBar);
		// change for all other open windows
		{
			SortedMap<EventSource<LoggingEvent>, ViewContainer<LoggingEvent>> views = getSortedLoggingViews();
			for(Map.Entry<EventSource<LoggingEvent>, ViewContainer<LoggingEvent>> current : views.entrySet())
			{
				setShowingStatusBar(current.getValue(), showingStatusBar);
			}
		}
		{
			SortedMap<EventSource<AccessEvent>, ViewContainer<AccessEvent>> views = getSortedAccessViews();
			for(Map.Entry<EventSource<AccessEvent>, ViewContainer<AccessEvent>> current : views.entrySet())
			{
				setShowingStatusBar(current.getValue(), showingStatusBar);
			}
		}
	}

	private void setShowingTipOfTheDay(boolean showingTipOfTheDay)
	{
		preferencesDialog.setShowingTipOfTheDay(showingTipOfTheDay);
		tipOfTheDayDialog.setShowingTipOfTheDay(showingTipOfTheDay);
	}

	private void setShowingToolbar(boolean showingToolbar)
	{
		toolbar.setVisible(showingToolbar);

		// change for all other open windows
		{
			SortedMap<EventSource<LoggingEvent>, ViewContainer<LoggingEvent>> views = getSortedLoggingViews();
			for(Map.Entry<EventSource<LoggingEvent>, ViewContainer<LoggingEvent>> current : views.entrySet())
			{
				setShowingToolbar(current.getValue(), showingToolbar);
			}
		}
		{
			SortedMap<EventSource<AccessEvent>, ViewContainer<AccessEvent>> views = getSortedAccessViews();
			for(Map.Entry<EventSource<AccessEvent>, ViewContainer<AccessEvent>> current : views.entrySet())
			{
				setShowingToolbar(current.getValue(), showingToolbar);
			}
		}
	}

	private static void setShowingToolbar(ViewContainer container, boolean showingToolbar)
	{
		ViewWindow viewWindow = container.resolveViewWindow();
		if(viewWindow instanceof ViewContainerFrame)
		{
			ViewContainerFrame viewContainerFrame = (ViewContainerFrame) viewWindow;
			viewContainerFrame.setShowingToolbar(showingToolbar);
		}
	}

	private static void setShowingStatusBar(ViewContainer container, boolean showingStatusBar)
	{
		ViewWindow viewWindow = container.resolveViewWindow();
		if(viewWindow != null)
		{
			viewWindow.setShowingStatusBar(showingStatusBar);
		}
	}

	private void updatePreviousSearchStrings()
	{
		List<String> previousSearchStrings = applicationPreferences.getPreviousSearchStrings();

		processViewContainers(new PreviousSearchStringsContainerProcessor(previousSearchStrings));
	}

	private void updateRecentFiles()
	{
		processViewActions(UPDATE_RECENT_FILES_ACTIONS_PROCESSOR);
	}

	Condition getFindActiveCondition()
	{
		return findActiveCondition;
	}

	private void updateConditions()
	{
		List<SavedCondition> conditions = applicationPreferences.getConditions();
		List<SavedCondition> active = new ArrayList<>();
		if(conditions != null)
		{
			active.addAll(conditions.stream().filter(SavedCondition::isActive).collect(Collectors.toList()));
		}
		activeConditions = active;
		int activeCount = active.size();
		if(activeCount>0)
		{
			if(activeCount == 1)
			{
				findActiveCondition = active.get(0).getCondition();
			}
			else
			{
				Or or=new Or();

				List<Condition> cond=new ArrayList<>(activeCount);
				cond.addAll(active.stream().map(SavedCondition::getCondition).collect(Collectors.toList()));
				or.setConditions(cond);
				findActiveCondition=or;
			}
		}
		//flushCachedConditionResults();

		processViewContainers(UPDATE_VIEWS_CONTAINER_PROCESSOR);

		List<String> conditionNames = applicationPreferences.getConditionNames();

		processViewContainers(new ConditionNamesContainerProcessor(conditionNames));
		processViewActions(new ConditionNamesActionsProcessor(conditionNames));
	}

	private void processViewContainers(ViewContainerProcessor processor)
	{
		Map<EventSource<LoggingEvent>, ViewContainer<LoggingEvent>> loggingViews = loggingEventViewManager.getViews();
		for(Map.Entry<EventSource<LoggingEvent>, ViewContainer<LoggingEvent>> current : loggingViews.entrySet())
		{
			processor.process(current.getValue());
		}
		Map<EventSource<AccessEvent>, ViewContainer<AccessEvent>> accessViews = accessEventViewManager.getViews();
		for(Map.Entry<EventSource<AccessEvent>, ViewContainer<AccessEvent>> current : accessViews.entrySet())
		{
			processor.process(current.getValue());
		}
	}

	private void processViewActions(ViewActionsProcessor processor)
	{
		processor.process(viewActions);
		// process other frames
		Map<EventSource<LoggingEvent>, ViewContainer<LoggingEvent>> loggingViews = loggingEventViewManager.getViews();
		for(Map.Entry<EventSource<LoggingEvent>, ViewContainer<LoggingEvent>> current : loggingViews.entrySet())
		{
			ViewContainer<LoggingEvent> value = current.getValue();
			ViewWindow window = value.resolveViewWindow();
			if(window instanceof JFrame)
			{
				processor.process(window.getViewActions());
			}
		}
		Map<EventSource<AccessEvent>, ViewContainer<AccessEvent>> accessViews = accessEventViewManager.getViews();
		for(Map.Entry<EventSource<AccessEvent>, ViewContainer<AccessEvent>> current : accessViews.entrySet())
		{
			ViewContainer<AccessEvent> value = current.getValue();
			ViewWindow window = value.resolveViewWindow();
			if(window instanceof JFrame)
			{
				processor.process(window.getViewActions());
			}
		}
	}

	private class SourceTitleContainerProcessor
			implements ViewContainerProcessor
	{
		private Map<String, String> sourceNames = null;
		private boolean showingPrimaryIdentifier = false;
		private boolean showingSecondaryIdentifier = false;

		void updateSourceNameSettings()
		{
			sourceNames = applicationPreferences.getSourceNames();
			showingPrimaryIdentifier = applicationPreferences.isShowingPrimaryIdentifier();
			showingSecondaryIdentifier = applicationPreferences.isShowingSecondaryIdentifier();
		}

		@Override
		public void process(ViewContainer<?> container)
		{
			ViewWindow window = container.resolveViewWindow();
			if(window != null)
			{
				String title = ViewActions.resolveSourceTitle(container, sourceNames, showingPrimaryIdentifier, showingSecondaryIdentifier);
				window.setTitle(title);
			}
		}
	}

	private class ScrollingSmoothlyContainerProcessor
			implements ViewContainerProcessor
	{
		private boolean scrollingSmoothly;

		void updateScrollingSmoothly()
		{
			scrollingSmoothly = applicationPreferences.isScrollingSmoothly();
		}

		@Override
		public void process(ViewContainer<?> container)
		{
			container.setScrollingSmoothly(scrollingSmoothly);
		}
	}

	private void updateViewScale(double scale)
	{
		processViewContainers(new UpdateScaleContainerProcessor(scale));
	}

	/*
	 private void flushCachedConditionResults()
	 {
		 colorsCache.clear();
	 }
 */
	private void cleanObsoleteFiles()
	{
		File obsoleteDir = new File(startupApplicationPath, "sources");
		if(obsoleteDir.isDirectory())
		{
			longTaskManager
				.startTask(new CleanObsoleteCallable(obsoleteDir), "Clean obsolete files", "Deletes the directory '" + obsoleteDir
					.getAbsolutePath() + "' recursively.");
		}
	}

	private void deleteInactiveLogs()
	{
		deleteInactiveLogs(loggingFileFactory);
		deleteInactiveLogs(accessFileFactory);
	}

	private void deleteInactiveLogs(LogFileFactory fileFactory)
	{
		List<SourceIdentifier> inactiveLogs = collectInactiveLogs(fileFactory);
		for(SourceIdentifier si : inactiveLogs)
		{
			File dataFile = fileFactory.getDataFile(si);
			File indexFile = fileFactory.getIndexFile(si);
			if(dataFile.delete())
			{
				if(logger.isInfoEnabled()) logger.info("Deleted {}", dataFile); // NOPMD
			}
			if(indexFile.delete())
			{
				if(logger.isInfoEnabled()) logger.info("Deleted {}", indexFile); // NOPMD
			}
		}
	}

	public List<SourceIdentifier> collectInactiveLogs(LogFileFactory fileFactory)
	{
		List<SourceIdentifier> result = new ArrayList<>();
		File logsRoot = fileFactory.getBaseDir();
		File[] sources = logsRoot.listFiles(new DirectoryFilter());
		if(sources != null)
		{
			for(File f : sources)
			{
				collectInactiveLogs(fileFactory, f, result);
			}
			if(logger.isDebugEnabled()) logger.debug("Inactive logs: {}", result);
		}
		return result;
	}

	private void collectInactiveLogs(LogFileFactory fileFactory, final File sourceDir, List<SourceIdentifier> inactiveLogs)
	{
		File[] logs = sourceDir.listFiles(new LogFileFilter(fileFactory));
		if(logs == null)
		{
			return;
		}

		String extension = fileFactory.getDataFileExtension();
		String primary = sourceDir.getName();
		for(File f : logs)
		{
			String abs = f.getAbsolutePath();
			abs = abs.substring(0, abs.length() - extension.length());
			File active = new File(abs + FileConstants.ACTIVE_FILE_EXTENSION); // NOPMD - AvoidInstantiatingObjectsInLoops
			if(!active.isFile())
			{
				String secondary = f.getName();
				secondary = secondary.substring(0, secondary.length() - extension.length());

				inactiveLogs.add(new SourceIdentifier(primary, secondary)); // NOPMD - AvoidInstantiatingObjectsInLoops
			}
		}
	}

	public static void copyText(String text)
	{
		Clipboard systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		Transferable transferableText = new StringSelection(text);
		systemClipboard.setContents(transferableText, null);
	}

	private static class EventSourceComparator<T extends Serializable>
		implements Comparator<EventSource<T>>
	{
		private final Map<String, String> sourceNames;
		private final boolean showingPrimaryIdentifier;

		EventSourceComparator(Map<String, String> sourceNames, boolean showingPrimaryIdentifier)
		{
			this.sourceNames = sourceNames;
			this.showingPrimaryIdentifier = showingPrimaryIdentifier;
		}

		@Override
		public int compare(EventSource<T> o1, EventSource<T> o2)
		{
			if(o1 == o2) // NOPMD
			{
				return 0;
			}
			if(o1 == null)
			{
				return -1;
			}
			if(o2 == null)
			{
				return 1;
			}
			SourceIdentifier si1 = o1.getSourceIdentifier();
			SourceIdentifier si2 = o2.getSourceIdentifier();
			if(si1 == si2) // NOPMD
			{
				return 0;
			}
			// SourceIdentifier of EventSource can't be null.
			String primary1 = ViewActions.getPrimarySourceTitle(si1.getIdentifier(), sourceNames, showingPrimaryIdentifier);
			String primary2 = ViewActions.getPrimarySourceTitle(si2.getIdentifier(), sourceNames, showingPrimaryIdentifier);
			if(primary1 != null && primary2 != null)
			{
				int compare = primary1.compareTo(primary2);
				if(compare != 0)
				{
					return compare;
				}
			}
			return o1.compareTo(o2);
		}
	}

	private class MainWindowListener
		extends WindowAdapter
	{
		@Override
		public void windowClosing(WindowEvent e)
		{
			if(traySupport != null && traySupport.isActive() && applicationPreferences.isHidingOnClose())
			{
				setFramesVisible(false);
			}
			else
			{
				exit();
			}
		}
	}

	private class ShutdownRunnable
		implements Runnable
	{
		@Override
		public void run()
		{
			if(logger.isInfoEnabled()) logger.info("Executing shutdown hook...");
			if(gotoSourceProvider != null)
			{
				gotoSourceProvider.stop();
				gotoSourceProvider = null;
			}
			for(AutostartRunnable current : autostartProcesses)
			{
				current.destroyProcess();
			}
			if(logger.isInfoEnabled()) logger.info("Finished executing shutdown hook...");
		}
	}


	public static class AutostartRunnable
		implements Runnable
	{
		private final Logger logger = LoggerFactory.getLogger(MainFrame.class);

		private final File file;
		private Process process;

		AutostartRunnable(File file)
		{
			this.file = file;
		}

		void destroyProcess()
		{
			if(process != null)
			{
				process.destroy();
			}
		}

		@Override
		public void run()
		{
			try
			{
				if(logger.isInfoEnabled()) logger.info("Starting '{}'.", file.getAbsolutePath());
				process = Runtime.getRuntime().exec(file.getAbsolutePath());

				Thread errThread = new Thread(new ErrorConsumerRunnable(process.getErrorStream()));
				errThread.setDaemon(true);
				errThread.start();

				Thread outThread = new Thread(new OutConsumerRunnable(process.getInputStream()));
				outThread.setDaemon(true);
				outThread.start();

				int exitCode = process.waitFor();
				if(logger.isInfoEnabled())
				{
					logger.info("Execution of '{}' finished with exitCode {}.", file.getAbsolutePath(), exitCode);
				}
			}
			catch(IOException e)
			{
				if(logger.isWarnEnabled()) logger.warn("Exception while executing '" + file.getAbsolutePath() + "'!", e);
			}
			catch(InterruptedException e)
			{
				if(logger.isDebugEnabled()) logger.debug("Execution of '" + file.getAbsolutePath() + "' was interrupted.", e);
			}
		}

		abstract class AbstractOutputConsumerRunnable
			implements Runnable
		{
			private final BufferedReader inputReader;

			AbstractOutputConsumerRunnable(InputStream input)
			{
				inputReader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8));
			}

			@Override
			public void run()
			{
				try
				{

					for(;;)
					{
						String line = inputReader.readLine();
						if(line == null)
						{
							break;
						}
						processLine(line);
					}

				}
				catch(IOException e)
				{
					if(logger.isDebugEnabled())
					{
						logger.debug("Exception while reading from process '" + file.getAbsolutePath() + "'.", e);
					}
				}
			}

			public abstract void processLine(String line);
		}

		private class OutConsumerRunnable
			extends AbstractOutputConsumerRunnable
		{
			OutConsumerRunnable(InputStream input)
			{
				super(input);
			}

			@Override
			public void processLine(String line)
			{
				if(logger.isInfoEnabled()) logger.info("{}: {}", file.getAbsolutePath(), line);
			}
		}

		private class ErrorConsumerRunnable
			extends AbstractOutputConsumerRunnable
		{
			ErrorConsumerRunnable(InputStream input)
			{
				super(input);
			}

			@Override
			@SuppressWarnings("PMD.SystemPrintln")
			public void processLine(String line)
			{
				System.err.println(file.getAbsolutePath() + ": " + line);
			}
		}

	}

	public static class ProcessConsumerRunnable
		implements Runnable
	{
		private final Logger logger = LoggerFactory.getLogger(MainFrame.class);

		private final Process process;

		ProcessConsumerRunnable(Process process)
		{
			this.process = process;
		}

		@Override
		public void run()
		{
			try
			{
				Thread errThread = new Thread(new ErrorConsumerRunnable(process.getErrorStream()));
				errThread.setDaemon(true);
				errThread.start();

				Thread outThread = new Thread(new OutConsumerRunnable(process.getInputStream()));
				outThread.setDaemon(true);
				outThread.start();

				int exitCode = process.waitFor();
				if(logger.isDebugEnabled()) logger.debug("Execution finished with exitCode {}.", exitCode);
			}
			catch(InterruptedException e)
			{
				if(logger.isDebugEnabled()) logger.debug("Execution of openUrl process was interrupted.", e);
			}
		}

		abstract class AbstractOutputConsumerRunnable
			implements Runnable
		{
			private final BufferedReader inputReader;

			AbstractOutputConsumerRunnable(InputStream input)
			{
				inputReader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8));
			}

			@Override
			public void run()
			{
				try
				{

					for(;;)
					{
						String line = inputReader.readLine();
						if(line == null)
						{
							break;
						}
						processLine(line);
					}

				}
				catch(IOException e)
				{
					if(logger.isDebugEnabled()) logger.debug("Exception while reading next line.", e);
				}
			}

			public abstract void processLine(String line);
		}

		private class OutConsumerRunnable
			extends AbstractOutputConsumerRunnable
		{
			OutConsumerRunnable(InputStream input)
			{
				super(input);
			}

			@Override
			public void processLine(String line)
			{
				if(logger.isDebugEnabled()) logger.debug("{}", line);
			}
		}

		private class ErrorConsumerRunnable
			extends AbstractOutputConsumerRunnable
		{
			ErrorConsumerRunnable(InputStream input)
			{
				super(input);
			}

			@Override
			@SuppressWarnings("PMD.SystemPrintln")
			public void processLine(String line)
			{
				System.err.println("Process: " + line);
			}
		}

	}

	private static String readUrl(String url)
	{
		final Logger logger = LoggerFactory.getLogger(MainFrame.class);

		// Create an instance of HttpClient.
		CloseableHttpClient client = HttpClientBuilder.create().build();
		HttpContext localContext = new BasicHttpContext();
		HttpGet httpget = new HttpGet(url);
		try
		{
			HttpResponse response = client.execute(httpget, localContext);
			StatusLine status = response.getStatusLine();
			int statusCode = status.getStatusCode();
			if(statusCode == HttpStatus.NOT_FOUND.getCode())
			{
				if(logger.isInfoEnabled()) logger.info("'{}' not found.", url);
				return null;
			}
			if(status.getStatusCode() != HttpStatus.OK.getCode())
			{
				if(logger.isWarnEnabled()) logger.warn("Status while retrieving '{}': {}", url, status);
				return null;
			}
			HttpEntity entity = response.getEntity();
			String result = EntityUtils.toString(entity);
			EntityUtils.consume(entity);
			return result;
		}
		catch(IOException e)
		{
			if(logger.isWarnEnabled()) logger.warn("Exception while retrieving '{}'!", url, e);
			return null;
		}
		finally
		{
			try
			{
				client.close();
			}
			catch (IOException e)
			{
				if(logger.isWarnEnabled()) logger.warn("Exception while closing down HttpClient!", e);
			}
		}
	}

	private static VersionBundle retrieveVersion(String url)
	{
		return VersionBundle.fromString(readUrl(url));
	}

	private static String retrieveChanges(String currentVersion)
	{
		final String url = "http://lilithapp.com/releases/" + currentVersion + ".xhtml";

		return readUrl(url);
	}

	private class CheckForUpdateRunnable
		implements Runnable
	{
		private static final String RELEASE_VERSION_URL = "http://lilithapp.com/release-version.txt";
		private static final String SNAPSHOT_VERSION_URL = "http://lilithapp.com/snapshot-version.txt";

		private final boolean showAlways;
		private final boolean checkSnapshot;

		CheckForUpdateRunnable(boolean showAlways, boolean checkSnapshot)
		{
			this.showAlways = showAlways;
			this.checkSnapshot = checkSnapshot;
		}

		@Override
		public void run()
		{
			VersionBundle releaseVersionBundle = retrieveVersion(RELEASE_VERSION_URL);
			if(releaseVersionBundle != null)
			{
				int compare = Lilith.APP_VERSION_BUNDLE.compareTo(releaseVersionBundle);
				if (compare < 0)
				{
					String version = releaseVersionBundle.getVersion();
					String message = "New release: " + version;
					String changes = retrieveChanges(version);
					EventQueue.invokeLater(new ShowUpdateDialogRunnable(message, changes));
					return;
				}

				if (!Lilith.APP_SNAPSHOT && compare > 0)
				{
					showNewzestVersion("release", releaseVersionBundle);
					return;
				}
			}

			if(Lilith.APP_SNAPSHOT || checkSnapshot)
			{
				// check for snapshot if either checking is enabled or we are already using a snapshot
				VersionBundle snapshotVersionBundle = retrieveVersion(SNAPSHOT_VERSION_URL);
				if(snapshotVersionBundle != null)
				{
					int compare = Lilith.APP_VERSION_BUNDLE.compareTo(snapshotVersionBundle);
					if (compare < 0)
					{
						String version = snapshotVersionBundle.getVersion();
						long timestamp = snapshotVersionBundle.getTimestamp();
						String timestampString =
							DateTimeFormatters.DATETIME_IN_SYSTEM_ZONE_SPACE.format(Instant.ofEpochMilli(timestamp));
						String message = "New snapshot: " + version + " - " + timestampString;
						String changes = retrieveChanges(version);
						EventQueue.invokeLater(new ShowUpdateDialogRunnable(message, changes));
						return;
					}

					if (compare > 0)
					{
						showNewzestVersion("snapshot", snapshotVersionBundle);
						return;
					}
				}
			}

			if(showAlways)
			{
				String changes = retrieveChanges(Lilith.APP_VERSION_BUNDLE.getVersion());
				EventQueue.invokeLater(new ShowUpdateDialogRunnable(null /* i.e. up to date */, changes));
			}
		}

		private void showNewzestVersion(String versionType, VersionBundle versionBundle)
		{
			String message = "OH HAI! You can haz newzest "+versionType+" version!!1";
			String changes = retrieveChanges(Lilith.APP_VERSION_BUNDLE.getVersion());
			if(changes == null)
			{
				String version = versionBundle.getVersion();

				changes = retrieveChanges(version);
			}
			EventQueue.invokeLater(new ShowUpdateDialogRunnable(message, changes));
		}
	}

	private class ShowUpdateDialogRunnable
		implements Runnable
	{
		private final String message;
		private final String changes;

		ShowUpdateDialogRunnable(String message, String changes)
		{
			this.message = message;
			this.changes = changes;
		}

		@Override
		public void run()
		{
			MainFrame.this.showUpdateDialog(message, changes);
		}
	}

	private void showUpdateDialog(String message, String changes)
	{
		checkForUpdateDialog.setMessage(message);
		checkForUpdateDialog.setChanges(changes);
		if(logger.isDebugEnabled()) logger.debug("Check for update: message='{}', changes='{}'", message, changes);
		Windows.showWindow(checkForUpdateDialog, this, true);
	}

	private class MainTaskListener
		implements TaskListener<Long>
	{
		private final Logger logger = LoggerFactory.getLogger(MainTaskListener.class);

		@Override
		public void taskCreated(Task<Long> longTask)
		{
			if(logger.isDebugEnabled()) logger.debug("Task {} created.", longTask.getName());
			updateTaskStatus();
		}

		@Override
		public void executionFailed(Task<Long> longTask, ExecutionException exception)
		{
			if(logger.isWarnEnabled()) logger.warn("Execution of task {} failed!", longTask.getName(), exception);
			String message="Execution of task "+longTask.getName()+" failed!";
			Throwable cause=exception.getCause();
			if(cause==null)
			{
				cause=exception;
			}
			String causeMsg=cause.getMessage();
			if(causeMsg == null)
			{
				causeMsg=cause.toString();
			}
			message=message+"\n"+causeMsg;
			JOptionPane.showMessageDialog(MainFrame.this, message, "Exception while executing task…", JOptionPane.ERROR_MESSAGE);

			updateTaskStatus();
		}

		@Override
		public void executionFinished(Task<Long> longTask, Long result)
		{
			if(logger.isDebugEnabled()) logger.debug("Execution of task {} finished!", longTask.getName());
			updateTaskStatus();
			Callable<Long> callable = longTask.getCallable();

			if(callable instanceof IndexingCallable)
			{
				IndexingCallable iCallable = (IndexingCallable) callable;
				File dataFile = iCallable.getDataFile();
				File indexFile = iCallable.getIndexFile();
				createViewFor(dataFile, indexFile, true);
				return;
			}
			if(callable instanceof Log4jImportCallable)
			{
				Log4jImportCallable iCallable = (Log4jImportCallable) callable;
				createView(iCallable.getBuffer());
				return;
			}
			if(callable instanceof JulImportCallable)
			{
				JulImportCallable iCallable = (JulImportCallable) callable;
				createView(iCallable.getBuffer());
			}
		}

		private void createView(AppendOperation<EventWrapper<LoggingEvent>> buffer)
		{
			if(buffer instanceof CodecFileBuffer)
			{
				CodecFileBuffer cfb = (CodecFileBuffer) buffer;
				File dataFile = cfb.getDataFile();
				File indexFile = cfb.getIndexFile();
				cfb.dispose();
				createViewFor(dataFile, indexFile, false);
			}
		}

		@Override
		public void executionCanceled(Task<Long> longTask)
		{
			if(logger.isInfoEnabled()) logger.info("Execution of task {} canceled!", longTask.getName());
			Callable<Long> c = longTask.getCallable();
			if(c instanceof ExportCallable)
			{
				if(logger.isInfoEnabled()) logger.info("Done? {}",longTask.getFuture().isDone());
				ExportCallable ec = (ExportCallable) c;
				FileBuffer output = ec.getOutput();
				output.reset();
				File dataFile = output.getDataFile();
				if(dataFile.isFile())
				{
					if(dataFile.delete())
					{
						if(logger.isInfoEnabled()) logger.info("Deleted {}.", dataFile.getAbsolutePath());
						if(dataFile.isFile())
						{
							if(logger.isWarnEnabled()) logger.warn("WTF???? I just deleted {} and now it's still a file?!", dataFile.getAbsolutePath()); // NOPMD
						}
						if(dataFile.exists())
						{
							if(logger.isWarnEnabled()) logger.warn("WTF???? I just deleted {} and now it still exists?!", dataFile.getAbsolutePath()); // NOPMD
						}
					}
					else
					{
						if(logger.isWarnEnabled()) logger.warn("Couldn't delete {}.", dataFile.getAbsolutePath());
					}
				}
				else
				{
					if(logger.isWarnEnabled()) logger.warn("WTF? {}", dataFile.getAbsolutePath());
				}
			}
			updateTaskStatus();
		}

		@Override
		public void progressUpdated(Task<Long> longTask, int progress)
		{
			if(logger.isDebugEnabled()) logger.debug("Progress of task {} updated to {}.", longTask.getName(), progress);
			updateTaskStatus();
		}
	}
}
