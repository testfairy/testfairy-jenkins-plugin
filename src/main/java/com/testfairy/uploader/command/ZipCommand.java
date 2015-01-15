package com.testfairy.uploader.command;

import java.io.File;
import java.util.ArrayList;

public class ZipCommand extends ArrayList<String> {

	/**
	 * zip, -qd, /var/folders/z3/9d_05n9x387brvzh7vzh_rb80000gn/T/instrumented-14210837433664044779768455514126.apk, META-INF/*
	 * @param zipPath
	 * @param apkFilename
	 */
	public ZipCommand(String zipPath, String apkFilename) {
		super();
		add(zipPath);
		add("-qd");
		add(apkFilename);
		add("META-INF"+ File.separator + "*");
	}
}