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
import jakarta.persistence.Id;

/**
 * Simple entity for testing implicit access-type determination.
 * <p/>
 * Implicit access-type will be FIELD due to placement of `@Id`
 *
 * @author Steve Ebersole
 */
@Entity
@Access( AccessType.FIELD )
public class SimpleExplicitEntity {
	@Id
	private Integer id;
	@Basic
	private String name;

	protected SimpleExplicitEntity() {
		// for use by Hibernate
	}

	public SimpleExplicitEntity(Integer id, String name) {
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
