package com.testfairy.uploader.command;

import java.util.ArrayList;

public class ZipAlignCommand extends ArrayList<String> {

	/**
	 * zipAlignPath + " -f 4 " + apkFilename +" "+ apkFilenameZipAlign;
	 * @param zipAlignPath zipAlignPath
	 * @param apkFilename apkFilename
	 * @param apkFilenameZipAlign apkFilenameZipAlign
	 */
	public ZipAlignCommand(String zipAlignPath, String apkFilename, String apkFilenameZipAlign) {
		super();
		add(zipAlignPath);
		add("-f");
		add("4");
		add(apkFilename);
		add(apkFilenameZipAlign);
	}
}