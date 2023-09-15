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
 * Normalizes annotation attribute values.
 * <p/>
 * This is the code that controls value transformations from the "raw" type directly
 * on the annotation to the "wrapper" type used in {@link org.hibernate.models.source.spi.AnnotationAttributeValue}
 *
 * @param <V> The raw type
 * @param <W> The wrapper type
 *
 * @author Steve Ebersole
 */
@FunctionalInterface
public interface ValueNormalizer<V, W> {
	W normalize(V incomingValue, AnnotationTarget target, SourceModelBuildingContext processingContext);
}
