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
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.github.pjpo.commons.predicates.Filter;
import com.github.pjpo.commons.predicates.OrderBy;
import com.github.pjpo.pimsdriver.datasource.DataSourceProvider;
import com.github.pjpo.pimsdriver.pimsstore.ReportDTO;
import com.github.pjpo.pimsdriver.pimsstore.aop.DTOEncloser;
import com.github.pjpo.pimsdriver.pimsstore.entities.JPAQueryBuilder;
import com.github.pjpo.pimsdriver.pimsstore.entities.RsfA;
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
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public LinkedHashMap<String, Long> readPmsiOverview(
			UploadedPmsi model, String headerName) {
		// SPECIFIC TO ECLIPSELINK
		Connection con = em.unwrap(Connection.class);
		try {
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

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public List<RsfA> getRsfAList(
			List<Filter> filters, List<OrderBy> orders,
			Integer first, Integer rows) {
		return JPAQueryBuilder.getList(em, RsfA.class, filters, orders, first, rows);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Long getRsfASize(List<Filter> filters) {
		return JPAQueryBuilder.getCount(em, RsfA.class, filters);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public RsfA getRsfASummary(List<Filter> filters) {
		
		final CriteriaBuilder builder = em.getCriteriaBuilder();
		 
		 // SELECT FROM RSFA
		 final CriteriaQuery<Object> query = builder.createQuery();
		 final Root<RsfA> rsfARoot = query.from(RsfA.class);
		 
		 // SUMS
		 Expression<?> sumTotalfacturehonoraire = builder.sum(rsfARoot.get("totalfacturehonoraire"));
		 Expression<?> sumTotalfactureph = builder.sum(rsfARoot.get("totalfactureph"));

		 query.multiselect(sumTotalfacturehonoraire, sumTotalfactureph);
		 
		 // WHERE PREDICATES
		 final Predicate predicate = JPAQueryBuilder.convertPredicateAnd(filters, builder, rsfARoot);
		 query.where(predicate);
	    
		 // GETS RESULTS
		 @SuppressWarnings("unused")
		 Object result = em.createQuery(query).getSingleResult();
		 
		 return null;

	}
	
}
