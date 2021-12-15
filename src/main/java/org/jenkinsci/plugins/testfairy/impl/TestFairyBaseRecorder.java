package org.jenkinsci.plugins.testfairy.impl;

import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Recorder;
import hudson.util.Secret;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.Serializable;

public class TestFairyBaseRecorder extends Recorder implements Serializable {

	protected final Secret apiKey;
	protected final String appFile;
	protected final String mappingFile;
	protected final String tags;
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

	@DataBoundConstructor
	public TestFairyBaseRecorder(
		Secret apiKey,
		String appFile,
		String mappingFile,
		String tags,
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
		Boolean logs,
		Boolean network,
		Boolean phoneSignal,
		Boolean wifi,
		Boolean gps,
		Boolean battery
	) {
		this.apiKey = apiKey;
		this.appFile = appFile;
		this.mappingFile = mappingFile;
		this.tags = tags;
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
	}

	public String getApiKey() {
		return apiKey.getPlainText();
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

	public String getTags() { return tags; }

	public BuildStepMonitor getRequiredMonitorService() {
		return BuildStepMonitor.NONE;
	}
}
