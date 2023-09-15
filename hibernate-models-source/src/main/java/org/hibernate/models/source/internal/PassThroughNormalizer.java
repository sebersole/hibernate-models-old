/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.source.internal;

import org.hibernate.models.source.spi.AnnotationTarget;
import org.hibernate.models.source.spi.SourceModelBuildingContext;

/**
 * ValueNormalizer which simply returns the value it is given
 *
 * @author Steve Ebersole
 */
public class PassThroughNormalizer<V> implements ValueNormalizer<V, V> {
	/**
	 * Singleton access
	 */
	@SuppressWarnings("rawtypes")
	public static final PassThroughNormalizer INSTANCE = new PassThroughNormalizer();

	@SuppressWarnings("unchecked")
	public static <V> PassThroughNormalizer<V> singleton() {
		return INSTANCE;
	}

	@Override
	public V normalize(V incomingValue, AnnotationTarget target, SourceModelBuildingContext processingContext) {
		return incomingValue;
	}
}
