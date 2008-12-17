package de.huxhorn.lilith.swing.preferences;

import de.huxhorn.sulky.swing.KeyStrokes;

import javax.swing.JDialog;
import javax.swing.JColorChooser;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JButton;
import javax.swing.AbstractAction;
import javax.swing.KeyStroke;
import javax.swing.Action;
import java.awt.Dialog;
import java.awt.HeadlessException;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ColorChooserDialog
		extends JDialog
{
	private final Logger logger = LoggerFactory.getLogger(ColorChooserDialog.class);

	private boolean canceled;
	private JColorChooser textChooser;
	private JColorChooser backgroundChooser;
	private JColorChooser borderChooser;
	private ColorScheme colorScheme;

	public ColorChooserDialog(Dialog owner)
			throws HeadlessException
	{
		super(owner, true);
		createUI();
	}

	private void createUI()
	{
		setLayout(new BorderLayout());
		JPanel emptyPreview = new JPanel();
		emptyPreview.setMinimumSize(new Dimension(0, 0));
		emptyPreview.setPreferredSize(new Dimension(0, 0));
		emptyPreview.setMaximumSize(new Dimension(0, 0));

		textChooser = new JColorChooser();
		textChooser.setPreviewPanel(emptyPreview);
		backgroundChooser = new JColorChooser();
		backgroundChooser.setPreviewPanel(emptyPreview);
		borderChooser = new JColorChooser();
		borderChooser.setPreviewPanel(emptyPreview);
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.add("Text", textChooser);
		tabbedPane.add("Background", backgroundChooser);
		tabbedPane.add("Border", borderChooser);

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		OkAction okAction = new OkAction();
		CancelAction cancelAction = new CancelAction();
		buttonPanel.add(new JButton(okAction));
		buttonPanel.add(new JButton(cancelAction));
		add(buttonPanel, BorderLayout.SOUTH);

		KeyStrokes.registerCommand(tabbedPane, cancelAction, "CANCEL_ACTION");
		KeyStrokes.registerCommand(buttonPanel, cancelAction, "CANCEL_ACTION");

		add(tabbedPane, BorderLayout.CENTER);
	}

	public ColorScheme getColorScheme()
	{
		return colorScheme;
	}

	public void setColorScheme(ColorScheme colorScheme)
	{
		this.colorScheme = colorScheme;
	}

	public boolean isCanceled()
	{
		return canceled;
	}

	private void initUI()
	{
		ColorScheme cs;
		if(colorScheme==null)
		{
			colorScheme=new ColorScheme();
		}
		try
		{
			cs = colorScheme.clone();
		}
		catch (CloneNotSupportedException e)
		{
			cs=new ColorScheme();
			if(logger.isWarnEnabled()) logger.warn("Couldn't clone ColorScheme!", e);
		}

		textChooser.setColor(cs.getTextColor());
		backgroundChooser.setColor(cs.getBackgroundColor());
		borderChooser.setColor(cs.getBorderColor());
		canceled = true;
	}

	@Override
	public void setVisible(boolean b)
	{
		if (b)
		{
			initUI();
		}
		super.setVisible(b);
	}

	private class OkAction
			extends AbstractAction
	{
		public OkAction()
		{
			super("Ok");
		}

		public void actionPerformed(ActionEvent e)
		{
			canceled = false;
			colorScheme.setTextColor(textChooser.getColor());
			colorScheme.setBackgroundColor(backgroundChooser.getColor());
			colorScheme.setBorderColor(borderChooser.getColor());
			ColorChooserDialog.super.setVisible(false);
		}
	}

	private class CancelAction
			extends AbstractAction
	{
		public CancelAction()
		{
			super("Cancel");
			KeyStroke accelerator = KeyStrokes.resolveAcceleratorKeyStroke("ESCAPE");
			putValue(Action.ACCELERATOR_KEY, accelerator);
		}

		public void actionPerformed(ActionEvent e)
		{
			canceled = true;
			ColorChooserDialog.super.setVisible(false);
		}
	}
}