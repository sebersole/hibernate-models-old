/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.source.internal.standard.annotations;

import org.jboss.jandex.AnnotationValue;

import static org.hibernate.models.source.internal.standard.annotations.CharacterValueExtractor.CHARACTER_EXTRACTOR;

/**
 * Descriptor for Character-valued annotation attributes
 *
 * @see AnnotationValue.Kind#CHARACTER
 *
 * @author Steve Ebersole
 */
public class CharacterValueDescriptor extends AbstractCommonValueDescriptor<Character> {
	public CharacterValueDescriptor(String name) {
		super( name );
	}

	@Override
	protected ValueExtractor<Character> getValueExtractor() {
		return CHARACTER_EXTRACTOR;
	}
}
