package com.testfairy.uploader;

import java.io.Serializable;

/**
 * Created by gilt on 1/15/15.
 */
public class TestFairyException extends Exception implements Serializable{


	public TestFairyException(String msg) {
		super(msg);
	}

	public TestFairyException(String message, Throwable ue) {
		super(message, ue);
	}
}
