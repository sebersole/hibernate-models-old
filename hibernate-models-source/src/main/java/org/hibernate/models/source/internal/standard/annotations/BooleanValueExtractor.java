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
public class BooleanValueExtractor implements ValueExtractor<Boolean> {
	public static final BooleanValueExtractor BOOLEAN_EXTRACTOR = new BooleanValueExtractor();

	@Override
	public Boolean extractValue(AnnotationValue jandexValue, SourceModelBuildingContext buildingContext) {
		return jandexValue == null ? null : requireValue( jandexValue, buildingContext );
	}

	@Override
	public Boolean requireValue(AnnotationValue jandexValue, SourceModelBuildingContext buildingContext) {
		return jandexValue.asBoolean();
	}

	@Override
	public AnnotationAttributeDescriptor createAttributeDescriptor(
			String name) {
		return new BooleanValueDescriptor( name );
	}
}
