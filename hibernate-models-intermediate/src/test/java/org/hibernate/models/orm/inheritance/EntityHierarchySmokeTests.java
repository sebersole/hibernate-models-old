/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.orm.inheritance;

import java.util.Set;

import org.hibernate.models.orm.spi.EntityHierarchy;
import org.hibernate.models.orm.spi.EntityTypeMetadata;
import org.hibernate.models.source.spi.ClassDetails;

import org.hibernate.testing.orm.junit.ServiceRegistry;
import org.junit.jupiter.api.Test;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Basic;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Table;

import static jakarta.persistence.InheritanceType.JOINED;
import static jakarta.persistence.InheritanceType.SINGLE_TABLE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hibernate.models.orm.TestHelper.buildHierarchies;

/**
 * Simple unit tests for {@link EntityHierarchy}
 *
 * @author Steve Ebersole
 */
@ServiceRegistry
public class EntityHierarchySmokeTests {
	@Test
	void testNoInheritance() {
		final Set<EntityHierarchy> entityHierarchies = buildHierarchies( SimpleEntity.class );

		assertThat( entityHierarchies ).hasSize( 1 );

		final EntityHierarchy hierarchy = entityHierarchies.iterator().next();
		assertThat( hierarchy.getInheritanceType() ).isEqualTo( SINGLE_TABLE );

		final EntityTypeMetadata entityTypeMetadata = hierarchy.getRoot();
		final ClassDetails classDetails = entityTypeMetadata.getClassDetails();
		assertThat( classDetails.getClassName() ).isEqualTo( SimpleEntity.class.getName() );
		assertThat( classDetails.getName() ).isEqualTo( classDetails.getClassName() );
		assertThat( entityTypeMetadata.getEntityName() ).isEqualTo( classDetails.getClassName() );
		assertThat( entityTypeMetadata.getJpaEntityName() ).isEqualTo( "SimpleEntity" );

		assertThat( entityTypeMetadata.getSuperType() ).isNull();
		assertThat( entityTypeMetadata.hasSubTypes() ).isFalse();

		assertThat( entityTypeMetadata.getAttributes() ).hasSize( 3 );
	}

	@Test
	void testJoinedInheritance() {
		final Set<EntityHierarchy> entityHierarchies = buildHierarchies(
				JoinedRoot.class,
				JoinedLeaf.class
		);

		assertThat( entityHierarchies ).hasSize( 1 );

		final EntityHierarchy hierarchy = entityHierarchies.iterator().next();
		assertThat( hierarchy.getInheritanceType() ).isEqualTo( JOINED );

		assertThat( hierarchy.getRoot().getEntityName() ).isEqualTo( JoinedRoot.class.getName() );
		assertThat( hierarchy.getRoot().getSuperType() ).isNull();
		assertThat( hierarchy.getRoot().hasSubTypes() ).isTrue();
		final EntityTypeMetadata subType = (EntityTypeMetadata) hierarchy.getRoot().getSubTypes().iterator().next();
		assertThat( subType.getEntityName() ).isEqualTo( JoinedLeaf.class.getName() );
		assertThat( subType.getSuperType() ).isSameAs( hierarchy.getRoot() );
	}

	@Test
	void testDetailsOnMappedSuper() {
		final Set<EntityHierarchy> entityHierarchies = buildHierarchies(
				MappedSuper.class,
				RootWithMappedSuper.class
		);

		assertThat( entityHierarchies ).hasSize( 1 );

		final EntityHierarchy hierarchy = entityHierarchies.iterator().next();
		assertThat( hierarchy.getInheritanceType() ).isEqualTo( JOINED );
		assertThat( hierarchy.getRoot().getAccessType() ).isEqualTo( AccessType.PROPERTY );
		assertThat( hierarchy.getRoot().getSuperType().getAccessType() ).isEqualTo( AccessType.FIELD );
		assertThat( hierarchy.getRoot().getNumberOfAttributes() ).isEqualTo( 2 );
	}

	@Inheritance( strategy = JOINED )
	@Access( AccessType.FIELD )
	@MappedSuperclass
	public static class MappedSuper {

	}

	@Entity( name = "RootWithMappedSuper" )
	@Table( name = "RootWithMappedSuper" )
	@Access( AccessType.PROPERTY )
	public static class RootWithMappedSuper extends MappedSuper {
	    @Id
		@Access( AccessType.FIELD )
	    private Integer id;
	    @Basic
		@Access( AccessType.FIELD )
		private String name;

		private RootWithMappedSuper() {
			// for use by Hibernate
		}

		public RootWithMappedSuper(Integer id, String name) {
			this.id = id;
			this.name = name;
		}

		public Integer getId() {
			return id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}
}
