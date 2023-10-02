/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.source.internal.explicit;

import java.lang.annotation.Annotation;

import org.hibernate.models.source.internal.standard.annotations.AnnotationUsageImpl;
import org.hibernate.models.source.internal.standard.annotations.NestedValueDescriptor;
import org.hibernate.models.source.internal.standard.annotations.ValueExtractor;
import org.hibernate.models.source.spi.AnnotationAttributeDescriptor;
import org.hibernate.models.source.spi.AnnotationDescriptor;
import org.hibernate.models.source.spi.AnnotationUsage;
import org.hibernate.models.source.spi.SourceModelBuildingContext;

import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.AnnotationValue;

/**
 * @author Steve Ebersole
 */
public class NestedValueExtractor<A extends Annotation> implements ValueExtractor<AnnotationUsage<A>> {
	private final Class<A> annotationType;
	private AnnotationDescriptor<A> annotationDescriptor;

	public NestedValueExtractor(Class<A> annotationType) {
		this.annotationType = annotationType;
	}

	@Override
	public AnnotationUsage<A> extractValue(
			AnnotationValue jandexValue,
			SourceModelBuildingContext buildingContext) {
		if ( jandexValue == null ) {
			return null;
		}
		return requireValue( jandexValue, buildingContext );
	}

	@Override
	public AnnotationUsage<A> requireValue(
			AnnotationValue jandexValue,
			SourceModelBuildingContext buildingContext) {
		final AnnotationInstance nested = jandexValue.asNested();
		return new AnnotationUsageImpl<>(
				nested,
				resolveAnnotationDescriptor( buildingContext ),
				null,
				buildingContext
		);
	}

	private AnnotationDescriptor<A> resolveAnnotationDescriptor(SourceModelBuildingContext buildingContext) {
		if ( annotationDescriptor == null ) {
			annotationDescriptor = buildingContext
					.getAnnotationDescriptorRegistry()
					.getDescriptor( annotationType );
		}
		return annotationDescriptor;
	}

	@Override
	public AnnotationAttributeDescriptor createAttributeDescriptor(String name) {
		return new NestedValueDescriptor<>( name, annotationDescriptor );
	}
}
