/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.boot.model.jandex;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.hibernate.models.Internal;
import org.hibernate.models.Incubating;
import org.hibernate.models.internal.StringHelper;
import org.hibernate.models.orm.process.spi.ManagedResources;
import org.hibernate.models.spi.ClassLoading;

import org.jboss.jandex.IndexView;
import org.jboss.jandex.Indexer;

/**
 * Indexes managed-resources (from scanning, persistence.xml, etc.).  Used when
 * Hibernate is asked to do the indexing, as opposed to the index being passed to it.
 *
 * @author Steve Ebersole
 */
@Internal
@Incubating
public class JandexIndexer {
	/**
	 * Index the managed-resources
	 *
	 * @param classLoading Used to load {@linkplain ClassLoading#locateResource resources}
	 */
	public static void index(
			ManagedResources managedResources,
			Indexer indexer,
			ClassLoading classLoading) {
		for ( String packageName : managedResources.getPackageNames() ) {
			// Jandex models a package by its `package-info` class, if one
			final String packageInfoResourceName = StringHelper.classNameToResourceName( packageName + ".package-info" );
			final URL resource = classLoading.locateResource( packageInfoResourceName );
			if ( resource == null ) {
				// should mean the package has no package-info (or does not exist)
				continue;
			}
			index( resource, indexer );
		}

		for ( String className : managedResources.getClassNames() ) {
			// NOTE : a class name here could potentially be a package name (from JPA persistence.xml)
			// first try as a class
			final String classResourceName = StringHelper.classNameToResourceName( className );
			final URL classResource = classLoading.locateResource( classResourceName );
			if ( classResource != null ) {
				index( classResource, indexer );
				continue;
			}

			// try as a package
			final String packageInfoResourceName = StringHelper.classNameToResourceName( className + ".package-info" );
			final URL packageResource = classLoading.locateResource( packageInfoResourceName );
			if ( packageResource != null ) {
				index( packageResource, indexer );
			}

			// todo : exception?
		}

		for ( Class<?> loadedClass : managedResources.getLoadedClasses() ) {
			index( loadedClass, indexer );
		}

		// todo : mapping files
	}

	public static void index(URL resource, Indexer indexer) {
		try {
			final URLConnection resourceConnection = resource.openConnection();
			try (final InputStream inputStream = resourceConnection.getInputStream()) {
				indexer.index( inputStream );
			}
		}
		catch (IOException e) {
			throw new IndexingException( "Unable to index resource" + resource, e );
		}
	}

	private static void index(Class<?> loadedClass, Indexer indexer) {
		try {
			indexer.indexClass( loadedClass );
		}
		catch (IOException e) {
			throw new IndexingException( "Unable to index loaded Class" + loadedClass.getName(), e );
		}
	}
}
