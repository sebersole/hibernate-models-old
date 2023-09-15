package org.hibernate.models.source.spi;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Objects;

/**
 * @author Steve Ebersole
 */
public interface NamedAnnotationScope extends AnnotationScope {
	/**
	 * Get a usage of the given annotation {@code annotationDescriptor} whose {@code attributeToMatch} attribute value
	 * matches the given {@code matchName}.
	 *
	 * @param annotationDescriptor The type of annotation to search
	 * @param matchName The name to match.
	 * @return The matching annotation usage, or {@code null} if none matched
	 */
	default <X extends Annotation> AnnotationUsage<X> getNamedAnnotation(
			AnnotationDescriptor<X> annotationDescriptor,
			String matchName) {
		return getNamedAnnotation( annotationDescriptor, matchName, "name" );
	}

	/**
	 * Get a usage of the given {@code annotationDescriptor} whose
	 * {@code attributeToMatch} attribute value matches the given {@code matchName}.
	 *
	 * @param annotationDescriptor The type of annotation to search
	 * @param matchName The name to match.
	 * @param attributeToMatch Name of the attribute to match on.
	 *
	 * @return The matching annotation usage, or {@code null} if none matched
	 */
	default <A extends Annotation> AnnotationUsage<A> getNamedAnnotation(
			AnnotationDescriptor<A> annotationDescriptor,
			String matchName,
			String attributeToMatch) {
		final List<AnnotationUsage<A>> allUsages = getAllUsages( annotationDescriptor );
		for ( int i = 0; i < allUsages.size(); i++ ) {
			final AnnotationUsage<A> annotationUsage = allUsages.get( i );
			final Object usageName = annotationUsage.extractAttributeValue( attributeToMatch );
			if ( Objects.equals( matchName, usageName ) ) {
				return annotationUsage;
			}
		}
		return null;
	}
}
