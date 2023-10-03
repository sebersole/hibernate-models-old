/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.boot.model.convert.internal;

import java.lang.reflect.Member;
import java.lang.reflect.ParameterizedType;
import java.util.List;

import org.hibernate.AnnotationException;
import org.hibernate.HibernateException;
import org.hibernate.internal.util.GenericsHelper;
import org.hibernate.internal.util.type.PrimitiveWrapperHelper;
import org.hibernate.models.Copied;
import org.hibernate.models.orm.spi.ClassmateContext;
import org.hibernate.models.source.spi.ClassDetails;
import org.hibernate.models.source.spi.MemberDetails;

import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.classmate.ResolvedTypeWithMembers;
import com.fasterxml.classmate.members.ResolvedField;
import com.fasterxml.classmate.members.ResolvedMember;
import com.fasterxml.classmate.members.ResolvedMethod;
import jakarta.persistence.AttributeConverter;

/**
 * Helpers related to handling converters
 */
@Copied
public class ConverterHelper {
	public static ParameterizedType extractAttributeConverterParameterizedType(Class<? extends AttributeConverter<?,?>> base) {
		return GenericsHelper.extractParameterizedType( base );
	}

	public static ResolvedMember<? extends Member> resolveMember(
			ClassDetails container,
			MemberDetails propertyMember,
			ClassmateContext classmateContext) {
		final Class<?> declaringType = container.toJavaClass();
		final ResolvedType declaringTypeResolved = classmateContext.getTypeResolver().resolve( declaringType );
		final ResolvedTypeWithMembers declaringClassWithMembers = classmateContext.getMemberResolver().resolve(
				declaringTypeResolved,
				null,
				null
		);

		if ( propertyMember.isField() ) {
			for ( ResolvedField resolvedMember : declaringClassWithMembers.getMemberFields() ) {
				if ( resolvedMember.getName().equals( propertyMember.getName() ) ) {
					return resolvedMember;
				}
			}
		}
		else {
			for ( ResolvedMethod resolvedMember : declaringClassWithMembers.getMemberMethods() ) {
				if ( resolvedMember.getName().equals( propertyMember.getName() ) ) {
					return resolvedMember;
				}
			}
		}

		throw new HibernateException(
				"Could not locate resolved type information for attribute [" + propertyMember.getName() + "] from Classmate"
		);
	}

	public static List<ResolvedType> resolveConverterClassParamTypes(
			Class<? extends AttributeConverter<?, ?>> converterClass,
			ClassmateContext context) {
		final ResolvedType converterType = context.getTypeResolver().resolve( converterClass );
		final List<ResolvedType> converterParamTypes = converterType.typeParametersFor( AttributeConverter.class );
		if ( converterParamTypes == null ) {
			throw new AnnotationException(
					"Could not extract type argument from attribute converter class '"
							+ converterClass.getName() + "'"
			);
		}
		else if ( converterParamTypes.size() != 2 ) {
			throw new AnnotationException(
					"Unexpected type argument for attribute converter class '"
							+ converterClass.getName()
							+ "' (expected 2 type arguments, but found " + converterParamTypes.size() + ")"
			);
		}
		return converterParamTypes;
	}

	/**
	 * Determine whether 2 types match.  Intended for determining whether to auto applying a converter
	 *
	 * @param converterDefinedType The type defined via the converter's parameterized type signature.
	 * 		E.g. {@code O} in {@code implements AttributeConverter<O,R>}
	 * @param checkType The type from the domain model (basic attribute type, Map key type, Collection element type)
	 *
	 * @return {@code true} if they match, otherwise {@code false}.
	 */
	public static boolean typesMatch(ResolvedType converterDefinedType, ResolvedType checkType) {
		Class<?> erasedCheckType = checkType.getErasedType();
		if ( erasedCheckType.isPrimitive() ) {
			erasedCheckType = PrimitiveWrapperHelper.getDescriptorByPrimitiveType( erasedCheckType ).getWrapperClass();
		}
		if ( !converterDefinedType.getErasedType().isAssignableFrom( erasedCheckType ) ) {
			return false;
		}

		// if the converter did not define any nested type parameters, then the check above is
		// enough for a match
		if ( converterDefinedType.getTypeParameters().isEmpty() ) {
			return true;
		}

		// however, here the converter *did* define nested type parameters, so we'd have a converter defined using something like, e.g., List<String> for its
		// domain type.
		//
		// we need to check those nested types as well

		if ( checkType.getTypeParameters().isEmpty() ) {
			// the domain type did not define nested type params.  a List<String> would not auto-match a List(<Object>)
			return false;
		}

		if ( converterDefinedType.getTypeParameters().size() != checkType.getTypeParameters().size() ) {
			// they had different number of type params somehow.
			return false;
		}

		for ( int i = 0; i < converterDefinedType.getTypeParameters().size(); i++ ) {
			if ( !typesMatch( converterDefinedType.getTypeParameters().get( i ), checkType.getTypeParameters().get( i ) ) ) {
				return false;
			}
		}

		return true;
	}
}
