/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.orm.process.internal;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.models.orm.spi.EntityHierarchy;
import org.hibernate.models.orm.process.spi.ProcessResult;

/**
 * @author Steve Ebersole
 */
public class ProcessResultImpl implements ProcessResult {
	private final Set<EntityHierarchy> entityHierarchies;

	private final List<JavaTypeRegistration> javaTypeRegistrations;
	private final List<JdbcTypeRegistration> jdbcTypeRegistrations;
	private final List<ConversionRegistration> converterRegistrations;
	private final List<UserTypeRegistration> userTypeRegistrations;
	private final List<CompositeUserTypeRegistration> compositeUserTypeRegistrations;
	private final List<CollectionTypeRegistration> collectionTypeRegistrations;
	private final List<EmbeddableInstantiatorRegistration> embeddableInstantiatorRegistrations;
	private final Map<String, IdGeneratorRegistration> globalIdGeneratorRegistrations;

	public ProcessResultImpl(
			Set<EntityHierarchy> entityHierarchies,
			List<JavaTypeRegistration> javaTypeRegistrations,
			List<JdbcTypeRegistration> jdbcTypeRegistrations,
			List<ConversionRegistration> converterRegistrations,
			List<UserTypeRegistration> userTypeRegistrations,
			List<CompositeUserTypeRegistration> compositeUserTypeRegistrations,
			List<CollectionTypeRegistration> collectionTypeRegistrations,
			List<EmbeddableInstantiatorRegistration> embeddableInstantiatorRegistrations,
			Map<String, IdGeneratorRegistration> globalIdGeneratorRegistrations) {
		this.entityHierarchies = entityHierarchies;
		this.javaTypeRegistrations = javaTypeRegistrations;
		this.jdbcTypeRegistrations = jdbcTypeRegistrations;
		this.converterRegistrations = converterRegistrations;
		this.userTypeRegistrations = userTypeRegistrations;
		this.compositeUserTypeRegistrations = compositeUserTypeRegistrations;
		this.collectionTypeRegistrations = collectionTypeRegistrations;
		this.embeddableInstantiatorRegistrations = embeddableInstantiatorRegistrations;
		this.globalIdGeneratorRegistrations = globalIdGeneratorRegistrations;
	}

	@Override
	public Set<EntityHierarchy> getEntityHierarchies() {
		return entityHierarchies;
	}

	@Override
	public List<JavaTypeRegistration> getJavaTypeRegistrations() {
		return javaTypeRegistrations;
	}

	@Override
	public List<JdbcTypeRegistration> getJdbcTypeRegistrations() {
		return jdbcTypeRegistrations;
	}

	@Override
	public List<ConversionRegistration> getConverterRegistrations() {
		return converterRegistrations;
	}

	@Override
	public List<UserTypeRegistration> getUserTypeRegistrations() {
		return userTypeRegistrations;
	}

	@Override
	public List<CompositeUserTypeRegistration> getCompositeUserTypeRegistrations() {
		return compositeUserTypeRegistrations;
	}

	@Override
	public List<CollectionTypeRegistration> getCollectionTypeRegistrations() {
		return collectionTypeRegistrations;
	}

	@Override
	public List<EmbeddableInstantiatorRegistration> getEmbeddableInstantiatorRegistrations() {
		return embeddableInstantiatorRegistrations;
	}

	@Override
	public Map<String, IdGeneratorRegistration> getGlobalIdGeneratorRegistrations() {
		return globalIdGeneratorRegistrations;
	}
}
