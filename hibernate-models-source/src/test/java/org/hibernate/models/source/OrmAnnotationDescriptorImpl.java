/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.source;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import org.hibernate.models.source.internal.AnnotationHelper;
import org.hibernate.models.source.spi.AnnotationAttributeDescriptor;
import org.hibernate.models.source.spi.AnnotationDescriptor;
import org.hibernate.models.source.spi.AnnotationUsage;

/**
 * @author Steve Ebersole
 */
public class OrmAnnotationDescriptorImpl<A extends Annotation> implements AnnotationDescriptor<A> {
	private final Class<A> annotationType;
	private final List<AnnotationAttributeDescriptor<A,?,?>> attributeDescriptors;
	private final AnnotationDescriptor<?> repeatableContainer;

	private final boolean inherited;
	private final EnumSet<Kind> allowableTargets;

	public OrmAnnotationDescriptorImpl(
			Class<A> annotationType,
			List<AnnotationAttributeDescriptor<A,?,?>> attributeDescriptors,
			AnnotationDescriptor<?> repeatableContainer) {
		this.annotationType = annotationType;
		this.attributeDescriptors = attributeDescriptors;
		this.repeatableContainer = repeatableContainer;

		this.inherited = AnnotationHelper.isInherited( annotationType );
		this.allowableTargets = AnnotationHelper.extractTargets( annotationType );
	}

	@Override
	public String getName() {
		return annotationType.getName();
	}

	/**
	 * The {@linkplain Class Java type} of the annotation.
	 */
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

	/**
	 * Descriptors for the attributes of this annotation
	 */
	@Override
	public List<AnnotationAttributeDescriptor<A,?,?>> getAttributes() {
		return attributeDescriptors;
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

	/**
	 * If the annotation is {@linkplain java.lang.annotation.Repeatable repeatable},
	 * returns the descriptor for the container annotation
	 */
	@Override
	public AnnotationDescriptor<?> getRepeatableContainer() {
		return repeatableContainer;
	}

	@Override
	public <X extends Annotation> AnnotationUsage<X> getAnnotation(AnnotationDescriptor<X> type) {
		// there are none
		return null;
	}

	@Override
	public <X extends Annotation> List<AnnotationUsage<X>> getRepeatedAnnotations(AnnotationDescriptor<X> type) {
		return Collections.emptyList();
	}

	@Override
	public <X extends Annotation> void forEachAnnotation(AnnotationDescriptor<X> type, Consumer<AnnotationUsage<X>> consumer) {
	}

	@Override
	public <X extends Annotation> AnnotationUsage<X> getNamedAnnotation(AnnotationDescriptor<X> type, String name, String attributeName) {
		// there are none
		return null;
	}

	@Override
	public boolean equals(Object o) {
		if ( this == o ) {
			return true;
		}
		if ( o == null || getClass() != o.getClass() ) {
			return false;
		}
		OrmAnnotationDescriptorImpl<?> that = (OrmAnnotationDescriptorImpl<?>) o;
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
