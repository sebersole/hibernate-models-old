/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.source.spi;

/**
 * Describes an attribute of an annotation
 *
 * @apiNote Many of the methods here deal with the underlying attribute value type.
 * E.g., even though we wrap {@link Class} values as {@link ClassDetails}, these methods
 * deal with {@link Class}.
 */
public interface AnnotationAttributeDescriptor {
	/**
	 * The value attribute name
	 */
	String VALUE = "value";

	/**
	 * The name of the attribute.
	 */
	String getAttributeName();
}
