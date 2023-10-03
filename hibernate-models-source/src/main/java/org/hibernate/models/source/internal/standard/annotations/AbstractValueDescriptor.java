/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.source.internal.standard.annotations;

import java.util.Locale;

import org.hibernate.models.source.internal.AnnotationValueWrapper;
import org.hibernate.models.source.spi.AnnotationAttributeDescriptor;
import org.hibernate.models.source.spi.AnnotationAttributeValue;
import org.hibernate.models.source.spi.AnnotationTarget;
import org.hibernate.models.source.spi.SourceModelBuildingContext;

import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.AnnotationValue;

/**
 * @author Steve Ebersole
 */
public abstract class AbstractValueDescriptor<W> implements AnnotationAttributeDescriptor, AnnotationValueWrapper<W> {
	private final String name;

	public AbstractValueDescriptor(String name) {
		this.name = name;
	}

	@Override
	public String getAttributeName() {
		return name;
	}

	@Override
	public AnnotationAttributeValue<W> wrapValue(
			AnnotationInstance annotationInstance,
			AnnotationAttributeDescriptor attributeDescriptor,
			AnnotationTarget target,
			SourceModelBuildingContext buildingContext) {
		final AnnotationValue explicitValue = annotationInstance.value( attributeDescriptor.getAttributeName() );
		if ( explicitValue == null ) {
			// this attribute was not specified, use the default
			final AnnotationValue implicitValue = annotationInstance.valueWithDefault(
					buildingContext.getJandexIndex(),
					attributeDescriptor.getAttributeName()
			);
			return createImplicitWrapper( implicitValue, target, buildingContext );
		}
		return createExplicitWrapper( explicitValue, target, buildingContext );
	}

	protected abstract AnnotationAttributeValue<W> createImplicitWrapper(
			AnnotationValue implicitValue,
			AnnotationTarget target,
			SourceModelBuildingContext buildingContext);

	protected abstract AnnotationAttributeValue<W> createExplicitWrapper(
			AnnotationValue explicitValue,
			AnnotationTarget target,
			SourceModelBuildingContext buildingContext);

	@Override
	public String toString() {
		return String.format(
				Locale.ROOT,
				"AnnotationAttributeDescriptor(%s)",
				name
		);
	}
}
