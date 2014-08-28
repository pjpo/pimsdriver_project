package com.github.pjpo.pimsdriver.pimsstore.ejb;

import java.util.LinkedHashMap;
import java.util.List;

import javax.ejb.Local;

import com.github.pjpo.commons.predicates.Filter;
import com.github.pjpo.commons.predicates.OrderBy;
import com.github.pjpo.pimsdriver.pimsstore.entities.RsfA;
import com.github.pjpo.pimsdriver.pimsstore.entities.RssMain;
import com.github.pjpo.pimsdriver.pimsstore.entities.UploadedPmsi;

@Local
public interface Report {

	public LinkedHashMap<String, Long> readPmsiOverview(
			UploadedPmsi model, String headerName);

	public List<RssMain> getRssMainList(
			List<Filter> filters, List<OrderBy> orders,
			Integer first, Integer rows);

	public Long getRssMainSize(List<Filter> filters);
	
	public List<RsfA> getRsfAList(
			List<Filter> filters, List<OrderBy> orders,
			Integer first, Integer rows);

	public Long getRsfASize(List<Filter> filters);

	public RsfA getRsfASummary(List<Filter> filters);

}
