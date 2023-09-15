/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.orm.access;

import jakarta.persistence.Basic;
import jakarta.persistence.Entity;

/**
 * Along with ImplicitMappedSuper, implies FIELD access-type
 *
 * @author Steve Ebersole
 */
@Entity
public class ImplicitMappedSuperRoot extends ImplicitMappedSuper {
	@Basic
	private String name;

	protected ImplicitMappedSuperRoot() {
		// for use by Hibernate
	}

	public ImplicitMappedSuperRoot(Integer id, String name) {
		this.id = id;
		this.name = name;
	}

	public Integer getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
