/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.source.internal.standard;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.function.Consumer;

import org.hibernate.models.source.spi.AnnotationDescriptor;
import org.hibernate.models.source.spi.AnnotationUsage;
import org.hibernate.models.source.spi.PackageDetails;
import org.hibernate.models.source.spi.SourceModelBuildingContext;

import org.jboss.jandex.AnnotationTarget;

/**
 * @author Steve Ebersole
 */
public class NoPackageDetailsImpl extends AbstractAnnotationTarget implements PackageDetails {
	private final String packageName;

	public NoPackageDetailsImpl(
			String packageName,
			SourceModelBuildingContext buildingContext) {
		super( buildingContext );
		this.packageName = packageName;
	}

	@Override
	protected AnnotationTarget getJandexAnnotationTarget() {
		return null;
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
