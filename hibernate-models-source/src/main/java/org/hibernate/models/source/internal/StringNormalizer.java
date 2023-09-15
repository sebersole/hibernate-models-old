/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.source.internal;


import org.hibernate.models.internal.StringHelper;
import org.hibernate.models.source.spi.AnnotationTarget;
import org.hibernate.models.source.spi.SourceModelBuildingContext;

/**
 * ValueNormalizer for {@link String} values
 *
 * @author Steve Ebersole
 */
public class StringNormalizer implements ValueNormalizer<String, String> {
	/**
	 * Singleton access
	 */
	public static final StringNormalizer INSTANCE = new StringNormalizer();

	@Override
	public String normalize(String incomingValue, AnnotationTarget target, SourceModelBuildingContext processingContext) {
		return StringHelper.nullIfEmpty( incomingValue );
	}
}
