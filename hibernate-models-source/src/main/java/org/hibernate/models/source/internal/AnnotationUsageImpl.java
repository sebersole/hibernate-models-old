/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.source.internal;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.models.internal.CollectionHelper;
import org.hibernate.models.source.spi.AnnotationAttributeDescriptor;
import org.hibernate.models.source.spi.AnnotationAttributeValue;
import org.hibernate.models.source.spi.AnnotationDescriptor;
import org.hibernate.models.source.spi.AnnotationTarget;
import org.hibernate.models.source.spi.AnnotationUsage;
import org.hibernate.models.source.spi.SourceModelBuildingContext;


/**
 * @author Steve Ebersole
 */
public class AnnotationUsageImpl<A extends Annotation> implements AnnotationUsage<A> {
	private final AnnotationDescriptor<A> annotationDescriptor;
	private final AnnotationTarget location;

	private final Map<String, AnnotationAttributeValue<?,?>> valueMap;

	public AnnotationUsageImpl(
			A annotation,
			AnnotationDescriptor<A> annotationDescriptor,
			AnnotationTarget location,
			SourceModelBuildingContext processingContext) {
		this.annotationDescriptor = annotationDescriptor;
		this.location = location;

		this.valueMap = AnnotationUsageBuilder.extractAttributeValues( annotation, annotationDescriptor, location, processingContext );

		processingContext.registerUsage( this );
	}

	public AnnotationUsageImpl(
			AnnotationDescriptor<A> annotationDescriptor,
			AnnotationTarget location,
			Map<String, AnnotationAttributeValue<?,?>> valueMap) {
		this.annotationDescriptor = annotationDescriptor;
		this.location = location;
		this.valueMap = valueMap;
	}

	public AnnotationUsageImpl(
			AnnotationDescriptor<A> annotationDescriptor,
			AnnotationTarget location,
			List<AnnotationAttributeValue<?,?>> valueList) {
		this( annotationDescriptor, location, indexValues( valueList ) );
	}

	@Override
	public AnnotationDescriptor<A> getAnnotationDescriptor() {
		return annotationDescriptor;
	}

	@Override
	public AnnotationTarget getAnnotationTarget() {
		return location;
	}

	@Override
	public <V,W> AnnotationAttributeValue<V,W> getAttributeValue(String name) {
		//noinspection unchecked
		return (AnnotationAttributeValue<V,W>) valueMap.get( name );
	}

	@Override
	public <V,W> AnnotationAttributeValue<V,W> getAttributeValue(AnnotationAttributeDescriptor<A,V,W> attributeDescriptor) {
		return getAttributeValue( attributeDescriptor.getAttributeName() );
	}

	private static Map<String, AnnotationAttributeValue<?,?>> indexValues(List<AnnotationAttributeValue<?,?>> valueList) {
		if ( CollectionHelper.isEmpty( valueList ) ) {
			return Collections.emptyMap();
		}

		final Map<String, AnnotationAttributeValue<?,?>> result = new HashMap<>();
		for ( int i = 0; i < valueList.size(); i++ ) {
			final AnnotationAttributeValue<?,?> value = valueList.get( i );
			result.put( value.getAttributeDescriptor().getAttributeName(), value );
		}
		return result;
	}
}
