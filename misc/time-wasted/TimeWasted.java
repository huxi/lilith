import java.util.concurrent.locks.*;

public class TimeWasted
{
	public static void main(String args[])
		throws InterruptedException
	{
		int delay=1;
		if(args.length>0)
		{
			try
			{
				delay=Integer.parseInt(args[0]);
			}
			catch(NumberFormatException ex)
			{
				//ignore
			}
		}
		else
		{
			System.out.println("Usage: TimeWasted [delay in ms]");
		}

		System.out.println("Environment:");
		System.out.println("delay                = "+delay);
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

		usingSynchronized(10, delay);
		usingUnfairLock(10, delay);
		usingFairLock(10, delay);
	}

	public static void execute(String text, Thread[] threads)
		throws InterruptedException
	{
		System.out.println("About to execute "+text+"...");
		int threadCount=threads.length;
		for(int i=0;i<threadCount;i++)
		{
			threads[i].start();
		}

		Thread.sleep(10000);

		for(int i=threadCount - 1 ; i>=0 ; i--)
		{
			threads[i].interrupt();
		}
		Thread.sleep(1000); // wait a moment for termination, too lazy for join ;)
	}

	public static void print(String text, Runnable[] runnables)
	{
		System.out.println("Results for "+text+":");
		for(int i=0;i<runnables.length;i++)
		{
			System.out.println("runnables["+i+"]: "+runnables[i]);
		}
		System.out.println("##########################################");
	}

	public static void usingSynchronized(int threadCount, int delay)
		throws InterruptedException
	{
		Object lockObject=new Object();
		Runnable[] runnables=new Runnable[threadCount];
		Thread[] threads=new Thread[threadCount];

		for(int i=0;i<threadCount;i++)
		{
			if(i==0)
			{
				runnables[i]=new SynchronizedRunnable(lockObject, delay);
			}
			else
			{
				runnables[i]=new WastedTimeSynchronizedRunnable(lockObject, delay);
			}
			threads[i]=new Thread(runnables[i]);
		}
		String text="usingSynchronized";
		execute(text, threads);
		print(text, runnables);
	}

	public static void usingUnfairLock(int threadCount, int delay)
		throws InterruptedException
	{
		Lock lock=new ReentrantLock();
		Runnable[] runnables=new Runnable[threadCount];
		Thread[] threads=new Thread[threadCount];

		for(int i=0;i<threadCount;i++)
		{
			if(i==0)
			{
				runnables[i]=new LockRunnable(lock, delay);
			}
			else
			{
				runnables[i]=new WastedTimeLockRunnable(lock, delay);
			}
			threads[i]=new Thread(runnables[i]);
		}

		String text="usingUnfairLock";
		execute(text, threads);
		print(text, runnables);
	}

	public static void usingFairLock(int threadCount, int delay)
		throws InterruptedException
	{
		Lock lock=new ReentrantLock(true);
		Runnable[] runnables=new Runnable[threadCount];
		Thread[] threads=new Thread[threadCount];

		for(int i=0;i<threadCount;i++)
		{
			if(i==0)
			{
				runnables[i]=new LockRunnable(lock, delay);
			}
			else
			{
				runnables[i]=new WastedTimeLockRunnable(lock, delay);
			}
			threads[i]=new Thread(runnables[i]);
		}

		String text="usingFairLock";
		execute(text, threads);
		print(text, runnables);
	}

	public static class SynchronizedRunnable
		implements Runnable
	{
		private final Object lockObject;
		private final int delay;
		private int counter;
		private boolean running;

		public SynchronizedRunnable(Object lockObject, int delay)
		{
			this.lockObject=lockObject;
			this.delay=delay;
			this.counter=0;
			this.running=false;
		}

		public void run()
		{
			running=true;
			for(;;)
			{
				synchronized(lockObject)
				{
					counter++;
					try
					{
						Thread.sleep(delay);
					}
					catch(InterruptedException ex)
					{
						break;
					}
				}
			}
			running=false;
		}

		public String toString()
		{
			return "SynchronizedRunnable[counter="+counter+", running="+running+", delay="+delay+"]";
		}
	}

	public static class LockRunnable
		implements Runnable
	{
		private final Lock lock;
		private final int delay;
		private int counter;
		private boolean running;

		public LockRunnable(Lock lock, int delay)
		{
			this.lock=lock;
			this.delay=delay;
			this.counter=0;
			this.running=false;
		}

		public void run()
		{
			running=true;
			for(;;)
			{
				lock.lock();
				try
				{
					counter++;
					Thread.sleep(delay);
				}
				catch(InterruptedException ex)
				{
					break;
				}
				finally
				{
					lock.unlock();
				}
			}
			running=false;
		}

		public String toString()
		{
			return "LockRunnable[counter="+counter+", running="+running+", delay="+delay+"]";
		}
	}


	public static class WastedTimeLockRunnable
		implements Runnable
	{
		private final Lock lock;
		private final int delay;
		private int counter;
		private boolean running;
		private long minWasted=0xFFFFFFFFL;
		private long maxWasted;
		private long totalWasted;

		public WastedTimeLockRunnable(Lock lock, int delay)
		{
			this.lock=lock;
			this.delay=delay;
			this.counter=0;
			this.running=false;
		}

		public void run()
		{
			running=true;
			for(;;)
			{
				try
				{
					Thread.sleep(delay);
				}
				catch(InterruptedException ex)
				{
					break;
				}
				long wasted=System.nanoTime();
				lock.lock();
				try
				{
					counter++;
				}
				finally
				{
					lock.unlock();
				}
				wasted=System.nanoTime()-wasted;
				if(wasted<minWasted)
				{
					minWasted=wasted;
				}
				if(wasted>maxWasted)
				{
					maxWasted=wasted;
				}
				totalWasted+=wasted;
			}
			running=false;
		}

		public String toString()
		{
			return "WastedTimeLockRunnable[counter="+counter+", running="+running+", delay="+delay+", minWasted="+minWasted+", maxWasted="+maxWasted+", totalWasted="+totalWasted+"]";
		}
	}

	public static class WastedTimeSynchronizedRunnable
		implements Runnable
	{
		private final Object lockObject;
		private final int delay;
		private int counter;
		private boolean running;
		private long minWasted=0xFFFFFFFFL;
		private long maxWasted;
		private long totalWasted;

		public WastedTimeSynchronizedRunnable(Object lockObject, int delay)
		{
			this.lockObject=lockObject;
			this.delay=delay;
			this.counter=0;
			this.running=false;
		}

		public void run()
		{
			running=true;
			for(;;)
			{
				try
				{
					Thread.sleep(delay);
				}
				catch(InterruptedException ex)
				{
					break;
				}
				long wasted=System.nanoTime();
				synchronized(lockObject)
				{
					counter++;
				}
				wasted=System.nanoTime()-wasted;
				if(wasted<minWasted)
				{
					minWasted=wasted;
				}
				if(wasted>maxWasted)
				{
					maxWasted=wasted;
				}
				totalWasted+=wasted;
			}
			running=false;
		}

		public String toString()
		{
			return "WastedTimeSynchronizedRunnable[counter="+counter+", running="+running+", delay="+delay+", minWasted="+minWasted+", maxWasted="+maxWasted+", totalWasted="+totalWasted+"]";
		}
	}}
