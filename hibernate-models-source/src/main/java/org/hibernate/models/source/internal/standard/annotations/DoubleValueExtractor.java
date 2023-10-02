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
public class DoubleValueExtractor implements ValueExtractor<Double> {
	public static final DoubleValueExtractor DOUBLE_EXTRACTOR = new DoubleValueExtractor();

	@Override
	public Double extractValue(
			AnnotationValue jandexValue,
			SourceModelBuildingContext buildingContext) {
		if ( jandexValue == null ) {
			return null;
		}
		return requireValue( jandexValue, buildingContext );
	}

	@Override
	public Double requireValue(AnnotationValue jandexValue, SourceModelBuildingContext buildingContext) {
		return jandexValue.asDouble();
	}

	@Override
	public AnnotationAttributeDescriptor createAttributeDescriptor(
			String name) {
		return new DoubleValueDescriptor( name );
	}
}
