/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.source.internal.standard.annotations;

import org.jboss.jandex.AnnotationValue;

import static org.hibernate.models.source.internal.standard.annotations.ShortValueExtractor.SHORT_EXTRACTOR;

/**
 * Descriptor for Short-valued annotation attributes
 *
 * @see AnnotationValue.Kind#SHORT
 *
 * @author Steve Ebersole
 */
public class ShortValueDescriptor extends AbstractCommonValueDescriptor<Short> {
	public ShortValueDescriptor(String name) {
		super( name );
	}

	@Override
	protected ValueExtractor<Short> getValueExtractor() {
		return SHORT_EXTRACTOR;
	}
}
