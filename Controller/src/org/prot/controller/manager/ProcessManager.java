package org.prot.controller.manager;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class ProcessManager
{

	private Process process;

	private ByteArrayOutputStream outStream = new ByteArrayOutputStream();
	private ByteArrayOutputStream errOutStream = new ByteArrayOutputStream();

	private StreamThread errThr, outThr;

	public void start(Process process)
	{
		this.process = process;

		listen();
	}

	public void stop()
	{
		try
		{
			errThr.end();
			errThr.join();

			outThr.end();
			outThr.join();
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}

	private void listen()
	{

		InputStream errInStream = process.getErrorStream();
		InputStream inStream = process.getInputStream();

		errThr = new StreamThread(errInStream, this.errOutStream);
		outThr = new StreamThread(inStream, this.outStream);

		errThr.start();
		outThr.start();
	}


	private Object lock = new Object();
	private boolean hasSignal = false; 

	public void waitForAppServer()
	{
		synchronized(lock) {
			if(hasSignal)
				return; 
			
			try
			{
				lock.wait();
				hasSignal = false; 
			} catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public void notifyAppServer()
	{
		synchronized(lock) {
			hasSignal = true;
			lock.notify();
		}
	}

	private class StreamThread extends Thread
	{

		private boolean end = false;

		private InputStream inStream;
		private OutputStream outStream;

		private final boolean pipe = true;

		public StreamThread(InputStream inStream, OutputStream outStream)
		{
			this.inStream = inStream;
			this.outStream = outStream;
		}

		public void end()
		{
			this.end = true;
		}

		public void run()
		{
			try
			{
				int len = 0;
				byte[] buffer = new byte[64];
				BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));
				// while ((len = inStream.read(buffer)) > 0 && end == false)
				// {
				String line;
				while ((line = reader.readLine()) != null)
				{
					// outStream.write(buffer, 0, len);
					outStream.write(line.getBytes());

					if (line.equals("server started"))
					{
						notifyAppServer();
					}

					if (pipe)
					{
						System.out.println(line);
					}
				}

			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

}
