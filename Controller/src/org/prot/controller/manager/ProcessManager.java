package org.prot.controller.manager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ProcessManager {

	private Process process;
	
	private ByteArrayOutputStream outStream = new ByteArrayOutputStream();
	private ByteArrayOutputStream errOutStream = new ByteArrayOutputStream();

	Object lock = new Object();
	private StreamThread errThr, outThr;

	public void start(Process process) {
		this.process = process;

		listen();
	}

	public void stop() {
		try {
			errThr.end();
			errThr.join();

			outThr.end();
			outThr.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void listen() {

		InputStream errInStream = process.getErrorStream();
		InputStream inStream = process.getInputStream();

		errThr = new StreamThread(errInStream, this.errOutStream);
		outThr = new StreamThread(inStream, this.outStream);

		errThr.start();
		outThr.start();
	}

	private class StreamThread extends Thread {

		private boolean end = false;

		private InputStream inStream;
		private OutputStream outStream;

		private final boolean pipe = true;

		public StreamThread(InputStream inStream, OutputStream outStream) {
			this.inStream = inStream;
			this.outStream = outStream;
		}

		public void end() {
			this.end = true;
		}

		public void run() {
			try {

				int len = 0;
				byte[] buffer = new byte[64];

				while ((len = inStream.read(buffer)) > 0 && end == false) {
					outStream.write(buffer, 0, len);

					if (pipe) {
						System.out.print(new String(buffer, 0, len));
					}
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
