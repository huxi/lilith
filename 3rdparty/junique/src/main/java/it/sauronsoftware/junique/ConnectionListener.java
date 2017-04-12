/*
 * JUnique - Helps in preventing multiple instances of the same application
 * 
 * Copyright (C) 2008-2010 Carlo Pelliccia (www.sauronsoftware.it)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version
 * 2.1, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License 2.1 for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License version 2.1 along with this program.
 * If not, see <http://www.gnu.org/licenses/>.
 */
package it.sauronsoftware.junique;

/**
 * This interface is used by {@link Connection} class to notify
 * connection related events.
 * 
 * @author Carlo Pelliccia
 */
interface ConnectionListener {

	/**
	 * This method is called when an incoming message is received.
	 * 
	 * @param connection
	 *            The source connection.
	 * @param message
	 *            The message received.
	 * @return An optional response (may be null).
	 */
	public String messageReceived(Connection connection,
			String message);

	/**
	 * This method is called to notify that the connection with the remote side
	 * has been closed.
	 * 
	 * @param connection
	 *            The source connection.
	 */
	public void connectionClosed(Connection connection);

}
