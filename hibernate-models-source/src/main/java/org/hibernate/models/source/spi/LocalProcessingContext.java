/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.source.spi;

/**
 * A processing context that is local to a {@linkplain #getScope() scope} - a package,
 * class, etc.
 *
 * @author Steve Ebersole
 */
public interface LocalProcessingContext<S> {
	/**
	 * The scope of this local context
	 */
	S getScope();

	/**
	 * Access to the top-level processing context
	 */
	SourceModelBuildingContext getModelProcessingContext();
}
