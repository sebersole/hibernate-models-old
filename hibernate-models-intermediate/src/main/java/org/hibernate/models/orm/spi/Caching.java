/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.orm.spi;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.boot.CacheRegionDefinition;
import org.hibernate.cache.spi.access.AccessType;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.models.source.internal.AnnotationUsageHelper;
import org.hibernate.models.source.spi.AnnotationAttributeValue;
import org.hibernate.models.source.spi.AnnotationUsage;

/**
 * Models the caching options for an entity, natural-id, or collection.
 *
 * @author Steve Ebersole
 * @author Hardy Ferentschik
 */
public class Caching {
	private boolean enabled;

	private String region;
	private AccessType accessType;
	private boolean cacheLazyProperties;

	/**
	 * Generally used for disabled caching
	 */
	public Caching(boolean enabled) {
		this.enabled = enabled;
	}

	public Caching(
			AnnotationUsage<Cache> cacheAnnotation,
			AccessType implicitCacheAccessType,
			String implicitRegionName) {
		this.enabled = true;

		if ( cacheAnnotation == null ) {
			region = implicitRegionName;
			accessType = implicitCacheAccessType;
			cacheLazyProperties = true;
		}
		else {
			region = AnnotationUsageHelper.extractValue( cacheAnnotation, "region", implicitRegionName );
			accessType = interpretAccessType( cacheAnnotation.getAttributeValue( "usage" ), implicitCacheAccessType );
			cacheLazyProperties = AnnotationUsageHelper.extractValue(
					cacheAnnotation,
					"includeLazy",
					() -> {
						final String include = cacheAnnotation.getAttributeValue( "include" ).getValue( String.class );
						assert "all".equals( include ) || "non-lazy".equals( include );
						return include.equals( "all" );
					}
			);
		}
	}

	private static AccessType interpretAccessType(AnnotationAttributeValue<CacheConcurrencyStrategy> usageValue, AccessType implicitValue) {
		if ( usageValue != null ) {
			final CacheConcurrencyStrategy strategy = usageValue.getValue();
			return strategy.toAccessType();
		}
		return implicitValue;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public AccessType getAccessType() {
		return accessType;
	}

	public void setAccessType(AccessType accessType) {
		this.accessType = accessType;
	}

	public boolean isCacheLazyProperties() {
		return cacheLazyProperties;
	}

	public void setCacheLazyProperties(boolean cacheLazyProperties) {
		this.cacheLazyProperties = cacheLazyProperties;
	}

	public void overlay(CacheRegionDefinition overrides) {
		if ( overrides == null ) {
			return;
		}

		enabled = true;
		accessType = AccessType.fromExternalName( overrides.getUsage() );
		if ( StringHelper.isEmpty( overrides.getRegion() ) ) {
			region = overrides.getRegion();
		}
		// ugh, primitive boolean
		cacheLazyProperties = overrides.isCacheLazy();
	}

	public void overlay(Caching overrides) {
		if ( overrides == null ) {
			return;
		}

		this.enabled = overrides.enabled;
		this.accessType = overrides.accessType;
		this.region = overrides.region;
		this.cacheLazyProperties = overrides.cacheLazyProperties;
	}

	@Override
	public String toString() {
		return "Caching{region='" + region + '\''
				+ ", accessType=" + accessType
				+ ", cacheLazyProperties=" + cacheLazyProperties
				+ ", enabled=" + enabled + '}';
	}

}
