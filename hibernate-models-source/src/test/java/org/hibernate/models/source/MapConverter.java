/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.source;

import java.util.Map;

import jakarta.persistence.AttributeConverter;

/**
 * @author Steve Ebersole
 */
public class MapConverter implements AttributeConverter<String, Map<String, String>> {
	@Override
	public Map<String, String> convertToDatabaseColumn(String attribute) {
		return null;
	}

	@Override
	public String convertToEntityAttribute(Map<String, String> dbData) {
		return null;
	}
}
