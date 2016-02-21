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

public interface ExampleServiceIfc
{
	String getName();

	void setName(String name);

	void noArgs();

	String noArgsResult();

	void singleArg(String arg);

	String singleArgResult(String arg);

	String singleArgResultTakingTime(String arg);

	void args(String arg1, String arg2);

	void argsWithVarargs(String arg1, String arg2, String... args);

	String argsResult(String arg1, String arg2);

	void noArgsThrows();

	String noArgsResultThrows();

	void singleArgThrows(String arg);

	String singleArgResultThrows(String arg);

	void argsThrows(String arg1, String arg2);

	String argsResultThrows(String arg1, String arg2);

	String methodCallingPrivateMethod();

	String callingOther();
}
