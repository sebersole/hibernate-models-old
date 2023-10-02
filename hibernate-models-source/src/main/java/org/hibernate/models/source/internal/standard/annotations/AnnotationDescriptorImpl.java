/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.source.internal.standard.annotations;

import java.lang.annotation.Annotation;
import java.util.EnumSet;
import java.util.List;

import org.hibernate.models.source.internal.AnnotationHelper;
import org.hibernate.models.source.internal.standard.AbstractAnnotationTarget;
import org.hibernate.models.source.internal.standard.ClassDetailsBuilderImpl;
import org.hibernate.models.source.spi.AnnotationAttributeDescriptor;
import org.hibernate.models.source.spi.AnnotationDescriptor;
import org.hibernate.models.source.spi.SourceModelBuildingContext;

import org.jboss.jandex.AnnotationTarget;
import org.jboss.jandex.ClassInfo;

import static org.hibernate.models.source.internal.standard.annotations.AttributeDescriptorBuilder.extractAttributeDescriptors;

/**
 * @author Steve Ebersole
 */
public class AnnotationDescriptorImpl<A extends Annotation>
		extends AbstractAnnotationTarget
		implements AnnotationDescriptor<A> {
	private final Class<A> annotationType;
	private final ClassInfo annotationClassInfo;
	private final boolean inherited;
	private final EnumSet<Kind> allowableTargets;
	private final AnnotationDescriptor<? extends Annotation> repeatableContainer;
	private final SourceModelBuildingContext buildingContext;

	private List<AnnotationAttributeDescriptor> attributeDescriptors;

	public AnnotationDescriptorImpl(
			Class<A> annotationType,
			AnnotationDescriptor<?> repeatableContainer,
			SourceModelBuildingContext buildingContext) {
		this(
				resolveClassInfo( annotationType, buildingContext ),
				annotationType,
				repeatableContainer,
				buildingContext
		);
	}

	private static <A extends Annotation> ClassInfo resolveClassInfo(
			Class<A> annotationType,
			SourceModelBuildingContext buildingContext) {
		return buildingContext.getJandexView().getClassByName( annotationType );
	}

	public AnnotationDescriptorImpl(
			ClassInfo annotationClassInfo,
			Class<A> annotationType,
			AnnotationDescriptor<?> repeatableContainer,
			SourceModelBuildingContext buildingContext) {
		super( buildingContext );

		this.annotationClassInfo = annotationClassInfo;
		this.annotationType = annotationType;
		this.repeatableContainer = repeatableContainer;
		this.inherited = AnnotationHelper.isInherited( annotationType );
		this.allowableTargets = AnnotationHelper.extractTargets( annotationType );
		this.buildingContext = buildingContext;

		// Register ClassDetails for the annotation
		buildingContext.getClassDetailsRegistry().resolveClassDetails(
				annotationType.getName(),
				ClassDetailsBuilderImpl::buildClassDetailsStatic
		);
	}

	@Override
	protected AnnotationTarget getJandexAnnotationTarget() {
		return annotationClassInfo;
	}

	@Override
	public String getName() {
		return annotationType.getName();
	}

	@Override
	public Class<A> getAnnotationType() {
		return annotationType;
	}

	@Override
	public EnumSet<Kind> getAllowableTargets() {
		return allowableTargets;
	}

	@Override
	public boolean isInherited() {
		return inherited;
	}

	@Override
	public List<AnnotationAttributeDescriptor> getAttributes() {
		return resolveAttributes();
	}

	private List<AnnotationAttributeDescriptor> resolveAttributes() {
		if ( attributeDescriptors == null ) {
			attributeDescriptors = extractAttributeDescriptors( annotationType, buildingContext );
		}
		return attributeDescriptors;
	}


	@Override
	public AnnotationAttributeDescriptor getAttribute(String name) {
		for ( AnnotationAttributeDescriptor attributeDescriptor : resolveAttributes() ) {
			if ( attributeDescriptor.getAttributeName().equals( name ) ) {
				return attributeDescriptor;
			}
		}
		return null;
	}

	@Override
	public AnnotationDescriptor<?> getRepeatableContainer() {
		return repeatableContainer;
	}
}
