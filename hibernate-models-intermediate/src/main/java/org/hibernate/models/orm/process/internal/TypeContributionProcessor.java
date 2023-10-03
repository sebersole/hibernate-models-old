/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.orm.process.internal;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.annotations.Parameter;
import org.hibernate.boot.jaxb.mapping.JaxbCollectionUserTypeRegistration;
import org.hibernate.boot.jaxb.mapping.JaxbCompositeUserTypeRegistration;
import org.hibernate.boot.jaxb.mapping.JaxbConfigurationParameter;
import org.hibernate.boot.jaxb.mapping.JaxbConverterRegistration;
import org.hibernate.boot.jaxb.mapping.JaxbEmbeddableInstantiatorRegistration;
import org.hibernate.boot.jaxb.mapping.JaxbEntityMappings;
import org.hibernate.boot.jaxb.mapping.JaxbJavaTypeRegistration;
import org.hibernate.boot.jaxb.mapping.JaxbJdbcTypeRegistration;
import org.hibernate.boot.jaxb.mapping.JaxbUserTypeRegistration;
import org.hibernate.internal.util.collections.CollectionHelper;
import org.hibernate.models.internal.StringHelper;
import org.hibernate.models.orm.spi.ClassmateContext;
import org.hibernate.models.orm.spi.OrmModelBuildingContext;
import org.hibernate.models.source.spi.AnnotationAttributeValue;
import org.hibernate.models.source.spi.AnnotationTarget;
import org.hibernate.models.source.spi.AnnotationUsage;
import org.hibernate.models.source.spi.ClassDetails;
import org.hibernate.models.source.spi.ClassDetailsRegistry;

import static org.hibernate.models.orm.spi.HibernateAnnotations.COLLECTION_TYPE_REG;
import static org.hibernate.models.orm.spi.HibernateAnnotations.COMPOSITE_TYPE_REG;
import static org.hibernate.models.orm.spi.HibernateAnnotations.CONVERTER_REG;
import static org.hibernate.models.orm.spi.HibernateAnnotations.EMBEDDABLE_INSTANTIATOR_REG;
import static org.hibernate.models.orm.spi.HibernateAnnotations.JAVA_TYPE_REG;
import static org.hibernate.models.orm.spi.HibernateAnnotations.JDBC_TYPE_REG;
import static org.hibernate.models.orm.spi.HibernateAnnotations.TYPE_REG;
import static org.hibernate.models.source.spi.AnnotationAttributeDescriptor.VALUE;

/**
 * Processes global type-contribution metadata
 *
 * @see org.hibernate.annotations.JavaTypeRegistration
 * @see org.hibernate.annotations.JdbcTypeRegistration
 * @see org.hibernate.annotations.ConverterRegistration
 * @see org.hibernate.annotations.EmbeddableInstantiatorRegistration
 * @see org.hibernate.annotations.TypeRegistration
 * @see org.hibernate.annotations.CompositeTypeRegistration
 * @see org.hibernate.annotations.CollectionTypeRegistration
 *
 * @author Steve Ebersole
 */
public class TypeContributionProcessor {
	public static void processTypeContributions(
			AnnotationTarget annotationTarget,
			ProcessResultCollector resultCollector,
			ClassDetailsRegistry classDetailsRegistry) {
		final TypeContributionProcessor processor = new TypeContributionProcessor( resultCollector, classDetailsRegistry );
		processor.processJavaTypeRegistrations( annotationTarget );
		processor.processJdbcTypeRegistrations( annotationTarget );
		processor.processAttributeConverterRegistrations( annotationTarget );
		processor.processUserTypeRegistrations( annotationTarget );
		processor.processCompositeUserTypeRegistrations( annotationTarget );
		processor.processCollectionTypeRegistrations( annotationTarget );
		processor.processEmbeddableInstantiatorRegistrations( annotationTarget );
	}

	public static void processTypeContributions(
			JaxbEntityMappings root,
			ProcessResultCollector resultCollector,
			ClassDetailsRegistry classDetailsRegistry) {
		final TypeContributionProcessor processor = new TypeContributionProcessor( resultCollector, classDetailsRegistry );
		processor.processJavaTypeRegistrations( root.getJavaTypeRegistrations() );
		processor.processJdbcTypeRegistrations( root.getJdbcTypeRegistrations() );
		processor.processAttributeConverterRegistrations( root.getConverterRegistrations() );
		processor.processUserTypeRegistrations( root.getUserTypeRegistrations() );
		processor.processCompositeUserTypeRegistrations( root.getCompositeUserTypeRegistrations() );
		processor.processCollectionTypeRegistrations( root.getCollectionUserTypeRegistrations() );
		processor.processEmbeddableInstantiatorRegistrations( root.getEmbeddableInstantiatorRegistrations() );
	}

	private final ProcessResultCollector resultCollector;

	private final ClassDetailsRegistry classDetailsRegistry;

	public TypeContributionProcessor(ProcessResultCollector resultCollector, ClassDetailsRegistry classDetailsRegistry) {
		this.resultCollector = resultCollector;
		this.classDetailsRegistry = classDetailsRegistry;
	}

	private void processJavaTypeRegistrations(AnnotationTarget annotationTarget) {
		annotationTarget.forEachAnnotation( JAVA_TYPE_REG, (usage) -> resultCollector.collectJavaTypeRegistration(
				usage.getAttributeValue( "javaType" ).asClass(),
				usage.getAttributeValue( "descriptorClass" ).asClass()
		) );
	}

	private void processJavaTypeRegistrations(List<JaxbJavaTypeRegistration> registrations) {
		if ( CollectionHelper.isEmpty( registrations ) ) {
			return;
		}

		registrations.forEach( (reg) -> resultCollector.collectJavaTypeRegistration(
				classDetailsRegistry.resolveClassDetails( reg.getClazz() ),
				classDetailsRegistry.resolveClassDetails( reg.getDescriptor() )
		) );
	}

	private void processJdbcTypeRegistrations(AnnotationTarget annotationTarget) {
		annotationTarget.forEachAnnotation( JDBC_TYPE_REG, (usage) -> {
			final AnnotationAttributeValue<Integer> registrationCodeValue = usage.getAttributeValue( "registrationCode" );
			final Integer registrationCode = registrationCodeValue.isImplicit()
					? null
					: registrationCodeValue.getValue();

			resultCollector.collectJdbcTypeRegistration( registrationCode, usage.getAttributeValue( VALUE ).asClass() );
		} );
	}

	private void processJdbcTypeRegistrations(List<JaxbJdbcTypeRegistration> registrations) {
		if ( CollectionHelper.isEmpty( registrations ) ) {
			return;
		}

		registrations.forEach( (reg) -> resultCollector.collectJdbcTypeRegistration(
				reg.getCode(),
				classDetailsRegistry.resolveClassDetails( reg.getDescriptor() )
		) );
	}

	private void processAttributeConverterRegistrations(AnnotationTarget annotationTarget) {
		annotationTarget.forEachAnnotation( CONVERTER_REG, (usage) -> {
			final ClassDetails domainType = usage.getAttributeValue( "domainType" ).asClass();
			final ClassDetails converterType = usage.getAttributeValue( "converter" ).asClass();
			final boolean autoApply = usage.extractAttributeValue( "autoApply", true );
			resultCollector.collectConverterRegistration( new ConversionRegistration( domainType, converterType, autoApply ) );
		} );
	}

	private void processAttributeConverterRegistrations(List<JaxbConverterRegistration> registrations) {
		if ( CollectionHelper.isEmpty( registrations ) ) {
			return;
		}

		registrations.forEach( (reg) -> {
			final ClassDetails converterDetails = classDetailsRegistry.resolveClassDetails( reg.getConverter() );

			final ClassDetails explicitDomainType;
			final String explicitDomainTypeName = reg.getClazz();
			if ( StringHelper.isNotEmpty( explicitDomainTypeName ) ) {
				explicitDomainType = classDetailsRegistry.resolveClassDetails( explicitDomainTypeName );
			}
			else {
				explicitDomainType = null;
			}

			final boolean autoApply = reg.isAutoApply();

			resultCollector.collectConverterRegistration( new ConversionRegistration(
					explicitDomainType,
					converterDetails,
					autoApply
			) );
		} );
	}

	private void processEmbeddableInstantiatorRegistrations(AnnotationTarget annotationTarget) {
		annotationTarget.forEachAnnotation( EMBEDDABLE_INSTANTIATOR_REG, (usage) -> resultCollector.collectEmbeddableInstantiatorRegistration(
				usage.extractAttributeValue( "embeddableClass" ),
				usage.extractAttributeValue( "instantiator" )
		) );
	}

	private void processEmbeddableInstantiatorRegistrations(List<JaxbEmbeddableInstantiatorRegistration> registrations) {
		if ( CollectionHelper.isEmpty( registrations ) ) {
			return;
		}

		registrations.forEach( (reg) -> resultCollector.collectEmbeddableInstantiatorRegistration(
				classDetailsRegistry.resolveClassDetails( reg.getEmbeddableClass() ),
				classDetailsRegistry.resolveClassDetails( reg.getInstantiator() )
		) );
	}

	private void processUserTypeRegistrations(AnnotationTarget annotationTarget) {
		annotationTarget.forEachAnnotation( TYPE_REG, (usage) -> resultCollector.collectUserTypeRegistration(
				usage.extractAttributeValue( "basicClass" ),
				usage.extractAttributeValue( "userType" )
		) );
	}

	private void processUserTypeRegistrations(List<JaxbUserTypeRegistration> registrations) {
		if ( CollectionHelper.isEmpty( registrations ) ) {
			return;
		}

		registrations.forEach( (reg) -> {
			final ClassDetails domainTypeDetails = classDetailsRegistry.resolveClassDetails( reg.getClazz() );
			final ClassDetails descriptorDetails = classDetailsRegistry.resolveClassDetails( reg.getDescriptor() );
			resultCollector.collectUserTypeRegistration( domainTypeDetails, descriptorDetails );
		} );
	}

	private void processCompositeUserTypeRegistrations(AnnotationTarget annotationTarget) {
		annotationTarget.forEachAnnotation( COMPOSITE_TYPE_REG, (usage) -> resultCollector.collectCompositeUserTypeRegistration(
				usage.extractAttributeValue( "embeddableClass" ),
				usage.extractAttributeValue( "userType" )
		) );
	}

	private void processCompositeUserTypeRegistrations(List<JaxbCompositeUserTypeRegistration> registrations) {
		if ( CollectionHelper.isEmpty( registrations ) ) {
			return;
		}

		registrations.forEach( (reg) -> resultCollector.collectCompositeUserTypeRegistration(
				classDetailsRegistry.resolveClassDetails( reg.getClazz() ),
				classDetailsRegistry.resolveClassDetails( reg.getDescriptor() )
		) );
	}

	private void processCollectionTypeRegistrations(AnnotationTarget annotationTarget) {
		annotationTarget.forEachAnnotation( COLLECTION_TYPE_REG, (usage) -> resultCollector.collectCollectionTypeRegistration(
				usage.extractAttributeValue( "classification" ),
				usage.extractAttributeValue( "type" ),
				extractParameterMap( usage.getAttributeValue( "parameters" ) )
		) );
	}

	private Map<String,String> extractParameterMap(AnnotationAttributeValue<List<AnnotationUsage<Parameter>>> value) {
		final Map<String,String> result = new HashMap<>();
		final List<AnnotationUsage<Parameter>> parameters = value.getValue();
		for ( AnnotationUsage<Parameter> parameter : parameters ) {
			result.put(
					parameter.extractAttributeValue( "name" ),
					parameter.extractAttributeValue( VALUE )
			);
		}
		return result;
	}

	private void processCollectionTypeRegistrations(List<JaxbCollectionUserTypeRegistration> registrations) {
		if ( CollectionHelper.isEmpty( registrations ) ) {
			return;
		}

		registrations.forEach( (reg) -> resultCollector.collectCollectionTypeRegistration(
				reg.getClassification(),
				classDetailsRegistry.resolveClassDetails( reg.getDescriptor() ),
				extractParameterMap( reg.getParameters() )
		) );
	}

	private Map<String, String> extractParameterMap(List<JaxbConfigurationParameter> parameters) {
		if ( CollectionHelper.isEmpty( parameters ) ) {
			return Collections.emptyMap();
		}

		final Map<String,String> result = new HashMap<>();
		parameters.forEach( parameter -> result.put( parameter.getName(), parameter.getValue() ) );
		return result;
	}
}
