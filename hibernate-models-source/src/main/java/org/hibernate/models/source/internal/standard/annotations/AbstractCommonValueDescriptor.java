/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.source.internal.standard.annotations;

import org.hibernate.models.source.spi.AnnotationAttributeValue;
import org.hibernate.models.source.spi.AnnotationTarget;
import org.hibernate.models.source.spi.SourceModelBuildingContext;

import org.jboss.jandex.AnnotationValue;

/**
 * @author Steve Ebersole
 */
public abstract class AbstractCommonValueDescriptor<W> extends AbstractValueDescriptor<W> {
	public AbstractCommonValueDescriptor(String name) {
		super( name );
	}

	protected abstract ValueExtractor<W> getValueExtractor(SourceModelBuildingContext buildingContext);

	@Override
	protected final AnnotationAttributeValue<W> createImplicitWrapper(
			AnnotationValue implicitValue,
			AnnotationTarget target,
			SourceModelBuildingContext buildingContext) {
		return new AttributeValueImpl<>(
				this,
				getValueExtractor( buildingContext ).extractValue( implicitValue, buildingContext ),
				true
		);
	}

	@Override
	protected final AnnotationAttributeValue<W> createExplicitWrapper(
			AnnotationValue explicitValue,
			AnnotationTarget target,
			SourceModelBuildingContext buildingContext) {
		return new AttributeValueImpl<>(
				this,
				getValueExtractor( buildingContext ).extractValue( explicitValue, buildingContext ),
				false
		);
	}
}
