/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.orm.spi;

import org.hibernate.annotations.NaturalIdCache;
import org.hibernate.models.source.internal.AnnotationUsageHelper;
import org.hibernate.models.source.spi.AnnotationUsage;

/**
 * Details about caching related to the natural-id of an entity
 *
 * @see Caching
 *
 * @author Steve Ebersole
 */
public class NaturalIdCaching {
	private boolean enabled;
	private String region;

	public NaturalIdCaching(boolean enabled) {
		this.enabled = enabled;
	}

	public NaturalIdCaching(AnnotationUsage<NaturalIdCache> cacheAnnotation, Caching caching) {
		this.enabled = cacheAnnotation != null;

		if ( enabled ) {
			this.region = AnnotationUsageHelper.extractValue(
					cacheAnnotation,
					"region",
					caching.getRegion() + "##NaturalId"
			);
		}
		else {
			// use the default value
			this.region = caching.getRegion() + "##NaturalId";
		}
	}

	public boolean isEnabled() {
		return enabled;
	}

	public String getRegion() {
		return region;
	}
}
