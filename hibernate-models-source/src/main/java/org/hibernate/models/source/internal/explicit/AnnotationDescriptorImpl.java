/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.source.internal.explicit;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import org.hibernate.models.source.AnnotationAccessException;
import org.hibernate.models.source.internal.AnnotationHelper;
import org.hibernate.models.source.internal.standard.annotations.ArrayValueDescriptor;
import org.hibernate.models.source.internal.standard.annotations.EnumValueExtractor;
import org.hibernate.models.source.internal.standard.annotations.ValueExtractor;
import org.hibernate.models.source.spi.AnnotationAttributeDescriptor;
import org.hibernate.models.source.spi.AnnotationDescriptor;
import org.hibernate.models.source.spi.AnnotationTarget;
import org.hibernate.models.source.spi.AnnotationUsage;

import static org.hibernate.models.source.internal.standard.annotations.BooleanValueExtractor.BOOLEAN_EXTRACTOR;
import static org.hibernate.models.source.internal.standard.annotations.ByteValueExtractor.BYTE_EXTRACTOR;
import static org.hibernate.models.source.internal.standard.annotations.CharacterValueExtractor.CHARACTER_EXTRACTOR;
import static org.hibernate.models.source.internal.standard.annotations.ClassValueExtractor.CLASS_EXTRACTOR;
import static org.hibernate.models.source.internal.standard.annotations.DoubleValueExtractor.DOUBLE_EXTRACTOR;
import static org.hibernate.models.source.internal.standard.annotations.FloatValueExtractor.FLOAT_EXTRACTOR;
import static org.hibernate.models.source.internal.standard.annotations.IntegerValueExtractor.INTEGER_EXTRACTOR;
import static org.hibernate.models.source.internal.standard.annotations.LongValueExtractor.LONG_EXTRACTOR;
import static org.hibernate.models.source.internal.standard.annotations.PassthruValueExtractor.PASSTHRU_EXTRACTOR;
import static org.hibernate.models.source.internal.standard.annotations.ShortValueExtractor.SHORT_EXTRACTOR;
import static org.hibernate.models.source.internal.standard.annotations.StringValueExtractor.STRING_EXTRACTOR;

/**
 * AnnotationDescriptor implementation where we do not care about annotations
 * defined on the annotation itself.  The {@link AnnotationTarget} contract here
 * behaves as if no annotations where found.
 * Generally speaking this would be used to model JPA and Hibernate annotations.
 *
 * @author Steve Ebersole
 */
public class AnnotationDescriptorImpl<A extends Annotation> implements AnnotationDescriptor<A> {
	private final Class<A> annotationType;
	private final List<AnnotationAttributeDescriptor> attributeDescriptors;
	private final AnnotationDescriptor<?> repeatableContainer;

	private final boolean inherited;
	private final EnumSet<Kind> allowableTargets;

	public AnnotationDescriptorImpl(
			Class<A> annotationType,
			List<AnnotationAttributeDescriptor> attributeDescriptors,
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
	public List<AnnotationAttributeDescriptor> getAttributes() {
		return attributeDescriptors;
	}

	@Override
	public AnnotationAttributeDescriptor getAttribute(String name) {
		for ( int i = 0; i < attributeDescriptors.size(); i++ ) {
			final AnnotationAttributeDescriptor attributeDescriptor = attributeDescriptors.get( i );
			if ( attributeDescriptor.getAttributeName().equals( name ) ) {
				return attributeDescriptor;
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
		AnnotationDescriptorImpl<?> that = (AnnotationDescriptorImpl<?>) o;
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

	public static <A extends Annotation> AnnotationDescriptorImpl<A> buildDescriptor(Class<A> annotationType) {
		return buildDescriptor( annotationType, null );
	}

	public static <A extends Annotation> AnnotationDescriptorImpl<A> buildDescriptor(
			Class<A> annotationType,
			AnnotationDescriptor<?> repeatableContainer) {
		final Method[] methods = annotationType.getDeclaredMethods();
		final List<AnnotationAttributeDescriptor> attributeDescriptors = new ArrayList<>( methods.length );
		for ( Method method : methods ) {
			attributeDescriptors.add( createAttributeDescriptor( method ) );
		}
		return new AnnotationDescriptorImpl<>( annotationType, attributeDescriptors, repeatableContainer );
	}

	private static AnnotationAttributeDescriptor createAttributeDescriptor(Method method) {
		final Class<?> attributeType = method.getReturnType();

		if ( attributeType.isArray() ) {
			return buildArrayValuedAttributeDescriptor( method.getName(), attributeType );
		}

		return resolveValueExtractor( attributeType )
				.createAttributeDescriptor( method.getName() );
	}

	private static AnnotationAttributeDescriptor buildArrayValuedAttributeDescriptor(
			String name,
			Class<?> attributeType) {
		return new ArrayValueDescriptor<>(
				name,
				resolveValueExtractor( attributeType.getComponentType() )
		);
	}

	@SuppressWarnings("unchecked")
	private static <T> ValueExtractor<T> resolveValueExtractor(Class<T> attributeType) {
		if ( attributeType.isEnum() ) {
			//noinspection rawtypes
			return new EnumValueExtractor<>( (Class) attributeType );
		}

		if ( attributeType == byte.class ) {
			return (ValueExtractor<T>) BYTE_EXTRACTOR;
		}

		if ( attributeType == boolean.class ) {
			return (ValueExtractor<T>) BOOLEAN_EXTRACTOR;
		}

		if ( attributeType == short.class ) {
			return (ValueExtractor<T>) SHORT_EXTRACTOR;
		}

		if ( attributeType == int.class ) {
			return (ValueExtractor<T>) INTEGER_EXTRACTOR;
		}

		if ( attributeType == long.class ) {
			return (ValueExtractor<T>) LONG_EXTRACTOR;
		}

		if ( attributeType == float.class ) {
			return (ValueExtractor<T>) FLOAT_EXTRACTOR;
		}

		if ( attributeType == double.class ) {
			return (ValueExtractor<T>) DOUBLE_EXTRACTOR;
		}

		if ( attributeType == char.class ) {
			return (ValueExtractor<T>) CHARACTER_EXTRACTOR;
		}

		if ( attributeType == String.class ) {
			return (ValueExtractor<T>) STRING_EXTRACTOR;
		}

		if ( attributeType == Class.class ) {
			return (ValueExtractor<T>) CLASS_EXTRACTOR;
		}

		if ( Annotation.class.isAssignableFrom( attributeType ) ) {
			return (ValueExtractor<T>) new NestedValueExtractor<>( (Class<? extends Annotation>) attributeType );
		}

		return (ValueExtractor<T>) PASSTHRU_EXTRACTOR;
	}

	@Override
	public <X extends Annotation> AnnotationUsage<X> getAnnotation(Class<X> type) {
		return null;
	}

	@Override
	public <X extends Annotation> List<AnnotationUsage<X>> getRepeatedAnnotations(Class<X> type) {
		return null;
	}

	@Override
	public <X extends Annotation> void forEachAnnotation(Class<X> type, Consumer<AnnotationUsage<X>> consumer) {
	}

	@Override
	public <X extends Annotation> AnnotationUsage<X> getNamedAnnotation(
			Class<X> type,
			String matchName,
			String attributeToMatch) {
		return null;
	}
}
