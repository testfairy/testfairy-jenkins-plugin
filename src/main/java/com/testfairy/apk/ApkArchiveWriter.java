package com.testfairy.apk;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;

public class ApkArchiveWriter extends ApkArchiveVisitor {


	private OutputStream outputStream;
	private ZipArchiveOutputStream zaos;
	private Set<String> includedFiles = new HashSet<String>();

	public ApkArchiveWriter(OutputStream outputStream) {
		super();
		this.outputStream = outputStream;

		zaos = new ZipArchiveOutputStream(outputStream);

	}

	public ApkArchiveWriter(ApkArchiveVisitor av) {
		super(av);
	}

	public void writeTo(OutputStream outputStream) {
		this.outputStream = outputStream;

		zaos = new ZipArchiveOutputStream(outputStream);
	}

	@Override
	public void visit() {
		super.visit();
	}

	@Override
	public void visitEntry(ApkArchiveEntry entry) throws IOException {
		super.visitEntry(entry);

		if (includedFiles.contains(entry.getFilename())) {
			return;
		}

		ZipArchiveEntry zipEntry = new ZipArchiveEntry(entry.getFilename());
		includedFiles.add(entry.getFilename());

		if (entry.getMethod() == ZipEntry.STORED) {
			zipEntry.setSize(entry.getSize());
			zipEntry.setCrc(entry.getCrc());
		}

		zaos.setMethod(entry.getMethod());

		zaos.putArchiveEntry(zipEntry);
		IOUtils.copy(entry.getInputStream(), zaos);

		zaos.closeArchiveEntry();
	}

	@Override
	public void visitEnd() throws IOException {
		super.visitEnd();

		zaos.close();
		includedFiles.clear();
	}
}
