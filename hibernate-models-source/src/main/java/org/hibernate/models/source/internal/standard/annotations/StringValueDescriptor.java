/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.source.internal.standard.annotations;

import org.hibernate.models.source.spi.SourceModelBuildingContext;

import org.jboss.jandex.AnnotationValue;

import static org.hibernate.models.source.internal.standard.annotations.StringValueExtractor.STRING_EXTRACTOR;

/**
 * Descriptor for String-valued annotation attributes
 *
 * @see AnnotationValue.Kind#STRING
 *
 * @author Steve Ebersole
 */
public class StringValueDescriptor extends AbstractCommonValueDescriptor<String> {
	public StringValueDescriptor(String name) {
		super( name );
	}

	@Override
	protected ValueExtractor<String> getValueExtractor(SourceModelBuildingContext buildingContext) {
		return STRING_EXTRACTOR;
	}
}
