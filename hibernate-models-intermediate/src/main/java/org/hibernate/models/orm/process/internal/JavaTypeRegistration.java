/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.orm.process.internal;

import org.hibernate.models.source.spi.ClassDetails;

/**
 * @see org.hibernate.annotations.JavaTypeRegistration
 *
 * @author Steve Ebersole
 */
public class JavaTypeRegistration {
	private final ClassDetails domainType;
	private final ClassDetails descriptor;

	public JavaTypeRegistration(ClassDetails domainType, ClassDetails descriptor) {
		this.domainType = domainType;
		this.descriptor = descriptor;
	}

	public ClassDetails getDomainType() {
		return domainType;
	}

	public ClassDetails getDescriptor() {
		return descriptor;
	}
}
