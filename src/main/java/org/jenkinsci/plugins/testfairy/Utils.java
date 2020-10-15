package org.jenkinsci.plugins.testfairy;

import com.testfairy.uploader.TestFairyException;
import com.testfairy.uploader.Uploader;
import com.testfairy.uploader.Validation;
import hudson.EnvVars;
import hudson.scm.ChangeLogSet;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

public class Utils implements Serializable {

	public static String extractChangeLog(EnvVars vars, final ChangeLogSet<?> changeSet, PrintStream logger) {

		String fileName = vars.expand("$JENKINS_HOME") + File.separator +
				    "jobs" + File.separator +
				    vars.expand("$JOB_NAME") + File.separator +
				    "builds" + File.separator +
				    vars.expand("$BUILD_ID") + File.separator +
				    "testfairy_change_log";

		String changeLog = getChangeLogFromFile(fileName);

		if (changeLog != null && !changeLog.isEmpty()) {
			logger.println("Loading custom changeLog from " + fileName);
			return changeLog;
		} else {
			logger.println("Loading changeLog from source control");
			return getChangeLogFromSourceControl(changeSet);
		}
	}

	private static String getChangeLogFromSourceControl(ChangeLogSet<?> changeSet) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("\n\n")
		    .append(changeSet.isEmptySet() ? "No changes since last build" : "Changes")
		    .append("\n");

		int entryNumber = 1;

		for (ChangeLogSet.Entry entry : changeSet) {
			stringBuilder.append("\n").append(entryNumber).append(". ");
			stringBuilder.append(entry.getMsg()).append(" \u2014 ").append(entry.getAuthor());
			entryNumber++;
		}
		return stringBuilder.toString();
	}

	private static String getChangeLogFromFile(String file) {
		BufferedReader reader = null;
		try {
		 	reader = new BufferedReader( new FileReader(file));
			String line = null;
			StringBuilder  stringBuilder = new StringBuilder();
			String ls = System.getProperty("line.separator");

			while( ( line = reader.readLine() ) != null ) {
				stringBuilder.append( line );
				stringBuilder.append( ls );
			}

			return stringBuilder.toString();

		} catch (IOException e) {
			return null;
		} finally {
			if (reader != null) { try { reader.close(); } catch (Exception ignored) {} }
		}
	}

	public static String downloadFromUrl(String urlString, PrintStream logger) throws IOException {

		logger.println("downloadFromUrl: " + urlString);
		InputStream is = null;
		FileOutputStream fos = null;
		long timeStamp = System.currentTimeMillis();

		URL url = new URL(urlString);

		File tempFile = File.createTempFile("instrumented-" + timeStamp, ".apk");

		try {
			URLConnection urlConn = url.openConnection();//connect

			is = urlConn.getInputStream();               //get connection inputstream
			fos = new FileOutputStream(tempFile);   //open outputstream to local file

			byte[] buffer = new byte[4096];              //declare 4KB buffer
			int len;

			//while we have availble data, continue downloading and storing to local file
			while ((len = is.read(buffer)) > 0) {
				fos.write(buffer, 0, len);
			}
		} finally {
			try {
				if (is != null) {
					is.close();
				}
			} finally {
				if (fos != null) {
					fos.close();
				}
			}
		}

		return tempFile.getPath();
	}

	public static String getFilePath(String file, String name, EnvVars vars, Boolean required) throws TestFairyException {
		if (file == null || file.length() == 0){
			if (required) {
				throw new TestFairyException("Can't find a " + name + " in " + file);
			} else {
				return null;
			}
		}
		String toReturn = vars.expand(file);
		if(isFileExists(toReturn)) {
			return toReturn;
		} else if (required) {
			throw new TestFairyException("Can't find a " + name + " in " + toReturn + " the original path was " + file);
		} else {
			return null;
		}
	}

	private static boolean isFileExists(String file) {
		File f = new File(file);
		return f.exists();
	}

	public static String createEmptyFile() throws IOException {
		long timeStamp = System.currentTimeMillis();
		File tempFile = File.createTempFile("ttemp-" + timeStamp, ".apk");
		return tempFile.getPath();
	}

	public static String getJenkinsUrl(EnvVars vars) {
		String hudsonUrl = vars.expand("$HUDSON_URL");
		if (hudsonUrl != null && !hudsonUrl.isEmpty() && !hudsonUrl.equals("$HUDSON_URL")) {
			return hudsonUrl;
		} else {
			return "[jenkinsURL]/";
		}
	}

	public static String getVersion(Class c) {
		return c.getPackage().getImplementationVersion();
	}
}