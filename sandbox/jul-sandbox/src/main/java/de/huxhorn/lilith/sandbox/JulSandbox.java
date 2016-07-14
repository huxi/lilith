package de.huxhorn.lilith.sandbox;

import java.util.logging.*;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class JulSandbox
{
	public static void addSuppressed(Throwable throwable, Throwable suppressed)
	{
		throwable.addSuppressed(suppressed);
	}

	public static class InnerClass
	{
		public static void execute()
		{
			final Logger logger = Logger.getLogger(InnerClass.class.getName());
			logger.log(Level.INFO,"Foo!");
			logger.log(Level.WARNING,"Foo!", createRuntimeException());
			Throwable t=createRuntimeException();
			addSuppressed(t, new RuntimeException("Suppressed1"));
			addSuppressed(t, new RuntimeException("Suppressed2"));
			logger.log(Level.SEVERE,"Foo with Suppressed!", t);
			System.out.println("printStackTrace:");
			t.printStackTrace(System.out);
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
		throws IOException, InterruptedException
	{
		Logger rootLogger=Logger.getLogger("");

		{
			FileHandler fh = new FileHandler("log.xml");
			fh.setEncoding(StandardCharsets.UTF_8.toString());
			fh.setFormatter(new XMLFormatter());

			rootLogger.addHandler(fh);
		}

		{
			try
			{
				SocketHandler fh = new SocketHandler("127.0.0.1", 11020);
				fh.setEncoding(StandardCharsets.UTF_8.toString());
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
		for(;;) {
			logger.log(Level.INFO, "Args {0}{1}", new Object[]{"Foo", "bar"});

			InnerClass.execute();
			Thread.sleep(500);
			
			logger.log(Level.INFO, "Issue #26: Unable to read logs which contains unprintable characters:" + (char)0 + (char)0xFFFF + "...");
		}
	}
}
