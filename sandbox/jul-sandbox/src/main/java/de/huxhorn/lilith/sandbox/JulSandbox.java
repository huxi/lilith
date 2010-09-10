package de.huxhorn.lilith.sandbox;

import java.util.logging.*;
import java.io.*;

public class JulSandbox
{
	public static class InnerClass
	{
		public static void execute()
		{
			final Logger logger = Logger.getLogger(InnerClass.class.getName());
			logger.log(Level.INFO,"Foo!");
			logger.log(Level.WARNING,"Foo!", createRuntimeException());
		}
	}

	private static RuntimeException createRuntimeException()
	{
		RuntimeException result;
		try
		{
			throw new RuntimeException("Cause-Exception");
		}
		catch(RuntimeException ex)
		{
			result=new RuntimeException("Exception", ex);
		}
		return result;
	}
	

	public static void main(String args[])
		throws IOException
	{
		Logger rootLogger=Logger.getLogger("");

		{
			FileHandler fh = new FileHandler("log.xml");
			fh.setEncoding("UTF-8");
			fh.setFormatter(new XMLFormatter());

			rootLogger.addHandler(fh);
		}

		{
			try
			{
				SocketHandler fh = new SocketHandler("127.0.0.1", 11020);
				fh.setEncoding("UTF-8");
				fh.setFormatter(new XMLFormatter());

				rootLogger.addHandler(fh);
			}
			catch(IOException ex)
			{
				System.out.println("Couldn't connect the SocketHandler. What a fail.");
				ex.printStackTrace();
			}
		}
		rootLogger.setLevel(Level.ALL);
		
		final Logger logger = Logger.getLogger(JulSandbox.class.getName());
		logger.log(Level.INFO, "Args {0}{1}", new Object[]{"Foo", "bar"});

		InnerClass.execute();
	}
}
