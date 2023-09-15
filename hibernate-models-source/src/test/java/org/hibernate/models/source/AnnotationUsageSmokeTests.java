/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.source;

import java.lang.reflect.Field;
import java.util.List;

import org.hibernate.models.source.internal.AnnotationUsageImpl;
import org.hibernate.models.source.spi.AnnotationDescriptor;
import org.hibernate.models.source.spi.AnnotationDescriptorRegistry;
import org.hibernate.models.source.spi.AnnotationUsage;
import org.hibernate.models.source.spi.SourceModelBuildingContext;

import org.junit.jupiter.api.Test;

import jakarta.persistence.Column;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hibernate.models.source.TestHelper.COLUMN;

/**
 * @author Steve Ebersole
 */
public class AnnotationUsageSmokeTests {

	@Test
	void testColumn() throws NoSuchFieldException {
		final SourceModelBuildingContext processingContext = TestHelper.buildProcessingContext();

		final Field nameField = SimpleEntity.class.getDeclaredField( "name" );
		final Column nameColumnAnn = nameField.getAnnotation( Column.class );
		final AnnotationUsageImpl<Column> nameColumnUsage = new AnnotationUsageImpl<>( nameColumnAnn, COLUMN, null, processingContext );

		assertThat( nameColumnUsage.getAttributeValue( "name" ).asString() ).isEqualTo( "description" );
		assertThat( nameColumnUsage.getAttributeValue( "table" ).asString() ).isNull();
		assertThat( nameColumnUsage.getAttributeValue( "nullable" ).asBoolean() ).isFalse();
		assertThat( nameColumnUsage.getAttributeValue( "unique" ).asBoolean() ).isTrue();
		assertThat( nameColumnUsage.getAttributeValue( "insertable" ).asBoolean() ).isFalse();
		assertThat( nameColumnUsage.getAttributeValue( "updatable" ).asBoolean() ).isTrue();
	}

	@Test
	void testMetaAnnotation() {
		final SourceModelBuildingContext processingContext = TestHelper.buildProcessingContext();
		final AnnotationDescriptorRegistry descriptorRegistry = processingContext.getAnnotationDescriptorRegistry();

		final AnnotationDescriptor<CustomAnnotation> descriptor = descriptorRegistry.getDescriptor( CustomAnnotation.class );
		final AnnotationDescriptor<CustomMetaAnnotation> metaDescriptor = descriptorRegistry.getDescriptor( CustomMetaAnnotation.class );
		assertThat( descriptor ).isNotNull();
		assertThat( metaDescriptor ).isNotNull();

		final AnnotationUsage<CustomMetaAnnotation> metaUsage = descriptor.getAnnotation( metaDescriptor );
		assertThat( metaUsage ).isNotNull();
	}

	@Test
	void testGlobalUsages() {
		final SourceModelBuildingContext processingContext = TestHelper.buildProcessingContext();
		final AnnotationDescriptorRegistry descriptorRegistry = processingContext.getAnnotationDescriptorRegistry();

		final AnnotationDescriptor<CustomAnnotation> descriptor = descriptorRegistry.getDescriptor( CustomAnnotation.class );
		final List<AnnotationUsage<CustomAnnotation>> allDescriptorUsages = processingContext.getAllUsages( descriptor );

		final AnnotationDescriptor<CustomMetaAnnotation> metaDescriptor = descriptorRegistry.getDescriptor( CustomMetaAnnotation.class );
		final List<AnnotationUsage<CustomAnnotation>> allMetaDescriptorUsages = processingContext.getAllUsages( descriptor );
	}
}
