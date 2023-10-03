/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.orm.process;

import java.io.IOException;

import org.hibernate.annotations.JavaTypeRegistration;
import org.hibernate.boot.model.jandex.JandexIndexer;
import org.hibernate.models.orm.process.internal.ManagedResourcesImpl;
import org.hibernate.models.orm.process.spi.ManagedResources;
import org.hibernate.models.orm.process.spi.ProcessResult;
import org.hibernate.models.orm.process.spi.Processor;
import org.hibernate.models.orm.spi.EntityHierarchy;
import org.hibernate.models.orm.spi.EntityTypeMetadata;
import org.hibernate.models.source.internal.SourceModelBuildingContextImpl;
import org.hibernate.models.source.internal.jandex.JandexIndexerHelper;
import org.hibernate.type.descriptor.java.StringJavaType;

import org.junit.jupiter.api.Test;

import org.jboss.jandex.Indexer;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Basic;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hibernate.models.internal.SimpleClassLoading.SIMPLE_CLASS_LOADING;

/**
 * @author Steve Ebersole
 */
public class ProcessorTests {
	@Test
	void testSimple() {
		final ManagedResourcesImpl.Builder managedResourcesBuilder = new ManagedResourcesImpl.Builder();
		managedResourcesBuilder.addLoadedClasses( Person.class );
		final ManagedResources managedResources = managedResourcesBuilder.build();

		final Indexer indexer = new Indexer();
		JandexIndexerHelper.applyBaseline( indexer, SIMPLE_CLASS_LOADING );
		JandexIndexer.index( managedResources, indexer, SIMPLE_CLASS_LOADING );

		// the test also needs these indexed
		try {
			indexer.indexClass( org.hibernate.type.descriptor.java.StringJavaType.class );
			indexer.indexClass( org.hibernate.type.descriptor.java.AbstractClassJavaType.class );
		}
		catch (IOException e) {
			throw new RuntimeException( e );
		}

		final SourceModelBuildingContextImpl buildingContext = new SourceModelBuildingContextImpl(
				SIMPLE_CLASS_LOADING,
				indexer.complete()
		);

		final ProcessResult processResult = Processor.process( managedResources, false, buildingContext );
		assertThat( processResult.getEntityHierarchies() ).hasSize( 1 );
		final EntityHierarchy hierarchy = processResult.getEntityHierarchies().iterator().next();
		assertThat( hierarchy.getInheritanceType() ).isEqualTo( InheritanceType.SINGLE_TABLE );
		assertThat( hierarchy.getCaching() ).isNotNull();
		assertThat( hierarchy.getCaching().isEnabled() ).isFalse();
		assertThat( hierarchy.getNaturalIdCaching() ).isNotNull();
		assertThat( hierarchy.getNaturalIdCaching().isEnabled() ).isFalse();

		assertThat( hierarchy.getRoot() ).isNotNull();
		assertThat( hierarchy.getRoot().getSuperType() ).isNull();
		assertThat( hierarchy.getRoot().hasSubTypes() ).isFalse();
		assertThat( hierarchy.getRoot().getEntityName() ).isEqualTo( Person.class.getName() );
		assertThat( hierarchy.getRoot().getJpaEntityName() ).isEqualTo( Person.class.getSimpleName() );
		assertThat( hierarchy.getRoot().getAccessType() ).isEqualTo( AccessType.FIELD );
		assertThat( hierarchy.getRoot().getAttributes() ).hasSize( 2 );

		assertThat( processResult.getJavaTypeRegistrations() ).hasSize( 1 );
		assertThat( processResult.getJavaTypeRegistrations().get( 0 ).getDomainType() ).isNotNull();
		assertThat( processResult.getJavaTypeRegistrations().get( 0 ).getDomainType().getClassName() ).isEqualTo( String.class.getName() );
		assertThat( processResult.getJavaTypeRegistrations().get( 0 ).getDescriptor() ).isNotNull();
		assertThat( processResult.getJavaTypeRegistrations().get( 0 ).getDescriptor().getClassName() ).endsWith( "StringJavaType" );
	}

	@Test
	void testJoined() {
		final ManagedResourcesImpl.Builder managedResourcesBuilder = new ManagedResourcesImpl.Builder();
		managedResourcesBuilder.addLoadedClasses( Root.class, Sub.class );
		final ManagedResources managedResources = managedResourcesBuilder.build();

		final Indexer indexer = new Indexer();
		JandexIndexerHelper.applyBaseline( indexer, SIMPLE_CLASS_LOADING );
		JandexIndexer.index( managedResources, indexer, SIMPLE_CLASS_LOADING );

		final SourceModelBuildingContextImpl buildingContext = new SourceModelBuildingContextImpl(
				SIMPLE_CLASS_LOADING,
				indexer.complete()
		);

		final ProcessResult processResult = Processor.process( managedResources, false, buildingContext );
		assertThat( processResult.getEntityHierarchies() ).hasSize( 1 );
		final EntityHierarchy hierarchy = processResult.getEntityHierarchies().iterator().next();
		assertThat( hierarchy.getInheritanceType() ).isEqualTo( InheritanceType.JOINED );
		assertThat( hierarchy.getCaching() ).isNotNull();
		assertThat( hierarchy.getCaching().isEnabled() ).isFalse();
		assertThat( hierarchy.getNaturalIdCaching() ).isNotNull();
		assertThat( hierarchy.getNaturalIdCaching().isEnabled() ).isFalse();

		assertThat( hierarchy.getRoot() ).isNotNull();
		assertThat( hierarchy.getRoot().getSuperType() ).isNull();
		assertThat( hierarchy.getRoot().hasSubTypes() ).isTrue();
		assertThat( hierarchy.getRoot().getEntityName() ).isEqualTo( Root.class.getName() );
		assertThat( hierarchy.getRoot().getJpaEntityName() ).isEqualTo( Root.class.getSimpleName() );
		assertThat( hierarchy.getRoot().getAccessType() ).isEqualTo( AccessType.FIELD );
		assertThat( hierarchy.getRoot().getAttributes() ).hasSize( 2 );

		final EntityTypeMetadata sub = (EntityTypeMetadata) hierarchy.getRoot().getSubTypes().iterator().next();
		assertThat( sub ).isNotNull();
		assertThat( sub.getSuperType() ).isNotNull();
		assertThat( sub.hasSubTypes() ).isFalse();
		assertThat( sub.getEntityName() ).isEqualTo( Sub.class.getName() );
		assertThat( sub.getJpaEntityName() ).isEqualTo( Sub.class.getSimpleName() );
		assertThat( sub.getAccessType() ).isEqualTo( AccessType.PROPERTY );
		assertThat( sub.getAttributes() ).hasSize( 1 );
	}

	@Entity(name="Person")
	@Table(name="persons")
	@JavaTypeRegistration(javaType = String.class, descriptorClass = StringJavaType.class)
	public static class Person {
		@Id
		private Integer id;
		private String name;
	}

	@Entity(name="Root")
	@Table(name="roots")
	@Inheritance(strategy = InheritanceType.JOINED)
	public static class Root {
		@Id
		private Integer id;
		private String name;
	}

	@Entity(name="Sub")
	@Table(name="subs")
	@Access( AccessType.PROPERTY )
	public static class Sub extends Root {
		private String moreDetails;

		@Basic
		public String getMoreDetails() {
			return moreDetails;
		}

		public void setMoreDetails(String moreDetails) {
			this.moreDetails = moreDetails;
		}
	}
}
