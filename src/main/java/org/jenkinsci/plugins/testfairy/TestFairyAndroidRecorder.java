package org.jenkinsci.plugins.testfairy;

import com.testfairy.uploader.TestFairyException;
import com.testfairy.uploader.Uploader;
import com.testfairy.uploader.Validation;
import hudson.EnvVars;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Publisher;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import javax.servlet.ServletException;
import java.io.IOException;

public class TestFairyAndroidRecorder extends TestFairyBaseRecorder {

	private String keystorePath;
	private final String storepass;
	private final String alias;

	@DataBoundConstructor
	public TestFairyAndroidRecorder(String apiKey, String appFile, String mappingFile,
					String testersGroups, Boolean notifyTesters, Boolean autoUpdate,
					String maxDuration, Boolean recordOnBackground, Boolean dataOnlyWifi,
					Boolean isVideoEnabled, String screenshotInterval, String videoQuality, String advancedOptions,
					String keystorePath, String storepass, String alias,
					Boolean cpu, Boolean memory, Boolean network,
					Boolean logs, Boolean phoneSignal, Boolean wifi,
					Boolean gps, Boolean battery, Boolean openGl
	) {

		super(apiKey, appFile, mappingFile, testersGroups, notifyTesters, autoUpdate, maxDuration,
		    recordOnBackground, dataOnlyWifi, isVideoEnabled, screenshotInterval, videoQuality, advancedOptions, cpu, memory, logs, network, phoneSignal, wifi, gps, battery, openGl);

		this.keystorePath = keystorePath;
		this.storepass = storepass;
		this.alias = alias;
	}



	public String getKeystorePath() {
		return keystorePath;
	}

	public String getStorepass() {
		return storepass;
	}

	public String getAlias() {
		return alias;
	}

	@Override
	public boolean perform(AbstractBuild build, Launcher launcher, final BuildListener listener) throws InterruptedException, IOException {
		listener.getLogger().println("TestFairy Uploader... ");

		try {
			Uploader uploader = new Uploader(listener.getLogger());
			AndroidBuildEnvironment environment = getDescriptor().getEnvironment();
			String changeLog = Utils.extractChangeLog(build.getChangeSet());
			EnvVars vars = build.getEnvironment(listener);

			Utils.setJenkinsUrl(vars);
			appFile = Utils.getApkFilePath(appFile, environment, vars);
			mappingFile = Utils.getFilePath(mappingFile, vars, false);

			JSONObject response = uploader.uploadApp(appFile, changeLog, this);

			String instrumentedUrl = response.getString("instrumented_url");
			String instrumentedAppPath = Utils.downloadFromUrl(instrumentedUrl, listener.getLogger());

			keystorePath = Utils.getFilePath(keystorePath, vars, true);
			String signedFilePath = uploader.signingApk(environment, instrumentedAppPath, this);

			JSONObject responseSigned = uploader.uploadSignedApk(signedFilePath, this);

			//print the build url
			listener.getLogger().println("Check the new build: " + responseSigned.getString("build_url"));
			return true;

		} catch (TestFairyException e) {
			listener.error(e.getMessage() + "\n");
			return false;
		}

	}

	@Override
	public DescriptorImpl getDescriptor() {
		return (DescriptorImpl) super.getDescriptor();
	}


	/**
	 * Descriptor for {@link TestFairyAndroidRecorder}. Used as a singleton.
	 * The class is marked as public so that it can be accessed from views.
	 * <p/>
	 * <p/>
	 * See <tt>src/main/resources/hudson/plugins/hello_world/TestFairyRecorder/*.jelly</tt>
	 * for the actual HTML fragment for the configuration screen.
	 */
	@Extension // This indicates to Jenkins that this is an implementation of an extension point.
	public static final class DescriptorImpl extends BuildStepDescriptor<Publisher> {
		/**
		 * To persist global configuration information,
		 * simply store it in a field and call save().
		 * <p/>
		 * <p/>
		 * If you don't want fields to be persisted, use <tt>transient</tt>.
		 */
		private String zipPath;
		private String jarsignerPath;
		private String zipalignPath;

		/**
		 * In order to load the persisted global configuration, you have to
		 * call load() in the constructor.
		 */
		public DescriptorImpl() {
			load();
		}

		/**
		 * Performs on-the-fly validation of the form field 'name'.
		 *
		 * @param value This parameter receives the value that the user has typed.
		 * @return Indicates the outcome of the validation. This is sent to the browser.
		 * <p/>
		 * Note that returning {@link FormValidation#error(String)} does not
		 * prevent the form from being saved. It just means that a message
		 * will be displayed to the user.
		 */

//		public FormValidation doCheckAppFile(@QueryParameter String value) throws IOException, ServletException {
//
//			return Valid.checkApk(jarsignerPath, value);
//
//		}

//		public FormValidation doCheckEnvironmentExist(@QueryParameter String value) throws IOException, ServletException {
//			if (curlPath.length() == 0 ) {
//				return FormValidation.error("Please set a curl path in '..jenkins/configure'");
//			} else if (!Validation.isValidProgram(curlPath)) {
//				return FormValidation.error("Your curl is invalid, checkProgram it in '..jenkins/configure'");
//			}
//
//			if (zipPath.length() == 0 ) {
//				return FormValidation.error("Please set a zip path in '..jenkins/configure'");
//			} else if (!Validation.isValidProgram(zipPath)) {
//				return FormValidation.error("Your zip is invalid, checkProgram it in '..jenkins/configure'");
//			}
//
//			if (jarsignerPath.length() == 0 ) {
//				return FormValidation.error("Please set a jarsigner path in '..jenkins/configure'");
//			} else if (!Validation.isValidProgram(jarsignerPath)) {
//				return FormValidation.error("Your jarsigner is invalid, checkProgram it in '..jenkins/configure'");
//			}
//
////			if (zipalignPath.length() == 0 ) {
////				return FormValidation.error("Please set a zipalign path in '..jenkins/configure'");
////			} else if (!Valid.isValidProgram(zipalignPath)) {
////				return FormValidation.error("Your zipalign is invalid, checkProgram it in '..jenkins/configure'");
////			}
//			return FormValidation.ok();
//
//		}
		public FormValidation doCheckApiKey(@QueryParameter String value) throws IOException, ServletException {
			if (value.length() == 0)
				return FormValidation.error("Please set an ApiKey");
			if (value.length() != 40)
				return FormValidation.warning("This is invalid ApiKey");
			return FormValidation.ok();
		}
		public FormValidation doCheckZipPath(@QueryParameter String value) throws IOException, ServletException {
			return Validation.checkProgram(value);
		}
		public FormValidation doCheckJarsignerPath(@QueryParameter String value) throws IOException, ServletException {
			return Validation.checkProgram(value);
		}
		public FormValidation doCheckZipalignPath(@QueryParameter String value) throws IOException, ServletException {
			return Validation.checkProgram(value);
		}


		/**
		 * Called when rendering maxDuration field
		 *
		 * @return ListBoxModel
		 */
		public ListBoxModel doFillMaxDurationItems() {
			ListBoxModel items = new ListBoxModel();

			items.add("10 minutes", "10m");
			items.add("1 hour", "60m");
			items.add("5 hours", "300m");
			items.add("24 hours", "1440m");
			return items;
		}

		public ListBoxModel doFillScreenshotIntervalItems() {
			ListBoxModel items = new ListBoxModel();

			items.add("1 second", "1");
			items.add("2 seconds", "2");
			items.add("5 seconds", "5");
			return items;
		}

		public ListBoxModel doFillVideoQualityItems() {
			ListBoxModel items = new ListBoxModel();


			items.add("High", "high");
			items.add("Medium", "medium");
			items.add("Low", "low");

			return items;
		}

		public boolean isApplicable(Class<? extends AbstractProject> aClass) {
			// Indicates that this builder can be used with all kinds of project types
			return true;
		}

		/**
		 * This human readable name is used in the configuration screen.
		 */
		public String getDisplayName() {
			return "Upload to TestFairy Android";
		}

		@Override
		public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {

			zipPath = formData.getString("zipPath");
			jarsignerPath = formData.getString("jarsignerPath");
			zipalignPath = formData.getString("zipalignPath");

			save();
			return super.configure(req, formData);
		}


		public String getZipPath() {
			return zipPath;
		}

		public String getJarsignerPath() {
			return jarsignerPath;
		}

		public String getZipalignPath() {
			return zipalignPath;
		}

		public AndroidBuildEnvironment getEnvironment() throws TestFairyException {

//			//the default variables are only for display
			if (zipPath == null) {
				zipPath = "zip";
			}
			if (jarsignerPath == null) {
				jarsignerPath = "jarsigner";
			}
			if (zipalignPath == null) {
				zipalignPath = "zipalign";
			}
			Validation.isValidProgram(zipPath, "zip");
			Validation.isValidProgram(jarsignerPath, "jarsigner");
			Validation.isValidProgram(zipalignPath, "zipalign");
			return new AndroidBuildEnvironment(zipPath, jarsignerPath, zipalignPath);
		}
	}

	public static class AndroidBuildEnvironment {
		public String zipPath;
		public String jarsignerPath;
		public String zipalignPath;

		/**
		 *
		 * @param zipPath
		 * @param jarsignerPath
		 * @param zipalignPath
		 */
		public AndroidBuildEnvironment(String zipPath, String jarsignerPath, String zipalignPath) {

			this.zipPath = zipPath;
			this.jarsignerPath = jarsignerPath;
			this.zipalignPath = zipalignPath;
		}
	}
}

