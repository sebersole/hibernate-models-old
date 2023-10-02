/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.source.jandex;

import java.io.IOException;
import java.io.Serializable;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Comment;
import org.hibernate.models.internal.SimpleClassLoading;
import org.hibernate.models.source.CustomAnnotation;
import org.hibernate.models.source.CustomAnnotations;
import org.hibernate.models.source.SimpleEntity;
import org.hibernate.models.source.internal.SourceModelBuildingContextImpl;
import org.hibernate.models.source.internal.jandex.JandexIndexerHelper;
import org.hibernate.models.source.internal.standard.ClassDetailsBuilderImpl;
import org.hibernate.models.source.spi.AnnotationDescriptor;
import org.hibernate.models.source.spi.ClassDetails;

import org.junit.jupiter.api.Test;

import org.jboss.jandex.Index;
import org.jboss.jandex.Indexer;

import jakarta.persistence.Basic;
import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.SecondaryTable;
import jakarta.persistence.Table;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hibernate.models.source.TestHelper.applyStandardIndexing;

/**
 * @author Steve Ebersole
 */
public class JandexSmokeTests {
	@Test
	void testIt() throws IOException {
		final Indexer indexer = new Indexer();
		JandexIndexerHelper.applyBaseline( indexer, SimpleClassLoading.SIMPLE_CLASS_LOADING );
		indexer.indexClass( SimpleEntity.class );
		indexer.indexClass( CustomAnnotation.class );
		indexer.indexClass( CustomAnnotations.class );
		final Index jandexIndex = indexer.complete();

		final SourceModelBuildingContextImpl buildingContext = new SourceModelBuildingContextImpl(
				SimpleClassLoading.SIMPLE_CLASS_LOADING,
				jandexIndex,
				(contributions, buildingContext1) -> {
				}
		);

		final ClassDetailsBuilderImpl builder = ClassDetailsBuilderImpl.DEFAULT_BUILDER;
		final ClassDetails classDetails = builder.buildClassDetails( SimpleEntity.class.getName(), buildingContext );
		assertThat( classDetails ).isNotNull();

		final AnnotationDescriptor<Entity> entityAnnDescriptor = buildingContext
				.getAnnotationDescriptorRegistry()
				.getDescriptor( Entity.class );
		assertThat( classDetails.getAnnotation( entityAnnDescriptor ) ).isNotNull();
	}
}
