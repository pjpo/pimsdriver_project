package com.github.pjpo.pimsdriver.pimsstore.ejb;

import java.sql.Connection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.github.pjpo.commons.predicates.Filter;
import com.github.pjpo.commons.predicates.OrderBy;
import com.github.pjpo.pimsdriver.datasource.DataSourceProvider;
import com.github.pjpo.pimsdriver.pimsstore.ReportDTO;
import com.github.pjpo.pimsdriver.pimsstore.aop.DTOEncloser;
import com.github.pjpo.pimsdriver.pimsstore.entities.JPAQueryBuilder;
import com.github.pjpo.pimsdriver.pimsstore.entities.RssMain;
import com.github.pjpo.pimsdriver.pimsstore.entities.UploadedPmsi;

@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class ReportBean implements Report {

	private static final Logger LOGGER = Logger.getLogger(ReportBean.class.toString());

	@PersistenceContext(unitName="pimsdriver")
	private EntityManager em;

	@EJB(lookup="java:global/business/datasource-0.0.1-SNAPSHOT/DataSourceProviderBean!com.github.pjpo.pimsdriver.datasource.DataSourceProvider")
	private DataSourceProvider dataSourceProvider;

	@Override
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public LinkedHashMap<String, Long> readPmsiOverview(
			UploadedPmsi model, String headerName) {
		try (final Connection con = dataSourceProvider.getConnection()) {
			return DTOEncloser.execute(con, () -> ReportDTO.readPmsiOverview(con, model, headerName));
		} catch (Throwable e) {
			LOGGER.log(Level.WARNING, "Error when retrieving finesses", e);
			return null;
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public List<RssMain> getRssMainList(final List<Filter> filters, final List<OrderBy> orders,
			final Integer first, final Integer rows) {
		return JPAQueryBuilder.getList(em, RssMain.class, filters, orders, first, rows);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Long getRssMainSize(final List<Filter> filters) {
		return JPAQueryBuilder.getCount(em, RssMain.class, filters);
	}

}
