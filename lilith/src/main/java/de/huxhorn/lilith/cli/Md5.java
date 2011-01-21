package de.huxhorn.lilith.cli;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import java.util.ArrayList;
import java.util.List;

@Parameters(commandDescription = "Creates an MD5 checksum files for the given files.")
public class Md5
{
	public static final String NAME = "md5";

	@Parameter(description = "Files to checksum.")
	public List<String> files=new ArrayList<String>();
}
