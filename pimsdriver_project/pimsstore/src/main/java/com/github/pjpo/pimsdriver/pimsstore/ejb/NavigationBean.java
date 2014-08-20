package com.github.pjpo.pimsdriver.pimsstore.ejb;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import com.github.pjpo.commons.predicates.Filter;
import com.github.pjpo.commons.predicates.OrderBy;
import com.github.pjpo.pimsdriver.datasource.DataSourceProvider;
import com.github.pjpo.pimsdriver.pimsstore.NavigationDTO;
import com.github.pjpo.pimsdriver.pimsstore.aop.DTOEncloser;
import com.github.pjpo.pimsdriver.pimsstore.entities.JPAQueryBuilder;
import com.github.pjpo.pimsdriver.pimsstore.entities.UploadedPmsi;


@Stateless
public class NavigationBean implements Navigation {

	private static final Logger LOGGER = Logger.getLogger(NavigationBean.class.toString());

	@PersistenceContext
	private EntityManager em;
	 
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
	
	@Override
	public List<LocalDate> getPmsiDates(final String finess) {
		try (final Connection con = dataSourceProvider.getConnection()) {
			return DTOEncloser.execute(con, () -> NavigationDTO.getPmsiDates(con, finess));
		} catch (Throwable e) {
			LOGGER.log(Level.WARNING, "Error when retrieving dates for finess " + finess, e);
			return null;
		}
	}

	@Override
	public List<UploadedPmsi> getUploadedPmsi(List<Filter> filters,
			List<OrderBy> orderBys, Integer first, Integer rows) {
		return JPAQueryBuilder.getList(em, UploadedPmsi.class, filters, orderBys, first, rows);
	}
}
