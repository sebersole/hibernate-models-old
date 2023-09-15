/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.orm.access;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

/**
 * MappedSuperclass expressing explicit access-type
 *
 * @author Steve Ebersole
 */
@MappedSuperclass
@Access( AccessType.FIELD )
public class ExplicitMappedSuper {
	@Id
	protected Integer id;

	protected ExplicitMappedSuper() {
		// for use by Hibernate
	}

	public ExplicitMappedSuper(Integer id) {
		this.id = id;
	}

	public Integer getId() {
		return id;
	}
}
