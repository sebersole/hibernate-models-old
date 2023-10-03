/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.source.spi;

import java.util.List;

import org.hibernate.models.source.ModelsException;
import org.hibernate.models.source.UnknownClassException;

/**
 * Registry of all {@link ClassDetails} references
 *
 * @author Steve Ebersole
 */
public interface ClassDetailsRegistry {
	/**
	 * Find the managed-class with the given {@code name}, if there is one.
	 * Returns {@code null} if there are none registered with that name.
	 */
	ClassDetails findClassDetails(String name);

	/**
	 * Form of {@link #findClassDetails} throwing an exception if no registration is found
	 *
	 * @throws UnknownClassException If no registration is found with the given {@code name}
	 */
	ClassDetails getClassDetails(String name);

	/**
	 * Visit each registered class details
	 */
	void forEachClassDetails(ClassDetailsConsumer consumer);

	/**
	 * Get the list of all direct subtypes for the named managed-class.  Returns
	 * {@code null} if there are none
	 */
	List<ClassDetails> getDirectSubTypes(String superTypeName);

	/**
	 * Visit each direct subtype of the named managed-class
	 */
	void forEachDirectSubType(String superTypeName, ClassDetailsConsumer consumer);

	/**
	 * Adds a managed-class descriptor using its {@linkplain ClassDetails#getName() name}
	 * as the registration key.
	 */
	void addClassDetails(ClassDetails classDetails);

	/**
	 * Adds a managed-class descriptor using the given {@code name} as the registration key
	 */
	void addClassDetails(String name, ClassDetails classDetails);

	/**
	 * Resolves a managed-class by name.  If there is currently no such registration,
	 * one is created.
	 */
	ClassDetails resolveClassDetails(String name);

	/**
	 * Resolves a managed-class by name.  If there is currently no such registration,
	 * one is created using the specified {@code creator}.
	 */
	ClassDetails resolveClassDetails(String name, ClassDetailsBuilder creator);

	/**
	 * Resolve (find or create) ClassDetails by name.  If there is currently no
	 * such registration, one is created using the specified {@code creator}.
	 */
	ClassDetails resolveClassDetails(String name, ClassDetailsCreator creator);

	/**
	 * Resolve (find or create) the named PackageDetails.  If there is currently no
	 * such registration, one is created using the specified {@code creator}.
	 */
	PackageDetails resolvePackageDetails(String packageName, PackageDetailsCreator creator);

	/**
	 * Find the named PackageDetails, or return {@code null}
	 *
	 * @return The PackageDetails, or null
	 */
	PackageDetails findPackageDetails(String name);

	/**
	 * Get the named PackageDetails, or throw an exception
	 *
	 * @return The PackageDetails, never null.
	 * @throws ModelsException if named PackageDetails not found
	 */
	PackageDetails getPackageDetails(String name);

	/**
	 * Visit each registered package details
	 */
	void forEachPackageDetails(PackageDetailsConsumer consumer);

	@FunctionalInterface
	interface PackageDetailsConsumer {
		void consume(PackageDetails packageDetails);
	}

	@FunctionalInterface
	interface ClassDetailsCreator {
		ClassDetails createClassDetails();
	}

	@FunctionalInterface
	interface ClassDetailsConsumer {
		void consume(ClassDetails classDetails);
	}

	@FunctionalInterface
	interface PackageDetailsCreator {
		PackageDetails createPackageDetails();
	}
}
