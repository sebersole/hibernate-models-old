/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.source.internal.jandex;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import org.hibernate.models.internal.SimpleClassLoading;
import org.hibernate.models.internal.StringHelper;
import org.hibernate.models.source.ModelsException;
import org.hibernate.models.source.spi.AnnotationDescriptor;
import org.hibernate.models.spi.ClassLoading;

import org.jboss.jandex.DotName;
import org.jboss.jandex.Indexer;
import org.jboss.jandex.Type;

import jakarta.persistence.AssociationOverrides;
import jakarta.persistence.Basic;

/**
 * @author Steve Ebersole
 */
public class JandexIndexerHelper {
	public static final String ORM_CHECK_NAME = "org.hibernate.annotations.NaturalId";
	public static final String ORM_ANNOTATION_INDEXER = "org.hibernate.models.source.internal.jandex.OrmAnnotationIndexer";

	public static final String JPA_CHECK_NAME = "jakarta.persistence.Entity";
	public static final String JPA_ANNOTATION_INDEXER = "org.hibernate.models.source.internal.jandex.JpaAnnotationIndexer";

	public static void apply(String className, Indexer indexer) {
		apply( className, indexer, SimpleClassLoading.SIMPLE_CLASS_LOADING );
	}

	public static void apply(String className, Indexer indexer, ClassLoading classLoading) {
		final String resourceName = StringHelper.classNameToResourceName( className );
		final URL resource = classLoading.locateResource( resourceName );
		if ( resource == null ) {
			throw new JandexIndexingException( className );
		}
		try (InputStream inputStream = resource.openStream()) {
			indexer.index( inputStream );
		}
		catch (IOException e) {
			throw new JandexIndexingException( e );
		}
	}

	public static void applyBaseline(Indexer indexer, ClassLoading classLoading) {
		try {
			indexer.indexClass( Object.class );
			indexer.indexClass( Void.class );
			indexer.indexClass( Deprecated.class );
			indexer.indexClass( Serializable.class );
			indexer.indexClass( Comparable.class );
			indexer.indexClass( CharSequence.class );
			indexer.indexClass( String.class );
			indexer.indexClass( Boolean.class );
			indexer.indexClass( Enum.class );
			indexer.indexClass( Byte.class );
			indexer.indexClass( Number.class );
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
			indexer.indexClass( Inherited.class );
			indexer.indexClass( Annotation.class );
			indexer.indexClass( FunctionalInterface.class );
			indexer.indexClass( Consumer.class );
			indexer.indexClass( BiConsumer.class );
			indexer.indexClass( Function.class );
			indexer.indexClass( BiFunction.class );
			indexer.indexClass( UUID.class );
		}
		catch (IOException e) {
			throw new JandexIndexingException( e );
		}

		if ( isHibernateOrmAvailable( classLoading ) ) {
			applyOrmAnnotations( indexer, classLoading );
		}
		else if ( isJpaAvailable( classLoading ) ) {
			applyJpaAnnotations( indexer, classLoading );
		}
	}

	private static boolean isHibernateOrmAvailable(ClassLoading classLoading) {
		try {
			final Class<?> reference = classLoading.classForName( ORM_CHECK_NAME );
			return reference != null;
		}
		catch (Exception e) {
			return false;
		}
	}

	private static void applyOrmAnnotations(Indexer jandexIndexer, ClassLoading classLoading) {
		applyJpaAnnotations( jandexIndexer, classLoading );

		final Class<?> indexer = classLoading.classForName( ORM_ANNOTATION_INDEXER );
		try {
			final Method applyMethod = indexer.getDeclaredMethod( "apply", Indexer.class );
			try {
				applyMethod.invoke( null, jandexIndexer );
			}
			catch (InvocationTargetException | IllegalAccessException e) {
				throw new RuntimeException( e );
			}
		}
		catch (NoSuchMethodException e) {
			throw new RuntimeException( "Error applying JPA annotations to Jandex Indexer", e );
		}
	}

	private static boolean isJpaAvailable(ClassLoading classLoading) {
		try {
			final Class<?> reference = classLoading.classForName( JPA_CHECK_NAME );
			return reference != null;
		}
		catch (Exception e) {
			return false;
		}
	}

	private static void applyJpaAnnotations(Indexer jandexIndexer, ClassLoading classLoading) {
		final Class<?> indexer = classLoading.classForName( JPA_ANNOTATION_INDEXER );
		try {
			final Method applyMethod = indexer.getDeclaredMethod( "apply", Indexer.class );
			try {
				applyMethod.invoke( null, jandexIndexer );
			}
			catch (InvocationTargetException | IllegalAccessException e) {
				throw new RuntimeException( e );
			}
		}
		catch (NoSuchMethodException e) {
			throw new RuntimeException( "Error applying JPA annotations to Jandex Indexer", e );
		}
	}

	public static class JandexIndexingException extends ModelsException {
		public JandexIndexingException(String className) {
			super( "Could not locate classpath resource for " + className );
		}

		public JandexIndexingException(Throwable cause) {
			super( "Error indexing standard types", cause );
		}
	}
}
