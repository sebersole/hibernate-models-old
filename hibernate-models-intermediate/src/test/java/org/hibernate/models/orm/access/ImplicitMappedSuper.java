/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.orm.access;

import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

/**
 * MappedSuperclass expressing implicit access-type
 * <p/>
 * Implicit access-type will be FIELD due to placement of `@Id`
 *
 * @author Steve Ebersole
 */
@MappedSuperclass
public class ImplicitMappedSuper {
	@Id
	protected Integer id;
}
