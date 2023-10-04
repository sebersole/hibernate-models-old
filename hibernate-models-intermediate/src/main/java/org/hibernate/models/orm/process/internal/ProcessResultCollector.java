/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.orm.process.internal;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.metamodel.CollectionClassification;
import org.hibernate.models.orm.process.spi.ProcessResult;
import org.hibernate.models.orm.spi.EntityHierarchy;
import org.hibernate.models.source.spi.AnnotationUsage;
import org.hibernate.models.source.spi.ClassDetails;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;

/**
 * @author Steve Ebersole
 */
public class ProcessResultCollector {
	private List<JavaTypeRegistration> javaTypeRegistrations;
	private List<JdbcTypeRegistration> jdbcTypeRegistrations;
	private List<ConversionRegistration> converterRegistrations;
	private List<UserTypeRegistration> userTypeRegistrations;
	private List<CompositeUserTypeRegistration> compositeUserTypeRegistrations;
	private List<CollectionTypeRegistration> collectionTypeRegistrations;
	private List<EmbeddableInstantiatorRegistration> embeddableInstantiatorRegistrations;
	private Map<String, IdGeneratorRegistration> globalIdGeneratorRegistrations;
	private List<ClassDetails> autoAppliedConverters;

	public List<JavaTypeRegistration> getJavaTypeRegistrations() {
		return javaTypeRegistrations;
	}

	public List<JdbcTypeRegistration> getJdbcTypeRegistrations() {
		return jdbcTypeRegistrations;
	}

	public List<ConversionRegistration> getConverterRegistrations() {
		return converterRegistrations;
	}

	public List<UserTypeRegistration> getUserTypeRegistrations() {
		return userTypeRegistrations;
	}

	public List<CompositeUserTypeRegistration> getCompositeUserTypeRegistrations() {
		return compositeUserTypeRegistrations;
	}

	public List<CollectionTypeRegistration> getCollectionTypeRegistrations() {
		return collectionTypeRegistrations;
	}

	public List<EmbeddableInstantiatorRegistration> getEmbeddableInstantiatorRegistrations() {
		return embeddableInstantiatorRegistrations;
	}

	public  void collectJavaTypeRegistration(ClassDetails javaType, ClassDetails descriptor) {
		if ( javaTypeRegistrations == null ) {
			javaTypeRegistrations = new ArrayList<>();
		}
		javaTypeRegistrations.add( new JavaTypeRegistration( javaType, descriptor ) );
	}

	public void collectJdbcTypeRegistration(Integer registrationCode, ClassDetails descriptor) {
		if ( jdbcTypeRegistrations == null ) {
			jdbcTypeRegistrations = new ArrayList<>();
		}
		jdbcTypeRegistrations.add( new JdbcTypeRegistration( registrationCode, descriptor ) );
	}

	public void collectConverterRegistration(ConversionRegistration conversion) {
		if ( converterRegistrations == null ) {
			converterRegistrations = new ArrayList<>();
		}
		converterRegistrations.add( conversion );
	}

	public void collectAutoAppliedConverter(ClassDetails converterClass) {
		if ( autoAppliedConverters == null ) {
			autoAppliedConverters = new ArrayList<>();
		}
		autoAppliedConverters.add( converterClass );
	}

	public void collectUserTypeRegistration(ClassDetails domainClass, ClassDetails userTypeClass) {
		if ( userTypeRegistrations == null ) {
			userTypeRegistrations = new ArrayList<>();
		}
		userTypeRegistrations.add( new UserTypeRegistration( domainClass, userTypeClass ) );
	}

	public void collectCompositeUserTypeRegistration(ClassDetails domainClass, ClassDetails userTypeClass) {
		if ( compositeUserTypeRegistrations == null ) {
			compositeUserTypeRegistrations = new ArrayList<>();
		}
		compositeUserTypeRegistrations.add( new CompositeUserTypeRegistration( domainClass, userTypeClass ) );
	}

	public void collectCollectionTypeRegistration(
			CollectionClassification classification,
			ClassDetails userTypeClass,
			Map<String,String> parameters) {
		if ( collectionTypeRegistrations == null ) {
			collectionTypeRegistrations = new ArrayList<>();
		}
		collectionTypeRegistrations.add( new CollectionTypeRegistration( classification, userTypeClass, parameters ) );
	}

	public void collectEmbeddableInstantiatorRegistration(ClassDetails embeddableClass, ClassDetails instantiator) {
		if ( embeddableInstantiatorRegistrations == null ) {
			embeddableInstantiatorRegistrations = new ArrayList<>();
		}
		embeddableInstantiatorRegistrations.add( new EmbeddableInstantiatorRegistration( embeddableClass, instantiator ) );
	}

	public void collectGlobalIdGeneratorRegistration(
			String name,
			IdGeneratorRegistration.Kind kind,
			AnnotationUsage<? extends Annotation> annotation) {
		if ( globalIdGeneratorRegistrations == null ) {
			globalIdGeneratorRegistrations = new HashMap<>();
		}

		globalIdGeneratorRegistrations.put( name, new IdGeneratorRegistration( name, kind, annotation ) );
	}

	public ProcessResult createResult(Set<EntityHierarchy> entityHierarchies) {
		return new ProcessResultImpl(
				entityHierarchies,
				javaTypeRegistrations == null ? emptyList() : javaTypeRegistrations,
				jdbcTypeRegistrations == null ? emptyList() : jdbcTypeRegistrations,
				converterRegistrations == null ? emptyList() : converterRegistrations,
				autoAppliedConverters == null ? emptyList() : autoAppliedConverters,
				userTypeRegistrations == null ? emptyList() : userTypeRegistrations,
				compositeUserTypeRegistrations == null ? emptyList() : compositeUserTypeRegistrations,
				collectionTypeRegistrations == null ? emptyList() : collectionTypeRegistrations,
				embeddableInstantiatorRegistrations == null ? emptyList() : embeddableInstantiatorRegistrations,
				globalIdGeneratorRegistrations == null ? emptyMap() : globalIdGeneratorRegistrations
		);
	}
}
