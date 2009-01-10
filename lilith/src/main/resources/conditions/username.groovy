/**
 * Returns true if the MDC of the logging event contains a
 * "username" entry that equals the searchString.
 */
def mdc = input?.event?.mdc;
if(mdc)
{
	return (mdc['username'] == searchString);
}
return false; 