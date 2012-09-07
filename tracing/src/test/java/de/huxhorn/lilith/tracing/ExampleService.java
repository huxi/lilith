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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory; 

public class ExampleService implements ExampleServiceIfc, Cloneable
{
	private final Logger logger = LoggerFactory.getLogger(ExampleService.class);

	private String name;
	private OtherIfc other;

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public OtherIfc getOther()
	{
		return other;
	}

	public void setOther(OtherIfc other)
	{
		this.other = other;
	}

	public void noArgs()
	{
		if(logger.isInfoEnabled()) logger.info("Executing noArgs");
	}

	public String noArgsResult()
	{
		if(logger.isInfoEnabled()) logger.info("Executing noArgsResult");
		return "noArgsResult";
	}

	public void singleArg(String arg)
	{
		if(logger.isInfoEnabled()) logger.info("Executing singleArg with "+arg);
	}

	public String singleArgResult(String arg)
	{
		if(logger.isInfoEnabled()) logger.info("Executing singleArgResult with "+arg);
		return "singleArgResult "+arg;
	}

	public String singleArgResultTakingTime(String arg)
	{
		if(logger.isInfoEnabled()) logger.info("Executing singleArgResultTakingTime with "+arg);
		try
		{
			Thread.sleep(2000);
		}
		catch(InterruptedException e)
		{
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}
		return "singleArgResultTakingTime "+arg;
	}

	public void args(String arg1, String arg2)
	{
		if(logger.isInfoEnabled()) logger.info("Executing args with "+arg1+" and "+arg2);
	}

	public void argsWithVarargs(String arg1, String arg2, String... args)
	{
		if(logger.isInfoEnabled()) logger.info("Foo {}", (Object) args);
		if(logger.isInfoEnabled()) logger.info("Executing argsWithVarargs with {}, {} & {}", new Object[]{arg1, arg2, args});
	}

	public String argsResult(String arg1, String arg2)
	{
		if(logger.isInfoEnabled()) logger.info("Executing argsResult with "+arg1+" and "+arg2);

		return "argsResult[arg1="+arg1+", arg2="+arg2+"]";
	}

	public void noArgsThrows()
	{
		throw new RuntimeException("Executing noArgsThrows");
	}

	public String noArgsResultThrows()
	{
		throw new RuntimeException("Executing noArgsThrows");
	}

	public void singleArgThrows(String arg)
	{
		throw new RuntimeException("Executing singleArgThrows with "+arg);
	}

	public String singleArgResultThrows(String arg)
	{
		throw new RuntimeException("Executing singleArgResultThrows with "+arg);
	}

	public void argsThrows(String arg1, String arg2)
	{
		throw new RuntimeException("Executing argsThrows with "+arg1+" and "+arg2);
	}

	public String argsResultThrows(String arg1, String arg2)
	{
		throw new RuntimeException("Executing argsResultThrows with "+arg1+" and "+arg2);
	}

	public String notInInterface()
	{
		if(logger.isInfoEnabled()) logger.info("Executing notInInterface");
		return "notInInterface";
	}

	public String callingOther()
	{
		if(logger.isInfoEnabled()) logger.info("Executing callingOther");
		return other.someMethod();
	}

	public String methodCallingPrivateMethod()
	{
		if(logger.isInfoEnabled()) logger.info("Executing methodCallingPrivateMethod");
		return privateMethod();
	}

	private String privateMethod()
	{
		if(logger.isInfoEnabled()) logger.info("Executing privateMethod");
		return "privateMethod";
	}

	public Object clone() throws CloneNotSupportedException
	{
		return super.clone();
	}
}
