/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.source.internal.standard.annotations;

import org.hibernate.models.source.spi.AnnotationAttributeDescriptor;
import org.hibernate.models.source.spi.SourceModelBuildingContext;

import org.jboss.jandex.AnnotationValue;

/**
 * @author Steve Ebersole
 */
public class ByteValueExtractor implements ValueExtractor<Byte> {
	public static final ByteValueExtractor BYTE_EXTRACTOR = new ByteValueExtractor();

	@Override
	public Byte extractValue(AnnotationValue jandexValue, SourceModelBuildingContext buildingContext) {
		return jandexValue == null ? null : requireValue( jandexValue, buildingContext );
	}

	@Override
	public Byte requireValue(AnnotationValue jandexValue, SourceModelBuildingContext buildingContext) {
		return jandexValue.asByte();
	}

	@Override
	public AnnotationAttributeDescriptor createAttributeDescriptor(String name) {
		return new ByteValueDescriptor( name );
	}
}
