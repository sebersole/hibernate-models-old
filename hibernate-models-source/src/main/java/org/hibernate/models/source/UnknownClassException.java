/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.source;

/**
 * @author Steve Ebersole
 */
public class UnknownClassException extends ModelsException {
	public UnknownClassException(String message) {
		super( message );
	}

	public UnknownClassException(String message, Throwable cause) {
		super( message, cause );
	}
}
