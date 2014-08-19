package com.github.pjpo.pimsdriver.pimsstore.ejb;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.github.pjpo.pimsdriver.datasource.DataSourceProvider;
import com.github.pjpo.pimsdriver.pimsstore.NavigationDTO;
import com.github.pjpo.pimsdriver.pimsstore.aop.DTOEncloser;

@Stateless
public class NavigationBean implements Navigation {

	private static final Logger LOGGER = Logger.getLogger(NavigationBean.class.toString());

	@EJB(lookup="java:global/business/datasource-0.0.1-SNAPSHOT/DataSourceProviderBean!com.github.pjpo.pimsdriver.datasource.DataSourceProvider")
	private DataSourceProvider dataSourceProvider;
	
	@Override
	public List<String> getFinesses() {
		try (final Connection con = dataSourceProvider.getConnection()) {
			return DTOEncloser.execute(con, () -> NavigationDTO.getFinesses(con));
		} catch (Throwable e) {
			LOGGER.log(Level.WARNING, "Error when retrieving finesses", e);
			return null;
		}
	}
	
	public List<LocalDate> getPmsiDates(final String finess) {
		try (final Connection con = dataSourceProvider.getConnection()) {
			return DTOEncloser.execute(con, () -> NavigationDTO.getPmsiDates(con, finess));
		} catch (Throwable e) {
			LOGGER.log(Level.WARNING, "Error when retrieving dates for finess " + finess, e);
			return null;
		}
	}

}
