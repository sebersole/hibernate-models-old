/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.source.internal.standard.annotations;

import org.hibernate.models.source.spi.SourceModelBuildingContext;

import org.jboss.jandex.AnnotationValue;

import static org.hibernate.models.source.internal.standard.annotations.PassthruValueExtractor.PASSTHRU_EXTRACTOR;

/**
 * Descriptor for annotation attributes whose value type is not pre-known
 *
 * @see AnnotationValue.Kind#UNKNOWN
 *
 * @author Steve Ebersole
 */
public class PassthruValueDescriptor<V> extends AbstractCommonValueDescriptor<V> {
	public PassthruValueDescriptor(String name) {
		super( name );
	}

	@Override
	protected ValueExtractor<V> getValueExtractor(SourceModelBuildingContext buildingContext) {
		//noinspection unchecked
		return (ValueExtractor<V>) PASSTHRU_EXTRACTOR;
	}
}
