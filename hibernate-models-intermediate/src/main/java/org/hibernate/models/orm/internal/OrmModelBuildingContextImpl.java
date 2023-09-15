package org.hibernate.models.orm.internal;

import org.hibernate.models.orm.spi.OrmModelBuildingContext;
import org.hibernate.models.orm.spi.SourceModel;

/**
 * @author Steve Ebersole
 */
public class OrmModelBuildingContextImpl implements OrmModelBuildingContext {
	private final SourceModel sourceModel;

	public OrmModelBuildingContextImpl(SourceModel sourceModel) {
		this.sourceModel = sourceModel;
	}

	@Override
	public SourceModel getSourceModel() {
		return sourceModel;
	}
}
