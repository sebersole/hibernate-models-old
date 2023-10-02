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
public class IntegerValueExtractor implements ValueExtractor<Integer> {
	public static final IntegerValueExtractor INTEGER_EXTRACTOR = new IntegerValueExtractor();

	@Override
	public Integer extractValue(
			AnnotationValue jandexValue,
			SourceModelBuildingContext buildingContext) {
		if ( jandexValue == null ) {
			return null;
		}
		return requireValue( jandexValue, buildingContext );
	}

	@Override
	public Integer requireValue(AnnotationValue jandexValue, SourceModelBuildingContext buildingContext) {
		return jandexValue.asInt();
	}

	@Override
	public AnnotationAttributeDescriptor createAttributeDescriptor(
			String name) {
		return new IntegerValueDescriptor( name );
	}
}
