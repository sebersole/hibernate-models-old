/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.source.internal.jandex;

import org.hibernate.Incubating;
import org.hibernate.Internal;
import org.hibernate.Remove;
import org.hibernate.annotations.*;
import org.hibernate.binder.AttributeBinder;
import org.hibernate.binder.TypeBinder;
import org.hibernate.binder.internal.AttributeAccessorBinder;
import org.hibernate.binder.internal.CommentBinder;
import org.hibernate.binder.internal.TenantIdBinder;
import org.hibernate.boot.model.relational.ExportableProducer;
import org.hibernate.generator.BeforeExecutionGenerator;
import org.hibernate.generator.EventType;
import org.hibernate.generator.Generator;
import org.hibernate.generator.OnExecutionGenerator;
import org.hibernate.generator.internal.CurrentTimestampGeneration;
import org.hibernate.generator.internal.GeneratedAlwaysGeneration;
import org.hibernate.generator.internal.GeneratedGeneration;
import org.hibernate.generator.internal.SourceGeneration;
import org.hibernate.generator.internal.TenantIdGeneration;
import org.hibernate.id.Configurable;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.metamodel.CollectionClassification;
import org.hibernate.property.access.spi.PropertyAccessStrategy;
import org.hibernate.tuple.VmValueGeneration;
import org.hibernate.type.descriptor.java.BasicJavaType;
import org.hibernate.type.descriptor.java.MutabilityPlan;
import org.hibernate.usertype.CompositeUserType;
import org.hibernate.usertype.UserCollectionType;
import org.hibernate.usertype.UserType;

import org.jboss.jandex.Indexer;

import jakarta.persistence.FetchType;

/**
 * Applies Hibernate classes needed to read the domain model to the Jandex index.
 *
 * @apiNote It is super important that this class only be reference by reflection
 * to avoid runtime dependency on Hibernate ORM
 *
 * @author Steve Ebersole
 */
@SuppressWarnings("unused")
public class OrmAnnotationIndexer {
	public static final Class<?>[] SUPPORT_CLASSES = new Class[] {
			AttributeBinder.class,
			AttributeAccessorBinder.class,
			BasicJavaType.class,
			BeforeExecutionGenerator.class,
			CacheConcurrencyStrategy.class,
			CacheModeType.class,
			CascadeType.class,
			CollectionClassification.class,
			CommentBinder.class,
			CompositeUserType.class,
			Configurable.class,
			CurrentTimestampGeneration.class,
			DialectOverride.class,
			org.hibernate.metamodel.spi.EmbeddableInstantiator.class,
			EventType.class,
			ExportableProducer.class,
			FetchMode.class,
			FetchType.class,
			FlushModeType.class,
			GeneratedAlwaysGeneration.class,
			GeneratedGeneration.class,
			GenerationTime.class,
			Generator.class,
			IdentifierGenerator.class,
			org.hibernate.metamodel.spi.Instantiator.class,
			org.hibernate.type.descriptor.java.JavaType.class,
			org.hibernate.type.descriptor.jdbc.JdbcType.class,
			LazyCollectionOption.class,
			LazyToOneOption.class,
			MutabilityPlan.class,
			NotFoundAction.class,
			OnDeleteAction.class,
			OnExecutionGenerator.class,
			OptimisticLockType.class,
			PolymorphismType.class,
			PropertyAccessStrategy.class,
			ResultCheckStyle.class,
			SourceGeneration.class,
			SourceType.class,
			TenantIdBinder.class,
			TenantIdGeneration.class,
			TimeZoneStorageType.class,
			TypeBinder.class,
			UserCollectionType.class,
			UserType.class,
			org.hibernate.id.uuid.UuidGenerator.class,
			VmValueGeneration.class
	};

	public static final Class<?>[] ANNOTATION_CLASSES = new Class[] {
			Any.class,
			AnyDiscriminator.class,
			AnyDiscriminatorValue.class,
			AnyDiscriminatorValues.class,
			AnyKeyJavaClass.class,
			AnyKeyJavaType.class,
			AnyKeyJdbcType.class,
			AnyKeyJdbcTypeCode.class,
			AttributeAccessor.class,
			AttributeBinderType.class,
			Bag.class,
			BatchSize.class,
			Cache.class,
			Cascade.class,
			Check.class,
			Checks.class,
			CollectionId.class,
			CollectionIdJavaType.class,
			CollectionIdJdbcType.class,
			CollectionIdJdbcTypeCode.class,
			CollectionIdMutability.class,
			CollectionIdType.class,
			CollectionType.class,
			CollectionTypeRegistration.class,
			CollectionTypeRegistrations.class,
			ColumnDefault.class,
			ColumnTransformer.class,
			ColumnTransformers.class,
			Comment.class,
			Comments.class,
			CompositeType.class,
			CompositeTypeRegistration.class,
			CompositeTypeRegistrations.class,
			ConverterRegistration.class,
			ConverterRegistrations.class,
			CreationTimestamp.class,
			CurrentTimestamp.class,
			DiscriminatorFormula.class,
			DiscriminatorOptions.class,
			DynamicInsert.class,
			DynamicUpdate.class,
			EmbeddableInstantiator.class,
			EmbeddableInstantiatorRegistration.class,
			EmbeddableInstantiatorRegistrations.class,
			Fetch.class,
			FetchProfile.class,
			FetchProfiles.class,
			Filter.class,
			Filters.class,
			FilterDef.class,
			FilterDefs.class,
			FilterJoinTable.class,
			FilterJoinTables.class,
			ForeignKey.class,
			Formula.class,
			Generated.class,
			GeneratedColumn.class,
			GeneratorType.class,
			GenericGenerator.class,
			GenericGenerators.class,
			IdGeneratorType.class,
			Immutable.class,
			Imported.class,
			Incubating.class,
			Index.class,
			IndexColumn.class,
			Instantiator.class,
			Internal.class,
			JavaType.class,
			JavaTypeRegistration.class,
			JavaTypeRegistrations.class,
			JdbcType.class,
			JdbcTypeCode.class,
			JdbcTypeRegistration.class,
			JdbcTypeRegistrations.class,
			JoinColumnOrFormula.class,
			JoinColumnsOrFormulas.class,
			JoinFormula.class,
			LazyCollection.class,
			LazyGroup.class,
			LazyToOne.class,
			ListIndexBase.class,
			ListIndexJavaType.class,
			ListIndexJdbcType.class,
			ListIndexJdbcTypeCode.class,
			Loader.class,
			ManyToAny.class,
			MapKeyCompositeType.class,
			MapKeyJavaType.class,
			MapKeyJdbcType.class,
			MapKeyJdbcTypeCode.class,
			MapKeyMutability.class,
			MapKeyType.class,
			Mutability.class,
			NamedNativeQuery.class,
			NamedNativeQueries.class,
			NamedQuery.class,
			NamedQueries.class,
			Nationalized.class,
			NaturalId.class,
			NaturalIdCache.class,
			NotFound.class,
			OnDelete.class,
			OptimisticLock.class,
			OptimisticLocking.class,
			OrderBy.class,
			ParamDef.class,
			Parameter.class,
			Parent.class,
			PartitionKey.class,
			Persister.class,
			Polymorphism.class,
			Proxy.class,
			Remove.class,
			RowId.class,
			SecondaryRow.class,
			SecondaryRows.class,
			SelectBeforeUpdate.class,
			SortComparator.class,
			SortNatural.class,
			Source.class,
			SQLDelete.class,
			SQLDeletes.class,
			SQLDeleteAll.class,
			SqlFragmentAlias.class,
			SQLInsert.class,
			SQLInserts.class,
			SQLUpdate.class,
			SQLUpdates.class,
			Struct.class,
			Subselect.class,
			Synchronize.class,
			Table.class,
			Tables.class,
			Target.class,
			TenantId.class,
			TimeZoneColumn.class,
			TimeZoneStorage.class,
			Type.class,
			TypeBinderType.class,
			TypeRegistration.class,
			TypeRegistrations.class,
			UpdateTimestamp.class,
			UuidGenerator.class,
			ValueGenerationType.class,
			Where.class,
			WhereJoinTable.class,

			DialectOverride.Check.class,
			DialectOverride.Checks.class,
			DialectOverride.OrderBy.class,
			DialectOverride.OrderBys.class,
			DialectOverride.ColumnDefault.class,
			DialectOverride.ColumnDefaults.class,
			DialectOverride.GeneratedColumn.class,
			DialectOverride.GeneratedColumns.class,
			DialectOverride.DiscriminatorFormula.class,
			DialectOverride.DiscriminatorFormulas.class,
			DialectOverride.Formula.class,
			DialectOverride.Formulas.class,
			DialectOverride.JoinFormula.class,
			DialectOverride.JoinFormulas.class,
			DialectOverride.Where.class,
			DialectOverride.Wheres.class,
			DialectOverride.Filters.class,
			DialectOverride.FilterOverrides.class,
			DialectOverride.FilterDefs.class,
			DialectOverride.FilterDefOverrides.class,
			DialectOverride.Version.class,
			DialectOverride.OverridesAnnotation.class
	};

	public static void apply(Indexer indexer) {
		try {
			for ( int i = 0; i < SUPPORT_CLASSES.length; i++ ) {
				indexer.indexClass( SUPPORT_CLASSES[i] );
			}

			for ( int i = 0; i < ANNOTATION_CLASSES.length; i++ ) {
				indexer.indexClass( ANNOTATION_CLASSES[i] );
			}
		}
		catch (Exception e) {
			throw new JandexIndexerHelper.JandexIndexingException( e );
		}

	}
}
