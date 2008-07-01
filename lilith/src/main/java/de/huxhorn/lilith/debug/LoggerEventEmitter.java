/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2008 Joern Huxhorn
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.huxhorn.lilith.debug;

import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;


public class LoggerEventEmitter
{
	private static final int DEFAULT_DELAY = 50;
	private int delay=DEFAULT_DELAY;
	private Marker marker;
	private Marker fnordMarker;
	private ExecutorService executor;

	public LoggerEventEmitter()
	{
		marker = createMarker();
		fnordMarker = createFnordMarker();
		executor = Executors.newCachedThreadPool();
	}

	public int getDelay()
	{
		return delay;
	}

	public void setDelay(int delay)
	{
		this.delay = delay;
	}

	public void logStuff() throws InterruptedException
	{
//		if(logger.isTraceEnabled()) logger.trace("A trace message.");
//		sleep();
//		if(logger.isDebugEnabled()) logger.debug("A debug message.");
//		sleep();
//		if(logger.isInfoEnabled()) logger.info("A info message.");
//		sleep();
//		if(logger.isWarnEnabled()) logger.warn("A warn message.");
//		sleep();
//		if(logger.isErrorEnabled()) logger.error("A error message.");
//		sleep();
		execute(new LogStuffRunnable(delay, null));
	}

	private void execute(Runnable runnable)
	{
		executor.execute(runnable);
	}

	public void logStuffWithMdc() throws InterruptedException
	{
//		MDC.put("foo","bar");
//		if(logger.isTraceEnabled()) logger.trace("A trace message.");
//		sleep();
//		if(logger.isDebugEnabled()) logger.debug("A debug message.");
//		sleep();
//		if(logger.isInfoEnabled()) logger.info("A info message.");
//		sleep();
//		if(logger.isWarnEnabled()) logger.warn("A warn message.");
//		sleep();
//		if(logger.isErrorEnabled()) logger.error("A error message.");
//		sleep();
//		MDC.remove("foo");
		execute(new LogStuffWithMdcRunnable(delay, null));
	}

	private Marker createMarker()
	{

		Marker marker = MarkerFactory.getMarker("FooBar");
		Marker marker1 = MarkerFactory.getMarker("Foo");
		Marker marker2 = MarkerFactory.getMarker("Bar");
		Marker marker3 = MarkerFactory.getMarker("Recursive");

		marker.add(marker1);
		marker.add(marker2);
		marker.add(marker3);
		marker3.add(marker);
		return marker;
	}

	private Marker createFnordMarker()
	{

		Marker eris = MarkerFactory.getMarker("Hail Eris!!");
		Marker discordia = MarkerFactory.getMarker("All Hail Discordia!!!");
		eris.add(discordia);
		return eris;
	}

	public void logStuffWithMdcAndMarker() throws InterruptedException
	{
//		MDC.put("foo","bar");
//		MDC.put("marker","manual marker from MDC");
//		if(logger.isTraceEnabled()) logger.trace(marker, "A trace message.");
//		sleep();
//		if(logger.isDebugEnabled()) logger.debug(marker, "A debug message.");
//		sleep();
//		if(logger.isInfoEnabled()) logger.info(marker, "A info message.");
//		sleep();
//		if(logger.isWarnEnabled()) logger.warn(marker, "A warn message.");
//		sleep();
//		if(logger.isErrorEnabled()) logger.error(marker, "A error message.");
//		sleep();
//		MDC.remove("foo");
//		MDC.remove("marker");
		execute(new LogStuffWithMdcRunnable(delay, marker));
	}

	public void logStuffWithMarker() throws InterruptedException
	{
//		if(logger.isTraceEnabled()) logger.trace(marker, "A trace message.");
//		sleep();
//		if(logger.isDebugEnabled()) logger.debug(marker, "A debug message.");
//		sleep();
//		if(logger.isInfoEnabled()) logger.info(marker, "A info message.");
//		sleep();
//		if(logger.isWarnEnabled()) logger.warn(marker, "A warn message.");
//		sleep();
//		if(logger.isErrorEnabled()) logger.error(marker, "A error message.");
//		sleep();
		execute(new LogStuffRunnable(delay, marker));
	}

	public void logException() throws InterruptedException
	{
		//noinspection ThrowableInstanceNeverThrown
		Throwable ex=new RuntimeException("Test-Exception");
//		if(logger.isTraceEnabled()) logger.trace("A trace message.", ex);
//		sleep();
//		if(logger.isDebugEnabled()) logger.debug("A debug message.",ex);
//		sleep();
//		if(logger.isInfoEnabled()) logger.info("A info message.",ex);
//		sleep();
//		if(logger.isWarnEnabled()) logger.warn("A warn message.",ex);
//		sleep();
//		if(logger.isErrorEnabled()) logger.error("A error message.",ex);
//		sleep();
		execute(new LogThrowableRunnable(delay, ex));
	}

//	public interface StuffLogger
//	{
//		void logStuff();
//	}

	public void logAnonymous()
	{
//		class MethodInternal
//			implements StuffLogger
//		{
//			public void logStuff()
//			{
//				if(logger.isErrorEnabled()) logger.error("MethodInternalClass");
//
//			}
//		}
//
//		StuffLogger stuffLogger=new StuffLogger()
//		{
//			class AnonymousInternalClass
//				implements StuffLogger
//			{
//				public void logStuff()
//				{
//					if(logger.isErrorEnabled()) logger.error("AnonymousInternalClass");
//
//				}
//			}
//
//			StuffLogger stuffLogger=new StuffLogger()
//			{
//				public void logStuff()
//				{
//					if(logger.isWarnEnabled()) logger.warn("SecondLevelAnonymous");
//				}
//			};
//
//			public void logStuff()
//			{
//				if(logger.isInfoEnabled()) logger.info("FirstLevelAnonymous");
//				stuffLogger.logStuff();
//				new AnonymousInternalClass().logStuff();
//				new MethodInternal().logStuff();
//			}
//		};
//		stuffLogger.logStuff();
		execute(new LogAnonymousRunnable(delay));
	}

	public void logException2() throws InterruptedException
	{
		//noinspection ThrowableInstanceNeverThrown
		Throwable ex=new RuntimeException("Another Test-Exception", new RuntimeException("Test-Exception"));
//		if(logger.isTraceEnabled()) logger.trace("A trace message.", ex);
//		sleep();
//		if(logger.isDebugEnabled()) logger.debug("A debug message.",ex);
//		sleep();
//		if(logger.isInfoEnabled()) logger.info("A info message.",ex);
//		sleep();
//		if(logger.isWarnEnabled()) logger.warn("A warn message.",ex);
//		sleep();
//		if(logger.isErrorEnabled()) logger.error("A error message.",ex);
//		sleep();
		execute(new LogThrowableRunnable(delay, ex));
	}

	public void logSkull()
	{
//		if (logger.isDebugEnabled())
//		{
//			logger.debug("Skull!\n" +
//			"     _.--\"\"\"\"\"--._\n" +
//			"   .'             '.\n" +
//			"  /                 \\\n" +
//			" ;                   ;\n" +
//			" |                   |\n" +
//			" |                   |\n" +
//			" ;                   ;\n" +
//			"  \\ (`'--,    ,--'`) /\n" +
//			"   \\ \\  _ )  ( _  / /\n" +
//			"    ) )(')/  \\(')( (\n" +
//			"   (_ `\"\"` /\\ `\"\"` _)\n" +
//			"    \\`\"-, /  \\ ,-\"`/\n" +
//			"     `\\ / `\"\"` \\ /`\n" +
//			"      |/\\/\\/\\/\\/\\|\n" +
//			"      |\\        /|\n" +
//			"      ; |/\\/\\/\\| ;\n" +
//			"       \\`-`--`-`/\n" +
//			"        \\      /\n" +
//			"         ',__,'\n" +
//			"          q__p\n" +
//			"          q__p\n" +
//			"          q__p\n" +
//			"          q__p\n");
//		}
		execute(new LogSkullRunnable(delay));
	}

//	private void sleep() throws InterruptedException
//	{
//		if(delay>0)
//		{
//			Thread.sleep(delay);
//		}
//	}

//	private static final String[][] beliefs=
//			{
//	{ "The" },
//                { "Arian", "Athanasian", "Roman", "Sunni", "Eastern", "Presbyterian", "United Reformed",
//                  "Unitarian", "Anglican", "Jehovah's", "Jewish", "Fundamentalist", "Shiite", "Hari",
//                  "Shinto", "Zoroastrian", "Christian", "Salvation", "Seventh Day", "Saphardic", "Zen",
//                  "Hebraic", "Palestinian", "Galilean", "New Age", "Reformed", "Orthodox", "Southern",
//                  "Northern", "Eastern", "Western" },
//								{ "Catholic", "Baptist", "Protestant", "Muslim", "Moonie", "Mormon", "Witnesses", "Sikh",
//                  "Buddhist", "Krishna", "Methodist", "Fire-worshipper", "Parsee", "Theosophist", "Science",
//                  "Army", "Adventist", "Confucianist", "Shamanist", "Taoist", "Atheist", "Rosicrusian",
//                  "Sub-Genius", "Episcopalian", "Secular Humanist", "Revisionist", "Masonic", "Shriner",
//                  "Pentecostal", "Charismatic", "Thetan", "Odd Fellow", "PreClear", "Twelve Step", "Druid",
//                  "Lutheran", "Four Square", "Bible Thumpers", "Flat-Earth", "Whole-Earth" },
//								{ "Church", "Faith", "Sect", "Heresy", "Liberation Front", "Elder Council", "Lodge", "Society",
//                  "Temple", "Army", "Congregation", "Sunday School", "Cult", "Fellowship" },
//								{ "believes that", "denies that", "is strongly divided over whether", "rejects the notion that",
//                  "posits that", "insists that", "professes that", "proclaims that", "suspects that" },
//								{ "the Bible", "the Koran", "the Magna Carta", "the Kama Sutra", "the Bagavad Gita", "the Talmud",
//                  "the Torah", "the Satanic Verses", "the Mosaic Law", "the book of Revelation", "the collection of predictions of Nostradamus",
//                  "every Norse saga", "the new, controversial eleventh Commandment", "the Apocrypha", "the Sargent Pepper album",
//                  "the Athanasian creed", "the Rosetta Stone", "chapter thirteen of Dianetics", "a transcript of the very first Ramtha channeling",
//                  "The Writ of Common Wisdom", "a certain part of \"The Last Temptation of Christ\"", "\"The Prophet\", by Kahlil Gibran",
//                  "\"The Profit\", by Kehlog Albran", "a secret document hidden in Fawn Hall's underwear",
//                  "one of the lesser known Dead Sea Scrolls", "the Marquis de Sade's \"Bedroom Etiquette\"",
//                  "a cryptic notation on the back of the bar tab from the Last Supper", "Darwin's \"Origin of Species\"",
//                  "a particularly crude example of poetry from the men's room wall", "Elvis's Will",
//                  "The Pentagon Papers", "Shirley McLaine's \"Out on a Limb\"", "an inscription found on the basement wall in the Temple of Karnak",
//                  "\"The Life of Brian\"", "a certain scrap of MVS source code from 1971", "an IBM punch card found on the machine room floor in 1978, in reality",
//                  "the private diary of Marcia Brady", "the Book of Mormon (with introduction by Charlton Heston),",
//                  "grafitti once found on the A-train to Far Rockaway in 1969", "a certain limerick containing a reference to \"Nantucket\", in reality",
//                  "Randy Travis' Copenhagen ring", "a mysterious pattern of cat box scratchings",
//                  "\"The Yosemite\" by John Muir", "\"No Bad Dogs\", by Barbara Woodhouse", "\"Atlas Shrugged\", by Ayn Rand",
//                  "a certain cryptic coding construct of Richard Stallman", "Cher's secret tattoo",
//                  "the bumps on the skull of Thomas \"Tip\" O'Neill (if interpreted as braille),",
//                  "Jonathan Livingston Seagull", "a particularly obscure pattern of crop circles",
//                  "scene II, act V of \"The Search for Spock\"", "a forgotten screenplay by Sidney Sheldon",
//                  "the pattern of crows feet around Nancy Reagan's eyes (as interpreted by her astrologist),",
//                  "a certain old \"Leave it to Beaver\" script", "Nancy Reagan's \"My Turn\"" },
//								{ "predicts the eventual supremacy of", "contains a direct reference to", "has been misunderstood by",
//                  "proclaims the divinity of", "can be read as denouncing the divinity of", "is an insult to",
//                  "is a heretical misinterpretation of scripture by", "was inspired by", "denies the existence of",
//                  "contains coded messages from the Inner Child of", "was dictated by", "encodes the personal opinions of",
//                  "can be deduced from the writings of", "denies the existence of", "implies the divinity of",
//                  "justifies worshipping the sandals of", "explains the holy writings of", "can be interpreted as the memoirs of",
//                  "if read backwards, reveals cryptic messages from", "if held up to strong light, reveals a message from" },
//								{ "Salman Rushdie,", "the Ayatollah,", "Thomas Aquinas,", "Billy Graham,", "J. Z. Knight,",
//                  "Elijah,", "Elvis,", "Elvis Costello,", "Elvira, Mistress of the Dark,", "Jesus' little known brother Tony,",
//                  "the Reverend Al Sharpton,", "Louisiana State Senator Merle Jacobs,", "John Lennon,",
//                  "Buddy Holly,", "Andrew Lloyd Weber,", "a certain bombastic revivalist preacher,",
//                  "the creature from the Black Lagoon,", "a particular White-Supremacist from Hayden Lake,",
//                  "Jerry Lee Lewis,", "a twelve year old stigmatic from Lubbock Texas,", "Zeus,",
//                  "King Tut,", "Ernest Hemingway,", "Mariel Hemingway,", "Ernest Borgnine,", "Ernest P. Worrell,",
//                  "Ernest P. Worrell's love-child,", "\"Jake the Snake\",", "Ann Landers,", "Bess Truman,",
//                  "a fire-and-brimstone street preacher from Provo,", "the last living Baghwan Shree-Rajneesh loyalist,",
//                  "a certain white-shoed car salesman,", "Crazy Eddie,", "Howard Stern,", "a certain Australian footballer named Bubba,",
//                  "Father Guido Sarducci,", "the last practicing medicine man of the Mohicans,",
//                  "James Randi,", "Our Lady of the White Go-Go Boot,", "Verleen, The patron saint of Big Hair,",
//                  "Helga, the patron saint of Unseasoned Food,", "Randy, the patron saint of Big Pickup Trucks,",
//                  "Maxine, the patron saint of Press-on Nails,", "Tina, the patron saint of Polyester Stretch Pants,",
//                  "Boopsie, the patron saint of Large Dangly Earrings,",  "Mahomet,", "Walt Disney,",
//                  "Ezekiel,", "Dan Quayle,",  "Murphy Brown,",  "George Bush,", "Oliver North,",
//                  "Thomas Merton", "Leo Buscaglia,", "Marilyn Quayle,", "Ernest Angsley,", "Gautama Buddha,",
//                  "Odin and Thor,", "Adolf Hitler,", "Linus Pauling,", "Pope John-Paul II,", "Bishop Spong,",
//                  "Mumon,", "Martin Scorcese,", "Bill Gates,", "Daryl Gates,", "Robert Gates,",
//                  "Robert DeNiro", "the Mormon Tabernacle Choir,", "Jim Bakker,", "Tammy-Faye Bakker,",
//                  "Jimmy Swaggart,", "Susanne Somers,", "Richard Simmons,", "Richard Lewis,",
//                  "Cardinal Richelieu,", "Charles Manson,", "James Taylor,", "Tipper Gore,", "Hillary Clinton,",
//                  "Dolly Parton,", "Jerry Falwell,", "Robert Bly,", "Joseph Campbell,", "Zig Zeigler,",
//                  "L. Ron Hubbard,", "Captain Al Hubbard,", "Jesse Helms,", "Jesse Jackson,",
//                  "Jimi Hendrix,", "The Juice Man (tm),", "Clarence Thomas,", "Long Dong Silver,",
//                  "a certain paunchy, ex-Chippendale dancer,", "the shadowy leader of a certain gang of biker nuns,",
//                  "a spaced out, pot smoking crystal worshipper,", "a certain swarthy wild-eyed, Eastern European anarchist,",
//                  "a certain middle-aged bag lady with a personality disorder,", "a certain pornography-addicted TV preacher,",
//                  "a certain washed up TV actor that's now doing infomercials on cable,", "a certain philandering, saxophone-playing Southern Governor,",
//                  "a certain flamboyant, gender-bending rock star,", "a certain female talk show pop-psychologist,",
//                  "a certain truck stop waitress from Cocolalla, Idaho,", "a certain shameful, Republican soon-to-be-ex President,",
//                  "a certain small town sheriff from North Carolina,", "an avowed heterosexual female tennis player,",
//                  "a certain absent-minded, father figure ex-President,", "a certain blonde, pointy-bra wearing pop singer,",
//                  "David Duke,",  "Robert Mapplethorpe,", "Wilheim Reich,", "Kreskin,", "Mahatma Gandhi,",
//                  "Abbie Hoffman,", "Timothy Leary,", "Jack Kerouac,", "Allen Ginsberg", "Paul Krassner,",
//                  "Marlin Fitzwater,", "Ken Kesey,", "Helen Gurley Brown", "Tina Brown", "David Geffen",
//                  "Howard Cosell,", "Cy Sperling,", "Andy Warhol", "Ferdie \"The Fight Doctor\" Pacheco,",
//                  "Madonna,", "The Beatles," },
//								{ "and", "but" },
//								{ "says", "explains", "claims", "believes", "argues", "insists", "refutes", "denies",
//                  "professes", "preaches" },
//								{ "that", "that soon", "that after the second coming", "that ultimately", "that inevitably",
//                  "that if we are true and faithful servants," },
//								{ "we should strive for", "the world will end with", "we should beware", "we shall see",
//                  "the ungodly are about to experience", "only the faithful will achieve", "the chosen people will soon experience",
//                  "men will yield to", "women will yield to", "mankind will yield to", "man will succeed in avoiding",
//                  "woman will succeed in avoiding", "mankind will succeed in avoiding", "only the born-again will actually enjoy",
//                  "only the chosen people will experience", "the chosen people will be spared" },
//								{ "reincarnation.", "Judgement Day.", "the Apocalypse.", "\"Apocalypse Now\".",
//                  "Oral Robert's prayer tower.", "nirvana.", "Nirvana (the band).", "Mount Olympus.",
//                  "martyrdom.", "the Second Coming.", "the sound of one hand clapping.", "birth control.",
//                  "better living through chemistry.", "human sacrifices.", "cherry pie and damn fine coffee.",
//                  "damnation.", "Papal Infallability.", "enlightenment.", "the \"666\" mysteriously found tattooed on your head.",
//                  "pinball heaven.", "phallic idoltry.", "pubic hair on your Coke.", "a guilt-free afterlife.",
//                  "guilt-free sex.", "guilt-free bingo.", "anarchy.", "government, by the people, and for the people.",
//                  "remorse for their crimes.", "\"Crimes Against Nature\" (at least in some Southern states).",
//                  "the Turin Shroud.", "purgatory.", "premarital sex.", "a feeling of oneness with nature.",
//                  "that oh-so-fresh feeling.", "that not-so-fresh feeling.", "shiatsu massage.",
//                  "chiropractic treatment.", "ESO.", "tetrahydrocannabinol.", "lysergic acid diethylamide.",
//                  "psilocybin.", "quinuclidinyl benzilate.", "phenylpropanolamine.", "Xanax addiction.",
//                  "Prozac addiction.", "a life made livable with clozapine.", "colonic irrigation, twice a day.",
//                  "self abuse.", "real tears from Michelangelo's \"Pieta\".", "the face of Jesus in a bowl of spaghetti.",
//                  "one man, one vote.", "a resurgence of tribbles.", "a Popeil pocket fisherman.",
//                  "\"from each according to their ability, to each according to their need\".",
//                  "the upcoming Vernal Equinox.", "Buddha Nature.", "the upcoming Summer Solstice.",
//                  "trout fishing.", "rolfing.", "engrams.", "utopia.", "the ThighMaster.", "bliss.",
//                  "the monolith in 2001 A Space Odessy.", "the writings of Douglas Hofsteader.",
//                  "the end of the world.", "the eventual domination of world.", "backward masking.",
//                  "megadoses of vitamin C." }
//			};
//
//	private static String getTruth()
//	{
//		StringBuffer result=new StringBuffer();
//		for(String[] current: beliefs)
//		{
//			result.append(current[(int)(Math.random()*current.length)]).append(" ");
//		}
//		return result.toString();
//	}

	public void logTruth()
	{
//		String truth=getTruth();
//		truth=WordUtils.wrap(truth, 40);
//		if(logger.isErrorEnabled()) logger.error(fnordMarker, truth);
//
		execute(new LogTruthRunnable(delay, fnordMarker));
	}
}
