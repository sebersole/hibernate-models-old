/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.orm.internal;

import java.util.List;
import java.util.function.Consumer;

import org.hibernate.boot.model.naming.EntityNaming;
import org.hibernate.models.internal.StringHelper;
import org.hibernate.models.orm.spi.AttributeMetadata;
import org.hibernate.models.orm.spi.EntityHierarchy;
import org.hibernate.models.orm.spi.EntityTypeMetadata;
import org.hibernate.models.orm.spi.IdentifiableTypeMetadata;
import org.hibernate.models.orm.spi.JpaAnnotations;
import org.hibernate.models.orm.spi.OrmModelBuildingContext;
import org.hibernate.models.source.internal.AnnotationUsageHelper;
import org.hibernate.models.source.spi.AnnotationUsage;
import org.hibernate.models.source.spi.ClassDetails;

import jakarta.persistence.AccessType;
import jakarta.persistence.Entity;

import static java.util.Collections.emptyList;
import static org.hibernate.internal.util.StringHelper.unqualify;

/**
 * @author Steve Ebersole
 */
public class EntityTypeMetadataImpl
		extends AbstractIdentifiableTypeMetadata
		implements EntityTypeMetadata, EntityNaming {
	private final EntityHierarchy hierarchy;

	private final String entityName;
	private final String jpaEntityName;

	private final List<AttributeMetadata> attributeList;

//	private final String proxy;
//
//	private final String customLoaderQueryName;
//	private final String[] synchronizedTableNames;
//	private final int batchSize;
//	private final boolean isDynamicInsert;
//	private final boolean isDynamicUpdate;
//	private final boolean isSelectBeforeUpdate;
//	private final CustomSql customInsert;
//	private final CustomSql customUpdate;
//	private final CustomSql customDelete;
//	private final String discriminatorMatchValue;
//	private final boolean isLazy;

	/**
	 * This form is intended for construction of root Entity.
	 */
	public EntityTypeMetadataImpl(
			ClassDetails classDetails,
			EntityHierarchy hierarchy,
			AccessType defaultAccessType,
			Consumer<IdentifiableTypeMetadata> typeConsumer,
			OrmModelBuildingContext modelContext) {
		super( classDetails, hierarchy, true, defaultAccessType, typeConsumer, modelContext );

		this.hierarchy = hierarchy;

		// NOTE: there is no annotation for `entity-name`; it comes exclusively from
		// XML mappings.  by default, the `entityName` is simply the entity class name.
		// all of which `ClassDetails#getName` already handles for us
		this.entityName = getClassDetails().getName();

		final AnnotationUsage<Entity> entityAnnotation = classDetails.getAnnotation( JpaAnnotations.ENTITY );
		this.jpaEntityName = determineJpaEntityName( entityAnnotation, entityName );

//		this.synchronizedTableNames = determineSynchronizedTableNames();
//		this.batchSize = determineBatchSize();
//
//		this.customLoaderQueryName = determineCustomLoader();
//		this.customInsert = extractCustomSql( classDetails.getAnnotation( HibernateAnnotations.SQL_INSERT ) );
//		this.customUpdate = extractCustomSql( classDetails.getAnnotation( HibernateAnnotations.SQL_UPDATE ) );
//		this.customDelete = extractCustomSql( classDetails.getAnnotation( HibernateAnnotations.SQL_DELETE ) );
//
//		this.isDynamicInsert = decodeDynamicInsert();
//		this.isDynamicUpdate = decodeDynamicUpdate();
//		this.isSelectBeforeUpdate = decodeSelectBeforeUpdate();
//
//		// Proxy generation
//		final AnnotationUsage<Proxy> proxyAnnotation = classDetails.getAnnotation( HibernateAnnotations.PROXY );
//		if ( proxyAnnotation != null ) {
//			final AnnotationUsage.AnnotationAttributeValue lazyValue = proxyAnnotation.getAttributeValue( "lazy" );
//			if ( lazyValue != null ) {
//				this.isLazy = lazyValue.asBoolean();
//			}
//			else {
//				this.isLazy = true;
//			}
//
//			if ( this.isLazy ) {
//				final AnnotationUsage.AnnotationAttributeValue proxyClassValue = proxyAnnotation.getAttributeValue( "proxyClass" );
//				if ( proxyClassValue != null && !proxyClassValue.isDefaultValue() ) {
//					this.proxy = proxyClassValue.asString();
//				}
//				else {
//					this.proxy = null;
//				}
//			}
//			else {
//				this.proxy = null;
//			}
//		}
//		else {
//			// defaults are that it is lazy and that the class itself is the proxy class
//			this.isLazy = true;
//			this.proxy = getName();
//		}
//
//		final AnnotationUsage<DiscriminatorValue> discriminatorValueAnnotation = classDetails.getAnnotation( JpaAnnotations.DISCRIMINATOR_VALUE );
//		if ( discriminatorValueAnnotation != null ) {
//			final AnnotationUsage.AnnotationAttributeValue discriminatorValueValue = discriminatorValueAnnotation.getValueAttributeValue();
//			this.discriminatorMatchValue = discriminatorValueValue.asString();
//		}
//		else {
//			this.discriminatorMatchValue = null;
//		}

		this.attributeList = resolveAttributes();
	}

	/**
	 * This form is intended for construction of non-root Entity.
	 */
	public EntityTypeMetadataImpl(
			ClassDetails classDetails,
			EntityHierarchy hierarchy,
			AbstractIdentifiableTypeMetadata superType,
			Consumer<IdentifiableTypeMetadata> typeConsumer,
			OrmModelBuildingContext modelContext) {
		super( classDetails, hierarchy, superType, typeConsumer, modelContext );
		this.hierarchy = hierarchy;

		// NOTE: this is no annotation for `entity-name`.  it comes exclusively from
		// XML mappings.  by default, the `entityName` is simply the entity class name.
		// all of which `ClassDetails#getName` already handles for us
		this.entityName = getClassDetails().getName();

		final AnnotationUsage<Entity> entityAnnotation = classDetails.getAnnotation( JpaAnnotations.ENTITY );
		this.jpaEntityName = determineJpaEntityName( entityAnnotation, entityName );

		this.attributeList = resolveAttributes();
	}

	private String determineJpaEntityName(AnnotationUsage<Entity> entityAnnotation, String entityName) {
		final String name = AnnotationUsageHelper.extractValue( entityAnnotation, "name" );
		if ( StringHelper.isNotEmpty( name ) ) {
			return name;
		}
		return unqualify( entityName );
	}

	@Override
	protected List<AttributeMetadata> attributeList() {
		return attributeList;
	}

//	private String determineCustomLoader() {
//		final AnnotationUsage<Loader> loaderAnnotation = getManagedClass().getAnnotation( HibernateAnnotations.LOADER );
//		if ( loaderAnnotation != null ) {
//			final AnnotationUsage.AnnotationAttributeValue namedQueryValue = loaderAnnotation.getAttributeValue( "namedQuery" );
//			return namedQueryValue.asString();
//		}
//		return null;
//	}
//
//	private String[] determineSynchronizedTableNames() {
//		final AnnotationUsage<Synchronize> synchronizeAnnotation = getManagedClass().getAnnotation( HibernateAnnotations.SYNCHRONIZE );
//		if ( synchronizeAnnotation != null ) {
//			return synchronizeAnnotation.getValueAttributeValue().getValue();
//		}
//		return StringHelper.EMPTY_STRINGS;
//	}
//
//	private int determineBatchSize() {
//		final AnnotationUsage<BatchSize> batchSizeAnnotation = getManagedClass().getAnnotation( HibernateAnnotations.BATCH_SIZE );
//		if ( batchSizeAnnotation != null ) {
//			return batchSizeAnnotation.getAttributeValue( "size" ).asInt();
//		}
//		return -1;
//	}
//
//	private boolean decodeDynamicInsert() {
//		final AnnotationUsage<DynamicInsert> dynamicInsertAnnotation = getManagedClass().getAnnotation( HibernateAnnotations.DYNAMIC_INSERT );
//		if ( dynamicInsertAnnotation == null ) {
//			return false;
//		}
//
//		return dynamicInsertAnnotation.getValueAttributeValue().asBoolean();
//	}
//
//	private boolean decodeDynamicUpdate() {
//		final AnnotationUsage<DynamicUpdate> dynamicUpdateAnnotation = getManagedClass().getAnnotation( HibernateAnnotations.DYNAMIC_UPDATE );
//		if ( dynamicUpdateAnnotation == null ) {
//			return false;
//		}
//		return dynamicUpdateAnnotation.getValueAttributeValue().asBoolean();
//	}
//
//	private boolean decodeSelectBeforeUpdate() {
//		final AnnotationUsage<SelectBeforeUpdate> selectBeforeUpdateAnnotation = getManagedClass().getAnnotation( HibernateAnnotations.SELECT_BEFORE_UPDATE );
//		if ( selectBeforeUpdateAnnotation == null ) {
//			return false;
//		}
//		return selectBeforeUpdateAnnotation.getValueAttributeValue().asBoolean();
//	}

	@Override
	public EntityHierarchy getHierarchy() {
		return hierarchy;
	}

//	@Override
//	public EntityNaming getEntityNaming() {
//		return this;
//	}

	@Override
	public String getEntityName() {
		return entityName;
	}

	@Override
	public String getJpaEntityName() {
		return jpaEntityName;
	}

	@Override
	public String getClassName() {
		return getClassDetails().getClassName();
	}

//	@Override
//	public String getCustomLoaderQueryName() {
//		return customLoaderQueryName;
//	}
//
//	public String[] getSynchronizedTableNames() {
//		return synchronizedTableNames;
//	}
//
//	public int getBatchSize() {
//		return batchSize;
//	}
//
//	public boolean isDynamicInsert() {
//		return isDynamicInsert;
//	}
//
//	public boolean isDynamicUpdate() {
//		return isDynamicUpdate;
//	}
//
//	public boolean isSelectBeforeUpdate() {
//		return isSelectBeforeUpdate;
//	}
//
//	public CustomSql getCustomInsert() {
//		return customInsert;
//	}
//
//	public CustomSql getCustomUpdate() {
//		return customUpdate;
//	}
//
//	public CustomSql getCustomDelete() {
//		return customDelete;
//	}
//
//	public String getDiscriminatorMatchValue() {
//		return discriminatorMatchValue;
//	}
//
//	public boolean isLazy() {
//		return isLazy;
//	}
//
//	public String getProxy() {
//		return proxy;
//	}


//	@Override
//	public MetadataBuildingContext getBuildingContext() {
//		return getModelContext()getModelProcessingContext().getMetadataBuildingContext();
//	}
}
