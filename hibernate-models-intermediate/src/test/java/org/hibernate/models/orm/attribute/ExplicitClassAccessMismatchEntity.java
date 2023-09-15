/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.orm.attribute;

import jakarta.persistence.Access;
import jakarta.persistence.Basic;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import static jakarta.persistence.AccessType.PROPERTY;

/**
 * The class specifies PROPERTY but the annotations are defined on fields
 *
 * @author Steve Ebersole
 */
@Entity
@Access(PROPERTY)
public class ExplicitClassAccessMismatchEntity {
	@Id
	private Integer id;
	@Basic
	private String name;
}
