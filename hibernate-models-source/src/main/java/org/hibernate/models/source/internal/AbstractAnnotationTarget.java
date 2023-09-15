/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.source.internal;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import org.hibernate.models.source.spi.AnnotationDescriptor;
import org.hibernate.models.source.spi.AnnotationTarget;
import org.hibernate.models.source.spi.AnnotationUsage;
import org.hibernate.models.source.spi.SourceModelBuildingContext;

/**
 * Basic support for AnnotationTarget.
 *
 * @implNote Immediately resolves all declared annotations
 *
 * @author Steve Ebersole
 */
public abstract class AbstractAnnotationTarget implements AnnotationTarget {
	private final Map<Class<? extends Annotation>, AnnotationUsage<?>> usageMap = new ConcurrentHashMap<>();

	public AbstractAnnotationTarget(
			Annotation[] annotations,
			SourceModelBuildingContext processingContext) {
		AnnotationUsageBuilder.processAnnotations( annotations, this, usageMap::put, processingContext );
	}

	@Override
	public <A extends Annotation> AnnotationUsage<A> getAnnotation(AnnotationDescriptor<A> type) {
		return AnnotationUsageHelper.getAnnotation( type, usageMap );
	}

	@Override
	public <A extends Annotation> List<AnnotationUsage<A>> getRepeatedAnnotations(AnnotationDescriptor<A> type) {
		return AnnotationUsageHelper.getRepeatedAnnotations( type, usageMap );
	}

	@Override
	public <A extends Annotation> void forEachAnnotation(AnnotationDescriptor<A> type, Consumer<AnnotationUsage<A>> consumer) {
		final List<AnnotationUsage<A>> annotations = getRepeatedAnnotations( type );
		if ( annotations == null ) {
			return;
		}
		annotations.forEach( consumer );
	}

	@Override
	public <A extends Annotation> AnnotationUsage<A> getNamedAnnotation(
			AnnotationDescriptor<A> type,
			String matchValue,
			String attributeToMatch) {
		return AnnotationUsageHelper.getNamedAnnotation( type, matchValue, attributeToMatch, usageMap );
	}
}
