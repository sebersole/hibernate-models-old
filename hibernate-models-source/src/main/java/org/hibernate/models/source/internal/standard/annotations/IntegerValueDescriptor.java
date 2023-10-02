/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.source.internal.standard.annotations;

import org.jboss.jandex.AnnotationValue;

import static org.hibernate.models.source.internal.standard.annotations.IntegerValueExtractor.INTEGER_EXTRACTOR;

/**
 * Descriptor for Integer-valued annotation attributes
 *
 * @see AnnotationValue.Kind#INTEGER
 *
 * @author Steve Ebersole
 */
public class IntegerValueDescriptor extends AbstractCommonValueDescriptor<Integer> {
	public IntegerValueDescriptor(String name) {
		super( name );
	}

	@Override
	protected ValueExtractor<Integer> getValueExtractor() {
		return INTEGER_EXTRACTOR;
	}
}
