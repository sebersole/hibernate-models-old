package org.hibernate.models.source.spi;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.function.Consumer;

/**
 * A scope for annotations - global, package, class, ...
 *
 * @author Steve Ebersole
 */
public interface AnnotationScope {

	<A extends Annotation> List<AnnotationUsage<A>> getAllUsages(AnnotationDescriptor<A> annotationDescriptor);

	<A extends Annotation> List<AnnotationUsage<A>> getAllUsages(A annotation);

	<A extends Annotation> void forEachUsage(AnnotationDescriptor<A> annotationDescriptor, Consumer<AnnotationUsage<A>> consumer);

	<A extends Annotation> void forEachUsage(A annotation, Consumer<AnnotationUsage<A>> consumer);

	SourceModelBuildingContext getSourceModelBuildingContext();

	void registerUsage(AnnotationUsage<?> usage);
}
