/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.orm.process.internal;

import java.util.HashSet;
import java.util.Set;

import org.hibernate.models.internal.StringHelper;
import org.hibernate.models.orm.spi.HibernateAnnotations;
import org.hibernate.models.orm.spi.JpaAnnotations;
import org.hibernate.models.orm.spi.OrmModelBuildingContext;
import org.hibernate.models.source.spi.AnnotationTarget;

import static org.hibernate.models.orm.process.internal.TypeContributionProcessor.processTypeContributions;
import static org.hibernate.models.orm.spi.HibernateAnnotations.GENERIC_GENERATOR;
import static org.hibernate.models.orm.spi.HibernateAnnotations.UUID_GENERATOR;
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
	public interface Options {
		boolean areGeneratorsGlobal();
	}

	private final ProcessResultCollector resultCollector;
	private final Options options;
	private final OrmModelBuildingContext processingContext;
	private final Set<String> processedGlobalAnnotationSources = new HashSet<>();

	public GlobalAnnotationProcessor(
			ProcessResultCollector resultCollector,
			Options options,
			OrmModelBuildingContext processingContext) {
		this.resultCollector = resultCollector;
		this.options = options;
		this.processingContext = processingContext;
	}

	public void processGlobalAnnotations(AnnotationTarget annotationTarget) {
		if ( !processedGlobalAnnotationSources.add( annotationTarget.getName() ) ) {
			// we've already processed this target
			return;
		}

		processTypeContributions( annotationTarget, resultCollector, processingContext.getSourceModel().getClassDetailsRegistry() );

		processGenerators( annotationTarget );
		processNamedQueries( annotationTarget );
		processNamedEntityGraphs( annotationTarget );
		processFilterDefinitions( annotationTarget );
	}

	private void processGenerators(AnnotationTarget annotationTarget) {
		// collect the generators as global if either -
		//		1. target is a package
		//		2. `options.areGeneratorsGlobal()` is true
		if ( annotationTarget.getKind() == AnnotationTarget.Kind.PACKAGE
				|| options.areGeneratorsGlobal() ) {
			processSequenceGenerators( annotationTarget );
			processTableGenerators( annotationTarget );
			processGenericGenerators( annotationTarget );
			// todo (models) : @IdGeneratorType - META
			// NOTE : @UUIDGenerator can only be local to the id attribute
		}
	}

	private void processSequenceGenerators(AnnotationTarget annotationTarget) {
		annotationTarget.forEachAnnotation( SEQUENCE_GENERATOR, (usage) -> {
			final String generatorName = usage.extractAttributeValue( "name" );
			assert generatorName != null;
			resultCollector.collectGlobalIdGeneratorRegistration(
					generatorName,
					IdGeneratorRegistration.Kind.SEQUENCE,
					usage
			);
		} );
	}

	private void processTableGenerators(AnnotationTarget annotationTarget) {
		annotationTarget.forEachAnnotation( TABLE_GENERATOR, (usage) -> {
			final String generatorName = usage.extractAttributeValue( "name" );
			assert generatorName != null;
			resultCollector.collectGlobalIdGeneratorRegistration(
					generatorName,
					IdGeneratorRegistration.Kind.TABLE,
					usage
			);
		} );
	}

	private void processGenericGenerators(AnnotationTarget annotationTarget) {
		annotationTarget.forEachAnnotation( GENERIC_GENERATOR, (usage) -> {
			final String generatorName = usage.extractAttributeValue( "name" );
			assert generatorName != null;
			resultCollector.collectGlobalIdGeneratorRegistration(
					generatorName,
					IdGeneratorRegistration.Kind.GENERIC,
					usage
			);
		} );
	}

	private void processNamedQueries(AnnotationTarget annotationTarget) {
		processNamedQuery( annotationTarget );
		processNamedNativeQuery( annotationTarget );
		processNamedProcedureQuery( annotationTarget );
	}

	private void processNamedQuery(AnnotationTarget annotationTarget) {
		annotationTarget.forEachAnnotation( JpaAnnotations.NAMED_QUERY, (usage) -> {
			// todo (models)  : implement
		} );

		annotationTarget.forEachAnnotation( HibernateAnnotations.NAMED_QUERY, (usage) -> {
			// todo (models)  : implement
		} );
	}

	private void processNamedNativeQuery(AnnotationTarget annotationTarget) {
		annotationTarget.forEachAnnotation( JpaAnnotations.NAMED_NATIVE_QUERY, (usage) -> {
			// todo (models)  : implement
		} );

		annotationTarget.forEachAnnotation( HibernateAnnotations.NAMED_NATIVE_QUERY, (usage) -> {
			// todo (models)  : implement
		} );
	}


	private void processNamedProcedureQuery(AnnotationTarget annotationTarget) {
		annotationTarget.forEachAnnotation( JpaAnnotations.NAMED_STORED_PROCEDURE_QUERY, (usage) -> {
			// todo (models) : implement
		} );
	}

	private void processNamedEntityGraphs(AnnotationTarget annotationTarget) {
		annotationTarget.forEachAnnotation( NAMED_ENTITY_GRAPH, (usage) -> {
			// todo (models) : implement
		} );
	}

	private void processFilterDefinitions(AnnotationTarget annotationTarget) {
		annotationTarget.forEachAnnotation( HibernateAnnotations.FILTER_DEF, (usage) -> {
			// todo (models) : implement
		} );
	}
}
