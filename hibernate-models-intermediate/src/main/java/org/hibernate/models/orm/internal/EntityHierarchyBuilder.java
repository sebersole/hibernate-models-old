/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.orm.internal;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import org.hibernate.models.orm.AccessTypeDeterminationException;
import org.hibernate.models.orm.spi.EntityHierarchy;
import org.hibernate.models.orm.spi.IdentifiableTypeMetadata;
import org.hibernate.models.orm.spi.OrmModelBuildingContext;
import org.hibernate.models.source.spi.AnnotationTarget;
import org.hibernate.models.source.spi.AnnotationUsage;
import org.hibernate.models.source.spi.ClassDetails;
import org.hibernate.models.source.spi.ClassDetailsRegistry;
import org.hibernate.models.source.spi.FieldDetails;
import org.hibernate.models.orm.spi.JpaAnnotations;
import org.hibernate.models.source.spi.MethodDetails;
import org.hibernate.internal.util.collections.CollectionHelper;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;

import static org.hibernate.models.source.internal.SourceModelLogging.SOURCE_MODEL_LOGGER;

/**
 * Builds {@link EntityHierarchy} references from
 * {@linkplain ClassDetailsRegistry#forEachClassDetails managed classes}.
 *
 * @author Steve Ebersole
 */
public class EntityHierarchyBuilder {

	/**
	 * Pre-processes the annotated entities from the index and create a set of entity hierarchies which can be bound
	 * to the metamodel.
	 *
	 * @param typeConsumer Callback for any identifiable-type metadata references
	 * @param buildingContext The binding context, giving access to needed services and information
	 *
	 * @return a set of {@code EntityHierarchySource} instances.
	 */
	public static Set<EntityHierarchy> createEntityHierarchies(
			Set<ClassDetails> rootEntities,
			Consumer<IdentifiableTypeMetadata> typeConsumer,
			OrmModelBuildingContext buildingContext) {
		return new EntityHierarchyBuilder( buildingContext ).process( rootEntities, typeConsumer );
	}

	/**
	 * Pre-processes the annotated entities from the index and create a set of entity hierarchies which can be bound
	 * to the metamodel.
	 *
	 * @param typeConsumer Callback for any identifiable-type metadata references
	 * @param buildingContext The binding context, giving access to needed services and information
	 *
	 * @return a set of {@code EntityHierarchySource} instances.
	 */
	public static Set<EntityHierarchy> createEntityHierarchies(
			Consumer<IdentifiableTypeMetadata> typeConsumer,
			OrmModelBuildingContext buildingContext) {
		return createEntityHierarchies(
				collectRootEntityTypes( buildingContext.getSourceModel().getClassDetailsRegistry() ),
				typeConsumer,
				buildingContext
		);
	}

	private final OrmModelBuildingContext modelContext;

	private final Set<ClassDetails> allKnownMappedSuperclassTypes = new HashSet<>();

	public EntityHierarchyBuilder(OrmModelBuildingContext modelContext) {
		this.modelContext = modelContext;
	}

	private Set<EntityHierarchy> process(
			Set<ClassDetails> rootEntities,
			Consumer<IdentifiableTypeMetadata> typeConsumer) {
		final Set<EntityHierarchy> hierarchies = CollectionHelper.setOfSize( rootEntities.size() );

		rootEntities.forEach( (rootEntity) -> {
			final AccessType defaultAccessType = determineDefaultAccessTypeForHierarchy( rootEntity );
			hierarchies.add( new EntityHierarchyImpl(
					rootEntity,
					defaultAccessType,
					typeConsumer,
					modelContext
			) );
		} );

		if ( SOURCE_MODEL_LOGGER.isDebugEnabled() ) {
			warnAboutUnusedMappedSuperclasses( hierarchies );
		}

		return hierarchies;
	}

	private void warnAboutUnusedMappedSuperclasses(Set<EntityHierarchy> hierarchies) {
		assert SOURCE_MODEL_LOGGER.isDebugEnabled();

		for ( EntityHierarchy hierarchy : hierarchies ) {
			walkUp( hierarchy.getRoot() );
			walkDown( hierarchy.getRoot() );
		}

		// At this point, anything left in `allKnownMappedSuperclassTypes` is unused...
		for ( ClassDetails unusedType : allKnownMappedSuperclassTypes ) {
			SOURCE_MODEL_LOGGER.debugf(
					"Encountered MappedSuperclass [%s] which was unused in any entity hierarchies",
					unusedType.getName()
			);
		}

		allKnownMappedSuperclassTypes.clear();
	}

	private void walkDown(IdentifiableTypeMetadata type) {
		for ( IdentifiableTypeMetadata subType : type.getSubTypes() ) {
			allKnownMappedSuperclassTypes.remove( type.getClassDetails() );
			walkDown( subType );
		}
	}

	private void walkUp(IdentifiableTypeMetadata type) {
		if ( type != null ) {
			allKnownMappedSuperclassTypes.remove( type.getClassDetails() );
			walkUp( type.getSuperType() );
		}
	}

	private AccessType determineDefaultAccessTypeForHierarchy(ClassDetails rootEntityType) {
		assert rootEntityType != null;

		ClassDetails current = rootEntityType;
		while ( current != null ) {
			// look for `@Access` on the class
			final AnnotationUsage<Access> accessAnnotation = current.getAnnotation( JpaAnnotations.ACCESS );
			if ( accessAnnotation != null ) {
				return (AccessType) accessAnnotation.getAttributeValue( "value" ).getValue();
			}

			// look for `@Id` or `@EmbeddedId`
			final AnnotationTarget idMember = determineIdMember( current );
			if ( idMember != null ) {
				switch ( idMember.getKind() ) {
					case FIELD: {
						return AccessType.FIELD;
					}
					case METHOD: {
						return AccessType.PROPERTY;
					}
					default: {
						throw new IllegalStateException( "@Id / @EmbeddedId found on target other than field or method : " + idMember );
					}
				}
			}

			current = current.getSuperType();
		}

		// 2.3.1 Default Access Type
		//    It is an error if a default access type cannot be determined and an access type is not explicitly specified
		//    by means of annotations or the XML descriptor.

		throw new AccessTypeDeterminationException( rootEntityType );
	}

	private AnnotationTarget determineIdMember(ClassDetails current) {
		final List<MethodDetails> methods = current.getMethods();
		for ( int i = 0; i < methods.size(); i++ ) {
			final MethodDetails methodDetails = methods.get( i );
			if ( methodDetails.getAnnotation( JpaAnnotations.ID ) != null
					|| methodDetails.getAnnotation( JpaAnnotations.EMBEDDED_ID ) != null ) {
				return methodDetails;
			}
		}

		final List<FieldDetails> fields = current.getFields();
		for ( int i = 0; i < fields.size(); i++ ) {
			final FieldDetails fieldDetails = fields.get( i );
			if ( fieldDetails.getAnnotation( JpaAnnotations.ID ) != null
					|| fieldDetails.getAnnotation( JpaAnnotations.EMBEDDED_ID ) != null ) {
				return fieldDetails;
			}
		}

		return null;
	}

	private Set<ClassDetails> collectRootEntityTypes() {
		return collectRootEntityTypes( modelContext.getSourceModel().getClassDetailsRegistry() );
	}

	private static Set<ClassDetails> collectRootEntityTypes(ClassDetailsRegistry classDetailsRegistry) {
		final Set<ClassDetails> collectedTypes = new HashSet<>();

		classDetailsRegistry.forEachClassDetails( (managedType) -> {
			if ( managedType.getAnnotation( JpaAnnotations.ENTITY ) != null
					&& isRoot( managedType ) ) {
				collectedTypes.add( managedType );
			}
		} );

		return collectedTypes;
	}

	public static boolean isRoot(ClassDetails classInfo) {
		// perform a series of opt-out checks against the super-type hierarchy

		// an entity is considered a root of the hierarchy if:
		// 		1) it has no super-types
		//		2) its super types contain no entities (MappedSuperclasses are allowed)

		if ( classInfo.getSuperType() == null ) {
			return true;
		}

		ClassDetails current = classInfo.getSuperType();
		while (  current != null ) {
			if ( current.getAnnotation( JpaAnnotations.ENTITY ) != null ) {
				// a super type has `@Entity`, cannot be root
				return false;
			}
			current = current.getSuperType();
		}

		// if we hit no opt-outs we have a root
		return true;
	}


	/**
	 * Used in tests
	 */
	public static Set<EntityHierarchy> createEntityHierarchies(OrmModelBuildingContext processingContext) {
		return new EntityHierarchyBuilder( processingContext ).process(
				collectRootEntityTypes( processingContext.getSourceModel().getClassDetailsRegistry() ),
				EntityHierarchyBuilder::ignore
		);
	}

	private static void ignore(IdentifiableTypeMetadata it) {}
}
