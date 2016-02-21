import java.util.concurrent.locks.*;
import java.util.concurrent.atomic.*;
import java.io.*;

public class WorkDone
{

    public static void main(String[] args)
		throws InterruptedException, IOException
	{
		int amount=0;
		if(args.length>0)
		{
			try
			{
				amount=Integer.parseInt(args[0]);
			}
			catch(NumberFormatException ex)
			{
				//ignore
			}
		}

		if(amount<1)
		{
			Runtime runtime = Runtime.getRuntime();

			amount=runtime.availableProcessors();
		}

		System.out.println("Environment:");
		System.out.println("java.runtime.name    = "+System.getProperty("java.runtime.name"));
		System.out.println("java.runtime.version = "+System.getProperty("java.runtime.version"));
		System.out.println("java.vendor          = "+System.getProperty("java.vendor"));
		System.out.println("java.version         = "+System.getProperty("java.version"));
		System.out.println("java.vm.name         = "+System.getProperty("java.vm.name"));
		System.out.println("java.vm.info         = "+System.getProperty("java.vm.info"));

		System.out.println("os.name              = "+System.getProperty("os.name"));
		System.out.println("os.version           = "+System.getProperty("os.version"));
		System.out.println("os.arch              = "+System.getProperty("os.arch"));
		System.out.println("##########################################");

		usingSynchronized(amount);
		usingFairLock(amount);
		usingUnfairLock(amount);
    }

	public static void usingSynchronized(int threadCount)
		throws InterruptedException, IOException
	{
		perform(threadCount, "usingSynchronized", new SynchronizedLogger());
	}

	public static void usingFairLock(int threadCount)
		throws InterruptedException, IOException
	{
		perform(threadCount, "usingFairLock", new FairLogger());
	}

	public static void usingUnfairLock(int threadCount)
		throws InterruptedException, IOException
	{
		perform(threadCount, "usingUnfairLock", new UnfairLogger());
	}

	private static void perform(int threadCount, String name, AbstractLogger logger)
		throws InterruptedException, IOException
	{
		WorkRunnable[] workRunnables=new WorkRunnable[threadCount];
		for(int i=0;i<threadCount;i++)
		{
			workRunnables[i]=new WorkRunnable(logger);
		}
		execute(name, workRunnables);
		print(name, workRunnables);
		logger.close();
	}


	public static void execute(String text, WorkRunnable[] workRunnables)
		throws InterruptedException
	{
		System.out.println("About to execute "+text+"...");
		int threadCount=workRunnables.length;
		Thread[] threads=new Thread[threadCount];
		for(int i=0;i<threadCount;i++)
		{
			threads[i]=new Thread(workRunnables[i]);
		}

		for(int i=0;i<threadCount;i++)
		{
			threads[i].start();
		}

		Thread.sleep(10000);

		for(int i=threadCount - 1 ; i>=0 ; i--)
		{
			workRunnables[i].cancel();
		}
		Thread.sleep(1000); // wait a moment for termination, too lazy for join ;)
	}

	public static void print(String text, WorkRunnable[] workRunnables)
	{
		System.out.println("Results for "+text+":");
		int totalWorkUnits=0;
		for(int i=0;i<workRunnables.length;i++)
		{
			WorkRunnable current=workRunnables[i];
			System.out.println("runnables["+i+"]: "+current);
			totalWorkUnits=totalWorkUnits+current.getCounter();
		}
		System.out.println("Total work-units done: "+totalWorkUnits);
		System.out.println("##########################################");
	}

	public static abstract class AbstractLogger
	{
		private File file;
		private RandomAccessFile raf;

		public AbstractLogger()
			throws IOException
		{
			file=File.createTempFile("example",".log");
			file.deleteOnExit();
			raf=new RandomAccessFile(file, "rw");

		}

		public File getFile()
		{
			return file;
		}

		public abstract void log(String message);

		protected void performLogging(String message)
		{
			try
			{
				raf.writeChars(message);
			}
			catch(IOException ex)
			{
				ex.printStackTrace();
			}
			/*
			System.err.println(message);
			try
			{
				Thread.sleep(10);
			}
			catch(InterruptedException ex)
			{}
			*/
		}

		public void close()
			throws IOException
		{
			raf.close();
		}

	}

	public static class SynchronizedLogger
		extends AbstractLogger
	{
		public SynchronizedLogger()
			throws IOException
		{
			super();
		}

		public void log(String message)
		{
			synchronized(this)
			{
				performLogging(message);
			}
		}
	}

	public static class LockLogger
		extends AbstractLogger
	{
		private final ReentrantLock lock;

		protected LockLogger(boolean fair)
			throws IOException
		{
			super();
			this.lock=new ReentrantLock(fair);
		}

		public void log(String message)
		{
			lock.lock();
			try
			{
				performLogging(message);
			}
			finally
			{
				lock.unlock();
			}
		}
	}

	public static class FairLogger
		extends LockLogger
	{
		public FairLogger()
			throws IOException
		{
			super(true);
		}
	}

	public static class UnfairLogger
		extends LockLogger
	{
		public UnfairLogger()
			throws IOException
		{
			super(false);
		}
	}

	public static class WorkRunnable
		implements Runnable
	{
		private final AtomicBoolean cancel=new AtomicBoolean(false);
		private final AbstractLogger logger;
		private int counter=0;
		private boolean done;

		public WorkRunnable(AbstractLogger logger)
		{
			this.logger=logger;
		}

		public void cancel()
		{
			cancel.set(true);
		}

		public int getCounter()
		{
			return counter;
		}

		public boolean isDone()
		{
			return done;
		}

		public int fib(int value)
		{
			if(value == 0)
			{
				return 0;
			}
			if(value == 1)
			{
				return 1;
			}
			logger.log("Calling fib("+(value-1)+") + fib("+(value-2)+")...");
			return fib(value-1)+fib(value-2);
		}

		public void run()
		{
			done=false;
			for(;;counter++)
			{
				long value=counter;//fib(counter + 5);
				value=value*value*value;
				//int value=counter;
				logger.log("The calculated value for input "+counter+" is "+value+".");
				//logger.log("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
				//logger.log("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
				if(cancel.get())
				{
					break;
				}
			}
			done=true;
		}

		public String toString()
		{
			return "WorkRunnable[counter="+counter+", done="+done+"]";
		}
	}
}


