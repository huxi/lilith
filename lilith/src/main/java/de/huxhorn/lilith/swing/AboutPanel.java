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

package de.huxhorn.lilith.swing;

import de.huxhorn.sulky.swing.GraphicsUtilities;
import de.huxhorn.sulky.swing.filters.ColorTintFilter;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
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
import java.util.Objects;
import java.util.StringTokenizer;
import javax.swing.JComponent;
import javax.swing.Timer;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.event.MouseInputAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: get/setMouseHandling/MouseInputMode
// TODO: setVersionHeight(versionHeight);
// TODO: setVersionString(versionString);
// TODO: correct versionHeight if string would be outside background.
// TODO: VersionString centered to bg/scroll.
// TODO: relative ScrollAreas (values given as % of backgroundImage)
// TODO: Handle errors in Image-Loading
// TODO: offscreenImage hoechstens so gross wie die size / nicht gesamten bg malen
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
public final class AboutPanel
	extends JComponent
{
	private static final long serialVersionUID = -1152941907500323104L;

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

	private BufferedImage backgroundImage;
	private BufferedImage aboutImage;
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
	private final int mouseEventHandling = MOUSE_BACKGROUND;
	private boolean debug;
	private Timer timer;

	/**
	 * Creates a new <code>AboutPanel</code> initialized with the given parameters.
	 *
	 * @param backgroundImageUrl The URL to the Background-Image of the
	 *                           AboutPanel. This parameter is mandatory.
	 * @param scrollArea         The Rectangle inside the background-image where
	 *                           scrolling should take place. This parameter is optional. If it's null
	 *                           then the scroll-area is set to (0, 0, background.width,
	 *                           background.height).
	 * @param scrollText The text that will be scrolled.
	 * @throws java.io.IOException If receiving content from backgroundImageUrl fails
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
	 * @param scrollText         The text to be scrolled.
	 * @param versionText        The String describing the version of the program.
	 *                           It is painted centered to the scroll-rectangle at the specified height.
	 *                           This parameter is optional.
	 * @param versionHeight      The height at which the version-string is
	 *                           supposed to be painted. This parameter is optional but should be given
	 *                           a correct value if versionText!=null..
	 * @throws IOException       if loading the images failed.
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
	 * @param scrollText         The text to be scrolled.
	 * @param imageUrl           The URL to the Image that will be painted at the
	 *                           start of the scroll-area. This parameter is optional.
	 * @param versionText        The String describing the version of the program.
	 *                           It is painted centered to the scroll-rectangle at the specified height.
	 *                           This parameter is optional.
	 * @param versionHeight      The height at which the version-string is
	 *                           supposed to be painted. This parameter is optional but should be given
	 *                           a correct value if versionText!=null..
	 * @throws IOException       if loading the images failed.
	 */
	public AboutPanel(URL backgroundImageUrl, Rectangle scrollArea, String scrollText, URL imageUrl, String versionText, int versionHeight)
		throws IOException
	{
		this();
		Objects.requireNonNull(backgroundImageUrl, "backgroundImageUrl must not be null!");
		Objects.requireNonNull(scrollText, "scrollText must not be null!");
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

		setScrolling(false);
	}

	private void init(URL backgroundImageUrl, Rectangle scrollArea, String scrollText, URL imageUrl, String versionText, int versionHeight)
		throws IOException
	{
		if(logger.isDebugEnabled()) logger.debug("init called with following arguments: backgroundImageUrl={}, scrollArea={}, scrollText={}, imageUrl={}, versionText={}, versionHeight={}",
				backgroundImageUrl, scrollArea, scrollText, imageUrl, versionText, versionHeight);
		setBackgroundImage(backgroundImageUrl);
		setScrollArea(scrollArea);
		setAboutImage(imageUrl);
		this.versionText = versionText;
		this.versionHeight = versionHeight;
		setScrollText(scrollText);
	}

	private void initAttributes()
	{
		preferredSize = new Dimension();
		offscreenOffset = new Point();
		backgroundImageArea = new Rectangle();
		translatedScrollArea = new Rectangle();
		translatedBackgroundImageArea = new Rectangle();
		scrollArea = new Rectangle();
		paintArea = new Rectangle();
		insets = getInsets();
	}


	public void setScrollText(String scrollText)
	{
		StringTokenizer st = new StringTokenizer(scrollText, "\n", true);

		List<String> lines = new ArrayList<>(st.countTokens() / 2);
		String prevToken = null;
		while(st.hasMoreTokens())
		{
			String token = st.nextToken();
			if("\n".equals(token))
			{
				if(prevToken != null && !"\n".equals(prevToken))
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
		if(prevToken != null && !"\n".equals(prevToken))
		{
			lines.add(prevToken);
		}

		String[] loScrollLines = new String[lines.size()];
		loScrollLines = lines.toArray(loScrollLines);
		setScrollLines(loScrollLines);
	}

	protected void setScrollLines(String[] scrollLines)
	{
		Objects.requireNonNull(scrollLines, "scrollLines must not be null!");
		this.scrollLines = scrollLines.clone();
		flushScrollImage();
	}

	/**
	 * Sets the backgroundImage attribute of the <code>AboutPanel</code> object
	 *
	 * @param imageUrl the image to be used as background.
	 * @throws IOException if loading of image fails.
	 */
	public void setBackgroundImage(URL imageUrl)
		throws IOException
	{
		setBackgroundImage(GraphicsUtilities.loadCompatibleImage(imageUrl));
	}


	/**
	 * Sets the backgroundImage attribute of the <code>AboutPanel</code> object
	 *
	 * @param backgroundImage The new backgroundImage value
	 */
	public void setBackgroundImage(BufferedImage backgroundImage)
	{
		if(this.backgroundImage != null)
		{
			this.backgroundImage.flush();
			this.backgroundImage = null;
		}
		this.backgroundImage = backgroundImage;
		updateBackgroundAttributes();
	}


	public void setAboutImage(URL imageUrl)
		throws IOException
	{
		setAboutImage(GraphicsUtilities.loadCompatibleImage(imageUrl));
	}


	public void setAboutImage(BufferedImage aboutImage)
	{
		if(this.aboutImage != null)
		{
			this.aboutImage.flush();
			this.aboutImage = null;
		}
		this.aboutImage = aboutImage;
		flushScrollImage();
	}

	/**
	 * Sets the scrollArea attribute of the <code>AboutPanel</code> object
	 *
	 * @param scrollArea The new scrollArea value
	 */
	public void setScrollArea(Rectangle scrollArea)
	{
		if(scrollArea != null)
		{
			maxScrollArea = backgroundImageArea.intersection(scrollArea);
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
	 * is inside the scroll-area and <code>null</code> otherwise.
	 *
	 * It's needed by the <code>ToolTipManager</code>.
	 *
	 * @param evt a <code>MouseEvent</code>.
	 * @return The toolTipText value for the <code>ToolTipManager</code>.
	 */
	@Override
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
		// MOUSE_BACKGROUND / MOUSE_SCROLLAREA
		return loArea.contains(loPoint);
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
				if(logger.isInfoEnabled()) logger.info("maxWidth={} != maxScrollArea={}", maxWidth, maxScrollArea);
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
			final int blurSize = 10; // NOPMD
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

		for(int i = -radius; i <= radius; i++) // NOPMD
		{
			float distance = i * i;
			int index = i + radius;
			data[index] = (float) Math.exp(-distance / twoSigmaSquare) / sigmaRoot;
			total += data[index];
		}

		for(int i = 0; i < data.length; i++)
		{
			data[i] /= total;
		}

		Kernel kernel;
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
	 * scroll-text.
	 *
	 * If the parameter is <code>null</code> then <code>UIManager.getFont( "Label.font" )</code>
	 * will be used.
	 *
	 * @param newFont The new font value.
	 */
	@Override
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


	/**
	 * Paints this component.
	 *
	 * @param graphics <code>Graphics</code>-object
	 */
	@Override
	public void paintComponent(Graphics graphics)
	{
		super.paintComponent(graphics);

		processOffscreenImage();

		// we need to create a copy of the given graphics since we
		// change the clip. Otherwise the border wouldn't be painted
		// properly (not at all in this case).
		Graphics2D graphics2D = (Graphics2D) graphics.create();

		graphics2D.setClip(paintArea.x, paintArea.y, paintArea.width, paintArea.height);

		graphics2D.drawImage(offscreenImage, paintArea.x + offscreenOffset.x, paintArea.y + offscreenOffset.y, this);
		graphics2D.dispose();
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
	@Override
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
	@Override
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
	 * @param scrolling The new scrolling value
	 */
	public void setScrolling(boolean scrolling)
	{
		if(this.scrolling != scrolling)
		{
			this.scrolling = scrolling;
			if(this.scrolling)
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
		@Override
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
		@Override
		public void propertyChange(PropertyChangeEvent evt)
		{
			String propertyName = evt.getPropertyName();

			if("border".equals(propertyName))
			{
				calculatePreferredSize();
			}
			else if("foreground".equals(propertyName))
			{
				flushScrollImage();
			}
			else if("background".equals(propertyName))
			{
				flushScrollImage();
			}
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
		@Override
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
		@Override
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
		@Override
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
		@Override
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
		private static final long FREQUENCY = 25;

		private final Logger logger = LoggerFactory.getLogger(AboutPanel.class);

		private long lastRepaintStart;

		@Override
		public void actionPerformed(ActionEvent e)
		{
			long currentTime = System.nanoTime() / 1_000_000;
			long meanTime = currentTime - lastRepaintStart;
			if(meanTime > FREQUENCY)
			{
				if(logger.isDebugEnabled()) logger.debug("Tick! meanTime={}", meanTime);
				increaseScrollPosition();
				lastRepaintStart = currentTime;
			}
		}
	}
}

