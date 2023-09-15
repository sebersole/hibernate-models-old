/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.source.internal;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.function.Consumer;

import org.hibernate.models.source.spi.AnnotationDescriptor;
import org.hibernate.models.source.spi.AnnotationUsage;
import org.hibernate.models.source.spi.PackageDetails;

/**
 * @author Steve Ebersole
 */
public class NoPackageDetailsImpl implements PackageDetails {
	private final String packageName;

	public NoPackageDetailsImpl(String packageName) {
		this.packageName = packageName;
	}

	@Override
	public String getName() {
		return packageName;
	}

	@Override
	public <A extends Annotation> AnnotationUsage<A> getAnnotation(AnnotationDescriptor<A> type) {
		return null;
	}

	@Override
	public <A extends Annotation> List<AnnotationUsage<A>> getRepeatedAnnotations(AnnotationDescriptor<A> type) {
		return null;
	}

	@Override
	public <X extends Annotation> void forEachAnnotation(AnnotationDescriptor<X> type, Consumer<AnnotationUsage<X>> consumer) {
	}

	@Override
	public <X extends Annotation> AnnotationUsage<X> getNamedAnnotation(AnnotationDescriptor<X> type, String matchName, String attributeToMatch) {
		return null;
	}
}
