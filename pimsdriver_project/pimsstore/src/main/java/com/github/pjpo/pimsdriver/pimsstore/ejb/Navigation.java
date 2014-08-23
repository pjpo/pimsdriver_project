package com.github.pjpo.pimsdriver.pimsstore.ejb;

import java.time.LocalDate;
import java.util.List;

import javax.ejb.Local;

import com.github.pjpo.commons.predicates.Filter;
import com.github.pjpo.commons.predicates.OrderBy;
import com.github.pjpo.pimsdriver.pimsstore.entities.UploadedPmsi;

@Local
public interface Navigation {

	public List<String> getFinesses();
	
	public List<LocalDate> getPmsiDates(String finess);
	
	public List<UploadedPmsi> getUploadedPmsi(final List<Filter> filters, final List<OrderBy> orders,
			final Integer first, final Integer rows);

}
