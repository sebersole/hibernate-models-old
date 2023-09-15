/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */

/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.models.source.spi;

import com.fasterxml.classmate.MemberResolver;
import com.fasterxml.classmate.TypeResolver;

/**
 * @author Steve Ebersole
 */
public class ClassmateContext {
	private TypeResolver typeResolver = new TypeResolver();
	private MemberResolver memberResolver = new MemberResolver( typeResolver );

	public TypeResolver getTypeResolver() {
		if ( typeResolver == null ) {
			throw new IllegalStateException( "Classmate context has been released" );
		}
		return typeResolver;
	}

	public MemberResolver getMemberResolver() {
		if ( memberResolver == null ) {
			throw new IllegalStateException( "Classmate context has been released" );
		}
		return memberResolver;
	}

	public void release() {
		typeResolver = null;
		memberResolver = null;
	}
}
