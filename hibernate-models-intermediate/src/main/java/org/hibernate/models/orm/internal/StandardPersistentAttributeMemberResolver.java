/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.orm.internal;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.hibernate.models.orm.AccessTypePlacementException;
import org.hibernate.models.orm.spi.OrmModelBuildingContext;
import org.hibernate.models.source.internal.AnnotationUsageHelper;
import org.hibernate.models.source.spi.AnnotationUsage;
import org.hibernate.models.source.spi.ClassDetails;
import org.hibernate.models.source.spi.FieldDetails;
import org.hibernate.models.orm.spi.JpaAnnotations;
import org.hibernate.models.source.spi.MemberDetails;
import org.hibernate.models.source.spi.MethodDetails;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;

import static org.hibernate.models.source.spi.AnnotationAttributeDescriptor.VALUE;

/**
 * Standard implementation of the PersistentAttributeMemberResolver contract
 * based strictly on the JPA specification.
 *
 * @author Steve Ebersole
 */
public class StandardPersistentAttributeMemberResolver extends AbstractPersistentAttributeMemberResolver {
	/**
	 * Singleton access
	 */
	public static final StandardPersistentAttributeMemberResolver INSTANCE = new StandardPersistentAttributeMemberResolver();

	@Override
	protected List<MemberDetails> resolveAttributesMembers(
			Function<FieldDetails,Boolean> transientFieldChecker,
			Function<MethodDetails,Boolean> transientMethodChecker,
			ClassDetails classDetails,
			AccessType classLevelAccessType,
			OrmModelBuildingContext processingContext) {
		assert classLevelAccessType != null;

		final LinkedHashMap<String,MemberDetails> results = new LinkedHashMap<>();

		processAttributeLevelAccess(
				results::put,
				transientFieldChecker,
				transientMethodChecker,
				classDetails,
				processingContext
		);

		processClassLevelAccess(
				results::containsKey,
				results::put,
				transientFieldChecker,
				transientMethodChecker,
				classDetails,
				classLevelAccessType,
				processingContext
		);

		return new ArrayList<>( results.values() );
	}

	private <M extends MemberDetails> void processAttributeLevelAccess(
			BiConsumer<String,MemberDetails> memberConsumer,
			Function<FieldDetails,Boolean> transientFieldChecker,
			Function<MethodDetails,Boolean> transientMethodChecker,
			ClassDetails classDetails,
			OrmModelBuildingContext processingContext) {
		final List<FieldDetails> fields = classDetails.getFields();
		for ( int i = 0; i < fields.size(); i++ ) {
			final FieldDetails fieldDetails = fields.get( i );
			processAttributeLevelAccessMember( fieldDetails, memberConsumer, transientFieldChecker, classDetails, processingContext );
		}

		final List<MethodDetails> methods = classDetails.getMethods();
		for ( int i = 0; i < methods.size(); i++ ) {
			final MethodDetails methodDetails = methods.get( i );
			processAttributeLevelAccessMember( methodDetails, memberConsumer, transientMethodChecker, classDetails, processingContext );
		}
	}

	private <M extends MemberDetails> void processAttributeLevelAccessMember(
			M memberDetails,
			BiConsumer<String,MemberDetails> memberConsumer,
			Function<M,Boolean> transiencyChecker,
			ClassDetails classDetails,
			OrmModelBuildingContext processingContext) {
		final AnnotationUsage<Access> access = memberDetails.getAnnotation( JpaAnnotations.ACCESS );
		if ( access == null  ) {
			return;
		}

		final AccessType attributeAccessType = AnnotationUsageHelper.extractValue( access, VALUE );

		validateAttributeLevelAccess(
				memberDetails,
				attributeAccessType,
				classDetails,
				processingContext
		);

		if ( transiencyChecker.apply( memberDetails ) ) {
			// the field is @Transient
			return;
		}

		memberConsumer.accept( memberDetails.resolveAttributeName(), memberDetails );
	}

	private void validateAttributeLevelAccess(
			MemberDetails annotationTarget,
			AccessType attributeAccessType,
			ClassDetails classDetails,
			OrmModelBuildingContext processingContext) {
		// Apply the checks defined in section `2.3.2 Explicit Access Type` of the persistence specification

		// Mainly, it is never legal to:
		//		1. specify @Access(FIELD) on a getter
		//		2. specify @Access(PROPERTY) on a field

		if ( ( attributeAccessType == AccessType.FIELD && !annotationTarget.isField() )
				|| ( attributeAccessType == AccessType.PROPERTY && annotationTarget.isField() ) ) {
			throw new AccessTypePlacementException( classDetails, annotationTarget );
		}
	}

	private void processClassLevelAccess(
			Function<String,Boolean> alreadyProcessedChecker,
			BiConsumer<String, MemberDetails> memberConsumer,
			Function<FieldDetails,Boolean> transientFieldChecker,
			Function<MethodDetails,Boolean> transientMethodChecker,
			ClassDetails classDetails,
			AccessType classLevelAccessType,
			@SuppressWarnings("unused") OrmModelBuildingContext processingContext) {
		if ( classLevelAccessType == AccessType.FIELD ) {
			final List<FieldDetails> fields = classDetails.getFields();
			for ( int i = 0; i < fields.size(); i++ ) {
				final FieldDetails fieldDetails = fields.get( i );
				if ( !fieldDetails.isPersistable() ) {
					// the field cannot be a persistent attribute
					continue;
				}

				final String attributeName = fieldDetails.resolveAttributeName();
				if ( alreadyProcessedChecker.apply( attributeName ) ) {
					continue;
				}

				if ( transientFieldChecker.apply( fieldDetails ) ) {
					// the field is @Transient
					continue;
				}

				memberConsumer.accept( attributeName, fieldDetails );
			}
		}
		else {
			assert classLevelAccessType == AccessType.PROPERTY;
			final List<MethodDetails> methods = classDetails.getMethods();
			for ( int i = 0; i < methods.size(); i++ ) {
				final MethodDetails methodDetails = methods.get( i );
				if ( !methodDetails.isPersistable() ) {
					continue;
				}

				final String attributeName = methodDetails.resolveAttributeName();
				if ( alreadyProcessedChecker.apply( attributeName ) ) {
					continue;
				}

				if ( transientMethodChecker.apply( methodDetails ) ) {
					// the method is @Transient
					continue;
				}

				memberConsumer.accept( attributeName, methodDetails );
			}
		}
	}
}
