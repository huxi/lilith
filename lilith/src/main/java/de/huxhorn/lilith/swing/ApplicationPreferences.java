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
package de.huxhorn.lilith.swing;

import de.huxhorn.lilith.LilithSounds;
import de.huxhorn.sulky.conditions.Condition;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import java.net.URL;
import java.net.MalformedURLException;

public class ApplicationPreferences
{
	private static final String DETAILS_VIEW_ROOT_FOLDER = "detailsView";
	public static final String DETAILS_VIEW_CSS_FILENAME = "detailsView.css";
	public static final String DETAILS_VIEW_GROOVY_FILENAME = "detailsView.groovy";
	private static final String CONDITIONS_XML_FILENAME = "conditions.xml";
	private static final String CONDITIONS_PROPERTY = "conditions";
	private File detailsViewRoot;

	public static enum SourceFiltering
	{
		NONE, BLACKLIST, WHITELIST
	}

	private static final Preferences PREFERENCES =Preferences.userNodeForPackage(ApplicationPreferences.class);

	public static final String SHOWING_IDENTIFIER_PROPERTY = "showingIdentifier";
	public static final String CHECKING_FOR_UPDATE_PROPERTY = "checkingForUpdate";
	public static final String SOURCE_FILTERING_PROPERTY = "sourceFiltering";
	public static final String SOUND_LOCATIONS_PROPERTY = "soundLocations";
	public static final String MUTE_PROPERTY = "mute";
	public static final String USING_INTERNAL_FRAMES_PROPERTY = "usingInternalFrames";
	public static final String SCROLLING_TO_BOTTOM_PROPERTY = "scrollingToBottom";
	public static final String SOURCE_NAMES_PROPERTY = "sourceNames";
	public static final String APPLICATION_PATH_PROPERTY = "applicationPath";
	public static final String AUTO_OPENING_PROPERTY = "autoOpening";
	public static final String AUTO_CLOSING_PROPERTY = "autoClosing";
	public static final String IMAGE_PATH_PROPERTY = "imagePath";
	public static final String SOUND_PATH_PROPERTY = "soundPath";
	public static final String AUTO_FOCUSING_WINDOW_PROPERTY = "autoFocusingWindow";
	public static final String SOURCE_LISTS_PROPERTY = "sourceLists";
	public static final String BLACK_LIST_NAME_PROPERTY = "blackListName";
	public static final String WHITE_LIST_NAME_PROPERTY = "whiteListName";


	public static final String SOURCE_NAMES_XML_FILENAME = "SourceNames.xml";
	public static final String SOURCE_LISTS_XML_FILENAME = "SourceLists.xml";
	public static final String SOURCE_NAMES_PROPERTIES_FILENAME = "SourceNames.properties";
	public static final String SOUND_LOCATIONS_XML_FILENAME = "SoundLocations.xml";
	public static final String SOUND_LOCATIONS_PROPERTIES_FILENAME = "SoundLocations.properties";
	public static final String PREVIOUS_APPLICATION_PATH_FILENAME = ".previous.application.path";

	private static final String LICENSED_PREFERENCES_KEY = "licensed";
	public static final String DEFAULT_APPLICATION_PATH;
	private static final Map<String, String> DEFAULT_SOURCE_NAMES;
	private static final Map<String, String> DEFAULT_SOUND_LOCATIONS;

	static
	{
		String userHome=System.getProperty("user.home");
		File defaultAppPath=new File(userHome, ".lilith");
		DEFAULT_APPLICATION_PATH=defaultAppPath.getAbsolutePath();

		Map<String, String> defaultSoundLocations = new HashMap<String, String>();
		defaultSoundLocations.put(LilithSounds.SOURCE_ADDED, "/events/SourceAdded.mp3");
		defaultSoundLocations.put(LilithSounds.SOURCE_REMOVED, "/events/SourceRemoved.mp3");
		defaultSoundLocations.put(LilithSounds.ERROR_EVENT_ALARM, "/events/ErrorEventAlarm.mp3");
		DEFAULT_SOUND_LOCATIONS = Collections.unmodifiableMap(defaultSoundLocations);

		Map<String, String> defaultSourceNames = new HashMap<String, String>();
		defaultSourceNames.put("127.0.0.1", "Localhost");
		DEFAULT_SOURCE_NAMES = Collections.unmodifiableMap(defaultSourceNames);
	}

	private final Logger logger = LoggerFactory.getLogger(ApplicationPreferences.class);

	private File startupApplicationPath;

	private PropertyChangeSupport propertyChangeSupport;
	private URL detailsViewRootUrl;

	/**
	 * Identifier => Name
	 */
	private Map<String, String> sourceNames;
	private long lastSourceNamesModified;

	private long lastConditionsModified;

	private Map<String, String> soundLocations;
	private long lastSoundLocationsModified;

	private Map<String, Set<String>> sourceLists;
	private long lastSourceListsModified;

	private SourceFiltering sourceFiltering;

	private Set<String> blackList;
	private Set<String> whiteList;
	private Map<String, Condition> conditions;

	public ApplicationPreferences()
	{
		lastSourceNamesModified = -1;
		lastConditionsModified = -1;
		propertyChangeSupport=new PropertyChangeSupport(this);
		startupApplicationPath=getApplicationPath();
	}

	public void setSourceFiltering(SourceFiltering sourceFiltering)
	{
		Object oldValue=getSourceFiltering();
		PREFERENCES.put(SOURCE_FILTERING_PROPERTY, sourceFiltering.toString());
		this.sourceFiltering=sourceFiltering;
		propertyChangeSupport.firePropertyChange(SOURCE_FILTERING_PROPERTY, oldValue, sourceFiltering);
		if(logger.isInfoEnabled()) logger.info("SourceFiltering set to {}.", this.sourceFiltering);
	}


	private void initSourceLists()
	{
		File appPath=getStartupApplicationPath();
		File sourceListsFile =new File(appPath, SOURCE_LISTS_XML_FILENAME);

		if(sourceListsFile.isFile())
		{
			long lastModified = sourceListsFile.lastModified();
			if(sourceLists!=null && lastSourceListsModified >=lastModified)
			{
				if(logger.isDebugEnabled()) logger.debug("Won't reload source lists.");
				return;
			}
			XMLDecoder d = null;
			try
			{
				d = new XMLDecoder(
								  new BufferedInputStream(
									  new FileInputStream(sourceListsFile)));

				sourceLists=(Map<String, Set<String>>) d.readObject();
				lastSourceListsModified=lastModified;
			}
			catch (FileNotFoundException ex)
			{
				if(logger.isWarnEnabled()) logger.warn("Exception while loading source lists from sourceListsFile '"+sourceListsFile.getAbsolutePath()+"'!",ex);
				sourceLists=new HashMap<String, Set<String>>();
			}
			finally
			{
				if(d!=null)
				{
					d.close();
				}
			}
		}
		else if(sourceLists==null)
		{
			sourceLists=new HashMap<String, Set<String>>();
		}
	}

	public Map<String, Set<String>> getSourceLists()
	{
		initSourceLists();
		return new HashMap<String, Set<String>>(sourceLists);
	}

	public void setSourceLists(Map<String, Set<String>> sourceLists)
	{
		Object oldValue=getSourceLists();
		writeSourceLists(sourceLists);
		Object newValue=getSourceLists();
		blackList=null;
		whiteList=null;
		propertyChangeSupport.firePropertyChange(SOURCE_LISTS_PROPERTY, oldValue, newValue);
	}

	public SourceFiltering getSourceFiltering()
	{
		if(sourceFiltering!=null)
		{
			return sourceFiltering;
		}
		String sf=PREFERENCES.get(SOURCE_FILTERING_PROPERTY, "NONE");
		try
		{
			sourceFiltering = SourceFiltering.valueOf(sf);
		}
		catch (IllegalArgumentException e)
		{
			sourceFiltering = SourceFiltering.NONE;
		}
		return sourceFiltering;
	}

	private void initDetailsViewRoot()
	{
		detailsViewRoot =new File(startupApplicationPath, DETAILS_VIEW_ROOT_FOLDER);
		detailsViewRoot.mkdirs();
		try
		{
			detailsViewRootUrl = detailsViewRoot.toURI().toURL();
		}
		catch (MalformedURLException e)
		{
			if(logger.isWarnEnabled()) logger.warn("Exception while creating detailsViewRootUrl for '{}'!", detailsViewRoot.getAbsolutePath());
			detailsViewRootUrl =null;
		}

		{
			File detailsViewCss=new File(detailsViewRoot, DETAILS_VIEW_CSS_FILENAME);
			if(!detailsViewCss.isFile())
			{
				InputStream is=getClass().getResourceAsStream("/detailsView/"+ DETAILS_VIEW_CSS_FILENAME);
				if(is==null)
				{
					if(logger.isErrorEnabled()) logger.error("Couldn't find "+DETAILS_VIEW_CSS_FILENAME+" resource!");
					return;
				}
				FileOutputStream os= null;
				try
				{
					os = new FileOutputStream(detailsViewCss);
					IOUtils.copy(is, os);
					if(logger.isInfoEnabled()) logger.info("Initialized "+DETAILS_VIEW_CSS_FILENAME+" at '{}'.", detailsViewCss.getAbsolutePath());
				}
				catch (IOException e)
				{
					if(logger.isWarnEnabled()) logger.warn("Exception while initializing "+DETAILS_VIEW_CSS_FILENAME+" in settings!", e);
				}
				finally
				{
					IOUtils.closeQuietly(is);
					IOUtils.closeQuietly(os);
				}
			}
		}

		{
			File detailsViewGroovy=new File(detailsViewRoot, DETAILS_VIEW_GROOVY_FILENAME);
			if(!detailsViewGroovy.isFile())
			{
				InputStream is=getClass().getResourceAsStream("/detailsView/"+ DETAILS_VIEW_GROOVY_FILENAME);
				if(is==null)
				{
					if(logger.isErrorEnabled()) logger.error("Couldn't find "+DETAILS_VIEW_GROOVY_FILENAME+" resource!");
					return;
				}
				FileOutputStream os= null;
				try
				{
					os = new FileOutputStream(detailsViewGroovy);
					IOUtils.copy(is, os);
					if(logger.isInfoEnabled()) logger.info("Initialized "+DETAILS_VIEW_GROOVY_FILENAME+" at '{}'.", detailsViewGroovy.getAbsolutePath());
				}
				catch (IOException e)
				{
					if(logger.isWarnEnabled()) logger.warn("Exception while initializing "+DETAILS_VIEW_GROOVY_FILENAME+" in settings!", e);
				}
				finally
				{
					IOUtils.closeQuietly(is);
					IOUtils.closeQuietly(os);
				}
			}
		}
	}

	public File getDetailsViewRoot()
	{
		if(detailsViewRoot !=null)
		{
			return detailsViewRoot;
		}
		initDetailsViewRoot();
		return detailsViewRoot;
	}

	public URL getDetailsViewRootUrl()
	{
		if(detailsViewRootUrl !=null)
		{
			return detailsViewRootUrl;
		}
		initDetailsViewRoot();
		return detailsViewRootUrl;
		/*
		detailsViewRoot=new File(startupApplicationPath, DETAILS_VIEW_ROOT_FOLDER);
		detailsViewRoot.mkdirs();
		File messageViewCss=new File(detailsViewRoot, DETAILS_VIEW_CSS_FILENAME);
		if(!messageViewCss.isFile())
		{
			InputStream is=getClass().getResourceAsStream("/styles/"+DETAILS_VIEW_CSS_FILENAME);
			if(is==null)
			{
				if(logger.isErrorEnabled()) logger.error("Couldn't find messageView.css resource!");
				return null;
			}
			FileOutputStream os= null;
			try
			{
				os = new FileOutputStream(messageViewCss);
				IOUtils.copy(is, os);
				if(logger.isInfoEnabled()) logger.info("Initialized messageView.css at '{}'.", messageViewCss.getAbsolutePath());
			}
			catch (IOException e)
			{
				if(logger.isWarnEnabled()) logger.warn("Exception while initializing messageView.css in settings!", e);
			}
			finally
			{
				IOUtils.closeQuietly(is);
				IOUtils.closeQuietly(os);
			}
		}
		try
		{
			detailsViewRootUrl=detailsViewRoot.toURI().toURL();
		}
		catch (MalformedURLException e)
		{
			if(logger.isWarnEnabled()) logger.warn("Exception while creating detailsViewRootUrl from '{}'!", detailsViewRoot.getAbsolutePath());
		}
		return detailsViewRootUrl;
		*/
	}

	public boolean isValidSource(String source)
	{
		if(source==null)
		{
			return false;
		}
		SourceFiltering filtering=getSourceFiltering();
		switch(filtering)
		{
			case BLACKLIST:
				return !isBlackListed(source);
			case WHITELIST:
				return isWhiteListed(source);
		}
		return true;
	}

	public boolean isBlackListed(String source)
	{
		if(blackList==null)
		{
			String listName=getBlackListName();
			initSourceLists();
			blackList = sourceLists.get(listName);
			if(blackList==null)
			{
				// meaning there was no list of the given blacklist name.
				if(logger.isInfoEnabled()) logger.info("Couldn't find blacklist '{}'!", listName);
				setSourceFiltering(SourceFiltering.NONE);
				setBlackListName("");
				return true;
			}
		}
		return blackList.contains(source);
	}

	public void setBlackListName(String name)
	{
		Object oldValue=getBlackListName();
		PREFERENCES.put(BLACK_LIST_NAME_PROPERTY, name);
		Object newValue=getBlackListName();
		propertyChangeSupport.firePropertyChange(BLACK_LIST_NAME_PROPERTY, oldValue, newValue);
		if(logger.isInfoEnabled()) logger.info("BlackListName set to {}.", newValue);
	}

	public String getBlackListName()
	{
		return PREFERENCES.get(BLACK_LIST_NAME_PROPERTY, "");
	}

	public boolean isWhiteListed(String source)
	{
		if(whiteList==null)
		{
			String listName=getWhiteListName();
			initSourceLists();
			whiteList = sourceLists.get(listName);
			if(whiteList==null)
			{
				// meaning there was no list of the given blacklist name.
				if(logger.isInfoEnabled()) logger.info("Couldn't find whitelist '{}'!", listName);
				setSourceFiltering(SourceFiltering.NONE);
				setWhiteListName("");
				return true;
			}
		}
		return whiteList.contains(source);
	}

	public void setWhiteListName(String name)
	{
		Object oldValue=getWhiteListName();
		PREFERENCES.put(WHITE_LIST_NAME_PROPERTY, name);
		Object newValue=getWhiteListName();
		propertyChangeSupport.firePropertyChange(WHITE_LIST_NAME_PROPERTY, oldValue, newValue);
		if(logger.isInfoEnabled()) logger.info("WhiteListName set to {}.", newValue);
	}

	public String getWhiteListName()
	{
		return PREFERENCES.get(WHITE_LIST_NAME_PROPERTY, "");
	}

	private void initConditions()
	{
		File appPath=getStartupApplicationPath();
		File conditionsFile =new File(appPath, CONDITIONS_XML_FILENAME);

		if(conditionsFile.isFile())
		{
			long lastModified = conditionsFile.lastModified();
			if(conditions!=null && lastConditionsModified >=lastModified)
			{
				if(logger.isDebugEnabled()) logger.debug("Won't reload conditions.");
				return;
			}
			XMLDecoder d = null;
			try
			{
				d = new XMLDecoder(
								  new BufferedInputStream(
									  new FileInputStream(conditionsFile)));

				conditions=(Map<String, Condition>) d.readObject();
				lastConditionsModified =lastModified;
			}
			catch (FileNotFoundException ex)
			{
				if(logger.isWarnEnabled()) logger.warn("Exception while loading conditions from file '"+ conditionsFile.getAbsolutePath()+"'!",ex);
				conditions=new HashMap<String, Condition>();
			}
			finally
			{
				if(d!=null)
				{
					d.close();
				}
			}
		}
		else if(conditions==null)
		{
			conditions=new HashMap<String, Condition>();
		}
	}

	public Map<String, Condition> getConditions()
	{
		initConditions();
		return new HashMap<String, Condition>(conditions);
	}

	public void setConditions(Map<String, Condition> conditions)
	{
		Object oldValue=getConditions();
		writeConditions(conditions);
		Object newValue=getConditions();
		propertyChangeSupport.firePropertyChange(CONDITIONS_PROPERTY, oldValue, newValue);
	}

	public void setAutoOpening(boolean autoOpening)
	{
		Object oldValue=isAutoOpening();
		PREFERENCES.putBoolean(AUTO_OPENING_PROPERTY, autoOpening);
		Object newValue=isAutoOpening();
		propertyChangeSupport.firePropertyChange(AUTO_OPENING_PROPERTY, oldValue, newValue);
	}

	public boolean isAutoOpening()
	{
		return PREFERENCES.getBoolean(AUTO_OPENING_PROPERTY, true);
	}

	public void setShowingIdentifier(boolean showingIdentifierWithName)
	{
		Object oldValue= isShowingIdentifier();
		PREFERENCES.putBoolean(SHOWING_IDENTIFIER_PROPERTY, showingIdentifierWithName);
		Object newValue= isShowingIdentifier();
		propertyChangeSupport.firePropertyChange(SHOWING_IDENTIFIER_PROPERTY, oldValue, newValue);
	}

	public boolean isShowingIdentifier()
	{
		return PREFERENCES.getBoolean(SHOWING_IDENTIFIER_PROPERTY, true);
	}


	public void setCheckingForUpdate(boolean checkingForUpdate)
	{
		Object oldValue=isCheckingForUpdate();
		PREFERENCES.putBoolean(CHECKING_FOR_UPDATE_PROPERTY, checkingForUpdate);
		Object newValue=isCheckingForUpdate();
		propertyChangeSupport.firePropertyChange(CHECKING_FOR_UPDATE_PROPERTY, oldValue, newValue);
	}

	public boolean isCheckingForUpdate()
	{
		return PREFERENCES.getBoolean(CHECKING_FOR_UPDATE_PROPERTY, true);
	}

	public void setAutoClosing(boolean autoClosing)
	{
		Object oldValue=isAutoClosing();
		PREFERENCES.putBoolean(AUTO_CLOSING_PROPERTY, autoClosing);
		Object newValue=isAutoClosing();
		propertyChangeSupport.firePropertyChange(AUTO_CLOSING_PROPERTY, oldValue, newValue);
	}

	public boolean isAutoClosing()
	{
		return PREFERENCES.getBoolean(AUTO_CLOSING_PROPERTY, false);
	}

	public File getImagePath()
	{
		String userHome=System.getProperty("user.home");
		String imagePath =PREFERENCES.get(IMAGE_PATH_PROPERTY, userHome);
		File result=new File(imagePath);
		if(!result.isDirectory())
		{
			result=new File(userHome);
		}
		return result;
	}

	public void setImagePath(File imagePath)
	{
		if(!imagePath.isDirectory())
		{
			throw new IllegalArgumentException("'"+imagePath.getAbsolutePath()+"' is not a directory!");
		}
		Object oldValue=getImagePath();
		PREFERENCES.put(IMAGE_PATH_PROPERTY, imagePath.getAbsolutePath());
		Object newValue=getImagePath();
		propertyChangeSupport.firePropertyChange(IMAGE_PATH_PROPERTY, oldValue, newValue);
	}

	public File getSoundPath()
	{
		String userHome=System.getProperty("user.home");
		String soundPath =PREFERENCES.get(SOUND_PATH_PROPERTY, userHome);
		File result=new File(soundPath);
		if(!result.isDirectory())
		{
			result=new File(userHome);
		}
		return result;
	}

	public void setSoundPath(File soundPath)
	{
		if(!soundPath.isDirectory())
		{
			throw new IllegalArgumentException("'"+soundPath.getAbsolutePath()+"' is not a directory!");
		}
		Object oldValue=getSoundPath();
		PREFERENCES.put(SOUND_PATH_PROPERTY, soundPath.getAbsolutePath());
		Object newValue=getSoundPath();
		propertyChangeSupport.firePropertyChange(SOUND_PATH_PROPERTY, oldValue, newValue);
	}

	public void setMute(boolean mute)
	{
		Object oldValue=isMute();
		PREFERENCES.putBoolean(MUTE_PROPERTY, mute);
		Object newValue=isMute();
		propertyChangeSupport.firePropertyChange(MUTE_PROPERTY, oldValue, newValue);
	}

	public boolean isMute()
	{
		return PREFERENCES.getBoolean(MUTE_PROPERTY, false);
	}

	public void setLicensed(boolean licensed)
	{
		Object oldValue=isLicensed();
		PREFERENCES.putBoolean(LICENSED_PREFERENCES_KEY, licensed);
		Object newValue=isLicensed();
		propertyChangeSupport.firePropertyChange(LICENSED_PREFERENCES_KEY, oldValue, newValue);
	}

	public boolean isLicensed()
	{
		return PREFERENCES.getBoolean(LICENSED_PREFERENCES_KEY, false);
	}

	public void setApplicationPath(File applicationPath)
	{
		applicationPath.mkdirs();
		if(!applicationPath.isDirectory())
		{
			throw new IllegalArgumentException("'"+applicationPath.getAbsolutePath()+"' is not a directory!");
		}
		Object oldValue=getStartupApplicationPath(); // !!!
		PREFERENCES.put(APPLICATION_PATH_PROPERTY, applicationPath.getAbsolutePath());
		Object newValue=getApplicationPath();
		propertyChangeSupport.firePropertyChange(APPLICATION_PATH_PROPERTY, oldValue, newValue);
	}

	public File getApplicationPath()
	{
		String appPath=PREFERENCES.get(APPLICATION_PATH_PROPERTY, DEFAULT_APPLICATION_PATH);
		File result=new File(appPath);
		result.mkdirs();
		return result;
	}

	/**
	 * The StartupApplicationPath is initialized on application startup via ApplicationPreferences.getApplicationPath.
	 * If a part of the application needs the application path it should *always* use this method instead of
	 * getApplicationPath() since the application path might change while this one will always stay
	 * the same.
	 *
	 * A switch of the application path while the application is running isn't safe so it's changed for real
	 * upon next restart.
	 *
	 * @return the application path at startup time.
	 */
	public File getStartupApplicationPath()
	{
		return startupApplicationPath;
	}

	public void setUsingInternalFrames(boolean usingInternalFrames)
	{
		Object oldValue=isUsingInternalFrames();
		PREFERENCES.putBoolean(USING_INTERNAL_FRAMES_PROPERTY, usingInternalFrames);
		Object newValue=isUsingInternalFrames();
		propertyChangeSupport.firePropertyChange(USING_INTERNAL_FRAMES_PROPERTY, oldValue, newValue);
	}

	public boolean isUsingInternalFrames()
	{
		return PREFERENCES.getBoolean(USING_INTERNAL_FRAMES_PROPERTY, true);
	}

	public void setAutoFocusingWindow(boolean autoFocusingWindow)
	{
		Object oldValue=isAutoFocusingWindow();
		PREFERENCES.putBoolean(AUTO_FOCUSING_WINDOW_PROPERTY, autoFocusingWindow);
		Object newValue=isAutoFocusingWindow();
		propertyChangeSupport.firePropertyChange(AUTO_FOCUSING_WINDOW_PROPERTY, oldValue, newValue);
	}

	public boolean isAutoFocusingWindow()
	{
		return PREFERENCES.getBoolean(AUTO_FOCUSING_WINDOW_PROPERTY, false);
	}

	public void setSourceNames(Map<String, String> sourceNames)
	{
		Object oldValue=getSourceNames();
		writeSourceNames(sourceNames);
		Object newValue=getSourceNames();
		propertyChangeSupport.firePropertyChange(SOURCE_NAMES_PROPERTY, oldValue, newValue);
	}

	public Map<String,String> getSourceNames()
	{
		File appPath=getStartupApplicationPath();
		File sourceNamesFile=new File(appPath, SOURCE_NAMES_XML_FILENAME);

		if(sourceNamesFile.isFile())
		{
			if(loadSourceNamesXml(sourceNamesFile))
			{
				return new HashMap<String, String>(sourceNames);
			}
		}

		sourceNamesFile=new File(appPath, SOURCE_NAMES_PROPERTIES_FILENAME);
		if(sourceNamesFile.isFile())
		{
			if(loadSourceNamesProperties(sourceNamesFile))
			{
				return new HashMap<String, String>(sourceNames);
			}
		}
		return new HashMap<String, String>(DEFAULT_SOURCE_NAMES);
	}


	public Map<String,String> getSoundLocations()
	{
		File appPath=getStartupApplicationPath();
		File file =new File(appPath, SOUND_LOCATIONS_XML_FILENAME);

		if(file.isFile())
		{
			if(loadSoundLocationsXml(file))
			{
				return new HashMap<String, String>(soundLocations);
			}
		}

		file =new File(appPath, SOUND_LOCATIONS_PROPERTIES_FILENAME);
		if(file.isFile())
		{
			if(loadSoundLocationsProperties(file))
			{
				return new HashMap<String, String>(soundLocations);
			}
		}
		return new HashMap<String, String>(DEFAULT_SOUND_LOCATIONS);
	}

	public void setSoundLocations(Map<String, String> soundLocations)
	{
		Object oldValue=getSoundLocations();
		writeSoundLocations(soundLocations);
		Object newValue=getSoundLocations();
		propertyChangeSupport.firePropertyChange(SOUND_LOCATIONS_PROPERTY, oldValue, newValue);
	}

	public void resetSoundLocations()
	{
		if(logger.isInfoEnabled()) logger.info("Initializing preferences with default sound locations.");
		setSoundLocations(DEFAULT_SOUND_LOCATIONS);
	}

	public void addPropertyChangeListener(PropertyChangeListener listener)
	{
		propertyChangeSupport.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener)
	{
		propertyChangeSupport.removePropertyChangeListener(listener);
	}

	public void reset()
	{
		final Logger logger = LoggerFactory.getLogger(ApplicationPreferences.class);
		boolean licensed=isLicensed();
		try
		{
			PREFERENCES.clear();
			resetSoundLocations();
			setLicensed(licensed);
			setApplicationPath(new File(DEFAULT_APPLICATION_PATH));
		}
		catch (BackingStoreException e)
		{
			if(logger.isWarnEnabled()) logger.warn("Exception while clearing preferences!");
		}
	}


	public void setScrollingToBottom(boolean scrollingToBottom)
	{
		Object oldValue=isScrollingToBottom();
		PREFERENCES.putBoolean(SCROLLING_TO_BOTTOM_PROPERTY, scrollingToBottom);
		Object newValue=isScrollingToBottom();
		propertyChangeSupport.firePropertyChange(SCROLLING_TO_BOTTOM_PROPERTY, oldValue, newValue);
	}

	public boolean isScrollingToBottom()
	{
		return PREFERENCES.getBoolean(SCROLLING_TO_BOTTOM_PROPERTY, true);
	}


	private boolean loadSoundLocationsProperties(File sourceNamesFile)
	{
		long lastModified = sourceNamesFile.lastModified();
		if(soundLocations!=null && lastSoundLocationsModified >=lastModified)
		{
			if(logger.isDebugEnabled()) logger.debug("Won't reload sound locations.");
			return true;
		}

		Map<String, String> props = loadProperties(sourceNamesFile);
		if(props!=null)
		{
			lastSoundLocationsModified=lastModified;
			soundLocations=props;
			return true;
		}
		return false;
	}

	private boolean loadSoundLocationsXml(File file)
	{
		long lastModified = file.lastModified();
		if(soundLocations!=null && lastSoundLocationsModified >=lastModified)
		{
			if(logger.isDebugEnabled()) logger.debug("Won't reload sound locations.");
			return true;
		}
		Map<String, String> props = loadPropertiesXml(file);
		if(props!=null)
		{
			lastSoundLocationsModified=lastModified;
			soundLocations=props;
			return true;
		}
		return false;
	}

	private boolean writeSoundLocations(Map<String, String> sourceNames)
	{
		File appPath=getStartupApplicationPath();
		File file = new File(appPath, SOUND_LOCATIONS_XML_FILENAME);
		return writePropertiesXml(file, sourceNames, "Sound locations");
	}

	private boolean loadSourceNamesXml(File file)
	{
		long lastModified = file.lastModified();
		if(sourceNames!=null && lastSourceNamesModified >=lastModified)
		{
			if(logger.isDebugEnabled()) logger.debug("Won't reload source names.");
			return true;
		}
		Map<String, String> props = loadPropertiesXml(file);
		if(props!=null)
		{
			lastSourceNamesModified=lastModified;
			sourceNames=props;
			return true;
		}
		return false;
	}

	private boolean loadSourceNamesProperties(File sourceNamesFile)
	{
		long lastModified = sourceNamesFile.lastModified();
		if(sourceNames!=null && lastSourceNamesModified >=lastModified)
		{
			if(logger.isDebugEnabled()) logger.debug("Won't reload source names.");
			return true;
		}

		Map<String, String> props = loadProperties(sourceNamesFile);
		if(props!=null)
		{
			lastSourceNamesModified=lastModified;
			sourceNames=props;
			return true;
		}
		return false;
	}

	private boolean writeSourceNames(Map<String, String> sourceNames)
	{
		File appPath=getStartupApplicationPath();
		File file = new File(appPath, SOURCE_NAMES_XML_FILENAME);
		return writePropertiesXml(file, sourceNames, "Source names");
	}

	private boolean writeSourceLists(Map<String, Set<String>> sourceLists)
	{
		File appPath=getStartupApplicationPath();
		File file = new File(appPath, SOURCE_LISTS_XML_FILENAME);
		XMLEncoder e = null;
		Throwable error=null;
		try
		{
			BufferedOutputStream bos=new BufferedOutputStream(new FileOutputStream(file));
			e = new XMLEncoder(bos);
			e.writeObject(sourceLists);
		}
		catch (FileNotFoundException ex)
		{
			error=ex;
		}
		finally
		{
			if(e!=null)
			{
				e.close();
			}
		}
		if(error!=null)
		{
			if(logger.isWarnEnabled()) logger.warn("Exception while writing source lists!", error);
			return false;
		}
		return true;
	}

	private boolean writeConditions(Map<String, Condition> conditions)
	{
		File appPath=getStartupApplicationPath();
		File file = new File(appPath, CONDITIONS_XML_FILENAME);
		XMLEncoder e = null;
		Throwable error=null;
		try
		{
			BufferedOutputStream bos=new BufferedOutputStream(new FileOutputStream(file));
			e = new XMLEncoder(bos);
			e.writeObject(conditions);
			if(logger.isInfoEnabled()) logger.info("Wrote conditions {}.", conditions);
		}
		catch (FileNotFoundException ex)
		{
			error=ex;
		}
		finally
		{
			if(e!=null)
			{
				e.close();
			}
		}
		if(error!=null)
		{
			if(logger.isWarnEnabled()) logger.warn("Exception while writing source lists!", error);
			return false;
		}
		return true;
	}

	/** @noinspection MismatchedQueryAndUpdateOfCollection*/
	private Map<String,String> loadPropertiesXml(File file)
	{
		InputStream is=null;
		try
		{
			is=new BufferedInputStream(new FileInputStream(file));
			Properties props=new Properties();
			props.loadFromXML(is);
			Map<String,String> result=new HashMap<String, String>();
			for(Object keyObj:props.keySet())
			{
				String key=(String) keyObj;
				String value=(String) props.get(key);
				if(value!=null)
				{
					result.put(key,value);
				}
			}
			return result;
		}
		catch (IOException e)
		{
			if(logger.isWarnEnabled()) logger.warn("Couldn't load properties from '"+file.getAbsolutePath()+"'!", e);
		}
		finally
		{
			IOUtils.closeQuietly(is);
		}
		return null;
	}

	/** @noinspection MismatchedQueryAndUpdateOfCollection*/
	private boolean writePropertiesXml(File file, Map<String, String> sourceNames, String comment)
	{
		Properties output=new Properties();
		for(Map.Entry<String, String> entry:sourceNames.entrySet())
		{
			String key=entry.getKey();
			String value=entry.getValue();
			if(value!=null)
			{
				output.put(key, value);
			}
		}
		OutputStream os=null;
		Throwable error=null;
		try
		{
			os=new BufferedOutputStream(new FileOutputStream(file));
			output.storeToXML(os, comment, "UTF-8");
		}
		catch (FileNotFoundException e)
		{
			error=e;
		}
		catch (IOException e)
		{
			error=e;
		}
		finally
		{
			IOUtils.closeQuietly(os);
		}
		if(error!=null)
		{
			if(logger.isWarnEnabled()) logger.warn("Exception while writing source names!", error);
			return false;
		}
		return true;
	}


	private Map<String,String> loadProperties(File file)
	{
		InputStream is=null;
		try
		{
			is=new BufferedInputStream(new FileInputStream(file));
			Properties props=new Properties();
			props.load(is);
			Map<String,String> result=new HashMap<String, String>();
			for(Object keyObj:props.keySet())
			{
				String key=(String) keyObj;
				String value=(String) props.get(key);
				if(value!=null)
				{
					result.put(key,value);
				}
			}
			return result;
		}
		catch (IOException e)
		{
			if(logger.isWarnEnabled()) logger.warn("Couldn't load properties from '"+file.getAbsolutePath()+"'!", e);
		}
		finally
		{
			IOUtils.closeQuietly(is);
		}
		return null;
	}

}
