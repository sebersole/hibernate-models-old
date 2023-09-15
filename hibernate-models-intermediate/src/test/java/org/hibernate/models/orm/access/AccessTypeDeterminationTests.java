/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.orm.access;

import java.util.Set;

import org.hibernate.models.orm.TestHelper;
import org.hibernate.models.orm.spi.EntityHierarchy;
import org.hibernate.models.orm.spi.EntityTypeMetadata;

import org.junit.jupiter.api.Test;

import jakarta.persistence.AccessType;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Steve Ebersole
 */
public class AccessTypeDeterminationTests {
	@Test
	void testImplicitAccessType() {
		final Set<EntityHierarchy> entityHierarchies = TestHelper.buildHierarchies(
				SimpleImplicitEntity.class
		);

		final EntityHierarchy hierarchy = entityHierarchies.iterator().next();
		final EntityTypeMetadata entity = hierarchy.getRoot();
		assertThat( entity.getAccessType() ).isEqualTo( AccessType.FIELD );
	}

	@Test
	void testExplicitAccessType() {
		final Set<EntityHierarchy> entityHierarchies = TestHelper.buildHierarchies(
				SimpleExplicitEntity.class
		);

		final EntityHierarchy hierarchy = entityHierarchies.iterator().next();
		final EntityTypeMetadata entity = hierarchy.getRoot();
		assertThat( entity.getAccessType() ).isEqualTo( AccessType.FIELD );
	}

	@Test
	void testImplicitAccessTypeMappedSuper() {
		final Set<EntityHierarchy> entityHierarchies = TestHelper.buildHierarchies(
				ImplicitMappedSuper.class,
				ImplicitMappedSuperRoot.class
		);

		final EntityHierarchy hierarchy = entityHierarchies.iterator().next();
		final EntityTypeMetadata root = hierarchy.getRoot();
		assertThat( root.getAccessType() ).isEqualTo( AccessType.FIELD );
		assertThat( root.getNumberOfAttributes() ).isEqualTo( 1 );
		assertThat( root.getNumberOfSubTypes() ).isEqualTo( 0 );
		assertThat( root.getSuperType() ).isNotNull();
		assertThat( root.getSuperType().getAccessType() ).isEqualTo( AccessType.FIELD );
		assertThat( root.getSuperType().getNumberOfAttributes() ).isEqualTo( 1 );
	}

	@Test
	void testExplicitAccessTypeMappedSuper() {
		final Set<EntityHierarchy> entityHierarchies = TestHelper.buildHierarchies(
				ExplicitMappedSuper.class,
				ExplicitMappedSuperRoot1.class
		);

		final EntityHierarchy hierarchy = entityHierarchies.iterator().next();
		final EntityTypeMetadata root = hierarchy.getRoot();
		assertThat( root.getAccessType() ).isEqualTo( AccessType.PROPERTY );
		assertThat( root.getNumberOfAttributes() ).isEqualTo( 1 );
		assertThat( root.getNumberOfSubTypes() ).isEqualTo( 0 );
		assertThat( root.getSuperType() ).isNotNull();
		assertThat( root.getSuperType().getAccessType() ).isEqualTo( AccessType.FIELD );
		assertThat( root.getSuperType().getNumberOfAttributes() ).isEqualTo( 1 );
	}

}
