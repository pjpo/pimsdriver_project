package com.github.pjpo.pimsdriver.pimsstore.vaadin;

import java.io.Serializable;
import java.util.List;

import com.vaadin.data.Container.Filter;

public interface DBTranslator extends Serializable {

	public boolean translatesFilter(Filter filter);

    public String getWhereStringForFilter(Filter filter, List<Object> arguments);
}
