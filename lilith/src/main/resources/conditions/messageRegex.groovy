/**
 * Returns true if the logging events message matches the regex 
 * given as searchString.
 */
import de.huxhorn.lilith.data.logging.LoggingEvent;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

def event = input?.event;

if(searchString == null || '' == searchString)
{
	// so no string doesn't filter anything
	return true;
}

if(event instanceof LoggingEvent)
{
	try
	{
		def pattern = Pattern.compile(searchString);
		def message = event.message.message;
		if(message)
		{
			return message ==~ pattern; // short for pattern.matcher(message).matches();
		}
	}
	catch(PatternSyntaxException ex)
	{
		// ignore, returns false
	}
}
return false;
