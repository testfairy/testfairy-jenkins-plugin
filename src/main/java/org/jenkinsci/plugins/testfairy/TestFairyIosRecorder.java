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
import hudson.model.Result;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Publisher;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import hudson.util.Secret;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import javax.servlet.ServletException;
import java.io.IOException;

import static hudson.Util.getHostName;

public class TestFairyIosRecorder extends TestFairyBaseRecorder {
	@DataBoundConstructor
	public TestFairyIosRecorder(
		Secret apiKey,
		String appFile,
		String mappingFile,
		String testersGroups,
		Boolean notifyTesters,
		Boolean autoUpdate,
		String maxDuration,
		Boolean recordOnBackground,
		Boolean dataOnlyWifi,
		Boolean isVideoEnabled,
		String screenshotInterval,
		String videoQuality,
		String advancedOptions,
		Boolean cpu,
		Boolean memory,
		Boolean network,
		Boolean logs,
		Boolean phoneSignal,
		Boolean wifi,
		Boolean gps,
		Boolean battery
	) {
		super(
			apiKey,
			appFile,
			mappingFile,
			testersGroups,
			notifyTesters,
			autoUpdate,
			maxDuration,
			recordOnBackground,
			dataOnlyWifi,
			isVideoEnabled,
			screenshotInterval,
			videoQuality,
			advancedOptions,
			cpu,
			memory,
			logs,
			network,
			phoneSignal,
			wifi,
			gps,
			battery
		);
	}

	@Override
	public boolean perform(AbstractBuild build, Launcher launcher, final BuildListener listener) throws IOException, InterruptedException {
		if (build.getResult() != null && build.getResult() == Result.FAILURE) {
			return false;
		}

		listener.getLogger().println("TestFairy iOS/Android Uploader... v " + Utils.getVersion(getClass()) + ", run on " + getHostName());

		try {
			EnvVars vars = build.getEnvironment(listener);
			String changeLog = Utils.extractChangeLog(vars, build.getChangeSet(), listener.getLogger());

			try {
				launcher.getChannel().call(new IosRemoteRecorder(listener, this, vars, changeLog));
			} catch (Throwable ue) {
				throw new TestFairyException(ue.getMessage(), ue);
			}

			return true;
		} catch (TestFairyException e) {
			listener.error(e.getMessage() + "\n");
			e.printStackTrace(listener.getLogger());
			return false;
		}
	}

	class IosRemoteRecorder extends RemoteRecorder {
		public IosRemoteRecorder(BuildListener listener, TestFairyIosRecorder testFairyIosRecorder, EnvVars vars, String changeLog) {
			super(listener, testFairyIosRecorder, vars, changeLog);
		}

		@Override
		public String call() throws Throwable {
			Utils.setJenkinsUrl(vars);
			Uploader.setServer(vars, listener.getLogger());
			Uploader upload = new Uploader(listener.getLogger(), Utils.getVersion(getClass()));

			String appFilePath = Utils.getFilePath(appFile, "*.ipa/*.apk", vars, true);
			String mappingFilePath = Utils.getFilePath(mappingFile, "symbols file", vars, false);

			// the last parameter is true, it means that "notify" and "auto-update" will be sent and (there is no "uploadSignApk" call for iOS)
			String responseString = upload.uploadApp(appFilePath, mappingFilePath, changeLog, recorder, true); // isInstrumentation == Off
			JSONObject response = JSONObject.fromObject(responseString);

			//print the build url
			listener.getLogger().println("Check the new build : " + response.getString("build_url"));
			return responseString;
		}
	};

	@Override
	public DescriptorImpl getDescriptor() {
		return (DescriptorImpl) super.getDescriptor();
	}

	/**
	 * Descriptor for {@link TestFairyIosRecorder}. Used as a singleton.
	 * The class is marked as public so that it can be accessed from views.
	 * See <tt>src/main/resources/hudson/plugins/hello_world/TestFairyRecorder/*.jelly</tt>
	 * for the actual HTML fragment for the configuration screen.
	 */
	@Extension // This indicates to Jenkins that this is an implementation of an extension point.
	public static final class DescriptorImpl extends BuildStepDescriptor<Publisher> {
		/**
		 * To persist global configuration information,
		 * simply store it in a field and call save().
		 * If you don't want fields to be persisted, use <tt>transient</tt>.
		 */

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
		 * Note that returning {@link hudson.util.FormValidation#error(String)} does not
		 * prevent the form from being saved. It just means that a message
		 * will be displayed to the user.
		 */
		public FormValidation doCheckApiKey(@QueryParameter String value) throws IOException, ServletException {
			if (value.length() == 0)
				return FormValidation.error("Please set an ApiKey");
			if (value.length() != 40)
				return FormValidation.warning("This is invalid ApiKey");
			return FormValidation.ok();
		}

		public FormValidation doCheckAppFile(@QueryParameter String value) throws IOException, ServletException {
			return Validation.checkAbsolutePath(value);
		}

		public FormValidation doCheckMappingFile(@QueryParameter String value) throws IOException, ServletException {
			return Validation.checkAbsolutePath(value);
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
			return "  TestFairy iOS/Android Uploader";
		}

		@Override
		public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
			save();
			return super.configure(req, formData);
		}
	}
}



