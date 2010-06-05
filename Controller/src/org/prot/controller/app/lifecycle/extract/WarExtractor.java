package org.prot.controller.app.lifecycle.extract;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.log4j.Logger;
import org.prot.util.io.Directory;

public class WarExtractor implements AppExtractor {
	private static final Logger logger = Logger.getLogger(WarExtractor.class);

	@Override
	public void extract(byte[] archive, String destPath, String appId) throws IOException {
		String folder = createFolder(destPath);
		decompress(archive, folder);
	}

	private String createFolder(String destPath) throws IOException {
		String folder = destPath;
		logger.debug("Using App-folder: " + folder);

		// Delete folder if it already exists!
		File file = new File(folder);
		if (file.exists()) {
			boolean deleteFolder = false;
			for (int retries = 3; retries > 0; retries--) {
				if (deleteFolder = Directory.deleteFolder(file)) {
					logger.debug("Existing app directory deleted");
					break;
				} else {
					logger.warn("Could not delete existing app directory - retries: " + retries);
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						logger.error("InterruptedException", e);
						System.exit(1);
					}
				}
			}

			if (!deleteFolder) {
				logger.error("Could not delete existing app directory - exiting");
				System.exit(1);
			}
		}

		// Create the folder
		file.mkdir();

		// Return path to the folder
		return folder;
	}

	private void decompress(byte[] warFile, String folder) throws IOException {
		ZipInputStream zipIn = new ZipInputStream(new ByteArrayInputStream(warFile));
		ZipEntry entry;

		logger.debug("Reading ZIP entries");
		int fileCounter = 0;
		while ((entry = zipIn.getNextEntry()) != null) {
			if (entry.isDirectory()) {
				// Ignore directory entries (Directory structure is created for
				// each file)
				// ZIP-Files don't require folde entries!
			} else {
				// Destination file for the ZIP-File
				File dest = new File(folder, entry.getName());

				// Get the parent directory
				File parentDir = dest.getParentFile();

				// Create folder structure to the parent directory
				if (!parentDir.exists())
					parentDir.mkdirs();

				// Extract the file
				fileCounter++;
				BufferedOutputStream fo = new BufferedOutputStream(new FileOutputStream(dest));

				byte buffer[] = new byte[1024 * 1024];
				int len = 0;
				while ((len = zipIn.read(buffer, 0, buffer.length)) != -1)
					fo.write(buffer, 0, len);

				fo.flush();
				fo.close();
			}
		}

		zipIn.close();

		logger.debug("Done with " + fileCounter + " files extracted");
	}
}
