/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.source.internal.standard.annotations;

import java.lang.annotation.Annotation;
import java.util.Map;

import org.hibernate.models.source.spi.AnnotationAttributeDescriptor;
import org.hibernate.models.source.spi.AnnotationAttributeValue;
import org.hibernate.models.source.spi.AnnotationDescriptor;
import org.hibernate.models.source.spi.AnnotationTarget;
import org.hibernate.models.source.spi.AnnotationUsage;
import org.hibernate.models.source.spi.SourceModelBuildingContext;

import org.jboss.jandex.AnnotationInstance;

/**
 * AnnotationUsage implementation based on the Jandex AnnotationInstance
 *
 * @author Steve Ebersole
 */
public class AnnotationUsageImpl<A extends Annotation> implements AnnotationUsage<A> {
	private final AnnotationDescriptor<A> annotationDescriptor;
	private final AnnotationTarget annotationTarget;

	private final Map<String, AnnotationAttributeValue<?>> attributeValueMap;

	public AnnotationUsageImpl(
			AnnotationInstance annotationInstance,
			AnnotationDescriptor<A> annotationDescriptor,
			AnnotationTarget annotationTarget,
			SourceModelBuildingContext processingContext) {
		this.annotationTarget = annotationTarget;
		this.annotationDescriptor = annotationDescriptor;

		this.attributeValueMap = AnnotationUsageBuilder.extractAttributeValues(
				annotationInstance,
				annotationDescriptor,
				annotationTarget,
				processingContext
		);
	}

	@Override
	public AnnotationDescriptor<A> getAnnotationDescriptor() {
		return annotationDescriptor;
	}

	@Override
	public AnnotationTarget getAnnotationTarget() {
		return annotationTarget;
	}

	@Override
	public <V> AnnotationAttributeValue<V> getAttributeValue(String name) {
		//noinspection unchecked
		return (AnnotationAttributeValue<V>) attributeValueMap.get( name );
	}

	@Override
	public <V> AnnotationAttributeValue<V> getAttributeValue(AnnotationAttributeDescriptor attributeDescriptor) {
		//noinspection unchecked
		return (AnnotationAttributeValue<V>) attributeValueMap.get( attributeDescriptor.getAttributeName() );
	}
}