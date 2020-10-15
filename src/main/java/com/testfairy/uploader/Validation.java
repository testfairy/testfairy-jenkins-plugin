package com.testfairy.uploader;

import hudson.util.FormValidation;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Validation implements Serializable {
	public static FormValidation checkAbsolutePath(String value) {
		if (!value.startsWith("/") && !value.startsWith("$") && !value.startsWith("C:\\") && !value.startsWith("\\")) {
			return FormValidation.warning("Please make sure you are using absolute path");
		}

		return FormValidation.ok();
	}
}
