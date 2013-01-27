package de.huxhorn.lilith.tools;

import de.huxhorn.lilith.api.FileConstants;

import java.io.File;

public class FileHelper
{
	public static File resolveDataFile(File baseFile)
	{
		String fileStr = baseFile.getAbsolutePath();

		if(fileStr.toLowerCase().endsWith(FileConstants.FILE_EXTENSION))
		{
			return new File(fileStr);
		}
		if(fileStr.toLowerCase().endsWith(FileConstants.INDEX_FILE_EXTENSION))
		{
			fileStr = fileStr.substring(0, fileStr.length() - FileConstants.INDEX_FILE_EXTENSION.length());
		}
		return new File(fileStr + FileConstants.FILE_EXTENSION);
	}

	public static File resolveIndexFile(File baseFile)
	{
		String fileStr = baseFile.getAbsolutePath();

		if(fileStr.toLowerCase().endsWith(FileConstants.INDEX_FILE_EXTENSION))
		{
			return new File(fileStr);
		}
		if(fileStr.toLowerCase().endsWith(FileConstants.FILE_EXTENSION))
		{
			fileStr = fileStr.substring(0, fileStr.length() - FileConstants.FILE_EXTENSION.length());
		}
		return new File(fileStr + FileConstants.INDEX_FILE_EXTENSION);
	}
}
