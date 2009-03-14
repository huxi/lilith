/**
 * Returns true if the logging event has a thread name that
 * starts with the given searchString.
 */
import de.huxhorn.lilith.data.logging.LoggingEvent;

def event = input?.event;

if(event instanceof LoggingEvent)
{
	def threadName = event.threadName;
	if(threadName)
	{
		return threadName.startsWith(searchString);
	}
}
return false;
