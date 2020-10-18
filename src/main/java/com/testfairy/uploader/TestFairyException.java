package com.testfairy.uploader;

import java.io.Serializable;

public class TestFairyException extends Exception implements Serializable{
	public TestFairyException(String msg) {
		super(msg);
	}
	public TestFairyException(String message, Throwable ue) {
		super(message, ue);
	}
}
