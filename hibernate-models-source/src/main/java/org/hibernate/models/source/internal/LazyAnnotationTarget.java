/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.source.internal;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.hibernate.models.source.spi.AnnotationDescriptor;
import org.hibernate.models.source.spi.AnnotationTarget;
import org.hibernate.models.source.spi.AnnotationUsage;
import org.hibernate.models.source.spi.SourceModelBuildingContext;

/**
 * AnnotationTarget where we know the annotations up front, but
 * want to delay processing them until (unless!) they are needed
 *
 * @author Steve Ebersole
 */
public abstract class LazyAnnotationTarget implements AnnotationTarget {
	private final Supplier<Annotation[]> annotationSupplier;
	private final SourceModelBuildingContext processingContext;

	private Map<Class<? extends Annotation>, AnnotationUsage<?>> usagesMap;

	public LazyAnnotationTarget(
			Supplier<Annotation[]> annotationSupplier,
			SourceModelBuildingContext processingContext) {
		this.annotationSupplier = annotationSupplier;
		this.processingContext = processingContext;
	}

	protected SourceModelBuildingContext getProcessingContext() {
		return processingContext;
	}

	@Override
	public <A extends Annotation> AnnotationUsage<A> getAnnotation(AnnotationDescriptor<A> type) {
		return AnnotationUsageHelper.getAnnotation( type, resolveUsagesMap() );
	}

	@Override
	public <A extends Annotation> List<AnnotationUsage<A>> getRepeatedAnnotations(AnnotationDescriptor<A> type) {
		return AnnotationUsageHelper.getRepeatedAnnotations( type, resolveUsagesMap() );
	}

	private Map<Class<? extends Annotation>, AnnotationUsage<?>> resolveUsagesMap() {
		if ( usagesMap == null ) {
			usagesMap = buildUsagesMap();
		}
		return usagesMap;
	}

	private Map<Class<? extends Annotation>, AnnotationUsage<?>> buildUsagesMap() {
		final Map<Class<? extends Annotation>, AnnotationUsage<?>> result = new HashMap<>();
		AnnotationUsageBuilder.processAnnotations(
				annotationSupplier.get(),
				this,
				result::put,
				processingContext
		);
		return result;
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
		return AnnotationUsageHelper.getNamedAnnotation( type, matchValue, attributeToMatch, resolveUsagesMap() );
	}


}
