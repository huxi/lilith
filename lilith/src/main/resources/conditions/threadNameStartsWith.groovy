/**
 * Returns true if the logging event has a thread name that
 * starts with the given searchString.
 */
def threadName = input?.event?.threadName;
if(threadName)
{
	return threadName.startsWith(searchString);
}
return false; 