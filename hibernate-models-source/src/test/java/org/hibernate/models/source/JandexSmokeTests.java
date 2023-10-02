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
import org.hibernate.internal.util.MutableInteger;
import org.hibernate.models.source.spi.AnnotationDescriptorRegistry;
import org.hibernate.models.source.spi.AnnotationUsage;
import org.hibernate.models.source.spi.ClassDetails;
import org.hibernate.models.source.spi.FieldDetails;
import org.hibernate.models.source.spi.MethodDetails;
import org.hibernate.models.source.spi.SourceModelBuildingContext;

import org.junit.jupiter.api.Test;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.NamedQuery;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hibernate.models.source.TestHelper.createBuildingContext;
import static org.hibernate.models.source.internal.AnnotationHelper.extractTargets;
import static org.hibernate.models.source.spi.AnnotationTarget.Kind.CLASS;
import static org.hibernate.models.source.spi.AnnotationTarget.Kind.FIELD;
import static org.hibernate.models.source.spi.AnnotationTarget.Kind.METHOD;
import static org.hibernate.models.source.spi.AnnotationTarget.Kind.PACKAGE;

/**
 * Smoke tests about how Jandex handles various situations
 *
 * @author Steve Ebersole
 */
public class JandexSmokeTests {
	@Test
	void basicAssertions() {
		final SourceModelBuildingContext buildingContext = createBuildingContext(
				SimpleEntity.class,
				CustomAnnotation.class,
				CustomAnnotations.class
		);
		final ClassDetails classDetails = buildingContext
				.getClassDetailsRegistry()
				.resolveClassDetails( SimpleEntity.class.getName() );

		assertThat( classDetails.getFields() ).hasSize( 3 );
		assertThat( classDetails.getFields().stream().map( FieldDetails::getName ) )
				.containsAll( Arrays.asList( "id", "name", "name2" ) );

		assertThat( classDetails.getMethods() ).hasSize( 3 );
		assertThat( classDetails.getMethods().stream().map( MethodDetails::getName ) )
				.containsAll( Arrays.asList( "getId", "getName", "setName" ) );

		verifyNameMapping( findNamedField( classDetails.getFields(), "name" ) );
		verifyIdMapping( findNamedField( classDetails.getFields(), "id" ) );

		final AnnotationDescriptorRegistry descriptorRegistry = buildingContext.getAnnotationDescriptorRegistry();
		final AnnotationUsage<CustomAnnotation> customAnnotation = classDetails.getAnnotation( descriptorRegistry.getDescriptor( CustomAnnotation.class ) );
		assertThat( customAnnotation ).isNotNull();
	}

	@Test
	void testAllowableTargets() {
		assertThat( extractTargets( Column.class ) ).containsOnly( FIELD, METHOD );
		assertThat( extractTargets( Entity.class ) ).containsOnly( CLASS );
		assertThat( extractTargets( JavaTypeRegistration.class ) ).contains( PACKAGE );
	}

	@Test
	void testRepeatableAnnotationHandling() {
		final SourceModelBuildingContext buildingContext = createBuildingContext(
				SimpleEntity.class,
				CustomAnnotation.class,
				CustomAnnotations.class
		);
		final ClassDetails classDetails = buildingContext
				.getClassDetailsRegistry()
				.resolveClassDetails( SimpleEntity.class.getName() );

		final List<AnnotationUsage<NamedQuery>> usages = classDetails.getRepeatedAnnotations( NamedQuery.class );
		assertThat( usages ).hasSize( 2 );

		final AnnotationUsage<NamedQuery> abc = classDetails.getNamedAnnotation( NamedQuery.class, "abc" );
		assertThat( abc.getAttributeValue( "query" ).asString() ).isEqualTo( "select me" );

		final AnnotationUsage<NamedQuery> xyz = classDetails.getNamedAnnotation( NamedQuery.class, "xyz" );
		assertThat( xyz.getAttributeValue( "query" ).asString() ).isEqualTo( "select you" );

		// just to test
		final AnnotationUsage<NamedQuery> xyzReverse = classDetails.getNamedAnnotation( NamedQuery.class, "select you", "query" );
		assertThat( xyzReverse.getAttributeValue( "name" ).asString() ).isEqualTo( "xyz" );

		final MutableInteger expectedIndexRef = new MutableInteger();
		classDetails.forEachAnnotation( NamedQuery.class, (usage) -> {
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
		final AnnotationUsage<Column> column = nameAttributeSource.getAnnotation( Column.class );
		assertThat( column.getAttributeValue( "name" ).asString() ).isEqualTo( "description" );

		assertThat( column.getAttributeValue( "table" ).asString() ).isEqualTo( "" );
		assertThat( column.getAttributeValue( "table" ).isImplicit() ).isTrue();

		assertThat( column.getAttributeValue( "nullable" ).asBoolean() ).isFalse();
		assertThat( column.getAttributeValue( "nullable" ).isImplicit() ).isFalse();

		assertThat( column.getAttributeValue( "unique" ).asBoolean() ).isTrue();
		assertThat( column.getAttributeValue( "unique" ).isImplicit() ).isFalse();

		assertThat( column.getAttributeValue( "insertable" ).asBoolean() ).isFalse();
		assertThat( column.getAttributeValue( "insertable" ).isImplicit() ).isFalse();

		assertThat( column.getAttributeValue( "updatable" ).asBoolean() ).isTrue();
		assertThat( column.getAttributeValue( "updatable" ).isImplicit() ).isTrue();
	}

	private void verifyIdMapping(FieldDetails idSource) {
		final AnnotationUsage<Column> column = idSource.getAnnotation( Column.class );
		assertThat( column.getAttributeValue( "name" ).asString() ).isEqualTo( "id" );
//		assertThat( column.getAttributeValue( "table" ).isDefaultValue() ).isTrue();
//		assertThat( column.getAttributeValue( "nullable" ).isDefaultValue() ).isTrue();
//		assertThat( column.getAttributeValue( "unique" ).isDefaultValue() ).isTrue();
//		assertThat( column.getAttributeValue( "insertable" ).isDefaultValue() ).isTrue();
//		assertThat( column.getAttributeValue( "updatable" ).isDefaultValue() ).isTrue();
	}

}
