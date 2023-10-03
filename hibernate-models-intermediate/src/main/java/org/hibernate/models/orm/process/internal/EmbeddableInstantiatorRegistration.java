/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.orm.process.internal;

import org.hibernate.models.source.spi.ClassDetails;

/**
 * @see org.hibernate.annotations.EmbeddableInstantiatorRegistration
 *
 * @author Steve Ebersole
 */
public class EmbeddableInstantiatorRegistration {
	private final ClassDetails embeddableClass;
	private final ClassDetails instantiator;

	public EmbeddableInstantiatorRegistration(ClassDetails embeddableClass, ClassDetails instantiator) {
		this.embeddableClass = embeddableClass;
		this.instantiator = instantiator;
	}

	public ClassDetails getEmbeddableClass() {
		return embeddableClass;
	}

	public ClassDetails getInstantiator() {
		return instantiator;
	}
}
