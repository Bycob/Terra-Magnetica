package org.terramagnetica.opengl.engine;

public class ShaderCompilationException extends Exception {

	private static final long serialVersionUID = 1L;

	public ShaderCompilationException() {
		super();
	}

	public ShaderCompilationException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ShaderCompilationException(String message, Throwable cause) {
		super(message, cause);
	}

	public ShaderCompilationException(String message) {
		super(message);
	}

	public ShaderCompilationException(Throwable cause) {
		super(cause);
	}
}
