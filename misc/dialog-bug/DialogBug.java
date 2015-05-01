import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * This small example reproduces the ticket #72 problem.
 * (see http://sourceforge.net/apps/trac/lilith/ticket/72 )
 *
 * Compile executing "javac DialogBug.java",
 * Execute running "java DialogBug"
 *
 * Steps to reproduce:
 * Dismiss both dialogs clicking on "Ok".
 * Press "Ctrl-O". This should print "Open executed" but doesn't.
 * Click on "File" menu.
 * Press "Ctrl-O". Now "Open executed" is printed as expected.
 *
 * Alternatively, execute running "java DialogBug x".
 * Only one dialog will appear.
 * "Ctrl-O" will work immediately after dismissing the dialog.
 *
 */
public class DialogBug
{
	public static void main(String[] args)
	{
		System.out.println("java.version="+System.getProperty("java.version"));
		Toolkit.getDefaultToolkit().addAWTEventListener( new MyAwtListener(),
			AWTEvent.FOCUS_EVENT_MASK |
			AWTEvent.CONTAINER_EVENT_MASK |
			AWTEvent.WINDOW_FOCUS_EVENT_MASK |
			AWTEvent.WINDOW_EVENT_MASK |
			AWTEvent.WINDOW_STATE_EVENT_MASK);

		EventQueue.invokeLater(new StartupRunnable(args.length == 0));
	}

	public static class MyAwtListener
		implements AWTEventListener
	{
		public void eventDispatched(AWTEvent event)
		{
			System.out.println("event = " + event);
		}
	}

	public static class StartupRunnable
		implements Runnable
	{
		private boolean both;

		public StartupRunnable(boolean both)
		{
			this.both=both;
		}

		public void run()
		{
			MyFrame myFrame=new MyFrame();
			myFrame.setVisible(true);
			myFrame.startUp(both);
		}
	}

	public static class MyFrame
		extends JFrame
	{
		private MyDialog dialog1;
		private MyDialog dialog2;

		public MyFrame()
		{
			super("MyFrame");
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			initUI();
		}

		private void initUI()
		{
			dialog1=new MyDialog(this, "Dialog1");
			dialog2=new MyDialog(this, "Dialog2");
			JMenuBar menuBar=new JMenuBar();
			JMenu fileMenu=new JMenu("File");
			menuBar.add(fileMenu);
			fileMenu.add(new OpenAction());
			setJMenuBar(menuBar);
			setSize(200,200);
		}

		public void startUp(boolean both)
		{
			dialog1.setVisible(true);
			if(both)
			{
				dialog2.setVisible(true);
			}
		}

		private class OpenAction
			extends AbstractAction
		{
			public OpenAction()
			{
				super("Open");
				KeyStroke accelerator = KeyStroke.getKeyStroke("ctrl O");
				putValue(Action.ACCELERATOR_KEY, accelerator);
			}

			public void actionPerformed(ActionEvent e)
			{
				System.out.println("Open executed");
			}
		}

		public String toString()
		{
			return "MyFrame";
		}
	}

	public static class MyDialog
		extends JDialog
	{
		private String title;

		public MyDialog(JFrame parent, String title)
		{
			super(parent);
			this.title=title;
			setTitle(title);
			setModal(false);
			add(new JButton(new OkAction()));
			pack();
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
				setVisible(false);
			}
		}

		public String toString()
		{
			return title;
		}
	}
}
