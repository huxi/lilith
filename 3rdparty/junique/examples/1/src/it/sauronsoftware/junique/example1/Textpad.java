package it.sauronsoftware.junique.example1;

import it.sauronsoftware.junique.AlreadyLockedException;
import it.sauronsoftware.junique.JUnique;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;

public class Textpad extends JFrame {

	private static final long serialVersionUID = 1L;

	private JTabbedPane tabs = new JTabbedPane();

	private JButton openButton = new JButton("Open file");

	private JButton closeButton = new JButton("Close tab");

	public Textpad() {
		super("Textpad");
		closeButton.setEnabled(false);
		openButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openDialog();
			}
		});
		closeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				closeCurrent();
			}
		});
		JToolBar toolbar = new JToolBar();
		toolbar.setFloatable(false);
		toolbar.add(openButton);
		toolbar.add(closeButton);
		JPanel all = new JPanel();
		all.setLayout(new BorderLayout(3, 3));
		all.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		all.add(toolbar, BorderLayout.NORTH);
		all.add(tabs, BorderLayout.CENTER);
		setContentPane(all);
		setSize(600, 500);
		setLocation(100, 100);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	private void openDialog() {
		FileDialog fd = new FileDialog(this, "Open", FileDialog.LOAD);
		fd.setVisible(true);
		String dir = fd.getDirectory();
		String file = fd.getFile();
		if (dir != null && file != null) {
			open(dir + File.separator + file);
		}
	}

	private void closeCurrent() {
		Component c = tabs.getSelectedComponent();
		if (c != null) {
			tabs.remove(c);
			if (tabs.getComponentCount() == 0) {
				closeButton.setEnabled(false);
			}
		}
	}

	public void open(String file) {
		StringBuffer text = new StringBuffer();
		File f = new File(file);
		Reader reader = null;
		try {
			reader = new FileReader(f);
			char[] buf = new char[1024];
			int l;
			while ((l = reader.read(buf)) != -1) {
				text.append(buf, 0, l);
			}
		} catch (Throwable t) {
			t.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (Throwable t) {
					;
				}
			}
		}
		JTextArea area = new JTextArea(text.toString());
		area.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		JScrollPane scroll = new JScrollPane(area);
		JPanel all = new JPanel();
		all.setLayout(new BorderLayout(3, 3));
		all.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		all.add(scroll, BorderLayout.CENTER);
		tabs.add(f.getName(), scroll);
		tabs.setSelectedComponent(scroll);
		closeButton.setEnabled(true);
	}

	public static void main(String[] args) {
		String id = Textpad.class.getName();
		boolean start;
		try {
			JUnique.acquireLock(id, null);
			start = true;
		} catch (AlreadyLockedException e) {
			// Application already running.
			start = false;
		}
		if (start) {
			Textpad t = new Textpad();
			t.setVisible(true);
		}
	}

}
