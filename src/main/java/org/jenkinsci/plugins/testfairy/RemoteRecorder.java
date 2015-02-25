package org.jenkinsci.plugins.testfairy;

import hudson.EnvVars;
import hudson.model.BuildListener;
import hudson.remoting.Callable;
import net.sf.json.JSONObject;
import org.jenkinsci.remoting.RoleChecker;

import java.io.Serializable;

public abstract class RemoteRecorder implements Callable<JSONObject, Throwable>, Serializable {

	protected BuildListener listener;
	protected EnvVars vars;
	protected TestFairyBaseRecorder recorder;
	protected String version;
	protected String changeLog;


	public RemoteRecorder(BuildListener listener, TestFairyBaseRecorder testFairyIosRecorder, EnvVars vars, String changeLog) {
		this.listener = listener;
		this.recorder = testFairyIosRecorder;
		this.version = Utils.getVersion(this.getClass());
		this.changeLog = changeLog;
		this.vars = vars;

		this.listener.getLogger().println("create RemoteRecorder");
	}

	public RemoteRecorder() {
	}


	@Override
	public void checkRoles(RoleChecker roleChecker) throws SecurityException {

	}
}
