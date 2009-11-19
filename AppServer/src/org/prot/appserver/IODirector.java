package org.prot.appserver;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import org.apache.log4j.Logger;

public class IODirector
{
	private static final Logger logger = Logger.getLogger(IODirector.class);

	private static IODirector director;

	private PrintStream stdOut;
	private PrintStream stdErr;

	private PassThroughPrinter ptpOut;
	private PassThroughPrinter ptpErr;

	class PassThroughPrinter extends OutputStream
	{
		private PrintStream destination;
		private boolean enabled = false;

		public PassThroughPrinter(PrintStream destionation)
		{
			this.destination = destionation;
		}

		String buffer = "";  
		
		@Override
		public void write(int b) throws IOException
		{
			if (this.enabled == true)
				destination.write(b);
		}

		public void enable()
		{
			this.enabled = true;
		}
	}

	public IODirector()
	{
		IODirector.director = this;

		this.stdOut = System.out;
		this.stdErr = System.err;

		this.ptpOut = new PassThroughPrinter(this.stdOut);
		this.ptpErr = new PassThroughPrinter(this.stdErr);

		System.setOut(new PrintStream(this.ptpOut));
		System.setErr(new PrintStream(this.ptpErr));
	}

	public static IODirector getInstance()
	{
		if (IODirector.director == null)
			new IODirector();

		return IODirector.director;
	}

	public void enableStd()
	{
		this.ptpOut.enable();
		this.ptpErr.enable();
	}

	public void forcedStdOutPrintln(String out)
	{
		this.stdOut.println(out);
	}
}
