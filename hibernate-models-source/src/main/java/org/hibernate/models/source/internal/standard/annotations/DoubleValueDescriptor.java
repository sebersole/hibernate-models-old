/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.source.internal.standard.annotations;

import org.jboss.jandex.AnnotationValue;

import static org.hibernate.models.source.internal.standard.annotations.DoubleValueExtractor.DOUBLE_EXTRACTOR;

/**
 * Descriptor for Double-valued annotation attributes
 *
 * @see AnnotationValue.Kind#DOUBLE
 *
 * @author Steve Ebersole
 */
public class DoubleValueDescriptor extends AbstractCommonValueDescriptor<Double> {
	public DoubleValueDescriptor(String name) {
		super( name );
	}

	@Override
	protected ValueExtractor<Double> getValueExtractor() {
		return DOUBLE_EXTRACTOR;
	}
}
