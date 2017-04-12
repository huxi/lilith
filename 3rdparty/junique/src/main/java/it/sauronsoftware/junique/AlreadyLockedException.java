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
 * This exception is thrown when an ID cannot be locked since it is currently
 * already locked by another JUnique caller.
 * 
 * @author Carlo Pelliccia
 */
public class AlreadyLockedException extends Exception {

	private static final long serialVersionUID = 1L;

	/**
	 * The ID already locked.
	 */
	private String id;

	/**
	 * It builds the exception.
	 * 
	 * @param id
	 *            The ID already locked.
	 */
	AlreadyLockedException(String id) {
		super("Lock for ID \"" + id + "\" has already been taken");
		this.id = id;
	}

	/**
	 * It returns the ID already locked.
	 * 
	 * @return The ID already locked.
	 */
	public String getID() {
		return id;
	}

}
