/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.orm.process.internal;

import java.util.Map;

import org.hibernate.metamodel.CollectionClassification;
import org.hibernate.models.source.spi.ClassDetails;

/**
 * @author Steve Ebersole
 */
public class CollectionTypeRegistration {
	private final CollectionClassification classification;
	private final ClassDetails userTypeClass;
	private final Map<String,String> parameterMap;

	public CollectionTypeRegistration(
			CollectionClassification classification,
			ClassDetails userTypeClass,
			Map<String, String> parameterMap) {
		this.classification = classification;
		this.userTypeClass = userTypeClass;
		this.parameterMap = parameterMap;
	}

	public CollectionClassification getClassification() {
		return classification;
	}

	public ClassDetails getUserTypeClass() {
		return userTypeClass;
	}

	public Map<String, String> getParameterMap() {
		return parameterMap;
	}
}
