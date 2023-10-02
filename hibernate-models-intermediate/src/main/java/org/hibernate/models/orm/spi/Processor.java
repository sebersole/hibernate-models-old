/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.orm.spi;

import java.util.HashSet;
import java.util.Set;

import org.hibernate.models.orm.internal.ProcessResultImpl;
import org.hibernate.models.source.spi.ClassDetails;

import static org.hibernate.models.orm.internal.EntityHierarchyBuilder.createEntityHierarchies;
import static org.hibernate.models.orm.internal.EntityHierarchyBuilder.isRoot;

/**
 * @author Steve Ebersole
 */
public class Processor {
	public static ProcessResult process(OrmModelBuildingContext mappingBuildingContext) {
		final Set<ClassDetails> rootEntities = new HashSet<>();
		final Set<ClassDetails> mappedSuperClasses = new HashSet<>();

		mappingBuildingContext.getSourceModel().getClassDetailsRegistry().forEachClassDetails( (classDetails) -> {
			if ( classDetails.getAnnotation( JpaAnnotations.MAPPED_SUPERCLASS ) != null ) {
				mappedSuperClasses.add( classDetails );
				processIdentifiableType( classDetails, mappingBuildingContext );
			}
			else if ( classDetails.getAnnotation( JpaAnnotations.ENTITY ) != null ) {
				if ( isRoot( classDetails ) ) {
					rootEntities.add( classDetails );
				}
				processIdentifiableType( classDetails, mappingBuildingContext );
			}
			else {
				processNonIdentifiableType( classDetails, mappingBuildingContext );
			}
		} );

		final Set<EntityHierarchy> entityHierarchies = createEntityHierarchies(
				rootEntities,
				(identifiableType) -> {
					if ( identifiableType instanceof MappedSuperclassTypeMetadata ) {
						mappedSuperClasses.remove( identifiableType.getClassDetails() );
					}
				},
				mappingBuildingContext
		);

		return new ProcessResultImpl( entityHierarchies );
	}

	private static void processIdentifiableType(
			ClassDetails classDetails,
			OrmModelBuildingContext mappingBuildingContext) {

	}

	private static void processNonIdentifiableType(
			ClassDetails classDetails,
			OrmModelBuildingContext mappingBuildingContext) {

	}

}
