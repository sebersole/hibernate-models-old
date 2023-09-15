package org.hibernate.models.source.spi;

import java.lang.annotation.Annotation;

/**
 * @author Steve Ebersole
 */
public interface RegistryPrimer {
	void primeRegistries(Contributions contributions, SourceModelBuildingContext buildingContext);

	interface Contributions {
		<A extends Annotation> void registerAnnotation(AnnotationDescriptor<A> descriptor);

		void registerClass(ClassDetails details);
	}
}
