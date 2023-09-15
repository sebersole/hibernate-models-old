package org.hibernate.models.internal;

/**
 * @author Steve Ebersole
 */
public class ArrayHelper {
	public static boolean isEmpty(Object[] array) {
		return array == null || array.length == 0;
	}

	public static boolean isNotEmpty(Object[] array) {
		return array != null && array.length > 0;
	}
}
