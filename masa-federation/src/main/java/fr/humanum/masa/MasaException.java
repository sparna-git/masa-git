package fr.humanum.masa;

import org.slf4j.helpers.MessageFormatter;

public class MasaException extends RuntimeException {

	private static final long serialVersionUID = 4038417825992668593L;

	public static void when(boolean test) {
		if (test) {
			throw new MasaException("Assertion failed");
		}
	}

	public static void when(boolean test, String message) {
		if (test) {
			throw new MasaException(message);
		}
	}

	public static void when(boolean test, String message, Object... parameters) {
		if (test) {
			throw new MasaException(message, parameters);
		}
	}

	public static MasaException rethrow(Throwable exception) {
		if (exception instanceof Error) {
			throw (Error) exception;
		}
		if (exception instanceof RuntimeException) {
			throw (RuntimeException) exception;
		}
		throw new MasaException(exception);
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

	public MasaException() {
	}

	public MasaException(String message) {
		super(message);
	}

	public MasaException(Throwable cause, String message, Object... parameters) {
		super(MessageFormatter.arrayFormat(message, parameters).getMessage(), cause);
	}

	public MasaException(String message, Object... parameters) {
		super(MessageFormatter.arrayFormat(message, parameters).getMessage());
	}

	public MasaException(Throwable cause) {
		super(cause);
	}

}
