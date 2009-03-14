/**
 * Returns true if the MDC of the logging event contains a
 * "username" entry that equals the searchString.
 */
import de.huxhorn.lilith.data.logging.LoggingEvent;

def event = input?.event;

if(event instanceof LoggingEvent)
{
	def mdc = event.mdc;
	if(mdc)
	{
		return (mdc['username'] == searchString);
	}
}
return false;
