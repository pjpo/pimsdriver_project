package com.github.pjpo.pimsdriver.pimsstore.ejb;

import java.util.LinkedHashMap;
import java.util.List;

import javax.ejb.Local;

import com.github.pjpo.commons.predicates.Filter;
import com.github.pjpo.commons.predicates.OrderBy;
import com.github.pjpo.pimsdriver.pimsstore.entities.UploadedPmsi;

@Local
public interface Report {

	public LinkedHashMap<String, Long> readPmsiOverview(
			UploadedPmsi model, String headerName);

	public <T> List<T> getList(
			Class<T> clazz, List<Filter> filters,
			List<OrderBy> orders, Integer first, Integer rows);

	public <T> Long getSize(Class<T> clazz, List<Filter> filters);
	
	public <T> List<Long> getSummary(Class<T> clazz, List<Filter> filters, String... sums);

}
