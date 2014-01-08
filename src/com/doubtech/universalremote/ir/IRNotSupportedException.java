package com.doubtech.universalremote.ir;

public class IRNotSupportedException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public IRNotSupportedException() {
        super("IR Service is not supported on this device.");
    }

    public IRNotSupportedException(String detailMessage, Throwable e) {
        super(detailMessage, e);
    }
}
