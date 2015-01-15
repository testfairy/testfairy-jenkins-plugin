package com.testfairy.uploader.command;

import java.util.ArrayList;

public class VerifyCommand extends ArrayList<String> {

	/**
	 * jarsignerPath + " -verify " + apkFilename
	 * @param jarsignerPath
	 * @param apkFilename
	 */
	public VerifyCommand(String jarsignerPath, String apkFilename) {
		super();
		add(jarsignerPath);
		add("-verify");
		add(apkFilename);
	}
}