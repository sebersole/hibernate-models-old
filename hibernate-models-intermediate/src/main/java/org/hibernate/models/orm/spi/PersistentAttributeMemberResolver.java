/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.orm.spi;

import java.util.List;

import org.hibernate.models.source.spi.ClassDetails;
import org.hibernate.models.source.spi.MemberDetails;

import jakarta.persistence.AccessType;

/**
 * Contract responsible for resolving the members that identify the persistent
 * attributes for a given class descriptor representing a managed type.
 *
 * These members (field or method) would be where we look for mapping annotations
 * for the attribute.
 *
 * Additionally, whether the member is a field or method would tell us the default
 * runtime access strategy
 *
 * @author Steve Ebersole
 */
public interface PersistentAttributeMemberResolver {
	/**
	 * Given the class descriptor representing a ManagedType and the implicit AccessType
	 * to use, resolve the members that indicate persistent attributes.
	 *
	 * @param classDetails Descriptor of the class
	 * @param classLevelAccessType The implicit AccessType
	 * @param buildingContext The local context
	 *
	 * @return The list of "backing members"
	 */
	List<MemberDetails> resolveAttributesMembers(
			ClassDetails classDetails,
			AccessType classLevelAccessType,
			OrmModelBuildingContext buildingContext);

}
