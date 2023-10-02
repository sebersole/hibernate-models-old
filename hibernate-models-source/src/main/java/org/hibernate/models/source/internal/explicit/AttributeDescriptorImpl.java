/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.source.internal.explicit;

import org.hibernate.models.source.spi.AnnotationAttributeDescriptor;

/**
 * @author Steve Ebersole
 */
public class AttributeDescriptorImpl implements AnnotationAttributeDescriptor {
	private final String name;

	public AttributeDescriptorImpl(String name) {
		this.name = name;
	}

	@Override
	public String getAttributeName() {
		return name;
	}
}
