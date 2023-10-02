/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.source.internal.standard.annotations;

import org.hibernate.models.source.spi.ClassDetails;
import org.hibernate.models.source.spi.SourceModelBuildingContext;

import org.jboss.jandex.AnnotationValue;

import static org.hibernate.models.source.internal.standard.annotations.ClassValueExtractor.CLASS_EXTRACTOR;

/**
 * Descriptor for Class-valued annotation attributes, handled as {@linkplain ClassDetails}
 *
 * @see AnnotationValue.Kind#CLASS
 *
 * @author Steve Ebersole
 */
public class ClassValueDescriptor extends AbstractCommonValueDescriptor<ClassDetails> {
	public ClassValueDescriptor(String name) {
		super( name );
	}

	@Override
	protected ValueExtractor<ClassDetails> getValueExtractor(SourceModelBuildingContext buildingContext) {
		return CLASS_EXTRACTOR;
	}
}
