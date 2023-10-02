/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.source.internal.standard.annotations;

import org.jboss.jandex.AnnotationValue;

import static org.hibernate.models.source.internal.standard.annotations.BooleanValueExtractor.BOOLEAN_EXTRACTOR;

/**
 * Descriptor for Boolean-valued annotation attributes
 *
 * @see AnnotationValue.Kind#BOOLEAN
 *
 * @author Steve Ebersole
 */
public class BooleanValueDescriptor extends AbstractCommonValueDescriptor<Boolean> {
	public BooleanValueDescriptor(String name) {
		super( name );
	}

	@Override
	protected ValueExtractor<Boolean> getValueExtractor() {
		return BOOLEAN_EXTRACTOR;
	}
}
