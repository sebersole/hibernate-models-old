/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.source.internal.hcann;

import org.hibernate.annotations.common.reflection.XMethod;
import org.hibernate.models.source.internal.LazyAnnotationTarget;
import org.hibernate.models.source.spi.ClassDetails;
import org.hibernate.models.source.spi.MethodDetails;
import org.hibernate.models.source.spi.SourceModelBuildingContext;

import static org.hibernate.models.source.internal.ModifierUtils.isPersistableMethod;

/**
 * @author Steve Ebersole
 */
public class MethodDetailsImpl extends LazyAnnotationTarget implements MethodDetails {
	private final XMethod xMethod;
	private final ClassDetails type;

	public MethodDetailsImpl(XMethod xMethod, SourceModelBuildingContext processingContext) {
		super( xMethod::getAnnotations, processingContext );
		this.xMethod = xMethod;
		this.type = processingContext.getClassDetailsRegistry().resolveClassDetails(
				xMethod.getType().getName(),
				() -> new ClassDetailsImpl( xMethod.getType(), processingContext )
		);
	}

	@Override
	public String getName() {
		return xMethod.getName();
	}

	@Override
	public ClassDetails getType() {
		return type;
	}

	@Override
	public boolean isPersistable() {
		// todo (annotation-source) : HCANN does not give access to arguments
//		if ( !xMethod.getArguments().isEmpty() ) {
//			return false;
//		}

		if ( "void".equals( type.getName() ) || "Void".equals( type.getName() ) ) {
			return false;
		}

		return isPersistableMethod( xMethod.getModifiers() );
	}

	@Override
	public String toString() {
		return "MethodDetails(`"
				+ xMethod.getDeclaringClass().getName()
				+ "." + xMethod.getName() + "` [HCANN])";
	}
}
