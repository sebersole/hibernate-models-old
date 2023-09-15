/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.orm;

import java.util.Locale;

import org.hibernate.MappingException;
import org.hibernate.models.source.spi.ClassDetails;

/**
 * Indicates that {@link jakarta.persistence.AccessType} could not be
 * determined
 *
 * @author Steve Ebersole
 */
public class AccessTypeDeterminationException extends MappingException {
	public AccessTypeDeterminationException(ClassDetails managedClass) {
		super(
				String.format(
						Locale.ROOT,
						"Unable to determine default `AccessType` for class `%s`",
						managedClass.getName()
				)
		);
	}
}
