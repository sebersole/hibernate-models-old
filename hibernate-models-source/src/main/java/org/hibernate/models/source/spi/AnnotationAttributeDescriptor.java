/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.source.spi;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * Describes an attribute of an annotation
 *
 * @apiNote Many of the methods here deal with the underlying attribute value type.
 * E.g., even though we wrap {@link Class} values as {@link ClassDetails}, these methods
 * deal with {@link Class}.
 */
public interface AnnotationAttributeDescriptor<A extends Annotation, V, W> {
	/**
	 * The value attribute name
	 */
	String VALUE = "value";

	/**
	 * The name of the attribute.
	 */
	String getAttributeName();

	/**
	 * The attribute method.
	 */
	Method getAttributeMethod();

	/**
	 * The {@linkplain Class Java type} of the attribute
	 *
	 * @apiNote This is the type of the underlying value.  E.g., it will return
	 * {@link Class} even though we wrap the Class in a {@link ClassDetails}
	 */
	Class<V> getAttributeType();

	/**
	 * The default value for this annotation
	 *
	 * @apiNote This is the default value of the underlying value.  E.g., it
	 * will return the {@link Class} value even though we wrap the Class in a
	 * {@link ClassDetails}
	 */
	V getAttributeDefault();

	/**
	 * Extract the value for the described attribute from an instance of the containing annotation
	 *
	 * @apiNote This is the underlying value.  E.g., it will return the {@link Class} value even
	 * though we wrap the Class in a {@link ClassDetails}
	 */
	V extractValue(A annotation);

	AnnotationAttributeValue<V,W> makeValueWrapper(V value, AnnotationTarget target, SourceModelBuildingContext processingContext);
}
