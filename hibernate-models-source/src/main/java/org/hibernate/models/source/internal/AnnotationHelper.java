/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.source.internal;

import java.lang.annotation.Annotation;
import java.lang.annotation.Inherited;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.EnumSet;
import java.util.Locale;

import org.hibernate.models.source.AnnotationAccessException;
import org.hibernate.models.source.spi.AnnotationTarget;

/**
 * Helper for dealing with actual {@link Annotation} references
 *
 * @author Steve Ebersole
 */
public class AnnotationHelper {
	private AnnotationHelper() {
		// disallow direct instantiation
	}

	/**
	 * Extract a "raw" value directly from an {@linkplain Annotation annotation}
	 */
	public static <A extends Annotation, T> T extractRawAttributeValue(A annotation, String attributeName) {
		try {
			final Method method = annotation.getClass().getDeclaredMethod( attributeName );
			//noinspection unchecked
			return (T) method.invoke( annotation );
		}
		catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
			throw new AnnotationAccessException(
					String.format(
							Locale.ROOT,
							"Unable to extract attribute value : %s.%s",
							annotation.annotationType().getName(),
							attributeName
					),
					e
			);
		}
	}

	public static <A extends Annotation> boolean isInherited(Class<A> annotationType) {
		return annotationType.isAnnotationPresent( Inherited.class );
	}

	public static <A extends Annotation> EnumSet<AnnotationTarget.Kind> extractTargets(Class<A> annotationType) {
		return AnnotationTarget.Kind.from( annotationType.getAnnotation( Target.class ) );
	}
}
