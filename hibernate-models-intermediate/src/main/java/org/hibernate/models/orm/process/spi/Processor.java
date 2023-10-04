/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.orm.process.spi;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.models.orm.internal.OrmModelBuildingContextImpl;
import org.hibernate.models.orm.internal.SourceModelImpl;
import org.hibernate.models.orm.process.internal.GlobalAnnotationProcessor;
import org.hibernate.models.orm.process.internal.ProcessResultCollector;
import org.hibernate.models.orm.spi.EntityHierarchy;
import org.hibernate.models.orm.spi.JpaAnnotations;
import org.hibernate.models.orm.spi.MappedSuperclassTypeMetadata;
import org.hibernate.models.orm.spi.OrmModelBuildingContext;
import org.hibernate.models.source.internal.explicit.AnnotationDescriptorImpl;
import org.hibernate.models.source.internal.standard.ClassDetailsImpl;
import org.hibernate.models.source.internal.standard.PackageDetailsImpl;
import org.hibernate.models.source.spi.AnnotationDescriptorRegistry;
import org.hibernate.models.source.spi.ClassDetails;
import org.hibernate.models.source.spi.ClassDetailsRegistry;
import org.hibernate.models.source.spi.PackageDetails;
import org.hibernate.models.source.spi.SourceModelBuildingContext;

import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.IndexView;

import static org.hibernate.models.orm.internal.EntityHierarchyBuilder.createEntityHierarchies;
import static org.hibernate.models.orm.internal.EntityHierarchyBuilder.isRoot;

/**
 * Processes {@linkplain ManagedResources managed resources} and produces a
 * {@linkplain ProcessResult result} which collects broad categorizations of the
 * classes defined by those resources based on annotations (and XML mappings).
 *
 * @author Steve Ebersole
 */
public class Processor {
	public interface Options extends GlobalAnnotationProcessor.Options {
		boolean shouldIgnoreUnlistedClasses();
	}

	public static ProcessResult process(
			ManagedResources managedResources,
			Options options,
			SourceModelBuildingContext sourceModelBuildingContext) {
		fillRegistries( sourceModelBuildingContext );

		final OrmModelBuildingContextImpl ormModelBuildingContext = new OrmModelBuildingContextImpl(
				new SourceModelImpl(
						sourceModelBuildingContext.getAnnotationDescriptorRegistry(),
						sourceModelBuildingContext.getClassDetailsRegistry()
				),
				sourceModelBuildingContext.getClassLoadingAccess(),
				sourceModelBuildingContext.getJandexIndex()
		);

		return process( managedResources, options, ormModelBuildingContext );
	}

	public static ProcessResult process(
			ManagedResources managedResources,
			Options options,
			OrmModelBuildingContext mappingBuildingContext) {
		final Set<ClassDetails> rootEntities = new HashSet<>();
		final Set<ClassDetails> mappedSuperClasses = new HashSet<>();
		final ProcessResultCollector processResultCollector = new ProcessResultCollector();
		final GlobalAnnotationProcessor globalAnnotationProcessor = new GlobalAnnotationProcessor(
				processResultCollector,
				options,
				mappingBuildingContext
		);

		if ( options.shouldIgnoreUnlistedClasses() ) {
			processOnlyListed(
					managedResources,
					rootEntities,
					mappedSuperClasses,
					globalAnnotationProcessor,
					options,
					mappingBuildingContext
			);
		}
		else {
			processAllKnown(
					rootEntities,
					mappedSuperClasses,
					globalAnnotationProcessor,
					options,
					mappingBuildingContext
			);
		}

		final Set<EntityHierarchy> entityHierarchies = createEntityHierarchies(
				rootEntities,
				(identifiableType) -> {
					if ( identifiableType instanceof MappedSuperclassTypeMetadata ) {
						mappedSuperClasses.remove( identifiableType.getClassDetails() );
					}
					globalAnnotationProcessor.processGlobalAnnotations( identifiableType.getClassDetails() );
				},
				mappingBuildingContext
		);

		return processResultCollector.createResult( entityHierarchies );
	}

	private static void fillRegistries(SourceModelBuildingContext buildingContext) {
		final ClassDetailsRegistry classDetailsRegistry = buildingContext.getClassDetailsRegistry();
		final AnnotationDescriptorRegistry annotationDescriptorRegistry = buildingContext.getAnnotationDescriptorRegistry();
		final IndexView jandexIndex = buildingContext.getJandexIndex();

		for ( ClassInfo knownClass : jandexIndex.getKnownClasses() ) {
			final String className = knownClass.name().toString();
			if ( className.endsWith( "package-info" ) ) {
				classDetailsRegistry.resolvePackageDetails(
						className,
						() -> new PackageDetailsImpl( knownClass, buildingContext )
				);
				continue;
			}

			if ( knownClass.isAnnotation() ) {
				// it is always safe to load the annotation classes - we will never be enhancing them
				//noinspection rawtypes
				final Class annotationClass = buildingContext
						.getClassLoadingAccess()
						.classForName( className );
				//noinspection unchecked
				annotationDescriptorRegistry.resolveDescriptor(
						annotationClass,
						(t) -> AnnotationDescriptorImpl.buildDescriptor( annotationClass, annotationDescriptorRegistry )
				);
			}

			classDetailsRegistry.resolveClassDetails(
					className,
					() -> new ClassDetailsImpl( knownClass, buildingContext )
			);
		}
	}

	private static void processOnlyListed(
			ManagedResources managedResources,
			Set<ClassDetails> rootEntities,
			Set<ClassDetails> mappedSuperClasses,
			GlobalAnnotationProcessor globalAnnotationProcessor,
			Options options,
			OrmModelBuildingContext mappingBuildingContext) {
		final ClassDetailsRegistry classDetailsRegistry = mappingBuildingContext
				.getSourceModel()
				.getClassDetailsRegistry();
		final List<ClassDetails> listedClasses = new ArrayList<>();
		final List<PackageDetails> listedPackages = new ArrayList<>();
		for ( Class<?> loadedClass : managedResources.getLoadedClasses() ) {
			listedClasses.add( classDetailsRegistry.getClassDetails( loadedClass.getName() ) );
		}
		for ( String className : managedResources.getClassNames() ) {
			// Again, a package can be named in the class-names
			final ClassDetails classDetails = classDetailsRegistry.findClassDetails( className );
			if ( classDetails != null ) {
				listedClasses.add( classDetails );
			}
			else {
				final PackageDetails packageDetails = classDetailsRegistry.findPackageDetails( className );
				if ( packageDetails != null ) {
					listedPackages.add( packageDetails );
				}
				else {
					// todo : exception?
				}
			}
		}
		for ( String packageName : managedResources.getPackageNames() ) {
			final PackageDetails packageDetails = classDetailsRegistry.findPackageDetails( packageName );
			if ( packageDetails != null ) {
				listedPackages.add( packageDetails );
			}
		}

		processResources(
				listedClasses::contains,
				listedPackages::contains,
				rootEntities,
				mappedSuperClasses,
				globalAnnotationProcessor,
				options,
				mappingBuildingContext
		);
	}

	private static void processAllKnown(
			Set<ClassDetails> rootEntities,
			Set<ClassDetails> mappedSuperClasses,
			GlobalAnnotationProcessor globalAnnotationProcessor,
			Options options, OrmModelBuildingContext mappingBuildingContext) {
		processResources(
				null,
				null,
				rootEntities,
				mappedSuperClasses,
				globalAnnotationProcessor,
				options,
				mappingBuildingContext
		);
	}

	private static void processResources(
			ClassInclusions classInclusions,
			PackageInclusions packageInclusions,
			Set<ClassDetails> rootEntities,
			Set<ClassDetails> mappedSuperClasses,
			GlobalAnnotationProcessor globalAnnotationProcessor,
			Options options,
			OrmModelBuildingContext mappingBuildingContext) {
		final ClassDetailsRegistry classDetailsRegistry = mappingBuildingContext
				.getSourceModel()
				.getClassDetailsRegistry();
		classDetailsRegistry.forEachClassDetails( (classDetails) -> {
			if ( classInclusions != null && !classInclusions.shouldInclude( classDetails ) ) {
				// skip this class
				return;
			}

			globalAnnotationProcessor.processGlobalAnnotations( classDetails );

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

		classDetailsRegistry.forEachPackageDetails( (packageDetails) -> {
			if ( packageInclusions != null && !packageInclusions.shouldInclude( packageDetails ) ) {
				// skip this class
				return;
			}

			globalAnnotationProcessor.processGlobalAnnotations( packageDetails );
		} );
	}

	@FunctionalInterface
	private interface ClassInclusions {
		boolean shouldInclude(ClassDetails classDetails);
	}

	@FunctionalInterface
	private interface PackageInclusions {
		boolean shouldInclude(PackageDetails packageDetails);
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
