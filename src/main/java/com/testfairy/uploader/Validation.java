package com.testfairy.uploader;

import hudson.util.FormValidation;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gilt on 1/7/15.
 */
public class Validation {


	public static FormValidation checkApk(String jarsignerPath, String apkPath) {
		if (apkPath.length() == 0)
			return FormValidation.error("Please set a Path");
		if (!Validation.isValidAPK(jarsignerPath, apkPath))
			return FormValidation.warning(apkPath + " is invalid. Please make sure that this file exists.");
		return FormValidation.ok();
	}

	public static boolean isValidAPK(String jarsignerPath, String apkPath) {
//		String toVerify = jarsignerPath + " -verify " + apkPath;
//		System.out.println(toVerify);
		ArrayList<String> toVerify = new ArrayList<String>();
		toVerify.add(jarsignerPath);
		toVerify.add("-verify");
		toVerify.add(apkPath);

		String out = exec(toVerify);
		if (out != null && out.contains("jar verified.")) {
//			System.out.println(out);
			return true;
		}
		return false;
	}

	public static FormValidation checkProgram(String value) {
		if (value.length() == 0)
			return FormValidation.error("Please set a Path");
		if (!Validation.isValidProgram(value))
			return FormValidation.warning(value + " is invalid. Please make sure that this file exists.");
		return FormValidation.ok();
	}

	private static Boolean isValidProgram(String path) {
//		System.out.println(curlPath + " validation");

		if (path == null) {
			return false;
		}
		ArrayList<String> toVerify = new ArrayList<String>();
		toVerify.add(path);
		toVerify.add("-h");
		String out = exec(toVerify);
		if (out != null) {
			return true;
		}
//		System.out.println(out);
		return false;
	}

	public static Boolean isValidProgram(String path, String name) throws TestFairyException {

		if (path == null ) {
			throw new TestFairyException("Wrong " + name + " path, Please checkProgram it on " + Uploader.JENKINS_URL + "configure (" + path + ")");
		}
		ArrayList<String> toVerify = new ArrayList<String>();
		toVerify.add(path);
		toVerify.add("-h");
		String out = exec(toVerify);
		if (out != null) {
			return true;
		} else {
			throw new TestFairyException("Wrong " + name + " path, Please checkProgram it on " + Uploader.JENKINS_URL + "configure (" + path + ")");
		}
	}

	private static String exec(List<String> command) {

		String outputString;
		String outputStringToReturn = "";
		Process process;
		try {
			ProcessBuilder pb = new ProcessBuilder(command);
			process = pb.start();

			DataInputStream inputStream = new DataInputStream(process.getInputStream());
			while ((outputString = inputStream.readLine()) != null) {
				outputStringToReturn = outputStringToReturn + outputString;
			}
		} catch (IOException e1) {
			outputStringToReturn = null;
		}
		return outputStringToReturn;

	}
}
