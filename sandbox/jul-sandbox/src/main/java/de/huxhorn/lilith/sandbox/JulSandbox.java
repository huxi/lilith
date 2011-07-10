package de.huxhorn.lilith.sandbox;

import java.util.logging.*;
import java.io.*;
import java.lang.reflect.Method;

public class JulSandbox
{
	public static final Method ADD_SUPPRESSED_METHOD;

	static
	{
	    Method method = null;
		try
		{
			method = Throwable.class.getMethod("addSuppressed", Throwable.class);
		}
		catch(NoSuchMethodException e)
		{
			// ignore
		}
		ADD_SUPPRESSED_METHOD = method;
	}

	public static void addSuppressed(Throwable throwable, Throwable suppressed)
	{
		if(ADD_SUPPRESSED_METHOD != null)
		{
			try
			{
				ADD_SUPPRESSED_METHOD.invoke(throwable, suppressed);
			}
			catch(Throwable t)
			{
				System.err.println("Exception while invoking Throwable.addSuppressed method!");
				t.printStackTrace();
			}
		}
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
