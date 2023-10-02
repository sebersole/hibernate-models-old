/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.source.internal.standard;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.hibernate.models.source.internal.AnnotationUsageHelper;
import org.hibernate.models.source.spi.AnnotationDescriptor;
import org.hibernate.models.source.spi.AnnotationTarget;
import org.hibernate.models.source.spi.AnnotationUsage;
import org.hibernate.models.source.spi.SourceModelBuildingContext;

import static org.hibernate.models.source.internal.standard.annotations.AnnotationUsageBuilder.buildUsagesMap;

/**
 * @author Steve Ebersole
 */
public abstract class AbstractAnnotationTarget implements AnnotationTarget {
	private final SourceModelBuildingContext buildingContext;
	private Map<Class<? extends Annotation>, AnnotationUsage<?>> usagesMap;

	public AbstractAnnotationTarget(SourceModelBuildingContext buildingContext) {
		this.buildingContext = buildingContext;
	}

	/**
	 * The Jandex AnnotationTarget we can use to read the AnnotationInstance from
	 * which to build the {@linkplain #getUsagesMap() AnnotationUsage map}
	 */
	protected abstract org.jboss.jandex.AnnotationTarget getJandexAnnotationTarget();

	protected Map<Class<? extends Annotation>, AnnotationUsage<?>> getUsagesMap() {
		if ( usagesMap == null ) {
			usagesMap = buildUsagesMap( getJandexAnnotationTarget(), this, buildingContext );
		}
		return usagesMap;
	}

	protected SourceModelBuildingContext getBuildingContext() {
		return buildingContext;
	}

	@Override
	public <A extends Annotation> AnnotationUsage<A> getAnnotation(AnnotationDescriptor<A> type) {
		return AnnotationUsageHelper.getAnnotation( type, getUsagesMap() );
	}

	@Override
	public <A extends Annotation> AnnotationUsage<A> getAnnotation(Class<A> type) {
		return getAnnotation( buildingContext.getAnnotationDescriptorRegistry().getDescriptor( type ) );
	}

	@Override
	public <A extends Annotation> List<AnnotationUsage<A>> getRepeatedAnnotations(AnnotationDescriptor<A> type) {
		return AnnotationUsageHelper.getRepeatedAnnotations( type, getUsagesMap() );
	}

	@Override
	public <A extends Annotation> List<AnnotationUsage<A>> getRepeatedAnnotations(Class<A> type) {
		return getRepeatedAnnotations( buildingContext.getAnnotationDescriptorRegistry().getDescriptor( type ) );
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
	public <A extends Annotation> void forEachAnnotation(Class<A> type, Consumer<AnnotationUsage<A>> consumer) {
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
		return AnnotationUsageHelper.getNamedAnnotation( type, matchValue, attributeToMatch, getUsagesMap() );
	}
	@Override
	public <A extends Annotation> AnnotationUsage<A> getNamedAnnotation(
			Class<A> type,
			String matchValue,
			String attributeToMatch) {
		return getNamedAnnotation(
				buildingContext.getAnnotationDescriptorRegistry().getDescriptor( type ),
				matchValue,
				attributeToMatch
		);
	}
}
