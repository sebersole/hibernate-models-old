/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.source;

import java.util.Arrays;
import java.util.List;

import org.hibernate.annotations.JavaTypeRegistration;
import org.hibernate.annotations.common.reflection.XClass;
import org.hibernate.annotations.common.reflection.java.JavaReflectionManager;
import org.hibernate.internal.util.MutableInteger;
import org.hibernate.models.source.internal.hcann.ClassDetailsImpl;
import org.hibernate.models.source.spi.AnnotationDescriptorRegistry;
import org.hibernate.models.source.spi.AnnotationUsage;
import org.hibernate.models.source.spi.FieldDetails;
import org.hibernate.models.source.spi.MethodDetails;
import org.hibernate.models.source.spi.SourceModelBuildingContext;

import org.junit.jupiter.api.Test;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.NamedQuery;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hibernate.models.source.TestHelper.COLUMN;
import static org.hibernate.models.source.TestHelper.NAMED_QUERY;
import static org.hibernate.models.source.internal.AnnotationHelper.extractTargets;
import static org.hibernate.models.source.spi.AnnotationTarget.Kind.CLASS;
import static org.hibernate.models.source.spi.AnnotationTarget.Kind.FIELD;
import static org.hibernate.models.source.spi.AnnotationTarget.Kind.METHOD;
import static org.hibernate.models.source.spi.AnnotationTarget.Kind.PACKAGE;

/**
 * Smoke tests about how HCANN handles various situations
 *
 * @author Steve Ebersole
 */
public class CommonAnnotationSmokeTests {
	@Test
	void basicAssertions() {
		final SourceModelBuildingContext buildingContext = TestHelper.buildProcessingContext();
		final ClassDetailsImpl entityClass = buildClassDetails( buildingContext );

		assertThat( entityClass.getFields() ).hasSize( 3 );
		assertThat( entityClass.getFields().stream().map( FieldDetails::getName ) )
				.containsAll( Arrays.asList( "id", "name", "name2" ) );

		assertThat( entityClass.getMethods() ).hasSize( 3 );
		assertThat( entityClass.getMethods().stream().map( MethodDetails::getName ) )
				.containsAll( Arrays.asList( "getId", "getName", "setName" ) );

		verifyNameMapping( findNamedField( entityClass.getFields(), "name" ) );
		verifyIdMapping( findNamedField( entityClass.getFields(), "id" ) );

		final AnnotationDescriptorRegistry descriptorRegistry = buildingContext.getAnnotationDescriptorRegistry();
		final AnnotationUsage<CustomAnnotation> customAnnotation = entityClass.getAnnotation( descriptorRegistry.getDescriptor( CustomAnnotation.class ) );
		assertThat( customAnnotation ).isNotNull();
	}

	private ClassDetailsImpl buildClassDetails(SourceModelBuildingContext buildingContext) {
		final JavaReflectionManager hcannReflectionManager = new JavaReflectionManager();
		final XClass xClass = hcannReflectionManager.toXClass( SimpleEntity.class );

		return new ClassDetailsImpl( xClass, buildingContext );
	}

	@Test
	void testAllowableTargets() {
		assertThat( extractTargets( Column.class ) ).containsOnly( FIELD, METHOD );
		assertThat( extractTargets( Entity.class ) ).containsOnly( CLASS );
		assertThat( extractTargets( JavaTypeRegistration.class ) ).contains( PACKAGE );
	}

	@Test
	void testRepeatableAnnotationHandling() {
		final SourceModelBuildingContext buildingContext = TestHelper.buildProcessingContext();
		final ClassDetailsImpl entityClass = buildClassDetails( buildingContext );

		final List<AnnotationUsage<NamedQuery>> usages = entityClass.getRepeatedAnnotations( NAMED_QUERY );
		assertThat( usages ).hasSize( 2 );

		final AnnotationUsage<NamedQuery> abc = entityClass.getNamedAnnotation( NAMED_QUERY, "abc" );
		assertThat( abc.getAttributeValue( "query" ).asString() ).isEqualTo( "select me" );

		final AnnotationUsage<NamedQuery> xyz = entityClass.getNamedAnnotation( NAMED_QUERY, "xyz" );
		assertThat( xyz.getAttributeValue( "query" ).asString() ).isEqualTo( "select you" );

		// just to test
		final AnnotationUsage<NamedQuery> xyzReverse = entityClass.getNamedAnnotation( NAMED_QUERY, "select you", "query" );
		assertThat( xyzReverse.getAttributeValue( "name" ).asString() ).isEqualTo( "xyz" );

		final MutableInteger expectedIndexRef = new MutableInteger();
		entityClass.forEachAnnotation( NAMED_QUERY, (usage) -> {
			expectedIndexRef.getAndIncrement();
		} );
		assertThat( expectedIndexRef.get() ).isEqualTo( 2 );
	}

	private FieldDetails findNamedField(List<FieldDetails> fields, String name) {
		for ( FieldDetails field : fields ) {
			if ( field.getName().equals( name ) ) {
				return field;
			}
		}
		throw new RuntimeException();
	}


	private void verifyNameMapping(FieldDetails nameAttributeSource) {
		final AnnotationUsage<Column> column = nameAttributeSource.getAnnotation( COLUMN );
		assertThat( column.getAttributeValue( "name" ).asString() ).isEqualTo( "description" );
		assertThat( column.getAttributeValue( "table" ).asString() ).isNull();
		assertThat( column.getAttributeValue( "table" ).isDefaultValue() ).isTrue();
		assertThat( column.getAttributeValue( "nullable" ).asBoolean() ).isFalse();
//		assertThat( column.getAttributeValue( "nullable" ).isDefaultValue() ).isTrue();
		assertThat( column.getAttributeValue( "unique" ).asBoolean() ).isTrue();
//		assertThat( column.getAttributeValue( "unique" ).isDefaultValue() ).isTrue();
		assertThat( column.getAttributeValue( "insertable" ).asBoolean() ).isFalse();
//		assertThat( column.getAttributeValue( "insertable" ).isDefaultValue() ).isTrue();
		assertThat( column.getAttributeValue( "updatable" ).asBoolean() ).isTrue();
	}

	private void verifyIdMapping(FieldDetails idSource) {
		final AnnotationUsage<Column> column = idSource.getAnnotation( COLUMN );
		assertThat( column.getAttributeValue( "name" ).asString() ).isEqualTo( "id" );
//		assertThat( column.getAttributeValue( "table" ).isDefaultValue() ).isTrue();
//		assertThat( column.getAttributeValue( "nullable" ).isDefaultValue() ).isTrue();
//		assertThat( column.getAttributeValue( "unique" ).isDefaultValue() ).isTrue();
//		assertThat( column.getAttributeValue( "insertable" ).isDefaultValue() ).isTrue();
//		assertThat( column.getAttributeValue( "updatable" ).isDefaultValue() ).isTrue();
	}

}
