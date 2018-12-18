package fr.humanum.openarchaeo;

import org.slf4j.helpers.MessageFormatter;

public class OpenArchaeoException extends RuntimeException {

	private static final long serialVersionUID = 4038417825992668593L;

	public static void when(boolean test) {
		if (test) {
			throw new OpenArchaeoException("Assertion failed");
		}
	}

	public static void when(boolean test, String message) {
		if (test) {
			throw new OpenArchaeoException(message);
		}
	}

	public static void when(boolean test, String message, Object... parameters) {
		if (test) {
			throw new OpenArchaeoException(message, parameters);
		}
	}

	public static OpenArchaeoException rethrow(Throwable exception) {
		if (exception instanceof Error) {
			throw (Error) exception;
		}
		if (exception instanceof RuntimeException) {
			throw (RuntimeException) exception;
		}
		throw new OpenArchaeoException(exception);
	}

	public static <T> T failIfNotInstance(Object object, Class<T> clazz, String message, Object... parameters) {
		when(!clazz.isInstance(object), message, parameters);
		//noinspection unchecked
		return (T) object;
	}

	public static <T> T failIfNull(T value, String message, Object... parameters) {
		when(null == value, message, parameters);
		return value;
	}

	public OpenArchaeoException() {
	}

	public OpenArchaeoException(String message) {
		super(message);
	}

	public OpenArchaeoException(Throwable cause, String message, Object... parameters) {
		super(MessageFormatter.arrayFormat(message, parameters).getMessage(), cause);
	}

	public OpenArchaeoException(String message, Object... parameters) {
		super(MessageFormatter.arrayFormat(message, parameters).getMessage());
	}

	public OpenArchaeoException(Throwable cause) {
		super(cause);
	}

}
