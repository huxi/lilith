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
package de.huxhorn.lilith.swing;

import de.huxhorn.sulky.swing.GraphicsUtilities;
import de.huxhorn.sulky.swing.filters.ColorTintFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;

// TODO: get/setMouseHandling/MouseInputMode
// TODO: setVersionHeight(versionHeight);
// TODO: setVersionString(versionString);
// TODO: correct versionHeight if string would be outside background.
// TODO: VersionString centered to bg/scroll.
// TODO: relative ScrollAreas (values given as % of backgroundImage)
// TODO: Handle errors in Image-Loading
// TODO: offscreenImage h�chstens so gro� wie die size / nicht gesamten bg malen
// TODO: paint background-color for rest of component (not only behind bg-image)
// TODO: scroll-area defined by object-array containing icons and strings...
// TODO: transient attributes
// TODO: serialVersion

// TODO: use ResourceSupport
/**
 * <code>AboutPanel</code> is a component which has a background-image and a
 * rectangle in which a given text is scrolling (the scroll-area). You may also
 * specify an Image (e.g. a png-file with alpha-channel) that is drawn before
 * the scroll-text itself. An optional version-string may be given that will be
 * painted centered relative to the scroll-area.
 *
 * @author Joern Huxhorn
 */
public class AboutPanel
	extends JComponent
{
	private final Logger logger = LoggerFactory.getLogger(AboutPanel.class);

	public static final String BACKGROUND_IMAGE_RESOURCE = "background.png";
	public static final String ABOUT_IMAGE_RESOURCE = "about.png";

	public static final String TEXT_RESOURCE_PREFIX = "about.";

	public static final String SCROLL_TEXT_RESOURCE =
		TEXT_RESOURCE_PREFIX + "scroll.text";
	public static final String VERSION_TEXT_RESOURCE =
		TEXT_RESOURCE_PREFIX + "version.text";
	public static final String VERSION_HEIGHT_RESOURCE =
		TEXT_RESOURCE_PREFIX + "version.height";
	public static final String SCROLL_AREA_RESOURCE_BASE =
		TEXT_RESOURCE_PREFIX + "scroll.area.";
	public static final String SCROLL_AREA_X_RESOURCE =
		SCROLL_AREA_RESOURCE_BASE + "x";
	public static final String SCROLL_AREA_Y_RESOURCE =
		SCROLL_AREA_RESOURCE_BASE + "y";
	public static final String SCROLL_AREA_WIDTH_RESOURCE =
		SCROLL_AREA_RESOURCE_BASE + "width";
	public static final String SCROLL_AREA_HEIGHT_RESOURCE =
		SCROLL_AREA_RESOURCE_BASE + "height";
	public static final String SCROLL_AREA_TOOLTIP_TEXT_RESOURCE =
		SCROLL_AREA_RESOURCE_BASE + "tooltip.text";


	public static final String TEXT_RESOURCE_BUNDLE_RESOURCE = "TextResources";

	public static final int MOUSE_DISABLED = 0;
	public static final int MOUSE_COMPONENT = 1;
	public static final int MOUSE_SCROLLAREA = 2;
	public static final int MOUSE_BACKGROUND = 3;

	//private static final int SCROLL_SLEEP_TIME = 50;
	private static final int SCROLL_PIXELS = 1;
	//private static final int SCROLL_THREAD_PRIORITY = Thread.NORM_PRIORITY+1;

	//private final ResourceSupport resourceSupport;

	private BufferedImage backgroundImage;
	private BufferedImage aboutImage;
	//private ImageIcon backgroundImageIcon;
	//private ImageIcon aboutImageIcon;
	private FontMetrics fontMetrics;

	private Insets insets;
	private Dimension size;
	private Dimension preferredSize;
	private Point offscreenOffset;
	private String[] scrollLines;
	private String versionText;
	private String scrollAreaToolTipText;
	private int versionHeight;
	private int scrollPosition;
	private int maxScrollPosition;
	private int minScrollPosition;
	private Rectangle maxScrollArea;
	private Rectangle backgroundImageArea;
	private Rectangle translatedBackgroundImageArea;
	private Rectangle translatedScrollArea;
	private Rectangle scrollArea;
	private Rectangle paintArea;
	private BufferedImage offscreenImage;
	private BufferedImage scrollImage;
	private boolean scrolling;
	//private boolean offscreenInitialized = false;
	//private boolean scrollInitialized = false;
	//private boolean painted;
	private int mouseEventHandling = MOUSE_BACKGROUND;
	//private transient Thread scrollThread;
	private boolean debug;
	private Timer timer;
	//private String scrollText;
	//private Map textBundleMap;
	//private int mouseEventHandling=MOUSE_DISABLED;

	/**
	 * Creates a new <code>AboutPanel</code> initialized with the given parameters.
	 *
	 * @param backgroundImageUrl The URL to the Background-Image of the
	 *                           AboutPanel. This parameter is mandatory.
	 * @param scrollArea         The Rectangle inside the background-image where
	 *                           scrolling should take place. This parameter is optional. If it's null
	 *                           then the scroll-area is set to (0, 0, background.width,
	 *                           background.height).
	 */
	public AboutPanel(URL backgroundImageUrl, Rectangle scrollArea, String scrollText)
		throws IOException
	{
		this(backgroundImageUrl, scrollArea, scrollText, null, null, -1);
	}

	public boolean isDebug()
	{
		return debug;
	}

	public void setDebug(boolean debug)
	{
		this.debug = debug;
	}

	/**
	 * Creates a new <code>AboutPanel</code> initialized with the given parameters.
	 *
	 * @param backgroundImageUrl The URL to the Background-Image of the
	 *                           AboutPanel. This parameter is mandatory.
	 * @param scrollArea         The Rectangle inside the background-image where
	 *                           scrolling should take place. This parameter is optional. If it's null
	 *                           then the scroll-area is set to (0, 0, background.width,
	 *                           background.height).
	 * @param versionText        The String describing the version of the program.
	 *                           It is painted centered to the scroll-rectangle at the specified height.
	 *                           This parameter is optional.
	 * @param versionHeight      The height at which the version-string is
	 *                           supposed to be painted. This parameter is optional but should be given
	 *                           a correct value if versionText!=null..
	 */
	public AboutPanel(URL backgroundImageUrl, Rectangle scrollArea, String scrollText, String versionText, int versionHeight)
		throws IOException
	{
		this(backgroundImageUrl, scrollArea, scrollText, null, versionText, versionHeight);
	}


	/**
	 * Creates a new <code>AboutPanel</code> initialized with the given parameters.
	 *
	 * @param backgroundImageUrl The URL to the Background-Image of the
	 *                           AboutPanel. This parameter is mandatory.
	 * @param scrollArea         The Rectangle inside the background-image where
	 *                           scrolling should take place. This parameter is optional. If it's null
	 *                           then the scroll-area is set to (0, 0, background.width,
	 *                           background.height).
	 * @param imageUrl           The URL to the Image that will be painted at the
	 *                           start of the scroll-area. This parameter is optional.
	 * @param versionText        The String describing the version of the program.
	 *                           It is painted centered to the scroll-rectangle at the specified height.
	 *                           This parameter is optional.
	 * @param versionHeight      The height at which the version-string is
	 *                           supposed to be painted. This parameter is optional but should be given
	 *                           a correct value if versionText!=null..
	 */
	public AboutPanel(URL backgroundImageUrl, Rectangle scrollArea, String scrollText, URL imageUrl, String versionText, int versionHeight)
		throws IOException
	{
		this();
		if(backgroundImageUrl == null)
		{
			throw new NullPointerException("backgroundImageUrl must not be null!");
		}
		if(scrollText == null)
		{
			throw new NullPointerException("scrollText must not be null!");
		}
		init(backgroundImageUrl, scrollArea, scrollText, imageUrl, versionText, versionHeight);
	}

	public AboutPanel()
	{
		ActionListener timerListener = new TimerActionListener();
		timer = new Timer(10, timerListener);
		//this.resourceSupport=new ResourceSupport(this);
		initAttributes();

		addPropertyChangeListener(new AboutPropertyChangeListener());
		addComponentListener(new AboutComponentListener());

		setFont(null);// initializes to Label.font
		AboutMouseInputListener mouseInputListener = new AboutMouseInputListener();
		addMouseListener(mouseInputListener);
		addMouseMotionListener(mouseInputListener);

		//this.scrollThread=null;

		setScrolling(false);
//		initResources();
	}


//	protected void initResources()
//	{
//		initTextBundleMap();
//
//		//URL backgroundImageUrl=resourceSupport.getResource(BACKGROUND_IMAGE_RESOURCE);
//		//URL imageUrl=resourceSupport.getResource(ABOUT_IMAGE_RESOURCE);
//		//scrollText=getTextResource(SCROLL_TEXT_RESOURCE, null);
//		versionText=getTextResource(VERSION_TEXT_RESOURCE, null);
//		int versionHeight=-1;
//		try
//		{
//			versionHeight=Integer.parseInt(getTextResource(VERSION_HEIGHT_RESOURCE, "-1"));
//		}
//		catch(NumberFormatException ex)
//		{
//			if(logger.isWarnEnabled()) logger.warn("Illegal integer value!", ex);
//		}
//
//		Rectangle scrollArea=new Rectangle(-1, -1, -1, -1);
//		try
//		{
//			scrollArea.x=Integer.parseInt(getTextResource(SCROLL_AREA_X_RESOURCE, "-1"));
//		}
//		catch(NumberFormatException ex)
//		{
//			if(logger.isWarnEnabled()) logger.warn("Illegal integer value!", ex);
//		}
//		try
//		{
//			scrollArea.y=Integer.parseInt(getTextResource(SCROLL_AREA_Y_RESOURCE, "-1"));
//		}
//		catch(NumberFormatException ex)
//		{
//			if(logger.isWarnEnabled()) logger.warn("Illegal integer value!", ex);
//		}
//		try
//		{
//			scrollArea.width=Integer.parseInt(getTextResource(SCROLL_AREA_WIDTH_RESOURCE, "-1"));
//		}
//		catch(NumberFormatException ex)
//		{
//			if(logger.isWarnEnabled()) logger.warn("Illegal integer value!", ex);
//		}
//		try
//		{
//			scrollArea.height=Integer.parseInt(getTextResource(SCROLL_AREA_HEIGHT_RESOURCE, "-1"));
//		}
//		catch(NumberFormatException ex)
//		{
//			if(logger.isWarnEnabled()) logger.warn("Illegal integer value!", ex);
//		}
//		if(	scrollArea.x == -1 ||
//			scrollArea.y == -1 ||
//			scrollArea.width == -1 ||
//			scrollArea.height == -1 )
//		{
//			// ignore if scroll-area isn't fully specified
//			scrollArea = null;
//		}
//		init(backgroundImageUrl, scrollArea, scrollText, imageUrl, versionText, versionHeight );
//		//setScrollAreaToolTipText(getTextResource(SCROLL_AREA_TOOLTIP_TEXT_RESOURCE, null));
//	}

//	protected void initTextBundleMap()
//	{
//		textBundleMap=resourceSupport.getResourceMap(TEXT_RESOURCE_BUNDLE_RESOURCE, getLocale());
//		if(logger.isDebugEnabled() && textBundleMap!=null)
//		{
//			StringBuffer buffer=new StringBuffer();
//
//			Iterator iter=textBundleMap.keySet().iterator();
//			while(iter.hasNext())
//			{
//				Object key=iter.next();
//				Object value=textBundleMap.get(key);
//				buffer.append("Key: ");
//				buffer.append(key);
//				buffer.append("    Value: ");
//				buffer.append(value);
//				buffer.append("\n");
//
//			}
//			logger.debug("BundleMap \""+TEXT_RESOURCE_BUNDLE_RESOURCE+"\" of class "+getClass().getName()+":\n"+buffer.toString());
//		}
//		if(logger.isInfoEnabled() && textBundleMap==null)
//		{
//			logger.info("Couldn't find BundleMap \""+TEXT_RESOURCE_BUNDLE_RESOURCE+"\" of class "+getClass().getName()+".");
//		}
//	}

//	protected String getTextResource(final String resourceName, final String defaultValue)
//	{
//		String result=null;
//		if(textBundleMap!=null)
//		{
//			result=(String)textBundleMap.get(resourceName);
//		}
//		if(result==null)
//		{
//			result=defaultValue;
//			if(logger.isDebugEnabled()) logger.debug("Using default-value '"+defaultValue+"' for text-resource '"+resourceName+"'.");
//		}
//
//		return result;
//	}

	private void init(URL backgroundImageUrl, Rectangle scrollArea, String scrollText, URL imageUrl, String versionText, int versionHeight)
		throws IOException
	{
		if(logger.isDebugEnabled())
		{
			logger.debug("init called with following arguments: backgroundImageUrl=" + backgroundImageUrl + ", " +
				"scrollArea=" + scrollArea + ", scrollText=" + scrollText + ", imageUrl=" + imageUrl + ", versionText=" + versionText + ", versionHeight=" + versionHeight);
		}
		setBackgroundImage(backgroundImageUrl);
		setScrollArea(scrollArea);
		setAboutImage(imageUrl);
		this.versionText = versionText;
		this.versionHeight = versionHeight;
		setScrollText(scrollText);
	}

	/*
	protected synchronized boolean isPainted()
	{
		return painted;
	}

	protected synchronized void setPainted(boolean painted)
	{
		if(this.painted!=painted)
		{
			this.painted=painted;
			notifyAll();
		}
	}
    */

	private void initAttributes()
	{
		//setPainted(true);
		preferredSize = new Dimension();
		offscreenOffset = new Point();
		backgroundImageArea = new Rectangle();
		translatedScrollArea = new Rectangle();
		translatedBackgroundImageArea = new Rectangle();
		scrollArea = new Rectangle();
		paintArea = new Rectangle();
		insets = getInsets();
	}


	public void setScrollText(String ScrollText)
	{
		StringTokenizer st = new StringTokenizer(ScrollText, "\n", true);

		List<String> lines = new ArrayList<String>(st.countTokens() / 2);
		String prevToken = null;
		while(st.hasMoreTokens())
		{
			String token = st.nextToken();
			if(token.equals("\n"))
			{
				if(prevToken != null && !prevToken.equals("\n"))
				{
					lines.add(prevToken);
				}
				else
				{
					lines.add("");
				}
			}
			prevToken = token;
		}
		if(prevToken != null && !prevToken.equals("\n"))
		{
			lines.add(prevToken);
		}

		String loScrollLines[] = new String[lines.size()];
		loScrollLines = lines.toArray(loScrollLines);
		setScrollLines(loScrollLines);
	}

	protected void setScrollLines(String[] scrollLines)
	{
		if(scrollLines == null)
		{
			NullPointerException ex = new NullPointerException("scrollLines must not be null!");
			if(logger.isDebugEnabled())
			{
				logger.debug("Parameter 'scrollLines' of method 'setScrollLines' must not be null!", ex);
			}
			throw ex;
		}

		this.scrollLines = scrollLines.clone();
		flushScrollImage();
	}

	/**
	 * Sets the backgroundImage attribute of the <code>AboutPanel</code> object
	 */
	public void setBackgroundImage(URL imageUrl)
		throws IOException
	{
		setBackgroundImage(GraphicsUtilities.loadCompatibleImage(imageUrl));
	}


	/**
	 * Sets the backgroundImage attribute of the <code>AboutPanel</code> object
	 *
	 * @param BackgroundImage The new backgroundImage value
	 */
	public void setBackgroundImage(BufferedImage BackgroundImage)
	{
		if(backgroundImage != null)
		{
			backgroundImage.flush();
			backgroundImage = null;
		}
		backgroundImage = BackgroundImage;
		updateBackgroundAttributes();
	}


	public void setAboutImage(URL imageUrl)
		throws IOException
	{
		setAboutImage(GraphicsUtilities.loadCompatibleImage(imageUrl));
	}


	public void setAboutImage(BufferedImage AboutImage)
	{
		if(aboutImage != null)
		{
			aboutImage.flush();
			aboutImage = null;
		}
		aboutImage = AboutImage;
		flushScrollImage();
	}

	/**
	 * Sets the scrollArea attribute of the <code>AboutPanel</code> object
	 *
	 * @param ScrollArea The new scrollArea value
	 */
	public void setScrollArea(Rectangle ScrollArea)
	{
		if(ScrollArea != null)
		{
			maxScrollArea = backgroundImageArea.intersection(ScrollArea);
		}
		else
		{
			maxScrollArea = (Rectangle) backgroundImageArea.clone();
		}
		minScrollPosition = -maxScrollArea.height;
		calculateAttributes();
		flushScrollImage();
	}


	/**
	 * Description of the Method
	 */
	private void flushScrollImage()
	{
		if(scrollImage != null)
		{
			if(logger.isInfoEnabled()) logger.info("Flushing ScrollImage");
			scrollImage.flush();
			scrollImage = null;
		}
		setScrollPosition(minScrollPosition);
	}


	/**
	 * Description of the Method
	 */
	private void flushOffscreenImage()
	{
		if(offscreenImage != null)
		{
			if(logger.isInfoEnabled()) logger.info("Flushing OffscreenImage");
			offscreenImage.flush();
			offscreenImage = null;
		}
	}


	/**
	 * Description of the Method
	 */
	private void updateBackgroundAttributes()
	{
		backgroundImageArea.x = 0;
		backgroundImageArea.y = 0;
		backgroundImageArea.width = backgroundImage.getWidth();
		backgroundImageArea.height = backgroundImage.getHeight();

		calculatePreferredSize();

		if(maxScrollArea != null)
		{
			maxScrollArea = maxScrollArea.intersection(backgroundImageArea);
		}
		else
		{
			maxScrollArea = (Rectangle) backgroundImageArea.clone();
		}
		flushOffscreenImage();
		flushScrollImage();
		repaint();
	}


	/**
	 * Sets the ToolTipText that will appear if the user moves the mouse over the
	 * scroll-area of this component.
	 *
	 * @param toolTipText The new ScrollAreaToolTipText value
	 */
	public void setScrollAreaToolTipText(String toolTipText)
	{
		scrollAreaToolTipText = toolTipText;
	}


	/**
	 * Gets the ScrollAreaToolTipText attribute of the <code>AboutPanel</code>
	 * object
	 *
	 * @return The ScrollAreaToolTipText value
	 */
	public String getScrollAreaToolTipText()
	{
		return scrollAreaToolTipText;
	}


	/**
	 * This method returns ScrollAreaToolTipText if the point of the <code>MouseEvent</code>
	 * is inside the scroll-area and <code>null</code> otherwise.<p />
	 * <p/>
	 * It's needed by the <code>ToolTipManager</code> .
	 *
	 * @param evt a <code>MouseEvent</code>.
	 * @return The toolTipText value for the <code>ToolTipManager</code>.
	 */
	public String getToolTipText(MouseEvent evt)
	{
		if(handleMouseEvent(evt))
		{
			return scrollAreaToolTipText;
		}
		return null;
	}

	protected boolean handleMouseEvent(MouseEvent evt)
	{
		Rectangle loArea = null;
		if(mouseEventHandling == MOUSE_BACKGROUND)
		{
			loArea = translatedBackgroundImageArea;
		}
		else if(mouseEventHandling == MOUSE_SCROLLAREA)
		{
			loArea = translatedScrollArea;
		}
		else if(mouseEventHandling == MOUSE_DISABLED)
		{
			return false;
		}
		Point loPoint = evt.getPoint();
		if(loArea == null)
		{	// -> default: MOUSE_COMPONENT
			return contains(loPoint);
		}
		if(loArea.contains(loPoint))
		{	// MOUSE_BACKGROUND / MOUSE_SCROLLAREA
			return true;
		}
		return false;
	}


	/**
	 * Increases the ScrollPosition by SCROLL_PIXELS. This method is called by the
	 * scroll-thread and calls <code>setScrollPosition</code>, therefore causing a
	 * repaint of the scroll-area..
	 *
	 * @see #setScrollPosition
	 */
	protected void increaseScrollPosition()
	{
		setScrollPosition(scrollPosition + SCROLL_PIXELS);
	}


	/**
	 * Sets the scrollPosition attribute of the <code>AboutPanel</code> object. The
	 * value will be corrected according Minimum- and MaximumScrollPosition.
	 * Changing the scroll-position will result in a repaint of the scroll-area.
	 *
	 * @param scrollPosition The new scrollPosition value. This value indicates
	 *                       the height-offset of the scroll-area.
	 * @see #getMinimumScrollPosition
	 * @see #getMaximumScrollPosition
	 */
	public void setScrollPosition(int scrollPosition)
	{
		if(scrollPosition > maxScrollPosition)
		{
			int remainder = scrollPosition % maxScrollPosition;

			scrollPosition = minScrollPosition + remainder;
		}
		else if(scrollPosition < minScrollPosition)
		{
			int remainder = scrollPosition % minScrollPosition;

			scrollPosition = maxScrollPosition + remainder;
		}
		if(this.scrollPosition != scrollPosition)
		{
			this.scrollPosition = scrollPosition;
			repaintScrollArea();
		}
	}


	/**
	 * Gets the ScrollPosition attribute of the <code>AboutPanel</code> object
	 *
	 * @return this value indicates the height-offset of the scroll-area.
	 */
	public int getScrollPosition()
	{
		return scrollPosition;
	}


	/**
	 * Gets the MinimumScrollPosition attribute of the <code>AboutPanel</code>
	 * object. It's value is the negated value of the scroll-area-height.
	 *
	 * @return The MinimumScrollPosition value
	 */
	public int getMinimumScrollPosition()
	{
		return minScrollPosition;
	}


	/**
	 * Gets the MaximumScrollPosition attribute of the <code>AboutPanel</code>
	 * object. It's value is the height needed for all lines of text plus (if
	 * available) the height of the image with an additional empty line.
	 *
	 * @return The MaximumScrollPosition value
	 */
	public int getMaximumScrollPosition()
	{
		return maxScrollPosition;
	}


	/**
	 * This method creates the offscreen-image when needed (when called for the
	 * first time or recreated because of a changed font) and updates it on
	 * subsequent calls by calling <code>updateOffscreenImage()</code>.
	 */
	private void processOffscreenImage()
	{
		Graphics2D g;
		if(offscreenImage == null)
		{
			if(logger.isInfoEnabled()) logger.info("Creating offscreen-image");
			boolean opaque = false;
			if(isOpaque())
			{
				offscreenImage = GraphicsUtilities
					.createOpaqueCompatibleImage(backgroundImageArea.width, backgroundImageArea.height);
				opaque = true;
			}
			else
			{
				offscreenImage = GraphicsUtilities
					.createTranslucentCompatibleImage(backgroundImageArea.width, backgroundImageArea.height);
			}
			g = (Graphics2D) offscreenImage.getGraphics();
			if(opaque)
			{
				g.setColor(getBackground());
				g.fillRect(backgroundImageArea.x, backgroundImageArea.y, backgroundImageArea.width, backgroundImageArea.height);
			}
			g.drawImage(backgroundImage, 0, 0, null);
			if(versionText != null)
			{
				// draw version-text...
				g.setColor(getForeground());
				g.drawString(versionText, maxScrollArea.x +
					(maxScrollArea.width - fontMetrics.stringWidth(versionText)) / 2,
					versionHeight);
			}
		}
		else
		{
			g = (Graphics2D) offscreenImage.getGraphics();
		}


		g.setFont(getFont());

		drawScrollArea(g);
		g.dispose();
	}


	/**
	 * Updates the offscreen-image to represent the current scroll-position. It
	 * calls <code>initScrollImage()</code>.
	 *
	 * @param g <code>Graphics</code>-object
	 */
	private void drawScrollArea(Graphics2D g)
	{
		initScrollImage();
		// only draw in the scroll-area
		g.setClip(scrollArea.x, scrollArea.y, scrollArea.width, scrollArea.height);
		// clear background for transparent bg-images
		g.setColor(getBackground());
		g.fillRect(scrollArea.x, scrollArea.y, scrollArea.width, scrollArea.height);
		// draw background-image
		g.drawImage(backgroundImage, 0, 0, this);
		// redraw version-text if available.
		if(versionText != null)
		{
			g.setColor(getForeground());

			g.drawString(versionText, maxScrollArea.x +
				(maxScrollArea.width - fontMetrics.stringWidth(versionText)) / 2,
				versionHeight);
		}
		// draw proper part of precalculated scroll-image.
		g.drawImage(scrollImage, scrollArea.x,
			scrollArea.y - scrollPosition, this);
		if(debug)
		{
			g.setColor(Color.YELLOW);
			g.drawRect(scrollArea.x, scrollArea.y, scrollArea.width - 1, scrollArea.height - 1);
		}
	}


	/**
	 * Initializes the scroll-image if needed. The scroll-image is as high as
	 * needed to contain all the scroll-lines and (if available) the image.
	 */
	private void initScrollImage()
	{
		int fontHeight = fontMetrics.getHeight();

		maxScrollPosition = fontHeight * (scrollLines.length);

		int additionalImageOffset = 0;
		int imageWidth = 0;

		if(aboutImage != null)
		{
			imageWidth = aboutImage.getWidth();
			additionalImageOffset = aboutImage.getHeight() + 2 * fontHeight;
			maxScrollPosition = maxScrollPosition + additionalImageOffset;
		}

		if(scrollImage != null && scrollImage.getHeight() != maxScrollPosition)
		{
			flushScrollImage();
		}
		if(scrollImage == null)
		{
			int maxWidth = imageWidth + 2 * fontHeight;
			if(logger.isInfoEnabled()) logger.info("imageWidth={}, maxWidth={}", imageWidth, maxWidth);
			for(String scrollLine : scrollLines)
			{
				int curWidth = fontMetrics.stringWidth(scrollLine);

				if(curWidth > maxWidth)
				{
					maxWidth = curWidth;
				}
			}
			if(maxWidth > maxScrollArea.width)
			{
				if(logger.isInfoEnabled()) logger.info("maxWidth={} != maxScrollArea=", maxWidth, maxScrollArea);
				maxWidth = maxScrollArea.width;
			}

			scrollArea.x = maxScrollArea.x + (maxScrollArea.width - maxWidth) / 2;
			scrollArea.y = maxScrollArea.y;
			scrollArea.width = maxWidth;
			scrollArea.height = maxScrollArea.height;

			scrollImage = GraphicsUtilities.createTranslucentCompatibleImage(scrollArea.width, maxScrollPosition);

			Color foreground = getForeground();


			Graphics2D g;
			g = (Graphics2D) scrollImage.getGraphics();

			g.setFont(getFont());

			if(aboutImage != null)
			{
				g.drawImage(aboutImage, (((scrollArea.width - imageWidth) / 2)), fontHeight, null);
			}
			g.setColor(foreground);

			int y = fontMetrics.getAscent() + additionalImageOffset;

			for(String line : scrollLines)
			{
				g.drawString(line, (scrollArea.width
					- fontMetrics.stringWidth(line)) / 2, y);
				y += fontHeight;
			}
			g.dispose();

			BufferedImage copy = GraphicsUtilities.createCompatibleCopy(scrollImage);
			BufferedImageOp filter;
			final int blurSize = 10;
			filter = getGaussianBlurFilter(blurSize, false);
			scrollImage = filter.filter(scrollImage, null);

			filter = getGaussianBlurFilter(blurSize, true);
			scrollImage = filter.filter(scrollImage, null);


			filter = new ColorTintFilter(Color.GREEN, 1.0f);
			scrollImage = filter.filter(scrollImage, null);

			g = (Graphics2D) scrollImage.getGraphics();

			g.setComposite(AlphaComposite.SrcOver);
			g.drawImage(copy, 0, 0, null);

			if(debug)
			{
				g.setColor(Color.RED);
				g.drawRect(0, 0, scrollImage.getWidth() - 1, scrollImage.getHeight() - 1);

				g.setColor(Color.GREEN);
				g.drawRect((((scrollArea.width - imageWidth) / 2)), fontHeight, aboutImage.getWidth(), aboutImage.getHeight());
			}

			g.dispose();
			copy.flush();
		}


	}

	public static ConvolveOp getGaussianBlurFilter(int radius, boolean horizontal)
	{
		final Logger logger = LoggerFactory.getLogger(AboutPanel.class);

		if(radius < 1)
		{
			throw new IllegalArgumentException("Radius must be >= 1");
		}

		int size = radius * 2 + 1;
		float[] data = new float[size];

		float sigma = radius / 3.0f;
		float twoSigmaSquare = 2.0f * sigma * sigma;
		float sigmaRoot = (float) Math.sqrt(twoSigmaSquare * Math.PI);
		float total = 0.0f;

		for(int i = -radius; i <= radius; i++)
		{
			float distance = i * i;
			int index = i + radius;
			data[index] = (float) Math.exp(-distance / twoSigmaSquare) / sigmaRoot;
			total += data[index];
		}

		for(int i = 0; i < data.length; i++)
		{
			data[i] /= total;
			if(logger.isDebugEnabled()) logger.debug("data[{}]={}", i, data[i]);
		}

		Kernel kernel = null;
		if(horizontal)
		{
			kernel = new Kernel(size, 1, data);
		}
		else
		{
			kernel = new Kernel(1, size, data);
		}
		return new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
	}

	/**
	 * Sets the font attribute of the <code>AboutPanel</code> object. Setting it
	 * will result in the recreation of all buffers. The font can even be safely
	 * changed while the component is visible. It will be used for the version- and
	 * scroll-text.<p />
	 * <p/>
	 * If the parameter is <code>null</code> then <code>UIManager.getFont( "Label.font" )</code>
	 * will be used.
	 *
	 * @param newFont The new font value.
	 */
	public void setFont(Font newFont)
	{
		if(newFont == null)
		{
			newFont = UIManager.getFont("Label.font");
		}
		if(newFont != null && !newFont.equals(getFont()))
		{
			super.setFont(newFont);
			fontMetrics = getFontMetrics(newFont);
			flushScrollImage();
		}
	}


	/*
	FontRenderContext frc = g2.getFontRenderContext();
        Font f = new Font("sansserif",Font.PLAIN,w/8);
        Font f1 = new Font("sansserif",Font.ITALIC,w/8);
        String s = "AttributedString";
        AttributedString as = new AttributedString(s);


        // applies the TextAttribute.Font attribute to the AttributedString
        // with the range 0 to 10, which encompasses the letters 'A' through
        // 'd' of the String "AttributedString"
        as.addAttribute(TextAttribute.FONT, f, 0, 10 );

        // applies the TextAttribute.Font attribute to the AttributedString
        // with the range 10 to the length of the String s, which encompasses
        // the letters 'S' through 'g' of String "AttributedString"
        as.addAttribute(TextAttribute.FONT, f1, 10, s.length() );

        AttributedCharacterIterator aci = as.getIterator();

        // creates a TextLayout from the AttributedCharacterIterator
        TextLayout tl = new TextLayout (aci, frc);
        float sw = (float) tl.getBounds().getWidth();
        float sh = (float) tl.getBounds().getHeight();

        // creates an outline shape from the TextLayout and centers it
        // with respect to the width of the surface
        Shape sha = tl.getOutline(AffineTransform.getTranslateInstance(w/2-sw/2, h*0.2+sh/2));
        g2.setColor(Color.blue);
        g2.setStroke(new BasicStroke(1.5f));
        g2.draw(sha);
        g2.setColor(Color.magenta);
        g2.fill(sha);
	*/

	/**
	 * Paints this component.
	 *
	 * @param _g <code>Graphics</code>-object
	 */
	public void paintComponent(Graphics _g)
	{
		super.paintComponent(_g);

		processOffscreenImage();

		// we need to create a copy of the given graphics since we
		// change the clip. Otherwise the border wouldn't be painted
		// propertly (not at all in this case).
		Graphics2D g = (Graphics2D) _g.create();

		g.setClip(paintArea.x, paintArea.y, paintArea.width, paintArea.height);

		g.drawImage(offscreenImage, paintArea.x + offscreenOffset.x, paintArea.y + offscreenOffset.y, this);
		g.dispose();
		//setPainted(true);
	}


	/**
	 * Makes sure that the private attributes size, paintArea, offscreenOffset and
	 * translated areas have sane values. It's called on component-resize.
	 */
	private void calculateAttributes()
	{
		size = getSize(size);

		paintArea.x = insets.left;
		paintArea.y = insets.top;
		paintArea.width = size.width - insets.left - insets.right;
		paintArea.height = size.height - insets.top - insets.bottom;

		int loOffscreenOffsetX = (paintArea.width - preferredSize.width) / 2;
		int loOffscreenOffsetY = (paintArea.height - preferredSize.height) / 2;

		if(loOffscreenOffsetX < 0)
		{
			loOffscreenOffsetX = 0;
		}
		if(loOffscreenOffsetY < 0)
		{
			loOffscreenOffsetY = 0;
		}
		offscreenOffset.x = loOffscreenOffsetX;
		offscreenOffset.y = loOffscreenOffsetY;

		translatedScrollArea.x = maxScrollArea.x + offscreenOffset.x;
		translatedScrollArea.y = maxScrollArea.y + offscreenOffset.y;
		translatedScrollArea.width = maxScrollArea.width;
		translatedScrollArea.height = maxScrollArea.height;

		translatedBackgroundImageArea.x = backgroundImageArea.x + offscreenOffset.x;
		translatedBackgroundImageArea.y = backgroundImageArea.y + offscreenOffset.y;
		translatedBackgroundImageArea.width = backgroundImageArea.width;
		translatedBackgroundImageArea.height = backgroundImageArea.height;
		repaint();
	}


	/**
	 * This methods takes the insets (the border) of this component into account
	 * when the preferred size is calculated. Any border will work. It is called by
	 * the property-change-listener if the border was changed.
	 */
	protected void calculatePreferredSize()
	{
		insets = getInsets(insets);
		preferredSize.width = insets.left + insets.right + backgroundImageArea.width;
		preferredSize.height = insets.top + insets.bottom + backgroundImageArea.height;
		setPreferredSize(preferredSize);
		invalidate();
	}


	/**
	 * This method requests a repaint of the scroll-area. The rest of the component
	 * will not be repainted. It is called by <code>setScrollPosition()</code> .
	 *
	 * @see
	 */
	private void repaintScrollArea()
	{
		//setPainted(false);

		repaint(scrollArea.x + offscreenOffset.x,
			scrollArea.y + offscreenOffset.y,
			scrollArea.width, // + 1,
			scrollArea.height);// + 1 );
	}


	/**
	 * This method calls <code>super.addNotify()</code> and notifies the
	 * scroll-thread by calling <code>setScrolling(true)</code>. It also
	 * (re)initializes the scroll-position to MinimumScrollPosition (this is always
	 * the negative height of the scroll-rectangle) and registers tbis component at
	 * the <code>ToolTipManager</code>.
	 *
	 * @see #setScrolling
	 * @see #setScrollPosition
	 * @see #getMinimumScrollPosition
	 */
	public void addNotify()
	{
		super.addNotify();

		setScrolling(true);
		ToolTipManager.sharedInstance().registerComponent(this);
	}


	/**
	 * This method calls <code>super.removeNotify()</code> and sends the
	 * scroll-thread into a wait-state by calling <code>setScrolling(false)</code>
	 * . It also unregisters this component from the <code>ToolTipManager</code>.
	 *
	 * @see #setScrolling
	 */
	public void removeNotify()
	{
		super.removeNotify();

		setScrolling(false);
		ToolTipManager.sharedInstance().unregisterComponent(this);

		// flush used buffer-images.
		flushOffscreenImage();
		flushScrollImage();
	}


	/**
	 * This method is used to set the scrolling-property of this component. A value
	 * of <code>true</code> will notify the scroll-thread that it has to resume
	 * work. A value of <code>false</code> will send it into wait-state instead.
	 *
	 * @param Scrolling The new scrolling value
	 */
	public void setScrolling(boolean Scrolling)
	{
		if(scrolling != Scrolling)
		{
			scrolling = Scrolling;
			if(scrolling)
			{
				timer.start();
				if(logger.isInfoEnabled()) logger.info("Timer started.");
			}
			else
			{
				timer.stop();
				if(logger.isInfoEnabled()) logger.info("Timer stopped.");
			}
		}
	}


	/**
	 * This method returns <code>true</code> if scrolling is currently active. If
	 * it returns <code>false</code> then the scroll-thread is waiting.
	 *
	 * @return The scrolling value
	 */
	public boolean isScrolling()
	{
		return scrolling;
	}


	/**
	 * Description of the Class
	 *
	 * @author Joern Huxhorn
	 */
	class AboutComponentListener
		extends ComponentAdapter
	{
		/**
		 * Description of the Method
		 *
		 * @param e Description of the Parameter
		 */
		public void componentResized(ComponentEvent e)
		{
			AboutPanel.this.calculateAttributes();
		}
	}


	/**
	 * Description of the Class
	 *
	 * @author Joern Huxhorn
	 */
	class AboutPropertyChangeListener
		implements PropertyChangeListener
	{
		/**
		 * Description of the Method
		 *
		 * @param evt Description of the Parameter
		 */
		public void propertyChange(PropertyChangeEvent evt)
		{
			String propertyName = evt.getPropertyName();

			if(propertyName.equals("border"))
			{
				calculatePreferredSize();
			}
			else if(propertyName.equals("foreground"))
			{
				flushScrollImage();
			}
			else if(propertyName.equals("background"))
			{
				flushScrollImage();
			}
//			else if ( propertyName.equals( "locale" ) )
//			{
//				initResources();
//			}
		}
	}


	/**
	 * This <code>MouseInputListener</code> handles the pause/resume on click as
	 * well as the dragging inside the scroll-area.
	 *
	 * @author Joern Huxhorn
	 */
	class AboutMouseInputListener
		extends MouseInputAdapter
	{
		Point lastPoint = null;
		boolean scrollingBeforePress = false;
		boolean dragged = false;


		/**
		 * Description of the Method
		 *
		 * @param evt Description of the Parameter
		 */
		public void mousePressed(MouseEvent evt)
		{
			if(handleMouseEvent(evt))
			{
				// always stop scrolling if mouse is pressed inside
				// the scroll-area
				lastPoint = evt.getPoint();
				scrollingBeforePress = isScrolling();
				setScrolling(false);
			}
			else
			{
				lastPoint = null;
			}
			dragged = false;
		}


		/**
		 * Description of the Method
		 *
		 * @param evt Description of the Parameter
		 */
		public void mouseReleased(MouseEvent evt)
		{
			if(dragged)
			{
				// set scrolling-attribute to the value before the user dragged.
				lastPoint = null;
				setScrolling(scrollingBeforePress);
			}
		}


		/**
		 * Description of the Method
		 *
		 * @param evt Description of the Parameter
		 */
		public void mouseClicked(MouseEvent evt)
		{
			// this is only called after mouseReleased if no drag occurred.
			if(handleMouseEvent(evt))
			{
				// toggle scrolling.
				setScrolling(!scrollingBeforePress);
			}
			dragged = false;
		}


		/**
		 * Description of the Method
		 *
		 * @param evt Description of the Parameter
		 */
		public void mouseDragged(MouseEvent evt)
		{
			// only drag if original press was inside scroll-rectangle
			if(lastPoint != null)
			{
				dragged = true;

				Point currentPoint = evt.getPoint();
				int yOffset = lastPoint.y - currentPoint.y;

				setScrollPosition(getScrollPosition() + yOffset);
				lastPoint = currentPoint;
			}
		}
	}


	private class TimerActionListener
		implements ActionListener
	{
		private final Logger logger = LoggerFactory.getLogger(AboutPanel.class);

		private long lastRepaintStart;
		private long frequency = 25;

		public void actionPerformed(ActionEvent e)
		{
			long currentTime = System.nanoTime() / 1000000;
			long meanTime = currentTime - lastRepaintStart;
			if(meanTime > frequency)
			{
				if(logger.isDebugEnabled()) logger.debug("Tick! meanTime={}", meanTime);
				increaseScrollPosition();
				lastRepaintStart = currentTime;
			}
		}
	}

//	public static class Example
//	{
//		private JFrame dummyFrame;
//		private JDialog dialog;
//
//		private AboutPanel theAboutPanel;
//		private static JFileChooser chooser=new JFileChooser(new File("."));
//
//		abstract class SelectionAction extends AbstractAction
//		{
//			protected SelectionAction(String name)
//			{
//				super(name);
//			}
//
//			public abstract void setImage(URL url);
//
//			public void actionPerformed(ActionEvent evt)
//			{
//				int returnVal = chooser.showOpenDialog(dialog);
//				if(returnVal == JFileChooser.APPROVE_OPTION)
//				{
//					File file = chooser.getSelectedFile();
//					System.out.println("You chose to open this file: " + file.getName());
//					try
//					{
//						setImage(file.toURL());
//						AboutPanel.Example.this.theAboutPanel.setScrollArea(null);
//						dialog.pack();
//					}
//					catch(Exception loEx)
//					{
//						System.out.println(loEx);
//					}
//				}
//			}
//		}
//
//		class ImageSelectionAction extends SelectionAction
//		{
//			ImageSelectionAction()
//			{
//				super("Select image");
//			}
//
//			public void setImage(URL url)
//			{
//				AboutPanel.Example.this.theAboutPanel.setAboutImage(url);
//			}
//		}
//
//		class BackgroundImageSelectionAction extends SelectionAction
//		{
//			BackgroundImageSelectionAction()
//			{
//				super("Select background-image");
//			}
//
//			public void setImage(URL url)
//			{
//				AboutPanel.Example.this.theAboutPanel.setBackgroundImage(url);
//			}
//		}
//
//		class CloseAction extends AbstractAction
//		{
//			CloseAction()
//			{
//				super("Close");
//			}
//
//			public void actionPerformed( ActionEvent evt )
//			{
//				dialog.dispose();
//			}
//		}
//
//		class FontSizeAction extends AbstractAction
//		{
//			int fontChange;
//
//
//			public FontSizeAction( String name, int FontChange )
//			{
//				super(name);
//				fontChange = FontChange;
//			}
//
//
//			public void actionPerformed( java.awt.event.ActionEvent evt )
//			{
//				Font loFont = AboutPanel.Example.this.theAboutPanel.getFont();
//				float loSize = loFont.getSize2D() + fontChange;
//
//				AboutPanel.Example.this.theAboutPanel.setFont( loFont.deriveFont( loSize ) );
//			}
//		}
//
//		abstract class ChooseColorAction extends AbstractAction
//		{
//			private JColorChooser chooser;
//			private String chooserTitle;
//
//			public ChooseColorAction( String name)//, String chooserTitle)
//			{
//				super(name);
//				chooserTitle=name;
//				chooser=new JColorChooser();
////			this.chooserTitle=chooserTitle;
//			}
//
//
//			public void actionPerformed( java.awt.event.ActionEvent evt )
//			{
//				Color c=chooser.showDialog(dialog, chooserTitle, getSelectColor());
//				if(c!=null)
//				{
//					setSelectColor(c);
//				}
//			}
//
//			public abstract Color getSelectColor();
//			public abstract void setSelectColor(Color c);
//		}
//
//		class ChooseBackgroundColorAction extends ChooseColorAction
//		{
//			public ChooseBackgroundColorAction()
//			{
//				super("Choose background-color");
//			}
//
//			public Color getSelectColor()
//			{
//				return AboutPanel.Example.this.theAboutPanel.getBackground();
//			}
//
//			public void setSelectColor(Color c)
//			{
//				AboutPanel.Example.this.theAboutPanel.setBackground(c);
//			}
//		}
//
//		class ChooseTextColorAction extends ChooseColorAction
//		{
//			public ChooseTextColorAction()
//			{
//				super("Choose text-color");
//			}
//
//			public Color getSelectColor()
//			{
//				return AboutPanel.Example.this.theAboutPanel.getForeground();
//			}
//
//			public void setSelectColor(Color c)
//			{
//				AboutPanel.Example.this.theAboutPanel.setForeground(c);
//			}
//		}
//
//		class ResetAction extends AbstractAction
//		{
//			public ResetAction()
//			{
//				super("Reset dialog");
//			}
//
//			public void actionPerformed(ActionEvent evt)
//			{
//				AboutPanel panel = AboutPanel.Example.this.theAboutPanel;
////				panel.initResources();
//				dialog.pack();
//			}
//		}
//
//		class PackAction extends AbstractAction
//		{
//			public PackAction()
//			{
//				super("Pack dialog");
//			}
//
//			public void actionPerformed(ActionEvent evt)
//			{
//				dialog.pack();
//			}
//		}
//
//		class ShowDialogAction extends AbstractAction
//		{
//			public ShowDialogAction()
//			{
//				super("Show dialog");
//			}
//
//			public void actionPerformed(ActionEvent evt)
//			{
//				dialog.setVisible(true);
//			}
//		}
//
//		class ExitMenuAction extends AbstractAction
//		{
//			public ExitMenuAction()
//			{
//				super("Exit");
//			}
//
//			public void actionPerformed(ActionEvent evt)
//			{
//				exit();
//			}
//		}
//
//		// TODO: Select font
//		// TODO: Select scroll-area
//		// TODO: Select scroll-text
//		// TODO: Select version-text/height
//
//		public Example()
//		{
//			JMenuBar menuBar=new JMenuBar();
//			dummyFrame=new JFrame( "DummyFrame" );
//			dummyFrame.setDefaultCloseOperation( javax.swing.JFrame.EXIT_ON_CLOSE );
//			dummyFrame.setJMenuBar(menuBar);
//			JMenu fileMenu=new JMenu("File");
//			menuBar.add(fileMenu);
//			fileMenu.add(new JMenuItem(new ShowDialogAction()));
//			fileMenu.addSeparator();
//			fileMenu.add(new JMenuItem(new ExitMenuAction()));
//
//			dummyFrame.setBounds(10,10,100,100);
//			dialog = new JDialog( dummyFrame, "About example", false );
//
//			JPanel content = new JPanel( new BorderLayout() );
//
//			dialog.setContentPane( content );
//			content.setBorder( new EmptyBorder( 12, 12, 12, 12 ) );
//
//
//			AboutPanel aboutPanel = new AboutPanel();
//
//			content.add(BorderLayout.CENTER,aboutPanel);
//
//			theAboutPanel=aboutPanel;
//
//			CloseAction closeAction = new CloseAction();
//			JButton closeButton = new JButton( closeAction );
//
//			dialog.getRootPane().setDefaultButton( closeButton );
//			JPanel buttonPanel = new JPanel();
//
//			buttonPanel.setLayout( new BoxLayout( buttonPanel, BoxLayout.X_AXIS ) );
//			buttonPanel.setBorder( new EmptyBorder( 12, 0, 0, 0 ) );
//			buttonPanel.add( Box.createGlue() );
//			buttonPanel.add( closeButton );
//			buttonPanel.add( Box.createGlue() );
//			content.add( BorderLayout.SOUTH, buttonPanel );
//
//			JMenuBar dialogBar=new JMenuBar();
//			dialog.setJMenuBar(dialogBar);
//			JMenu optionsMenu=new JMenu("Options");
//			dialogBar.add(optionsMenu);
//
//			JMenuItem bgImageItem = new JMenuItem( new BackgroundImageSelectionAction() );
//			JMenuItem imageItem = new JMenuItem( new ImageSelectionAction() );
//			JMenuItem textColorItem = new JMenuItem( new ChooseTextColorAction() );
//			JMenuItem bgColorItem = new JMenuItem( new ChooseBackgroundColorAction() );
//			JMenuItem fontPlusItem = new JMenuItem( new FontSizeAction("Increase font-size", 1));
//			JMenuItem fontMinusItem = new JMenuItem(  new FontSizeAction("Decrease font-size", -1));
//			JMenuItem packItem = new JMenuItem( new PackAction() );
//			JMenuItem resetItem = new JMenuItem( new ResetAction() );
//			JMenuItem closeItem = new JMenuItem( closeAction );
//			JMenuItem exitItem = new JMenuItem( new ExitMenuAction() );
//
//			optionsMenu.add( bgImageItem );
//			optionsMenu.add( imageItem );
//			optionsMenu.add( textColorItem );
//			optionsMenu.add( bgColorItem );
//			optionsMenu.add( fontPlusItem );
//			optionsMenu.add( fontMinusItem );
//			optionsMenu.addSeparator();
//			optionsMenu.add( packItem );
//			optionsMenu.add( resetItem );
//			optionsMenu.addSeparator();
//			optionsMenu.add( closeItem );
//			optionsMenu.add( exitItem );
//
//			dialog.pack();
//			dummyFrame.setVisible(true);
//		}
//
//		public void showDialog()
//		{
//			dialog.setVisible(true);
//		}
//
//		public void exit()
//		{
//			System.exit(0);
//		}
//
//		public static void main(String args[])
//		{
//            Example example=new Example();
//			example.showDialog();
//		}
//	}
}

