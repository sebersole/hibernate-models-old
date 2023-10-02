/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.source.internal.standard.annotations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hibernate.models.internal.CollectionHelper;
import org.hibernate.models.source.spi.AnnotationAttributeValue;
import org.hibernate.models.source.spi.AnnotationTarget;
import org.hibernate.models.source.spi.SourceModelBuildingContext;

import org.jboss.jandex.AnnotationValue;

/**
 * Descriptor for array-valued annotation attributes
 *
 * @see AnnotationValue.Kind#ARRAY
 *
 * @author Steve Ebersole
 */
public class ArrayValueDescriptor<C> extends AbstractValueDescriptor<List<C>> {
	private final ValueExtractor<C> componentDescriptor;

	public ArrayValueDescriptor(String name, ValueExtractor<C> componentDescriptor) {
		super( name );
		this.componentDescriptor = componentDescriptor;
	}

	@Override
	protected AnnotationAttributeValue<List<C>> createImplicitWrapper(
			AnnotationValue implicitValue,
			AnnotationTarget target,
			SourceModelBuildingContext buildingContext) {
		final List<AnnotationValue> rawValues = implicitValue.asArrayList();
		if ( CollectionHelper.isEmpty( rawValues ) ) {
			return new AttributeValueImpl<>( this, Collections.emptyList(), true );
		}

		final List<C> wrappedValues = new ArrayList<>( rawValues.size() );
		for ( int i = 0; i < rawValues.size(); i++ ) {
			wrappedValues.add( componentDescriptor.extractValue(
					rawValues.get( i ),
					buildingContext
			) );
		}
		return new AttributeValueImpl<>( this, wrappedValues, true );
	}

	@Override
	protected AnnotationAttributeValue<List<C>> createExplicitWrapper(
			AnnotationValue explicitValue,
			AnnotationTarget target,
			SourceModelBuildingContext buildingContext) {
		final List<AnnotationValue> rawValues = explicitValue.asArrayList();
		final List<C> wrappedValues = new ArrayList<>( rawValues.size() );
		for ( int i = 0; i < rawValues.size(); i++ ) {
			wrappedValues.add( componentDescriptor.extractValue(
					rawValues.get( i ),
					buildingContext
			) );
		}
		return new AttributeValueImpl<>( this, wrappedValues, false );
	}
}
