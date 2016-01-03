package com.testfairy.apk;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ApkArchiveFileLister extends ApkArchiveVisitor {

	private ArrayList<String> filenames = new ArrayList<String>();

	@Override
	public void visitEntry(ApkArchiveEntry entry) throws IOException {
		super.visitEntry(entry);

		filenames.add(entry.getFilename());
	}

	public List<String> getFilenames() {
		return filenames;
	}
}
