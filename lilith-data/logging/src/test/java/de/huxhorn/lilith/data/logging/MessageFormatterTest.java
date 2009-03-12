/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2009 Joern Huxhorn
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

import static junit.framework.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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
		Throwable t = new FooThrowable("FooThrowable");
		Integer[] p1 = new Integer[]{i2, i3};

		Object[] multiArray = new Object[]{null, p1};
		Object[] multiArrayRecursive = new Object[]{null, p1};
		multiArrayRecursive[0] = multiArrayRecursive;
		multiArray[0] = multiArrayRecursive;
		String multiArrayRecId = MessageFormatter.identityToString(multiArrayRecursive);
		String multiArrayRec = MessageFormatter.RECURSION_PREFIX + multiArrayRecId + MessageFormatter.RECURSION_SUFFIX;

		Integer[] ia0 = new Integer[]{i1, i2, i3};
		Integer[] ia1 = new Integer[]{10, 20, 30};

		Object[][] multiOA = new Object[][]{ia0, ia1};
		Object[][][] _3DOA = new Object[][][]{multiOA, multiOA};

		Object[] cyclicA = new Object[1];
		cyclicA[0] = cyclicA;

		String cyclicAId = MessageFormatter.identityToString(cyclicA);

		Object[] recArray;
		Object[] cyclicB = new Object[2];
		{
			cyclicB[0] = i1;
			Object[] c = new Object[]{i3, cyclicB};
			Object[] b = new Object[]{i2, c};
			recArray = b;
			cyclicB[1] = b;
		}
		String cyclicBRecId = MessageFormatter.identityToString(recArray);

		Object[] cyclicC = new Object[3];
		{
			cyclicC[0] = i1;
			Object[] c = new Object[]{i3, cyclicC};
			Object[] b = new Object[]{i2, c};
			recArray = b;
			cyclicC[1] = b;
			cyclicC[2] = t;
		}
		String cyclicCRecId = MessageFormatter.identityToString(recArray);

		String cyclicARec = MessageFormatter.RECURSION_PREFIX + cyclicAId + MessageFormatter.RECURSION_SUFFIX;
		String cyclicBRec = MessageFormatter.RECURSION_PREFIX + cyclicBRecId + MessageFormatter.RECURSION_SUFFIX;
		String cyclicCRec = MessageFormatter.RECURSION_PREFIX + cyclicCRecId + MessageFormatter.RECURSION_SUFFIX;

//		if(logger.isInfoEnabled()) logger.info("multiArray rec identity: {}", multiArrayRecId);
//		if(logger.isInfoEnabled()) logger.info("cyclicA identity: {}", cyclicAId);
//		if(logger.isInfoEnabled()) logger.info("cyclicB rec identity: {}", cyclicBRecId);
//		if(logger.isInfoEnabled()) logger.info("cyclicC rec identity: {}", cyclicCRecId);
		useCases = new UseCase[]{


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
			new UseCase("One param", "Value is {bla} {}", new Object[]{i3}, 1, "Value is {bla} 3"),
			new UseCase("One param", "Value is \\{bla} {}", new Object[]{i3}, 1, "Value is \\{bla} 3"),
			new UseCase("One param", "Escaped \\{} subst", new Object[]{i3}, 0, "Escaped {} subst"),
			new UseCase("One param", "{Escaped", new Object[]{i3}, 0, "{Escaped"),
			new UseCase("One param", "\\{}Escaped", new Object[]{i3}, 0, "{}Escaped"),
			new UseCase("One param", "File name is {{}}.", new Object[]{"App folder.zip"}, 1, "File name is {App folder.zip}."),
			new UseCase("One param", "File name is C:\\\\{}.", new Object[]{"App folder.zip"}, 1, "File name is C:\\App folder.zip."),
			new UseCase("Two params", "Value {} is smaller than {}.", new Object[]{i1, i2}, 2, "Value 1 is smaller than 2."),
			new UseCase("Two params", "Value {} is smaller than {}", new Object[]{i1, i2}, 2, "Value 1 is smaller than 2"),
			new UseCase("Two params", "{}{}", new Object[]{i1, i2}, 2, "12"),
			new UseCase("Special One param", "Val1={}, Val2={", new Object[]{i1, i2}, new String[]{"[1, 2]"}, 1, "Val1=[1, 2], Val2={", null),
			new UseCase("Special One param", "Value {} is smaller than \\{}", new Object[]{i1, i2}, new String[]{"[1, 2]"}, 1, "Value [1, 2] is smaller than {}", null),
			new UseCase("Special One param", "Value {} is smaller than \\{} tail", new Object[]{i1, i2}, new String[]{"[1, 2]"}, 1, "Value [1, 2] is smaller than {} tail", null),
			new UseCase("Special One param", "Value {} is smaller than \\{", new Object[]{i1, i2}, new String[]{"[1, 2]"}, 1, "Value [1, 2] is smaller than \\{", null),
			new UseCase("Special One param", "Value {} is smaller than {tail", new Object[]{i1, i2}, new String[]{"[1, 2]"}, 1, "Value [1, 2] is smaller than {tail", null),
			new UseCase("Special One param", "Value \\{} is smaller than {}", new Object[]{i1, i2}, new String[]{"[1, 2]"}, 1, "Value {} is smaller than [1, 2]", null),
			new UseCase("Null Array", "msg0", null, 0, "msg0"),
			new UseCase("Null Array", "msg1 {}", null, 1, "msg1 {}"),
			new UseCase("Null Array", "msg2 {} {}", null, 2, "msg2 {} {}"),
			new UseCase("Null Array", "msg3 {} {} {}", null, 3, "msg3 {} {} {}"),
			new UseCase("Array", "Value {} is smaller than {} and {}.", new Object[]{i1, i2, i3}, 3, "Value 1 is smaller than 2 and 3."),
			new UseCase("Array", "{}{}{}", new Object[]{i1, i2, i3}, 3, "123"),
			new UseCase("Array", "Value {} is smaller than {}.", new Object[]{i1, i2, i3}, 2, "Value 1 is smaller than 2."),
			new UseCase("Array", "Value {} is smaller than {}", new Object[]{i1, i2, i3}, 2, "Value 1 is smaller than 2"),
			new UseCase("Array", "Val={}, {, Val={}", new Object[]{i1, i2, i3}, 2, "Val=1, {, Val=2"),
			new UseCase("Array", "Val={}, \\{, Val={}", new Object[]{i1, i2, i3}, 2, "Val=1, \\{, Val=2"),
			new UseCase("Special One param", "Val1={}, Val2={", new Object[]{i1, i2, i3}, new String[]{"[1, 2, 3]"}, 1, "Val1=[1, 2, 3], Val2={", null),
			new UseCase("Array & Throwable", "Value {} is smaller than {} and {}.", new Object[]{i1, i2, i3, t}, 3, "Value 1 is smaller than 2 and 3.", t),
			new UseCase("Array & Throwable", "{}{}{}", new Object[]{i1, i2, i3, t}, 3, "123", t),
			new UseCase("Array & Throwable", "Value {} is smaller than {}.", new Object[]{i1, i2, i3, t}, 2, "Value 1 is smaller than 2.", t),
			new UseCase("Array & Throwable", "Value {} is smaller than {}", new Object[]{i1, i2, i3, t}, 2, "Value 1 is smaller than 2", t),
			new UseCase("Array & Throwable", "Val={}, {, Val={}", new Object[]{i1, i2, i3, t}, 2, "Val=1, {, Val=2", t),
			new UseCase("Array & Throwable", "Val1={}, Val2={", new Object[]{i1, i2, i3, t}, 1, "Val1=1, Val2={", t),
			new UseCase("ArrayValues", "{}{}", new Object[]{i1, new Integer[]{i2, i3}, t}, 2, "1[2, 3]", t),
			new UseCase("ArrayValues", "{}{}", new Object[]{"a", new Integer[]{i2, i3}, t}, 2, "a[2, 3]", t),
			new UseCase("ArrayValues", "{}{}", new Object[]{"a", new byte[]{1, 2}, t}, 2, "a[1, 2]", t),
			new UseCase("ArrayValues", "{}{}", new Object[]{"a", new int[]{1, 2}, t}, 2, "a[1, 2]", t),
			new UseCase("ArrayValues", "{}{}", new Object[]{"a", new float[]{1, 2}, t}, 2, "a[1.0, 2.0]", t),
			new UseCase("ArrayValues", "{}{}", new Object[]{"a", new double[]{1, 2}, t}, 2, "a[1.0, 2.0]", t), // missing short, long, boolean, char
			new UseCase("MultiDimensionalArrayValues", "{}{}", new Object[]{"a", new Integer[][]{ia0, ia1}, t}, 2, "a[[1, 2, 3], [10, 20, 30]]", t),
			new UseCase("MultiDimensionalArrayValues", "{}{}", new Object[]{"a", new int[][]{{1, 2}, {10, 20}}, t}, 2, "a[[1, 2], [10, 20]]", t),
			new UseCase("MultiDimensionalArrayValues", "{}{}", new Object[]{"a", new float[][]{{1, 2}, {10, 20}}, t}, 2, "a[[1.0, 2.0], [10.0, 20.0]]", t),
			new UseCase("MultiDimensionalArrayValues", "{}{}", new Object[]{"a", new double[][]{{1, 2}, {10, 20}}, t}, 2, "a[[1.0, 2.0], [10.0, 20.0]]", t),
			new UseCase("MultiDimensionalArrayValues", "{}{}", new Object[]{"a", multiOA, t}, 2, "a[[1, 2, 3], [10, 20, 30]]", t),
			new UseCase("MultiDimensionalArrayValues", "{}{}", new Object[]{"a", _3DOA, t}, 2, "a[[[1, 2, 3], [10, 20, 30]], [[1, 2, 3], [10, 20, 30]]]", t),
			new UseCase("CyclicArrays", "{}", new Object[]{cyclicA, t}, new String[]{"[" + cyclicARec + "]"}, 1, "[" + cyclicARec + "]", t),
			new UseCase("CyclicArrays", "{}{}", cyclicB, new String[]{"1", "[2, [3, [1, " + cyclicBRec + "]]]"}, 2, "1[2, [3, [1, " + cyclicBRec + "]]]", null),
			new UseCase("CyclicArrays", "{}{}", cyclicC, new String[]{"1", "[2, [3, [1, " + cyclicCRec + ", FooThrowable]]]"}, 2, "1[2, [3, [1, " + cyclicCRec + ", FooThrowable]]]", t),
			new UseCase("CyclicArrays", "{}{}{}", cyclicC, new String[]{"1", "[2, [3, [1, " + cyclicCRec + ", FooThrowable]]]", "FooThrowable"}, 3, "1[2, [3, [1, " + cyclicCRec + ", FooThrowable]]]FooThrowable", null),
			new UseCase("Array & Used Throwable", "Value {} is smaller than {} and {}. Also: {}!", new Object[]{i1, i2, i3, t}, 4, "Value 1 is smaller than 2 and 3. Also: " + t.toString() + "!"),
			new UseCase("Array & Used Throwable", "{}{}{}{}", new Object[]{i1, i2, i3, t}, 4, "123" + t.toString()),
			new UseCase("Escaping", "Value {} is smaller than \\\\{}", new Object[]{i1, i2, i3, t}, 2, "Value 1 is smaller than \\2", t),
			new UseCase("Escaping", "Value {} is smaller than \\\\{} tail", new Object[]{i1, i2, i3, t}, 2, "Value 1 is smaller than \\2 tail", t),
			new UseCase("Escaping", "Value {} is smaller than \\\\{", new Object[]{i1, i2, i3, t}, 1, "Value 1 is smaller than \\\\{", t),
			new UseCase("Escaping", "Value {} is smaller than \\\\{tail", new Object[]{i1, i2, i3, t}, 1, "Value 1 is smaller than \\\\{tail", t),
			new UseCase("Escaping", "Value \\\\{} is smaller than {}", new Object[]{i1, i2, i3, t}, 2, "Value \\1 is smaller than 2", t),
			new UseCase("Escaping", "\\\\{}", new Object[]{i1, i2, i3, t}, 1, "\\1", t),
			new UseCase("Escaping", "\\\\\\{}", new Object[]{i1, i2, i3, t}, 0, "\\{}", t),
			new UseCase("Escaping", "\\\\\\\\{}", new Object[]{i1, i2, i3, t}, 1, "\\\\1", t),
			new UseCase("Escaping", "\\{}", new Object[]{i1, i2, i3, t}, 0, "{}", t),
			new UseCase("ArrayValues", "{}{}", new Object[]{i1, p1}, 2, i1 + Arrays.toString(p1)),
			new UseCase("ArrayValues", "{}{}", new Object[]{"a", p1}, 2, "a" + Arrays.toString(p1)),
			new UseCase("ArrayValues", "{}{}", new Object[]{"a", new byte[]{1, 2}}, 2, "a" + Arrays.toString(new byte[]{1, 2})),
			new UseCase("ArrayValues", "{}{}", new Object[]{"a", new short[]{1, 2}}, 2, "a" + Arrays.toString(new short[]{1, 2})),
			new UseCase("ArrayValues", "{}{}", new Object[]{"a", new int[]{1, 2}}, 2, "a" + Arrays.toString(new int[]{1, 2})),
			new UseCase("ArrayValues", "{}{}", new Object[]{"a", new long[]{1, 2}}, 2, "a" + Arrays.toString(new long[]{1, 2})),
			new UseCase("ArrayValues", "{}{}", new Object[]{"a", new float[]{1, 2}}, 2, "a" + Arrays.toString(new float[]{1, 2})),
			new UseCase("ArrayValues", "{}{}", new Object[]{"a", new double[]{1, 2}}, 2, "a" + Arrays.toString(new double[]{1, 2})),
			new UseCase("ArrayValues", "{}{}", new Object[]{"a", new boolean[]{true, false}}, 2, "a" + Arrays.toString(new boolean[]{true, false})),
			new UseCase("ArrayValues", "{}{}", new Object[]{"a", new char[]{'b', 'c'}}, 2, "a" + Arrays.toString(new char[]{'b', 'c'})),
			new UseCase("ArrayValues", "{}{}", multiArray, new String[]{"[" + multiArrayRec + ", " + Arrays.toString(p1) + "]", Arrays.toString(p1)}, 2, "[" + multiArrayRec + ", " + Arrays.toString(p1) + "]" + Arrays.toString(p1), null),
			new UseCase("SpecialOneArgument", "Special {}", new Object[]{"One", "Two", "Three"}, new String[]{"[One, Two, Three]"}, 1, "Special "+Arrays.toString(new Object[]{"One", "Two", "Three"}), null),
		};
	}

	@Test
	public void simpleCountPlaceholders()
	{
		validateCountArgumentPlaceholders(null, 0);
		validateCountArgumentPlaceholders("foo", 0);
		validateCountArgumentPlaceholders("{}", 1);
		validateCountArgumentPlaceholders("{} {} {}", 3);
	}

	@Test
	public void brokenCountPlaceholders()
	{
		validateCountArgumentPlaceholders("{", 0);
		validateCountArgumentPlaceholders("{} { {}", 2);
		validateCountArgumentPlaceholders("{} {", 1);
	}

	@Test
	public void escapedCountPlaceholders()
	{
		validateCountArgumentPlaceholders("\\{}", 0);
		validateCountArgumentPlaceholders("\\\\{}", 1);
		validateCountArgumentPlaceholders("\\\\\\{}", 0);

		validateCountArgumentPlaceholders("{} \\{}", 1);
		validateCountArgumentPlaceholders("{} \\\\{}", 2);
		validateCountArgumentPlaceholders("{} \\\\\\{}", 1);
	}


	@Test
	public void useCasesCountArguments()
	{
		for(int i = 0; i < useCases.length; i++)
		{
			UseCase useCase = useCases[i];
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
		FooThrowable t = new FooThrowable("FooException");
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

	@Test
	public void useCasesValidateEvaluateArguments()
	{
		for(int i = 0; i < useCases.length; i++)
		{
			UseCase useCase = useCases[i];
			if(logger.isDebugEnabled()) logger.debug("Validating evaluateArguments for [{}]: {}...", i, useCase);


			String[] argStrings = useCase.getArgumentStrings();
			MessageFormatter.ArgumentResult expectedResult = null;
			if(argStrings != null)
			{
				//noinspection ThrowableResultOfMethodCallIgnored
				expectedResult = new MessageFormatter.ArgumentResult(argStrings, useCase.getThrowable());
			}
			validateEvaluateArguments(useCase.getMessagePattern(), useCase.getArguments(), expectedResult);
		}
	}

	@Test
	public void useCasesValidateFormatMessage()
	{
		for(int i = 0; i < useCases.length; i++)
		{
			UseCase useCase = useCases[i];
			if(logger.isDebugEnabled()) logger.debug("Validating format for [{}]: {}...", i, useCase);

			validateFormatMessage(useCase);
		}
	}

	@SuppressWarnings({"unchecked"})
	@Test(expected = StackOverflowError.class)
	public void showMapRecursionProblem()
	{
		Map a = new HashMap();
		Map b = new HashMap();
		b.put("bar", a);
		a.put("foo", b);
		// the following line will throw an java.lang.StackOverflowError!
		a.toString();
	}

	@SuppressWarnings({"unchecked"})
	@Test(expected = java.lang.StackOverflowError.class)
	public void showCollectionRecursionProblem()
	{
		List a = new ArrayList();
		List b = new ArrayList();
		b.add(a);
		a.add(b);
		// the following line will throw an java.lang.StackOverflowError!
		a.toString();
	}

	@Test
	public void deepToString()
	{
		String result;
		String expected;
		Object o;

		{
			List<String> list = new ArrayList<String>();
			list.add("One");
			list.add("Two");
			Map<String, List<String>> map = new TreeMap<String, List<String>>();
			map.put("foo", list);
			map.put("bar", list);
			o = map;
			expected = "{bar=[One, Two], foo=[One, Two]}";
		}
		if(logger.isInfoEnabled()) logger.info("Evaluating {}...", o);
		result = MessageFormatter.deepToString(o);
		if(logger.isInfoEnabled()) logger.info("Result of {} is {}.", o, result);
		assertEquals(expected, result);

		{
			String[] array = new String[]{"One", "Two"};
			Map<String, String[]> map = new TreeMap<String, String[]>();
			map.put("foo", array);
			map.put("bar", array);
			o = map;
			expected = "{bar=[One, Two], foo=[One, Two]}";
		}
		if(logger.isInfoEnabled()) logger.info("Evaluating {}...", o);
		result = MessageFormatter.deepToString(o);
		if(logger.isInfoEnabled()) logger.info("Result of {} is {}.", o, result);
		assertEquals(expected, result);


		{
			List<String> list = new ArrayList<String>();
			list.add("One");
			list.add("Two");
			List<List<String>> outer = new ArrayList<List<String>>();
			outer.add(list);
			outer.add(list);
			o = outer;
			expected = "[[One, Two], [One, Two]]";
		}
		if(logger.isInfoEnabled()) logger.info("Evaluating {}...", o);
		result = MessageFormatter.deepToString(o);
		if(logger.isInfoEnabled()) logger.info("Result of {} is {}.", o, result);
		assertEquals(expected, result);

		{
			String[] array = new String[]{"One", "Two"};
			List<String[]> map = new ArrayList<String[]>();
			map.add(array);
			map.add(array);
			o = map;
			expected = "[[One, Two], [One, Two]]";
		}
		if(logger.isInfoEnabled()) logger.info("Evaluating {}...", o);
		result = MessageFormatter.deepToString(o);
		if(logger.isInfoEnabled()) logger.info("Result of {} is {}.", o, result);
		assertEquals(expected, result);
	}

	@SuppressWarnings({"unchecked"})
	@Test
	public void deepToStringSpecial()
	{
		String result;
		String expected;
		Object o;

		o = null;
		expected = null;
		result = MessageFormatter.deepToString(o);
		assertEquals(expected, result);

		o = new ProblematicToString();
		expected = MessageFormatter.ERROR_PREFIX + MessageFormatter.identityToString(o)
			+ MessageFormatter.ERROR_SEPARATOR + FooThrowable.class.getName()
			+ MessageFormatter.ERROR_MSG_SEPARATOR
			+ "FooThrowable"
			+ MessageFormatter.ERROR_SUFFIX;
		result = MessageFormatter.deepToString(o);
		if(logger.isInfoEnabled()) logger.info("Result is {}.", result);
		assertEquals(expected, result);

		{
			Map a = new HashMap();
			Map b = new HashMap();
			b.put("bar", a);
			a.put("foo", b);
			o = a;
			expected = "{foo={bar=" + MessageFormatter.RECURSION_PREFIX + MessageFormatter
				.identityToString(a) + MessageFormatter.RECURSION_SUFFIX + "}}";
		}
		result = MessageFormatter.deepToString(o);
		if(logger.isInfoEnabled()) logger.info("Result is {}.", result);
		assertEquals(expected, result);

		{
			List a = new ArrayList();
			List b = new ArrayList();
			b.add(a);
			a.add(b);
			o = a;
			expected = "[[" + MessageFormatter.RECURSION_PREFIX + MessageFormatter
				.identityToString(a) + MessageFormatter.RECURSION_SUFFIX + "]]";
		}
		result = MessageFormatter.deepToString(o);
		if(logger.isInfoEnabled()) logger.info("Result is {}.", result);
		assertEquals(expected, result);
	}

	private void validateEvaluateArguments(String messagePattern, Object[] arguments, MessageFormatter.ArgumentResult expected)
	{
		MessageFormatter.ArgumentResult result = MessageFormatter.evaluateArguments(messagePattern, arguments);
		StringBuilder message = new StringBuilder();
		message.append("messagePattern=");
		if(messagePattern != null)
		{
			message.append("'").append(messagePattern).append("'");
		}
		else
		{
			message.append("null");
		}
		message.append(" and arguments=");
		if(arguments != null)
		{
			message.append("[");
			boolean isFirst = true;
			for(Object current : arguments)
			{
				if(!isFirst)
				{
					message.append(", ");
				}
				else
				{
					isFirst = false;
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

	private void validateCountArgumentPlaceholders(String messagePattern, int expected)
	{
		int result = MessageFormatter.countArgumentPlaceholders(messagePattern);

		StringBuilder message = new StringBuilder();
		message.append("messagePattern ");
		if(messagePattern != null)
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

		String[] argumentStrings = useCase.getArgumentStrings();
		String expectedResult = useCase.getExpectedResult();
		validateFormatMessage(messagePattern, argumentStrings, expectedResult);
	}

	private void validateFormatMessage(String messagePattern, String[] argumentStrings, String expectedResult)
	{
		String result = MessageFormatter.format(messagePattern, argumentStrings);

		StringBuilder message = new StringBuilder();
		message.append("messagePattern ");
		if(messagePattern != null)
		{
			message.append("'").append(messagePattern).append("'");
		}
		else
		{
			message.append("null");
		}
		message.append(" with arguments ");
		if(argumentStrings == null)
		{
			message.append("null");
		}
		else
		{
			message.append("[");
			boolean isFirst = true;
			for(String current : argumentStrings)
			{
				if(isFirst)
				{
					isFirst = false;
				}
				else
				{
					message.append(", ");
				}
				if(current == null)
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
		if(expectedResult == null)
		{
			message.append("null");
		}
		else
		{
			message.append("'").append(expectedResult).append("'!");
		}
		assertEquals(message.toString(), expectedResult, result);
	}

	private static class FooThrowable
		extends RuntimeException
	{
		private static final long serialVersionUID = 9140989200041952994L;

		public FooThrowable(String s)
		{
			super(s);
		}

		@Override
		public String toString()
		{
			return "" + getMessage();
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
			this.section = section;
			this.messagePattern = messagePattern;
			this.arguments = arguments;
			this.numberOfPlaceholders = numberOfPlaceholders;
			this.expectedResult = expectedResult;
			this.throwable = throwable;
			String[] argStrings = null;
			if(arguments != null)
			{
				if(throwable != null)
				{
					argStrings = new String[arguments.length - 1];
				}
				else
				{
					argStrings = new String[arguments.length];
				}
				for(int i = 0; i < argStrings.length; i++)
				{
					argStrings[i] = getStringFor(arguments[i]);
				}
			}
			this.argumentStrings = argStrings;
		}

		public UseCase(String section, String messagePattern, Object[] arguments, String[] argumentStrings, int numberOfPlaceholders, String expectedResult, Throwable throwable)
		{
			this.section = section;
			this.messagePattern = messagePattern;
			this.arguments = arguments;
			this.numberOfPlaceholders = numberOfPlaceholders;
			this.expectedResult = expectedResult;
			this.throwable = throwable;
			this.argumentStrings = argumentStrings;
		}

		/**
		 * I can't think of a better way to test this...
		 *
		 * @param o the Object to get a String for
		 * @return the String for the given Object
		 */
		private String getStringFor(Object o)
		{
			String argStr = null;
			if(o != null)
			{
				if(o.getClass().isArray())
				{
					if(o instanceof byte[])
					{
						argStr = Arrays.toString((byte[]) o);
					}
					else if(o instanceof short[])
					{
						argStr = Arrays.toString((short[]) o);
					}
					else if(o instanceof int[])
					{
						argStr = Arrays.toString((int[]) o);
					}
					else if(o instanceof long[])
					{
						argStr = Arrays.toString((long[]) o);
					}
					else if(o instanceof float[])
					{
						argStr = Arrays.toString((float[]) o);
					}
					else if(o instanceof double[])
					{
						argStr = Arrays.toString((double[]) o);
					}
					else if(o instanceof boolean[])
					{
						argStr = Arrays.toString((boolean[]) o);
					}
					else if(o instanceof char[])
					{
						argStr = Arrays.toString((char[]) o);
					}
					else
					{
						argStr = Arrays.deepToString((Object[]) o);
					}
				}
				else if(o instanceof String)
				{
					argStr = (String) o;
				}
				else
				{
					argStr = o.toString();
				}
			}
			return argStr;
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
		 *
		 * @return the section this usecase is part of.
		 */
		public String getSection()
		{
			return section;
		}

		public boolean equals(Object o)
		{
			if(this == o) return true;
			if(o == null || getClass() != o.getClass()) return false;

			UseCase useCase = (UseCase) o;

			if(numberOfPlaceholders != useCase.numberOfPlaceholders) return false;
			// Probably incorrect - comparing Object[] arrays with Arrays.equals
			if(!Arrays.equals(arguments, useCase.arguments)) return false;
			if(expectedResult != null ? !expectedResult.equals(useCase.expectedResult) : useCase.expectedResult != null)
			{
				return false;
			}
			if(messagePattern != null ? !messagePattern.equals(useCase.messagePattern) : useCase.messagePattern != null)
			{
				return false;
			}
			if(throwable != null ? !throwable.equals(useCase.throwable) : useCase.throwable != null) return false;

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
			StringBuilder result = new StringBuilder();
			result.append("UseCase[");
			result.append("section=").append(section);
			result.append(", ");
			result.append("messagePattern=");
			if(messagePattern == null)
			{
				result.append("null");
			}
			else
			{
				result.append("'").append(messagePattern).append("'");
			}
			result.append(", ");
			result.append("arguments=");
			if(arguments == null)
			{
				result.append("null");
			}
			else
			{
				result.append("[");
				boolean isFirst = true;
				for(Object argument : arguments)
				{
					if(isFirst)
					{
						isFirst = false;
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
			result.append("argumentStrings=");
			if(argumentStrings == null)
			{
				result.append("null");
			}
			else
			{
				result.append("[");
				boolean isFirst = true;
				for(String argument : argumentStrings)
				{
					if(isFirst)
					{
						isFirst = false;
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
			if(expectedResult == null)
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

	private static class ProblematicToString
	{
		public String toString()
		{
			throw new FooThrowable("FooThrowable");
		}
	}
}
