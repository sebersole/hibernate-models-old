/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.orm.access;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Basic;
import jakarta.persistence.Entity;

/**
 * @author Steve Ebersole
 */
@Entity
@Access( AccessType.PROPERTY )
public class ExplicitMappedSuperRoot1 extends ExplicitMappedSuper {
	private String name;

	public ExplicitMappedSuperRoot1(Integer id, String name) {
		super( id );
		this.name = name;
	}

	@Basic
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
