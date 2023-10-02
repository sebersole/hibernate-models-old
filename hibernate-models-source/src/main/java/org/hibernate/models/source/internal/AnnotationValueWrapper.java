/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.source.internal;

import org.hibernate.models.source.spi.AnnotationAttributeDescriptor;
import org.hibernate.models.source.spi.AnnotationAttributeValue;
import org.hibernate.models.source.spi.AnnotationTarget;
import org.hibernate.models.source.spi.SourceModelBuildingContext;

import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.AnnotationValue;

/**
 * Contract for converting {@linkplain AnnotationValue} references into
 * {@linkplain AnnotationAttributeValue} references
 *
 * @author Steve Ebersole
 */
public interface AnnotationValueWrapper<W> {
	/**
	 * Given a Jandex {@linkplain AnnotationValue}, create a {@linkplain AnnotationAttributeValue value wrapper}.
	 */
	AnnotationAttributeValue<W> wrapValue(
			AnnotationInstance annotationInstance,
			AnnotationAttributeDescriptor attributeDescriptor,
			AnnotationTarget target,
			SourceModelBuildingContext processingContext);
}
