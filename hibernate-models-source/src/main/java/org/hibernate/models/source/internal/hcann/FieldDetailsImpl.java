/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.source.internal.hcann;

import org.hibernate.annotations.common.reflection.XProperty;
import org.hibernate.models.source.internal.LazyAnnotationTarget;
import org.hibernate.models.source.spi.ClassDetails;
import org.hibernate.models.source.spi.FieldDetails;
import org.hibernate.models.source.spi.SourceModelBuildingContext;

import static org.hibernate.models.source.internal.ModifierUtils.isPersistableField;


/**
 * @author Steve Ebersole
 */
public class FieldDetailsImpl extends LazyAnnotationTarget implements FieldDetails {
	private final XProperty xProperty;
	private final ClassDetails type;

	public FieldDetailsImpl(XProperty xProperty, SourceModelBuildingContext processingContext) {
		super( xProperty::getAnnotations, processingContext );
		this.xProperty = xProperty;
		this.type = processingContext.getClassDetailsRegistry().resolveClassDetails(
				xProperty.getType().getName(),
				() -> new ClassDetailsImpl( xProperty.getType(), processingContext )
		);
	}

	@Override
	public String getName() {
		return xProperty.getName();
	}

	@Override
	public ClassDetails getType() {
		return type;
	}

	@Override
	public boolean isPersistable() {
		return isPersistableField( xProperty.getModifiers() );
	}

	@Override
	public String toString() {
		return "FieldDetails(`"
				+ xProperty.getDeclaringClass().getName()
				+ "." + xProperty.getName() + "` [HCANN])";
	}
}
