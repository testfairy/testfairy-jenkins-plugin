package test;

import com.testfairy.uploader.Uploader;
import net.sf.json.JSONObject;
import org.jenkinsci.plugins.testfairy.TestFairyAndroidRecorder;
import org.jenkinsci.plugins.testfairy.Utils;

/**
 * Created by gilt on 1/6/15.
 */
public class UploaderTest {

	private static String appFile = "/Users/gilt/dev/android/DialogTest/out/production/DialogTest/DialogTest.apk";
	private static String mappingFile = null;
	private static String testersGroups;//= "testGroup";
	private static Boolean notifyTesters = true;
	private static Boolean autoUpdate = true;
	private static String changeLog = "change Log test";
	private static String maxDuration = "60m";
	private static Boolean recordOnBackground = true;
	private static Boolean dataOnlyWifi = true;
	private static Boolean isVideoEnabled = true;
	private static String screenshotInterval = "2";
	private static String videoQuality = "medium";
	private static String advancedOptions = "option1,option2,option3";

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


	private static String keystorePath = "/Users/gilt/dev/android/command-line-uploader/debug.keystore";
	private static String storepass = "android";
	private static String alias = "androiddebugkey";
	private static String keypass = "";


	private static String apiKey = "c502ea9cc04d83a2aa4050fe13812c8dc139140d";
	private static String zipPath = "zip";
	private static String jarsignerPath = "jarsigner";
	private static String curlPath = "curl";
	private static String zipalignPath = "/Users/gilt/apps/testfairy_git/server/deployment/bin/darwin/platform-tools/zipalign";




	public static void main(String[] args) {

		try {

			TestFairyAndroidRecorder t = new TestFairyAndroidRecorder(apiKey, appFile, mappingFile, testersGroups, notifyTesters, autoUpdate, maxDuration,
			    recordOnBackground, dataOnlyWifi, isVideoEnabled, screenshotInterval, videoQuality, advancedOptions, keystorePath, storepass, alias,keypass ,cpu, memory, logs, network, phoneSignal, wifi, gps, battery, openGl);

			Uploader uploader = new Uploader(System.out, Utils.getVersion(UploaderTest.class));
			Uploader.VERSION = Uploader.VERSION + "-test";
			TestFairyAndroidRecorder.AndroidBuildEnvironment androidBuildEnvironment = new TestFairyAndroidRecorder.AndroidBuildEnvironment(zipPath, jarsignerPath, zipalignPath);


			JSONObject response = uploader.uploadApp(appFile, changeLog, t);

			String instrumentedUrl = response.getString("instrumented_url");
			String instrumentedAppPath = Utils.downloadFromUrl(instrumentedUrl, System.out);

			String signedFilePath = uploader.signingApk(androidBuildEnvironment, instrumentedAppPath, t);
			JSONObject responseSigned = uploader.uploadSignedApk(signedFilePath, t);

			//print the build url
			System.out.println("Check the new build: " + responseSigned.getString("build_url"));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}