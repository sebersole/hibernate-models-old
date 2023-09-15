/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.source.internal.dynamic;

import java.lang.annotation.Annotation;
import java.util.List;

import org.hibernate.models.source.spi.AnnotationTarget;
import org.hibernate.models.source.spi.AnnotationUsage;


/**
 * Specialization of AnnotationTarget where the annotations are not known up
 * front.  Rather, they are {@linkplain  #apply applied} later
 *
 * @author Steve Ebersole
 */
public interface DynamicAnnotationTarget extends AnnotationTarget {
	<X extends Annotation> void apply(List<AnnotationUsage<X>> annotationUsages);

	<X extends Annotation> void apply(AnnotationUsage<X> annotationUsage);

	void apply(Annotation[] annotations);

	void apply(Annotation annotation);
}
