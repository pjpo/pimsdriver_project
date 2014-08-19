package com.github.pjpo.pimsdriver.pimsstore.ejb;

import java.sql.Connection;
import java.util.LinkedHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.github.pjpo.pimsdriver.datasource.DataSourceProvider;
import com.github.pjpo.pimsdriver.pimsstore.ReportDTO;
import com.github.pjpo.pimsdriver.pimsstore.aop.DTOEncloser;

@Stateless
public class ReportBean implements Report {

	private static final Logger LOGGER = Logger.getLogger(ReportBean.class.toString());

	@EJB(lookup="java:global/business/datasource-0.0.1-SNAPSHOT/DataSourceProviderBean!com.github.pjpo.pimsdriver.datasource.DataSourceProvider")
	private DataSourceProvider dataSourceProvider;

	@Override
	public LinkedHashMap<String, Long> readPmsiOverview(
			Navigation.UploadedPmsi model, String headerName) {
		try (final Connection con = dataSourceProvider.getConnection()) {
			return DTOEncloser.execute(con, () -> ReportDTO.readPmsiOverview(con, model, headerName));
		} catch (Throwable e) {
			LOGGER.log(Level.WARNING, "Error when retrieving finesses", e);
			return null;
		}
	}

}
