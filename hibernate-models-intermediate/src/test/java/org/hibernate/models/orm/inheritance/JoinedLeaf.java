/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.orm.inheritance;

import jakarta.persistence.Basic;
import jakarta.persistence.Entity;

/**
 * @author Steve Ebersole
 */
@Entity
public class JoinedLeaf extends JoinedRoot {
	@Basic
	private String summary;

	protected JoinedLeaf() {
		// for Hibernate use
	}

	public JoinedLeaf(Integer id, String name, String summary) {
		super( id, name );
		this.summary = summary;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}
}
