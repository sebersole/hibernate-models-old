/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.source.internal.reflection;

import java.lang.reflect.Method;

import org.hibernate.models.source.internal.LazyAnnotationTarget;
import org.hibernate.models.source.spi.ClassDetails;
import org.hibernate.models.source.spi.MethodDetails;
import org.hibernate.models.source.spi.SourceModelBuildingContext;

import static org.hibernate.models.source.internal.ModifierUtils.isPersistableMethod;

/**
 * @author Steve Ebersole
 */
public class MethodDetailsImpl extends LazyAnnotationTarget implements MethodDetails {
	private final Method method;
	private final ClassDetails type;

	public MethodDetailsImpl(Method method, SourceModelBuildingContext processingContext) {
		super( method::getAnnotations, processingContext );
		this.method = method;
		this.type = processingContext.getClassDetailsRegistry().resolveClassDetails(
				method.getReturnType().getName(),
				() -> ClassDetailsBuilderImpl.buildClassDetails( method.getReturnType(), getProcessingContext() )
		);
	}

	@Override
	public String getName() {
		return method.getName();
	}

	@Override
	public ClassDetails getType() {
		return type;
	}

	@Override
	public boolean isPersistable() {
		if ( method.getParameterCount() > 0 ) {
			// should be the getter
			return false;
		}

		if ( "void".equals( type.getName() ) || "Void".equals( type.getName() ) ) {
			// again, should be the getter
			return false;
		}

		return isPersistableMethod( method.getModifiers() );
	}
}
