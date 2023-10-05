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
import org.hibernate.models.source.spi.ClassDetails;

/**
 * @author Steve Ebersole
 */
public class ProcessResultImpl implements ProcessResult {
	private final Set<EntityHierarchy> entityHierarchies;

	private final List<JavaTypeRegistration> javaTypeRegistrations;
	private final List<JdbcTypeRegistration> jdbcTypeRegistrations;
	private final List<ConversionRegistration> converterRegistrations;
	private final List<ClassDetails> autoAppliedConverters;
	private final List<UserTypeRegistration> userTypeRegistrations;
	private final List<CompositeUserTypeRegistration> compositeUserTypeRegistrations;
	private final List<CollectionTypeRegistration> collectionTypeRegistrations;
	private final List<EmbeddableInstantiatorRegistration> embeddableInstantiatorRegistrations;
	private final Map<String, IdGeneratorRegistration> globalIdGeneratorRegistrations;
	private final Map<String, NamedQuery> jpaNamedQueries;
	private final Map<String, NamedQuery> hibernateNamedHqlQueries;
	private final Map<String, NamedQuery> hibernateNamedNativeQueries;

	public ProcessResultImpl(
			Set<EntityHierarchy> entityHierarchies,
			List<JavaTypeRegistration> javaTypeRegistrations,
			List<JdbcTypeRegistration> jdbcTypeRegistrations,
			List<ConversionRegistration> converterRegistrations,
			List<ClassDetails> autoAppliedConverters,
			List<UserTypeRegistration> userTypeRegistrations,
			List<CompositeUserTypeRegistration> compositeUserTypeRegistrations,
			List<CollectionTypeRegistration> collectionTypeRegistrations,
			List<EmbeddableInstantiatorRegistration> embeddableInstantiatorRegistrations,
			Map<String, IdGeneratorRegistration> globalIdGeneratorRegistrations,
			Map<String, NamedQuery> jpaNamedQueries,
			Map<String, NamedQuery> hibernateNamedHqlQueries,
			Map<String, NamedQuery> hibernateNamedNativeQueries) {
		this.entityHierarchies = entityHierarchies;
		this.javaTypeRegistrations = javaTypeRegistrations;
		this.jdbcTypeRegistrations = jdbcTypeRegistrations;
		this.converterRegistrations = converterRegistrations;
		this.autoAppliedConverters = autoAppliedConverters;
		this.userTypeRegistrations = userTypeRegistrations;
		this.compositeUserTypeRegistrations = compositeUserTypeRegistrations;
		this.collectionTypeRegistrations = collectionTypeRegistrations;
		this.embeddableInstantiatorRegistrations = embeddableInstantiatorRegistrations;
		this.globalIdGeneratorRegistrations = globalIdGeneratorRegistrations;
		this.jpaNamedQueries = jpaNamedQueries;
		this.hibernateNamedHqlQueries = hibernateNamedHqlQueries;
		this.hibernateNamedNativeQueries = hibernateNamedNativeQueries;
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
	public List<ClassDetails> getAutoAppliedConverters() {
		return autoAppliedConverters;
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

	@Override
	public Map<String, NamedQuery> getJpaNamedQueries() {
		return jpaNamedQueries;
	}

	@Override
	public Map<String, NamedQuery> getHibernateNamedHqlQueries() {
		return hibernateNamedHqlQueries;
	}

	@Override
	public Map<String, NamedQuery> getHibernateNamedNativeQueries() {
		return hibernateNamedNativeQueries;
	}
}
