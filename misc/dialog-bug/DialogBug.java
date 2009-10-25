    import javax.swing.*;
    import java.awt.*;
    import java.awt.event.*;
    
    public class DialogBug
    {
        public static void main(String[] args)
        {
            SwingUtilities.invokeLater(new StartupRunnable(args.length == 0));
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
                dialog1=new MyDialog(this);
                dialog2=new MyDialog(this);
                JMenuBar menuBar=new JMenuBar();
                JMenu fileMenu=new JMenu("File");
                menuBar.add(fileMenu);
                fileMenu.add(new JMenuItem(new OpenAction()));
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
            
        }
        
        public static class MyDialog
            extends JDialog
        {
            public MyDialog(JFrame parent)
            {
                super(parent);
                setTitle("Dialog1");
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
        }
    }