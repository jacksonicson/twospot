package org.prot.appserver;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public class IODirector
{
	private PrintStream stdOut; 
	private PrintStream stdErr; 
	
	private PassThroughPrinter ptpOut;
	private PassThroughPrinter ptpErr;
	
	class PassThroughPrinter extends OutputStream
	{
		private PrintStream destination; 
		
		public PassThroughPrinter(PrintStream destionation)
		{
			this.destination = destionation;
		}
		
		@Override
		public void write(int b) throws IOException
		{
//			destination.write(b); 
		}
	}
	
	public IODirector()
	{
		this.stdOut = System.out; 
		this.stdErr = System.err;
		
		this.ptpOut = new PassThroughPrinter(this.stdOut);
		this.ptpErr = new PassThroughPrinter(this.stdErr);
		
		System.setOut(new PrintStream(this.ptpOut));
		System.setErr(new PrintStream(this.ptpErr));
	}
	
	
}
