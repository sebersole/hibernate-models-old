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
public interface ValueExtractor<N> {
	N extractValue(AnnotationValue jandexValue, SourceModelBuildingContext buildingContext);
	N requireValue(AnnotationValue jandexValue, SourceModelBuildingContext buildingContext);

	AnnotationAttributeDescriptor createAttributeDescriptor(String name);
}
