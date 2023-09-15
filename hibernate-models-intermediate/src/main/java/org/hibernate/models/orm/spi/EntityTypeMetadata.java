/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.orm.spi;

import org.hibernate.boot.model.naming.ImplicitEntityNameSource;

/**
 * Intermediate representation of an {@linkplain jakarta.persistence.metamodel.EntityType entity type}
 *
 * @author Steve Ebersole
 */
public interface EntityTypeMetadata extends IdentifiableTypeMetadata {
	String getEntityName();
	String getJpaEntityName();
}
