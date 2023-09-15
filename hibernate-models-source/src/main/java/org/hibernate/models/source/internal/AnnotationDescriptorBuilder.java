/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.source.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

import org.hibernate.models.source.spi.AnnotationAttributeDescriptor;

import static org.hibernate.models.internal.CollectionHelper.arrayList;

/**
 * Builders for AnnotationDescriptor instances.
 *
 * @author Steve Ebersole
 */
public class AnnotationDescriptorBuilder {

	public static <A extends Annotation> List<AnnotationAttributeDescriptor<A,?,?>> extractAttributeDescriptors(Class<A> javaType) {
		final Method[] attributes = javaType.getDeclaredMethods();
		final List<AnnotationAttributeDescriptor<A,?,?>> attributeDescriptors = arrayList( attributes.length );

		for ( int i = 0; i < attributes.length; i++ ) {
			final Method attribute = attributes[ i ];
			attributeDescriptors.add( createAttributeDescriptor( javaType, attribute ) );
		}

		return attributeDescriptors;
	}

	@SuppressWarnings("unchecked")
	private static <A extends Annotation,V,W> AnnotationAttributeDescriptor<A,V,W> createAttributeDescriptor(
			Class<A> annotationJavaType,
			Method attributeMethod) {
		final Class<V> attributeJavaType = (Class<V>) attributeMethod.getReturnType();
		return new AnnotationAttributeDescriptorImpl<>( attributeMethod, resolveValueNormalizer( attributeJavaType ) );
	}

	@SuppressWarnings("unchecked")
	private static <V, W> ValueNormalizer<V, W> resolveValueNormalizer(Class<V> attributeJavaType) {
		if ( attributeJavaType == String.class ) {
			return (ValueNormalizer<V, W>) StringNormalizer.INSTANCE;
		}

		if ( attributeJavaType == Class.class ) {
			return (ValueNormalizer<V, W>) new ClassNormalizer( attributeJavaType );
		}

		if ( attributeJavaType.isArray() ) {
			return (ValueNormalizer<V, W>) new ArrayNormalizer<>( resolveValueNormalizer( attributeJavaType.getComponentType() ) );
		}

		if ( attributeJavaType.isAnnotation() ) {
			return (ValueNormalizer<V, W>) AnnotationNormalizer.singleton();
		}

		return (ValueNormalizer<V, W>) PassThroughNormalizer.singleton();
	}
}
