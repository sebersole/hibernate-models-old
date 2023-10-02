/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.orm;

import java.io.IOException;
import java.util.Set;

import org.hibernate.annotations.common.reflection.ReflectionManager;
import org.hibernate.annotations.common.reflection.XClass;
import org.hibernate.annotations.common.reflection.java.JavaReflectionManager;
import org.hibernate.models.orm.internal.EntityHierarchyBuilder;
import org.hibernate.models.orm.internal.OrmModelBuildingContextImpl;
import org.hibernate.models.orm.internal.SourceModelImpl;
import org.hibernate.models.orm.spi.EntityHierarchy;
import org.hibernate.models.source.UnknownClassException;
import org.hibernate.models.source.internal.SourceModelBuildingContextImpl;
import org.hibernate.models.source.internal.jandex.JandexIndexerHelper;
import org.hibernate.models.source.internal.jandex.JpaAnnotationIndexer;
import org.hibernate.models.source.internal.jandex.OrmAnnotationIndexer;
import org.hibernate.models.source.internal.standard.ClassDetailsImpl;
import org.hibernate.models.source.spi.ClassDetailsRegistry;
import org.hibernate.models.source.spi.SourceModelBuildingContext;
import org.hibernate.models.spi.ClassLoading;

import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.IndexView;
import org.jboss.jandex.Indexer;

import static org.hibernate.models.internal.SimpleClassLoading.SIMPLE_CLASS_LOADING;
import static org.hibernate.models.orm.internal.AnnotationHelper.forEachOrmAnnotation;

/**
 * @author Steve Ebersole
 */
public class TestHelper {
	public static SourceModelBuildingContext createBuildingContext(Class<?>... domainClasses) {
		return createBuildingContext( SIMPLE_CLASS_LOADING, domainClasses );
	}

	public static SourceModelBuildingContext createBuildingContext(ClassLoading classLoading, Class<?>... domainClasses) {
		final Indexer indexer = new Indexer();
		JandexIndexerHelper.applyBaseline( indexer, SIMPLE_CLASS_LOADING );
		for ( Class<?> modelClass : domainClasses ) {
			try {
				indexer.indexClass( modelClass );
			}
			catch (IOException e) {
				throw new RuntimeException( e );
			}
		}

		return new SourceModelBuildingContextImpl(
				classLoading,
				indexer.complete(),
				null,
				(contributions, buildingContext) -> {
					forEachOrmAnnotation( contributions::registerAnnotation );
					final ClassDetailsRegistry classDetailsRegistry = buildingContext.getClassDetailsRegistry();
					for ( Class<?> supportClass : JpaAnnotationIndexer.SUPPORT_CLASSES ) {
						contributions.registerClass( classDetailsRegistry.resolveClassDetails( supportClass.getName() ) );
					}
					for ( Class<?> supportClass : OrmAnnotationIndexer.SUPPORT_CLASSES ) {
						contributions.registerClass( classDetailsRegistry.resolveClassDetails( supportClass.getName() ) );
					}
				}
		);
	}

	public static Set<EntityHierarchy> buildHierarchies(Class<?>... classes) {
		return buildHierarchies(
				createBuildingContext( classes ),
				classes
		);
	}

	public static Set<EntityHierarchy> buildHierarchies(
			SourceModelBuildingContext sourceModelBuildingContext,
			Class<?>... classes) {
		final IndexView jandexView = sourceModelBuildingContext.getJandexView();

		for ( int i = 0; i < classes.length; i++ ) {
			final ClassInfo classInfo = jandexView.getClassByName( classes[i] );
			if ( classInfo == null ) {
				throw new UnknownClassException( classes[i].getName() );
			}
			new ClassDetailsImpl( classInfo, sourceModelBuildingContext );
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
