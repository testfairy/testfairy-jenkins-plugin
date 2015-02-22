package com.testfairy.uploader;


import com.testfairy.uploader.command.JarSignerCommand;
import com.testfairy.uploader.command.VerifyCommand;
import com.testfairy.uploader.command.ZipAlignCommand;
import com.testfairy.uploader.command.ZipCommand;
import hudson.EnvVars;
import net.sf.json.JSONObject;
import org.apache.commons.httpclient.HttpHost;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jenkinsci.plugins.testfairy.TestFairyAndroidRecorder;
import org.jenkinsci.plugins.testfairy.TestFairyBaseRecorder;
import org.jenkinsci.plugins.testfairy.Utils;

import java.io.*;
import java.util.List;
import java.util.Scanner;

public class Uploader {

	public static String VERSION = "2.0";
	private static String SERVER = "http://api.testfairy.com";
	private static final String UPLOAD_URL_PATH = "/api/upload";
	private static final String UPLOAD_SIGNED_URL_PATH = "/api/upload-signed";

	public static final String USER_AGENT = "TestFairy Jenkins Plugin VERSION:" + Uploader.VERSION;
	public static String JENKINS_URL = "[jenkinsURL]/";

	private final PrintStream logger;

	public Uploader(PrintStream logger) {
		this.logger = logger;
	}

	private DefaultHttpClient buildHttpClient() {
		DefaultHttpClient httpClient = new DefaultHttpClient();

		// configure proxy (patched by timothy-volvo, https://github.com/timothy-volvo/testfairy-gradle-plugin)
		String proxyHost = System.getProperty("http.proxyHost");
		if (proxyHost != null) {
			Integer proxyPort = Integer.parseInt(System.getProperty("http.proxyPort"));
			HttpHost proxy = new HttpHost(proxyHost, proxyPort);
			String proxyUser = System.getProperty("http.proxyUser");
			if (proxyUser != null) {
				AuthScope authScope = new AuthScope(proxyUser, proxyPort);
				Credentials credentials = new UsernamePasswordCredentials(proxyUser, System.getProperty("http.proxyPassword"));
				httpClient.getCredentialsProvider().setCredentials(authScope, credentials);
			}

			httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
		}

		return httpClient;
	}

	private JSONObject post(String url, MultipartEntity entity) throws IOException, TestFairyException {
		DefaultHttpClient httpClient = buildHttpClient();
//		logger.println("post to  --> " + url);

		HttpPost post = new HttpPost(url);
		post.addHeader("User-Agent", USER_AGENT);
		post.setEntity(entity);

		try {
			HttpResponse response = httpClient.execute(post);
			InputStream is = response.getEntity().getContent();

			// Improved error handling.
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != 200) {
				String responseBody = new Scanner(is).useDelimiter("\\A").next();
				throw new TestFairyException(responseBody);
			}

			StringWriter writer = new StringWriter();
			IOUtils.copy(is, writer, "UTF-8");
			String responseString = writer.toString();

			logger.println("post finished " + responseString);
			JSONObject json = JSONObject.fromObject(responseString);
			if (json.getString("status").equals("fail")) {
				String errorMessage = json.getString("message");
				throw new TestFairyException(errorMessage);
			}
			return json;

		} catch (Throwable t) {
//			System.out.println("Post Throwable");
//			t.printStackTrace();
			if (t instanceof TestFairyException) {
				// The TestFairyException will be cached in the preform function (only the massage will be printed for the user)
				throw new TestFairyException(t.getMessage());
			} else {
				throw new IOException("Post fail " + t.getMessage());
			}
		}
	}


	/**
	 * Downloads the entire page at a remote location, onto a local file.
	 *
	 * @param url
	 * @param localFilename
	 */
	private void downloadFile(String url, String localFilename) throws IOException {
		DefaultHttpClient httpClient = buildHttpClient();
		HttpGet httpget = new HttpGet(url);
		HttpResponse response = httpClient.execute(httpget);
		HttpEntity entity = response.getEntity();

		FileOutputStream fis = new FileOutputStream(localFilename);
		IOUtils.copy(entity.getContent(), fis);
		fis.close();
	}

	/**
	 * Upload an APK using /api/upload REST service.
	 * @param apkFilename
	 * @param changeLog
	 * @param recorder
	 * @return
	 * @throws IOException
	 */
	public JSONObject uploadApp(String apkFilename, String changeLog, TestFairyBaseRecorder recorder) throws IOException, TestFairyException {

		logger.println("Uploading App...");
		MultipartEntity entity = buildEntity(recorder, apkFilename , changeLog);
		return post(SERVER + UPLOAD_URL_PATH, entity);
	}

	/**
	 * Upload a signed APK using /api/upload-signed REST service.
	 * @param apkFilename
	 * @param recorder
	 * @return
	 * @throws IOException
	 */
	public JSONObject uploadSignedApk(String apkFilename, TestFairyBaseRecorder recorder) throws IOException, TestFairyException {

		logger.println("Uploading SignedApk...");
		MultipartEntity entity = new MultipartEntity();

		addFileEntity(entity, "apk_file", apkFilename);
		addFileEntity(entity, "proguard_file", recorder.getMappingFile());

		addEntity(entity, "api_key",  recorder.getApiKey());
		addEntity(entity, "testers-groups",  recorder.getTestersGroups()); // if omitted, no emails will be sent to testers
		addEntity(entity, "notify", recorder.getNotifyTesters());
		addEntity(entity, "auto-update", recorder.getAutoUpdate());

		return post(SERVER + UPLOAD_SIGNED_URL_PATH, entity);
	}

	/**
	 * Build MultipartEntity for API parameters on Upload of an APK
	 * @param recorder
	 * @param apkFilename
	 * @param changeLog
	 * @return
	 * @throws IOException
	 */
	private MultipartEntity buildEntity(TestFairyBaseRecorder recorder, String apkFilename, String changeLog) throws IOException {

		MultipartEntity entity = new MultipartEntity();

		addFileEntity(entity, "apk_file", apkFilename);
		addFileEntity(entity, "proguard_file", recorder.getMappingFile());

		addEntity(entity, "api_key",  recorder.getApiKey());
		addEntity(entity, "changelog",  changeLog);
		addEntity(entity, "video-quality",  recorder.getVideoQuality()); // if omitted, default value is "high"
		addEntity(entity, "screenshot-interval",  recorder.getScreenshotInterval()); // if omitted, default is 1 frame per second (videoRate = 1.0)
		addEntity(entity, "max-duration",  recorder.getMaxDuration()); // override default value
		addEntity(entity, "testers-groups",  recorder.getTestersGroups()); // if omitted, no emails will be sent to testers
		addEntity(entity, "advanced-options",  recorder.getAdvancedOptions());

		addEntity(entity, "data-only-wifi", recorder.getDataOnlyWifi());
//		addEntity(entity, "auto-update", recorder.getAutoUpdate());
		addEntity(entity, "record-on-background", recorder.getRecordOnBackground()); // enable record on background option
		addEntity(entity, "video", recorder.getIsVideoEnabled());
//		addEntity(entity, "notify", recorder.getNotifyTesters());

		//todo addEntity(entity, "icon-watermark", recorder.);

		entity.addPart("metrics", new StringBody(extractMetrics(recorder)));

		return entity;
	}

	private void addEntity(MultipartEntity entity, String name, String value) throws UnsupportedEncodingException {
		if (value != null && !value.isEmpty()) {
			logger.println("--add " +name + ": " + value.replace("\n", ""));
			entity.addPart(name, new StringBody(value));
		}
	}

	private void addEntity(MultipartEntity entity, String name, Boolean value) throws UnsupportedEncodingException {
		logger.println("--add " +name + ": " + (value ? "on" : "off"));
		entity.addPart(name, new StringBody(value ? "on" : "off"));
	}

	private void addFileEntity(MultipartEntity entity, String name, String filePath) throws UnsupportedEncodingException {
		if (filePath != null && !filePath.isEmpty()) {
			logger.println("--add (file) " +name + ": " + filePath);
			entity.addPart(name, new FileBody(new File(filePath)));
		}
	}


	private String extractMetrics(TestFairyBaseRecorder baseRecorder) {
		StringBuilder stringBuilder = new StringBuilder();
		String metrics = "";
		if (baseRecorder.getCpu()) {
			stringBuilder.append(",cpu");
		}
		if (baseRecorder.getMemory()) {
			stringBuilder.append(",memory");
		}
		if (baseRecorder.getNetwork()) {
			stringBuilder.append(",network");
		}
		if (baseRecorder.getLogs()) {
			stringBuilder.append(",logcat");
		}
		if (baseRecorder.getPhoneSignal()) {
			stringBuilder.append(",phone-signal");
		}
		if (baseRecorder.getWifi()) {
			stringBuilder.append(",wifi");
		}
		if (baseRecorder.getGps()) {
			stringBuilder.append(",gps");
		}
		if (baseRecorder.getBattery()) {
			stringBuilder.append(",battery");
		}
		if (baseRecorder.getOpenGl()) {
			stringBuilder.append(",opengl");
		}

		if(stringBuilder.length() > 0) {
			// remove the first char (it will be ",")
			metrics = stringBuilder.substring(1);
		}
		logger.println("Metrics: " + metrics);
		return metrics;
	}
	/**
	 * return the path to the signed Apk
	 * @param environment
	 * @param apkFilename
	 * @param recorder
	 * @return the path to the signed Apk
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public String signingApk(TestFairyAndroidRecorder.AndroidBuildEnvironment environment, String apkFilename, TestFairyAndroidRecorder recorder) throws IOException, InterruptedException, TestFairyException {

		String apkFilenameZipAlign =  Utils.createEmptyInstrumentedAndSignedFile();
//
		ZipCommand zipCommand = new ZipCommand(environment.zipPath, apkFilename);
		exec(zipCommand);

		JarSignerCommand jarSignerCommand = new JarSignerCommand(environment.jarsignerPath, recorder, apkFilename);
		String out = exec(jarSignerCommand);
		if (out.contains("error") || out.contains("unsigned")) {
			throw new TestFairyException(out);
		}

		VerifyCommand verifyCommand = new VerifyCommand(environment.jarsignerPath, apkFilename);
		exec(verifyCommand);

		ZipAlignCommand zipAlignCommand = new ZipAlignCommand(environment.zipalignPath, apkFilename, apkFilenameZipAlign);
		exec(zipAlignCommand);

		return apkFilenameZipAlign;
	}

	private String exec(List<String> command) throws IOException, InterruptedException , TestFairyException{

		logger.println("exec command: " + command);
		String outputString;
		String outputStringToReturn = "";

		ProcessBuilder pb = new ProcessBuilder(command);
		Process process = pb.start();
		process.waitFor();
		DataInputStream curlIn = new DataInputStream(process.getInputStream());
		while ((outputString = curlIn.readLine()) != null) {
			outputStringToReturn = outputStringToReturn + outputString;
		}
		logger.println("Output: " + outputStringToReturn);

		logger.println("exitValue(): " + process.exitValue());
		if (process.exitValue() > 0) {
			throw new TestFairyException((outputStringToReturn.isEmpty()) ? "Error on " + command : outputStringToReturn);
		}
		return outputStringToReturn;
	}

	public static void setServer(EnvVars vars, PrintStream logger) {

		String server = vars.expand("$TESTFAIRY_UPLOADER_SERVER");
		if (server != null && !server.isEmpty() && !server.equals("$TESTFAIRY_UPLOADER_SERVER")) {
			SERVER = server;
			logger.println("The server will be  " + SERVER);
		}
	}
}
