package com.github.pjpo.pimsdriver.pimsstore.ejb;

import java.util.LinkedHashMap;

import javax.ejb.Local;

import com.github.pjpo.pimsdriver.pimsstore.entities.UploadedPmsi;

@Local
public interface Report {

	public LinkedHashMap<String, Long> readPmsiOverview(
			UploadedPmsi model, String headerName);

}
