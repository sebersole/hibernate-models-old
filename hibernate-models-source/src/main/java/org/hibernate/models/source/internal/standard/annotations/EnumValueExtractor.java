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
public class EnumValueExtractor<E extends Enum<E>> implements ValueExtractor<E> {
	private final Class<E> enumClass;

	public EnumValueExtractor(Class<E> enumClass) {
		this.enumClass = enumClass;
	}

	@Override
	public E extractValue(
			AnnotationValue jandexValue,
			SourceModelBuildingContext buildingContext) {
		if ( jandexValue == null ) {
			return null;
		}
		return requireValue( jandexValue, buildingContext );
	}

	@Override
	public E requireValue(AnnotationValue jandexValue, SourceModelBuildingContext buildingContext) {
		final String enumName = jandexValue.asEnum();
		return Enum.valueOf( enumClass, enumName );
	}

	@Override
	public AnnotationAttributeDescriptor createAttributeDescriptor(
			String name) {
		return new EnumValueDescriptor<>( name, enumClass );
	}
}
