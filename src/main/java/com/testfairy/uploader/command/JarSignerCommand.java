package com.testfairy.uploader.command;

import com.testfairy.uploader.TestFairyException;
import org.jenkinsci.plugins.testfairy.TestFairyAndroidRecorder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
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

		try {
			if (isJks(new File(recorder.getKeystorePath()))) {

				add(jarsignerPath);
				add("-keystore");
				add(recorder.getKeystorePath());
				add("-storepass");
				add(recorder.getStorepass());
				if (recorder.getKeypass() != null && !recorder.getKeypass().isEmpty() ) {
					add("-keypass");
					add(recorder.getKeypass());
				}
				add("-digestalg");
				add("SHA1");
				add("-sigalg");
				add("MD5withRSA");
				add(apkFilename);
				add(recorder.getAlias());

			} else {
				// pkcs12
				add(jarsignerPath);
				add("-storetype");
				add("pkcs12");
				add("-storepass");
				add(recorder.getStorepass());
				add("-keystore");
				add(recorder.getKeystorePath());
				add(apkFilename);
				add(recorder.getAlias());
				//example - jarsigner -storetype pkcs12 -storepass 123456789 -keystore jenkins.keystore ham-testfairy.apk androiddebugkey
			}
		} catch (Exception e) {
			throw new TestFairyException(e.getMessage());
		}
	}

	public static boolean isJks(File f) throws Exception {
		KeyStore ks = null;

		FileInputStream fis = null;

		try {
			fis = new FileInputStream(f);
			ks = KeyStore.getInstance("jks");
			ks.load(fis, null);
			return true;
		} catch (IOException e) {
			if(fis != null){
				fis.close();
			}
			fis = new FileInputStream(f);
			ks = KeyStore.getInstance("pkcs12");
			ks.load(fis, null);
			return false;
		} finally {
			if(fis != null){
				fis.close();
			}
		}
	}
}