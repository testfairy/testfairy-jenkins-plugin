package com.testfairy.uploader.command;

import com.testfairy.uploader.TestFairyException;
import org.jenkinsci.plugins.testfairy.TestFairyAndroidRecorder;

import java.util.ArrayList;

public class JarSignerCommand extends ArrayList<String> {

	/**
	 *
	 * @param jarsignerPath
	 * @param recorder
	 * @param apkFilename
	 */
	public JarSignerCommand(String jarsignerPath, TestFairyAndroidRecorder recorder, String apkFilename) throws TestFairyException {
		super();
		if (recorder.getKeystorePath() == null || recorder.getKeystorePath().isEmpty()) {
			throw new TestFairyException("Missing Keystore file");
		}
		if (recorder.getStorepass() == null || recorder.getStorepass().isEmpty()) {
			throw new TestFairyException("Missing Storepass");
		}
		if (recorder.getAlias() == null || recorder.getAlias().isEmpty()) {
			throw new TestFairyException("Missing Alias");
		}
		add(jarsignerPath);
		add("-keystore");
		add(recorder.getKeystorePath());
		add("-storepass");
		add(recorder.getStorepass());
		add("-digestalg");
		add("SHA1");
		add("-sigalg");
		add("MD5withRSA");
		add(apkFilename);
		add(recorder.getAlias());
	}
}