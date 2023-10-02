/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.source.spi;

/**
 * The descriptor for the value of a particular attribute for an annotation usage
 */
public interface AnnotationAttributeValue<W> {
	/**
	 * Descriptor for the attribute for which this is a value
	 */
	AnnotationAttributeDescriptor getAttributeDescriptor();

	/**
	 * The value
	 */
	W getValue();

	<X> X getValue(Class<X> type);

	default String asString() {
		return getValue( String.class );
	}

	default Integer asInteger() {
		return getValue( Integer.class );
	}

	default Boolean asBoolean() {
		return getValue( Boolean.class );
	}

	/**
	 * Whether the value is implicit (the default), or was explicitly specified
	 *
	 * @return {@code true} indicates that the attribute's value is
	 * the default; {@code false} indicates the attribute was explicitly
	 * specified
	 */
	boolean isImplicit();
}
