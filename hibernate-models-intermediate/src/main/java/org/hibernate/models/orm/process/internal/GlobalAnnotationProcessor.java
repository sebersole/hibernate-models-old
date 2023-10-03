/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.orm.process.internal;

import java.util.HashSet;
import java.util.Set;

import org.hibernate.models.orm.spi.HibernateAnnotations;
import org.hibernate.models.orm.spi.JpaAnnotations;
import org.hibernate.models.orm.spi.OrmModelBuildingContext;
import org.hibernate.models.source.spi.AnnotationTarget;

import static org.hibernate.models.orm.process.internal.TypeContributionProcessor.processTypeContributions;
import static org.hibernate.models.orm.spi.HibernateAnnotations.GENERIC_GENERATOR;
import static org.hibernate.models.orm.spi.JpaAnnotations.NAMED_ENTITY_GRAPH;
import static org.hibernate.models.orm.spi.JpaAnnotations.SEQUENCE_GENERATOR;
import static org.hibernate.models.orm.spi.JpaAnnotations.TABLE_GENERATOR;

/**
 * Processes "global" annotations which can be applied at a number of levels,
 * but are always considered global in scope (generators, queries, etc.).
 *
 * @author Steve Ebersole
 */
public class GlobalAnnotationProcessor {
	private final ProcessResultCollector resultCollector;
	private final OrmModelBuildingContext processingContext;
	private final Set<String> processedGlobalAnnotationSources = new HashSet<>();

	public GlobalAnnotationProcessor(ProcessResultCollector resultCollector, OrmModelBuildingContext processingContext) {
		this.resultCollector = resultCollector;
		this.processingContext = processingContext;
	}

	public void processGlobalAnnotations(AnnotationTarget annotationTarget) {
		if ( processedGlobalAnnotationSources.contains( annotationTarget.getName() ) ) {
			return;
		}
		processedGlobalAnnotationSources.add( annotationTarget.getName() );

		processTypeContributions( annotationTarget, resultCollector, processingContext.getSourceModel().getClassDetailsRegistry() );

		processGenerators( annotationTarget );
		processNamedQueries( annotationTarget );
		processNamedEntityGraphs( annotationTarget );
		processFilterDefinitions( annotationTarget );
	}

	private void processGenerators(AnnotationTarget annotationTarget) {
		processSequenceGenerators( annotationTarget );
		processTableGenerators( annotationTarget );
		processGenericGenerators( annotationTarget );
	}

	private void processSequenceGenerators(AnnotationTarget annotationTarget) {
		annotationTarget.forEachAnnotation( SEQUENCE_GENERATOR, (usage) -> {
			// todo (annotation-source) : implement
		} );
	}

	private void processTableGenerators(AnnotationTarget annotationTarget) {
		annotationTarget.forEachAnnotation( TABLE_GENERATOR, (usage) -> {
			// todo (annotation-source) : implement
		} );
	}

	private void processGenericGenerators(AnnotationTarget annotationTarget) {
		annotationTarget.forEachAnnotation( GENERIC_GENERATOR, (usage) -> {
			// todo (annotation-source) : implement
		} );
	}

	private void processNamedQueries(AnnotationTarget annotationTarget) {
		processNamedQuery( annotationTarget );
		processNamedNativeQuery( annotationTarget );
		processNamedProcedureQuery( annotationTarget );
	}

	private void processNamedQuery(AnnotationTarget annotationTarget) {
		annotationTarget.forEachAnnotation( JpaAnnotations.NAMED_QUERY, (usage) -> {
			// todo (annotation-source) : implement
		} );

		annotationTarget.forEachAnnotation( HibernateAnnotations.NAMED_QUERY, (usage) -> {
			// todo (annotation-source) : implement
		} );
	}

	private void processNamedNativeQuery(AnnotationTarget annotationTarget) {
		annotationTarget.forEachAnnotation( JpaAnnotations.NAMED_NATIVE_QUERY, (usage) -> {
			// todo (annotation-source) : implement
		} );

		annotationTarget.forEachAnnotation( HibernateAnnotations.NAMED_NATIVE_QUERY, (usage) -> {
			// todo (annotation-source) : implement
		} );
	}


	private void processNamedProcedureQuery(AnnotationTarget annotationTarget) {
		annotationTarget.forEachAnnotation( JpaAnnotations.NAMED_STORED_PROCEDURE_QUERY, (usage) -> {
			// todo (annotation-source) : implement
		} );
	}

	private void processNamedEntityGraphs(AnnotationTarget annotationTarget) {
		annotationTarget.forEachAnnotation( NAMED_ENTITY_GRAPH, (usage) -> {
			// todo (annotation-source) : implement
		} );
	}

	private void processFilterDefinitions(AnnotationTarget annotationTarget) {
		annotationTarget.forEachAnnotation( HibernateAnnotations.FILTER_DEF, (usage) -> {
			// todo (annotation-source) : implement
		} );
	}
}
