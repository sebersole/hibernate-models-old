/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.orm.internal;

import java.lang.annotation.Annotation;
import java.util.List;

import org.hibernate.models.orm.spi.HibernateAnnotations;
import org.hibernate.models.orm.spi.JpaAnnotations;
import org.hibernate.models.source.internal.explicit.AnnotationDescriptorImpl;
import org.hibernate.models.source.spi.AnnotationAttributeDescriptor;
import org.hibernate.models.source.spi.AnnotationDescriptor;
import org.hibernate.models.source.spi.AnnotationTarget;
import org.hibernate.models.source.spi.AnnotationUsage;

/**
 * Used to represent the {@linkplain JpaAnnotations JPA} and
 * {@linkplain HibernateAnnotations Hibernate} annotations.
 *
 * @implNote We never care about annotations defined on those annotation classes;
 * the {@link AnnotationTarget} contract here behaves as if no annotations where found.
 *
 * @see AnnotationUsage
 *
 * @author Steve Ebersole
 */
public class OrmAnnotationDescriptorImpl<A extends Annotation> extends AnnotationDescriptorImpl<A> {
	public OrmAnnotationDescriptorImpl(
			Class<A> annotationType,
			List<AnnotationAttributeDescriptor> attributeDescriptors,
			AnnotationDescriptor<?> repeatableContainer) {
		super( annotationType, attributeDescriptors, repeatableContainer );
	}
}
