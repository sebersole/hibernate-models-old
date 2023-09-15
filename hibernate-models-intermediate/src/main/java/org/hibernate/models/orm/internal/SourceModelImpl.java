package org.hibernate.models.orm.internal;

import org.hibernate.models.orm.spi.SourceModel;
import org.hibernate.models.source.spi.AnnotationDescriptorRegistry;
import org.hibernate.models.source.spi.ClassDetailsRegistry;

/**
 * @author Steve Ebersole
 */
public class SourceModelImpl implements SourceModel {
	private final AnnotationDescriptorRegistry annotationDescriptorRegistry;
	private final ClassDetailsRegistry classDetailsRegistry;

	public SourceModelImpl(
			AnnotationDescriptorRegistry annotationDescriptorRegistry,
			ClassDetailsRegistry classDetailsRegistry) {
		this.annotationDescriptorRegistry = annotationDescriptorRegistry;
		this.classDetailsRegistry = classDetailsRegistry;
	}

	@Override
	public AnnotationDescriptorRegistry getAnnotationDescriptorRegistry() {
		return annotationDescriptorRegistry;
	}

	@Override
	public ClassDetailsRegistry getClassDetailsRegistry() {
		return classDetailsRegistry;
	}
}
