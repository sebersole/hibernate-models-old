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
 * @see org.hibernate.models.orm.spi.JpaAnnotations#NAMED_QUERY
 * @see org.hibernate.models.orm.spi.JpaAnnotations#NAMED_NATIVE_QUERY
 * @see org.hibernate.models.orm.spi.JpaAnnotations#NAMED_STORED_PROCEDURE_QUERY
 * @see org.hibernate.models.orm.spi.HibernateAnnotations#NAMED_QUERY
 * @see org.hibernate.models.orm.spi.HibernateAnnotations#NAMED_NATIVE_QUERY
 *
 * @author Steve Ebersole
 */
public class NamedQuery {
	public enum Kind {
		HQL,
		NATIVE,
		CALLABLE
	}

	private final String name;
	private final Kind kind;
	private final boolean isJpa;
	private final AnnotationUsage<? extends Annotation> configuration;

	public NamedQuery(String name, Kind kind, boolean isJpa, AnnotationUsage<? extends Annotation> configuration) {
		this.name = name;
		this.kind = kind;
		this.isJpa = isJpa;
		this.configuration = configuration;
	}

	public String getName() {
		return name;
	}

	public Kind getKind() {
		return kind;
	}

	public boolean isJpa() {
		return isJpa;
	}

	public AnnotationUsage<? extends Annotation> getConfiguration() {
		return configuration;
	}
}
