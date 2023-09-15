/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.source.internal.hcann;

import org.hibernate.annotations.common.reflection.ReflectionManager;
import org.hibernate.annotations.common.reflection.XClass;
import org.hibernate.annotations.common.reflection.java.JavaReflectionManager;
import org.hibernate.models.source.spi.ClassDetails;
import org.hibernate.models.source.spi.ClassDetailsBuilder;
import org.hibernate.models.spi.ClassLoading;
import org.hibernate.models.source.spi.SourceModelBuildingContext;

/**
 * HCANN based ClassDetailsBuilder
 *
 * @author Steve Ebersole
 */
public class ClassDetailsBuilderImpl implements ClassDetailsBuilder {
	private final ClassLoading classLoaderAccess;
	private final ReflectionManager reflectionManager;

	public ClassDetailsBuilderImpl(SourceModelBuildingContext processingContext) {
		this.classLoaderAccess = processingContext.getClassLoadingAccess();
		this.reflectionManager = generateReflectionManager( classLoaderAccess, processingContext );
	}

	private static ReflectionManager generateReflectionManager(ClassLoading classLoaderAccess, SourceModelBuildingContext processingContext) {
		final JavaReflectionManager reflectionManager = new JavaReflectionManager();
//		reflectionManager.setMetadataProvider( new JPAXMLOverriddenMetadataProvider(
//				classLoaderAccess,
//				processingContext.getMetadataBuildingContext().getBootstrapContext()
//		) );
		return reflectionManager;
	}

	@Override
	public ClassDetails buildClassDetails(String name, SourceModelBuildingContext processingContext) {
		final Class<?> classForName = classLoaderAccess.classForName( name );
		final XClass xClassForName = reflectionManager.toXClass( classForName );
		return new ClassDetailsImpl( xClassForName, processingContext );
	}
}
