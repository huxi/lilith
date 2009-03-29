package de.huxhorn.lilith.sandbox;

import org.jgroups.ChannelException;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class JGroupsSandbox
{
	private JChannel channel;
	private String userName;

	public static void main(String args[])
		throws Exception
	{
		final Logger logger = LoggerFactory.getLogger(JGroupsSandbox.class);

		new JGroupsSandbox().start();
	}

	void start()
		throws ChannelException
	{
		userName = System.getProperty("user.name");
		channel = new JChannel();
		channel.setReceiver(new MyReceiver());
		channel.connect("ChatCluster");
		eventLoop();
		channel.close();

	}

	private void eventLoop()
	{
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		while(true)
		{
			try
			{
				System.out.print("> ");
				System.out.flush();
				String line = in.readLine().toLowerCase();
				if(line.startsWith("quit") || line.startsWith("exit"))
				{
					break;
				}
				line = "[" + userName + "] " + line;
				Message msg = new Message(null, null, line);
				channel.send(msg);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	private static class MyReceiver
		extends ReceiverAdapter
	{
		public void viewAccepted(View new_view)
		{
			System.out.println("** view: " + new_view);
		}

		public void receive(Message msg)
		{
			System.out.println(msg.getSrc() + ": " + msg.getObject());
		}
	}
}
