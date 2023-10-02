/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.orm.process;

import org.hibernate.models.orm.internal.OrmModelBuildingContextImpl;
import org.hibernate.models.orm.spi.EntityHierarchy;
import org.hibernate.models.orm.spi.EntityTypeMetadata;
import org.hibernate.models.orm.spi.ProcessResult;
import org.hibernate.models.orm.spi.Processor;

import org.junit.jupiter.api.Test;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Basic;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hibernate.models.orm.TestHelper.createBuildingContext;

/**
 * @author Steve Ebersole
 */
public class ProcessorTests {
	@Test
	void testSimple() {
		final OrmModelBuildingContextImpl buildingContext = createBuildingContext( Person.class );
		final ProcessResult result = Processor.process( buildingContext );
		assertThat( result.getEntityHierarchies() ).hasSize( 1 );
		final EntityHierarchy hierarchy = result.getEntityHierarchies().iterator().next();
		assertThat( hierarchy.getInheritanceType() ).isEqualTo( InheritanceType.SINGLE_TABLE );
		assertThat( hierarchy.getCaching() ).isNotNull();
		assertThat( hierarchy.getCaching().isEnabled() ).isFalse();
		assertThat( hierarchy.getNaturalIdCaching() ).isNotNull();
		assertThat( hierarchy.getNaturalIdCaching().isEnabled() ).isFalse();
		assertThat( hierarchy.getRoot() ).isNotNull();
		assertThat( hierarchy.getRoot().getSuperType() ).isNull();
		assertThat( hierarchy.getRoot().hasSubTypes() ).isFalse();
		assertThat( hierarchy.getRoot().getEntityName() ).isEqualTo( Person.class.getName() );
		assertThat( hierarchy.getRoot().getJpaEntityName() ).isEqualTo( Person.class.getSimpleName() );
		assertThat( hierarchy.getRoot().getAccessType() ).isEqualTo( AccessType.FIELD );
		assertThat( hierarchy.getRoot().getAttributes() ).hasSize( 2 );

	}

	@Test
	void testJoined() {
		final OrmModelBuildingContextImpl buildingContext = createBuildingContext( Root.class, Sub.class );
		final ProcessResult result = Processor.process( buildingContext );
		assertThat( result.getEntityHierarchies() ).hasSize( 1 );
		final EntityHierarchy hierarchy = result.getEntityHierarchies().iterator().next();
		assertThat( hierarchy.getInheritanceType() ).isEqualTo( InheritanceType.JOINED );
		assertThat( hierarchy.getCaching() ).isNotNull();
		assertThat( hierarchy.getCaching().isEnabled() ).isFalse();
		assertThat( hierarchy.getNaturalIdCaching() ).isNotNull();
		assertThat( hierarchy.getNaturalIdCaching().isEnabled() ).isFalse();

		assertThat( hierarchy.getRoot() ).isNotNull();
		assertThat( hierarchy.getRoot().getSuperType() ).isNull();
		assertThat( hierarchy.getRoot().hasSubTypes() ).isTrue();
		assertThat( hierarchy.getRoot().getEntityName() ).isEqualTo( Root.class.getName() );
		assertThat( hierarchy.getRoot().getJpaEntityName() ).isEqualTo( Root.class.getSimpleName() );
		assertThat( hierarchy.getRoot().getAccessType() ).isEqualTo( AccessType.FIELD );
		assertThat( hierarchy.getRoot().getAttributes() ).hasSize( 2 );

		final EntityTypeMetadata sub = (EntityTypeMetadata) hierarchy.getRoot().getSubTypes().iterator().next();
		assertThat( sub ).isNotNull();
		assertThat( sub.getSuperType() ).isNotNull();
		assertThat( sub.hasSubTypes() ).isFalse();
		assertThat( sub.getEntityName() ).isEqualTo( Sub.class.getName() );
		assertThat( sub.getJpaEntityName() ).isEqualTo( Sub.class.getSimpleName() );
		assertThat( sub.getAccessType() ).isEqualTo( AccessType.PROPERTY );
		assertThat( sub.getAttributes() ).hasSize( 1 );
	}

	@Entity(name="Person")
	@Table(name="persons")
	public static class Person {
		@Id
		private Integer id;
		private String name;
	}

	@Entity(name="Root")
	@Table(name="roots")
	@Inheritance(strategy = InheritanceType.JOINED)
	public static class Root {
		@Id
		private Integer id;
		private String name;
	}

	@Entity(name="Sub")
	@Table(name="subs")
	@Access( AccessType.PROPERTY )
	public static class Sub extends Root {
		private String moreDetails;

		@Basic
		public String getMoreDetails() {
			return moreDetails;
		}

		public void setMoreDetails(String moreDetails) {
			this.moreDetails = moreDetails;
		}
	}
}
