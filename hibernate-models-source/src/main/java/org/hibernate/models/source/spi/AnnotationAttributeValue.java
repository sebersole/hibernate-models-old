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
public interface AnnotationAttributeValue<V,W> {
	/**
	 * Descriptor for the attribute for which this is a value
	 */
	AnnotationAttributeDescriptor<?,V,W> getAttributeDescriptor();

	/**
	 * The value
	 */
	W getValue();

	<X> X getValue(Class<X> type);

	default String asString() {
		final W value = getValue();
		return value == null ? null : value.toString();
	}

	default boolean asBoolean() {
		return getValue( boolean.class );
	}

	default int asInt() {
		return getValue( int.class );
	}

	/**
	 * Whether the value is a default.
	 *
	 * @implNote Best guess at the moment since HCANN is unable to make
	 * 		this distinction.  This will be better handled once we can migrate
	 * 		to using Jandex for annotations.  See
	 * 		<a href="https://hibernate.atlassian.net/browse/HHH-9489">HHH-9489</a> (Migrate from commons-annotations to Jandex).
	 */
	boolean isDefaultValue();
}
