package de.huxhorn.lilith.debug;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogSkullRunnable
	extends AbstractDebugRunnable
{
	private final Logger logger = LoggerFactory.getLogger(LogStuffRunnable.class);

	public LogSkullRunnable(int delay)
	{
		super(delay);
	}

	public void runIt() throws InterruptedException
	{
		if (logger.isDebugEnabled())
		{
			logger.debug("Skull!\n" +
			"     _.--\"\"\"\"\"--._\n" +
			"   .'             '.\n" +
			"  /                 \\\n" +
			" ;                   ;\n" +
			" |                   |\n" +
			" |                   |\n" +
			" ;                   ;\n" +
			"  \\ (`'--,    ,--'`) /\n" +
			"   \\ \\  _ )  ( _  / /\n" +
			"    ) )(')/  \\(')( (\n" +
			"   (_ `\"\"` /\\ `\"\"` _)\n" +
			"    \\`\"-, /  \\ ,-\"`/\n" +
			"     `\\ / `\"\"` \\ /`\n" +
			"      |/\\/\\/\\/\\/\\|\n" +
			"      |\\        /|\n" +
			"      ; |/\\/\\/\\| ;\n" +
			"       \\`-`--`-`/\n" +
			"        \\      /\n" +
			"         ',__,'\n" +
			"          q__p\n" +
			"          q__p\n" +
			"          q__p\n" +
			"          q__p\n");
		}
	}
}