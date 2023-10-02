/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.source.internal.standard.annotations;

import org.hibernate.models.source.spi.AnnotationAttributeDescriptor;
import org.hibernate.models.source.spi.ClassDetails;
import org.hibernate.models.source.spi.SourceModelBuildingContext;

import org.jboss.jandex.AnnotationValue;
import org.jboss.jandex.DotName;
import org.jboss.jandex.Type;

/**
 * @author Steve Ebersole
 */
public class ClassValueExtractor implements ValueExtractor<ClassDetails> {
	public static final ClassValueExtractor CLASS_EXTRACTOR = new ClassValueExtractor();

	@Override
	public ClassDetails extractValue(
			AnnotationValue jandexValue,
			SourceModelBuildingContext buildingContext) {
		if ( jandexValue == null ) {
			return null;
		}
		return requireValue( jandexValue, buildingContext );
	}

	@Override
	public ClassDetails requireValue(AnnotationValue jandexValue, SourceModelBuildingContext buildingContext) {
		final Type classType = jandexValue.asClass();
		final DotName className = classType.name();
		return buildingContext.getClassDetailsRegistry().resolveClassDetails( className.toString() );
	}

	@Override
	public AnnotationAttributeDescriptor createAttributeDescriptor(
			String name) {
		return new ClassValueDescriptor( name );
	}
}
