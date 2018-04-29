/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2018 Joern Huxhorn
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

/*
 * Copyright 2007-2018 Joern Huxhorn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.huxhorn.lilith.data.logging;

import de.huxhorn.sulky.formatting.SafeString;
import java.util.Arrays;

@SuppressWarnings({"PMD.ArrayIsStoredDirectly", "PMD.MethodReturnsInternalArray"})
public final class MessageFormatterUseCases
{
	private MessageFormatterUseCases() {}

	public static UseCase[] generateUseCases()
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
		String multiArrayRecId = SafeString.identityToString(multiArrayRecursive);
		String multiArrayRec = SafeString.RECURSION_PREFIX + multiArrayRecId + SafeString.RECURSION_SUFFIX;

		Integer[] ia0 = new Integer[]{i1, i2, i3};
		Integer[] ia1 = new Integer[]{10, 20, 30};

		Object[][] multiOA = new Object[][]{ia0, ia1};
		Object[][][] multiOATwice = new Object[][][]{multiOA, multiOA};

		Object[] cyclicA = new Object[1];
		cyclicA[0] = cyclicA;

		String cyclicAId = SafeString.identityToString(cyclicA);

		Object[] recArray;
		Object[] cyclicB = new Object[2];
		{
			cyclicB[0] = i1;
			Object[] c = new Object[]{i3, cyclicB};
			Object[] b = new Object[]{i2, c};
			recArray = b;
			cyclicB[1] = b;
		}
		String cyclicBRecId = SafeString.identityToString(recArray);

		Object[] cyclicC = new Object[3];
		{
			cyclicC[0] = i1;
			Object[] c = new Object[]{i3, cyclicC};
			Object[] b = new Object[]{i2, c};
			recArray = b;
			cyclicC[1] = b;
			cyclicC[2] = t;
		}
		String cyclicCRecId = SafeString.identityToString(recArray);

		String cyclicARec = SafeString.RECURSION_PREFIX + cyclicAId + SafeString.RECURSION_SUFFIX;
		String cyclicBRec = SafeString.RECURSION_PREFIX + cyclicBRecId + SafeString.RECURSION_SUFFIX;
		String cyclicCRec = SafeString.RECURSION_PREFIX + cyclicCRecId + SafeString.RECURSION_SUFFIX;

		return new UseCase[]{
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
				new UseCase("ArrayValues", "{}{}", new Object[]{"a", new byte[]{1, 2}, t}, new String[]{"a", "[0x01, 0x02]"}, 2, "a[0x01, 0x02]", t),
				new UseCase("ArrayValues", "{}{}", new Object[]{"a", new int[]{1, 2}, t}, 2, "a[1, 2]", t),
				new UseCase("ArrayValues", "{}{}", new Object[]{"a", new float[]{1, 2}, t}, 2, "a[1.0, 2.0]", t),
				new UseCase("ArrayValues", "{}{}", new Object[]{"a", new double[]{1, 2}, t}, 2, "a[1.0, 2.0]", t), // missing short, long, boolean, char
				new UseCase("MultiDimensionalArrayValues", "{}{}", new Object[]{"a", new Integer[][]{ia0, ia1}, t}, 2, "a[[1, 2, 3], [10, 20, 30]]", t),
				new UseCase("MultiDimensionalArrayValues", "{}{}", new Object[]{"a", new int[][]{{1, 2}, {10, 20}}, t}, 2, "a[[1, 2], [10, 20]]", t),
				new UseCase("MultiDimensionalArrayValues", "{}{}", new Object[]{"a", new float[][]{{1, 2}, {10, 20}}, t}, 2, "a[[1.0, 2.0], [10.0, 20.0]]", t),
				new UseCase("MultiDimensionalArrayValues", "{}{}", new Object[]{"a", new double[][]{{1, 2}, {10, 20}}, t}, 2, "a[[1.0, 2.0], [10.0, 20.0]]", t),
				new UseCase("MultiDimensionalArrayValues", "{}{}", new Object[]{"a", multiOA, t}, 2, "a[[1, 2, 3], [10, 20, 30]]", t),
				new UseCase("MultiDimensionalArrayValues", "{}{}", new Object[]{"a", multiOATwice, t}, 2, "a[[[1, 2, 3], [10, 20, 30]], [[1, 2, 3], [10, 20, 30]]]", t),
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
				new UseCase("ArrayValues", "{}{}", new Object[]{"a", new byte[]{1, 2}}, new String[]{"a", "[0x01, 0x02]"}, 2, "a[0x01, 0x02]", null),
				new UseCase("ArrayValues", "{}{}", new Object[]{"a", new short[]{1, 2}}, 2, "a" + Arrays.toString(new short[]{1, 2})),
				new UseCase("ArrayValues", "{}{}", new Object[]{"a", new int[]{1, 2}}, 2, "a" + Arrays.toString(new int[]{1, 2})),
				new UseCase("ArrayValues", "{}{}", new Object[]{"a", new long[]{1, 2}}, 2, "a" + Arrays.toString(new long[]{1, 2})),
				new UseCase("ArrayValues", "{}{}", new Object[]{"a", new float[]{1, 2}}, 2, "a" + Arrays.toString(new float[]{1, 2})),
				new UseCase("ArrayValues", "{}{}", new Object[]{"a", new double[]{1, 2}}, 2, "a" + Arrays.toString(new double[]{1, 2})),
				new UseCase("ArrayValues", "{}{}", new Object[]{"a", new boolean[]{true, false}}, 2, "a" + Arrays.toString(new boolean[]{true, false})),
				new UseCase("ArrayValues", "{}{}", new Object[]{"a", new char[]{'b', 'c'}}, 2, "a" + Arrays.toString(new char[]{'b', 'c'})),
				new UseCase("ArrayValues", "{}{}", multiArray, new String[]{"[" + multiArrayRec + ", " + Arrays.toString(p1) + "]", Arrays.toString(p1)}, 2, "[" + multiArrayRec + ", " + Arrays.toString(p1) + "]" + Arrays.toString(p1), null),
				new UseCase("SpecialOneArgument", "Special {}", new Object[]{"One", "Two", "Three"}, new String[]{"['One', 'Two', 'Three']"}, 1, "Special ['One', 'Two', 'Three']", null),
		};
	}

	public static class UseCase
	{
		private final String messagePattern;
		private final Object[] arguments;
		private final int numberOfPlaceholders;
		private final String expectedResult;
		private final Throwable throwable;
		private final String[] argumentStrings;
		private final String section;

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
			if (arguments != null)
			{
				if (throwable != null)
				{
					argStrings = new String[arguments.length - 1];
				}
				else
				{
					argStrings = new String[arguments.length];
				}
				for (int i = 0; i < argStrings.length; i++)
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
			if (o == null)
			{
				return "null";
			}
			if (o instanceof String)
			{
				return (String) o;
			}
			if (o.getClass().isArray())
			{
				if (o instanceof short[])
				{
					return Arrays.toString((short[]) o);
				}
				if (o instanceof int[])
				{
					return Arrays.toString((int[]) o);
				}
				if (o instanceof long[])
				{
					return Arrays.toString((long[]) o);
				}
				if (o instanceof float[])
				{
					return Arrays.toString((float[]) o);
				}
				if (o instanceof double[])
				{
					return Arrays.toString((double[]) o);
				}
				else if (o instanceof boolean[])
				{
					return Arrays.toString((boolean[]) o);
				}
				else if (o instanceof char[])
				{
					return Arrays.toString((char[]) o);
				}
				else
				{
					return Arrays.deepToString((Object[]) o);
				}
			}
			return o.toString();
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

		public MessageFormatter.ArgumentResult getArgumentResult()
		{
			MessageFormatter.ArgumentResult result = null;
			if(argumentStrings != null)
			{
				result = new MessageFormatter.ArgumentResult(argumentStrings, throwable);
			}
			return result;
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

		@Override
		public boolean equals(Object o)
		{
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			UseCase useCase = (UseCase) o;

			return numberOfPlaceholders == useCase.numberOfPlaceholders
					&& Arrays.equals(arguments, useCase.arguments)
					&& (expectedResult != null ? expectedResult.equals(useCase.expectedResult) : useCase.expectedResult == null)
					&& (messagePattern != null ? messagePattern.equals(useCase.messagePattern) : useCase.messagePattern == null)
					&& (throwable != null ? throwable.equals(useCase.throwable) : useCase.throwable == null);
		}

		@Override
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
			StringBuilder result = new StringBuilder(500);
			result.append("UseCase[section=").append(section)
					.append(", messagePattern=");
			if (messagePattern == null)
			{
				result.append("null");
			}
			else
			{
				result.append('\'').append(messagePattern).append('\'');
			}
			result.append(", arguments=");
			if (arguments == null)
			{
				result.append("null");
			}
			else
			{
				result.append('[');
				boolean isFirst = true;
				for (Object argument : arguments)
				{
					if (isFirst)
					{
						isFirst = false;
					}
					else
					{
						result.append(", ");
					}
					result.append(argument);
				}
				result.append(']');
			}
			result.append(", argumentStrings=");
			if (argumentStrings == null)
			{
				result.append("null");
			}
			else
			{
				result.append('[');
				boolean isFirst = true;
				for (String argument : argumentStrings)
				{
					if (isFirst)
					{
						isFirst = false;
					}
					else
					{
						result.append(", ");
					}
					result.append(argument);
				}
				result.append(']');
			}
			result.append(", numberOfPlaceholders=").append(numberOfPlaceholders)

					.append(", expectedResult=");
			if (expectedResult == null)
			{
				result.append("null");
			}
			else
			{
				result.append('\'').append(expectedResult).append('\'');
			}

			result.append(", throwable=").append(throwable).append(']');

			return result.toString();
		}
	}

	public static class FooThrowable
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
			return getMessage();
		}
	}


}
