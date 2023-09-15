/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.orm.internal;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.function.Consumer;

import org.hibernate.MappingException;
import org.hibernate.models.orm.spi.EntityHierarchy;
import org.hibernate.models.orm.spi.IdentifiableTypeMetadata;
import org.hibernate.models.orm.spi.JpaAnnotations;
import org.hibernate.models.orm.spi.OrmModelBuildingContext;
import org.hibernate.models.source.internal.AnnotationUsageHelper;
import org.hibernate.models.source.spi.AnnotationUsage;
import org.hibernate.models.source.spi.ClassDetails;
import org.hibernate.models.source.spi.ClassDetailsRegistry;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;

import static org.hibernate.models.source.spi.AnnotationAttributeDescriptor.VALUE;


/**
 * @author Steve Ebersole
 */
public abstract class AbstractIdentifiableTypeMetadata
		extends AbstractManagedTypeMetadata
		implements IdentifiableTypeMetadata {
	private final EntityHierarchy hierarchy;
	private final AbstractIdentifiableTypeMetadata superType;
	private final Set<IdentifiableTypeMetadata> subTypes = new HashSet<>();

	private final AccessType accessType;


	/**
	 * This form is intended for construction of root Entity, and any of
	 * its MappedSuperclasses
	 *
	 * @param classDetails The Entity/MappedSuperclass class descriptor
	 * @param hierarchy Details about the hierarchy
	 * @param isRootEntity Whether this descriptor is for the root entity itself, or
	 * one of its mapped-superclasses.
	 * @param accessType The default AccessType for the hierarchy
	 * @param processingContext The context
	 */
	public AbstractIdentifiableTypeMetadata(
			ClassDetails classDetails,
			EntityHierarchy hierarchy,
			boolean isRootEntity,
			AccessType accessType,
			Consumer<IdentifiableTypeMetadata> typeConsumer,
			OrmModelBuildingContext processingContext) {
		super( classDetails, processingContext );

		typeConsumer.accept( this );

		this.hierarchy = hierarchy;
		this.accessType = determineAccessType( accessType );

		// walk up
		this.superType = walkRootSuperclasses( classDetails, accessType, typeConsumer );

		if ( isRootEntity ) {
			// walk down
			walkSubclasses( classDetails, this, this.accessType, typeConsumer );
		}

		// the idea here is to collect up class-level annotations and to apply
		// the maps from supers
		collectConversionInfo();
		collectAttributeOverrides();
		collectAssociationOverrides();
	}

	private AccessType determineAccessType(AccessType defaultAccessType) {
		final AnnotationUsage<Access> annotation = getClassDetails().getAnnotation( JpaAnnotations.ACCESS );
		if ( annotation != null ) {
			return AnnotationUsageHelper.extractValue( annotation, VALUE );
		}

		return defaultAccessType;
	}

	/**
	 * This form is intended for cases where the entity/mapped-superclass
	 * is part of the root subclass tree.
	 *
	 * @param classDetails The entity/mapped-superclass class descriptor
	 * @param hierarchy The hierarchy
	 * @param superType The metadata for the super type.
	 * @param processingContext The binding context
	 */
	public AbstractIdentifiableTypeMetadata(
			ClassDetails classDetails,
			EntityHierarchy hierarchy,
			AbstractIdentifiableTypeMetadata superType,
			Consumer<IdentifiableTypeMetadata> typeConsumer,
			OrmModelBuildingContext processingContext) {
		super( classDetails, processingContext );

		typeConsumer.accept( this );

		this.hierarchy = hierarchy;
		this.superType = superType;
		this.accessType = determineAccessType( superType.getAccessType() );

		// the idea here is to collect up class-level annotations and to apply
		// the maps from supers
		collectConversionInfo();
		collectAttributeOverrides();
		collectAssociationOverrides();
	}

	private AbstractIdentifiableTypeMetadata walkRootSuperclasses(
			ClassDetails classDetails,
			AccessType hierarchyAccessType,
			Consumer<IdentifiableTypeMetadata> typeConsumer) {
		final ClassDetails superTypeClassDetails = classDetails.getSuperType();
		if ( superTypeClassDetails == null ) {
			return null;
		}

		// make triple sure there is no @Entity annotation
		if ( isEntity( superTypeClassDetails ) ) {
			throw new MappingException(
					String.format(
							Locale.ENGLISH,
							"Unexpected @Entity [%s] as MappedSuperclass of entity hierarchy",
							superTypeClassDetails.getName()
					)
			);
		}
		else if ( isMappedSuperclass( superTypeClassDetails ) ) {
			final MappedSuperclassTypeMetadataImpl superType = new MappedSuperclassTypeMetadataImpl(
					superTypeClassDetails,
					getHierarchy(),
					hierarchyAccessType,
					typeConsumer,
					getModelContext()
			);
			superType.addSubclass( this );
			return superType;
		}
		else {
			// otherwise, we might have an "intermediate" subclass
			if ( superTypeClassDetails.getSuperType() != null ) {
				return walkRootSuperclasses( superTypeClassDetails, hierarchyAccessType, typeConsumer );
			}
			else {
				return null;
			}
		}
	}

	protected void addSubclass(IdentifiableTypeMetadata subclass) {
		subTypes.add( subclass );
	}

	protected boolean isMappedSuperclass(ClassDetails classDetails) {
		return classDetails.getAnnotation( JpaAnnotations.MAPPED_SUPERCLASS ) != null;
	}

	protected boolean isEntity(ClassDetails classDetails) {
		return classDetails.getAnnotation( JpaAnnotations.ENTITY ) != null;
	}

	private void walkSubclasses(
			ClassDetails classDetails,
			AbstractIdentifiableTypeMetadata superType,
			AccessType defaultAccessType,
			Consumer<IdentifiableTypeMetadata> typeConsumer) {
		final ClassDetailsRegistry classDetailsRegistry = getModelContext().getSourceModel().getClassDetailsRegistry();
		classDetailsRegistry.forEachDirectSubType( classDetails.getName(), (subTypeManagedClass) -> {
			final AbstractIdentifiableTypeMetadata subTypeMetadata;
			if ( isEntity( subTypeManagedClass ) ) {
				subTypeMetadata = new EntityTypeMetadataImpl(
						subTypeManagedClass,
						getHierarchy(),
						superType,
						typeConsumer,
						getModelContext()
				);
				superType.addSubclass( subTypeMetadata );
			}
			else if ( isMappedSuperclass( subTypeManagedClass ) ) {
				subTypeMetadata = new MappedSuperclassTypeMetadataImpl(
						subTypeManagedClass,
						getHierarchy(),
						superType,
						typeConsumer,
						getModelContext()
				);
				superType.addSubclass( subTypeMetadata );
			}
			else {
				subTypeMetadata = superType;
			}

			walkSubclasses( subTypeManagedClass, subTypeMetadata, defaultAccessType, typeConsumer );
		} );
	}

	@Override
	public EntityHierarchy getHierarchy() {
		return hierarchy;
	}

	@Override
	public boolean isAbstract() {
		return getClassDetails().isAbstract();
	}

	@Override
	public IdentifiableTypeMetadata getSuperType() {
		return superType;
	}

	@Override
	public boolean hasSubTypes() {
		// assume this is called only after its constructor is complete
		return !subTypes.isEmpty();
	}

	@Override
	public int getNumberOfSubTypes() {
		return subTypes.size();
	}

	@Override
	public void forEachSubType(Consumer<IdentifiableTypeMetadata> consumer) {
		// assume this is called only after its constructor is complete
		subTypes.forEach( consumer );
	}

	@Override
	public Iterable<IdentifiableTypeMetadata> getSubTypes() {
		// assume this is called only after its constructor is complete
		return subTypes;
	}

	@Override
	public AccessType getAccessType() {
		return accessType;
	}

	protected void collectConversionInfo() {
		// we only need to do this on root
	}

	protected void collectAttributeOverrides() {
		// we only need to do this on root
	}

	protected void collectAssociationOverrides() {
		// we only need to do this on root
	}
}
