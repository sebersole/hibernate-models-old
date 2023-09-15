/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.source.internal.hcann;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import org.hibernate.annotations.common.reflection.XClass;
import org.hibernate.annotations.common.reflection.XMethod;
import org.hibernate.annotations.common.reflection.XProperty;
import org.hibernate.models.internal.ArrayHelper;
import org.hibernate.models.internal.CollectionHelper;
import org.hibernate.models.internal.IndexedConsumer;
import org.hibernate.models.source.internal.LazyAnnotationTarget;
import org.hibernate.models.source.spi.ClassDetails;
import org.hibernate.models.source.spi.ClassDetailsRegistry;
import org.hibernate.models.source.spi.FieldDetails;
import org.hibernate.models.source.spi.MethodDetails;
import org.hibernate.models.source.spi.SourceModelBuildingContext;

/**
 * ClassDetails implementation based on a {@link XClass} reference
 *
 * @author Steve Ebersole
 */
public class ClassDetailsImpl extends LazyAnnotationTarget implements ClassDetails {
	private final XClass xClass;

	private final ClassDetails superType;
	private List<ClassDetails> implementedInterfaces;

	private List<FieldDetailsImpl> fields;
	private List<MethodDetailsImpl> methods;

	public ClassDetailsImpl(XClass xClass, SourceModelBuildingContext processingContext) {
		super( xClass::getAnnotations, processingContext );
		this.xClass = xClass;

		this.superType = determineSuperType( xClass, processingContext );

		processingContext.getClassDetailsRegistry().addClassDetails( this );
	}

	private ClassDetails determineSuperType(XClass xClass, SourceModelBuildingContext processingContext) {
		final XClass superclass = xClass.getSuperclass();
		if ( superclass == null ) {
			return null;
		}
		if ( Object.class.getName().equals( superclass.getName() ) ) {
			return null;
		}

		final ClassDetailsRegistry classDetailsRegistry = processingContext.getClassDetailsRegistry();
		final ClassDetails existing = classDetailsRegistry.findClassDetails( superclass.getName() );
		if ( existing != null ) {
			return existing;
		}

		final ClassDetailsImpl managedClass = new ClassDetailsImpl( superclass, processingContext );
		classDetailsRegistry.addClassDetails( managedClass );
		return managedClass;
	}

	@Override
	public String getName() {
		return getClassName();
	}

	@Override
	public String getClassName() {
		return xClass.getName();
	}

	@Override
	public boolean isAbstract() {
		return xClass.isAbstract();
	}

	@Override
	public ClassDetails getSuperType() {
		return superType;
	}

	@Override
	public List<ClassDetails> getImplementedInterfaceTypes() {
		if ( implementedInterfaces == null ) {
			implementedInterfaces = buildImplementedInterfaces();
		}

		return implementedInterfaces;
	}

	private List<ClassDetails> buildImplementedInterfaces() {
		final XClass[] interfaces = xClass.getInterfaces();
		if ( ArrayHelper.isEmpty( interfaces ) ) {
			return Collections.emptyList();
		}

		final ArrayList<ClassDetails> result = CollectionHelper.arrayList( interfaces.length );
		for ( int i = 0; i < interfaces.length; i++ ) {
			final XClass intf = interfaces[ i ];
			final ClassDetails classDetails = getProcessingContext().getClassDetailsRegistry().resolveClassDetails(
					intf.getName(),
					() -> new ClassDetailsImpl( intf, getProcessingContext() )
			);
			result.add( classDetails );
		}
		return result;
	}

	@Override
	public boolean isImplementor(Class<?> checkType) {
		return checkType.isAssignableFrom( toJavaClass() );
	}

	@Override
	public List<FieldDetails> getFields() {
		if ( fields == null ) {
			fields = resolveFields( xClass, getProcessingContext() );
		}
		//noinspection unchecked,rawtypes
		return (List) fields;
	}

	private static List<FieldDetailsImpl> resolveFields(XClass xClass, SourceModelBuildingContext processingContext) {
		final List<XProperty> xFields = xClass.getDeclaredProperties( "field" );
		final ArrayList<FieldDetailsImpl> fields = CollectionHelper.arrayList( xFields.size() );
		for ( int i = 0; i < xFields.size(); i++ ) {
			final XProperty xField = xFields.get( i );
			fields.add( new FieldDetailsImpl( xField, processingContext ) );
		}
		return fields;
	}

	@Override
	public void forEachField(IndexedConsumer<FieldDetails> consumer) {
		if ( fields == null ) {
			return;
		}
		//noinspection unchecked
		fields.forEach( (Consumer<FieldDetails>) consumer );
	}

	@Override
	public List<MethodDetails> getMethods() {
		if ( methods == null ) {
			methods = resolveMethods( xClass, getProcessingContext() );
		}
		//noinspection unchecked,rawtypes
		return (List) methods;
	}

	private static List<MethodDetailsImpl> resolveMethods(
			XClass xClass,
			SourceModelBuildingContext processingContext) {
		final List<XMethod> xMethods = xClass.getDeclaredMethods();
		final ArrayList<MethodDetailsImpl> methods = CollectionHelper.arrayList( xMethods.size() );
		for ( int i = 0; i < xMethods.size(); i++ ) {
			final XMethod xMethod = xMethods.get( i );
			methods.add( new MethodDetailsImpl( xMethod, processingContext ) );
		}
		return methods;
	}

	@Override
	public void forEachMethod(IndexedConsumer<MethodDetails> consumer) {
		if ( methods == null ) {
			return;
		}
		//noinspection unchecked,rawtypes
		methods.forEach( (Consumer) consumer );
	}

	@Override
	public Class<?> toJavaClass() {
		return getProcessingContext().getReflectionManager().toClass( xClass );
	}

	@Override
	public String toString() {
		return "ClassDetails(`" + xClass.getName() + "` [HCANN])";
	}
}
