/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2009 Joern Huxhorn
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
package de.huxhorn.lilith.engine;

/**
 * Defining the constants in one place...
 */
public interface FileConstants
{
	int MAGIC_VALUE = 0x0B5E55ED;

	String FILE_EXTENSION = ".lilith";
	String INDEX_FILE_EXTENSION = ".idx";
	String ACTIVE_FILE_EXTENSION = ".active";

	String IDENTIFIER_KEY = "primaryIdentifier";
	String SECONDARY_IDENTIFIER_KEY = "secondaryIdentifier";

	String CONTENT_TYPE_KEY = "contentType";
	String CONTENT_TYPE_VALUE_LOGGING = "logging";
	String CONTENT_TYPE_VALUE_ACCESS = "access";

	String CONTENT_FORMAT_KEY = "contentFormat";
	String CONTENT_FORMAT_VALUE_PROTOBUF = "protobuf";

	String COMPRESSION_KEY = "compression";
	String COMPRESSION_VALUE_GZIP = "GZIP";
}
