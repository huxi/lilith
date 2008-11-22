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
package de.huxhorn.lilith.swing.table;

public interface AccessEventTableModelConstants
{
	int COLUMN_INDEX_ID = 0;
	int COLUMN_INDEX_TIMESTAMP = 1;
	int COLUMN_INDEX_STATUS_CODE = 2;
	int COLUMN_INDEX_METHOD = 3;
	int COLUMN_INDEX_REQUEST_URI = 4;
	int COLUMN_INDEX_PROTOCOL = 5;
	int COLUMN_INDEX_REMOTE_ADDR = 6;
	int COLUMN_INDEX_APPLICATION = 7;
	int COLUMN_INDEX_SOURCE = 8;

	String DEFAULT_COLUMN_NAME_ID = "ID";
	String DEFAULT_COLUMN_NAME_TIMESTAMP = "Timestamp";
	String DEFAULT_COLUMN_NAME_STATUS_CODE = "Status";
	String DEFAULT_COLUMN_NAME_METHOD = "Method";
	String DEFAULT_COLUMN_NAME_REQUEST_URI = "Request URI";
	String DEFAULT_COLUMN_NAME_PROTOCOL = "Protocol";
	String DEFAULT_COLUMN_NAME_REMOTE_ADDR = "Remote Address";
	String DEFAULT_COLUMN_NAME_APPLICATIION = "Application";
	String DEFAULT_COLUMN_NAME_SOURCE = "Source";
}
