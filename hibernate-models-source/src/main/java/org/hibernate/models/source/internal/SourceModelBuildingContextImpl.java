/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.source.internal;

import java.lang.annotation.Annotation;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.function.Consumer;

import org.hibernate.models.internal.CollectionHelper;
import org.hibernate.models.source.ModelsException;
import org.hibernate.models.source.UnknownClassException;
import org.hibernate.models.source.internal.standard.ClassDetailsBuilderImpl;
import org.hibernate.models.source.internal.standard.ClassDetailsImpl;
import org.hibernate.models.source.spi.AnnotationDescriptor;
import org.hibernate.models.source.spi.AnnotationDescriptorRegistry;
import org.hibernate.models.source.spi.AnnotationUsage;
import org.hibernate.models.source.spi.ClassDetails;
import org.hibernate.models.source.spi.ClassDetailsRegistry;
import org.hibernate.models.source.spi.RegistryPrimer;
import org.hibernate.models.source.spi.SourceModelBuildingContext;
import org.hibernate.models.spi.ClassLoading;

import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.IndexView;

/**
 * Standard implementation of ModelProcessingContext
 *
 * @author Steve Ebersole
 */
public class SourceModelBuildingContextImpl implements SourceModelBuildingContext {
	private final ClassLoading classLoadingAccess;

	private final IndexView jandexIndex;

	private final AnnotationDescriptorRegistryImpl descriptorRegistry;
	private final ClassDetailsRegistry classDetailsRegistry;

	private final Map<AnnotationDescriptor<?>,List<AnnotationUsage<?>>> annotationUsageMap = new HashMap<>();

	public SourceModelBuildingContextImpl(ClassLoading classLoadingAccess, IndexView jandexIndex) {
		this( classLoadingAccess, jandexIndex, null );
	}

	public SourceModelBuildingContextImpl(
			ClassLoading classLoadingAccess,
			IndexView jandexIndex,
			RegistryPrimer registryPrimer) {
		this.classLoadingAccess = classLoadingAccess;
		this.jandexIndex = jandexIndex;

		this.descriptorRegistry = new AnnotationDescriptorRegistryImpl( this );
		this.classDetailsRegistry = new ClassDetailsRegistryImpl( this );

		primeRegistries();

		if ( registryPrimer != null ) {
			registryPrimer.primeRegistries( new RegistryContributions(), this );
		}
	}

	private void primeRegistries() {
		primeClassDetails( String.class );
		primeClassDetails( Boolean.class );
		primeClassDetails( Enum.class );
		primeClassDetails( Byte.class );
		primeClassDetails( Short.class );
		primeClassDetails( Integer.class );
		primeClassDetails( Long.class );
		primeClassDetails( Double.class );
		primeClassDetails( Float.class );
		primeClassDetails( BigInteger.class );
		primeClassDetails( BigDecimal.class );
		primeClassDetails( Blob.class );
		primeClassDetails( Clob.class );
		primeClassDetails( NClob.class );
		primeClassDetails( Instant.class );
		primeClassDetails( LocalDate.class );
		primeClassDetails( LocalTime.class );
		primeClassDetails( LocalDateTime.class );
		primeClassDetails( OffsetTime.class );
		primeClassDetails( OffsetDateTime.class );
		primeClassDetails( ZonedDateTime.class );
		primeClassDetails( java.util.Date.class );
		primeClassDetails( java.sql.Date.class );
		primeClassDetails( java.sql.Time.class );
		primeClassDetails( java.sql.Timestamp.class );
		primeClassDetails( URL.class );
		primeClassDetails( Collection.class );
		primeClassDetails( Set.class );
		primeClassDetails( List.class );
		primeClassDetails( Map.class );
		primeClassDetails( Comparator.class );
		primeClassDetails( Comparable.class );
		primeClassDetails( SortedSet.class );
		primeClassDetails( SortedMap.class );
	}

	private void primeClassDetails(Class<?> javaType) {
		// Since we have a Class reference already, it is safe to directly use
		// the reflection
		classDetailsRegistry.resolveClassDetails(
				javaType.getName(),
				ClassDetailsBuilderImpl::buildClassDetailsStatic
		);
	}

	@Override
	public AnnotationDescriptorRegistry getAnnotationDescriptorRegistry() {
		return descriptorRegistry;
	}

	@Override
	public ClassDetailsRegistry getClassDetailsRegistry() {
		return classDetailsRegistry;
	}

	@Override
	public ClassLoading getClassLoadingAccess() {
		return classLoadingAccess;
	}

	@Override
	public IndexView getJandexIndex() {
		return jandexIndex;
	}

	@Override
	public void registerUsage(AnnotationUsage<? extends Annotation> usage) {
		// todo (annotation-source) : we only care about this in specific cases.
		//		this feeds a Map used to locate annotations regardless of target.
		//		this is used when locating "global" annotations such as generators,
		//		named-queries, etc.
		//		+
		//		an option to limit what we cache would be to add a flag to AnnotationDescriptor
		//		to indicate whether the annotation is "globally resolvable".

		// register the usage under the appropriate descriptor.
		//
		// if the incoming value is a usage of a "repeatable container", skip the
		// registration - the repetitions themselves are what we are interested in,
		// and they will get registered themselves

		final AnnotationDescriptor<?> incomingUsageDescriptor = usage.getAnnotationDescriptor();
		final AnnotationDescriptor<? extends Annotation> repeatableDescriptor = descriptorRegistry.getContainedRepeatableDescriptor( incomingUsageDescriptor );
		if ( repeatableDescriptor != null ) {
			// the incoming value is a usage of a "repeatable container", skip the registration
			return;
		}


		final List<AnnotationUsage<?>> registeredUsages;
		final List<AnnotationUsage<?>> existingRegisteredUsages = annotationUsageMap.get( incomingUsageDescriptor );
		if ( existingRegisteredUsages == null ) {
			registeredUsages = new ArrayList<>();
			annotationUsageMap.put( incomingUsageDescriptor, registeredUsages );
		}
		else {
			registeredUsages = existingRegisteredUsages;
		}

		registeredUsages.add( usage );
	}

	@Override
	public <A extends Annotation> List<AnnotationUsage<A>> getAllUsages(AnnotationDescriptor<A> annotationDescriptor) {
		final AnnotationDescriptor<? extends Annotation> repeatableDescriptor = descriptorRegistry.getContainedRepeatableDescriptor( annotationDescriptor );
		if ( repeatableDescriptor != null ) {
			throw new ModelsException( "Annotations which are repeatable-containers are not supported" );
		}

		//noinspection unchecked,rawtypes
		return (List) annotationUsageMap.get( annotationDescriptor );
	}

	@Override
	public <A extends Annotation> List<AnnotationUsage<A>> getAllUsages(A annotation) {
		//noinspection unchecked
		final AnnotationDescriptor<A> descriptor = (AnnotationDescriptor<A>) descriptorRegistry.getDescriptor( annotation.getClass() );
		return getAllUsages( descriptor );
	}

	@Override
	public <A extends Annotation> void forEachUsage(AnnotationDescriptor<A> annotationDescriptor, Consumer<AnnotationUsage<A>> consumer) {
		final List<AnnotationUsage<A>> allUsages = getAllUsages( annotationDescriptor );
		if ( CollectionHelper.isEmpty( allUsages ) ) {
			return;
		}
		allUsages.forEach( consumer );
	}

	@Override
	public <A extends Annotation> void forEachUsage(A annotation, Consumer<AnnotationUsage<A>> consumer) {

	}

	private class RegistryContributions implements RegistryPrimer.Contributions {

		@Override
		public <A extends Annotation> void registerAnnotation(AnnotationDescriptor<A> descriptor) {
			descriptorRegistry.register( descriptor );

			final ClassInfo classInfo = jandexIndex.getClassByName( descriptor.getAnnotationType() );
			if ( classInfo == null ) {
				throw new UnknownClassException(
						"Unable to locate class in Jandex index - " + descriptor.getAnnotationType().getName()
				);
			}
			registerClass( new ClassDetailsImpl(
					classInfo,
					SourceModelBuildingContextImpl.this
			) );
		}

		@Override
		public void registerClass(ClassDetails details) {
			classDetailsRegistry.addClassDetails( details );
		}
	}
}
