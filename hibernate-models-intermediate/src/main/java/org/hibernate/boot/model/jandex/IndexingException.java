/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.boot.model.jandex;

import org.hibernate.models.source.ModelsException;

/**
 * Indicates a problem building the Jandex index, when Hibernate is asked to do so
 *
 * @author Steve Ebersole
 */
public class IndexingException extends ModelsException {
	public IndexingException(String message) {
		super( message );
	}

	public IndexingException(String message, Throwable cause) {
		super( message, cause );
	}
}
