package org.hibernate.models.orm.internal;

import org.hibernate.annotations.common.reflection.ReflectionManager;
import org.hibernate.models.source.internal.SourceModelBuildingContextImpl;
import org.hibernate.models.source.spi.SourceModelBuildingContext;
import org.hibernate.models.spi.ClassLoading;

import org.jboss.jandex.IndexView;

import static org.hibernate.models.orm.internal.AnnotationHelper.forEachOrmAnnotation;

/**
 * @author Steve Ebersole
 */
public class SourceModelHelper {
	public static SourceModelBuildingContext buildSourceModelBuildingContext(
			ClassLoading classLoadingAccess,
			IndexView jandexIndex,
			ReflectionManager reflectionManager) {
		return new SourceModelBuildingContextImpl(
				classLoadingAccess,
				jandexIndex,
				reflectionManager,
				(contributions, buildingContext) -> forEachOrmAnnotation( contributions::registerAnnotation )
		);
	}
}
