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
	private final NestedValueExtractor<A> extractor;

	public NestedValueDescriptor(String name, AnnotationDescriptor<A> descriptor) {
		super( name );
		this.extractor = new NestedValueExtractor<>( descriptor );
	}

	@Override
	protected ValueExtractor<AnnotationUsage<A>> getValueExtractor() {
		return extractor;
	}
}
