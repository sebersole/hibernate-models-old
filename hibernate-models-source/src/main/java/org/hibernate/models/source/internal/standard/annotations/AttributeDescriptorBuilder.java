/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.source.internal.standard.annotations;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

import org.hibernate.models.source.spi.AnnotationAttributeDescriptor;
import org.hibernate.models.source.spi.AnnotationDescriptor;
import org.hibernate.models.source.spi.SourceModelBuildingContext;

import static org.hibernate.models.internal.CollectionHelper.arrayList;
import static org.hibernate.models.source.internal.standard.annotations.BooleanValueExtractor.BOOLEAN_EXTRACTOR;
import static org.hibernate.models.source.internal.standard.annotations.ByteValueExtractor.BYTE_EXTRACTOR;
import static org.hibernate.models.source.internal.standard.annotations.CharacterValueExtractor.CHARACTER_EXTRACTOR;
import static org.hibernate.models.source.internal.standard.annotations.ClassValueExtractor.CLASS_EXTRACTOR;
import static org.hibernate.models.source.internal.standard.annotations.DoubleValueExtractor.DOUBLE_EXTRACTOR;
import static org.hibernate.models.source.internal.standard.annotations.FloatValueExtractor.FLOAT_EXTRACTOR;
import static org.hibernate.models.source.internal.standard.annotations.IntegerValueExtractor.INTEGER_EXTRACTOR;
import static org.hibernate.models.source.internal.standard.annotations.LongValueExtractor.LONG_EXTRACTOR;
import static org.hibernate.models.source.internal.standard.annotations.PassthruValueExtractor.PASSTHRU_EXTRACTOR;
import static org.hibernate.models.source.internal.standard.annotations.ShortValueExtractor.SHORT_EXTRACTOR;
import static org.hibernate.models.source.internal.standard.annotations.StringValueExtractor.STRING_EXTRACTOR;

/**
 * @author Steve Ebersole
 */
public class AttributeDescriptorBuilder {
	public static <A extends Annotation> List<AnnotationAttributeDescriptor> extractAttributeDescriptors(
			Class<A> javaType,
			SourceModelBuildingContext buildingContext) {
		final Method[] attributes = javaType.getDeclaredMethods();
		final List<AnnotationAttributeDescriptor> attributeDescriptors = arrayList( attributes.length );
		for ( int i = 0; i < attributes.length; i++ ) {
			final Method method = attributes[ i ];
			attributeDescriptors.add( createAttributeDescriptor( method, buildingContext ) );
		}
		return attributeDescriptors;
	}

	private static AnnotationAttributeDescriptor createAttributeDescriptor(
			Method method,
			SourceModelBuildingContext buildingContext) {
		final Class<?> attributeType = method.getReturnType();

		if ( attributeType.isArray() ) {
			return buildArrayValuedAttributeDescriptor( method.getName(), attributeType, buildingContext );
		}

		return resolveValueExtractor( attributeType, buildingContext )
				.createAttributeDescriptor( method.getName() );
	}

	@SuppressWarnings("unchecked")
	private static <T> ValueExtractor<T> resolveValueExtractor(
			Class<T> attributeType,
			SourceModelBuildingContext buildingContext) {
		if ( attributeType.isEnum() ) {
			//noinspection rawtypes
			return new EnumValueExtractor<>( (Class) attributeType );
		}

		if ( attributeType == byte.class ) {
			return (ValueExtractor<T>) BYTE_EXTRACTOR;
		}

		if ( attributeType == boolean.class ) {
			return (ValueExtractor<T>) BOOLEAN_EXTRACTOR;
		}

		if ( attributeType == short.class ) {
			return (ValueExtractor<T>) SHORT_EXTRACTOR;
		}

		if ( attributeType == int.class ) {
			return (ValueExtractor<T>) INTEGER_EXTRACTOR;
		}

		if ( attributeType == long.class ) {
			return (ValueExtractor<T>) LONG_EXTRACTOR;
		}

		if ( attributeType == float.class ) {
			return (ValueExtractor<T>) FLOAT_EXTRACTOR;
		}

		if ( attributeType == double.class ) {
			return (ValueExtractor<T>) DOUBLE_EXTRACTOR;
		}

		if ( attributeType == char.class ) {
			return (ValueExtractor<T>) CHARACTER_EXTRACTOR;
		}

		if ( attributeType == String.class ) {
			return (ValueExtractor<T>) STRING_EXTRACTOR;
		}

		if ( attributeType == Class.class ) {
			return (ValueExtractor<T>) CLASS_EXTRACTOR;
		}

		if ( Annotation.class.isAssignableFrom( attributeType ) ) {
			final AnnotationDescriptor<?> descriptor = buildingContext
					.getAnnotationDescriptorRegistry()
					.getDescriptor( (Class<? extends Annotation>) attributeType );
			return (ValueExtractor<T>) new NestedValueExtractor<>( descriptor );
		}

		return (ValueExtractor<T>) PASSTHRU_EXTRACTOR;
	}

	private static AnnotationAttributeDescriptor buildArrayValuedAttributeDescriptor(
			String name,
			Class<?> attributeType,
			SourceModelBuildingContext buildingContext) {
		final ValueExtractor<?> valueExtractor = resolveValueExtractor(
				attributeType.getComponentType(),
				buildingContext
		);
		return new ArrayValueDescriptor<>( name, valueExtractor );
	}

}
