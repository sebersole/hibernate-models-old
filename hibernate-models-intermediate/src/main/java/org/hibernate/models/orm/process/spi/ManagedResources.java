/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.orm.process.spi;

import java.util.List;

import org.hibernate.models.Copied;

/**
 * @author Steve Ebersole
 */
@Copied(org.hibernate.boot.model.process.spi.ManagedResources.class)
public interface ManagedResources {
	List<String> getPackageNames();
	List<String> getClassNames();
	List<Class<?>> getLoadedClasses();
	List<String> getXmlMappings();
}
