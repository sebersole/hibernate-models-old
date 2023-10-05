/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.orm.process;

import java.io.IOException;
import java.util.UUID;

import org.hibernate.annotations.ConverterRegistration;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.JavaTypeRegistration;
import org.hibernate.boot.model.jandex.JandexIndexer;
import org.hibernate.id.IncrementGenerator;
import org.hibernate.models.orm.process.internal.IdGeneratorRegistration;
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
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Basic;
import jakarta.persistence.Converter;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.NamedNativeQuery;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.NamedStoredProcedureQuery;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.TableGenerator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hibernate.models.internal.SimpleClassLoading.SIMPLE_CLASS_LOADING;

/**
 * @author Steve Ebersole
 */
public class ProcessorTests {
	private static final Processor.Options processOptions = new Processor.Options() {
		@Override
		public boolean shouldIgnoreUnlistedClasses() {
			return false;
		}

		@Override
		public boolean areGeneratorsGlobal() {
			// the JPA default is to consider any generator as global
			return true;
		}
	};

	@Test
	void testSimple() {
		final ManagedResourcesImpl.Builder managedResourcesBuilder = new ManagedResourcesImpl.Builder();
		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		// ManagedResources is built by scanning and from explicit resources
		// during ORM bootstrap
		managedResourcesBuilder
				.addLoadedClasses( Person.class, MyStringConverter.class )
				.addPackages( "org.hibernate.models.orm.process" );
		final ManagedResources managedResources = managedResourcesBuilder.build();
		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		// The Jandex index would generally (1) be built by WF and passed
		// to ORM or (2) be built by ORM
		final Indexer indexer = new Indexer();
		JandexIndexerHelper.applyBaseline( indexer, SIMPLE_CLASS_LOADING );
		JandexIndexer.index( managedResources, indexer, SIMPLE_CLASS_LOADING );

		// the test also needs these indexed
		try {
			indexer.indexClass( MyUuidConverter.class );
			indexer.indexClass( org.hibernate.type.YesNoConverter.class );
			indexer.indexClass( org.hibernate.type.CharBooleanConverter.class );
			indexer.indexClass( org.hibernate.type.descriptor.converter.spi.BasicValueConverter.class );
			indexer.indexClass( org.hibernate.type.descriptor.java.StringJavaType.class );
			indexer.indexClass( org.hibernate.type.descriptor.java.AbstractClassJavaType.class );
		}
		catch (IOException e) {
			throw new RuntimeException( e );
		}
		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		// Above here is work done before hibernate-models.
		// Below here is work done by hibernate-models.
		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

		final SourceModelBuildingContextImpl buildingContext = new SourceModelBuildingContextImpl(
				SIMPLE_CLASS_LOADING,
				indexer.complete()
		);

		final ProcessResult processResult = Processor.process( managedResources, processOptions, buildingContext );
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

		assertThat( processResult.getGlobalIdGeneratorRegistrations() ).hasSize( 3 );
		assertThat( processResult.getGlobalIdGeneratorRegistrations() ).containsKeys( "seq_gen", "tbl_gen", "increment_gen" );

		final IdGeneratorRegistration seqGen = processResult.getGlobalIdGeneratorRegistrations().get( "seq_gen" );
		assertThat( seqGen.getConfiguration().getAnnotationDescriptor().getAnnotationType() ).isEqualTo( SequenceGenerator.class );
		assertThat( seqGen.getConfiguration().getAttributeValue( "sequenceName" ).asString() ).isEqualTo( "id_seq" );

		final IdGeneratorRegistration tblGen = processResult.getGlobalIdGeneratorRegistrations().get( "tbl_gen" );
		assertThat( tblGen.getConfiguration().getAnnotationDescriptor().getAnnotationType() ).isEqualTo( TableGenerator.class );
		assertThat( tblGen.getConfiguration().getAttributeValue( "table" ).asString() ).isEqualTo( "id_tbl" );

		assertThat( processResult.getAutoAppliedConverters() ).hasSize( 1 );
		assertThat( processResult.getConverterRegistrations() ).hasSize( 2 );

		assertThat( processResult.getJpaNamedQueries() ).hasSize( 3 );
		assertThat( processResult.getJpaNamedQueries() ).containsKeys( "jpaHql", "jpaNative", "jpaCallable" );

		assertThat( processResult.getHibernateNamedHqlQueries() ).hasSize( 1 );
		assertThat( processResult.getHibernateNamedHqlQueries() ).containsKeys( "ormHql" );

		assertThat( processResult.getHibernateNamedNativeQueries() ).hasSize( 1 );
		assertThat( processResult.getHibernateNamedNativeQueries() ).containsKeys( "ormNative" );
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

		final ProcessResult processResult = Processor.process( managedResources, processOptions, buildingContext );
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
	@SequenceGenerator(name = "seq_gen", sequenceName = "id_seq")
	@TableGenerator(name = "tbl_gen", table = "id_tbl")
	@GenericGenerator(name = "increment_gen", type = IncrementGenerator.class)
	@JavaTypeRegistration(javaType = String.class, descriptorClass = StringJavaType.class)
	@ConverterRegistration(domainType = UUID.class, converter = MyUuidConverter.class)
	@NamedQuery(name = "jpaHql", query = "from Person")
	@NamedNativeQuery(name = "jpaNative", query = "select * from persons")
	@NamedStoredProcedureQuery(name = "jpaCallable", procedureName = "jpa_callable")
	@org.hibernate.annotations.NamedQuery(name = "ormHql", query = "from Person")
	@org.hibernate.annotations.NamedNativeQuery(name = "ormNative", query = "select * from persons")
	public static class Person {
		@Id
		private Integer id;
		private String name;
	}

	@Converter(autoApply = true)
	public static class MyStringConverter implements AttributeConverter<String,String> {
		@Override
		public String convertToDatabaseColumn(String attribute) {
			return null;
		}

		@Override
		public String convertToEntityAttribute(String dbData) {
			return null;
		}
	}

	@Converter
	public static class MyUuidConverter implements AttributeConverter<UUID,String> {
		@Override
		public String convertToDatabaseColumn(UUID attribute) {
			return null;
		}

		@Override
		public UUID convertToEntityAttribute(String dbData) {
			return null;
		}
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
