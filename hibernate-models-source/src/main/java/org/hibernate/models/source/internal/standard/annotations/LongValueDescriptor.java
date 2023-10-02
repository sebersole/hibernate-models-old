/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.source.internal.standard.annotations;

import org.jboss.jandex.AnnotationValue;

import static org.hibernate.models.source.internal.standard.annotations.LongValueExtractor.LONG_EXTRACTOR;

/**
 * Descriptor for Long-valued annotation attributes
 *
 * @see AnnotationValue.Kind#LONG
 *
 * @author Steve Ebersole
 */
public class LongValueDescriptor extends AbstractCommonValueDescriptor<Long> {
	public LongValueDescriptor(String name) {
		super( name );
	}

	@Override
	protected ValueExtractor<Long> getValueExtractor() {
		return LONG_EXTRACTOR;
	}
}
