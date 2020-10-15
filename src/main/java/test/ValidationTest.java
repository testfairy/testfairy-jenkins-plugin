package test;

import com.testfairy.uploader.TestFairyException;
import com.testfairy.uploader.Validation;

/**
 * Created by gilt on 1/6/15.
 */
public class ValidationTest {

	private static String appFile = "/Users/gilt/dev/android/DialogTest/out/production/DialogTest/DialogTest.apk";
	private static String mappingFile = null;
	private static String testersGroups;//= "testGroup";
	private static Boolean notifyTesters = true;
	private static Boolean autoUpdate = true;
	private static String changeLog = "change Log test";
	private static String maxDuration = "60m";
	private static Boolean recordOnBackground = true;
	private static Boolean dataOnlyWifi = true;
	private static String screenshotInterval = "2";
	private static String videoQuality = "medium";
	private static String comment = "Uploader Curl test";

	// metrix
	private static Boolean cpu = false;
	private static Boolean memory = false;
	private static Boolean network = false;
	private static Boolean logs = false;
	private static Boolean phoneSignal = false;
	private static Boolean wifi = false;
	private static Boolean gps = false;
	private static Boolean battery = false;
	private static Boolean openGl = false;


	private String keystorePath = "/Users/gilt/dev/android/command-line-uploader/debug.keystore";
	private String storepass = "android";
	private String alias = "androiddebugkey";


	private static String apiKey = "5f8d490c554f63cf7784174bcdcb3c87f2447709";
	private static String zipPath = "zip";
	private static String jarsignerPath = "jarsigner";
	private static String curlPath = "/usr/bin/curl";
	private static String zipalignPath = "zipalign";




	public static void main(String[] args) {


		try {
			if (Validation.isValidProgram(zipPath, "zip")) {
				valid(zipPath);
			} else {
				fail(zipPath);
			}

			if (Validation.isValidProgram(jarsignerPath, "jarsigner")) {
				valid(jarsignerPath);
			} else {
				fail(jarsignerPath);
			}

			if (Validation.isValidProgram(zipalignPath, "zipalign")) {
				valid(zipalignPath);
			} else {
				fail(zipalignPath);
			}


			if (Validation.isValidAPK(jarsignerPath, appFile)) {
				valid(appFile);
			} else {
				fail(appFile);
			}
		} catch (TestFairyException e1) {
			e1.printStackTrace();
		}


	}
	private static void valid(String path) {
		System.out.println(path + " is Valid");
	}
	private static void fail(String path) {
		System.out.println(path + " not Valid");

	}

}
