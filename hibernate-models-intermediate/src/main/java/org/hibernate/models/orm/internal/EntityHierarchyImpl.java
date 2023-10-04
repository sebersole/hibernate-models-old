/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.orm.internal;

import java.util.Locale;
import java.util.function.Consumer;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.NaturalIdCache;
import org.hibernate.models.orm.spi.Caching;
import org.hibernate.models.orm.spi.EntityHierarchy;
import org.hibernate.models.orm.spi.EntityTypeMetadata;
import org.hibernate.models.orm.spi.HibernateAnnotations;
import org.hibernate.models.orm.spi.IdentifiableTypeMetadata;
import org.hibernate.models.orm.spi.JpaAnnotations;
import org.hibernate.models.orm.spi.NaturalIdCaching;
import org.hibernate.models.source.internal.AnnotationUsageHelper;
import org.hibernate.models.source.spi.AnnotationUsage;
import org.hibernate.models.source.spi.ClassDetails;
import org.hibernate.models.orm.spi.OrmModelBuildingContext;
import org.hibernate.cache.spi.access.AccessType;

import jakarta.persistence.Cacheable;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.SharedCacheMode;

import static org.hibernate.models.orm.internal.OrmModelLogging.ORM_MODEL_LOGGER;
import static org.hibernate.models.source.internal.AnnotationUsageHelper.extractValue;
import static org.hibernate.models.source.spi.AnnotationAttributeDescriptor.VALUE;

/**
 * @author Steve Ebersole
 */
public class EntityHierarchyImpl implements EntityHierarchy {
	private final EntityTypeMetadata rootEntityTypeMetadata;

	private final InheritanceType inheritanceType;
	private final Caching caching;
	private final NaturalIdCaching naturalIdCaching;

	// todo (models) : version?  row-id?  tenant-id?  others?

	public EntityHierarchyImpl(
			ClassDetails rootEntityClassDetails,
			jakarta.persistence.AccessType defaultAccessType,
			Consumer<IdentifiableTypeMetadata> typeConsumer,
			OrmModelBuildingContext processingContext) {
		this.rootEntityTypeMetadata = new EntityTypeMetadataImpl(
				rootEntityClassDetails,
				this,
				defaultAccessType,
				typeConsumer,
				processingContext
		);

		this.inheritanceType = determineInheritanceType( rootEntityTypeMetadata );

		rootEntityTypeMetadata.findAnnotation( JpaAnnotations.CACHEABLE );
		final AnnotationUsage<Cacheable> cacheableAnnotation = rootEntityClassDetails.getAnnotation( JpaAnnotations.CACHEABLE );
		final AnnotationUsage<Cache> cacheAnnotation = rootEntityClassDetails.getAnnotation( HibernateAnnotations.CACHE );
		final AnnotationUsage<NaturalIdCache> naturalIdCacheAnnotation = rootEntityClassDetails.getAnnotation( HibernateAnnotations.NATURAL_ID_CACHE );

		// todo (models) : get these from ???
		final AccessType implicitCacheAccessType = null;
		final SharedCacheMode sharedCacheMode = SharedCacheMode.NONE;

		switch ( sharedCacheMode ) {
			case NONE: {
				this.caching = new Caching( false );
				this.naturalIdCaching = new NaturalIdCaching( false );
				break;
			}
			case ALL: {
				this.caching = new Caching( cacheAnnotation, implicitCacheAccessType, rootEntityClassDetails.getName() );
				this.naturalIdCaching = new NaturalIdCaching( naturalIdCacheAnnotation, caching );
				break;
			}
			case DISABLE_SELECTIVE: {
				// Caching is disabled for `@Cacheable(false)`, enabled otherwise
				final boolean cached;
				if ( cacheableAnnotation == null ) {
					cached = true;
				}
				else {
					cached = AnnotationUsageHelper.extractValue( cacheableAnnotation, VALUE );
				}
				if ( cached ) {
					this.caching = new Caching( cacheAnnotation, implicitCacheAccessType, rootEntityClassDetails.getName() );
					this.naturalIdCaching = new NaturalIdCaching( naturalIdCacheAnnotation, caching );
				}
				else {
					this.caching = new Caching( false );
					this.naturalIdCaching = new NaturalIdCaching( false );
				}
				break;
			}
			default: {
				// ENABLE_SELECTIVE
				// UNSPECIFIED

				// Caching is enabled for all entities for <code>Cacheable(true)</code>
				// is specified.  All other entities are not cached.
				final boolean cached = cacheableAnnotation != null
						&& AnnotationUsageHelper.extractValue( cacheableAnnotation, VALUE, true );
				if ( cached ) {
					this.caching = new Caching( cacheAnnotation, implicitCacheAccessType, rootEntityClassDetails.getName() );
					this.naturalIdCaching = new NaturalIdCaching( naturalIdCacheAnnotation, caching );
				}
				else {
					this.caching = new Caching( false );
					this.naturalIdCaching = new NaturalIdCaching( false );
				}
			}
		}
	}

	private InheritanceType determineInheritanceType(EntityTypeMetadata root) {
		if ( ORM_MODEL_LOGGER.isDebugEnabled() ) {
			// Validate that there is no @Inheritance annotation further down the hierarchy
			ensureNoInheritanceAnnotationsOnSubclasses( root );
		}

		IdentifiableTypeMetadata current = root;
		while ( current != null ) {
			final InheritanceType inheritanceType = getLocallyDefinedInheritanceType( current.getClassDetails() );
			if ( inheritanceType != null ) {
				return inheritanceType;
			}

			current = current.getSuperType();
		}

		return InheritanceType.SINGLE_TABLE;
	}

	/**
	 * Find the InheritanceType from the locally defined {@link Inheritance} annotation,
	 * if one.  Returns {@code null} if {@link Inheritance} is not locally defined.
	 *
	 * @apiNote Used when building the {@link EntityHierarchy}
	 */
	private static InheritanceType getLocallyDefinedInheritanceType(ClassDetails managedClass) {
		final AnnotationUsage<Inheritance> localAnnotation = managedClass.getAnnotation( JpaAnnotations.INHERITANCE );
		if ( localAnnotation == null ) {
			return null;
		}

		return AnnotationUsageHelper.extractValue( localAnnotation, "strategy" );
	}

	private void ensureNoInheritanceAnnotationsOnSubclasses(IdentifiableTypeMetadata type) {
		type.forEachSubType( (subType) -> {
			if ( getLocallyDefinedInheritanceType( subType.getClassDetails() ) != null ) {
				ORM_MODEL_LOGGER.debugf(
						"@javax.persistence.Inheritance was specified on non-root entity [%s]; ignoring...",
						type.getClassDetails().getName()
				);
			}
			ensureNoInheritanceAnnotationsOnSubclasses( subType );
		} );
	}

	@Override
	public EntityTypeMetadata getRoot() {
		return rootEntityTypeMetadata;
	}

	@Override
	public InheritanceType getInheritanceType() {
		return inheritanceType;
	}

	@Override
	public Caching getCaching() {
		return caching;
	}

	@Override
	public NaturalIdCaching getNaturalIdCaching() {
		return naturalIdCaching;
	}

	@Override
	public String toString() {
		return String.format(
				Locale.ROOT,
				"EntityHierarchyImpl(`%s` (%s))",
				rootEntityTypeMetadata.getEntityName(),
				inheritanceType.name()
		);
	}
}
