/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2008 Joern Huxhorn
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.huxhorn.lilith.data.logging;

import org.junit.Test;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static junit.framework.Assert.assertEquals;

import java.util.Arrays;

public class MessageFormatterTest
{
	private final Logger logger = LoggerFactory.getLogger(MessageFormatterTest.class);

	private UseCase[] useCases;

	@Before
	public void initUseCases()
	{
		Integer i1 = 1;
		Integer i2 = 2;
		Integer i3 = 3;
		//noinspection ThrowableInstanceNeverThrown
		Throwable t=new FooThrowable("FooThrowable");

		useCases=new UseCase[]
		{





new UseCase("Null message", null, new Object[]{}, 0, null),
new UseCase("Null message", null, new Object[]{i1, i2, i3, t}, 0, null, t),
new UseCase("Null params", "Value is {}.", new Object[]{null}, 1, "Value is null."),
new UseCase("Null params", "Val1 is {}, val2 is {}.", new Object[]{null, null}, 2, "Val1 is null, val2 is null."),
new UseCase("Null params", "Val1 is {}, val2 is {}.", new Object[]{i1, null}, 2, "Val1 is 1, val2 is null."),
new UseCase("Null params", "Val1 is {}, val2 is {}.", new Object[]{null, i2}, 2, "Val1 is null, val2 is 2."),
new UseCase("Null params", "Val1 is {}, val2 is {}, val3 is {}", new Object[]{null, null, null}, 3, "Val1 is null, val2 is null, val3 is null"),
new UseCase("Null params", "Val1 is {}, val2 is {}, val3 is {}", new Object[]{null, i2, i3}, 3, "Val1 is null, val2 is 2, val3 is 3"),
new UseCase("Null params", "Val1 is {}, val2 is {}, val3 is {}", new Object[]{null, null, i3}, 3, "Val1 is null, val2 is null, val3 is 3"),
new UseCase("One param", "Value is {}.", new Object[]{i3}, 1, "Value is 3."),
new UseCase("One param", "Value is {", new Object[]{i3}, 0, "Value is {"),
new UseCase("One param", "{} is larger than 2.", new Object[]{i3}, 1, "3 is larger than 2."),
new UseCase("One param", "No subst", new Object[]{i3}, 0, "No subst"),
new UseCase("One param", "Incorrect {subst", new Object[]{i3}, 0, "Incorrect {subst"),
new UseCase("One param", "Value is \\{bla} {}", new Object[]{i3}, 1, "Value is {bla} 3"),
new UseCase("One param", "Escaped \\{} subst", new Object[]{i3}, 0, "Escaped {} subst"),
new UseCase("One param", "\\{Escaped", new Object[]{i3}, 0, "{Escaped"),
new UseCase("One param", "\\{}Escaped", new Object[]{i3}, 0, "{}Escaped"),
new UseCase("One param", "File name is \\{{}}.", new Object[]{"App folder.zip"}, 1, "File name is {App folder.zip}."),
new UseCase("One param", "File name is C:\\\\{}.", new Object[]{"App folder.zip"}, 1, "File name is C:\\App folder.zip."),
new UseCase("Two params", "Value {} is smaller than {}.", new Object[]{i1, i2}, 2, "Value 1 is smaller than 2."),
new UseCase("Two params", "Value {} is smaller than {}", new Object[]{i1, i2}, 2, "Value 1 is smaller than 2"),
new UseCase("Two params", "{}{}", new Object[]{i1, i2}, 2, "12"),
new UseCase("Two params", "Val1={}, Val2={", new Object[]{i1, i2}, 1, "Val1=1, Val2={"),
new UseCase("Two params", "Value {} is smaller than \\{}", new Object[]{i1, i2}, 1, "Value 1 is smaller than {}"),
new UseCase("Two params", "Value {} is smaller than \\{} tail", new Object[]{i1, i2}, 1, "Value 1 is smaller than {} tail"),
new UseCase("Two params", "Value {} is smaller than \\{", new Object[]{i1, i2}, 1, "Value 1 is smaller than {"), // was originally "Value 1 is smaller than \\{"
new UseCase("Two params", "Value {} is smaller than \\{tail", new Object[]{i1, i2}, 1, "Value 1 is smaller than {tail"),
new UseCase("Two params", "Value \\{} is smaller than {}", new Object[]{i1, i2}, 1, "Value {} is smaller than 1"),
new UseCase("Null Array", "msg0", null, 0, "msg0"),
new UseCase("Null Array", "msg1 {}", null, 1, "msg1 {}"),
new UseCase("Null Array", "msg2 {} {}", null, 2, "msg2 {} {}"),
new UseCase("Null Array", "msg3 {} {} {}", null, 3, "msg3 {} {} {}"),
new UseCase("Array", "Value {} is smaller than {} and {}.", new Object[]{i1, i2, i3}, 3, "Value 1 is smaller than 2 and 3."),
new UseCase("Array", "{}{}{}", new Object[]{i1, i2, i3}, 3, "123"),
new UseCase("Array", "Value {} is smaller than {}.", new Object[]{i1, i2, i3}, 2, "Value 1 is smaller than 2."),
new UseCase("Array", "Value {} is smaller than {}", new Object[]{i1, i2, i3}, 2, "Value 1 is smaller than 2"),
new UseCase("Array", "Val={}, {, Val={}", new Object[]{i1, i2, i3}, 1, "Val=1, {, Val={}"),
new UseCase("Array", "Val={}, \\{, Val={}", new Object[]{i1, i2, i3}, 2, "Val=1, {, Val=2"),
new UseCase("Array", "Val1={}, Val2={", new Object[]{i1, i2, i3}, 1, "Val1=1, Val2={"),
new UseCase("Array & Throwable", "Value {} is smaller than {} and {}.", new Object[]{i1, i2, i3, t}, 3, "Value 1 is smaller than 2 and 3.", t),
new UseCase("Array & Throwable", "{}{}{}", new Object[]{i1, i2, i3, t}, 3, "123", t),
new UseCase("Array & Throwable", "Value {} is smaller than {}.", new Object[]{i1, i2, i3, t}, 2, "Value 1 is smaller than 2.", t),
new UseCase("Array & Throwable", "Value {} is smaller than {}", new Object[]{i1, i2, i3, t}, 2, "Value 1 is smaller than 2", t),
new UseCase("Array & Throwable", "Val={}, {, Val={}", new Object[]{i1, i2, i3, t}, 1, "Val=1, {, Val={}", t),
new UseCase("Array & Throwable", "Val={}, \\{, Val={}", new Object[]{i1, i2, i3, t}, 2, "Val=1, {, Val=2", t),
new UseCase("Array & Throwable", "Val1={}, Val2={", new Object[]{i1, i2, i3, t}, 1, "Val1=1, Val2={", t),
new UseCase("Array & Used Throwable", "Value {} is smaller than {} and {}. Also: {}!", new Object[]{i1, i2, i3, t}, 4, "Value 1 is smaller than 2 and 3. Also: "+t.toString()+"!"),
new UseCase("Array & Used Throwable", "{}{}{}{}", new Object[]{i1, i2, i3, t}, 4, "123"+t.toString()),
new UseCase("Escaping", "Value {} is smaller than \\\\{}", new Object[]{i1, i2, i3, t}, 2, "Value 1 is smaller than \\2", t),
new UseCase("Escaping", "Value {} is smaller than \\\\{} tail", new Object[]{i1, i2, i3, t}, 2, "Value 1 is smaller than \\2 tail", t),
new UseCase("Escaping", "Value {} is smaller than \\\\{", new Object[]{i1, i2, i3, t}, 1, "Value 1 is smaller than \\\\{", t),
new UseCase("Escaping", "Value {} is smaller than \\\\{tail", new Object[]{i1, i2, i3, t}, 1, "Value 1 is smaller than \\\\{tail", t), // was originally "Value 1 is smaller than \\{tail"
new UseCase("Escaping", "Value \\\\{} is smaller than {}", new Object[]{i1, i2, i3, t}, 2, "Value \\1 is smaller than 2", t),
new UseCase("Escaping", "\\\\{}", new Object[]{i1, i2, i3, t}, 1, "\\1", t),
new UseCase("Escaping", "\\\\\\{}",  new Object[]{i1, i2, i3, t}, 0, "\\{}", t),
new UseCase("Escaping", "\\\\\\\\{}",  new Object[]{i1, i2, i3, t}, 1, "\\\\1", t),
new UseCase("Escaping", "\\{}",  new Object[]{i1, i2, i3, t}, 0, "{}", t),
				
		};
	}

	@Test
	public void simpleCountPlaceholders()
	{
		validateCountArgumentPlaceholders(null,0);
		validateCountArgumentPlaceholders("foo",0);
		validateCountArgumentPlaceholders("{}",1);
		validateCountArgumentPlaceholders("{} {} {}",3);
	}

	@Test
	public void brokenCountPlaceholders()
	{
		validateCountArgumentPlaceholders("{",0);
		validateCountArgumentPlaceholders("{} { {}",1);
		validateCountArgumentPlaceholders("{} {",1);
	}

	@Test
	public void escapedCountPlaceholders()
	{
		validateCountArgumentPlaceholders("\\{}",0);
		validateCountArgumentPlaceholders("\\\\{}",1);
		validateCountArgumentPlaceholders("\\\\\\{}",0);

		validateCountArgumentPlaceholders("{} \\{}",1);
		validateCountArgumentPlaceholders("{} \\\\{}",2);
		validateCountArgumentPlaceholders("{} \\\\\\{}",1);
	}


	@Test
	public void useCasesCountArguments()
	{
		for(int i=0;i<useCases.length;i++)
		{
			UseCase useCase=useCases[i];
			if(logger.isDebugEnabled()) logger.debug("Validating countArguments for [{}]: {}...", i, useCase);
			validateCountArgumentPlaceholders(useCase.getMessagePattern(), useCase.getNumberOfPlaceholders());
		}
	}

	@Test
	public void evaluateArguments()
	{
		validateEvaluateArguments(null, null, null);

		validateEvaluateArguments("{}{}{}",
				new Object[]{"foo", null, 1L},
				new MessageFormatter.ArgumentResult(
						new String[]{"foo", null, "1"},
						null));

		//noinspection ThrowableInstanceNeverThrown
		FooThrowable t=new FooThrowable("FooException");
		validateEvaluateArguments("{}{}",
				new Object[]{"foo", null, t},
				new MessageFormatter.ArgumentResult(
						new String[]{"foo", null},
						t));

		validateEvaluateArguments("{}{}{}",
				new Object[]{"foo", null, t},
				new MessageFormatter.ArgumentResult(
						new String[]{"foo", null, "FooException"},
						null));

		validateEvaluateArguments("{}{}{}",
				new Object[]{"foo", null, t, 17L, 18L},
				new MessageFormatter.ArgumentResult(
						new String[]{"foo", null, "FooException", "17", "18"},
						null));

		validateEvaluateArguments("{}{}{}",
				new Object[]{"foo", null, 17L, 18L, t},
				new MessageFormatter.ArgumentResult(
						new String[]{"foo", null, "17", "18"},
						t));
	}

	public void validateEvaluateArguments(String messagePattern, Object[] arguments, MessageFormatter.ArgumentResult expected)
	{
		MessageFormatter.ArgumentResult result = MessageFormatter.evaluateArguments(messagePattern, arguments);
		StringBuilder message=new StringBuilder();
		message.append("messagePattern=");
		if(messagePattern!=null)
		{
			message.append("'").append(messagePattern).append("'");
		}
		else
		{
			message.append("null");
		}
		message.append(" and arguments=");
		if(arguments!=null)
		{
			message.append("[");
			boolean isFirst=true;
			for(Object current: arguments)
			{
				if(!isFirst)
				{
					message.append(", ");
				}
				else
				{
					isFirst=false;
				}
				message.append(current);
			}
			message.append("]");
		}
		else
		{
			message.append("null");
		}

		message.append(" did not return expected result!");
		assertEquals(message.toString(), expected, result);

	}

	@Test
	public void useCasesValidateEvaluateArguments()
	{
		for(int i=0;i<useCases.length;i++)
		{
			UseCase useCase=useCases[i];
			if(logger.isDebugEnabled()) logger.debug("Validating evaluateArguments for [{}]: {}...", i, useCase);


			String[] argStrings=useCase.getArgumentStrings();
			MessageFormatter.ArgumentResult expectedResult=null;
			if(argStrings!=null)
			{
				expectedResult=new MessageFormatter.ArgumentResult(argStrings, useCase.getThrowable());
			}
			validateEvaluateArguments(useCase.getMessagePattern(), useCase.getArguments(), expectedResult);
		}
	}

	@Test
	public void useCasesValidateFormatMessage()
	{
		for(int i=0;i<useCases.length;i++)
		{
			UseCase useCase=useCases[i];
			if(logger.isDebugEnabled()) logger.debug("Validating format for [{}]: {}...", i, useCase);

			validateFormatMessage(useCase);
		}
	}

	public void validateCountArgumentPlaceholders(String messagePattern, int expected)
	{
		int result=MessageFormatter.countArgumentPlaceholders(messagePattern);

		StringBuilder message=new StringBuilder();
		message.append("messagePattern ");
		if(messagePattern!=null)
		{
			message.append("'").append(messagePattern).append("'");
		}
		else
		{
			message.append("null");
		}
		message.append(" does not contain ").append(expected).append(" placeholders.");
		assertEquals(message.toString(), expected, result);

	}


	private void validateFormatMessage(UseCase useCase)
	{
		String messagePattern = useCase.getMessagePattern();

		String[] argumentStrings=useCase.getArgumentStrings();
		String expectedResult=useCase.getExpectedResult();
		validateFormatMessage(messagePattern, argumentStrings, expectedResult);
	}

	private void validateFormatMessage(String messagePattern, String[] argumentStrings, String expectedResult)
	{
		String result = MessageFormatter.format(messagePattern, argumentStrings);

		StringBuilder message=new StringBuilder();
		message.append("messagePattern ");
		if(messagePattern!=null)
		{
			message.append("'").append(messagePattern).append("'");
		}
		else
		{
			message.append("null");
		}
		message.append(" with arguments ");
		if(argumentStrings==null)
		{
			message.append("null");
		}
		else
		{
			message.append("[");
			boolean isFirst=true;
			for(String current:argumentStrings)
			{
				if(isFirst)
				{
					isFirst=false;
				}
				else
				{
					message.append(", ");
				}
				if(current==null)
				{
					message.append("null");
				}
				else
				{
					message.append("'").append(current).append("'");
				}
			}
			message.append("]");
		}
		message.append(" did not result in ");
		if(expectedResult==null)
		{
			message.append("null");
		}
		else
		{
			message.append("'").append(expectedResult).append("'!");
		}
		assertEquals(message.toString(), expectedResult, result);
	}
	
	private static class FooThrowable extends Throwable
	{
		public FooThrowable(String s)
		{
			super(s);
		}

		@Override
		public String toString()
		{
			return ""+getMessage();
		}
	}

	private static class UseCase
	{
		private String messagePattern;
		private Object[] arguments;
		private int numberOfPlaceholders;
		private String expectedResult;
		private Throwable throwable;
		private String[] argumentStrings;
		private String section;

		public UseCase(String section, String messagePattern, Object[] arguments, int numberOfPlaceholders, String expectedResult)
		{
			this(section, messagePattern, arguments, numberOfPlaceholders, expectedResult, null);
		}

		public UseCase(String section, String messagePattern, Object[] arguments, int numberOfPlaceholders, String expectedResult, Throwable throwable)
		{
			this.section=section;
			this.messagePattern=messagePattern;
			this.arguments=arguments;
			this.numberOfPlaceholders=numberOfPlaceholders;
			this.expectedResult=expectedResult;
			this.throwable=throwable;
			if(arguments!=null)
			{
				if(throwable!=null)
				{
					this.argumentStrings=new String[arguments.length-1];
					for(int i=0;i<argumentStrings.length;i++)
					{
						if(arguments[i]!=null)
						{
							argumentStrings[i]=arguments[i].toString();
						}
					}
				}
				else
				{
					this.argumentStrings=new String[arguments.length];
					for(int i=0;i<argumentStrings.length;i++)
					{
						if(arguments[i]!=null)
						{
							argumentStrings[i]=arguments[i].toString();
						}
					}
				}
			}
		}

		public String getMessagePattern()
		{
			return messagePattern;
		}

		public Object[] getArguments()
		{
			return arguments;
		}

		public int getNumberOfPlaceholders()
		{
			return numberOfPlaceholders;
		}

		public String getExpectedResult()
		{
			return expectedResult;
		}

		public Throwable getThrowable()
		{
			return throwable;
		}

		public String[] getArgumentStrings()
		{
			return argumentStrings;
		}

		/**
		 * For informational purpose only.
		 * @return
		 */
		public String getSection()
		{
			return section;
		}

		public boolean equals(Object o)
		{
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			UseCase useCase = (UseCase) o;

			if (numberOfPlaceholders != useCase.numberOfPlaceholders) return false;
			// Probably incorrect - comparing Object[] arrays with Arrays.equals
			if (!Arrays.equals(arguments, useCase.arguments)) return false;
			if (expectedResult != null ? !expectedResult.equals(useCase.expectedResult) : useCase.expectedResult != null)
				return false;
			if (messagePattern != null ? !messagePattern.equals(useCase.messagePattern) : useCase.messagePattern != null)
				return false;
			if (throwable != null ? !throwable.equals(useCase.throwable) : useCase.throwable != null) return false;

			return true;
		}

		public int hashCode()
		{
			int result;
			result = (messagePattern != null ? messagePattern.hashCode() : 0);
			result = 31 * result + (arguments != null ? Arrays.hashCode(arguments) : 0);
			result = 31 * result + numberOfPlaceholders;
			result = 31 * result + (expectedResult != null ? expectedResult.hashCode() : 0);
			result = 31 * result + (throwable != null ? throwable.hashCode() : 0);
			return result;
		}

		@Override
		public String toString()
		{
			StringBuilder result=new StringBuilder();
			result.append("UseCase[");
			result.append("section=").append(section);
			result.append(", ");
			result.append("messagePattern=");
			if(messagePattern==null)
			{
				result.append("null");
			}
			else
			{
				result.append("'").append(messagePattern).append("'");
			}
			result.append(", ");
			result.append("arguments=");
			if(arguments==null)
			{
				result.append("null");
			}
			else
			{
				result.append("[");
				boolean isFirst=true;
				for(Object argument:arguments)
				{
					if(isFirst)
					{
						isFirst=false;
					}
					else
					{
						result.append(", ");
					}
					result.append(argument);
				}
				result.append("]");
			}
			result.append(", ");
			result.append("numberOfPlaceholders=").append(numberOfPlaceholders);

			result.append(", ");
			result.append("expectedResult=");
			if(expectedResult==null)
			{
				result.append("null");
			}
			else
			{
				result.append("'").append(expectedResult).append("'");
			}

			result.append(", ");
			result.append("throwable=").append(throwable);
			result.append("]");
			return result.toString();
		}
	}
}
