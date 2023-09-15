/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.source.internal.dynamic;

import org.hibernate.models.source.spi.ClassDetails;
import org.hibernate.models.source.spi.MethodDetails;
import org.hibernate.models.source.spi.SourceModelBuildingContext;

/**
 *
 * @author Steve Ebersole
 */
public class MethodDetailsImpl extends AbstractDynamicAnnotationTarget implements DynamicMemberDetails, MethodDetails {
	private final String name;
	private final String attributeName;
	private ClassDetails type;

	public MethodDetailsImpl(
			String name,
			String attributeName,
			SourceModelBuildingContext processingContext) {
		super( processingContext );
		this.name = name;
		this.attributeName = attributeName;
	}

	public MethodDetailsImpl(
			String name,
			SourceModelBuildingContext processingContext) {
		this( name, null, processingContext );
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String resolveAttributeName() {
		return attributeName;
	}

	@Override
	public ClassDetails getType() {
		return type;
	}

	public void setType(ClassDetails type) {
		this.type = type;
	}

	@Override
	public boolean isPersistable() {
		return attributeName != null;
	}
}
