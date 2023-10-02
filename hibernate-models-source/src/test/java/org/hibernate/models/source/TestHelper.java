/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.source;

import java.io.IOException;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.NClob;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZonedDateTime;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.ChronoLocalDateTime;
import java.time.chrono.ChronoZonedDateTime;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAdjuster;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Comment;
import org.hibernate.models.source.internal.SourceModelBuildingContextImpl;
import org.hibernate.models.source.internal.explicit.AnnotationDescriptorImpl;
import org.hibernate.models.source.internal.jandex.JandexIndexerHelper;
import org.hibernate.models.source.spi.AnnotationDescriptor;
import org.hibernate.models.source.spi.SourceModelBuildingContext;

import org.jboss.jandex.Index;
import org.jboss.jandex.Indexer;

import jakarta.persistence.Basic;
import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.PrimaryKeyJoinColumns;
import jakarta.persistence.SecondaryTable;
import jakarta.persistence.SecondaryTables;
import jakarta.persistence.Table;

import static org.hibernate.models.internal.SimpleClassLoading.SIMPLE_CLASS_LOADING;

/**
 * @author Steve Ebersole
 */
public class TestHelper {
	public static final AnnotationDescriptor<Column> COLUMN = createDescriptor( Column.class );
	public static final AnnotationDescriptor<Entity> ENTITY = createDescriptor( Entity.class );

	public static final AnnotationDescriptor<NamedQueries> NAMED_QUERIES = createDescriptor( NamedQueries.class );
	public static final AnnotationDescriptor<NamedQuery> NAMED_QUERY = createDescriptor( NamedQuery.class, NAMED_QUERIES );

	public static SourceModelBuildingContext createBuildingContext(Class<?>... modelClasses) {
		final Indexer indexer = new Indexer();
		JandexIndexerHelper.applyBaseline( indexer, SIMPLE_CLASS_LOADING );
		for ( Class<?> modelClass : modelClasses ) {
			try {
				indexer.indexClass( modelClass );
			}
			catch (IOException e) {
				throw new RuntimeException( e );
			}
		}

		final Index jandexIndex = indexer.complete();
		return new SourceModelBuildingContextImpl(
				SIMPLE_CLASS_LOADING,
				jandexIndex,
				null,
				(contributions, buildingContext1) -> {
					contributions.registerAnnotation( COLUMN );
					contributions.registerAnnotation( ENTITY );
					contributions.registerAnnotation( NAMED_QUERY );
					contributions.registerAnnotation( NAMED_QUERIES );
				}
		);
	}

	public static <A extends Annotation > AnnotationDescriptor<A> createDescriptor(Class<A> annotationType) {
		return createDescriptor( annotationType, null );
	}

	public static <A extends Annotation> AnnotationDescriptor<A> createDescriptor(
			Class<A> javaType,
			AnnotationDescriptor<?> repeatableContainer) {
		assert javaType != null;

		return AnnotationDescriptorImpl.buildDescriptor( javaType, repeatableContainer );
	}

	public static void applyStandardIndexing(Indexer indexer) {
		try {
			indexer.indexClass( Entity.class );
			indexer.indexClass( Table.class );
			indexer.indexClass( SecondaryTable.class );
			indexer.indexClass( SecondaryTables.class );
			indexer.indexClass( CustomAnnotation.class );
			indexer.indexClass( jakarta.persistence.Index.class );
			indexer.indexClass( NamedQuery.class );
			indexer.indexClass( NamedQueries.class );
			indexer.indexClass( jakarta.persistence.UniqueConstraint.class );
			indexer.indexClass( Cacheable.class );
			indexer.indexClass( Cache.class );
			indexer.indexClass( CacheConcurrencyStrategy.class );
			indexer.indexClass( PrimaryKeyJoinColumns.class );
			indexer.indexClass( PrimaryKeyJoinColumn.class );
			indexer.indexClass( Id.class );
			indexer.indexClass( Column.class );
			indexer.indexClass( Comment.class );
			indexer.indexClass( Basic.class );
			indexer.indexClass( Serializable.class );
			indexer.indexClass( Comparable.class );
			indexer.indexClass( CharSequence.class );
			indexer.indexClass( String.class );
			indexer.indexClass( Boolean.class );
			indexer.indexClass( Enum.class );
			indexer.indexClass( Byte.class );
			indexer.indexClass( Short.class );
			indexer.indexClass( Integer.class );
			indexer.indexClass( Long.class );
			indexer.indexClass( Double.class );
			indexer.indexClass( Float.class );
			indexer.indexClass( BigInteger.class );
			indexer.indexClass( BigDecimal.class );
			indexer.indexClass( Blob.class );
			indexer.indexClass( Clob.class );
			indexer.indexClass( NClob.class );
			indexer.indexClass( Instant.class );
			indexer.indexClass( LocalDate.class );
			indexer.indexClass( LocalTime.class );
			indexer.indexClass( LocalDateTime.class );
			indexer.indexClass( OffsetTime.class );
			indexer.indexClass( OffsetDateTime.class );
			indexer.indexClass( ZonedDateTime.class );
			indexer.indexClass( java.util.Date.class );
			indexer.indexClass( java.sql.Date.class );
			indexer.indexClass( java.sql.Time.class );
			indexer.indexClass( java.sql.Timestamp.class );
			indexer.indexClass( URL.class );
			indexer.indexClass( Collection.class );
			indexer.indexClass( Set.class );
			indexer.indexClass( List.class );
			indexer.indexClass( Map.class );
			indexer.indexClass( Comparator.class );
			indexer.indexClass( Comparable.class );
			indexer.indexClass( SortedSet.class );
			indexer.indexClass( SortedMap.class );

			indexer.indexClass( Temporal.class );
			indexer.indexClass( TemporalAdjuster.class );
			indexer.indexClass( TemporalAccessor.class );
			indexer.indexClass( ChronoLocalDate.class );
			indexer.indexClass( ChronoLocalDateTime.class );
			indexer.indexClass( ChronoZonedDateTime.class );
			indexer.indexClass( Cloneable.class );
			indexer.indexClass( Iterable.class );
			indexer.indexClass( Documented.class );
			indexer.indexClass( Target.class );
			indexer.indexClass( ElementType.class );
			indexer.indexClass( Retention.class );
			indexer.indexClass( RetentionPolicy.class );
			indexer.indexClass( Annotation.class );
		}
		catch (IOException e) {
			throw new RuntimeException( e );
		}
	}
}
