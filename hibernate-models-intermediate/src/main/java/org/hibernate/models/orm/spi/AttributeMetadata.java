/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.orm.spi;


import org.hibernate.models.source.spi.MemberDetails;

/**
 * @author Steve Ebersole
 */
public interface AttributeMetadata {
	String getName();

	AttributeNature getNature();

	MemberDetails getMember();

	/**
	 * An enum defining the nature (categorization) of a persistent attribute.
	 */
	enum AttributeNature {
		BASIC,
		EMBEDDED,
		ANY,
		TO_ONE,
		PLURAL
	}
}
