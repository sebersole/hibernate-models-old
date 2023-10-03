/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.orm.process.internal;

import org.hibernate.models.source.spi.ClassDetails;

/**
 * @author Steve Ebersole
 */
public class UserTypeRegistration {
	private final ClassDetails domainClass;
	private final ClassDetails userTypeClass;

	public UserTypeRegistration(ClassDetails domainClass, ClassDetails userTypeClass) {
		this.domainClass = domainClass;
		this.userTypeClass = userTypeClass;
	}

	public ClassDetails getDomainClass() {
		return domainClass;
	}

	public ClassDetails getUserTypeClass() {
		return userTypeClass;
	}
}
