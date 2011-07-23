/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2011 Joern Huxhorn
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

import de.huxhorn.lilith.conditions.LoggerStartsWithCondition;
import de.huxhorn.lilith.data.access.AccessEvent;
import de.huxhorn.lilith.data.eventsource.EventWrapper;
import de.huxhorn.lilith.data.eventsource.SourceIdentifier;
import de.huxhorn.lilith.data.logging.ExtendedStackTraceElement;
import de.huxhorn.lilith.data.logging.LoggingEvent;
import de.huxhorn.lilith.data.logging.json.LoggingJsonEncoder;
import de.huxhorn.lilith.data.logging.xml.LoggingXmlEncoder;
import de.huxhorn.lilith.engine.EventSource;
import de.huxhorn.lilith.services.clipboard.AccessUriFormatter;
import de.huxhorn.lilith.services.clipboard.ClipboardFormatter;
import de.huxhorn.lilith.services.clipboard.ClipboardFormatterData;
import de.huxhorn.lilith.services.clipboard.GroovyFormatter;
import de.huxhorn.lilith.services.clipboard.LoggingCallLocationFormatter;
import de.huxhorn.lilith.services.clipboard.LoggingCallStackFormatter;
import de.huxhorn.lilith.services.clipboard.LoggingLoggerNameFormatter;
import de.huxhorn.lilith.services.clipboard.LoggingMarkerFormatter;
import de.huxhorn.lilith.services.clipboard.LoggingMdcFormatter;
import de.huxhorn.lilith.services.clipboard.LoggingMessageFormatter;
import de.huxhorn.lilith.services.clipboard.LoggingMessagePatternFormatter;
import de.huxhorn.lilith.services.clipboard.LoggingNdcFormatter;
import de.huxhorn.lilith.services.clipboard.LoggingThrowableFormatter;
import de.huxhorn.lilith.services.sender.EventSender;
import de.huxhorn.lilith.swing.table.EventWrapperViewTable;
import de.huxhorn.sulky.conditions.Not;
import de.huxhorn.sulky.swing.PersistentTableColumnModel;
import de.huxhorn.sulky.conditions.Condition;
import de.huxhorn.sulky.swing.KeyStrokes;

import org.simplericity.macify.eawt.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.TableColumn;

/**
 * This class needs cleanup...... remove duplicated logic, make ToolBar/Menu configurable...
 */
public class ViewActions
{
	private final Logger logger = LoggerFactory.getLogger(ViewActions.class);

	/**
	 * Taken over from Action.SELECTED_KEY for 1.5 compatibility.
	 * Does not work with 1.5 :( I was really sure that there was some selected event...
	 */
	//private static final String SELECTED_KEY = "SwingSelectedKey";

	private static final Icon EMPTY_16_ICON;

	private static final Icon CLEAR_MENU_ICON;
	private static final Icon ATTACH_MENU_ICON;
	private static final Icon DETACH_MENU_ICON;
	private static final Icon PAUSED_MENU_ICON;
	private static final Icon UNPAUSED_MENU_ICON;
	private static final Icon FIND_MENU_ITEM;
	private static final Icon STATISTICS_MENU_ICON;
	private static final Icon DISCONNECT_MENU_ICON;
	private static final Icon TAIL_MENU_ICON;
	private static final Icon FIND_NEXT_MENU_ICON;
	private static final Icon FIND_PREV_MENU_ICON;
	private static final Icon HELP_MENU_ICON;
	private static final Icon OPEN_INACTIVE_MENU_ICON;
	private static final Icon EXPORT_MENU_ICON;
	private static final Icon EXIT_MENU_ICON;
	private static final Icon PREFERENCES_MENU_ICON;

	private static final Icon CLEAR_TOOLBAR_ICON;
	private static final Icon ATTACH_TOOLBAR_ICON;
	private static final Icon DETACH_TOOLBAR_ICON;
	private static final Icon PAUSED_TOOLBAR_ICON;
	private static final Icon UNPAUSED_TOOLBAR_ICON;
	private static final Icon FIND_TOOLBAR_ICON;
	//private static final Icon STATISTICS_TOOLBAR_ICON;
	private static final Icon DISCONNECT_TOOLBAR_ICON;
	private static final Icon TAIL_TOOLBAR_ICON;

	private static final Icon PREFERENCES_TOOLBAR_ICON;

	private static final Icon CHECK_UPDATE_ICON;
	private static final Icon TOTD_ICON;
	private static final Icon WINDOW_16_ICON;

	// TODO: Resource classes
	// TODO: Separate Toolbar and Menu creation.
	// TODO: toolbar action, menu action => same action
	static
	{
		Icon icon;
		{
			URL url = ViewActions.class.getResource("/otherGraphics/empty16.png");
			if(url != null)
			{
				icon = new ImageIcon(url);
			}
			else
			{
				icon = null;
			}
		}
		EMPTY_16_ICON = icon;

		{
			URL url = ViewActions.class.getResource("/tango/16x16/actions/edit-clear.png");
			if(url != null)
			{
				icon = new ImageIcon(url);
			}
			else
			{
				icon = null;
			}
		}
		CLEAR_MENU_ICON = icon;

		{
			URL url = ViewActions.class.getResource("/tango/32x32/actions/edit-clear.png");
			if(url != null)
			{
				icon = new ImageIcon(url);
			}
			else
			{
				icon = null;
			}
		}
		CLEAR_TOOLBAR_ICON = icon;

		{
			URL url = ViewActions.class.getResource("/tango/16x16/actions/edit-undo.png");
			if(url != null)
			{
				icon = new ImageIcon(url);
			}
			else
			{
				icon = null;
			}
		}
		ATTACH_MENU_ICON = icon;

		{
			URL url = ViewActions.class.getResource("/tango/16x16/actions/edit-redo.png");
			if(url != null)
			{
				icon = new ImageIcon(url);
			}
			else
			{
				icon = null;
			}
		}
		DETACH_MENU_ICON = icon;

		{
			URL url = ViewActions.class.getResource("/tango/32x32/actions/edit-undo.png");
			if(url != null)
			{
				icon = new ImageIcon(url);
			}
			else
			{
				icon = null;
			}
		}
		ATTACH_TOOLBAR_ICON = icon;

		{
			URL url = ViewActions.class.getResource("/tango/32x32/actions/edit-redo.png");
			if(url != null)
			{
				icon = new ImageIcon(url);
			}
			else
			{
				icon = null;
			}
		}
		DETACH_TOOLBAR_ICON = icon;

		{
			URL url = ViewActions.class.getResource("/tango/16x16/actions/media-playback-start.png");
			if(url != null)
			{
				icon = new ImageIcon(url);
			}
			else
			{
				icon = null;
			}
		}
		PAUSED_MENU_ICON = icon;

		{
			URL url = ViewActions.class.getResource("/tango/16x16/actions/media-playback-pause.png");
			if(url != null)
			{
				icon = new ImageIcon(url);
			}
			else
			{
				icon = null;
			}
		}
		UNPAUSED_MENU_ICON = icon;

		{
			URL url = ViewActions.class.getResource("/tango/32x32/actions/media-playback-start.png");
			if(url != null)
			{
				icon = new ImageIcon(url);
			}
			else
			{
				icon = null;
			}
		}
		PAUSED_TOOLBAR_ICON = icon;

		{
			URL url = ViewActions.class.getResource("/tango/32x32/actions/media-playback-pause.png");
			if(url != null)
			{
				icon = new ImageIcon(url);
			}
			else
			{
				icon = null;
			}
		}
		UNPAUSED_TOOLBAR_ICON = icon;

		{
			URL url = ViewActions.class.getResource("/tango/16x16/actions/edit-find.png");
			if(url != null)
			{
				icon = new ImageIcon(url);
			}
			else
			{
				icon = null;
			}
		}
		FIND_MENU_ITEM = icon;

		{
			URL url = ViewActions.class.getResource("/tango/32x32/actions/edit-find.png");
			if(url != null)
			{
				icon = new ImageIcon(url);
			}
			else
			{
				icon = null;
			}
		}
		FIND_TOOLBAR_ICON = icon;

		{
			URL url = ViewActions.class.getResource("/tango/16x16/apps/utilities-system-monitor.png");
			if(url != null)
			{
				icon = new ImageIcon(url);
			}
			else
			{
				icon = null;
			}
		}
		STATISTICS_MENU_ICON = icon;

/*
		{
			URL url = ViewActions.class.getResource("/tango/32x32/apps/utilities-system-monitor.png");
			if(url != null)
			{
				icon = new ImageIcon(url);
			}
			else
			{
				icon = null;
			}
		}
		STATISTICS_TOOLBAR_ICON = icon;
*/

		{
			URL url = ViewActions.class.getResource("/tango/16x16/actions/media-eject.png");
			if(url != null)
			{
				icon = new ImageIcon(url);
			}
			else
			{
				icon = null;
			}
		}
		DISCONNECT_MENU_ICON = icon;

		{
			URL url = ViewActions.class.getResource("/tango/32x32/actions/media-eject.png");
			if(url != null)
			{
				icon = new ImageIcon(url);
			}
			else
			{
				icon = null;
			}
		}
		DISCONNECT_TOOLBAR_ICON = icon;

		{
			URL url = ViewActions.class.getResource("/tango/16x16/actions/go-down.png");
			if(url != null)
			{
				icon = new ImageIcon(url);
			}
			else
			{
				icon = null;
			}
		}
		FIND_NEXT_MENU_ICON = icon;

		{
			URL url = ViewActions.class.getResource("/tango/16x16/actions/go-up.png");
			if(url != null)
			{
				icon = new ImageIcon(url);
			}
			else
			{
				icon = null;
			}
		}
		FIND_PREV_MENU_ICON = icon;

		{
			URL url = ViewActions.class.getResource("/tango/16x16/actions/go-bottom.png");
			if(url != null)
			{
				icon = new ImageIcon(url);
			}
			else
			{
				icon = null;
			}

		}
		TAIL_MENU_ICON = icon;

		{
			URL url = ViewActions.class.getResource("/tango/32x32/actions/go-bottom.png");
			if(url != null)
			{
				icon = new ImageIcon(url);
			}
			else
			{
				icon = null;
			}

		}
		TAIL_TOOLBAR_ICON = icon;

		{
			URL url = ViewActions.class.getResource("/tango/16x16/apps/help-browser.png");

			if(url != null)
			{
				icon = new ImageIcon(url);
			}
			else
			{
				icon = null;
			}
		}
		HELP_MENU_ICON = icon;

		{
			URL url = ViewActions.class.getResource("/tango/16x16/actions/document-open.png");
			if(url != null)
			{
				icon = new ImageIcon(url);
			}
			else
			{
				icon = null;
			}
		}
		OPEN_INACTIVE_MENU_ICON = icon;


		{
			URL url = ViewActions.class.getResource("/tango/16x16/actions/document-save.png");
			if(url != null)
			{
				icon = new ImageIcon(url);
			}
			else
			{
				icon = null;
			}
		}
		EXPORT_MENU_ICON = icon;

		{
			URL url = ViewActions.class.getResource("/tango/16x16/actions/system-log-out.png");
			if(url != null)
			{
				icon = new ImageIcon(url);
			}
			else
			{
				icon = null;
			}
		}
		EXIT_MENU_ICON = icon;

		{
			URL url = ViewActions.class.getResource("/tango/16x16/categories/preferences-system.png");
			if(url != null)
			{
				icon = new ImageIcon(url);
			}
			else
			{
				icon = null;
			}
		}
		PREFERENCES_MENU_ICON = icon;

		{
			URL url = ViewActions.class.getResource("/tango/32x32/categories/preferences-system.png");
			if(url != null)
			{
				icon = new ImageIcon(url);
			}
			else
			{
				icon = null;
			}
		}
		PREFERENCES_TOOLBAR_ICON = icon;

		{
			URL url = ViewActions.class.getResource("/tango/16x16/status/software-update-available.png");
			if(url != null)
			{
				icon = new ImageIcon(url);
			}
			else
			{
				icon = null;
			}
		}
		CHECK_UPDATE_ICON = icon;

		{
			URL url = ViewActions.class.getResource("/tango/16x16/status/dialog-information.png");
			if(url != null)
			{
				icon = new ImageIcon(url);
			}
			else
			{
				icon = null;
			}
		}
		TOTD_ICON = icon;

		{
			URL url = ViewActions.class.getResource("/tango/16x16/mimetypes/text-x-generic.png");
			if(url != null)
			{
				icon = new ImageIcon(url);
			}
			else
			{
				icon = null;
			}
		}
		WINDOW_16_ICON = icon;
	}


	private JToolBar toolbar;
	private JMenuBar menubar;

	private MainFrame mainFrame;
	private ViewContainer viewContainer;
	private JToggleButton scrollToBottomButton;

	private ExportMenuAction exportMenuAction;
	private AttachToolBarAction attachToolBarAction;
	private AttachMenuAction attachMenuAction;
	private DisconnectToolBarAction disconnectToolBarAction;
	private DisconnectMenuAction disconnectMenuAction;
	private PauseToolBarAction pauseToolBarAction;
	private PauseMenuAction pauseMenuAction;
	private FindPreviousAction findPreviousAction;
	private FindNextAction findNextAction;
	private FindPreviousActiveAction findPreviousActiveAction;
	private FindNextActiveAction findNextActiveAction;
	private ResetFindAction resetFindAction;
	private ScrollToBottomMenuAction scrollToBottomMenuAction;
	private EditSourceNameMenuAction editSourceNameMenuAction;
	private SaveLayoutAction saveLayoutAction;
	private ResetLayoutAction resetLayoutAction;
	private EditConditionMenuAction editConditionMenuAction;

	private ZoomInMenuAction zoomInMenuAction;
	private ZoomOutMenuAction zoomOutMenuAction;
	private ResetZoomMenuAction resetZoomMenuAction;

	private NextTabAction nextTabAction;
	private PreviousTabAction previousTabAction;
	private CloseFilterAction closeFilterAction;
	private CloseOtherFiltersAction closeOtherFiltersAction;
	private CloseAllFiltersAction closeAllFiltersAction;

	//private ClearAndRemoveInactiveAction clearAndRemoveInactiveAction;
	private RemoveInactiveAction removeInactiveAction;
	private CloseAllAction closeAllAction;
	private CloseOtherAction closeOtherAction;
	private MinimizeAllAction minimizeAllAction;
	private MinimizeAllOtherAction minimizeAllOtherAction;

	private JMenuItem removeInactiveItem;
	//private JMenuItem clearAndRemoveInactiveItem;

	private JMenu windowMenu;
	private AboutAction aboutAction;
	private PreferencesMenuAction preferencesMenuAction;
	private FindMenuAction findMenuAction;
	private JMenu searchMenu;
	private JMenu viewMenu;
	private JMenu columnsMenu;
	private ClearMenuAction clearMenuAction;
	private FocusMessageAction focusMessageAction;
	private FocusEventsAction focusEventsAction;
	//private StatisticsMenuAction statisticsMenuAction;
	private ChangeListener containerChangeListener;
	private ScrollToBottomToolBarAction scrollToBottomToolBarAction;
	private ClearToolBarAction clearToolBarAction;
	private FindToolBarAction findToolBarAction;
	//private StatisticsToolBarAction statisticsToolBarAction;
	private CopySelectionAction copySelectionAction;
	private CopyToClipboardAction copyEventAction;
	private CopyToClipboardAction copyLoggingJsonEventAction;
	private CopyToClipboardAction copyLoggingXmlEventAction;
	private CopyToClipboardAction copyLoggingMessageAction;
	private CopyToClipboardAction copyLoggingMessagePatternAction;
	private CopyToClipboardAction copyLoggingThrowableAction;
	private CopyToClipboardAction copyLoggingCallStackAction;
	private CopyToClipboardAction copyLoggingCallLocationAction;
	private CopyToClipboardAction copyLoggingMarkerAction;
	private CopyToClipboardAction copyLoggingMdcAction;
	private CopyToClipboardAction copyLoggingNdcAction;
	private CopyToClipboardAction copyLoggerNameAction;
	private CopyToClipboardAction copyAccessUriAction;
	private ShowUnfilteredEventAction showUnfilteredEventAction;
	private JPopupMenu popup;
	private GotoSourceAction gotoSourceAction;
	private JMenu sendToPopupMenu;
	private JMenu focusPopupMenu;
	private JMenu excludePopupMenu;
	private JMenu filterPopupMenu;
	private JMenu copyPopupMenu;
	private PropertyChangeListener containerPropertyChangeListener;
	private EventWrapper eventWrapper;
	private JMenuItem showTaskManagerItem;
	private JMenuItem closeAllItem;
	private JMenuItem minimizeAllItem;
	private JMenuItem closeAllOtherItem;
	private JMenuItem minimizeAllOtherItem;
	private JMenu editMenu;
	private JMenu recentFilesMenu;
	private ClearRecentFilesAction clearRecentFilesAction;
	private JMenu customCopyMenu;
	private JMenu customCopyPopupMenu;
	private HashMap<String, CopyToClipboardAction> groovyClipboardActions;
	private HashMap<String, ClipboardFormatterData> groovyClipboardData;


	public ViewActions(MainFrame mainFrame, ViewContainer viewContainer)
	{
		this(mainFrame);
		setViewContainer(viewContainer);
	}

	public ViewActions(MainFrame mainFrame)
	{
		this.mainFrame = mainFrame;

		containerChangeListener = new ChangeListener()
		{
			/**
			 * Invoked when the target of the listener has changed its state.
			 *
			 * @param e a ChangeEvent object
			 */
			public void stateChanged(ChangeEvent e)
			{
				updateActions();
			}
		};

		containerPropertyChangeListener = new PropertyChangeListener()
		{

			/**
			 * This method gets called when a bound property is changed.
			 *
			 * @param evt A PropertyChangeEvent object describing the event source
			 *            and the property that has changed.
			 */

			public void propertyChange(PropertyChangeEvent evt)
			{
				if(ViewContainer.SELECTED_EVENT_PROPERTY_NAME.equals(evt.getPropertyName()))
				{
					setEventWrapper((EventWrapper) evt.getNewValue());
				}

			}
		};
		// ##### Menu Actions #####
		// File
		OpenMenuAction openMenuAction = new OpenMenuAction();
		clearRecentFilesAction=new ClearRecentFilesAction();
		OpenInactiveLogMenuAction openInactiveLogMenuAction = new OpenInactiveLogMenuAction();
		ImportMenuAction importMenuAction = new ImportMenuAction();
		exportMenuAction = new ExportMenuAction();
		CleanAllInactiveLogsMenuAction cleanAllInactiveLogsMenuAction = new CleanAllInactiveLogsMenuAction();
		preferencesMenuAction = new PreferencesMenuAction();
		ExitMenuAction exitMenuAction = new ExitMenuAction();

		// Edit
		showUnfilteredEventAction = new ShowUnfilteredEventAction();
		gotoSourceAction = new GotoSourceAction();
		copySelectionAction = new CopySelectionAction();
		copyEventAction = new CopyToClipboardAction(new EventFormatter());
		copyLoggingJsonEventAction = new CopyToClipboardAction(new EventJsonFormatter());
		copyLoggingXmlEventAction = new CopyToClipboardAction(new EventXmlFormatter());
		copyLoggingMessageAction = new CopyToClipboardAction(new LoggingMessageFormatter());
		copyLoggingMessagePatternAction = new CopyToClipboardAction(new LoggingMessagePatternFormatter());
		copyLoggerNameAction = new CopyToClipboardAction(new LoggingLoggerNameFormatter());
		copyLoggingThrowableAction = new CopyToClipboardAction(new LoggingThrowableFormatter());
		copyLoggingCallStackAction = new CopyToClipboardAction(new LoggingCallStackFormatter());
		copyLoggingCallLocationAction = new CopyToClipboardAction(new LoggingCallLocationFormatter());
		copyLoggingMarkerAction = new CopyToClipboardAction(new LoggingMarkerFormatter());
		copyLoggingMdcAction = new CopyToClipboardAction(new LoggingMdcFormatter());
		copyLoggingNdcAction = new CopyToClipboardAction(new LoggingNdcFormatter());
		copyAccessUriAction = new CopyToClipboardAction(new AccessUriFormatter());


		// Search
		findMenuAction = new FindMenuAction();
		findPreviousAction = new FindPreviousAction();
		findNextAction = new FindNextAction();
		findPreviousActiveAction = new FindPreviousActiveAction();
		findNextActiveAction = new FindNextActiveAction();
		resetFindAction = new ResetFindAction();

		// View
		scrollToBottomMenuAction = new ScrollToBottomMenuAction();
		pauseMenuAction = new PauseMenuAction();
		clearMenuAction = new ClearMenuAction();
		attachMenuAction = new AttachMenuAction();
		disconnectMenuAction = new DisconnectMenuAction();

		focusMessageAction = new FocusMessageAction();
		focusEventsAction = new FocusEventsAction();

		//statisticsMenuAction = new StatisticsMenuAction();
		editSourceNameMenuAction = new EditSourceNameMenuAction();
		saveLayoutAction = new SaveLayoutAction();
		resetLayoutAction = new ResetLayoutAction();
		editConditionMenuAction = new EditConditionMenuAction();

		zoomInMenuAction = new ZoomInMenuAction();
		zoomOutMenuAction = new ZoomOutMenuAction();
		resetZoomMenuAction = new ResetZoomMenuAction();

		previousTabAction = new PreviousTabAction();
		nextTabAction = new NextTabAction();
		closeFilterAction = new CloseFilterAction();
		closeOtherFiltersAction = new CloseOtherFiltersAction();
		closeAllFiltersAction = new CloseAllFiltersAction();

		// Window
		ShowTaskManagerAction showTaskManagerAction = new ShowTaskManagerAction();
		closeAllAction = new CloseAllAction();
		closeOtherAction = new CloseOtherAction();
		minimizeAllAction = new MinimizeAllAction();
		minimizeAllOtherAction = new MinimizeAllOtherAction();
		removeInactiveAction = new RemoveInactiveAction();
		//clearAndRemoveInactiveAction=new ClearAndRemoveInactiveAction();

		// Help
		KeyboardHelpAction keyboardHelpAction = new KeyboardHelpAction();
		TipOfTheDayAction tipOfTheDayAction = new TipOfTheDayAction();
		DebugAction debugAction = new DebugAction();
		aboutAction = new AboutAction();
		CheckForUpdateAction checkForUpdateAction = new CheckForUpdateAction();
		TroubleshootingAction troubleshootingAction = new TroubleshootingAction();

		// ##### ToolBar Actions #####
		scrollToBottomToolBarAction = new ScrollToBottomToolBarAction();
		pauseToolBarAction = new PauseToolBarAction();
		clearToolBarAction = new ClearToolBarAction();
		findToolBarAction = new FindToolBarAction();
		//statisticsToolBarAction = new StatisticsToolBarAction();
		attachToolBarAction = new AttachToolBarAction();
		PreferencesToolBarAction preferencesToolBarAction = new PreferencesToolBarAction();
		disconnectToolBarAction = new DisconnectToolBarAction();

		showTaskManagerItem = new JMenuItem(showTaskManagerAction);
		closeAllItem = new JMenuItem(closeAllAction);
		closeAllOtherItem = new JMenuItem(closeOtherAction);
		minimizeAllItem = new JMenuItem(minimizeAllAction);
		minimizeAllOtherItem = new JMenuItem(minimizeAllOtherAction);
		removeInactiveItem = new JMenuItem(removeInactiveAction);
		//clearAndRemoveInactiveItem = new JMenuItem(clearAndRemoveInactiveAction);

		toolbar = new JToolBar(SwingConstants.HORIZONTAL);
		toolbar.setFloatable(false);


		scrollToBottomButton = new JToggleButton(scrollToBottomToolBarAction);
		toolbar.add(scrollToBottomButton);

		JButton pauseButton = new JButton(pauseToolBarAction);
		toolbar.add(pauseButton);

		JButton clearButton = new JButton(clearToolBarAction);
		toolbar.add(clearButton);

		JButton findButton = new JButton(findToolBarAction);
		toolbar.add(findButton);

		JButton disconnectButton = new JButton(disconnectToolBarAction);
		toolbar.add(disconnectButton);

		toolbar.addSeparator();

		//JButton statisticsButton = new JButton(statisticsToolBarAction);
		//toolbar.add(statisticsButton);
		//toolbar.addSeparator();

		JButton attachButton = new JButton(attachToolBarAction);
		toolbar.add(attachButton);

		toolbar.addSeparator();

		JButton preferencesButton = new JButton(preferencesToolBarAction);
		toolbar.add(preferencesButton);

		recentFilesMenu=new JMenu("Recent Files");

		Application app = mainFrame.getApplication();

		menubar = new JMenuBar();

		// File
		JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic('f');
		fileMenu.add(new JMenuItem(openMenuAction));
		fileMenu.add(recentFilesMenu);
		fileMenu.add(new JMenuItem(openInactiveLogMenuAction));
		fileMenu.add(new JMenuItem(cleanAllInactiveLogsMenuAction));
		fileMenu.add(new JMenuItem(importMenuAction));
		fileMenu.add(new JMenuItem(exportMenuAction));
		if(!app.isMac())
		{
			fileMenu.addSeparator();
			fileMenu.add(new JMenuItem(preferencesMenuAction));
			fileMenu.addSeparator();
			fileMenu.add(new JMenuItem(exitMenuAction));
		}

		// Edit
		editMenu = new JMenu("Edit");
		editMenu.setMnemonic('e');
		editMenu.add(copySelectionAction);
		editMenu.addSeparator();
		editMenu.add(copyEventAction);
		editMenu.addSeparator();
		editMenu.add(copyLoggingJsonEventAction);
		editMenu.add(copyLoggingXmlEventAction);
		editMenu.add(copyLoggingMessageAction);
		editMenu.add(copyLoggingMessagePatternAction);
		editMenu.add(copyLoggerNameAction);
		editMenu.add(copyLoggingThrowableAction);
		editMenu.add(copyLoggingCallStackAction);
		editMenu.add(copyLoggingCallLocationAction);
		editMenu.add(copyLoggingMarkerAction);
		editMenu.add(copyLoggingMdcAction);
		editMenu.add(copyLoggingNdcAction);
		editMenu.addSeparator();
		editMenu.add(copyAccessUriAction);
		editMenu.addSeparator();
		customCopyMenu = new JMenu("Custom copy...");
		customCopyPopupMenu = new JMenu("Custom copy...");
		editMenu.add(customCopyMenu);

		// Search
		searchMenu = new JMenu("Search");
		searchMenu.setMnemonic('s');
		searchMenu.add(findMenuAction);
		searchMenu.add(resetFindAction);
		searchMenu.add(findPreviousAction);
		searchMenu.add(findNextAction);
		searchMenu.add(findPreviousActiveAction);
		searchMenu.add(findNextActiveAction);

		// View
		viewMenu = new JMenu("View");
		viewMenu.setMnemonic('v');
		JMenuItem scrollToBottomMenuItem = new JMenuItem(scrollToBottomMenuAction);
		viewMenu.add(scrollToBottomMenuItem);
		viewMenu.add(pauseMenuAction);
		viewMenu.add(clearMenuAction);
		viewMenu.add(attachMenuAction);
		viewMenu.add(disconnectMenuAction);
		viewMenu.add(focusEventsAction);
		viewMenu.add(focusMessageAction);
		//viewMenu.add(statisticsMenuAction);
		viewMenu.add(editSourceNameMenuAction);
		viewMenu.add(editConditionMenuAction);
		viewMenu.addSeparator();
		viewMenu.add(zoomInMenuAction);
		viewMenu.add(zoomOutMenuAction);
		viewMenu.add(resetZoomMenuAction);
		viewMenu.addSeparator();
		JMenu layoutMenu = new JMenu("Layout");
		columnsMenu = new JMenu("Columns");
		layoutMenu.add(columnsMenu);
		layoutMenu.addSeparator();
		layoutMenu.add(saveLayoutAction);
		layoutMenu.add(resetLayoutAction);
		viewMenu.add(layoutMenu);
		viewMenu.addSeparator();
		viewMenu.add(previousTabAction);
		viewMenu.add(nextTabAction);
		viewMenu.addSeparator();
		viewMenu.add(closeFilterAction);
		viewMenu.add(closeOtherFiltersAction);
		viewMenu.add(closeAllFiltersAction);

		// Window
		windowMenu = new JMenu("Window");
		windowMenu.setMnemonic('w');

		// Help
		JMenu helpMenu = new JMenu("Help");
		helpMenu.setMnemonic('h');

		helpMenu.add(new JMenuItem(keyboardHelpAction));
		helpMenu.add(new JMenuItem(tipOfTheDayAction));
		helpMenu.add(new JMenuItem(checkForUpdateAction));
		helpMenu.add(new JMenuItem(troubleshootingAction));
		helpMenu.addSeparator();
		helpMenu.add(new JMenuItem(debugAction));
		if(!app.isMac())
		{
			helpMenu.addSeparator();
			helpMenu.add(new JMenuItem(aboutAction));
		}


		menubar.add(fileMenu);
		menubar.add(editMenu);
		menubar.add(searchMenu);
		menubar.add(viewMenu);
		menubar.add(windowMenu);
		menubar.add(helpMenu);

		updateWindowMenu();
		updateRecentFiles();
		updateActions();
	}

	public PreferencesMenuAction getPreferencesAction()
	{
		return preferencesMenuAction;
	}

	public JToolBar getToolbar()
	{
		return toolbar;
	}

	public JMenuBar getMenuBar()
	{
		return menubar;
	}

	public void setViewContainer(ViewContainer viewContainer)
	{
		if(this.viewContainer != viewContainer)
		{
			if(this.viewContainer != null)
			{
				this.viewContainer.removeChangeListener(containerChangeListener);
				this.viewContainer.removePropertyChangeListener(containerPropertyChangeListener);
			}
			this.viewContainer = viewContainer;
			if(this.viewContainer != null)
			{
				this.viewContainer.addChangeListener(containerChangeListener);
				this.viewContainer.addPropertyChangeListener(containerPropertyChangeListener);

				//EventWrapperViewPanel zview = viewContainer.getSelectedView();

				setEventWrapper(this.viewContainer.getSelectedEvent());
			}
			updateActions();
		}
	}

	public ViewContainer getViewContainer()
	{
		return viewContainer;
	}

	public void updateWindowMenu()
	{
		updateWindowMenu(windowMenu);
	}

	public void updateActions()
	{
		boolean hasView = false;
		boolean hasFilter = false;
		boolean isActive = false;
		//boolean hasFilteredBuffer = false;
		EventSource eventSource = null;
		EventWrapperViewPanel eventWrapperViewPanel=null;
		if(viewContainer != null)
		{
			hasView = true;
			eventWrapperViewPanel = viewContainer.getSelectedView();
			if(eventWrapperViewPanel != null)
			{
				eventSource = eventWrapperViewPanel.getEventSource();
				hasFilter = eventWrapperViewPanel.getFilterCondition() != null;
				isActive = eventWrapperViewPanel.getState() == LoggingViewState.ACTIVE;
				//hasFilteredBuffer = eventWrapperViewPanel.getBufferCondition() != null;
			}
			copySelectionAction.setView(eventWrapperViewPanel);
		}

		if(logger.isDebugEnabled()) logger.debug("updateActions() eventSource={}", eventSource);

		// File
		exportMenuAction.setView(eventWrapperViewPanel);

		// Search
		searchMenu.setEnabled(hasView);
		findMenuAction.setEnabled(hasView);
		resetFindAction.setEnabled(hasFilter);
		findPreviousAction.setEnabled(hasFilter);
		findNextAction.setEnabled(hasFilter);

		Condition condition = mainFrame.getFindActiveCondition();
		findPreviousActiveAction.setEnabled(hasView && condition != null);
		findNextActiveAction.setEnabled(hasView && condition != null);

		// View
		viewMenu.setEnabled(hasView);
		scrollToBottomMenuAction.setEnabled(hasView);
		editSourceNameMenuAction.setEnabled(hasView);
		saveLayoutAction.setEnabled(hasView);
		resetLayoutAction.setEnabled(hasView);
		pauseMenuAction.setEnabled(hasView);
		clearMenuAction.setEnabled(hasView/* && !hasFilteredBuffer*/);
		attachMenuAction.setEnabled(hasView);
		disconnectMenuAction.setEnabled(isActive);
		focusEventsAction.setEnabled(hasView);
		focusMessageAction.setEnabled(hasView);
		updateShowHideMenu();
		previousTabAction.updateAction();
		nextTabAction.updateAction();

		disconnectToolBarAction.setEnabled(isActive);

		scrollToBottomMenuAction.updateAction();
		editSourceNameMenuAction.updateAction();
		editConditionMenuAction.updateAction();
		zoomInMenuAction.updateAction();
		zoomOutMenuAction.updateAction();
		resetZoomMenuAction.updateAction();

		pauseMenuAction.updateAction();
		attachMenuAction.updateAction();

		closeFilterAction.updateAction();
		closeOtherFiltersAction.updateAction();
		closeAllFiltersAction.updateAction();

		scrollToBottomButton.setSelected(isScrollingToBottom());
		pauseToolBarAction.updateAction();
		attachToolBarAction.updateAction();

		scrollToBottomToolBarAction.setEnabled(hasView);
		pauseToolBarAction.setEnabled(hasView);
		clearToolBarAction.setEnabled(hasView/* && !hasFilteredBuffer*/);
		findToolBarAction.setEnabled(hasView);
		//statisticsToolBarAction.setEnabled(hasView);
		attachToolBarAction.setEnabled(hasView);
		disconnectToolBarAction.setEnabled(isActive);

		if(eventSource != null)
		{
			showUnfilteredEventAction.setEnabled((eventSource.getFilter() != null));
		}
		else
		{
			showUnfilteredEventAction.setEnabled(false);
		}
	}

	private void updateShowHideMenu()
	{
		columnsMenu.removeAll();
		if(viewContainer != null)
		{
			EventWrapperViewPanel<?> viewPanel = viewContainer.getSelectedView();
			if(viewPanel != null)
			{
				EventWrapperViewTable<?> table = viewPanel.getTable();
				if(table != null)
				{
					PersistentTableColumnModel tableColumnModel = table.getTableColumnModel();
					List<PersistentTableColumnModel.TableColumnLayoutInfo> cli = tableColumnModel
						.getColumnLayoutInfos();
					for(PersistentTableColumnModel.TableColumnLayoutInfo current : cli)
					{
						boolean visible = current.isVisible();
						JCheckBoxMenuItem cbmi = new JCheckBoxMenuItem(new ShowHideAction(tableColumnModel, current.getColumnName(), visible));
						cbmi.setSelected(visible);
						columnsMenu.add(cbmi);
					}
				}
			}

		}
	}

	void setShowingFilters(boolean showingFilters)
	{
		if(viewContainer != null)
		{
			EventWrapperViewPanel eventWrapperViewPanel = viewContainer.getSelectedView();
			if(eventWrapperViewPanel != null)
			{
				eventWrapperViewPanel.setShowingFilters(showingFilters);
			}
		}
	}

	boolean isScrollingToBottom()
	{
		if(viewContainer != null)
		{
			EventWrapperViewPanel eventWrapperViewPanel = viewContainer.getSelectedView();
			if(eventWrapperViewPanel != null)
			{
				return eventWrapperViewPanel.isScrollingToBottom();
			}
		}
		return false;
	}

	void setScrollingToBottom(boolean scrollingToBottom)
	{
		if(viewContainer != null)
		{
			EventWrapperViewPanel eventWrapperViewPanel = viewContainer.getSelectedView();
			if(eventWrapperViewPanel != null)
			{
				eventWrapperViewPanel.setScrollingToBottom(scrollingToBottom);
			}
		}
	}


	boolean isPaused()
	{
		if(viewContainer != null)
		{
			EventWrapperViewPanel eventWrapperViewPanel = viewContainer.getSelectedView();
			if(eventWrapperViewPanel != null)
			{
				return eventWrapperViewPanel.isPaused();
			}
		}
		return false;
	}

	void setPaused(boolean paused)
	{
		if(viewContainer != null)
		{
			EventWrapperViewPanel eventWrapperViewPanel = viewContainer.getSelectedView();
			if(eventWrapperViewPanel != null)
			{
				eventWrapperViewPanel.setPaused(paused);
			}
		}
	}

	void clear()
	{
		if(viewContainer != null)
		{
			EventWrapperViewPanel eventWrapperViewPanel = viewContainer.getSelectedView();
			if(eventWrapperViewPanel != null)
			{
				eventWrapperViewPanel.clear();
			}
		}
	}

	void focusTable()
	{
		if(viewContainer != null)
		{
			EventWrapperViewPanel eventWrapperViewPanel = viewContainer.getSelectedView();
			if(eventWrapperViewPanel != null)
			{
				eventWrapperViewPanel.focusTable();
			}
		}
	}

	private void editCondition()
	{
		if(viewContainer != null)
		{
			EventWrapperViewPanel eventWrapperViewPanel = viewContainer.getSelectedView();
			if(eventWrapperViewPanel != null)
			{
				Condition currentFilter = eventWrapperViewPanel.getTable().getFilterCondition();

				Condition condition = eventWrapperViewPanel.getCombinedCondition(currentFilter);
				if(condition != null)
				{
					mainFrame.getPreferencesDialog().editCondition(condition);
				}
			}
		}
	}

	private void editSourceName()
	{
		if(viewContainer != null)
		{
			EventWrapperViewPanel eventWrapperViewPanel = viewContainer.getSelectedView();
			if(eventWrapperViewPanel != null)
			{
				String sourceIdentifier = eventWrapperViewPanel.getEventSource().getSourceIdentifier().getIdentifier();
				if(!"global".equals(sourceIdentifier) && !"Lilith".equals(sourceIdentifier))
				{
					mainFrame.getPreferencesDialog().editSourceName(sourceIdentifier);
				}
			}
		}
	}

	private void attachDetach()
	{
		ViewContainer container = getViewContainer();
		if(container != null)
		{
			MainFrame mainFrame = container.getMainFrame();
			ViewWindow window = container.resolveViewWindow();

			if(window instanceof JFrame)
			{
				window.closeWindow();
				mainFrame.showInternalFrame(container);
			}
			else if(window instanceof JInternalFrame)
			{
				window.closeWindow();
				mainFrame.showFrame(container);
			}
		}
		focusTable();
	}

/*
	public void requestMenuBarFocus()
	{
		menubar.getComponent().requestFocusInWindow();
	}
*/
/*
	private void showStatistics()
	{
		if(viewContainer != null)
		{
			EventWrapperViewPanel eventWrapperViewPanel = viewContainer.getSelectedView();
			if(eventWrapperViewPanel != null)
			{
				MainFrame mainFrame = viewContainer.getMainFrame();
				mainFrame.showStatistics(eventWrapperViewPanel.getEventSource().getSourceIdentifier());
			}
		}
	}
*/

	private void disconnect()
	{
		if(viewContainer != null)
		{
			EventWrapperViewPanel eventWrapperViewPanel = viewContainer.getSelectedView();
			if(eventWrapperViewPanel != null)
			{
				eventWrapperViewPanel.closeConnection(eventWrapperViewPanel.getEventSource().getSourceIdentifier());
			}
		}
	}

	private void focusMessage()
	{
		if(viewContainer != null)
		{
			EventWrapperViewPanel eventWrapperViewPanel = viewContainer.getSelectedView();
			if(eventWrapperViewPanel != null)
			{
				eventWrapperViewPanel.focusMessagePane();
			}
		}
	}

	private void focusEvents()
	{
		if(viewContainer != null)
		{
			EventWrapperViewPanel eventWrapperViewPanel = viewContainer.getSelectedView();
			if(eventWrapperViewPanel != null)
			{
				eventWrapperViewPanel.focusTable();
			}
		}
	}

	private void findNext()
	{
		if(viewContainer != null)
		{
			EventWrapperViewPanel eventWrapperViewPanel = viewContainer.getSelectedView();
			if(eventWrapperViewPanel != null)
			{
				eventWrapperViewPanel
					.findNext(eventWrapperViewPanel.getSelectedRow(), eventWrapperViewPanel.getFilterCondition());
			}
		}
	}

	private void findNextActive()
	{
		Condition condition = mainFrame.getFindActiveCondition();
		if(viewContainer != null && condition != null)
		{
			EventWrapperViewPanel eventWrapperViewPanel = viewContainer.getSelectedView();
			if(eventWrapperViewPanel != null)
			{
				eventWrapperViewPanel
					.findNext(eventWrapperViewPanel.getSelectedRow(), condition);
			}
		}
	}

	private void findPrevious()
	{
		if(viewContainer != null)
		{
			EventWrapperViewPanel eventWrapperViewPanel = viewContainer.getSelectedView();
			if(eventWrapperViewPanel != null)
			{
				eventWrapperViewPanel
					.findPrevious(eventWrapperViewPanel.getSelectedRow(), eventWrapperViewPanel.getFilterCondition());
			}
		}
	}

	private void findPreviousActive()
	{
		Condition condition = mainFrame.getFindActiveCondition();
		if(viewContainer != null && condition != null)
		{
			EventWrapperViewPanel eventWrapperViewPanel = viewContainer.getSelectedView();
			if(eventWrapperViewPanel != null)
			{
				eventWrapperViewPanel
					.findPrevious(eventWrapperViewPanel.getSelectedRow(), condition);
			}
		}
	}

	private void resetFind()
	{
		if(viewContainer != null)
		{
			EventWrapperViewPanel eventWrapperViewPanel = viewContainer.getSelectedView();
			if(eventWrapperViewPanel != null)
			{
				eventWrapperViewPanel.resetFind();
			}
		}
	}

	private void closeCurrentFilter()
	{
		if(viewContainer != null)
		{
			viewContainer.closeCurrentFilter();
		}
	}

	private void closeOtherFilters()
	{
		if(viewContainer != null)
		{
			viewContainer.closeOtherFilters();
		}
	}

	private void closeAllFilters()
	{
		if(viewContainer != null)
		{
			viewContainer.closeAllFilters();
		}
	}


	private void previousTab()
	{
		if(logger.isDebugEnabled()) logger.debug("PreviousTab");
		if(viewContainer != null)
		{
			int viewCount = viewContainer.getViewCount();
			int viewIndex = viewContainer.getViewIndex();
			if(viewIndex > -1)
			{
				int newView = viewIndex - 1;
				if(newView < 0)
				{
					newView = viewCount - 1;
				}
				if(newView >= 0 && newView < viewCount)
				{
					viewContainer.setViewIndex(newView);
				}
			}
		}
	}

	private void nextTab()
	{
		if(logger.isDebugEnabled()) logger.debug("NextTab");
		if(viewContainer != null)
		{
			int viewIndex = viewContainer.getViewIndex();
			int viewCount = viewContainer.getViewCount();
			if(viewIndex > -1)
			{
				int newView = viewIndex + 1;
				if(newView >= viewCount)
				{
					newView = 0;
				}
				if(newView >= 0)
				{
					viewContainer.setViewIndex(newView);
				}
			}
		}
	}

	private void showUnfilteredEvent()
	{
		if(viewContainer != null)
		{
			EventWrapperViewPanel eventWrapperViewPanel = viewContainer.getSelectedView();
			if(eventWrapperViewPanel != null)
			{
				eventWrapperViewPanel.showUnfilteredEvent();
			}
		}
	}

	private void initPopup()
	{
		if(logger.isDebugEnabled()) logger.debug("initPopup()");
		popup = new JPopupMenu();
		JMenuItem showUnfilteredMenuItem = new JMenuItem(showUnfilteredEventAction);
		Font f = showUnfilteredMenuItem.getFont();
		Font boldFont = f.deriveFont(Font.BOLD);
		showUnfilteredMenuItem.setFont(boldFont);

		popup.add(showUnfilteredMenuItem);

		updateCustomCopyMenu(this.eventWrapper);

		copyPopupMenu = new JMenu("Copy...");
		popup.add(copyPopupMenu);
		copyPopupMenu.add(new JMenuItem(copySelectionAction));
		copyPopupMenu.addSeparator();
		copyPopupMenu.add(new JMenuItem(copyEventAction));
		copyPopupMenu.addSeparator();
		copyPopupMenu.add(new JMenuItem(copyLoggingJsonEventAction));
		copyPopupMenu.add(new JMenuItem(copyLoggingXmlEventAction));
		copyPopupMenu.add(new JMenuItem(copyLoggingMessageAction));
		copyPopupMenu.add(new JMenuItem(copyLoggingMessagePatternAction));
		copyPopupMenu.add(new JMenuItem(copyLoggerNameAction));
		copyPopupMenu.add(new JMenuItem(copyLoggingThrowableAction));
		copyPopupMenu.add(new JMenuItem(copyLoggingCallStackAction));
		copyPopupMenu.add(new JMenuItem(copyLoggingCallLocationAction));
		copyPopupMenu.add(new JMenuItem(copyLoggingMarkerAction));
		copyPopupMenu.add(new JMenuItem(copyLoggingMdcAction));
		copyPopupMenu.add(new JMenuItem(copyLoggingNdcAction));
		copyPopupMenu.addSeparator();
		copyPopupMenu.add(new JMenuItem(copyAccessUriAction));
		copyPopupMenu.addSeparator();
		copyPopupMenu.add(customCopyPopupMenu);

		filterPopupMenu = new JMenu("Filter...");
		popup.add(filterPopupMenu);
		filterPopupMenu.add(new JMenuItem(closeFilterAction));
		filterPopupMenu.add(new JMenuItem(closeOtherFiltersAction));
		filterPopupMenu.add(new JMenuItem(closeAllFiltersAction));

		focusPopupMenu = new JMenu("Focus...");
		popup.add(focusPopupMenu);
		excludePopupMenu = new JMenu("Exclude...");
		popup.add(excludePopupMenu);

		sendToPopupMenu = new JMenu("Send to...");
		popup.add(sendToPopupMenu);

		JMenuItem gotoSourceMenuItem = new JMenuItem(gotoSourceAction);
		popup.add(gotoSourceMenuItem);
	}

	private void setEventWrapper(EventWrapper wrapper)
	{
		if(logger.isDebugEnabled()) logger.debug("setEventWrapper: {}", wrapper);
		this.eventWrapper = wrapper;
		gotoSourceAction.setEventWrapper(wrapper);
		copyEventAction.setEventWrapper(wrapper);
		copyLoggingJsonEventAction.setEventWrapper(wrapper);
		copyLoggingXmlEventAction.setEventWrapper(wrapper);
		copyLoggingMessageAction.setEventWrapper(wrapper);
		copyLoggingMessagePatternAction.setEventWrapper(wrapper);
		copyLoggerNameAction.setEventWrapper(wrapper);
		copyLoggingThrowableAction.setEventWrapper(wrapper);
		copyLoggingCallStackAction.setEventWrapper(wrapper);
		copyLoggingCallLocationAction.setEventWrapper(wrapper);
		copyLoggingMarkerAction.setEventWrapper(wrapper);
		copyLoggingMdcAction.setEventWrapper(wrapper);
		copyLoggingNdcAction.setEventWrapper(wrapper);
		copyAccessUriAction.setEventWrapper(wrapper);
		boolean enableEditMenu;
		if(wrapper == null)
		{
			enableEditMenu = false;
		}
		else
		{
			Serializable event = wrapper.getEvent();
			if(event instanceof LoggingEvent || event instanceof AccessEvent)
			{
				enableEditMenu = true;
			}
			else
			{
				enableEditMenu = false;
			}
		}
		editMenu.setEnabled(enableEditMenu);
		updateCustomCopyMenu(wrapper);
	}

	private void populateFocusExclude(EventWrapper wrapper)
	{
		if(wrapper == null)
		{
			focusPopupMenu.setEnabled(false);
			excludePopupMenu.setEnabled(false);
			return;
		}

		Serializable obj = wrapper.getEvent();
		if(obj instanceof LoggingEvent)
		{
			LoggingEvent event = (LoggingEvent) obj;
			String loggerName=event.getLogger();
			List<String> preparedLoggerNames =  prepareLoggerNames(loggerName);
			if(logger.isDebugEnabled()) logger.debug("preparedLoggerNames for input {}: {}", loggerName, preparedLoggerNames);
			if(preparedLoggerNames.size() == 0)
			{
				focusPopupMenu.setEnabled(false);
				excludePopupMenu.setEnabled(false);
			}
			else
			{
				focusPopupMenu.removeAll();
				for(String current : preparedLoggerNames)
				{
					focusPopupMenu.add(new JMenuItem(new FocusAction(current)));
				}
				focusPopupMenu.setEnabled(true);

				excludePopupMenu.removeAll();
				for(String current : preparedLoggerNames)
				{
					excludePopupMenu.add(new JMenuItem(new ExcludeAction(current)));
				}
				excludePopupMenu.setEnabled(true);
			}
		}
		else
		{
			focusPopupMenu.setEnabled(false);
			excludePopupMenu.setEnabled(false);
		}
	}

	private List<String> prepareLoggerNames(String loggerName)
	{
		if(loggerName == null)
		{
			return new ArrayList<String>();
		}
		List<String> tokens = new ArrayList<String>();
		loggerName = loggerName.replace('$', '.'); // better handling of inner classes
		StringTokenizer tok = new StringTokenizer(loggerName, ".", false);
		while(tok.hasMoreTokens())
		{
			String current=tok.nextToken();
			tokens.add(current);
		}

		List<String> result=new ArrayList<String>(tokens.size());
		for(int i=tokens.size();i>0;i--)
		{
			StringBuilder builder=new StringBuilder();
			boolean first=true;
			for(int j=0;j<i;j++)
			{
				if(first)
				{
					first = false;
				}
				else
				{
					builder.append(".");
				}
				builder.append(tokens.get(j));
			}
			result.add(builder.toString());
		}
		return result;
	}

	private void updateCustomCopyMenu(EventWrapper wrapper)
	{
		ApplicationPreferences prefs = mainFrame.getApplicationPreferences();
		String[] scripts = prefs.getClipboardFormatterScriptFiles();
		boolean changed = false;
		if(groovyClipboardActions == null)
		{
			groovyClipboardActions = new HashMap<String, CopyToClipboardAction>();
			changed = true;
		}
		if(groovyClipboardData == null)
		{
			groovyClipboardData = new HashMap<String, ClipboardFormatterData>();
			changed = true;
		}
		if(scripts == null || scripts.length == 0)
		{
			if(groovyClipboardActions.size() > 0)
			{
				groovyClipboardActions.clear();
				groovyClipboardData.clear();
				changed = true;
			}
		}
		else
		{
			List<String> scriptsList = Arrays.asList(scripts);
			// add missing formatters
			for(String current : scriptsList)
			{
				if(!groovyClipboardActions.containsKey(current))
				{
					GroovyFormatter newFormatter = new GroovyFormatter();
					newFormatter.setGroovyFileName(prefs.resolveClipboardFormatterScriptFile(current).getAbsolutePath());
					CopyToClipboardAction newAction = new CopyToClipboardAction(newFormatter);
					groovyClipboardActions.put(current, newAction);
					changed = true;
				}
			}

			// find deleted formatters
			List<String> deletedList = new ArrayList<String>();
			for(Map.Entry<String, CopyToClipboardAction> current : groovyClipboardActions.entrySet())
			{
				if(!scriptsList.contains(current.getKey()))
				{
					deletedList.add(current.getKey());
				}
			}

			// remove deleted formatters
			for(String current : deletedList)
			{
				groovyClipboardActions.remove(current);
				changed = true;
			}
		}

		for(Map.Entry<String, CopyToClipboardAction> current : groovyClipboardActions.entrySet())
		{
			String key = current.getKey();
			CopyToClipboardAction value = current.getValue();
			ClipboardFormatter formatter = value.getClipboardFormatter();
			if(formatter == null)
			{
				continue;
			}
			ClipboardFormatterData data = new ClipboardFormatterData(formatter);
			if(!data.equals(groovyClipboardData.get(key)))
			{
				changed = true;
				groovyClipboardData.put(key, data);
				value.setClipboardFormatter(formatter); // this reinitializes the action
			}
		}
		
		if(changed)
		{
			if(groovyClipboardActions.size() == 0)
			{
				customCopyMenu.setEnabled(false);
				customCopyPopupMenu.setEnabled(false);
				customCopyMenu.removeAll();
				customCopyPopupMenu.removeAll();
			}
			else
			{
				customCopyMenu.setEnabled(true);
				customCopyPopupMenu.setEnabled(true);
				customCopyMenu.removeAll();
				customCopyPopupMenu.removeAll();

				SortedSet<CopyToClipboardAction> sorted = new TreeSet<CopyToClipboardAction>(CopyToClipboardByNameComparator.INSTANCE);
				// sort the actions by name
				for(Map.Entry<String, CopyToClipboardAction> current : groovyClipboardActions.entrySet())
				{
					sorted.add(current.getValue());
				}

				// add the sorted actions to the menus.
				for(CopyToClipboardAction current : sorted)
				{
					customCopyMenu.add(new JMenuItem(current));
					customCopyPopupMenu.add(new JMenuItem(current));
				}
			}
		}

		for(Map.Entry<String, CopyToClipboardAction> current : groovyClipboardActions.entrySet())
		{
			current.getValue().setEventWrapper(wrapper);
		}
	}

	public void updateWindowMenu(JMenu windowMenu)
	{
		// must be executed later because the ancestor-change-event is fired
		// while parent is still != null...
		// see JComponent.removeNotify source for comment.
		SwingUtilities.invokeLater(new UpdateWindowMenuRunnable(windowMenu));
	}

	public ActionListener getAboutAction()
	{
		return aboutAction;
	}

	private void updatePopup()
	{
		if(logger.isDebugEnabled()) logger.debug("updatePopup()");
		if(popup == null)
		{
			initPopup();
		}
		sendToPopupMenu.removeAll();
		boolean enableCopyMenu;
		if(eventWrapper == null)
		{
			sendToPopupMenu.setEnabled(false);
			enableCopyMenu = false;
		}
		else
		{
			Serializable eventObj = eventWrapper.getEvent();
			if(eventObj instanceof LoggingEvent)
			{
				enableCopyMenu = true;
				Map<String, EventSender<LoggingEvent>> senders = mainFrame.getLoggingEventSenders();
				if(logger.isDebugEnabled()) logger.debug("Senders: {}", senders);
				if(senders.size() == 0)
				{
					sendToPopupMenu.setEnabled(false);
				}
				else
				{
					sendToPopupMenu.setEnabled(true);
					for(Map.Entry<String, EventSender<LoggingEvent>> current : senders.entrySet())
					{
						@SuppressWarnings({"unchecked"})
						SendAction<LoggingEvent> action = new SendAction<LoggingEvent>(current.getKey(), current.getValue(), eventWrapper);
						JMenuItem menuItem = new JMenuItem(action);
						sendToPopupMenu.add(menuItem);
					}
				}
			}
			else if(eventObj instanceof AccessEvent)
			{
				enableCopyMenu = true;
				Map<String, EventSender<AccessEvent>> senders = mainFrame.getAccessEventSenders();
				if(logger.isDebugEnabled()) logger.debug("Senders: {}", senders);
				if(senders.size() == 0)
				{
					sendToPopupMenu.setEnabled(false);
				}
				else
				{
					sendToPopupMenu.setEnabled(true);
					for(Map.Entry<String, EventSender<AccessEvent>> current : senders.entrySet())
					{
						@SuppressWarnings({"unchecked"})
						SendAction<AccessEvent> action = new SendAction<AccessEvent>(current.getKey(), current.getValue(), eventWrapper);
						JMenuItem menuItem = new JMenuItem(action);
						sendToPopupMenu.add(menuItem);
					}
				}
			}
			else
			{
				enableCopyMenu = false;
				sendToPopupMenu.setEnabled(false);
			}
		}
		boolean enableFilterMenu = closeFilterAction.isEnabled() || closeOtherFiltersAction.isEnabled() || closeAllFiltersAction.isEnabled();
		filterPopupMenu.setEnabled(enableFilterMenu);
		copyPopupMenu.setEnabled(enableCopyMenu);
		populateFocusExclude(eventWrapper);
	}

	public JPopupMenu getPopupMenu()
	{
		updatePopup();

		return popup;
	}

	public void updateRecentFiles()
	{
		ApplicationPreferences prefs = mainFrame.getApplicationPreferences();
		List<String> recentFilesStrings = prefs.getRecentFiles();
		if(recentFilesStrings == null || recentFilesStrings.size()==0)
		{
			recentFilesMenu.removeAll();
			recentFilesMenu.setEnabled(false);
		}
		else
		{
			boolean fullPath=prefs.isShowingFullRecentPath();

			recentFilesMenu.removeAll();

			for(String current:recentFilesStrings)
			{
				recentFilesMenu.add(new OpenFileAction(current, fullPath));
			}
			recentFilesMenu.addSeparator();
			recentFilesMenu.add(clearRecentFilesAction);
			recentFilesMenu.setEnabled(true);
		}
	}

	private class OpenFileAction
		extends AbstractAction
	{
		private static final long serialVersionUID = 3138705799791457944L;

		private String absoluteName;

		public OpenFileAction(String absoluteName, boolean fullPath)
		{
			super();

			this.absoluteName=absoluteName;
			String name=absoluteName;
			if(!fullPath)
			{
				File f=new File(absoluteName);
				name=f.getName();
			}
			putValue(Action.NAME, name);
			putValue(Action.SMALL_ICON, EMPTY_16_ICON);
			putValue(Action.SHORT_DESCRIPTION, absoluteName);
		}

		public void actionPerformed(ActionEvent e)
		{
			mainFrame.open(new File(absoluteName));
		}
	}

	private class ClearRecentFilesAction
		extends AbstractAction
	{
		private static final long serialVersionUID = 2330892725802760973L;

		public ClearRecentFilesAction()
		{
			super("Clear Recent Files");
			putValue(Action.SMALL_ICON, EMPTY_16_ICON);
			putValue(Action.MNEMONIC_KEY, Integer.valueOf('c'));
		}

		public void actionPerformed(ActionEvent e)
		{
			mainFrame.getApplicationPreferences().clearRecentFiles();
		}
	}
/*
	private class ClearAndRemoveInactiveAction
		extends AbstractAction
	{

		public ClearAndRemoveInactiveAction()
		{
			super("Clean and remove inactive");
			putValue(Action.SMALL_ICON, EMPTY_16_ICON);
			KeyStroke accelerator= KeyStrokes.resolveAcceleratorKeyStroke(KeyStrokes.COMMAND_ALIAS+" shift R");
			if(logger.isDebugEnabled()) logger.debug("accelerator: {}", accelerator);
			putValue(Action.ACCELERATOR_KEY, accelerator);
			putValue(Action.MNEMONIC_KEY, Integer.valueOf('c'));
		}

		public void actionPerformed(ActionEvent e)
		{
			mainFrame.removeInactiveViews(false, true);
			mainFrame.updateWindowMenus();
		}
	}
*/

	private class RemoveInactiveAction
		extends AbstractAction
	{
		private static final long serialVersionUID = -6662970580652310690L;

		public RemoveInactiveAction()
		{
			super("Remove inactive");
			putValue(Action.SMALL_ICON, EMPTY_16_ICON);
			KeyStroke accelerator = KeyStrokes.resolveAcceleratorKeyStroke(KeyStrokes.COMMAND_ALIAS + " R");
			if(logger.isDebugEnabled()) logger.debug("accelerator: {}", accelerator);
			putValue(Action.ACCELERATOR_KEY, accelerator);
			putValue(Action.MNEMONIC_KEY, Integer.valueOf('r'));
		}

		public void actionPerformed(ActionEvent e)
		{
			mainFrame.removeInactiveViews(false, false);
			mainFrame.updateWindowMenus();
		}
	}

	private class ShowTaskManagerAction
		extends AbstractAction
	{
		private static final long serialVersionUID = -8228641057263498624L;

		public ShowTaskManagerAction()
		{
			super("Task Manager");
			putValue(Action.SMALL_ICON, EMPTY_16_ICON);
			//KeyStroke accelerator= KeyStrokes.resolveAcceleratorKeyStroke(KeyStrokes.COMMAND_ALIAS+" R");
			//if(logger.isDebugEnabled()) logger.debug("accelerator: {}", accelerator);
			//putValue(Action.ACCELERATOR_KEY, accelerator);
			//putValue(Action.MNEMONIC_KEY, Integer.valueOf('r'));
		}

		public void actionPerformed(ActionEvent e)
		{
			mainFrame.showTaskManager();
		}
	}

	private class CloseAllAction
		extends AbstractAction
	{
		private static final long serialVersionUID = -1587444647880660196L;

		public CloseAllAction()
		{
			super("Close all");
			putValue(Action.SMALL_ICON, EMPTY_16_ICON);
			KeyStroke accelerator= KeyStrokes.resolveAcceleratorKeyStroke(KeyStrokes.COMMAND_ALIAS+" shift W");
			if(logger.isDebugEnabled()) logger.debug("accelerator: {}", accelerator);
			putValue(Action.ACCELERATOR_KEY, accelerator);
			//putValue(Action.MNEMONIC_KEY, Integer.valueOf('r'));
		}

		public void actionPerformed(ActionEvent e)
		{
			mainFrame.closeAllViews(null);
			mainFrame.updateWindowMenus();
		}
	}

	private class CloseOtherAction
		extends AbstractAction
	{
		private static final long serialVersionUID = -3031217070975763827L;

		public CloseOtherAction()
		{
			super("Close all other");
			putValue(Action.SMALL_ICON, EMPTY_16_ICON);
			//KeyStroke accelerator= KeyStrokes.resolveAcceleratorKeyStroke(KeyStrokes.COMMAND_ALIAS+" R");
			//if(logger.isDebugEnabled()) logger.debug("accelerator: {}", accelerator);
			//putValue(Action.ACCELERATOR_KEY, accelerator);
			//putValue(Action.MNEMONIC_KEY, Integer.valueOf('r'));
		}

		public void actionPerformed(ActionEvent e)
		{
			mainFrame.closeAllViews(viewContainer);
			mainFrame.updateWindowMenus();
		}
	}

	private class MinimizeAllAction
		extends AbstractAction
	{
		private static final long serialVersionUID = -8828005158469519472L;

		public MinimizeAllAction()
		{
			super("Minimize all");
			putValue(Action.SMALL_ICON, EMPTY_16_ICON);
			//KeyStroke accelerator= KeyStrokes.resolveAcceleratorKeyStroke(KeyStrokes.COMMAND_ALIAS+" R");
			//if(logger.isDebugEnabled()) logger.debug("accelerator: {}", accelerator);
			//putValue(Action.ACCELERATOR_KEY, accelerator);
			//putValue(Action.MNEMONIC_KEY, Integer.valueOf('r'));
		}

		public void actionPerformed(ActionEvent e)
		{
			mainFrame.minimizeAllViews(null);
			mainFrame.updateWindowMenus();
		}
	}

	private class MinimizeAllOtherAction
		extends AbstractAction
	{
		private static final long serialVersionUID = -2357859864329239268L;

		public MinimizeAllOtherAction()
		{
			super("Minimize all other");
			putValue(Action.SMALL_ICON, EMPTY_16_ICON);
			//KeyStroke accelerator= KeyStrokes.resolveAcceleratorKeyStroke(KeyStrokes.COMMAND_ALIAS+" R");
			//if(logger.isDebugEnabled()) logger.debug("accelerator: {}", accelerator);
			//putValue(Action.ACCELERATOR_KEY, accelerator);
			//putValue(Action.MNEMONIC_KEY, Integer.valueOf('r'));
		}

		public void actionPerformed(ActionEvent e)
		{
			mainFrame.minimizeAllViews(viewContainer);
			mainFrame.updateWindowMenus();
		}
	}

	private class ClearToolBarAction
		extends AbstractAction
	{
		private static final long serialVersionUID = -4713267797278778997L;

		public ClearToolBarAction()
		{
			super();
			putValue(Action.SMALL_ICON, CLEAR_TOOLBAR_ICON);
			putValue(Action.SHORT_DESCRIPTION, "Clear");
		}

		public void actionPerformed(ActionEvent e)
		{
			clear();
		}
	}

	private class ClearMenuAction
		extends AbstractAction
	{
		private static final long serialVersionUID = 776175842981192877L;

		public ClearMenuAction()
		{
			super("Clear");
			putValue(Action.SMALL_ICON, CLEAR_MENU_ICON);
			putValue(Action.SHORT_DESCRIPTION, "Clear this view.");
		}

		public void actionPerformed(ActionEvent e)
		{
			clear();
		}
	}

	private class ZoomInMenuAction
		extends AbstractAction
	{
		private static final long serialVersionUID = -8380709624103338783L;

		public ZoomInMenuAction()
		{
			super("Zoom in");
			KeyStroke accelerator = KeyStrokes.resolveAcceleratorKeyStroke(KeyStrokes.COMMAND_ALIAS + " PERIOD");
			if(logger.isDebugEnabled()) logger.debug("accelerator: {}", accelerator);
			putValue(Action.ACCELERATOR_KEY, accelerator);
			putValue(Action.SMALL_ICON, EMPTY_16_ICON);
			putValue(Action.SHORT_DESCRIPTION, "Zoom in on the details view.");
		}

		public void actionPerformed(ActionEvent e)
		{
			mainFrame.zoomIn();
		}

		public void updateAction()
		{
			boolean enable = false;
			if(viewContainer != null)
			{
				EventWrapperViewPanel eventWrapperViewPanel = viewContainer.getSelectedView();
				if(eventWrapperViewPanel != null)
				{
					enable = true;
				}
			}
			setEnabled(enable);
		}
	}

	private class ZoomOutMenuAction
		extends AbstractAction
	{
		private static final long serialVersionUID = -8380709624103338783L;

		public ZoomOutMenuAction()
		{
			super("Zoom out");
			KeyStroke accelerator = KeyStrokes.resolveAcceleratorKeyStroke(KeyStrokes.COMMAND_ALIAS + " shift PERIOD");
			if(logger.isDebugEnabled()) logger.debug("accelerator: {}", accelerator);
			putValue(Action.ACCELERATOR_KEY, accelerator);
			putValue(Action.SMALL_ICON, EMPTY_16_ICON);
			putValue(Action.SHORT_DESCRIPTION, "Zoom out on the details view.");
		}

		public void actionPerformed(ActionEvent e)
		{
			mainFrame.zoomOut();
		}

		public void updateAction()
		{
			boolean enable = false;
			if(viewContainer != null)
			{
				EventWrapperViewPanel eventWrapperViewPanel = viewContainer.getSelectedView();
				if(eventWrapperViewPanel != null)
				{
					enable = true;
				}
			}
			setEnabled(enable);
		}
	}

	private class ResetZoomMenuAction
		extends AbstractAction
	{
		private static final long serialVersionUID = -8380709624103338783L;

		public ResetZoomMenuAction()
		{
			super("Reset Zoom");
			//KeyStroke accelerator = KeyStrokes.resolveAcceleratorKeyStroke(KeyStrokes.COMMAND_ALIAS + " +");
			//if(logger.isDebugEnabled()) logger.debug("accelerator: {}", accelerator);
			//putValue(Action.ACCELERATOR_KEY, accelerator);
			putValue(Action.SMALL_ICON, EMPTY_16_ICON);
			putValue(Action.SHORT_DESCRIPTION, "Reset Zoom of the details view.");
		}

		public void actionPerformed(ActionEvent e)
		{
			mainFrame.resetZoom();
		}

		public void updateAction()
		{
			boolean enable = false;
			if(viewContainer != null)
			{
				EventWrapperViewPanel eventWrapperViewPanel = viewContainer.getSelectedView();
				if(eventWrapperViewPanel != null)
				{
					enable = true;
				}
			}
			setEnabled(enable);
		}
	}

	private class EditConditionMenuAction
		extends AbstractAction
	{
		private static final long serialVersionUID = -8380709624103338783L;

		public EditConditionMenuAction()
		{
			super("Add condition...");
			KeyStroke accelerator = KeyStrokes.resolveAcceleratorKeyStroke(KeyStrokes.COMMAND_ALIAS + " I");
			if(logger.isDebugEnabled()) logger.debug("accelerator: {}", accelerator);
			putValue(Action.ACCELERATOR_KEY, accelerator);
			putValue(Action.SMALL_ICON, EMPTY_16_ICON);
			putValue(Action.SHORT_DESCRIPTION, "Add the condition of the current view.");
		}

		public void actionPerformed(ActionEvent e)
		{
			editCondition();
		}

		public void updateAction()
		{
			boolean enable = false;
			if(viewContainer != null)
			{
				EventWrapperViewPanel eventWrapperViewPanel = viewContainer.getSelectedView();
				if(eventWrapperViewPanel != null)
				{
					Condition currentFilter = eventWrapperViewPanel.getTable().getFilterCondition();

					Condition condition = eventWrapperViewPanel.getCombinedCondition(currentFilter);
					if(condition != null)
					{
						enable = true;
					}
				}
			}
			setEnabled(enable);
		}
	}

	private class EditSourceNameMenuAction
		extends AbstractAction
	{
		private static final long serialVersionUID = 2807692748192366344L;

		public EditSourceNameMenuAction()
		{
			super("Edit source name...");
			KeyStroke accelerator = KeyStrokes.resolveAcceleratorKeyStroke(KeyStrokes.COMMAND_ALIAS + " B");
			if(logger.isDebugEnabled()) logger.debug("accelerator: {}", accelerator);
			putValue(Action.ACCELERATOR_KEY, accelerator);
			putValue(Action.SMALL_ICON, EMPTY_16_ICON);
			putValue(Action.SHORT_DESCRIPTION, "Edit the source name of the current view.");
		}

		public void actionPerformed(ActionEvent e)
		{
			editSourceName();
		}


		public void updateAction()
		{
			boolean enable = false;
			if(viewContainer != null)
			{
				EventWrapperViewPanel eventWrapperViewPanel = viewContainer.getSelectedView();
				if(eventWrapperViewPanel != null)
				{
					String sourceIdentifier = eventWrapperViewPanel.getEventSource().getSourceIdentifier()
						.getIdentifier();
					if(!"global".equals(sourceIdentifier) && !"Lilith".equals(sourceIdentifier))
					{
						enable = true;
					}
				}
			}
			setEnabled(enable);
		}
	}


	private class AttachMenuAction
		extends AbstractAction
	{
		private static final long serialVersionUID = -6686061036755515933L;

		private Icon attachIcon = ATTACH_MENU_ICON;
		private Icon detachIcon = DETACH_MENU_ICON;

		public AttachMenuAction()
		{
			super();
			KeyStroke accelerator = KeyStrokes.resolveAcceleratorKeyStroke(KeyStrokes.COMMAND_ALIAS + " shift A");
			if(logger.isDebugEnabled()) logger.debug("accelerator: {}", accelerator);
			putValue(Action.ACCELERATOR_KEY, accelerator);
			updateAction();
		}

		public void actionPerformed(ActionEvent e)
		{
			attachDetach();
			updateAction();
		}

		public void updateAction()
		{
			ViewContainer container = getViewContainer();
			if(container != null)
			{
				ViewWindow window = container.resolveViewWindow();
				if(window instanceof JInternalFrame)
				{
					putValue(Action.SMALL_ICON, detachIcon);
					putValue(Action.NAME, "Detach");
				}
				else if(window instanceof JFrame)
				{
					putValue(Action.SMALL_ICON, attachIcon);
					putValue(Action.NAME, "Attach");
				}
			}

		}
	}


	private class AttachToolBarAction
		extends AbstractAction
	{
		private static final long serialVersionUID = -6338324258055926639L;

		private Icon attachIcon = ATTACH_TOOLBAR_ICON;
		private Icon detachIcon = DETACH_TOOLBAR_ICON;

		public AttachToolBarAction()
		{
			super();
			updateAction();
		}

		public void actionPerformed(ActionEvent e)
		{
			attachDetach();
			updateAction();
		}

		public void updateAction()
		{
			ViewContainer container = getViewContainer();
			if(container != null)
			{
				ViewWindow window = container.resolveViewWindow();
				if(window instanceof JInternalFrame)
				{
					putValue(Action.SMALL_ICON, detachIcon);
					putValue(Action.SHORT_DESCRIPTION, "Detach");
					return;
				}
				else if(window instanceof JFrame)
				{
					putValue(Action.SMALL_ICON, attachIcon);
					putValue(Action.SHORT_DESCRIPTION, "Attach");
					return;
				}
			}
			// update anyway
			putValue(Action.SMALL_ICON, detachIcon);
			putValue(Action.SHORT_DESCRIPTION, "Detach");
		}
	}

	private class PauseMenuAction
		extends AbstractAction
	{
		private static final long serialVersionUID = -5242236903640590549L;

		private Icon pausedIcon = PAUSED_MENU_ICON;
		private Icon unpausedIcon = UNPAUSED_MENU_ICON;

		public PauseMenuAction()
		{
			super();
			updateAction();
			KeyStroke accelerator = KeyStrokes.resolveAcceleratorKeyStroke(KeyStrokes.COMMAND_ALIAS + " P");
			if(logger.isDebugEnabled()) logger.debug("accelerator: {}", accelerator);
			putValue(Action.ACCELERATOR_KEY, accelerator);
		}

		public void actionPerformed(ActionEvent e)
		{
			setPaused(!isPaused());
			updateAction();
			focusTable();
		}

		public void updateAction()
		{
			if(isPaused())
			{
				putValue(Action.SMALL_ICON, pausedIcon);
				putValue(Action.NAME, "Unpause");
			}
			else
			{
				putValue(Action.SMALL_ICON, unpausedIcon);
				putValue(Action.NAME, "Pause");
			}
		}
	}

	private class PauseToolBarAction
		extends AbstractAction
	{
		private static final long serialVersionUID = -5118623805829814815L;

		private Icon pausedIcon = PAUSED_TOOLBAR_ICON;
		private Icon unpausedIcon = UNPAUSED_TOOLBAR_ICON;

		public PauseToolBarAction()
		{
			super();
			updateAction();
			KeyStroke accelerator = KeyStrokes.resolveAcceleratorKeyStroke(KeyStrokes.COMMAND_ALIAS + " P");
			if(logger.isDebugEnabled()) logger.debug("accelerator: {}", accelerator);
			putValue(Action.ACCELERATOR_KEY, accelerator);
		}

		public void actionPerformed(ActionEvent e)
		{
			setPaused(!isPaused());
			updateAction();
			focusTable();
		}

		public void updateAction()
		{
			if(isPaused())
			{
				putValue(Action.SMALL_ICON, pausedIcon);
				putValue(Action.SHORT_DESCRIPTION, "Unpause");
			}
			else
			{
				putValue(Action.SMALL_ICON, unpausedIcon);
				putValue(Action.SHORT_DESCRIPTION, "Pause");
			}
		}
	}

	private class FindMenuAction
		extends AbstractAction
	{
		private static final long serialVersionUID = 2241714830900044485L;

		public FindMenuAction()
		{
			super("Find");
			putValue(Action.SMALL_ICON, FIND_MENU_ITEM);
			putValue(Action.SHORT_DESCRIPTION, "Opens the Find panel.");
			KeyStroke accelerator = KeyStrokes.resolveAcceleratorKeyStroke(KeyStrokes.COMMAND_ALIAS + " F");
			if(logger.isDebugEnabled()) logger.debug("accelerator: {}", accelerator);
			putValue(Action.ACCELERATOR_KEY, accelerator);
		}

		public void actionPerformed(ActionEvent e)
		{
			setShowingFilters(true);
		}
	}

	private class FindToolBarAction
		extends AbstractAction
	{
		private static final long serialVersionUID = -4080152597948489206L;

		public FindToolBarAction()
		{
			super();
			putValue(Action.SMALL_ICON, FIND_TOOLBAR_ICON);
			putValue(Action.SHORT_DESCRIPTION, "Find");
			KeyStroke accelerator = KeyStrokes.resolveAcceleratorKeyStroke(KeyStrokes.COMMAND_ALIAS + " F");
			if(logger.isDebugEnabled()) logger.debug("accelerator: {}", accelerator);
			putValue(Action.ACCELERATOR_KEY, accelerator);
		}

		public void actionPerformed(ActionEvent e)
		{
			setShowingFilters(true);
		}
	}

	private static class StatisticsMenuAction
		extends AbstractAction
	{
		private static final long serialVersionUID = -6336357605789928345L;

		public StatisticsMenuAction()
		{
			super("Statistics");
			putValue(Action.SMALL_ICON, STATISTICS_MENU_ICON);
			putValue(Action.SHORT_DESCRIPTION, "Statistics");
		}

		public void actionPerformed(ActionEvent e)
		{
		}
	}
/*
	private class StatisticsMenuAction
		extends AbstractAction
	{
		private static final long serialVersionUID = -6336357605789928345L;

		public StatisticsMenuAction()
		{
			super("Statistics");
			putValue(Action.SMALL_ICON, STATISTICS_MENU_ICON);
			putValue(Action.SHORT_DESCRIPTION, "Statistics");
		}

		public void actionPerformed(ActionEvent e)
		{
			showStatistics();
		}
	}

	private class StatisticsToolBarAction
		extends AbstractAction
	{
		private static final long serialVersionUID = 2394035359331601001L;

		public StatisticsToolBarAction()
		{
			super();
			putValue(Action.SMALL_ICON, STATISTICS_TOOLBAR_ICON);
			putValue(Action.SHORT_DESCRIPTION, "Statistics");
		}

		public void actionPerformed(ActionEvent e)
		{
			showStatistics();
		}
	}
*/

	private class DisconnectMenuAction
		extends AbstractAction
	{
		private static final long serialVersionUID = 8971640305824353589L;

		public DisconnectMenuAction()
		{
			super("Disconnect");
			KeyStroke accelerator = KeyStrokes.resolveAcceleratorKeyStroke(KeyStrokes.COMMAND_ALIAS + " shift D");
			if(logger.isDebugEnabled()) logger.debug("accelerator: {}", accelerator);
			putValue(Action.ACCELERATOR_KEY, accelerator);
			putValue(Action.SMALL_ICON, DISCONNECT_MENU_ICON);
			putValue(Action.SHORT_DESCRIPTION, "Terminates this connection");
		}

		public void actionPerformed(ActionEvent e)
		{
			disconnect();
		}
	}

	private class DisconnectToolBarAction
		extends AbstractAction
	{
		private static final long serialVersionUID = -8665004340745035737L;

		public DisconnectToolBarAction()
		{
			super();
			putValue(Action.SMALL_ICON, DISCONNECT_TOOLBAR_ICON);
			putValue(Action.SHORT_DESCRIPTION, "Disconnect");
		}

		public void actionPerformed(ActionEvent e)
		{
			disconnect();
		}
	}

	private class FocusMessageAction
		extends AbstractAction
	{
		private static final long serialVersionUID = -421929316399318971L;

		public FocusMessageAction()
		{
			super("Focus message");
			putValue(Action.SMALL_ICON, EMPTY_16_ICON);
			putValue(Action.SHORT_DESCRIPTION, "Focus detailed message view.");
			KeyStroke accelerator = KeyStrokes.resolveAcceleratorKeyStroke(KeyStrokes.COMMAND_ALIAS + " M");
			if(logger.isDebugEnabled()) logger.debug("accelerator: {}", accelerator);
			putValue(Action.ACCELERATOR_KEY, accelerator);
		}

		public void actionPerformed(ActionEvent e)
		{
			focusMessage();
		}
	}

	private class FocusEventsAction
		extends AbstractAction
	{
		private static final long serialVersionUID = 4207817900003297701L;

		public FocusEventsAction()
		{
			super("Focus events");
			putValue(Action.SMALL_ICON, EMPTY_16_ICON);
			putValue(Action.SHORT_DESCRIPTION, "Focus the table containing the events.");
			KeyStroke accelerator = KeyStrokes.resolveAcceleratorKeyStroke(KeyStrokes.COMMAND_ALIAS + " E");
			if(logger.isDebugEnabled()) logger.debug("accelerator: {}", accelerator);
			putValue(Action.ACCELERATOR_KEY, accelerator);
		}

		public void actionPerformed(ActionEvent e)
		{
			focusEvents();
		}
	}

	private class FindNextAction
		extends AbstractAction
	{
		private static final long serialVersionUID = 4771628062043742857L;

		public FindNextAction()
		{
			super("Find next");
			putValue(Action.SMALL_ICON, FIND_NEXT_MENU_ICON);
			putValue(Action.SHORT_DESCRIPTION, "Find next match of the current filter.");
			KeyStroke accelerator = KeyStrokes.resolveAcceleratorKeyStroke(KeyStrokes.COMMAND_ALIAS + " shift G");
			if(logger.isDebugEnabled()) logger.debug("accelerator: {}", accelerator);
			putValue(Action.ACCELERATOR_KEY, accelerator);
		}

		public void actionPerformed(ActionEvent e)
		{
			findNext();
		}

	}

	private class FindPreviousAction
		extends AbstractAction
	{
		private static final long serialVersionUID = -284066693780808511L;

		public FindPreviousAction()
		{
			super("Find previous");
			putValue(Action.SMALL_ICON, FIND_PREV_MENU_ICON);
			putValue(Action.SHORT_DESCRIPTION, "Find previous match of the current filter.");
			KeyStroke accelerator = KeyStrokes.resolveAcceleratorKeyStroke(KeyStrokes.COMMAND_ALIAS + " G");
			if(logger.isDebugEnabled()) logger.debug("accelerator: {}", accelerator);
			putValue(Action.ACCELERATOR_KEY, accelerator);
		}

		public void actionPerformed(ActionEvent e)
		{
			findPrevious();
		}
	}

	private class FindNextActiveAction
		extends AbstractAction
	{
		private static final long serialVersionUID = 8153060295931745089L;

		public FindNextActiveAction()
		{
			super("Find next active");
			putValue(Action.SMALL_ICON, FIND_NEXT_MENU_ICON);
			putValue(Action.SHORT_DESCRIPTION, "Find next match of any active condition.");
			KeyStroke accelerator = KeyStrokes.resolveAcceleratorKeyStroke(KeyStrokes.COMMAND_ALIAS + " shift T");
			if(logger.isDebugEnabled()) logger.debug("accelerator: {}", accelerator);
			putValue(Action.ACCELERATOR_KEY, accelerator);
		}

		public void actionPerformed(ActionEvent e)
		{
			findNextActive();
		}

	}

	private class FindPreviousActiveAction
		extends AbstractAction
	{
		private static final long serialVersionUID = 2473715367685180389L;

		public FindPreviousActiveAction()
		{
			super("Find previous active");
			putValue(Action.SMALL_ICON, FIND_PREV_MENU_ICON);
			putValue(Action.SHORT_DESCRIPTION, "Find previous match of any active condition.");
			KeyStroke accelerator = KeyStrokes.resolveAcceleratorKeyStroke(KeyStrokes.COMMAND_ALIAS + " T");
			if(logger.isDebugEnabled()) logger.debug("accelerator: {}", accelerator);
			putValue(Action.ACCELERATOR_KEY, accelerator);
		}

		public void actionPerformed(ActionEvent e)
		{
			findPreviousActive();
		}
	}

	private class ResetFindAction
		extends AbstractAction
	{
		private static final long serialVersionUID = -1245378100755440576L;

		public ResetFindAction()
		{
			super("Reset find");
			putValue(Action.SMALL_ICON, EMPTY_16_ICON);
			KeyStroke accelerator = KeyStrokes.resolveAcceleratorKeyStroke(KeyStrokes.COMMAND_ALIAS + " shift F");
			if(logger.isDebugEnabled()) logger.debug("accelerator: {}", accelerator);
			putValue(Action.ACCELERATOR_KEY, accelerator);
		}

		public void actionPerformed(ActionEvent e)
		{
			resetFind();
		}
	}

	private class ScrollToBottomMenuAction
		extends AbstractAction
	{
		private static final long serialVersionUID = -6698886479454486019L;

		private Icon selectedIcon = TAIL_MENU_ICON;
		private Icon unselectedIcon = EMPTY_16_ICON;

		public ScrollToBottomMenuAction()
		{
			super("Tail");
			updateAction();
			putValue(Action.SHORT_DESCRIPTION, "Tail (\"scroll to bottom\")");
			KeyStroke accelerator = KeyStrokes.resolveAcceleratorKeyStroke(KeyStrokes.COMMAND_ALIAS + " T");
			if(logger.isDebugEnabled()) logger.debug("accelerator: {}", accelerator);
			putValue(Action.ACCELERATOR_KEY, accelerator);
		}

		public void actionPerformed(ActionEvent e)
		{
			boolean tail = !isScrollingToBottom();
			setScrollingToBottom(tail);
			if(logger.isDebugEnabled()) logger.debug("tail={}", tail);
			focusTable();
		}

		public void updateAction()
		{
			if(isScrollingToBottom())
			{
				putValue(Action.SMALL_ICON, selectedIcon);
			}
			else
			{
				putValue(Action.SMALL_ICON, unselectedIcon);
			}
		}
	}

	private class ScrollToBottomToolBarAction
		extends AbstractAction
	{
		private static final long serialVersionUID = -7793074053120455264L;

		public ScrollToBottomToolBarAction()
		{
			super();
			putValue(Action.SMALL_ICON, TAIL_TOOLBAR_ICON);
			putValue(Action.SHORT_DESCRIPTION, "Tail (\"scroll to bottom\")");
			KeyStroke accelerator = KeyStrokes.resolveAcceleratorKeyStroke(KeyStrokes.COMMAND_ALIAS + " T");
			if(logger.isDebugEnabled()) logger.debug("accelerator: {}", accelerator);
			putValue(Action.ACCELERATOR_KEY, accelerator);
		}

		public void actionPerformed(ActionEvent e)
		{
			boolean tail = !isScrollingToBottom();
			setScrollingToBottom(tail);
			if(logger.isDebugEnabled()) logger.debug("tail={}", tail);
			focusTable();
		}
	}

	private class CloseFilterAction
		extends AbstractAction
	{
		private static final long serialVersionUID = -842677137302613585L;

		public CloseFilterAction()
		{
			super("Close this filter");
			putValue(Action.SMALL_ICON, EMPTY_16_ICON);
			putValue(Action.MNEMONIC_KEY, Integer.valueOf('c'));
			KeyStroke accelerator = KeyStrokes.resolveAcceleratorKeyStroke(KeyStrokes.COMMAND_ALIAS + " W");
			if(logger.isDebugEnabled()) logger.debug("accelerator: {}", accelerator);
			putValue(Action.ACCELERATOR_KEY, accelerator);
		}

		public void updateAction()
		{
			if(viewContainer != null)
			{
				int viewIndex = viewContainer.getViewIndex();
				if(viewIndex > 0)
				{
					setEnabled(true);
				}
				else
				{
					setEnabled(false);
				}
			}
			else
			{
				setEnabled(false);
			}
		}

		public void actionPerformed(ActionEvent e)
		{
			closeCurrentFilter();
		}

	}

	private class CloseOtherFiltersAction
		extends AbstractAction
	{
		private static final long serialVersionUID = -6399148183817841417L;

		public CloseOtherFiltersAction()
		{
			super("Close all other filters");
			putValue(Action.SMALL_ICON, EMPTY_16_ICON);
			putValue(Action.MNEMONIC_KEY, Integer.valueOf('o'));
			KeyStroke accelerator = KeyStrokes.resolveAcceleratorKeyStroke(KeyStrokes.COMMAND_ALIAS + " shift W");
			if(logger.isDebugEnabled()) logger.debug("accelerator: {}", accelerator);
			putValue(Action.ACCELERATOR_KEY, accelerator);
		}

		public void updateAction()
		{
			if(viewContainer != null)
			{
				int viewIndex = viewContainer.getViewIndex();
				int viewCount = viewContainer.getViewCount();
				if(viewIndex > -1 && ((viewIndex == 0 && viewCount > 1) || viewCount > 2))
				{
					setEnabled(true);
				}
				else
				{
					setEnabled(false);
				}
			}
			else
			{
				setEnabled(false);
			}
		}

		public void actionPerformed(ActionEvent e)
		{
			closeOtherFilters();
		}

	}

	private class CloseAllFiltersAction
		extends AbstractAction
	{
		private static final long serialVersionUID = 1212878326080544663L;

		public CloseAllFiltersAction()
		{
			super("Close all filters");
			putValue(Action.SMALL_ICON, EMPTY_16_ICON);
			putValue(Action.MNEMONIC_KEY, Integer.valueOf('a'));
		}

		public void updateAction()
		{
			int viewCount = 0;
			if(viewContainer != null)
			{
				viewCount = viewContainer.getViewCount();
			}
			if(viewCount > 1)
			{
				setEnabled(true);
			}
			else
			{
				setEnabled(false);
			}
		}

		public void actionPerformed(ActionEvent e)
		{
			closeAllFilters();
		}
	}


	class ViewLoggingAction
		extends AbstractAction
	{
		private static final long serialVersionUID = 6967472316665780683L;

		private EventSource<LoggingEvent> eventSource;

		public ViewLoggingAction(ViewContainer<LoggingEvent> container)
		{
			super(mainFrame.resolveSourceTitle(container));
			this.eventSource = container.getEventSource();
			if(eventSource.isGlobal())
			{
				KeyStroke accelerator = KeyStrokes.resolveAcceleratorKeyStroke(KeyStrokes.COMMAND_ALIAS + " 1");
				if(logger.isDebugEnabled()) logger.debug("accelerator: {}", accelerator);
				putValue(Action.ACCELERATOR_KEY, accelerator);
			}
			else
			{
				SourceIdentifier si = eventSource.getSourceIdentifier();
				if(si != null && "Lilith".equals(si.getIdentifier()))
				{
					// internal Lilith log
					KeyStroke accelerator = KeyStrokes.resolveAcceleratorKeyStroke(KeyStrokes.COMMAND_ALIAS + " 0");
					if(logger.isDebugEnabled()) logger.debug("accelerator: {}", accelerator);
					putValue(Action.ACCELERATOR_KEY, accelerator);
				}
			}
		}

		public void actionPerformed(ActionEvent evt)
		{
			mainFrame.showLoggingView(eventSource);
		}

	}

	class ViewAccessAction
		extends AbstractAction
	{
		private static final long serialVersionUID = 8054851261518410946L;

		private EventSource<AccessEvent> eventSource;

		public ViewAccessAction(ViewContainer<AccessEvent> container)
		{
			super(mainFrame.resolveSourceTitle(container));
			this.eventSource = container.getEventSource();
			if(eventSource.isGlobal())
			{
				KeyStroke accelerator = KeyStrokes.resolveAcceleratorKeyStroke(KeyStrokes.COMMAND_ALIAS + " 2");
				if(logger.isDebugEnabled()) logger.debug("accelerator: {}", accelerator);
				putValue(Action.ACCELERATOR_KEY, accelerator);
			}
		}

		public void actionPerformed(ActionEvent evt)
		{
			mainFrame.showAccessView(eventSource);
		}

	}

	class ViewStatisticsAction
		extends AbstractAction
	{
		private static final long serialVersionUID = 4453230971326526165L;

		private SourceIdentifier sourceIentifier;

		public ViewStatisticsAction(String name, SourceIdentifier sourceIdentifier)
		{
			super(name);
			this.sourceIentifier = sourceIdentifier;
		}

		public void actionPerformed(ActionEvent e)
		{
			mainFrame.showStatistics(sourceIentifier);
		}
	}

/*
	static class StatisticsSubMenuAction
		extends AbstractAction
	{
		private static final long serialVersionUID = -8180660223848656769L;

		public StatisticsSubMenuAction()
		{
			super("Statistics");
			putValue(Action.SMALL_ICON, STATISTICS_MENU_ICON);
		}

		public void actionPerformed(ActionEvent e)
		{
		}
	}
*/

	class UpdateWindowMenuRunnable
		implements Runnable
	{
		private JMenu windowMenu;

		public UpdateWindowMenuRunnable(JMenu windowMenu)
		{
			this.windowMenu = windowMenu;
		}

		public void run()
		{
			// remove loggingViews that were closed in the meantime...
			mainFrame.removeInactiveViews(true, false);
			if(logger.isDebugEnabled()) logger.debug("Updating Views-Menu.");

			windowMenu.removeAll();
			JMenu statisticsMenu = createStatisticsMenu();
			windowMenu.add(statisticsMenu);
			windowMenu.add(showTaskManagerItem);
			windowMenu.addSeparator();
			windowMenu.add(closeAllItem);
			windowMenu.add(closeAllOtherItem);
			windowMenu.add(minimizeAllItem);
			windowMenu.add(minimizeAllOtherItem);
			windowMenu.add(removeInactiveItem);
//			windowMenu.add(clearAndRemoveInactiveItem);

			int activeCounter = 0;
			int inactiveCounter = 0;
			int viewCounter = 0;
			Font inactiveFont = windowMenu.getFont();
			inactiveFont = inactiveFont.deriveFont(Font.PLAIN);

			boolean first;

			SortedMap<EventSource<LoggingEvent>, ViewContainer<LoggingEvent>> sortedLoggingViews = mainFrame
				.getSortedLoggingViews();

			SortedMap<EventSource<AccessEvent>, ViewContainer<AccessEvent>> sortedAccessViews = mainFrame
				.getSortedAccessViews();

			first = true;
			// Lilith logging
			for(Map.Entry<EventSource<LoggingEvent>, ViewContainer<LoggingEvent>> entry : sortedLoggingViews.entrySet())
			{
				EventSource<LoggingEvent> key = entry.getKey();
				SourceIdentifier si = key.getSourceIdentifier();
				if("Lilith".equals(si.getIdentifier()))
				{
					ViewContainer<LoggingEvent> value = entry.getValue();
					if(value.resolveViewWindow() != null)
					{
						viewCounter++;
					}
					if(first)
					{
						first = false;
						windowMenu.addSeparator();
					}
					JMenuItem menuItem = createLoggingMenuItem(key, value);
					windowMenu.add(menuItem);
				}
			}
			// global (Logging)
			for(Map.Entry<EventSource<LoggingEvent>, ViewContainer<LoggingEvent>> entry : sortedLoggingViews.entrySet())
			{
				EventSource<LoggingEvent> key = entry.getKey();
				SourceIdentifier si = key.getSourceIdentifier();
				if(!"Lilith".equals(si.getIdentifier()))
				{
					ViewContainer<LoggingEvent> value = entry.getValue();
					if(value.resolveViewWindow() != null)
					{
						viewCounter++;
					}
					if(key.isGlobal())
					{
						if(first)
						{
							first = false;
							windowMenu.addSeparator();
						}
						JMenuItem menuItem = createLoggingMenuItem(key, value);
						windowMenu.add(menuItem);
					}
				}
			}
			// global (Access)
			for(Map.Entry<EventSource<AccessEvent>, ViewContainer<AccessEvent>> entry : sortedAccessViews.entrySet())
			{
				EventSource<AccessEvent> key = entry.getKey();
				ViewContainer<AccessEvent> value = entry.getValue();
				if(value.resolveViewWindow() != null)
				{
					viewCounter++;
				}
				if(key.isGlobal())
				{
					if(first)
					{
						first = false;
						windowMenu.addSeparator();
					}
					JMenuItem menuItem = createAccessMenuItem(key, value);
					windowMenu.add(menuItem);
				}
			}

			first = true;
			// Logging (active)
			for(Map.Entry<EventSource<LoggingEvent>, ViewContainer<LoggingEvent>> entry : sortedLoggingViews.entrySet())
			{
				EventSource<LoggingEvent> key = entry.getKey();
				SourceIdentifier si = key.getSourceIdentifier();
				if(!"Lilith".equals(si.getIdentifier()))
				{
					ViewContainer<LoggingEvent> value = entry.getValue();
					EventWrapperViewPanel<LoggingEvent> panel = value.getDefaultView();
					if(!key.isGlobal() && (LoggingViewState.ACTIVE == panel.getState()))
					{
						if(first)
						{
							first = false;
							windowMenu.addSeparator();
						}
						JMenuItem menuItem = createLoggingMenuItem(key, value);
						windowMenu.add(menuItem);
						activeCounter++;
					}
				}
			}
			// Logging (inactive)
			for(Map.Entry<EventSource<LoggingEvent>, ViewContainer<LoggingEvent>> entry : sortedLoggingViews.entrySet())
			{
				EventSource<LoggingEvent> key = entry.getKey();
				SourceIdentifier si = key.getSourceIdentifier();
				if(!"Lilith".equals(si.getIdentifier()))
				{
					ViewContainer<LoggingEvent> value = entry.getValue();
					EventWrapperViewPanel<LoggingEvent> panel = value.getDefaultView();
					if(!key.isGlobal() && (LoggingViewState.ACTIVE != panel.getState()))
					{
						if(first)
						{
							first = false;
							windowMenu.addSeparator();
						}
						JMenuItem menuItem = createLoggingMenuItem(key, value);
						menuItem.setFont(inactiveFont);
						windowMenu.add(menuItem);
						inactiveCounter++;
					}
				}
			}

			// Access (active)
			first = true;
			for(Map.Entry<EventSource<AccessEvent>, ViewContainer<AccessEvent>> entry : sortedAccessViews.entrySet())
			{
				EventSource<AccessEvent> key = entry.getKey();
				ViewContainer<AccessEvent> value = entry.getValue();
				EventWrapperViewPanel<AccessEvent> panel = value.getDefaultView();
				if(!key.isGlobal() && (LoggingViewState.ACTIVE == panel.getState()))
				{
					if(first)
					{
						first = false;
						windowMenu.addSeparator();
					}
					JMenuItem menuItem = createAccessMenuItem(key, value);
					windowMenu.add(menuItem);
					activeCounter++;
				}
			}
			// Access (inactive)
			for(Map.Entry<EventSource<AccessEvent>, ViewContainer<AccessEvent>> entry : sortedAccessViews.entrySet())
			{
				EventSource<AccessEvent> key = entry.getKey();
				ViewContainer<AccessEvent> value = entry.getValue();
				EventWrapperViewPanel<AccessEvent> panel = value.getDefaultView();
				if(!key.isGlobal() && (LoggingViewState.ACTIVE != panel.getState()))
				{
					if(first)
					{
						first = false;
						windowMenu.addSeparator();
					}
					JMenuItem menuItem = createAccessMenuItem(key, value);
					menuItem.setFont(inactiveFont);
					windowMenu.add(menuItem);
					inactiveCounter++;
				}
			}

			// update status text
			boolean hasInactive = (inactiveCounter != 0);
			//clearAndRemoveInactiveAction.setEnabled(hasInactive);
			removeInactiveAction.setEnabled(hasInactive);
			boolean hasViews = viewCounter != 0;
			minimizeAllAction.setEnabled(hasViews);
			closeAllAction.setEnabled(hasViews);
			if(viewContainer == null || viewCounter <= 1)
			{
				minimizeAllOtherAction.setEnabled(false);
				closeOtherAction.setEnabled(false);
			}
			else
			{
				minimizeAllOtherAction.setEnabled(true);
				closeOtherAction.setEnabled(true);
			}

			mainFrame.setActiveConnectionsCounter(activeCounter);

			if(windowMenu.isPopupMenuVisible())
			{
				// I've not been able to find a more elegant solution to prevent
				// repaint artifacts if the menu contents change while the menu is still open...
				windowMenu.setPopupMenuVisible(false);
				windowMenu.setPopupMenuVisible(true);
			}
		}


		private JMenuItem createLoggingMenuItem(EventSource<LoggingEvent> key, ViewContainer<LoggingEvent> viewContainer)
		{
			JMenuItem result = new JMenuItem(new ViewLoggingAction(viewContainer));
			Container compParent = viewContainer.getParent();
			if(logger.isDebugEnabled()) logger.debug("\n\nParent for {}: {}\n", key.getSourceIdentifier(), compParent);
			if(compParent == null)
			{
				result.setIcon(EMPTY_16_ICON);
			}
			else
			{
				result.setIcon(WINDOW_16_ICON);
			}
			return result;
		}

		private JMenuItem createAccessMenuItem(EventSource<AccessEvent> key, ViewContainer<AccessEvent> viewContainer)
		{
			JMenuItem result = new JMenuItem(new ViewAccessAction(viewContainer));
			Container compParent = viewContainer.getParent();
			if(logger.isDebugEnabled()) logger.debug("\n\nParent for {}: {}\n", key.getSourceIdentifier(), compParent);
			if(compParent == null)
			{
				result.setIcon(EMPTY_16_ICON);
			}
			else
			{
				result.setIcon(WINDOW_16_ICON);
			}
			return result;
		}

		private JMenu createStatisticsMenu()
		{
			JMenu result = new JMenu(new StatisticsMenuAction());
			SortedMap<String, SourceIdentifier> sources = mainFrame.getAvailableStatistics();

			{
				JMenuItem menuItem = new JMenuItem(new ViewStatisticsAction("Global", new SourceIdentifier("global")));
				result.add(menuItem);
				result.addSeparator();
			}

			for(Map.Entry<String, SourceIdentifier> current : sources.entrySet())
			{
				String key = current.getKey();
				SourceIdentifier value = current.getValue();

				JMenuItem menuItem = new JMenuItem(new ViewStatisticsAction(key, value));
				result.add(menuItem);
			}
			return result;
		}
	}


	class AboutAction
		extends AbstractAction
	{
		private static final long serialVersionUID = -372250750198620913L;

		public AboutAction()
		{
			super("About...");
			putValue(Action.SMALL_ICON, EMPTY_16_ICON);
		}

		public void actionPerformed(ActionEvent e)
		{
			mainFrame.showAboutDialog();
		}
	}

	class SaveLayoutAction
		extends AbstractAction
	{
		private static final long serialVersionUID = 6135867758474252484L;

		public SaveLayoutAction()
		{
			super("Save layout");
			putValue(Action.SMALL_ICON, EMPTY_16_ICON);
		}

		public void actionPerformed(ActionEvent e)
		{
			if(viewContainer != null)
			{
				EventWrapperViewPanel<?> viewPanel = viewContainer.getSelectedView();
				if(viewPanel != null)
				{
					EventWrapperViewTable<?> table = viewPanel.getTable();
					if(table != null)
					{
						table.saveLayout();
					}
				}
			}
		}
	}

	class ResetLayoutAction
		extends AbstractAction
	{
		private static final long serialVersionUID = -8396518428359553649L;

		public ResetLayoutAction()
		{
			super("Reset layout");
			putValue(Action.SMALL_ICON, EMPTY_16_ICON);
		}

		public void actionPerformed(ActionEvent e)
		{
			if(viewContainer != null)
			{
				EventWrapperViewPanel<?> viewPanel = viewContainer.getSelectedView();
				if(viewPanel != null)
				{
					EventWrapperViewTable<?> table = viewPanel.getTable();
					if(table != null)
					{
						table.resetLayout();
					}
				}
			}
		}
	}

	class CheckForUpdateAction
		extends AbstractAction
	{
		private static final long serialVersionUID = 529742851501771901L;

		public CheckForUpdateAction()
		{
			super("Check for Update...");
			putValue(Action.SMALL_ICON, CHECK_UPDATE_ICON);
		}

		public void actionPerformed(ActionEvent e)
		{
			mainFrame.checkForUpdate(true);
		}
	}

	class TroubleshootingAction
		extends AbstractAction
	{
		private static final long serialVersionUID = 529742851501771901L;

		public TroubleshootingAction()
		{
			super("Troubleshooting...");
			putValue(Action.SMALL_ICON, EMPTY_16_ICON);
		}

		public void actionPerformed(ActionEvent e)
		{
			mainFrame.troubleshooting();
		}
	}

	class KeyboardHelpAction
		extends AbstractAction
	{
		private static final long serialVersionUID = 6942092383339768508L;

		public KeyboardHelpAction()
		{
			super("Help Topics");
			putValue(Action.SMALL_ICON, HELP_MENU_ICON);
			KeyStroke accelerator = KeyStrokes.resolveAcceleratorKeyStroke("F1");
			if(logger.isDebugEnabled()) logger.debug("accelerator: {}", accelerator);
			putValue(Action.ACCELERATOR_KEY, accelerator);

		}

		public void actionPerformed(ActionEvent e)
		{
			mainFrame.showHelp();
		}
	}

	class TipOfTheDayAction
		extends AbstractAction
	{
		private static final long serialVersionUID = -3703967582739382172L;

		public TipOfTheDayAction()
		{
			super("Tip of the Day...");
			putValue(Action.SMALL_ICON, TOTD_ICON);
		}

		public void actionPerformed(ActionEvent e)
		{
			mainFrame.showTipOfTheDayDialog();
		}
	}

	class PreferencesMenuAction
		extends AbstractAction
	{
		private static final long serialVersionUID = -196036112324455446L;

		public PreferencesMenuAction()
		{
			super("Preferences...");
			putValue(Action.SMALL_ICON, PREFERENCES_MENU_ICON);
			KeyStroke accelerator = KeyStrokes.resolveAcceleratorKeyStroke(KeyStrokes.COMMAND_ALIAS + " COMMA");
			if(logger.isDebugEnabled()) logger.debug("accelerator: {}", accelerator);
			putValue(Action.ACCELERATOR_KEY, accelerator);
			putValue(Action.MNEMONIC_KEY, Integer.valueOf('p'));
		}

		public void actionPerformed(ActionEvent e)
		{
			mainFrame.showPreferencesDialog();
		}
	}

	class PreferencesToolBarAction
		extends AbstractAction
	{
		private static final long serialVersionUID = 8353604009441967874L;

		public PreferencesToolBarAction()
		{
			super();
			putValue(Action.SMALL_ICON, PREFERENCES_TOOLBAR_ICON);
			putValue(Action.SHORT_DESCRIPTION, "Preferences...");
			KeyStroke accelerator = KeyStrokes.resolveAcceleratorKeyStroke(KeyStrokes.COMMAND_ALIAS + " COMMA");
			if(logger.isDebugEnabled()) logger.debug("accelerator: {}", accelerator);
			putValue(Action.ACCELERATOR_KEY, accelerator);
			putValue(Action.MNEMONIC_KEY, Integer.valueOf('p'));
		}

		public void actionPerformed(ActionEvent e)
		{
			mainFrame.showPreferencesDialog();
		}
	}

	class DebugAction
		extends AbstractAction
	{
		private static final long serialVersionUID = -1837786931224404611L;

		public DebugAction()
		{
			super("Debug");
			putValue(Action.SMALL_ICON, EMPTY_16_ICON);
		}

		public void actionPerformed(ActionEvent e)
		{
			mainFrame.showDebugDialog();
		}
	}

	class ExitMenuAction
		extends AbstractAction
	{
		private static final long serialVersionUID = 6693131597277483031L;

		public ExitMenuAction()
		{
			super("Exit");
			putValue(Action.SMALL_ICON, EXIT_MENU_ICON);
			KeyStroke accelerator = KeyStrokes.resolveAcceleratorKeyStroke(KeyStrokes.COMMAND_ALIAS + " Q");
			if(logger.isDebugEnabled()) logger.debug("accelerator: {}", accelerator);
			putValue(Action.ACCELERATOR_KEY, accelerator);
			putValue(Action.MNEMONIC_KEY, Integer.valueOf('x'));
		}

		public void actionPerformed(ActionEvent e)
		{
			mainFrame.exit();
		}
	}

	class OpenInactiveLogMenuAction
		extends AbstractAction
	{
		private static final long serialVersionUID = 7500131416548647712L;

		public OpenInactiveLogMenuAction()
		{
			super("Open inactive log...");
			putValue(Action.SMALL_ICON, OPEN_INACTIVE_MENU_ICON);
			KeyStroke accelerator = KeyStrokes.resolveAcceleratorKeyStroke(KeyStrokes.COMMAND_ALIAS + " shift O");
			if(logger.isDebugEnabled()) logger.debug("accelerator: {}", accelerator);
			putValue(Action.ACCELERATOR_KEY, accelerator);
			putValue(Action.MNEMONIC_KEY, Integer.valueOf('o'));
		}

		public void actionPerformed(ActionEvent e)
		{
			mainFrame.openInactiveLogs();
		}
	}

	class OpenMenuAction
		extends AbstractAction
	{
		private static final long serialVersionUID = 7500131416548647712L;

		public OpenMenuAction()
		{
			super("Open...");
			putValue(Action.SMALL_ICON, OPEN_INACTIVE_MENU_ICON);
			KeyStroke accelerator = KeyStrokes.resolveAcceleratorKeyStroke(KeyStrokes.COMMAND_ALIAS + " O");
			if(logger.isDebugEnabled()) logger.debug("accelerator: {}", accelerator);
			putValue(Action.ACCELERATOR_KEY, accelerator);
			putValue(Action.MNEMONIC_KEY, Integer.valueOf('o'));
		}

		public void actionPerformed(ActionEvent e)
		{
			mainFrame.open();
		}
	}

	class ImportMenuAction
		extends AbstractAction
	{
		private static final long serialVersionUID = 7500131416548647712L;

		public ImportMenuAction()
		{
			super("Import...");
			putValue(Action.SMALL_ICON, OPEN_INACTIVE_MENU_ICON);
			KeyStroke accelerator = KeyStrokes.resolveAcceleratorKeyStroke(KeyStrokes.COMMAND_ALIAS + " shift I");
			if(logger.isDebugEnabled()) logger.debug("accelerator: {}", accelerator);
			putValue(Action.ACCELERATOR_KEY, accelerator);
			putValue(Action.MNEMONIC_KEY, Integer.valueOf('i'));
		}

		public void actionPerformed(ActionEvent e)
		{
			mainFrame.importFile();
		}
	}

	class ExportMenuAction
		extends AbstractAction
	{
		private static final long serialVersionUID = -5912177735718627089L;

		private EventWrapperViewPanel view;

		public ExportMenuAction()
		{
			super("Export...");
			putValue(Action.SMALL_ICON, EXPORT_MENU_ICON);
			KeyStroke accelerator = KeyStrokes.resolveAcceleratorKeyStroke(KeyStrokes.COMMAND_ALIAS + " shift E");
			if(logger.isDebugEnabled()) logger.debug("accelerator: {}", accelerator);
			putValue(Action.ACCELERATOR_KEY, accelerator);
			putValue(Action.MNEMONIC_KEY, Integer.valueOf('e'));
		}

		public void actionPerformed(ActionEvent e)
		{
			mainFrame.exportFile(view);
		}

		public void setView(EventWrapperViewPanel eventWrapperViewPanel)
		{
			this.view=eventWrapperViewPanel;
			setEnabled(view != null);
		}
	}

	class CleanAllInactiveLogsMenuAction
		extends AbstractAction
	{
		private static final long serialVersionUID = 626049491764655228L;

		public CleanAllInactiveLogsMenuAction()
		{
			super("Clean all inactive logs");
			putValue(Action.SMALL_ICON, CLEAR_MENU_ICON);
			KeyStroke accelerator = KeyStrokes.resolveAcceleratorKeyStroke(KeyStrokes.COMMAND_ALIAS + " shift X");
			if(logger.isDebugEnabled()) logger.debug("accelerator: {}", accelerator);
			putValue(Action.ACCELERATOR_KEY, accelerator);
			putValue(Action.MNEMONIC_KEY, Integer.valueOf('c'));
		}

		public void actionPerformed(ActionEvent e)
		{
			if(logger.isInfoEnabled()) logger.info("Clean all inactive logs");
			mainFrame.cleanAllInactiveLogs();
		}
	}

	private class PreviousTabAction
		extends AbstractAction
	{
		private static final long serialVersionUID = 3841435361964210123L;

		public PreviousTabAction()
		{
			super("Previous tab");
			KeyStroke accelerator = KeyStrokes.resolveAcceleratorKeyStroke(KeyStrokes.COMMAND_ALIAS + " J");
			if(logger.isDebugEnabled()) logger.debug("accelerator: {}", accelerator);
			putValue(Action.ACCELERATOR_KEY, accelerator);
			putValue(Action.SMALL_ICON, EMPTY_16_ICON);
		}

		public void updateAction()
		{
			if(viewContainer != null)
			{
				int viewCount = viewContainer.getViewCount();
				if(viewCount > 1)
				{
					setEnabled(true);
				}
				else
				{
					setEnabled(false);
				}
			}
			else
			{
				setEnabled(false);
			}
		}

		public void actionPerformed(ActionEvent e)
		{
			previousTab();
		}
	}

	private class NextTabAction
		extends AbstractAction
	{
		private static final long serialVersionUID = 6997026628818486446L;

		public NextTabAction()
		{
			super("Next tab");
			KeyStroke accelerator = KeyStrokes.resolveAcceleratorKeyStroke(KeyStrokes.COMMAND_ALIAS + " K");
			if(logger.isDebugEnabled()) logger.debug("accelerator: {}", accelerator);
			putValue(Action.ACCELERATOR_KEY, accelerator);
			putValue(Action.SMALL_ICON, EMPTY_16_ICON);
		}

		public void updateAction()
		{
			if(viewContainer != null)
			{
				int viewCount = viewContainer.getViewCount();
				if(viewCount > 1)
				{
					setEnabled(true);
				}
				else
				{
					setEnabled(false);
				}
			}
			else
			{
				setEnabled(false);
			}
		}

		public void actionPerformed(ActionEvent e)
		{
			nextTab();
		}
	}


	private class CopySelectionAction
		extends AbstractAction
	{
		private static final long serialVersionUID = -551520865313383753L;

		private EventWrapperViewPanel view;

		public CopySelectionAction()
		{
			super("Copy selection");
			putValue(Action.SHORT_DESCRIPTION, "Copies the selection to the clipboard.");
			KeyStroke accelerator = KeyStrokes.resolveAcceleratorKeyStroke(KeyStrokes.COMMAND_ALIAS + " C");
			if(logger.isDebugEnabled()) logger.debug("accelerator: {}", accelerator);
			putValue(Action.ACCELERATOR_KEY, accelerator);
		}

		public void actionPerformed(ActionEvent e)
		{
			if(view != null)
			{
				view.copySelection();
			}
		}

		public void setView(EventWrapperViewPanel view)
		{
			this.view = view;
		}
	}


	private class EventFormatter
		implements ClipboardFormatter
	{
		private static final long serialVersionUID = 2263706767713579277L;

		public String getName()
		{
			return "Copy event";
		}

		public String getDescription()
		{
			return "Copies the HTML code of this events details view to the clipboard.";
		}

		public String getAccelerator()
		{
			return null;
		}

		public boolean isCompatible(Object object)
		{
			if(object instanceof EventWrapper)
			{
				EventWrapper wrapper = (EventWrapper) object;
				Object eventObj = wrapper.getEvent();
				return eventObj instanceof LoggingEvent || eventObj instanceof AccessEvent;
			}
			return false;
		}

		public String toString(Object object)
		{
			if(object instanceof EventWrapper)
			{
				EventWrapper wrapper = (EventWrapper) object;
				return mainFrame.createMessage(wrapper);
			}
			return null;
		}
	}

	private class EventJsonFormatter
		implements ClipboardFormatter
	{
		private static final long serialVersionUID = 2263706767713579277L;

		private LoggingJsonEncoder encoder = new LoggingJsonEncoder(false, true);

		public String getName()
		{
			return "Copy event as JSON";
		}

		public String getDescription()
		{
			return "Copies the JSON representation of the event to the clipboard.";
		}

		public String getAccelerator()
		{
			return null;
		}

		public boolean isCompatible(Object object)
		{
			if(object instanceof EventWrapper)
			{
				EventWrapper wrapper = (EventWrapper) object;
				Object eventObj = wrapper.getEvent();
				return eventObj instanceof LoggingEvent;
			}
			return false;
		}

		public String toString(Object object)
		{
			if(object instanceof EventWrapper)
			{
				EventWrapper wrapper = (EventWrapper) object;
				Serializable ser = wrapper.getEvent();
				if(ser instanceof LoggingEvent)
				{
					LoggingEvent event = (LoggingEvent) ser;
					byte[] bytes = encoder.encode(event);
					try
					{
						return new String(bytes, "UTF-8");
					}
					catch(UnsupportedEncodingException e)
					{
						if(logger.isErrorEnabled()) logger.error("Couldn't create UTF-8 string!", e);
					}
				}
			}
			return null;
		}
	}

	private class EventXmlFormatter
		implements ClipboardFormatter
	{
		private static final long serialVersionUID = 2263706767713579277L;

		private LoggingXmlEncoder encoder = new LoggingXmlEncoder(false, true);

		public String getName()
		{
			return "Copy event as XML";
		}

		public String getDescription()
		{
			return "Copies the XML representation of the event to the clipboard.";
		}

		public String getAccelerator()
		{
			return null;
		}

		public boolean isCompatible(Object object)
		{
			if(object instanceof EventWrapper)
			{
				EventWrapper wrapper = (EventWrapper) object;
				Object eventObj = wrapper.getEvent();
				return eventObj instanceof LoggingEvent;
			}
			return false;
		}

		public String toString(Object object)
		{
			if(object instanceof EventWrapper)
			{
				EventWrapper wrapper = (EventWrapper) object;
				Serializable ser = wrapper.getEvent();
				if(ser instanceof LoggingEvent)
				{
					LoggingEvent event = (LoggingEvent) ser;
					byte[] bytes = encoder.encode(event);
					try
					{
						return new String(bytes, "UTF-8");
					}
					catch(UnsupportedEncodingException e)
					{
						if(logger.isErrorEnabled()) logger.error("Couldn't create UTF-8 string!", e);
					}
				}
			}
			return null;
		}
	}

	private class CopyToClipboardAction
		extends AbstractAction
	{
		private static final long serialVersionUID = 7832452126107208925L;

		private ClipboardFormatter clipboardFormatter;
		private transient EventWrapper wrapper;

		public CopyToClipboardAction()
		{
		}

		private CopyToClipboardAction(ClipboardFormatter clipboardFormatter)
		{
			setClipboardFormatter(clipboardFormatter);
		}

		public ClipboardFormatter getClipboardFormatter()
		{
			return clipboardFormatter;
		}

		public void setClipboardFormatter(ClipboardFormatter clipboardFormatter)
		{
			if(clipboardFormatter == null)
			{
				throw new IllegalArgumentException("clipboardFormatter must not be null!");
			}
			this.clipboardFormatter = clipboardFormatter;
			putValue(Action.NAME, clipboardFormatter.getName());
			putValue(Action.SHORT_DESCRIPTION, clipboardFormatter.getDescription());
			String acc = clipboardFormatter.getAccelerator();
			if(acc != null)
			{
				KeyStroke accelerator= KeyStrokes.resolveAcceleratorKeyStroke(acc);
				if(logger.isDebugEnabled()) logger.debug("accelerator for '{}': {}", acc, accelerator);

				if(accelerator != null)
				{
					putValue(Action.ACCELERATOR_KEY, accelerator);
				}
				else
				{
					if(logger.isWarnEnabled()) logger.warn("'{}' did not represent a valid KeyStroke!", acc);
				}
			}
		}

		public void setEventWrapper(EventWrapper wrapper)
		{
			if(clipboardFormatter == null)
			{
				throw new IllegalStateException("clipboardFormatter must not be null!");
			}

			setEnabled(clipboardFormatter.isCompatible(wrapper));
			this.wrapper = wrapper;
		}

		public void actionPerformed(ActionEvent e)
		{
			if(clipboardFormatter == null)
			{
				throw new IllegalStateException("clipboardFormatter must not be null!");
			}
			String text = clipboardFormatter.toString(this.wrapper);
			if(text != null)
			{
				mainFrame.copyText(text);
			}
		}
	}

	private class ShowUnfilteredEventAction
		extends AbstractAction
	{
		private static final long serialVersionUID = -3282222163767568550L;

		public ShowUnfilteredEventAction()
		{
			super("Show unfiltered");
			putValue(Action.SHORT_DESCRIPTION, "Show unfiltered event.");
		}

		public void actionPerformed(ActionEvent e)
		{
			showUnfilteredEvent();
		}

	}

	private class GotoSourceAction
		extends AbstractAction
	{
		private static final long serialVersionUID = 4284532761807647658L;
		private ExtendedStackTraceElement stackTraceElement;

		public GotoSourceAction()
		{
			super("Go to source");
			putValue(Action.SHORT_DESCRIPTION, "Show source in IDEA if everything is ok ;)");
		}

		public void setEventWrapper(EventWrapper wrapper)
		{
			if(wrapper == null)
			{
				setStackTraceElement(null);
				return;
			}
			Serializable event = wrapper.getEvent();
			if(event instanceof LoggingEvent)
			{
				LoggingEvent loggingEvent = (LoggingEvent) event;
				ExtendedStackTraceElement[] callStack = loggingEvent.getCallStack();
				if(callStack != null && callStack.length > 0)
				{
					setStackTraceElement(callStack[0]);
					return;
				}
			}
			setStackTraceElement(null);
		}

		public void setStackTraceElement(ExtendedStackTraceElement stackTraceElement)
		{
			this.stackTraceElement = stackTraceElement;
			setEnabled(this.stackTraceElement != null);
		}

		public void actionPerformed(ActionEvent e)
		{
			mainFrame.goToSource(stackTraceElement.getStackTraceElement());
		}
	}

	protected static class SendAction<T extends Serializable>
		extends AbstractAction
	{
		private static final long serialVersionUID = 6612401555757959404L;
		private EventSender<T> sender;
		private T event;

		public SendAction(String name, EventSender<T> sender, EventWrapper<T> wrapper)
		{
			super(name);
			this.sender = sender;
			this.event = wrapper.getEvent();
			setEnabled(event != null);
		}

		/**
		 * Invoked when an action occurs.
		 */
		public void actionPerformed(ActionEvent e)
		{
			sender.send(event);
		}
	}

	private static class ShowHideAction
		extends AbstractAction
	{
		private static final long serialVersionUID = 7775753128032553866L;
		private boolean visible;
		private String columnName;
		private PersistentTableColumnModel tableColumnModel;

		public ShowHideAction(PersistentTableColumnModel tableColumnModel, String columnName, boolean visible)
		{
			super(columnName);
			this.columnName = columnName;
			this.visible = visible;
			this.tableColumnModel = tableColumnModel;
			//putValue(ViewActions.SELECTED_KEY, visible);
			// selection must be set manually
		}

		public void actionPerformed(ActionEvent e)
		{
			visible = !visible;
			Iterator<TableColumn> iter = tableColumnModel.getColumns(false);
			TableColumn found = null;
			while(iter.hasNext())
			{
				TableColumn current = iter.next();
				if(columnName.equals(current.getIdentifier()))
				{
					found = current;
					break;
				}
			}
			if(found != null)
			{
				tableColumnModel.setColumnVisible(found, visible);
			}
		}
	}

	private class FocusAction
		extends AbstractAction
	{
		private String loggerNamePart;

		public FocusAction(String loggerNamePart)
		{
			super(loggerNamePart);
			this.loggerNamePart = loggerNamePart;
		}

		@SuppressWarnings({"unchecked"})
		public void actionPerformed(ActionEvent e)
		{
			if(logger.isInfoEnabled()) logger.info("Focus '{}'.", loggerNamePart);
			if(viewContainer == null)
			{
				return;
			}

			EventWrapperViewPanel selectedView = viewContainer.getSelectedView();
			if(selectedView == null)
			{
				return;
			}

			Condition previousCondition = selectedView.getBufferCondition();

			Condition filter = selectedView.getCombinedCondition(new LoggerStartsWithCondition(loggerNamePart));
			if (filter == null || filter.equals(previousCondition))
			{
				return;
			}

			viewContainer.replaceFilteredView(selectedView, filter);
		}
	}

	private class ExcludeAction
		extends AbstractAction
	{
		private String loggerNamePart;

		public ExcludeAction(String loggerNamePart)
		{
			super(loggerNamePart);
			this.loggerNamePart = loggerNamePart;
		}

		@SuppressWarnings({"unchecked"})
		public void actionPerformed(ActionEvent e)
		{
			if(logger.isInfoEnabled()) logger.info("Exclude '{}'.", loggerNamePart);
			if(viewContainer == null)
			{
				return;
			}

			EventWrapperViewPanel selectedView = viewContainer.getSelectedView();
			if(selectedView == null)
			{
				return;
			}

			Condition previousCondition = selectedView.getBufferCondition();

			Condition filter = selectedView.getCombinedCondition(new Not(new LoggerStartsWithCondition(loggerNamePart)));
			if (filter == null || filter.equals(previousCondition))
			{
				return;
			}

			viewContainer.replaceFilteredView(selectedView, filter);
		}
	}

	private static class CopyToClipboardByNameComparator
		implements Comparator<CopyToClipboardAction>
	{
		public static final CopyToClipboardByNameComparator INSTANCE = new CopyToClipboardByNameComparator();

		public int compare(CopyToClipboardAction o1, CopyToClipboardAction o2)
		{
			if(o1 == o2)
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
			ClipboardFormatter f1 = o1.getClipboardFormatter();
			ClipboardFormatter f2 = o2.getClipboardFormatter();
			if(f1 == f2)
			{
				return 0;
			}
			if(f1 == null)
			{
				return -1;
			}
			if(f2 == null)
			{
				return 1;
			}
			String n1 = f1.getName();
			String n2 = f2.getName();
			//noinspection StringEquality
			if(n1 == n2)
			{
				return 0;
			}
			if(n1 == null)
			{
				return -1;
			}
			if(n2 == null)
			{
				return 1;
			}

			return n1.compareTo(n2);
		}
	}
}
