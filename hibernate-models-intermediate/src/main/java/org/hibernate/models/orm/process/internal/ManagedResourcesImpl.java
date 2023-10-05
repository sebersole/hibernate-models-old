/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.orm.process.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hibernate.models.orm.process.spi.ManagedResources;

/**
 * @author Steve Ebersole
 */
public class ManagedResourcesImpl implements ManagedResources {
	private final List<String> packageNames;
	private final List<String> classNames;
	private final List<Class<?>> loadedClasses;
	private final List<String> xmlMappings;

	public ManagedResourcesImpl(
			List<String> packageNames,
			List<String> classNames,
			List<Class<?>> loadedClasses,
			List<String> xmlMappings) {
		this.packageNames = packageNames;
		this.classNames = classNames;
		this.loadedClasses = loadedClasses;
		this.xmlMappings = xmlMappings;
	}

	@Override
	public List<String> getPackageNames() {
		return packageNames;
	}

	@Override
	public List<String> getClassNames() {
		return classNames;
	}

	@Override
	public List<Class<?>> getLoadedClasses() {
		return loadedClasses;
	}

	@Override
	public List<String> getXmlMappings() {
		return xmlMappings;
	}

	public static class Builder {
		private List<String> packageNames;
		private List<String> classNames;
		private List<Class<?>> loadedClasses;
		private List<String> xmlMappings;

		public Builder addPackages(String... names) {
			if ( packageNames == null ) {
				packageNames = new ArrayList<>();
			}
			Collections.addAll( packageNames, names );
			return this;
		}

		public Builder addClassNames(String... names) {
			if ( classNames == null ) {
				classNames = new ArrayList<>();
			}
			Collections.addAll( classNames, names );
			return this;
		}

		public Builder addLoadedClasses(Class<?>... classes) {
			if ( loadedClasses == null ) {
				loadedClasses = new ArrayList<>();
			}
			Collections.addAll( loadedClasses, classes );
			return this;
		}

		public Builder addXmlMappings(String... names) {
			if ( xmlMappings == null ) {
				xmlMappings = new ArrayList<>();
			}
			Collections.addAll( xmlMappings, names );
			return this;
		}

		public ManagedResources build() {
			return new ManagedResourcesImpl(
					packageNames == null ? Collections.emptyList() : packageNames,
					classNames == null ? Collections.emptyList() : classNames,
					loadedClasses == null ? Collections.emptyList() : loadedClasses,
					xmlMappings == null ? Collections.emptyList() : xmlMappings
			);
		}
	}
}
