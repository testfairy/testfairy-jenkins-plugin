package com.testfairy.apk;
import java.io.IOException;
import java.util.ArrayList;

public class ApkArchiveFilterVisitor extends ApkArchiveVisitor {


	private ArrayList<String> removeFiles = new ArrayList<String>();

	public ApkArchiveFilterVisitor() {
		super();
	}

	public ApkArchiveFilterVisitor(ApkArchiveVisitor av) {
		super(av);
	}

	public void addFilter(String str) {
		removeFiles.add(str);
	}

	@Override
	public void visitEntry(ApkArchiveEntry entry) throws IOException {

		boolean accepted = true;

		for (String s: removeFiles) {
			if (s.endsWith("*")) {
				// remove by prefix
				String prefix = s.substring(0, s.length()-1);

				if (entry.getFilename().startsWith(prefix)) {
					// matches prefix
					accepted = false;
					break;
				}
			} else {
				// static filename
				if (entry.getFilename().equals(s)) {
					accepted = false;
					break;
				}
			}
		}

		if (accepted) {
			super.visitEntry(entry);
		}
	}
}
