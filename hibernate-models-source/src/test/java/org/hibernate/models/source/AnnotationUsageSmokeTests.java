/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.source;

import java.util.List;

import org.hibernate.models.source.internal.standard.annotations.AnnotationUsageBuilder;
import org.hibernate.models.source.spi.AnnotationDescriptor;
import org.hibernate.models.source.spi.AnnotationDescriptorRegistry;
import org.hibernate.models.source.spi.AnnotationUsage;
import org.hibernate.models.source.spi.SourceModelBuildingContext;

import org.junit.jupiter.api.Test;

import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.DotName;
import org.jboss.jandex.FieldInfo;

import jakarta.persistence.Column;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hibernate.models.source.TestHelper.createBuildingContext;

/**
 * @author Steve Ebersole
 */
public class AnnotationUsageSmokeTests {

	@Test
	void testColumn() throws NoSuchFieldException {
		final SourceModelBuildingContext buildingContext = createBuildingContext(
				SimpleEntity.class,
				AnnotationUsageSemanticTests.Whatever.class,
				AnnotationUsageSemanticTests.Something.class,
				AnnotationUsageSemanticTests.SomethingExtra.class,
				CustomAnnotation.class,
				CustomAnnotations.class,
				CustomMetaAnnotation.class
		);

		final ClassInfo classInfo = buildingContext.getJandexIndex().getClassByName( SimpleEntity.class );
		final FieldInfo nameField = classInfo.field( "name" );
		final AnnotationInstance nameColumnAnn = nameField.annotation( DotName.createSimple( Column.class ) );

		final AnnotationDescriptor<Column> columnDescriptor = buildingContext
				.getAnnotationDescriptorRegistry()
				.getDescriptor( Column.class );
		final AnnotationUsage<?> nameColumnUsage = AnnotationUsageBuilder.makeUsage(
				nameColumnAnn,
				columnDescriptor,
				null,
				buildingContext
		);

		assertThat( nameColumnUsage.getAttributeValue( "name" ).asString() ).isEqualTo( "description" );

		assertThat( nameColumnUsage.getAttributeValue( "table" ).asString() ).isEqualTo( "" );
		assertThat( nameColumnUsage.getAttributeValue( "table" ).isImplicit() ).isTrue();

		assertThat( nameColumnUsage.getAttributeValue( "nullable" ).asBoolean() ).isFalse();
		assertThat( nameColumnUsage.getAttributeValue( "nullable" ).asBoolean() ).isFalse();

		assertThat( nameColumnUsage.getAttributeValue( "unique" ).asBoolean() ).isTrue();

		assertThat( nameColumnUsage.getAttributeValue( "insertable" ).asBoolean() ).isFalse();

		assertThat( nameColumnUsage.getAttributeValue( "updatable" ).asBoolean() ).isTrue();
	}

	@Test
	void testMetaAnnotation() {
		final SourceModelBuildingContext buildingContext = createBuildingContext(
				SimpleEntity.class,
				AnnotationUsageSemanticTests.Whatever.class,
				AnnotationUsageSemanticTests.Something.class,
				AnnotationUsageSemanticTests.SomethingExtra.class,
				CustomAnnotation.class,
				CustomAnnotations.class,
				CustomMetaAnnotation.class
		);
		final AnnotationDescriptorRegistry descriptorRegistry = buildingContext.getAnnotationDescriptorRegistry();

		final AnnotationDescriptor<CustomAnnotation> descriptor = descriptorRegistry.getDescriptor( CustomAnnotation.class );
		final AnnotationDescriptor<CustomMetaAnnotation> metaDescriptor = descriptorRegistry.getDescriptor( CustomMetaAnnotation.class );
		assertThat( descriptor ).isNotNull();
		assertThat( metaDescriptor ).isNotNull();

		final AnnotationUsage<CustomMetaAnnotation> metaUsage = descriptor.getAnnotation( metaDescriptor );
		assertThat( metaUsage ).isNotNull();
	}

	@Test
	void testGlobalUsages() {
		final SourceModelBuildingContext buildingContext = createBuildingContext(
				SimpleEntity.class,
				AnnotationUsageSemanticTests.Whatever.class,
				AnnotationUsageSemanticTests.Something.class,
				AnnotationUsageSemanticTests.SomethingExtra.class,
				CustomAnnotation.class,
				CustomAnnotations.class,
				CustomMetaAnnotation.class
		);
		final AnnotationDescriptorRegistry descriptorRegistry = buildingContext.getAnnotationDescriptorRegistry();

		final AnnotationDescriptor<CustomAnnotation> descriptor = descriptorRegistry.getDescriptor( CustomAnnotation.class );
		final List<AnnotationUsage<CustomAnnotation>> allDescriptorUsages = buildingContext.getAllUsages( descriptor );

		final AnnotationDescriptor<CustomMetaAnnotation> metaDescriptor = descriptorRegistry.getDescriptor( CustomMetaAnnotation.class );
		final List<AnnotationUsage<CustomAnnotation>> allMetaDescriptorUsages = buildingContext.getAllUsages( descriptor );
	}
}
