package org.hibernate.models.orm.spi;

import org.hibernate.annotations.common.reflection.ReflectionManager;
import org.hibernate.models.orm.internal.OrmModelBuildingContextImpl;
import org.hibernate.models.orm.internal.SourceModelImpl;
import org.hibernate.models.source.internal.SourceModelBuildingContextImpl;
import org.hibernate.models.spi.ClassLoading;

import org.jboss.jandex.IndexView;

import static org.hibernate.models.orm.internal.AnnotationHelper.forEachOrmAnnotation;

/**
 * @author Steve Ebersole
 */
public class Processor {
	public static void process(
			ClassLoading classLoadingAccess,
			IndexView jandexIndex,
			ReflectionManager reflectionManager) {
		final SourceModelBuildingContextImpl sourceModelBuildingContext = new SourceModelBuildingContextImpl(
				classLoadingAccess,
				jandexIndex,
				reflectionManager,
				(contributions, buildingContext) -> forEachOrmAnnotation( contributions::registerAnnotation )
		);

		final OrmModelBuildingContextImpl intermediateModelBuildingContext = new OrmModelBuildingContextImpl(
				new SourceModelImpl(
						sourceModelBuildingContext.getAnnotationDescriptorRegistry(),
						sourceModelBuildingContext.getClassDetailsRegistry()
				)
		);

	}
}
