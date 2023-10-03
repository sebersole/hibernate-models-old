/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.orm.process.internal;

import org.hibernate.models.orm.spi.OrmModelBuildingContext;
import org.hibernate.models.source.spi.ClassDetails;

/**
 * Processes annotations found on entity and mapped-superclass types
 *
 * @author Steve Ebersole
 */
public class IdentifiableTypeAnnotationProcessor {
	private final ProcessResultCollector resultCollector;
	private final OrmModelBuildingContext processingContext;

	public IdentifiableTypeAnnotationProcessor(
			ProcessResultCollector resultCollector,
			OrmModelBuildingContext processingContext) {
		this.resultCollector = resultCollector;
		this.processingContext = processingContext;
	}

	public void process(ClassDetails classDetails) {

	}
}
