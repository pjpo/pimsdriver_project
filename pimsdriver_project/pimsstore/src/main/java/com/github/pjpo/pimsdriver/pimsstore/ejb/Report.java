package com.github.pjpo.pimsdriver.pimsstore.ejb;

import java.util.LinkedHashMap;
import javax.ejb.Local;

@Local
public interface Report {

	public LinkedHashMap<String, Long> readPmsiOverview(
			Navigation.UploadedPmsi model, String headerName);

}
