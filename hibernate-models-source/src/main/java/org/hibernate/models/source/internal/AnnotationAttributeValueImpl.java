/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.source.internal;

import java.util.Objects;

import org.hibernate.models.source.spi.AnnotationAttributeDescriptor;
import org.hibernate.models.source.spi.AnnotationAttributeValue;

/**
 * @author Steve Ebersole
 */
public class AnnotationAttributeValueImpl<V,W> implements AnnotationAttributeValue<V,W> {
	private final AnnotationAttributeDescriptor<?,V,W> attributeDescriptor;
	private final W value;

	public AnnotationAttributeValueImpl(AnnotationAttributeDescriptor<?,V,W> attributeDescriptor, W value) {
		this.attributeDescriptor = attributeDescriptor;
		this.value = value;
	}

	@Override
	public AnnotationAttributeDescriptor<?,V,W> getAttributeDescriptor() {
		return attributeDescriptor;
	}

	@Override
	public W getValue() {
		return value;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <X> X getValue(Class<X> type) {
		// todo (annotation-source) : possibly add some simple conversions.

		// todo (annotation-source) : or possibly typed AnnotationAttributeDescriptor impls (`IntAttributeDescriptor`, ...)
		//		which can be a factory for AnnotationAttributeValue refs based on the descriptor type.

		return (X) getValue();
	}

	@Override
	public boolean isDefaultValue() {
		return value == null || Objects.equals( value, attributeDescriptor.getAttributeDefault() );
	}
}
