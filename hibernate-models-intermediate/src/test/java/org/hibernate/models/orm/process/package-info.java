/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */

/**
 * @author Steve Ebersole
 */
@ConverterRegistration(domainType = boolean.class, converter = YesNoConverter.class)
package org.hibernate.models.orm.process;

import org.hibernate.annotations.ConverterRegistration;
import org.hibernate.type.YesNoConverter;