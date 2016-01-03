package com.testfairy.apk;

import java.io.InputStream;

public interface ApkArchiveEntry {
	public String getFilename();

	public long getSize();

	public long getCompressedSize();

	public int getMethod();

	public long getCrc();

	public InputStream getInputStream();
}
