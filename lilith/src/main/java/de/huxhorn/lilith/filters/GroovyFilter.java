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
package de.huxhorn.lilith.filters;

import groovy.lang.Binding;
import groovy.lang.GroovyClassLoader;
import groovy.lang.Script;
import de.huxhorn.sulky.conditions.Condition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.ObjectInputStream;
import java.io.IOException;

public class GroovyFilter
	implements Condition
{
	private static final long serialVersionUID = 5218190112764981462L;

	private final Logger logger = LoggerFactory.getLogger(GroovyFilter.class);

	private String scriptFileName;
	//private transient File scriptFile;
	private transient Object instance;
	private transient String scriptName;


	public GroovyFilter()
	{
		this(null);
	}

	public GroovyFilter(String scriptFileName)
	{
		setScriptFileName(scriptFileName);
	}

	public void setScriptFileName(String scriptFileName)
	{
		this.instance=null;
		this.scriptName=null;
		this.scriptFileName=scriptFileName;
		if(scriptFileName!=null)
		{
			File scriptFile = new File(scriptFileName);
			if(!scriptFile.isFile())
			{
				if(logger.isWarnEnabled()) logger.warn("Scriptfile '{}' is not a file!", scriptFile.getAbsolutePath());
			}
			GroovyClassLoader gcl=new GroovyClassLoader();
			gcl.setShouldRecompile(true);
			try
			{
				Class clazz = gcl.parseClass(scriptFile);
				instance=clazz.newInstance();
				this.scriptName=scriptFile.getName();
			}
			catch (Throwable e)
			{
				if(logger.isWarnEnabled()) logger.warn("Exception while instanciating groovy condition '"+scriptFile.getAbsolutePath()+"'!",e);
			}
		}
	}

	public String getScriptFileName()
	{
		return scriptFileName;
	}

	public boolean isTrue(Object o)
	{
		try
		{
			if(instance instanceof Condition)
			{
				Condition condition=(Condition) instance;
				//noinspection unchecked
				return condition.isTrue(o);
			}
			else if(instance instanceof Script)
			{
				Script script=(Script) instance;

				Binding binding=new Binding();
				binding.setVariable("input", o);
				binding.setVariable("logger", logger);

				script.setBinding(binding);
				Object result=script.run();
				return !(result == null || result.equals(Boolean.FALSE));
			}
			else
			{
				return false;
			}
		}
		catch(Throwable t)
		{
			if(logger.isWarnEnabled()) logger.warn("Exception while executing '"+scriptFileName+"'!", t);
			return false;
		}
	}
	private void readObject(ObjectInputStream in)
		throws IOException, ClassNotFoundException
	{
		in.defaultReadObject();
		setScriptFileName(this.scriptFileName);
	}

	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		GroovyFilter that = (GroovyFilter) o;

		return !(scriptFileName != null ? !scriptFileName.equals(that.scriptFileName) : that.scriptFileName != null);
	}

	public int hashCode()
	{
		return (scriptFileName != null ? scriptFileName.hashCode() : 0);
	}

	@Override
	public String toString()
	{
		return "groovy("+scriptName+")";
	}

	public GroovyFilter clone() throws CloneNotSupportedException
	{
		GroovyFilter result = (GroovyFilter) super.clone();
		result.setScriptFileName(result.scriptFileName);
		return result;
	}
}
