/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.source.spi;

/**
 * Descriptor for a Java package, mainly to act as AnnotationTarget.
 * <p/>
 * Effectively a reference to the package's {@code package-info} class, if one
 *
 * @author Steve Ebersole
 */
public interface PackageDetails extends AnnotationTarget {
	@Override
	default Kind getKind() {
		return Kind.PACKAGE;
	}

	String getName();
}
