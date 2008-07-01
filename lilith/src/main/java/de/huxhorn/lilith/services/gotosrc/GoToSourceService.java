package de.huxhorn.lilith.services.gotosrc;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Socket;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.BufferedOutputStream;

public class GoToSourceService
{
	private final Logger logger = LoggerFactory.getLogger(GoToSourceService.class);

	private Socket socket;
	private ObjectOutputStream oos;

	public void start()
	{
		try
		{
			socket=new Socket("localhost", 11111);
			oos=new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
		}
		catch (IOException e)
		{
			if(logger.isInfoEnabled()) logger.info("Exception while creating connection with IDE!", e);
			stop();
		}
	}

	public void goToSource(StackTraceElement ste)
	{
		if(logger.isInfoEnabled()) logger.info("Go to source of {}.", ste);
		if(ste==null)
		{
			return;
		}
		if(oos==null)
		{
			start();
		}
		boolean error=false;
		if(oos!=null)
		{
			try
			{
				oos.writeObject(ste);
				oos.flush();
			}
			catch (IOException e)
			{
				if(logger.isDebugEnabled()) logger.debug("Exception on first try, probably lingering connection.", e);
				stop();
				error=true;
			}
		}
		if(error)
		{
			// try to send it again to work around lingering connections.
			start();
			if(oos!=null)
			{
				try
				{
					oos.writeObject(ste);
					oos.flush();
				}
				catch (IOException e)
				{
					if(logger.isWarnEnabled()) logger.warn("Exception on second try!", e);
					stop();
				}
			}
		}

	}

	public void stop()
	{
		if(oos!=null)
		{
			IOUtils.closeQuietly(oos);
			oos=null;
		}
		if(socket!=null)
		{
			try
			{
				socket.close();
			}
			catch (IOException e)
			{
				// ignore
			}
			socket=null;
		}
	}
}
