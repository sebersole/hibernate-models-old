/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.source.internal.reflection;

import org.hibernate.models.source.spi.ClassDetails;
import org.hibernate.models.source.spi.ClassDetailsBuilder;
import org.hibernate.models.source.spi.SourceModelBuildingContext;

/**
 * ClassDetailsBuilder implementation based on {@link Class}
 *
 * @author Steve Ebersole
 */
public class ClassDetailsBuilderImpl implements ClassDetailsBuilder {
	/**
	 * Singleton access
	 */
	public static final ClassDetailsBuilderImpl INSTANCE = new ClassDetailsBuilderImpl();

	@Override
	public ClassDetails buildClassDetails(String name, SourceModelBuildingContext processingContext) {
		return buildClassDetailsStatic( name, processingContext );
	}

	public static ClassDetails buildClassDetailsStatic(String name, SourceModelBuildingContext processingContext) {
		return buildClassDetails(
				processingContext.getClassLoadingAccess().classForName( name ),
				processingContext
		);
	}

	public static ClassDetails buildClassDetails(Class<?> javaClass, SourceModelBuildingContext processingContext) {
		return new ClassDetailsImpl( javaClass, processingContext );
	}
}
