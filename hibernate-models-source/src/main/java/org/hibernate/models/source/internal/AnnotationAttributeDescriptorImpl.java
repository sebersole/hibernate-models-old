/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.source.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.hibernate.models.source.AnnotationAccessException;
import org.hibernate.models.source.spi.AnnotationAttributeDescriptor;
import org.hibernate.models.source.spi.AnnotationAttributeValue;
import org.hibernate.models.source.spi.AnnotationTarget;
import org.hibernate.models.source.spi.SourceModelBuildingContext;

/**
 * Specialized AnnotationAttributeDescriptor impl for String attributes
 *
 * @author Steve Ebersole
 */
public class AnnotationAttributeDescriptorImpl<A extends Annotation,V,W> implements AnnotationAttributeDescriptor<A,V,W> {
	private final Method attributeMethod;
	private final ValueNormalizer<V,W> valueNormalizer;

	public AnnotationAttributeDescriptorImpl(Method attributeMethod) {
		//noinspection unchecked
		this( attributeMethod, (ValueNormalizer<V, W>) PassThroughNormalizer.singleton() );
	}

	public AnnotationAttributeDescriptorImpl(Method attributeMethod, ValueNormalizer<V,W> valueNormalizer) {
		this.attributeMethod = attributeMethod;
		this.valueNormalizer = valueNormalizer;
	}

	@Override
	public Method getAttributeMethod() {
		return attributeMethod;
	}

	@Override
	public String getAttributeName() {
		return attributeMethod.getName();
	}

	@Override
	public Class<V> getAttributeType() {
		//noinspection unchecked
		return (Class<V>) attributeMethod.getReturnType();
	}

	@Override
	public V getAttributeDefault() {
		//noinspection unchecked
		return (V) attributeMethod.getDefaultValue();
	}

	@Override
	public V extractValue(A annotation) {
		try {
			//noinspection unchecked
			return (V) attributeMethod.invoke( annotation );
		}
		catch (IllegalAccessException | InvocationTargetException e) {
			throw new AnnotationAccessException( "Unable to extract annotation attribute value", e );
		}
	}

	@Override
	public AnnotationAttributeValue<V, W> makeValueWrapper(
			V value,
			AnnotationTarget target,
			SourceModelBuildingContext processingContext) {
		return new AnnotationAttributeValueImpl<>( this, valueNormalizer.normalize( value, target, processingContext ) );
	}

}
