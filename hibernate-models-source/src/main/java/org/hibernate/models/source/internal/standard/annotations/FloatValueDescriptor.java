/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.source.internal.standard.annotations;

import org.hibernate.models.source.spi.SourceModelBuildingContext;

import org.jboss.jandex.AnnotationValue;

import static org.hibernate.models.source.internal.standard.annotations.FloatValueExtractor.FLOAT_EXTRACTOR;

/**
 * Descriptor for Float-valued annotation attributes
 *
 * @see AnnotationValue.Kind#FLOAT
 *
 * @author Steve Ebersole
 */
public class FloatValueDescriptor extends AbstractCommonValueDescriptor<Float> {
	public FloatValueDescriptor(String name) {
		super( name );
	}

	@Override
	protected ValueExtractor<Float> getValueExtractor(SourceModelBuildingContext buildingContext) {
		return FLOAT_EXTRACTOR;
	}
}
