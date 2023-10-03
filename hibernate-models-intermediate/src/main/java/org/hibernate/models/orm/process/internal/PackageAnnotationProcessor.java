/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.orm.process.internal;

import org.hibernate.models.orm.spi.OrmModelBuildingContext;
import org.hibernate.models.source.spi.PackageDetails;

import static org.hibernate.models.orm.process.internal.TypeContributionProcessor.processTypeContributions;

/**
 * Processes annotations found on packages.
 *
 * @author Steve Ebersole
 */
public class PackageAnnotationProcessor {
	private final ProcessResultCollector resultCollector;
	private final OrmModelBuildingContext processingContext;

	public PackageAnnotationProcessor(
			ProcessResultCollector resultCollector,
			OrmModelBuildingContext processingContext) {
		this.resultCollector = resultCollector;
		this.processingContext = processingContext;
	}

	public void process(PackageDetails packageDetails) {
		processTypeContributions( packageDetails, resultCollector, processingContext.getSourceModel().getClassDetailsRegistry() );
	}
}
