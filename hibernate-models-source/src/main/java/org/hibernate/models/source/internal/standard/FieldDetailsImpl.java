/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.source.internal.standard;

import org.hibernate.models.source.spi.ClassDetails;
import org.hibernate.models.source.spi.FieldDetails;
import org.hibernate.models.source.spi.SourceModelBuildingContext;

import org.jboss.jandex.AnnotationTarget;
import org.jboss.jandex.FieldInfo;

import static org.hibernate.models.source.internal.ModifierUtils.isPersistableField;

/**
 * @author Steve Ebersole
 */
public class FieldDetailsImpl extends AbstractAnnotationTarget implements FieldDetails {
	private final FieldInfo fieldInfo;
	private final ClassDetails type;

	public FieldDetailsImpl(
			FieldInfo fieldInfo,
			SourceModelBuildingContext buildingContext) {
		super( buildingContext );
		this.fieldInfo = fieldInfo;
		this.type = buildingContext.getClassDetailsRegistry().resolveClassDetails(
				fieldInfo.type().name().toString(),
				ClassDetailsBuilderImpl::buildClassDetailsStatic
		);
	}

	@Override
	protected AnnotationTarget getJandexAnnotationTarget() {
		return fieldInfo;
	}

	@Override
	public String getName() {
		return fieldInfo.name();
	}

	@Override
	public ClassDetails getType() {
		return type;
	}

	@Override
	public boolean isPersistable() {
		return isPersistableField( fieldInfo.flags() );
	}
}
