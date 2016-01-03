package com.testfairy.apk;

import java.io.IOException;

public abstract class ApkArchiveVisitor {
	private ApkArchiveVisitor av;

	public ApkArchiveVisitor() {
		av = null;
	}

	public ApkArchiveVisitor(ApkArchiveVisitor av) {
		this.av = av;
	}

	public void visit() {
		if (av != null) {
			av.visit();
		}
	}

	public void visitEntry(ApkArchiveEntry entry) throws IOException {
		if (av != null) {
			av.visitEntry(entry);
		}
	}

	public void visitEnd() throws IOException {
		if (av != null) {
			av.visitEnd();
		}
	}
}
