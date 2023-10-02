/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.source.internal.standard.annotations;

import java.lang.annotation.Annotation;

import org.hibernate.models.source.spi.AnnotationDescriptor;
import org.hibernate.models.source.spi.AnnotationUsage;
import org.hibernate.models.source.spi.SourceModelBuildingContext;

import org.jboss.jandex.AnnotationValue;

/**
 * Descriptor for Annotation-valued annotation attributes
 *
 * @see AnnotationValue.Kind#NESTED
 *
 * @author Steve Ebersole
 */
public class NestedValueDescriptor<A extends Annotation>
		extends AbstractCommonValueDescriptor<AnnotationUsage<A>> {
	private final Class<A> annotationType;
	private NestedValueExtractor<A> extractor;

	public NestedValueDescriptor(String name, AnnotationDescriptor<A> descriptor) {
		super( name );
		this.annotationType = descriptor.getAnnotationType();
		this.extractor = new NestedValueExtractor<>( descriptor );
	}

	public NestedValueDescriptor(String name, Class<A> annotationType) {
		super( name );
		this.annotationType = annotationType;
	}

	@Override
	protected ValueExtractor<AnnotationUsage<A>> getValueExtractor(SourceModelBuildingContext buildingContext) {
		if ( extractor == null ) {
			extractor = new NestedValueExtractor<>( buildingContext.getAnnotationDescriptorRegistry().getDescriptor( annotationType ) );
		}
		return extractor;
	}
}
