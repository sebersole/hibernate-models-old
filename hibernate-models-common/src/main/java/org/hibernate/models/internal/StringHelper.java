package org.hibernate.models.internal;

/**
 * @author Steve Ebersole
 */
public class StringHelper {

	public static boolean isEmpty(String string) {
		return string == null || string.isEmpty();
	}

	public static boolean isNotEmpty(String string) {
		return string != null && !string.isEmpty();
	}

	public static boolean isBlank(String string) {
		return string == null || string.isBlank();
	}

	public static String nullIfEmpty(String value) {
		return isEmpty( value ) ? null : value;
	}
}
