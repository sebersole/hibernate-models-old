/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.source.internal.standard.annotations;

import org.hibernate.models.source.spi.SourceModelBuildingContext;

import org.jboss.jandex.AnnotationValue;

/**
 * Descriptor for Enum-valued annotation attributes
 *
 * @see AnnotationValue.Kind#ENUM
 *
 * @author Steve Ebersole
 */
public class EnumValueDescriptor<E extends Enum<E>> extends AbstractCommonValueDescriptor<E> {
	private final EnumValueExtractor<E> extractor;

	public EnumValueDescriptor(String name, Class<E> enumClass) {
		super( name );
		this.extractor = new EnumValueExtractor<>( enumClass );
	}

	@Override
	protected ValueExtractor<E> getValueExtractor(SourceModelBuildingContext buildingContext) {
		return extractor;
	}
}
