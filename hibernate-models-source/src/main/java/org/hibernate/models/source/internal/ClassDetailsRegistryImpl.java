/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.source.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.hibernate.models.source.ModelsException;
import org.hibernate.models.source.UnknownClassException;
import org.hibernate.models.source.internal.standard.ClassDetailsBuilderImpl;
import org.hibernate.models.source.spi.ClassDetails;
import org.hibernate.models.source.spi.ClassDetailsBuilder;
import org.hibernate.models.source.spi.ClassDetailsRegistry;
import org.hibernate.models.source.spi.PackageDetails;
import org.hibernate.models.source.spi.SourceModelBuildingContext;

/**
 * Standard ClassDetailsRegistry implementation
 *
 * @author Steve Ebersole
 */
public class ClassDetailsRegistryImpl implements ClassDetailsRegistry {
	private final ClassDetailsBuilder fallbackClassDetailsBuilder;
	private final SourceModelBuildingContext context;

	private final Map<String, ClassDetails> classDetailsMap = new ConcurrentHashMap<>();
	// sub-type ClassDetails per super
	private final Map<String, List<ClassDetails>> subTypeClassDetailsMap = new ConcurrentHashMap<>();
	// for packages containing a package-info.class file
	private final Map<String, PackageDetails> packageDetailsMap = new ConcurrentHashMap<>();

	public ClassDetailsRegistryImpl(SourceModelBuildingContext context) {
		this( ClassDetailsBuilderImpl.DEFAULT_BUILDER, context );
	}

	public ClassDetailsRegistryImpl(ClassDetailsBuilder fallbackClassDetailsBuilder, SourceModelBuildingContext context) {
		this.context = context;
		this.fallbackClassDetailsBuilder = fallbackClassDetailsBuilder;
	}

	@Override public ClassDetails findClassDetails(String name) {
		return classDetailsMap.get( name );
	}

	@Override public ClassDetails getClassDetails(String name) {
		final ClassDetails named = classDetailsMap.get( name );
		if ( named == null ) {
			if ( "void".equals( name ) ) {
				return null;
			}
 			throw new UnknownClassException( "Unknown managed class - " + name );
		}
		return named;
	}

	@Override public void forEachClassDetails(ClassDetailsConsumer consumer) {
		for ( Map.Entry<String, ClassDetails> entry : classDetailsMap.entrySet() ) {
			consumer.consume( entry.getValue() );
		}
	}

	@Override public List<ClassDetails> getDirectSubTypes(String superTypeName) {
		return subTypeClassDetailsMap.get( superTypeName );
	}

	@Override public void forEachDirectSubType(String superTypeName, ClassDetailsConsumer consumer) {
		final List<ClassDetails> directSubTypes = getDirectSubTypes( superTypeName );
		if ( directSubTypes == null ) {
			return;
		}
		for ( int i = 0; i < directSubTypes.size(); i++ ) {
			consumer.consume( directSubTypes.get( i ) );
		}
	}

	@Override public void addClassDetails(ClassDetails classDetails) {
		addClassDetails( classDetails.getClassName(), classDetails );
	}

	@Override public void addClassDetails(String name, ClassDetails classDetails) {
		classDetailsMap.put( name, classDetails );

		if ( classDetails.getSuperType() != null ) {
			List<ClassDetails> subTypes = subTypeClassDetailsMap.get( classDetails.getSuperType().getName() );
			if ( subTypes == null ) {
				subTypes = new ArrayList<>();
				subTypeClassDetailsMap.put( classDetails.getSuperType().getName(), subTypes );
			}
			subTypes.add( classDetails );
		}
	}

	@Override public ClassDetails resolveClassDetails(String name) {
		return resolveClassDetails( name, fallbackClassDetailsBuilder );
	}

	@Override public ClassDetails resolveClassDetails(
			String name,
			ClassDetailsBuilder creator) {
		final ClassDetails existing = classDetailsMap.get( name );
		if ( existing != null ) {
			return existing;
		}

		final ClassDetails created = creator.buildClassDetails( name, context );
		addClassDetails( name, created );
		return created;
	}

	@Override public ClassDetails resolveClassDetails(
			String name,
			ClassDetailsCreator creator) {
		final ClassDetails existing = classDetailsMap.get( name );
		if ( existing != null ) {
			return existing;
		}

		final ClassDetails created = creator.createClassDetails();
		addClassDetails( name, created );
		return created;
	}

	@Override
	public PackageDetails resolvePackageDetails(String packageName, PackageDetailsCreator creator) {
		final PackageDetails existing = packageDetailsMap.get( packageName );
		if ( existing != null ) {
			return existing;
		}

		final PackageDetails created = creator.createPackageDetails();
		packageDetailsMap.put( packageName, created );
		return created;
	}

	@Override
	public PackageDetails findPackageDetails(String name) {
		return packageDetailsMap.get( name );
	}

	@Override
	public PackageDetails getPackageDetails(String name) {
		final PackageDetails found = findPackageDetails( name );
		if ( found == null ) {
			throw new ModelsException( "Could not locate PackageDetails - " + name );
		}
		return found;
	}

	@Override
	public void forEachPackageDetails(PackageDetailsConsumer consumer) {
		for ( Map.Entry<String, PackageDetails> entry : packageDetailsMap.entrySet() ) {
			consumer.consume( entry.getValue() );
		}
	}
}
