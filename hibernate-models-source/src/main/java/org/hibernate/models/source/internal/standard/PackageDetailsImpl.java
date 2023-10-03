/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.source.internal.standard;

import org.hibernate.models.source.spi.PackageDetails;
import org.hibernate.models.source.spi.SourceModelBuildingContext;

import org.jboss.jandex.AnnotationTarget;
import org.jboss.jandex.ClassInfo;

/**
 * Details about a package, presumably with annotations on its {@linkplain package-inf.class}.
 *
 * @author Steve Ebersole
 */
public class PackageDetailsImpl extends AbstractAnnotationTarget implements PackageDetails {
	private final ClassInfo packageInfoClassInfo;

	public PackageDetailsImpl(
			ClassInfo packageInfoClassInfo,
			SourceModelBuildingContext buildingContext) {
		super( buildingContext );
		this.packageInfoClassInfo = packageInfoClassInfo;
	}

	@Override
	protected AnnotationTarget getJandexAnnotationTarget() {
		return packageInfoClassInfo;
	}

	@Override
	public String getName() {
		return packageInfoClassInfo.name().packagePrefix();
	}
}
