package com.testfairy.apk;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;

public class ApkArchiveAddVisitor extends ApkArchiveVisitor {

	private Map<String,byte[]> files = new HashMap<String, byte[]>();

	public ApkArchiveAddVisitor() {
		super();
	}

	public ApkArchiveAddVisitor(ApkArchiveVisitor av) {
		super(av);
	}

	public void addFile(String filename, byte[] data) {
		files.put(filename, data);
	}

	@Override
	public void visitEnd() throws IOException {
		Iterator<Map.Entry<String, byte[]>> it = files.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, byte[]> entry = it.next();

			visitEntry(new VirtualEntry(entry.getKey(), entry.getValue()));
		}

		super.visitEnd();
	}

	private static class VirtualEntry implements ApkArchiveEntry {

		private String name;
		private byte[] data;

		public VirtualEntry(String name, byte[] data) {
			this.name = name;
			this.data = data;
		}

		@Override
		public String getFilename() {
			return name;
		}

		@Override
		public long getSize() {
			return data.length;
		}

		@Override
		public long getCompressedSize() {
			return data.length;
		}

		@Override
		public int getMethod() {
			return ZipEntry.DEFLATED;
		}

		@Override
		public long getCrc() {
			CRC32 crc32 = new CRC32();
			crc32.update(data);
			return crc32.getValue();
		}

		@Override
		public InputStream getInputStream() {
			return new ByteArrayInputStream(data);
		}
	}
}
