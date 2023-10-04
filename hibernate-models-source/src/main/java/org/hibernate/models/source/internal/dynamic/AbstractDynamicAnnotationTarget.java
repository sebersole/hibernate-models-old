/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.source.internal.dynamic;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.hibernate.models.source.AnnotationAccessException;
import org.hibernate.models.source.internal.AnnotationUsageHelper;
import org.hibernate.models.source.spi.AnnotationDescriptor;
import org.hibernate.models.source.spi.AnnotationUsage;
import org.hibernate.models.source.spi.SourceModelBuildingContext;

import static org.hibernate.models.source.spi.AnnotationAttributeDescriptor.VALUE;

/**
 * Implementation of  AnnotationTarget where the annotations are not known up
 * front; rather, they are {@linkplain  #apply(AnnotationUsage) applied} later
 *
 * @author Steve Ebersole
 */
public abstract class AbstractDynamicAnnotationTarget implements DynamicAnnotationTarget {
	private final SourceModelBuildingContext buildingContext;
	private final Map<Class<? extends Annotation>,AnnotationUsage<?>> usagesMap = new HashMap<>();

	public AbstractDynamicAnnotationTarget(SourceModelBuildingContext buildingContext) {
		this.buildingContext = buildingContext;
	}

	@Override
	public <X extends Annotation> void apply(List<AnnotationUsage<X>> annotationUsages) {
		// todo (models) : handle meta-annotations
		annotationUsages.forEach( this::apply );
	}

	/**
	 * Applies the given {@code annotationUsage} to this target.
	 *
	 * @apiNote
	 * todo (models) : It is undefined currently what happens if the
	 * 		{@link AnnotationUsage#getAnnotationDescriptor() annotation type} is
	 * 		already applied on this target.
	 */
	@Override
	public <X extends Annotation> void apply(AnnotationUsage<X> annotationUsage) {
		final AnnotationDescriptor<?> annotationDescriptor = annotationUsage.getAnnotationDescriptor();
		final Class<? extends Annotation> annotationJavaType = annotationDescriptor.getAnnotationType();

		final AnnotationUsage<?> previous = usagesMap.put( annotationJavaType, annotationUsage );

		if ( previous != null ) {
			// todo (models) : ignore?  log?  exception?
		}
	}

	@Override
	public <A extends Annotation> List<AnnotationUsage<A>> getRepeatedAnnotations(AnnotationDescriptor<A> type) {
		return AnnotationUsageHelper.getRepeatedAnnotations( type, usagesMap );
	}

	@Override
	public <A extends Annotation> List<AnnotationUsage<A>> getRepeatedAnnotations(Class<A> type) {
		return getRepeatedAnnotations(
				buildingContext.getAnnotationDescriptorRegistry().getDescriptor( type )
		);
	}

	@Override
	public <A extends Annotation> void forEachAnnotation(
			AnnotationDescriptor<A> type,
			Consumer<AnnotationUsage<A>> consumer) {
		if ( type.getRepeatableContainer() != null ) {
			// instead we want to look for the container annotation
			final Class<? extends Annotation> annotationJavaType = type.getRepeatableContainer().getAnnotationType();
			final AnnotationUsage<?> containerUsage = usagesMap.get( annotationJavaType );
			if ( containerUsage != null ) {
				final List<AnnotationUsage<A>> usages = AnnotationUsageHelper.extractValue( containerUsage, VALUE );
				for ( int i = 0; i < usages.size(); i++ ) {
					consumer.accept( usages.get( i ) );
				}
			}
		}
		else {
			final Class<A> annotationJavaType = type.getAnnotationType();
			//noinspection unchecked
			final AnnotationUsage<A> usage = (AnnotationUsage<A>) usagesMap.get( annotationJavaType );
			if ( usage != null ) {
				consumer.accept( usage );
			}
		}
	}

	@Override
	public <X extends Annotation> void forEachAnnotation(Class<X> type, Consumer<AnnotationUsage<X>> consumer) {
		forEachAnnotation(
				buildingContext.getAnnotationDescriptorRegistry().getDescriptor( type ),
				consumer
		);
	}

	@Override
	public <A extends Annotation> AnnotationUsage<A> getAnnotation(AnnotationDescriptor<A> type) {
		//noinspection unchecked
		return (AnnotationUsage<A>) usagesMap.get( type.getAnnotationType() );
	}

	@Override
	public <A extends Annotation> AnnotationUsage<A> getAnnotation(Class<A> type) {
		return getAnnotation(
				buildingContext.getAnnotationDescriptorRegistry().getDescriptor( type )
		);
	}

	@Override
	public <A extends Annotation> AnnotationUsage<A> getNamedAnnotation(
			AnnotationDescriptor<A> type,
			String matchValue,
			String attributeToMatch) {
		if ( type.getRepeatableContainer() == null ) {
			throw new AnnotationAccessException( "Expecting repeatable annotation" );
		}

		return AnnotationUsageHelper.getNamedAnnotation( type, matchValue, attributeToMatch, usagesMap );
	}

	@Override
	public <X extends Annotation> AnnotationUsage<X> getNamedAnnotation(
			Class<X> type,
			String matchName,
			String attributeToMatch) {
		return getNamedAnnotation(
				buildingContext.getAnnotationDescriptorRegistry().getDescriptor( type ),
				matchName,
				attributeToMatch
		);
	}
}
