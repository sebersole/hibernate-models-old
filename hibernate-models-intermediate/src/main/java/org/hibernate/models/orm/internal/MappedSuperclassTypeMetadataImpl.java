/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.orm.internal;

import java.util.List;
import java.util.function.Consumer;

import org.hibernate.models.orm.spi.AttributeMetadata;
import org.hibernate.models.orm.spi.EntityHierarchy;
import org.hibernate.models.orm.spi.IdentifiableTypeMetadata;
import org.hibernate.models.orm.spi.MappedSuperclassTypeMetadata;
import org.hibernate.models.source.spi.ClassDetails;
import org.hibernate.models.orm.spi.OrmModelBuildingContext;

import jakarta.persistence.AccessType;

/**
 * @author Steve Ebersole
 */
public class MappedSuperclassTypeMetadataImpl
		extends AbstractIdentifiableTypeMetadata
		implements MappedSuperclassTypeMetadata {

	private final List<AttributeMetadata> attributeList;

	public MappedSuperclassTypeMetadataImpl(
			ClassDetails classDetails,
			EntityHierarchy hierarchy,
			AccessType defaultAccessType,
			Consumer<IdentifiableTypeMetadata> typeConsumer,
			OrmModelBuildingContext modelContext) {
		super( classDetails, hierarchy, false, defaultAccessType, typeConsumer, modelContext );

		this.attributeList = resolveAttributes();
	}

	public MappedSuperclassTypeMetadataImpl(
			ClassDetails classDetails,
			EntityHierarchy hierarchy,
			AbstractIdentifiableTypeMetadata superType,
			Consumer<IdentifiableTypeMetadata> typeConsumer,
			OrmModelBuildingContext modelContext) {
		super( classDetails, hierarchy, superType, typeConsumer, modelContext );

		this.attributeList = resolveAttributes();
	}

	@Override
	protected List<AttributeMetadata> attributeList() {
		return attributeList;
	}
}
