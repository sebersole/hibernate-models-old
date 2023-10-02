/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.source.internal.standard.annotations;

import org.hibernate.models.source.spi.AnnotationAttributeDescriptor;
import org.hibernate.models.source.spi.AnnotationAttributeValue;

/**
 * @author Steve Ebersole
 */
public class AttributeValueImpl<W> implements AnnotationAttributeValue<W> {
	private final AnnotationAttributeDescriptor descriptor;
	private final W value;
	private final boolean implicit;

	public AttributeValueImpl(AnnotationAttributeDescriptor descriptor, W value, boolean implicit) {
		this.descriptor = descriptor;
		this.value = value;
		this.implicit = implicit;
	}

	@Override
	public AnnotationAttributeDescriptor getAttributeDescriptor() {
		return descriptor;
	}

	@Override
	public W getValue() {
		return value;
	}

	@Override
	public <X> X getValue(Class<X> type) {
		//noinspection unchecked
		return (X) value;
	}

	@Override
	public boolean isImplicit() {
		return implicit;
	}
}
