/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.orm.attribute;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Basic;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

/**
 * @author Steve Ebersole
 */
@Entity
@Access( AccessType.PROPERTY )
public class ExplicitAccessOnSetterEntity {
	private Integer id;
	private String name;

	private ExplicitAccessOnSetterEntity() {
		// for Hibernate use
	}

	public ExplicitAccessOnSetterEntity(Integer id, String name) {
		this.id = id;
		this.name = name;
	}

	@Id
	public Integer getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	@Basic
	public void setName(String name) {
		this.name = name;
	}
}
