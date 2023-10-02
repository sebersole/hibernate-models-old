/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.orm.internal;

import org.hibernate.models.orm.spi.OrmModelBuildingContext;
import org.hibernate.models.orm.spi.SourceModel;
import org.hibernate.models.spi.ClassLoading;

import org.jboss.jandex.IndexView;

/**
 * @author Steve Ebersole
 */
public class OrmModelBuildingContextImpl implements OrmModelBuildingContext {
	private final SourceModel sourceModel;
	private final ClassLoading classLoading;
	private final IndexView jandexIndex;

	public OrmModelBuildingContextImpl(SourceModel sourceModel, ClassLoading classLoading, IndexView jandexIndex) {
		this.sourceModel = sourceModel;
		this.classLoading = classLoading;
		this.jandexIndex = jandexIndex;
	}

	@Override
	public SourceModel getSourceModel() {
		return sourceModel;
	}

	@Override
	public ClassLoading getClassLoading() {
		return classLoading;
	}

	@Override
	public IndexView getJandexIndex() {
		return jandexIndex;
	}
}
