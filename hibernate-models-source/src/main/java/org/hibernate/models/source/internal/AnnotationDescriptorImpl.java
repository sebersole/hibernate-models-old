/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.source.internal;

import java.lang.annotation.Annotation;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;

import org.hibernate.models.source.AnnotationAccessException;
import org.hibernate.models.source.internal.reflection.ClassDetailsBuilderImpl;
import org.hibernate.models.source.spi.AnnotationAttributeDescriptor;
import org.hibernate.models.source.spi.AnnotationDescriptor;
import org.hibernate.models.source.spi.SourceModelBuildingContext;


/**
 * Standard AnnotationDescriptor implementation
 *
 * @author Steve Ebersole
 */
public class AnnotationDescriptorImpl<A extends Annotation>
		extends AbstractAnnotationTarget
		implements AnnotationDescriptor<A> {
	private final Class<A> annotationType;
	private final AnnotationDescriptor<?> repeatableContainer;

	private final boolean inherited;
	private final EnumSet<Kind> allowableTargets;
	private final List<AnnotationAttributeDescriptor<A,?,?>> attributeDescriptors;

	public AnnotationDescriptorImpl(
			Class<A> annotationType,
			AnnotationDescriptor<?> repeatableContainer,
			SourceModelBuildingContext processingContext) {
		// todo (annotation-source) : `#getDeclaredAnnotations` or `#getAnnotations`?
		//		- do we want `@Inherited` here?
//		super( annotationType.getAnnotations(), processingContext );
		super( annotationType.getDeclaredAnnotations(), processingContext );
		this.annotationType = annotationType;
		this.repeatableContainer = repeatableContainer;

		this.inherited = AnnotationHelper.isInherited( annotationType );
		this.allowableTargets = AnnotationHelper.extractTargets( annotationType );
		this.attributeDescriptors = AnnotationDescriptorBuilder.extractAttributeDescriptors( annotationType );

		processingContext.getClassDetailsRegistry().resolveClassDetails(
				annotationType.getName(),
				ClassDetailsBuilderImpl::buildClassDetailsStatic
		);
	}

	@Override
	public String getName() {
		return annotationType.getName();
	}

	@Override
	public Class<A> getAnnotationType() {
		return annotationType;
	}

	@Override
	public boolean isInherited() {
		return inherited;
	}

	@Override
	public EnumSet<Kind> getAllowableTargets() {
		return allowableTargets;
	}

	@Override
	public List<AnnotationAttributeDescriptor<A,?,?>> getAttributes() {
		return attributeDescriptors;
	}

	@Override
	public AnnotationDescriptor<?> getRepeatableContainer() {
		return repeatableContainer;
	}

	@Override
	public <V, W> AnnotationAttributeDescriptor<A, V, W> getAttribute(String name) {
		for ( int i = 0; i < attributeDescriptors.size(); i++ ) {
			final AnnotationAttributeDescriptor<A,?,?> attributeDescriptor = attributeDescriptors.get( i );
			if ( attributeDescriptor.getAttributeName().equals( name ) ) {
				//noinspection unchecked
				return (AnnotationAttributeDescriptor<A,V,W>) attributeDescriptor;
			}
		}
		throw new AnnotationAccessException( "No such attribute : " + annotationType.getName() + "." + name );
	}

	@Override
	public boolean equals(Object o) {
		if ( this == o ) {
			return true;
		}
		if ( o == null || getClass() != o.getClass() ) {
			return false;
		}
		final AnnotationDescriptorImpl<?> that = (AnnotationDescriptorImpl<?>) o;
		return annotationType.equals( that.annotationType );
	}

	@Override
	public int hashCode() {
		return Objects.hash( annotationType );
	}

	@Override
	public String toString() {
		return "AnnotationDescriptor(" + annotationType.getName() + ")";
	}
}
