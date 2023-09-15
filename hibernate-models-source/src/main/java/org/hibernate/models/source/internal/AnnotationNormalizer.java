/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.source.internal;

import java.lang.annotation.Annotation;

import org.hibernate.models.source.spi.AnnotationDescriptor;
import org.hibernate.models.source.spi.AnnotationTarget;
import org.hibernate.models.source.spi.AnnotationUsage;
import org.hibernate.models.source.spi.SourceModelBuildingContext;

/**
 * ValueNormalizer for handling {@link Annotation} values
 *
 * @author Steve Ebersole
 */
public class AnnotationNormalizer<A extends Annotation> implements ValueNormalizer<A, AnnotationUsage<A>> {
	/**
	 * Singleton access
	 */
	@SuppressWarnings("rawtypes")
	public static final AnnotationNormalizer INSTANCE = new AnnotationNormalizer();

	public static <A extends Annotation> ValueNormalizer<A, AnnotationUsage<A>> singleton() {
		//noinspection unchecked
		return INSTANCE;
	}

	@Override
	public AnnotationUsage<A> normalize(
			A incomingValue,
			AnnotationTarget target,
			SourceModelBuildingContext processingContext) {
		if ( incomingValue == null ) {
			return null;
		}

		//noinspection unchecked
		final AnnotationDescriptor<A> descriptor = (AnnotationDescriptor<A>) processingContext
				.getAnnotationDescriptorRegistry()
				.getDescriptor( incomingValue.annotationType() );
		return new AnnotationUsageImpl<>( incomingValue, descriptor, target, processingContext );
	}
}
