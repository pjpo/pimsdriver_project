package com.github.pjpo.pimsdriver.pimsstore.ejb;

import java.time.LocalDate;
import java.util.List;

import javax.ejb.Local;

@Local
public interface Navigation {

	public List<String> getFinesses();
	
	public List<LocalDate> getPmsiDates(String finess);
	
}
