package de.huxhorn.lilith.sandbox;

import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class JGroupsSandbox
{
	private JChannel channel;
	private String userName;

	public static void main(String args[])
		throws Exception
	{
		new JGroupsSandbox().start();
	}

	void start()
			throws Exception
	{
		// https://issues.jboss.org/browse/JGRP-1919
		// *sigh*
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
				String line = in.readLine();
				if(line == null)
				{
					throw new RuntimeException("Could not read next line. Using 'gradle run'? Add --no-daemon option to your call.");
				}

				line = line.toLowerCase();
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
				break;
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
