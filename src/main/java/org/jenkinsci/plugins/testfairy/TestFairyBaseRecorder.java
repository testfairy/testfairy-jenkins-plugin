package org.jenkinsci.plugins.testfairy;

import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Recorder;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.Serializable;

/**
 * Created by gilt on 1/14/15.
 */
public class TestFairyBaseRecorder extends Recorder implements Serializable {

	protected final String apiKey;
	protected String appFile;
	protected String mappingFile;
	protected final String testersGroups;
	protected final Boolean notifyTesters;
	protected final Boolean autoUpdate;
	protected final String maxDuration;
	protected final Boolean recordOnBackground;
	protected final Boolean dataOnlyWifi;
	protected final Boolean isVideoEnabled;
	protected final String screenshotInterval;
	protected final String videoQuality;
	protected final String advancedOptions;

	protected final Boolean cpu;
	protected final Boolean memory;
	protected final Boolean logs;
	protected final Boolean network;
	protected final Boolean phoneSignal;
	protected final Boolean wifi;
	protected final Boolean gps;
	protected final Boolean battery;
	protected final Boolean openGl;

	@DataBoundConstructor
	public TestFairyBaseRecorder(String apiKey, String appFile, String mappingFile, String testersGroups, Boolean notifyTesters, Boolean autoUpdate, String maxDuration, Boolean recordOnBackground, Boolean dataOnlyWifi, Boolean isVideoEnabled, String screenshotInterval, String videoQuality, String advancedOptions, Boolean cpu, Boolean memory, Boolean logs, Boolean network, Boolean phoneSignal, Boolean wifi, Boolean gps, Boolean battery, Boolean openGl) {

		this.apiKey = apiKey;
		this.appFile = appFile;
		this.mappingFile = mappingFile;
		this.testersGroups = testersGroups;
		this.notifyTesters = notifyTesters;
		this.autoUpdate = autoUpdate;
		this.maxDuration = maxDuration;
		this.recordOnBackground = recordOnBackground;
		this.dataOnlyWifi = dataOnlyWifi;
		this.isVideoEnabled = isVideoEnabled;
		this.screenshotInterval = screenshotInterval;
		this.videoQuality = videoQuality;
		this.advancedOptions = advancedOptions;
		this.cpu = cpu;
		this.memory = memory;
		this.logs = logs;
		this.network = network;
		this.phoneSignal = phoneSignal;
		this.wifi = wifi;
		this.gps = gps;
		this.battery = battery;


		this.openGl = openGl;
	}

	public void setAppFile(String appFile) {
		this.appFile = appFile;
	}

	public void setMappingFile(String mappingFile) {
		this.mappingFile = mappingFile;
	}

	public String getApiKey() {
		return apiKey;
	}

	public String getAppFile() {
		return appFile;
	}

	public String getMappingFile() {
		return mappingFile;
	}

	public String getTestersGroups() {
		return testersGroups;
	}

	public Boolean getNotifyTesters() {
		return notifyTesters;
	}

	public Boolean getAutoUpdate() {
		return autoUpdate;
	}

	public String getMaxDuration() {
		return maxDuration;
	}

	public Boolean getRecordOnBackground() {
		return recordOnBackground;
	}

	public Boolean getDataOnlyWifi() {
		return dataOnlyWifi;
	}

	public Boolean getIsVideoEnabled() {
		return isVideoEnabled;
	}

	public String getScreenshotInterval() {
		return screenshotInterval;
	}

	public String getVideoQuality() {
		return videoQuality;
	}

	public String getAdvancedOptions() {
		return advancedOptions;
	}

	public Boolean getCpu() {
		return cpu;
	}

	public Boolean getMemory() {
		return memory;
	}

	public Boolean getLogs() {
		return logs;
	}

	public Boolean getNetwork() {
		return network;
	}

	public Boolean getPhoneSignal() {
		return phoneSignal;
	}

	public Boolean getWifi() {
		return wifi;
	}

	public Boolean getGps() {
		return gps;
	}

	public Boolean getBattery() {
		return battery;
	}

	public Boolean getOpenGl() {
		return openGl;
	}

	public BuildStepMonitor getRequiredMonitorService() {
		return BuildStepMonitor.NONE;
	}
}
