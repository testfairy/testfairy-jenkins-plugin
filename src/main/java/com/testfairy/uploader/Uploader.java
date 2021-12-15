package com.testfairy.uploader;


import hudson.EnvVars;
import net.sf.json.JSONObject;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jenkinsci.plugins.testfairy.TestFairyBaseRecorder;
import org.jenkinsci.plugins.testfairy.Utils;

import java.io.*;
import java.util.Scanner;

public class Uploader {
	private static final String UPLOAD_URL_PATH = "/api/upload";

	private final String server;
	private final String userAgent;
	private final String version;
	private final PrintStream logger;

	public Uploader(
		PrintStream logger,
		String version,
		String server
	) {
		this.server = server;
		this.version = version;
		this.userAgent = "TestFairy Jenkins Plugin VERSION: " + version;
		this.logger = logger;
	}

	private DefaultHttpClient buildHttpClient() {
		DefaultHttpClient httpClient = new DefaultHttpClient();

		// configure proxy (patched by timothy-volvo, https://github.com/timothy-volvo/testfairy-gradle-plugin)
		String proxyHost = System.getProperty("http.proxyHost");
		if (proxyHost != null) {
			Integer proxyPort = System.getProperty("http.proxyPort") != null ? Integer.parseInt(System.getProperty("http.proxyPort")) : -1;
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

	private String post(String url, MultipartEntity entity) throws IOException, TestFairyException {
		DefaultHttpClient httpClient = buildHttpClient();
//		logger.println("post to  --> " + url);

		HttpPost post = new HttpPost(url);
		post.addHeader("User-Agent", userAgent);
		post.setEntity(entity);

		InputStream is = null;
		try {
			HttpResponse response = httpClient.execute(post);
			is = response.getEntity().getContent();

			// Improved error handling.
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != 200) {
				String responseBody = new Scanner(is, "UTF-8").useDelimiter("\\A").next();
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
			return responseString;

		} catch (Throwable t) {
			if (t instanceof TestFairyException) {
				// The TestFairyException will be cached in the preform function (only the massage will be printed for the user)
				throw new TestFairyException(t.getMessage());
			} else {
				throw new IOException("Post failed " + t.getMessage() , t);
			}
		} finally {
			Utils.closeSafely(is);
		}
	}

	/**
	 * Upload an APK using /api/upload REST service.
	 * @param apkFilename apkFilename
	 * @param mappingFile mappingFile
	 * @param changeLog
	 * @param recorder
	 * @param isInstrumentationOff
	 * @return JSONObject
	 * @throws IOException
	 */
	public String uploadApp(String apkFilename, String mappingFile, String changeLog, TestFairyBaseRecorder recorder, Boolean isInstrumentationOff) throws IOException, TestFairyException {

		logger.println("Uploading App...");
		MultipartEntity entity = buildEntity(recorder, apkFilename, mappingFile, changeLog, isInstrumentationOff);

		return post(this.server + UPLOAD_URL_PATH, entity);
	}

	/**
	 * Build MultipartEntity for API parameters on Upload of an APK
	 * @param recorder
	 * @param apkFilename
	 * @param mappingFile
	 * @param changeLog
	 * @return MultipartEntity
	 * @throws IOException
	 */
	private MultipartEntity buildEntity(TestFairyBaseRecorder recorder, String apkFilename, String mappingFile, String changeLog, Boolean isInstrumentationOff) throws IOException {

		MultipartEntity entity = new MultipartEntity();

		addFileEntity(entity, "apk_file", apkFilename);
		addFileEntity(entity, "proguard_file", mappingFile);

		addEntity(entity, "api_key",  recorder.getApiKey());
		addEntity(entity, "changelog",  changeLog);
		addEntity(entity, "video-quality",  recorder.getVideoQuality()); // if omitted, default value is "high"
		addEntity(entity, "screenshot-interval",  recorder.getScreenshotInterval()); // if omitted, default is 1 frame per second (videoRate = 1.0)
		addEntity(entity, "max-duration",  recorder.getMaxDuration()); // override default value
		addEntity(entity, "testers-groups",  recorder.getTestersGroups()); // if omitted, no emails will be sent to testers
		addEntity(entity, "advanced-options",  recorder.getAdvancedOptions());

		addEntity(entity, "data-only-wifi", recorder.getDataOnlyWifi());

		addEntity(entity, "record-on-background", recorder.getRecordOnBackground()); // enable record on background option
		addEntity(entity, "video", recorder.getIsVideoEnabled());
		addEntity(entity, "tags", recorder.getTags());

		if (isInstrumentationOff) {
			addEntity(entity, "auto-update", recorder.getAutoUpdate());
			addEntity(entity, "notify", recorder.getNotifyTesters());
			addEntity(entity, "instrumentation", "off");
		}

		//todo addEntity(entity, "icon-watermark", recorder.);

		entity.addPart("metrics", new StringBody(extractMetrics(recorder)));

		return entity;
	}

	private void addEntity(MultipartEntity entity, String name, String value) throws UnsupportedEncodingException {
		if (value != null && !value.isEmpty()) {
			logger.println("--add " +name + ": " + (name.contentEquals("api_key") ? "****" : value.replace("\n", "")));
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

		if(stringBuilder.length() > 0) {
			// remove the first char (it will be ",")
			metrics = stringBuilder.substring(1);
		}
		logger.println("Metrics: " + metrics);
		return metrics;
	}

	public static String getServer(EnvVars vars, PrintStream logger) {
		String server = vars.expand("$TESTFAIRY_UPLOADER_SERVER");
		if (server != null && !server.isEmpty() && !server.equals("$TESTFAIRY_UPLOADER_SERVER")) {
			logger.println("The server will be  " + server);
			return server;
		} else {
			return "https://upload.testfairy.com";
		}
	}
}
