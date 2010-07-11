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
package de.huxhorn.lilith.swing.statistics;

import de.huxhorn.lilith.data.eventsource.SourceIdentifier;
import de.huxhorn.lilith.swing.EventWrapperViewPanel;
import de.huxhorn.lilith.swing.MainFrame;
import de.huxhorn.lilith.swing.filefilters.PngFileFilter;

import de.huxhorn.sulky.io.IOUtilities;
import org.rrd4j.core.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;

public class StatisticsPanel
	extends JPanel
{
	private final Logger logger = LoggerFactory.getLogger(StatisticsPanel.class);

	private static final long REFRESH_DELAY = 10000;

	private SourceIdentifier sourceIdentifier;
	private MainFrame mainFrame;
	private boolean running;
	private GraphImageProducer[] graphImageFactories;
	private int selectedGraph;
	private JComboBox timerangeComboBox;
	private JFileChooser saveFileChooser;
	private BufferedImage[] imageToggle;
	private int imageIndex;
	private JLabel graphLabel;
	private JCheckBox showMaxCheckBox;
	private JComboBox sourcesComboBox;
	private Thread updateThread;
	public static final String SOURCE_IDENTIFIER_PROPERTY = "sourceIdentifier";
	private Object[] previousSourcesArray;

	public StatisticsPanel(MainFrame owner)
	{
		this.mainFrame = owner;
		this.imageToggle = new BufferedImage[2];
		this.imageIndex = 0;
		this.running = false;
		createUI();
	}

	public void setSourceIdentifier(SourceIdentifier sourceIdentifier)
	{
		Object oldValue = this.sourceIdentifier;
		this.sourceIdentifier = sourceIdentifier;
		Object newValue = this.sourceIdentifier;
		initUI();
		updateGraph();
		firePropertyChange(SOURCE_IDENTIFIER_PROPERTY, oldValue, newValue);
	}


	public SourceIdentifier getSourceIdentifier()
	{
		return sourceIdentifier;
	}

	private void createUI()
	{
		ShowMaxAction showMaxAction = new ShowMaxAction();
		showMaxCheckBox = new JCheckBox(showMaxAction);
		showMaxCheckBox.setOpaque(false);
		showMaxCheckBox.setSelected(true);
		saveFileChooser = new JFileChooser();
		saveFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		FileFilter pngFileFilter = new PngFileFilter();
		saveFileChooser.setFileFilter(pngFileFilter);
		graphImageFactories = new GraphImageProducer[]{
			new TwentyMinutesProducer(mainFrame),
			new TwoHoursProducer(mainFrame),
			new TenHoursProducer(mainFrame),
			new OneDayProducer(mainFrame),
			new SevenDaysProducer(mainFrame),
			new ThirtyDaysProducer(mainFrame),
			new NinetyDaysProducer(mainFrame),
			new OneYearProducer(mainFrame),
		};

		JPanel graphPanel = new JPanel(new GridLayout(1, 1));
		graphLabel = new JLabel();
		graphPanel.add(graphLabel);

		setLayout(new BorderLayout());
		add(graphPanel, BorderLayout.CENTER);
		timerangeComboBox = new JComboBox(new Object[]{
			"20 minutes",
			"2 hours",
			"10 hours",
			"1 day",
			"7 days",
			"30 days",
			"90 days",
			"1 year",
		});
		timerangeComboBox.addActionListener(new TimerangeActionListener());

		sourcesComboBox = new JComboBox();
		sourcesComboBox.addActionListener(new SourcesActionListener());

		JToolBar toolbar = new JToolBar();
		toolbar.setFloatable(false);
		toolbar.add(new JLabel("Source: "));
		toolbar.add(sourcesComboBox);
		toolbar.addSeparator();
		toolbar.add(new JLabel("Timerange: "));
		toolbar.add(timerangeComboBox);
		toolbar.add(showMaxCheckBox);
		toolbar.addSeparator();
		toolbar.add(new JButton(new SaveAction()));
		add(toolbar, BorderLayout.NORTH);
		setSelectedGraph(0);
	}

	private void initUI()
	{
		SortedMap<String, SourceIdentifier> stats = mainFrame.getAvailableStatistics();

		List<WrappedSourceIdentifier> wrappedSources = new ArrayList<WrappedSourceIdentifier>(stats.size() + 1);
		wrappedSources.add(new WrappedSourceIdentifier("Global", new SourceIdentifier("global")));
		for(Map.Entry<String, SourceIdentifier> current : stats.entrySet())
		{
			WrappedSourceIdentifier wrapped = new WrappedSourceIdentifier(current.getKey(), current.getValue());
			wrappedSources.add(wrapped);
		}
		Object[] newSourcesArray = wrappedSources.toArray();
		if(!Arrays.equals(previousSourcesArray, newSourcesArray))
		{
			previousSourcesArray = newSourcesArray;
			DefaultComboBoxModel model = new DefaultComboBoxModel(newSourcesArray);
			sourcesComboBox.setModel(model);
		}
		int index = 0;
		if(sourceIdentifier != null)
		{
			ComboBoxModel model = sourcesComboBox.getModel();
			for(int i = 0; i < model.getSize(); i++)
			{
				Object current = model.getElementAt(i);
				if(current instanceof WrappedSourceIdentifier)
				{
					WrappedSourceIdentifier wrapped = (WrappedSourceIdentifier) current;
					if(sourceIdentifier.getIdentifier().equals(wrapped.sourceIdentifier.getIdentifier()))
					{
						if(logger.isDebugEnabled()) logger.debug("Equal");
						index = i;
						break;
					}
					else
					{
						if(logger.isDebugEnabled())
						{
							logger.debug("Not equal: {} != {}", sourceIdentifier, wrapped.sourceIdentifier);
						}
					}
				}
				else
				{
					if(logger.isWarnEnabled()) logger.warn("Not instanceof WrappedSourceIdentifier");
				}
			}
		}
		sourcesComboBox.setSelectedIndex(index);
	}

	private void setSelectedGraph(int i)
	{
		if(i >= 0 && i < graphImageFactories.length)
		{
			selectedGraph = i;
		}
		else
		{
			selectedGraph = 0;
		}
		updateGraph();
	}

	private void updateGraph()
	{
		if(sourceIdentifier != null && selectedGraph >= 0 && selectedGraph < graphImageFactories.length)
		{
			GraphImageProducer graphImageFactory = graphImageFactories[selectedGraph];
			imageToggle[imageIndex] = graphImageFactory
				.createGraphImage(Util.getTime(), sourceIdentifier, imageToggle[imageIndex], showMaxCheckBox.isSelected());
			if(imageToggle[imageIndex] != null)
			{
				ImageIcon graphImageIcon = new ImageIcon();
				graphImageIcon.setImage(imageToggle[imageIndex]);
				graphLabel.setIcon(graphImageIcon);
				graphLabel.setText("");
			}
			else
			{
				graphLabel.setIcon(null);
				graphLabel.setText("Couldn't create graph image!");
			}
			if(imageIndex == 0)
			{
				imageIndex = 1;
			}
			else
			{
				imageIndex = 0;
			}
			graphLabel.repaint();
		}
	}

	/**
	 * Notifies this component that it now has a parent component.
	 * When this method is invoked, the chain of parent components is
	 * set up with <code>KeyboardAction</code> event listeners.
	 *
	 * @see #registerKeyboardAction
	 */
	@Override
	public void addNotify()
	{
		super.addNotify();
		setRunning(true);
		if(updateThread == null)
		{
			updateThread = new Thread(new GraphUpdateRunnable());
			updateThread.setDaemon(true);
			updateThread.start();
		}
		if(logger.isDebugEnabled()) logger.debug("addNotify!");
	}

	/**
	 * Notifies this component that it no longer has a parent component.
	 * When this method is invoked, any <code>KeyboardAction</code>s
	 * set up in the the chain of parent components are removed.
	 *
	 * @see #registerKeyboardAction
	 */
	@Override
	public void removeNotify()
	{
		super.removeNotify();
		setRunning(false);
		if(logger.isDebugEnabled()) logger.debug("removeNotify!");
	}

	private synchronized void setRunning(boolean running)
	{
		this.running = running;
		if(running)
		{
		}
		else
		{
			graphLabel.setIcon(null);
			for(int i = 0; i < imageToggle.length; i++)
			{
				if(imageToggle[i] != null)
				{
					imageToggle[i].flush();
					imageToggle[i] = null;
					if(logger.isInfoEnabled()) logger.info("Flushed image.");
				}
			}
		}
		notifyAll();
	}

	class GraphUpdateRunnable
		implements Runnable
	{

		public void run()
		{
			for(; ;)
			{
				for(; ;)
				{
					synchronized(StatisticsPanel.this)
					{
						if(!running)
						{
							try
							{
								StatisticsPanel.this.wait();
							}
							catch(InterruptedException ex)
							{
								if(logger.isInfoEnabled()) logger.info("Interrupted...", ex);
								return;
							}
						}
						else
						{
							break;
						}
					}
				}
				SwingUtilities.invokeLater(new SwingUpdateRunnable());
				try
				{
					Thread.sleep(REFRESH_DELAY);
				}
				catch(InterruptedException ex)
				{
					if(logger.isInfoEnabled()) logger.info("Interrupted...", ex);
					return;
				}
			}
		}
	}

	private void writeImage(File imageFile)
		throws IOException
	{
		BufferedImage resultImage = null;
		if(selectedGraph >= 0 && selectedGraph < graphImageFactories.length)
		{
			resultImage = graphImageFactories[selectedGraph]
				.createGraphImage(Util.getTime(), sourceIdentifier, null, showMaxCheckBox.isSelected());
		}
		if(resultImage != null)
		{
			final String format = "png";
			BufferedOutputStream imageOutput = null;
			try
			{
				imageOutput = new BufferedOutputStream(new FileOutputStream(imageFile));
				boolean writerFound = ImageIO.write(resultImage, format, imageOutput);
				if(!writerFound)
				{
					String msg = "Couldn't write image! No writer found for format '" + format + "'!";
					if(logger.isErrorEnabled()) logger.error(msg);
					throw new IOException(msg);
				}
			}
			finally
			{
				// close output stream no matter what. shouldn't be necessary...
				IOUtilities.closeQuietly(imageOutput);
				resultImage.flush();
			}
		}
	}

	class SwingUpdateRunnable
		implements Runnable
	{

		public void run()
		{
			updateGraph();
			if(logger.isInfoEnabled()) logger.info("Updated statistics...");
		}
	}

	private class TimerangeActionListener
		implements ActionListener
	{

		public void actionPerformed(ActionEvent e)
		{
			int index = timerangeComboBox.getSelectedIndex();
			setSelectedGraph(index);
		}
	}

	private class SourcesActionListener
		implements ActionListener
	{

		public void actionPerformed(ActionEvent e)
		{
			Object selected = sourcesComboBox.getSelectedItem();
			if(logger.isInfoEnabled()) logger.info("Selected source: {}", selected);
			SourceIdentifier si = null;
			if(selected instanceof WrappedSourceIdentifier)
			{
				si = ((WrappedSourceIdentifier) selected).sourceIdentifier;
				if(logger.isInfoEnabled()) logger.info("Selected sourceIdentifier: {}", si);
			}
			else
			{
				if(logger.isWarnEnabled()) logger.warn("Not instanceof WrappedSourceIdentifier: {}");
			}
			setSourceIdentifier(si);
		}
	}

	private class SaveAction
		extends AbstractAction
	{
		public SaveAction()
		{
			super();
			Icon icon;
			{
				URL url = EventWrapperViewPanel.class.getResource("/tango/32x32/actions/document-save-as.png");
				//URL url=EventWrapperViewPanel.class.getResource("/tango/scalable/actions/document-save-as.svg");
				if(url != null)
				{
//					try
//					{
//						//InputStreamReader reader = new InputStreamReader(url.openStream(), "utf-8");
//						String svg=IOUtils.toString(url.openStream(), "utf-8");
//						if(logger.isInfoEnabled()) logger.info("SVG: {}", svg);
//						StringReader reader=new StringReader(svg);
//						URI uri = SVGCache.getSVGUniverse().loadSVG(reader, "document-save-as");
//						SVGIcon svgIcon = new SVGIcon();
//						svgIcon.setSvgURI(uri);
//						svgIcon.setAntiAlias(true);
//						svgIcon.setPreferredSize(new Dimension(80, 80));
//						svgIcon.setScaleToFit(true);
//						icon = svgIcon;
//					}
//					catch (IOException e)
//					{
//						e.printStackTrace();
//					}
//					//svgIcon.setSvgURI(uri);
//					if(logger.isInfoEnabled()) logger.info("icon: {}",icon);
					icon = new ImageIcon(url);
				}
				else
				{
					icon = null;
				}
			}
			putValue(Action.SMALL_ICON, icon);
			putValue(Action.SHORT_DESCRIPTION, "Save as...");

		}

		public void actionPerformed(ActionEvent e)
		{
			saveFileChooser.setCurrentDirectory(mainFrame.getApplicationPreferences().getImagePath());
			int returnVal = saveFileChooser.showDialog(StatisticsPanel.this, "Save");
			if(returnVal == JFileChooser.APPROVE_OPTION)
			{
				File file = saveFileChooser.getSelectedFile();
				String filename = file.getAbsolutePath();
				if(!filename.toLowerCase().endsWith(".png"))
				{
					filename = filename + ".png";
				}
				file = new File(filename);
				try
				{
					writeImage(file);
					if(logger.isInfoEnabled()) logger.info("Wrote file '" + file.getAbsolutePath() + "'.");
					File parent = file.getParentFile();
					mainFrame.getApplicationPreferences().setImagePath(parent);
				}
				catch(IOException ex)
				{
					if(logger.isWarnEnabled())
					{
						logger.warn("Exception while writing file '" + file.getAbsolutePath() + "'!", ex);
					}
				}
			}
		}
	}

	private class ShowMaxAction
		extends AbstractAction
	{
		public ShowMaxAction()
		{
			super("Show Max");
			putValue(Action.SHORT_DESCRIPTION, "Show Max");
		}

		public void actionPerformed(ActionEvent e)
		{
			updateGraph();
		}
	}

	private static class WrappedSourceIdentifier
	{
		public String resolvedName;
		public SourceIdentifier sourceIdentifier;

		public WrappedSourceIdentifier(String resolvedName, SourceIdentifier sourceIdentifier)
		{
			this.resolvedName = resolvedName;
			this.sourceIdentifier = sourceIdentifier;
		}

		@Override
		public String toString()
		{
			return resolvedName;
		}

		public boolean equals(Object o)
		{
			if(this == o) return true;
			if(o == null || getClass() != o.getClass()) return false;

			final WrappedSourceIdentifier that = (WrappedSourceIdentifier) o;

			if(resolvedName != null ? !resolvedName.equals(that.resolvedName) : that.resolvedName != null) return false;
			return !(sourceIdentifier != null ? !sourceIdentifier
				.equals(that.sourceIdentifier) : that.sourceIdentifier != null);
		}

		public int hashCode()
		{
			int result;
			result = (resolvedName != null ? resolvedName.hashCode() : 0);
			result = 29 * result + (sourceIdentifier != null ? sourceIdentifier.hashCode() : 0);
			return result;
		}
	}
}
