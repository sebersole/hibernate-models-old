/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.source.internal.standard;

import org.hibernate.models.source.spi.ClassDetails;
import org.hibernate.models.source.spi.MethodDetails;
import org.hibernate.models.source.spi.SourceModelBuildingContext;

import org.jboss.jandex.AnnotationTarget;
import org.jboss.jandex.MethodInfo;

import static org.hibernate.models.source.internal.ModifierUtils.isPersistableMethod;

/**
 * @author Steve Ebersole
 */
public class MethodDetailsImpl extends AbstractAnnotationTarget implements MethodDetails {
	private final MethodInfo methodInfo;
	private final ClassDetails type;

	public MethodDetailsImpl(
			MethodInfo methodInfo,
			SourceModelBuildingContext buildingContext) {
		super( buildingContext );
		this.methodInfo = methodInfo;
		this.type = buildingContext.getClassDetailsRegistry().resolveClassDetails(
				methodInfo.returnType().name().toString(),
				ClassDetailsBuilderImpl::buildClassDetailsStatic
		);
	}

	@Override
	protected AnnotationTarget getJandexAnnotationTarget() {
		return methodInfo;
	}

	@Override
	public String getName() {
		return methodInfo.name();
	}

	@Override
	public ClassDetails getType() {
		return type;
	}

	@Override
	public boolean isPersistable() {
		if ( methodInfo.parametersCount() > 0 ) {
			return false;
		}

		if ( "void".equals( type.getName() ) || "Void".equals( type.getName() ) ) {
			return false;
		}

		return isPersistableMethod( methodInfo.flags() );
	}
}
