package org.prot.appserver.runtime.jython;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.python.core.PyCode;
import org.python.util.PythonInterpreter;

public class CodeBuffer
{
	private static final Logger logger = Logger.getLogger(CodeBuffer.class);
	
	private String applicationDirectory;

	private Map<String, PyCode> codes = new HashMap<String, PyCode>();

	public CodeBuffer(String applicationDirectory)
	{
		this.applicationDirectory = applicationDirectory;
	}

	private String readFile(String pythonScript)
	{
		try
		{
			BufferedReader reader = new BufferedReader(new FileReader(new File(applicationDirectory
					+ "/WEB-INF/python/" + pythonScript)));

			String file = "";
			String line = "";
			while ((line = reader.readLine()) != null)
				file += line + "\n";

			return file;

		} catch (Exception e)
		{
			logger.error("Could not load python scriptfile: " + pythonScript, e);
		}

		return null;
	}

	public PyCode loadScript(PythonInterpreter interpreter, String script)
	{
		// Check if compiled script is in the cache
		if (codes.containsKey(script) == false)
		{
			// Read the script-file (if possible)
			String file = readFile(script);
			if (file != null)
			{
				// Compile the file and cache it
				PyCode code = interpreter.compile(file);
				codes.put(script, code);
			}
		}

		return codes.get(script);
	}
}
