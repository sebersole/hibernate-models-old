/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.orm;

import java.util.Set;

import org.hibernate.annotations.common.reflection.ReflectionManager;
import org.hibernate.annotations.common.reflection.XClass;
import org.hibernate.annotations.common.reflection.java.JavaReflectionManager;
import org.hibernate.models.orm.internal.EntityHierarchyBuilder;
import org.hibernate.models.orm.internal.OrmModelBuildingContextImpl;
import org.hibernate.models.orm.internal.SourceModelImpl;
import org.hibernate.models.orm.spi.EntityHierarchy;
import org.hibernate.models.source.internal.SourceModelBuildingContextImpl;
import org.hibernate.models.source.internal.hcann.ClassDetailsImpl;
import org.hibernate.models.source.spi.SourceModelBuildingContext;
import org.hibernate.models.spi.ClassLoading;

import static org.hibernate.models.internal.SimpleClassLoading.SIMPLE_CLASS_LOADING;
import static org.hibernate.models.orm.internal.AnnotationHelper.forEachOrmAnnotation;

/**
 * @author Steve Ebersole
 */
public class TestHelper {
	public static SourceModelBuildingContext buildProcessingContext() {
		return buildProcessingContext( SIMPLE_CLASS_LOADING );
	}

	public static SourceModelBuildingContext buildProcessingContext(ClassLoading classLoading) {
		return new SourceModelBuildingContextImpl(
				classLoading,
				null,
				new JavaReflectionManager(),
				(contributions, buildingContext) -> forEachOrmAnnotation( contributions::registerAnnotation )
		);
	}

	public static Set<EntityHierarchy> buildHierarchies(Class<?>... classes) {
		return buildHierarchies(
				buildProcessingContext(),
				classes
		);
	}

	public static Set<EntityHierarchy> buildHierarchies(
			SourceModelBuildingContext sourceModelBuildingContext,
			Class<?>... classes) {
		final ReflectionManager hcannReflectionManager = sourceModelBuildingContext.getReflectionManager();

		for ( int i = 0; i < classes.length; i++ ) {
			final XClass xClass = hcannReflectionManager.toXClass( classes[ i ] );
			new ClassDetailsImpl( xClass, sourceModelBuildingContext );
		}

		final OrmModelBuildingContextImpl ormModelBuildingContext = new OrmModelBuildingContextImpl(
				new SourceModelImpl(
						sourceModelBuildingContext.getAnnotationDescriptorRegistry(),
						sourceModelBuildingContext.getClassDetailsRegistry()
				)
		);
		return EntityHierarchyBuilder.createEntityHierarchies( ormModelBuildingContext );
	}
}
