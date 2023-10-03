/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.orm.spi;

import org.hibernate.models.spi.ClassLoading;

import org.jboss.jandex.IndexView;

/**
 * @author Steve Ebersole
 */
public interface OrmModelBuildingContext {
	SourceModel getSourceModel();

	ClassLoading getClassLoading();

	IndexView getJandexIndex();

	ClassmateContext getClassmateContext();
}
