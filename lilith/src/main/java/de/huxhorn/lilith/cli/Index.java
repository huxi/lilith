package de.huxhorn.lilith.cli;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import java.util.ArrayList;
import java.util.List;

@Parameters(commandDescription = "Index the given Lilith file.")
public class Index
{
	public static final String NAME = "index";

	@Parameter(description = "Lilith log files to index.")
	public List<String> files=new ArrayList<>();
}
