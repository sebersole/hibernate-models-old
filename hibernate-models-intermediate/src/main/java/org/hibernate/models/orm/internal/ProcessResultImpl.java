/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.orm.internal;

import java.util.Set;

import org.hibernate.models.orm.spi.EntityHierarchy;
import org.hibernate.models.orm.spi.ProcessResult;

/**
 * @author Steve Ebersole
 */
public class ProcessResultImpl implements ProcessResult {
	private final Set<EntityHierarchy> entityHierarchies;

	public ProcessResultImpl(Set<EntityHierarchy> entityHierarchies) {
		this.entityHierarchies = entityHierarchies;
	}

	@Override
	public Set<EntityHierarchy> getEntityHierarchies() {
		return entityHierarchies;
	}
}
