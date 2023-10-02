/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.source.internal.jandex;

import org.jboss.jandex.Indexer;

import jakarta.persistence.*;

/**
 * Applies JPA classes needed to read the domain model to the Jandex index.
 *
 * @apiNote It is super important that this class only be reference by reflection
 * to avoid runtime dependency on JPA
 *
 * @author Steve Ebersole
 */
@SuppressWarnings("unused")
public class JpaAnnotationIndexer {
	public static final Class<?>[] SUPPORT_CLASSES = new Class[] {
			AccessType.class,
			AttributeConverter.class,
			CacheRetrieveMode.class,
			CacheStoreMode.class,
			CascadeType.class,
			ConstraintMode.class,
			DiscriminatorType.class,
			EnumType.class,
			FetchType.class,
			GenerationType.class,
			InheritanceType.class,
			LockModeType.class,
			ParameterMode.class,
			TemporalType.class
	};

	public static final Class<?>[] ANNOTATION_CLASSES = new Class[] {
			Access.class,
			AssociationOverride.class,
			AssociationOverrides.class,
			AttributeOverride.class,
			AttributeOverrides.class,
			Basic.class,
			Cacheable.class,
			CollectionTable.class,
			Column.class,
			ColumnResult.class,
			Convert.class,
			Converts.class,
			Converter.class,
			DiscriminatorColumn.class,
			DiscriminatorValue.class,
			ElementCollection.class,
			Embeddable.class,
			Embedded.class,
			EmbeddedId.class,
			Entity.class,
			EntityListeners.class,
			EntityResult.class,
			Enumerated.class,
			ExcludeDefaultListeners.class,
			ExcludeSuperclassListeners.class,
			FieldResult.class,
			ForeignKey.class,
			GeneratedValue.class,
			Id.class,
			IdClass.class,
			Index.class,
			Inheritance.class,
			JoinColumn.class,
			JoinColumns.class,
			JoinTable.class,
			Lob.class,
			ManyToMany.class,
			ManyToOne.class,
			MapKey.class,
			MapKeyClass.class,
			MapKeyColumn.class,
			MapKeyEnumerated.class,
			MapKeyJoinColumn.class,
			MapKeyJoinColumns.class,
			MapKeyTemporal.class,
			MappedSuperclass.class,
			MapsId.class,
			NamedAttributeNode.class,
			NamedEntityGraph.class,
			NamedEntityGraphs.class,
			NamedNativeQuery.class,
			NamedNativeQueries.class,
			NamedQuery.class,
			NamedQueries.class,
			NamedStoredProcedureQuery.class,
			NamedStoredProcedureQueries.class,
			NamedSubgraph.class,
			OneToMany.class,
			OneToOne.class,
			OrderBy.class,
			OrderColumn.class,
			PostLoad.class,
			PostPersist.class,
			PostRemove.class,
			PostUpdate.class,
			PrePersist.class,
			PreRemove.class,
			PreUpdate.class,
			PrimaryKeyJoinColumn.class,
			PrimaryKeyJoinColumns.class,
			QueryHint.class,
			SecondaryTable.class,
			SecondaryTables.class,
			SequenceGenerator.class,
			SequenceGenerators.class,
			SqlResultSetMapping.class,
			SqlResultSetMappings.class,
			StoredProcedureParameter.class,
			Table.class,
			TableGenerator.class,
			TableGenerators.class,
			Temporal.class,
			Transient.class,
			UniqueConstraint.class,
			Version.class
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
