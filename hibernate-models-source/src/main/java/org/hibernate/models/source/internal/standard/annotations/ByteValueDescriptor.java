/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.source.internal.standard.annotations;

import org.jboss.jandex.AnnotationValue;

import static org.hibernate.models.source.internal.standard.annotations.ByteValueExtractor.BYTE_EXTRACTOR;

/**
 * Descriptor for Byte-valued annotation attributes
 *
 * @see AnnotationValue.Kind#BYTE
 *
 * @author Steve Ebersole
 */
public class ByteValueDescriptor extends AbstractCommonValueDescriptor<Byte> {
	public ByteValueDescriptor(String name) {
		super( name );
	}

	@Override
	protected ValueExtractor<Byte> getValueExtractor() {
		return BYTE_EXTRACTOR;
	}
}
