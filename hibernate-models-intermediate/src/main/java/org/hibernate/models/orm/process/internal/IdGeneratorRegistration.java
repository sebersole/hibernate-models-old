/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.orm.process.internal;

import java.lang.annotation.Annotation;

import org.hibernate.models.source.spi.AnnotationUsage;

/**
 * Represents both global and local id generator registrations
 *
 * @author Steve Ebersole
 */
public class IdGeneratorRegistration {
	public enum Kind {
		SEQUENCE,
		TABLE,
		UUID,
		META,
		GENERIC
	}

	private final String name;
	private final Kind kind;
	private final AnnotationUsage<? extends Annotation> configuration;

	public IdGeneratorRegistration(String name, Kind kind, AnnotationUsage<? extends Annotation> configuration) {
		this.name = name;
		this.kind = kind;
		this.configuration = configuration;
	}

	/**
	 * The registration name.  For local generators, this may be {@code null}
	 */
	public String getName() {
		return name;
	}

	/**
	 * The kind of generation
	 */
	public Kind getKind() {
		return kind;
	}

	/**
	 * The annotation which configures the generator
	 */
	public AnnotationUsage<? extends Annotation> getConfiguration() {
		return configuration;
	}
}
