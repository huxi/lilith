/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2011 Joern Huxhorn
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
 * Copyright 2007-2011 Joern Huxhorn
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

package de.huxhorn.lilith.tracing;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TracingAspectTest
{
	private final Logger logger = LoggerFactory.getLogger(TracingAspectTest.class);

	public void callExampleService(ExampleServiceIfc exampleServiceIfc)
	{
		/*
		void noArgs();

		String noArgsResult();

		void singleArg(String arg);

		String singleArgResult(String arg);

		String singleArgResultTakingTime(String arg);

		void args(String arg1, String arg2);

		String argsResult(String arg1, String arg2);

		void noArgsThrows();

		String noArgsResultThrows();

		void singleArgThrows(String arg);

		String singleArgResultThrows(String arg);

		void argsThrows(String arg1, String arg2);

		String argsResultThrows(String arg1, String arg2);
		*/
		exampleServiceIfc.noArgs();
		exampleServiceIfc.noArgsResult();
		exampleServiceIfc.singleArg("single");
		exampleServiceIfc.singleArgResult("single");
		exampleServiceIfc.singleArgResultTakingTime("single");
		exampleServiceIfc.args("first", "second");
		exampleServiceIfc.argsWithVarargs("first", "second", "third", "fourth");
		exampleServiceIfc.argsResult("first", "second");
		exampleServiceIfc.methodCallingPrivateMethod();
		exampleServiceIfc.callingOther();

		try
		{
			exampleServiceIfc.noArgsThrows();
		}
		catch (RuntimeException ex)
		{
			// expected
		}

		try
		{
			exampleServiceIfc.noArgsResultThrows();
		}
		catch (RuntimeException ex)
		{
			// expected
		}

		try
		{
			exampleServiceIfc.singleArgThrows("single");
		}
		catch (RuntimeException ex)
		{
			// expected
		}

		try
		{
			exampleServiceIfc.singleArgResultThrows("single");
		}
		catch (RuntimeException ex)
		{
			// expected
		}

		try
		{
			exampleServiceIfc.argsThrows("first", "second");
		}
		catch (RuntimeException ex)
		{
			// expected
		}

		try
		{
			exampleServiceIfc.argsResultThrows("first", "second");
		}
		catch (RuntimeException ex)
		{
			// expected
		}

		if(exampleServiceIfc instanceof ExampleService)
		{
			ExampleService exampleService= (ExampleService) exampleServiceIfc;
			exampleService.notInInterface();
		}
	}

	@Test
	public void defaultTracing()
	{
		ApplicationContext context
				= new ClassPathXmlApplicationContext(new String[]{"defaultTracing.xml"});

		TracingAspect tracingAspect = (TracingAspect) context.getBean("tracingAspect");
		if(logger.isInfoEnabled()) logger.info("Using tracingAspect {}", tracingAspect);

		ExampleServiceIfc exampleService = (ExampleServiceIfc) context.getBean("exampleService");
		callExampleService(exampleService);
	}

	@Test
	public void customName()
	{
		ApplicationContext context
				= new ClassPathXmlApplicationContext(new String[]{"showingParameterValuesFalse.xml"});

		TracingAspect tracingAspect = (TracingAspect) context.getBean("tracingAspect");
		if(logger.isInfoEnabled()) logger.info("Using tracingAspect {}", tracingAspect);

		ExampleServiceIfc exampleService = (ExampleServiceIfc) context.getBean("exampleService");
		callExampleService(exampleService);
	}

	@Test
	public void defaultTracingClass()
	{
		ApplicationContext context
				= new ClassPathXmlApplicationContext(new String[]{"defaultTracingClass.xml"});

		TracingAspect tracingAspect = (TracingAspect) context.getBean("tracingAspect");
		if(logger.isInfoEnabled()) logger.info("Using tracingAspect {}", tracingAspect);

		ExampleServiceIfc exampleService = (ExampleServiceIfc) context.getBean("exampleService");
		callExampleService(exampleService);
	}

	@Test
	public void statisticTracing()
	{
		ApplicationContext context
				= new ClassPathXmlApplicationContext(new String[]{"statisticTracing.xml"});

		TracingAspect tracingAspect = (TracingAspect) context.getBean("tracingAspect");
		if(logger.isInfoEnabled()) logger.info("Using tracingAspect {}", tracingAspect);

		ExampleServiceIfc exampleService = (ExampleServiceIfc) context.getBean("exampleService");
		for(int i=0;i<3;i++)
		{
			callExampleService(exampleService);
		}
	}

}
