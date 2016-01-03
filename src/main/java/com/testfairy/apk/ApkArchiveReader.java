package com.testfairy.apk;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ApkArchiveReader {

	private ZipArchiveInputStream zipArchive = null;

	public ApkArchiveReader(String f) throws IOException {
		zipArchive = new ZipArchiveInputStream(new FileInputStream(f));
	}

	private class VirtualEntry implements ApkArchiveEntry {

		private ZipArchiveEntry archiveEntry;

		public VirtualEntry(ZipArchiveEntry archiveEntry) {
			this.archiveEntry = archiveEntry;

			//logger.debug("Entry: " + archiveEntry.getMethod() + " " + archiveEntry.getCompressedSize() + " " + archiveEntry.getSize());
		}

		@Override
		public String getFilename() {
			return archiveEntry.getName();
		}

		@Override
		public long getSize() {
			return archiveEntry.getSize();
		}

		@Override
		public int getMethod() {
			return archiveEntry.getMethod();
		}

		@Override
		public long getCompressedSize() {
			return archiveEntry.getCompressedSize();
		}

		@Override
		public long getCrc() {
			return archiveEntry.getCrc();
		}

		@Override
		public InputStream getInputStream() {
			return (InputStream)zipArchive;
		}
	}

	public void accept(ApkArchiveVisitor listener) throws IOException {

		// initialize
		listener.visit();

		while (true) {
			ZipArchiveEntry entry = zipArchive.getNextZipEntry();
			if (entry == null) {
				break;
			}

			listener.visitEntry(new VirtualEntry(entry));
		}

		// finalize
		listener.visitEnd();
	}
}
