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
package de.huxhorn.lilith.sender;

public interface SendBytesService
{
	/**
	 * A byte-sender is expected to send the given byte array if possible.
	 * There is no guarantee that the bytes are really sent out, e.g. in case of an error.
	 * There is no feedback of any kind concerning success or failure!
	 *
	 * @param bytes the bytes to send.
	 */
	void sendBytes(byte[] bytes);

	void startUp();

	void shutDown();
}
