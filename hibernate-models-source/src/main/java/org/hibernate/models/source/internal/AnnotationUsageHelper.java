/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.source.internal;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import org.hibernate.models.internal.CollectionHelper;
import org.hibernate.models.source.spi.AnnotationAttributeDescriptor;
import org.hibernate.models.source.spi.AnnotationAttributeValue;
import org.hibernate.models.source.spi.AnnotationDescriptor;
import org.hibernate.models.source.spi.AnnotationUsage;


/**
 * Helper for dealing with annotation wrappers -<ul>
 *     <li>{@link AnnotationDescriptor}</li>
 *     <li>{@link AnnotationAttributeDescriptor}</li>
 *     <li>{@link AnnotationUsage}</li>
 *     <li>{@link AnnotationAttributeValue}</li>
 * </ul>
 *
 * @see AnnotationHelper
 *
 * @author Steve Ebersole
 */
public class AnnotationUsageHelper {

	/**
	 * Get the {@link AnnotationUsage} from the {@code usageMap} for the given {@code type}
	 */
	public static <A extends Annotation> AnnotationUsage<A> getAnnotation(
			AnnotationDescriptor<A> type,
			Map<Class<? extends Annotation>,AnnotationUsage<? extends Annotation>> usageMap) {
		//noinspection unchecked
		return (AnnotationUsage<A>) usageMap.get( type.getAnnotationType() );
	}

	public static <A extends Annotation> List<AnnotationUsage<A>> getRepeatedAnnotations(AnnotationDescriptor<A> type, Map<Class<? extends Annotation>, AnnotationUsage<?>> usageMap) {
		// e.g. `@NamedQuery`
		final AnnotationUsage<A> usage = getAnnotation( type, usageMap );
		// e.g. `@NamedQueries`
		final AnnotationUsage<?> containerUsage = type.getRepeatableContainer() != null
				? getAnnotation( type.getRepeatableContainer(), usageMap )
				: null;

		if ( containerUsage != null ) {
			//noinspection unchecked
			final List<AnnotationUsage<A>> repetitions = (List<AnnotationUsage<A>>) containerUsage.getAttributeValue( "value" ).getValue();
			if ( CollectionHelper.isNotEmpty( repetitions ) ) {
				if ( usage != null ) {
					// we can have both when repeatable + inherited are mixed
					final ArrayList<AnnotationUsage<A>> combined = new ArrayList<>( repetitions );
					combined.add( usage );
					return combined;
				}
				return repetitions;
			}
		}

		if ( usage != null ) {
			return Collections.singletonList( usage );
		}

		return Collections.emptyList();
	}

	public static <A extends Annotation> AnnotationUsage<A> getNamedAnnotation(
			AnnotationDescriptor<A> type,
			String matchValue,
			String attributeToMatch,
			Map<Class<? extends Annotation>, AnnotationUsage<?>> usageMap) {
		final AnnotationUsage<?> annotationUsage = usageMap.get( type.getAnnotationType() );
		if ( annotationUsage != null ) {
			if ( nameMatches( annotationUsage, matchValue, attributeToMatch ) ) {
				//noinspection unchecked
				return (AnnotationUsage<A>) annotationUsage;
			}
			return null;
		}

		final AnnotationDescriptor<?> containerType = type.getRepeatableContainer();
		if ( containerType != null ) {
			final AnnotationUsage<?> containerUsage = usageMap.get( containerType.getAnnotationType() );
			if ( containerUsage != null ) {
				final AnnotationAttributeValue<List<AnnotationUsage<A>>> attributeValue = containerUsage.getAttributeValue( "value" );
				if ( attributeValue != null ) {
					final List<AnnotationUsage<A>> repeatedUsages = attributeValue.getValue();
					for ( int i = 0; i < repeatedUsages.size(); i++ ) {
						final AnnotationUsage<A> repeatedUsage = repeatedUsages.get( i );
						if ( nameMatches( repeatedUsage, matchValue, attributeToMatch ) ) {
							return repeatedUsage;
						}
					}
				}
			}
		}

		return null;
	}

	private static boolean nameMatches(AnnotationUsage<?> annotationUsage, String matchValue, String attributeToMatch) {
		final AnnotationAttributeValue<String> attributeValue = annotationUsage.getAttributeValue( attributeToMatch );
		return attributeValue != null && matchValue.equals( attributeValue.getValue() );
	}

	public static <V> AnnotationAttributeValue<V> extractValueWrapper(AnnotationUsage<?> usage, String attributeName) {
		if ( usage == null ) {
			return null;
		}
		return usage.getAttributeValue( attributeName );
	}

	public static <X> X extractValue(AnnotationUsage<?> usage, String attributeName) {
		if ( usage == null ) {
			return null;
		}

		final AnnotationAttributeValue<X> wrapper = usage.getAttributeValue( attributeName );
		if ( wrapper == null ) {
			return null;
		}

		return wrapper.getValue();
	}

	public static <X> X extractValue(AnnotationUsage<?> usage, String attributeName, X implicitValue) {
		if ( usage == null ) {
			return implicitValue;
		}

		final AnnotationAttributeValue<X> value = extractValueWrapper( usage, attributeName );
		if ( value == null ) {
			return implicitValue;
		}

		if ( value.isImplicit() ) {
			return implicitValue;
		}

		return value.getValue();
	}

	public static <X> X extractValue(AnnotationUsage<?> usage, String attributeName, Supplier<X> defaultValueSupplier) {
		return extractValue( extractValueWrapper( usage, attributeName ), defaultValueSupplier );
	}

	public static <X> X extractValue(AnnotationAttributeValue<X> attributeValue, Supplier<X> defaultValueSupplier) {
		if ( attributeValue == null || attributeValue.isImplicit() ) {
			return defaultValueSupplier.get();
		}
		return attributeValue.getValue();
	}

	private AnnotationUsageHelper() {
	}
}
