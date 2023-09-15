/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.orm.spi;

import org.hibernate.models.source.spi.AnnotationDescriptorRegistry;
import org.hibernate.models.source.spi.ClassDetailsRegistry;

/**
 * @author Steve Ebersole
 */
public interface SourceModel {
	AnnotationDescriptorRegistry getAnnotationDescriptorRegistry();
	ClassDetailsRegistry getClassDetailsRegistry();
}
