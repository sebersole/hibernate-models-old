/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.orm.attribute;

import java.util.List;

import jakarta.persistence.AttributeConverter;

/**
 * @author Steve Ebersole
 */
public class ListConverter implements AttributeConverter<List<String>,String> {
	@Override
	public String convertToDatabaseColumn(List<String> attribute) {
		return null;
	}

	@Override
	public List<String> convertToEntityAttribute(String dbData) {
		return null;
	}
}
