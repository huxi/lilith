/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2015 Joern Huxhorn
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
 * Copyright 2007-2015 Joern Huxhorn
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

package de.huxhorn.lilith.engine.impl.eventproducer;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of ObjectInputStream that only allows classes given during creation.
 *
 * See
 * - https://blogs.apache.org/foundation/entry/apache_commons_statement_to_widespread
 * - http://www.ibm.com/developerworks/library/se-lookahead/
 * - http://frohoff.github.io/appseccali-marshalling-pickles/
 *   - https://www.youtube.com/watch?v=KSA7vUkXGSg
 */
public class WhitelistObjectInputStream
	extends ObjectInputStream
{
	private final Logger logger = LoggerFactory.getLogger(WhitelistObjectInputStream.class);

	private final Set<String> whitelist;
	private final Set<String> unauthorized;
	private final boolean dryRunning;

	/**
	 * Creates a WhitelistObjectInputStream with copyMap = false and dryRunning = false.
	 *
	 * @param in the InputStream.
	 * @param whitelist whitelist of classes that may be deserialized.
	 * @throws IOException if an I/O error occurs while reading stream header
	 */
	public WhitelistObjectInputStream(InputStream in, Set<String> whitelist)
			throws IOException
	{
		this(in, whitelist, false, false);
	}

	/**
	 * Creates a WhitelistObjectInputStream with dryRunning = false.
	 *
	 * @param in the InputStream.
	 * @param whitelist whitelist of classes that may be deserialized.
	 * @param copySet whether or not the given whitelist should be copied defensively.
	 * @throws IOException if an I/O error occurs while reading stream header
	 */
	public WhitelistObjectInputStream(InputStream in, Set<String> whitelist, boolean copySet)
			throws IOException
	{
		this(in, whitelist, copySet, false);
	}

	/**
	 *
	 *
	 * @param in the InputStream.
	 * @param whitelist whitelist of classes that may be deserialized.
	 * @param copySet whether or not the given whitelist should be copied defensively.
	 * @param dryRunning if true, only warnings are logged but classes are serialized anyway.
	 * @throws IOException if an I/O error occurs while reading stream header
	 */
	public WhitelistObjectInputStream(InputStream in, Set<String> whitelist, boolean copySet, boolean dryRunning)
		throws IOException
	{
		super(in);
		this.dryRunning = dryRunning;
		Objects.requireNonNull(whitelist, "whitelist must not be null!");
		// Won't prevent empty whitelist since it makes sense in case of dryRun.
		// if(whitelist.isEmpty())
		// {
		// 	throw new IllegalArgumentException("whitelist must not be empty!");
		// }
		if(copySet)
		{
			this.whitelist = new HashSet<>(whitelist);
		}
		else
		{
			this.whitelist = whitelist;
		}
		this.unauthorized = new HashSet<>();
	}

	/**
	 * Only deserialize instances of our classes contained in whitelist.
	 */
	@Override
	protected Class<?> resolveClass(ObjectStreamClass desc)
		throws IOException, ClassNotFoundException
	{
		String className = desc.getName();
		if (!whitelist.contains(className))
		{
			if(!unauthorized.contains(className))
			{
				// show warning only once
				if (logger.isWarnEnabled()) logger.warn("Unauthorized deserialization attempt! {}", className);
				unauthorized.add(className);
			}
			if(!dryRunning)
			{
				throw new ClassNotFoundException("Unauthorized deserialization attempt! "+className);
				//throw new InvalidClassException(className, "Unauthorized deserialization attempt!");
			}
		}
		return super.resolveClass(desc);
	}

	public Set<String> getUnauthorized()
	{
		return Collections.unmodifiableSet(unauthorized);
	}

	public boolean isDryRunning()
	{
		return dryRunning;
	}

	public Set<String> getWhitelist()
	{
		return Collections.unmodifiableSet(whitelist);
	}

	@Override
	public String toString() {
		return "WhitelistObjectInputStream{" +
				"whitelist=" + whitelist +
				", dryRunning=" + dryRunning +
				'}';
	}
}
