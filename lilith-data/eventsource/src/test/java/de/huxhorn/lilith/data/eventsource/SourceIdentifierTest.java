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
package de.huxhorn.lilith.data.eventsource;

import static de.huxhorn.sulky.junit.JUnitTools.testClone;
import static de.huxhorn.sulky.junit.JUnitTools.testSerialization;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class SourceIdentifierTest
{
    @Test
    public void constructorDefault() throws IOException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException
    {
        SourceIdentifier original=new SourceIdentifier();
        testSerialization(original);
        testClone(original);

    }

    @Test
    public void constructorFull() throws IOException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException
    {
        SourceIdentifier original;
        original = new SourceIdentifier("primary", "secondary");
        testSerialization(original);
        testClone(original);

        original = new SourceIdentifier(null, "secondary");
        testSerialization(original);
        testClone(original);

        original = new SourceIdentifier("primary", null);
        testSerialization(original);
        testClone(original);
    }


}