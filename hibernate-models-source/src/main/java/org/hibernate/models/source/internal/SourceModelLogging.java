/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.source.internal;

import org.jboss.logging.BasicLogger;
import org.jboss.logging.Logger;
import org.jboss.logging.annotations.LogMessage;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageLogger;
import org.jboss.logging.annotations.ValidIdRange;

import static org.jboss.logging.Logger.Level.INFO;

/**
 * todo (annotation-source) : find the proper min/max id range
 * @author Steve Ebersole
 */
@MessageLogger( projectCode = "HHH" )
@ValidIdRange( min = 999901, max = 999999 )
public interface SourceModelLogging extends BasicLogger {
	String NAME = "org.hibernate.models";

	Logger SOURCE_MODEL_LOGGER = Logger.getLogger( NAME );
	SourceModelLogging SOURCE_MODEL_MSG_LOGGER = Logger.getMessageLogger( SourceModelLogging.class, NAME );

	@LogMessage(level = INFO)
	@Message( id = 999901, value = "Entity `%s` used both @DynamicInsert and @SQLInsert" )
	void dynamicAndCustomInsert(String entityName);

	@LogMessage(level = INFO)
	@Message( id = 999902, value = "Entity `%s` used both @DynamicUpdate and @SQLUpdate" )
	void dynamicAndCustomUpdate(String entityName);
}
