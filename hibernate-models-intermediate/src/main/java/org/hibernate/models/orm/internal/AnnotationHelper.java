/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.orm.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Locale;
import java.util.function.Consumer;

import org.hibernate.models.orm.spi.HibernateAnnotations;
import org.hibernate.models.orm.spi.IdentifiableTypeMetadata;
import org.hibernate.models.orm.spi.JpaAnnotations;
import org.hibernate.models.source.AnnotationAccessException;
import org.hibernate.models.source.spi.AnnotationDescriptor;
import org.hibernate.models.source.spi.AnnotationUsage;

import static org.hibernate.models.source.internal.AnnotationDescriptorBuilder.extractAttributeDescriptors;

/**
 * @author Steve Ebersole
 */
public class AnnotationHelper {

	/**
	 * Look for the given annotation on the passed type as well as any of its super-types.
	 * Similar concept as {@link java.lang.annotation.Inherited}, but without needing the
	 * physical {@link java.lang.annotation.Inherited} meta-annotation.
	 * <p/>
	 * This is useful for JPA annotations which can be used in inheritance situations but
	 * are never defined with {@link java.lang.annotation.Inherited}
	 */
	public static <A extends Annotation> AnnotationUsage<A> findInheritedAnnotation(
			IdentifiableTypeMetadata base,
			AnnotationDescriptor<A> annotationDescriptor) {
		final AnnotationUsage<A> annotation = base.getClassDetails().getAnnotation( annotationDescriptor );
		if ( annotation != null ) {
			return annotation;
		}

		if ( ! annotationDescriptor.isInherited() ) {
			// we have a case where the annotation is not marked as `@Inherited` (like JPA's annotations),
			// so manually check up the hierarchy.
			// todo (annotation-source) : do we need to manually check meta-annotations?

			if ( base.getSuperType() != null ) {
				return findInheritedAnnotation( base.getSuperType(), annotationDescriptor );
			}
		}

		return null;
	}

	public static <A extends Annotation> AnnotationDescriptor<A> createOrmDescriptor(Class<A> javaType) {
		return createOrmDescriptor( javaType, null );
	}

	public static <A extends Annotation> AnnotationDescriptor<A> createOrmDescriptor(
			Class<A> javaType,
			AnnotationDescriptor<?> repeatableContainer) {
		assert javaType != null;

		return new OrmAnnotationDescriptorImpl<>(
				javaType,
				extractAttributeDescriptors( javaType ),
				repeatableContainer
		);
	}

	public static void forEachOrmAnnotation(Consumer<AnnotationDescriptor<?>> consumer) {
		forEachOrmAnnotation( JpaAnnotations.class, consumer );
		forEachOrmAnnotation( HibernateAnnotations.class, consumer );
	}

	private static void forEachOrmAnnotation(Class<?> declarer, Consumer<AnnotationDescriptor<?>> consumer) {
		for ( Field field : declarer.getFields() ) {
			if ( AnnotationDescriptor.class.equals( field.getType() ) ) {
				try {
					consumer.accept( (AnnotationDescriptor<?>) field.get( null ) );
				}
				catch (IllegalAccessException e) {
					throw new AnnotationAccessException(
							String.format(
									Locale.ROOT,
									"Unable to access standard annotation descriptor field - %s",
									field.getName()
							),
							e
					);
				}
			}
		}
	}

}
