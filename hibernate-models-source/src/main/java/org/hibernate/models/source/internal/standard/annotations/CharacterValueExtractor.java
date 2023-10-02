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
public class CharacterValueExtractor implements ValueExtractor<Character> {
	public static final CharacterValueExtractor CHARACTER_EXTRACTOR = new CharacterValueExtractor();

	@Override
	public Character extractValue(AnnotationValue jandexValue, SourceModelBuildingContext buildingContext) {
		return jandexValue == null ? null : requireValue( jandexValue, buildingContext );
	}

	@Override
	public Character requireValue(AnnotationValue jandexValue, SourceModelBuildingContext buildingContext) {
		return jandexValue.asChar();
	}

	@Override
	public AnnotationAttributeDescriptor createAttributeDescriptor(
			String name) {
		return new CharacterValueDescriptor( name );
	}
}
