/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2017 Joern Huxhorn
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
 * Copyright 2007-2017 Joern Huxhorn
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

package de.huxhorn.lilith.engine.impl.eventproducer

import spock.lang.Specification

class WhitelistObjectInputStreamSpec extends Specification
{

    def "Blocked dryRunning works as expected."() {
        setup:
        Foo foo = new Foo('bar')
        byte[] bytes = serialize(foo)

        when:
        Set<String> whitelist = []
        ByteArrayInputStream is = new ByteArrayInputStream(bytes)
        WhitelistObjectInputStream instance = new WhitelistObjectInputStream(is, whitelist, false, true)
        Object read = instance.readObject()

        then:
        instance.dryRunning
        read == foo
        instance.unauthorized.contains(Foo.name)
    }

    def "Unblocked dryRunning works as expected."() {
        setup:
        Foo foo = new Foo('bar')
        byte[] bytes = serialize(foo)

        when:
        Set<String> whitelist = [Foo.name]
        ByteArrayInputStream is = new ByteArrayInputStream(bytes)
        WhitelistObjectInputStream instance = new WhitelistObjectInputStream(is, whitelist, false, true)
        Object read = instance.readObject()

        then:
        instance.dryRunning
        read == foo
        !instance.unauthorized.contains(Foo.name)
    }

    def "Blocked works as expected."() {
        setup:
        Foo foo = new Foo('bar')
        byte[] bytes = serialize(foo)

        when:
        Set<String> whitelist = []
        ByteArrayInputStream is = new ByteArrayInputStream(bytes)
        WhitelistObjectInputStream instance = new WhitelistObjectInputStream(is, whitelist)
        instance.readObject()

        then:
        !instance.dryRunning
//        InvalidClassException ex = thrown()
//        ex.message == Foo.name + '; Unauthorized deserialization attempt!'
//        ex.classname == Foo.name
        ClassNotFoundException ex = thrown()
        ex.message == 'Unauthorized deserialization attempt! '+Foo.name
        instance.unauthorized.contains(Foo.name)
    }

    def "Unblocked works as expected."() {
        setup:
        Foo foo = new Foo('bar')
        byte[] bytes = serialize(foo)

        when:
        Set<String> whitelist = [Foo.name]
        ByteArrayInputStream is = new ByteArrayInputStream(bytes)
        WhitelistObjectInputStream instance = new WhitelistObjectInputStream(is, whitelist)
        Object read = instance.readObject()

        then:
        !instance.dryRunning
        read == foo
        !instance.unauthorized.contains(Foo.name)
    }

    def "copySet=true works as expected."() {
        setup:
        Foo foo = new Foo('bar')
        byte[] bytes = serialize(foo)
        Set<String> whitelist = [Foo.name, 'Something']

        when:
        ByteArrayInputStream is = new ByteArrayInputStream(bytes)
        WhitelistObjectInputStream instance = new WhitelistObjectInputStream(is, whitelist, true)
        whitelist.remove('Something')

        then:
        instance.whitelist == [Foo.name, 'Something'] as Set<String>
    }

    def "copySet=false works as expected."() {
        setup:
        Foo foo = new Foo('bar')
        byte[] bytes = serialize(foo)
        Set<String> whitelist = [Foo.name, 'Something']

        when:
        ByteArrayInputStream is = new ByteArrayInputStream(bytes)
        WhitelistObjectInputStream instance = new WhitelistObjectInputStream(is, whitelist, false)
        whitelist.remove('Something')

        then:
        instance.whitelist == [Foo.name] as Set<String>
    }

    static byte[] serialize(Serializable o) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream()
        ObjectOutputStream dos = new ObjectOutputStream(bos)
        dos.writeObject(o)
        dos.close()
        bos.toByteArray()
    }

    private static class Foo implements Serializable
    {
        final String name

        Foo(String name) {
            this.name = name
        }

        boolean equals(o) {
            if (this.is(o)) return true
            if (getClass() != o.class) return false

            Foo foo = (Foo) o

            if (name != foo.name) return false

            return true
        }

        int hashCode() {
            return (name != null ? name.hashCode() : 0)
        }
    }
}
