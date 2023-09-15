/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.orm.spi;

import jakarta.persistence.InheritanceType;

/**
 * Models an entity hierarchy comprised of {@linkplain EntityTypeMetadata entity}
 * and {@linkplain MappedSuperclassTypeMetadata mapped-superclass} types.
 *
 * @author Steve Ebersole
 */
public interface EntityHierarchy {
	/**
	 * The hierarchy's root type.
	 */
	EntityTypeMetadata getRoot();

	/**
	 * The inheritance strategy for the hierarchy.
	 */
	InheritanceType getInheritanceType();

	/**
	 * The caching configuration for entities in this hierarchy.
	 */
	Caching getCaching();

	/**
	 * The caching configuration for this hierarchy's {@linkplain org.hibernate.annotations.NaturalId natural-id}
	 */
	NaturalIdCaching getNaturalIdCaching();
}
