/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
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

	public static boolean isNotBlank(String string) {
		return string != null && !string.isBlank();
	}

	public static String nullIfEmpty(String value) {
		return isEmpty( value ) ? null : value;
	}

	public static String classNameToResourceName(String className) {
		return className.replace( '.', '/' ) + ".class";
	}
}
