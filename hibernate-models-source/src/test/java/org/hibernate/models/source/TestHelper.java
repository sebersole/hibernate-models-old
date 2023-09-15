/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.source;

import java.lang.annotation.Annotation;

import org.hibernate.annotations.common.reflection.ReflectionManager;
import org.hibernate.annotations.common.reflection.java.JavaReflectionManager;
import org.hibernate.models.source.internal.SourceModelBuildingContextImpl;
import org.hibernate.models.source.spi.AnnotationDescriptor;
import org.hibernate.models.spi.ClassLoading;
import org.hibernate.models.source.spi.SourceModelBuildingContext;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;

import static org.hibernate.models.internal.SimpleClassLoading.SIMPLE_CLASS_LOADING;
import static org.hibernate.models.source.internal.AnnotationDescriptorBuilder.extractAttributeDescriptors;

/**
 * @author Steve Ebersole
 */
public class TestHelper {
	public static final AnnotationDescriptor<Column> COLUMN = createDescriptor( Column.class );
	public static final AnnotationDescriptor<Entity> ENTITY = createDescriptor( Entity.class );
	public static final AnnotationDescriptor<NamedQueries> NAMED_QUERIES = createDescriptor( NamedQueries.class );
	public static final AnnotationDescriptor<NamedQuery> NAMED_QUERY = createDescriptor( NamedQuery.class, NAMED_QUERIES );

	public static SourceModelBuildingContext buildProcessingContext() {
		return buildProcessingContext( SIMPLE_CLASS_LOADING );
	}

	public static SourceModelBuildingContext buildProcessingContext(ClassLoading classLoading) {
		final ReflectionManager hcannManager = new JavaReflectionManager();
		final SourceModelBuildingContextImpl buildingContext = new SourceModelBuildingContextImpl(
				classLoading,
				null,
				hcannManager,
				(contributions, buildingContext1) -> {
					contributions.registerAnnotation( COLUMN );
					contributions.registerAnnotation( ENTITY );
					contributions.registerAnnotation( NAMED_QUERY );
					contributions.registerAnnotation( NAMED_QUERIES );
				}
		);
		return buildingContext;
	}

	public static <A extends Annotation > AnnotationDescriptor<A> createDescriptor(Class<A> annotationType) {
		return createDescriptor( annotationType, null );
	}

	public static <A extends Annotation> AnnotationDescriptor<A> createDescriptor(
			Class<A> javaType,
			AnnotationDescriptor<?> repeatableContainer) {
		assert javaType != null;

		return new OrmAnnotationDescriptorImpl<>(
				javaType,
				extractAttributeDescriptors( javaType ),
				repeatableContainer
		);
	}
}
