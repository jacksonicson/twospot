package org.prot.controller.manager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ProcessManager {

	private Process process;
	private AppInfo info;

	private StreamThread errThr, outThr; 
	
	public ProcessManager(Process process, AppInfo info) {
		this.process = process;
		this.info = info;

		listen();
	}

	public void listen() {

		InputStream errStream = process.getErrorStream();
		InputStream outStream = process.getInputStream();
		
		errThr = new StreamThread(errStream, info.getErrStream()); 
		outThr = new StreamThread(outStream, info.getOutStream());
		
		errThr.start(); 
		outThr.start(); 
	}

	class StreamThread extends Thread {

		private InputStream inStream;
		private OutputStream outStream;

		private final boolean pipe = true; 
		
		public StreamThread(InputStream inStream, OutputStream outStream) {
			this.inStream = inStream;
			this.outStream = outStream;
		}

		public void run() {
			try {

				int len = 0;
				byte[] buffer = new byte[64];

				while ((len = inStream.read(buffer)) > 0) {
					outStream.write(buffer, 0, len);
					
					if(pipe) {
						System.out.print(new String(buffer, 0, len));
					}
				}
				
			} catch (IOException e) {
				e.printStackTrace(); 
			}
		}
	}

}
