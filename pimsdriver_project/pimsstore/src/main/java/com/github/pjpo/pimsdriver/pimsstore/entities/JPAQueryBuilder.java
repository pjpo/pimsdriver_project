package com.github.pjpo.pimsdriver.pimsstore.entities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.github.pjpo.commons.predicates.And;
import com.github.pjpo.commons.predicates.Between;
import com.github.pjpo.commons.predicates.Boundary;
import com.github.pjpo.commons.predicates.Compare;
import com.github.pjpo.commons.predicates.Filter;
import com.github.pjpo.commons.predicates.IsNull;
import com.github.pjpo.commons.predicates.Like;
import com.github.pjpo.commons.predicates.Not;
import com.github.pjpo.commons.predicates.Or;
import com.github.pjpo.commons.predicates.OrderBy;
import com.github.pjpo.commons.predicates.Boundary.BoundaryType;
import com.github.pjpo.commons.predicates.Compare.Type;

public class JPAQueryBuilder {

	public static <T> List<T> getList(final EntityManager em, final Class<T> clazz, 
			final List<Filter> filters,	final List<OrderBy> orderBys,
			final Integer first, final Integer rows) {

		final CriteriaBuilder builder = em.getCriteriaBuilder();
		 
		 // SELECT FROM UPLOADEDPMSI
		 final CriteriaQuery<T> query = builder.createQuery(clazz);
		 final Root<T> uploadedPmsiFrom = query.from(clazz);
		 query.select(uploadedPmsiFrom);
		 
		 // WHERE PREDICATES
		 final Predicate predicate = convertPredicateAnd(filters, builder, uploadedPmsiFrom);
		 query.where(predicate);
		 
		 // ORDER BY PREDICATES
		 final ArrayList<Order> orders = new ArrayList<>(orderBys.size());
		 for (final OrderBy orderBy : orderBys) {
			 orders.add(convertOrderBy(orderBy, builder, uploadedPmsiFrom));
		 }
		 query.orderBy(orders);
		 
		 // SETS LIMIT
		 final TypedQuery<T> allQuery = em.createQuery(query);
		 if (first != null)
			 allQuery.setFirstResult(first);
		 if (rows != null)
			 allQuery.setMaxResults(rows);
	    
		 return allQuery.getResultList();
	}

	private static <T> Order convertOrderBy(final OrderBy orderBy, final CriteriaBuilder builder, final Root<T> root) {
		if (orderBy.getOrder() == com.github.pjpo.commons.predicates.OrderBy.Order.ASC) {
			return builder.asc(root.get(orderBy.getProperty()));
		} else  {
			return builder.desc(root.get(orderBy.getProperty()));
		}
	}
	
	private static <T> Predicate convertPredicateAnd(final Collection<Filter> filters, final CriteriaBuilder builder, final Root<T> root) {
		final LinkedList<Predicate> predicates = convertToPredicateList(filters, builder, root);
		return builder.and(predicates.toArray(new Predicate[predicates.size()]));
	}

	private static <T> Predicate convertPredicateOr(final Collection<Filter> filters, final CriteriaBuilder builder, final Root<T> root) {
		final LinkedList<Predicate> predicates = convertToPredicateList(filters, builder, root);
		return builder.or(predicates.toArray(new Predicate[predicates.size()]));
	}

	private static <T> LinkedList<Predicate> convertToPredicateList(Collection<Filter> filters, CriteriaBuilder builder, Root<T> root) {
		final LinkedList<Predicate> predicates = new LinkedList<>();
		for (final Filter filter : filters) {
			final Predicate predicate = convertToPredicate(filter, builder, root);
			if (predicate != null)
				predicates.add(predicate);
		}
		return predicates;
	}
	
	private static <T> Predicate convertToPredicate(final Filter filter, final CriteriaBuilder builder, final Root<T> root) {
		if (filter instanceof And) {
			final And ffilter = (And) filter;
			return convertPredicateAnd(ffilter.getFilters(), builder, root);
		} else if (filter instanceof Or) {
			final Or ffilter = (Or) filter;
			return convertPredicateOr(ffilter.getFilters(), builder, root);
		} else if (filter instanceof Between) {
			final Between<?> ffilter = (Between<?>) filter;
			return convertBetween(ffilter, builder, root);
		} else if (filter instanceof Compare) {
			final Compare<?> ffilter = (Compare<?>) filter;
			return convertCompare(ffilter, builder, root);
		} else if (filter instanceof IsNull) {
			final IsNull<?> ffilter = (IsNull<?>) filter;
			return convertIsNull(ffilter, builder, root);
		} else if (filter instanceof Like) {
			final Like ffilter = (Like) filter;
			return convertLike(ffilter, builder, root);
		} else if (filter instanceof Not) {
			final Not ffilter = (Not) filter;
			return builder.not(convertToPredicate(ffilter.getFilter(), builder, root));
		} else {
			return null;
		}
	}

	private static <T> Predicate convertLike(final Like like, final CriteriaBuilder builder, final Root<T> root) {
		final String value = like.getValue();
		final Expression<String> expression = root.<String>get(like.getProperty());
		return builder.like(expression, value);
	}

	private static <T, Y> Predicate convertIsNull(final IsNull<Y> isNull, final CriteriaBuilder builder, final Root<T> root) {
		final Expression<? extends Y> expression = root.<Y>get(isNull.getProperty());
		return builder.isNull(expression);
	}

	private static <T, Y extends Comparable<? super Y>> Predicate convertCompare(final Compare<Y> compare, final CriteriaBuilder builder,
			final Root<T> root) {
		final Y value = compare.getValue();
		final Expression<? extends Y> expression = root.<Y>get(compare.getProperty());
		if (compare.getType() == Type.EQUAL) {
			return builder.equal(expression, value);
		} else if (compare.getType() == Type.GREATER) {
			return builder.greaterThan(expression, value);
		} else if (compare.getType() == Type.GREATER_OR_EQUAL) {
			return builder.greaterThanOrEqualTo(expression, value);
		} else if (compare.getType() == Type.LESS) {
			return builder.lessThan(expression, value);
		} else if (compare.getType() == Type.LESS_OR_EQUAL) {
			return builder.lessThanOrEqualTo(expression, value);
		} else {
			return null;
		}
	}
	
	private static <T, Y extends Comparable<? super Y>> Predicate convertBetween(final Between<Y> between, final CriteriaBuilder builder,
			final Root<T> root) {
		final Predicate startPredicate = convertGreaterBoundary(between.getStart(), between.getProperty(), builder, root);
		final Predicate endPredicate = convertLessBoundary(between.getEnd(), between.getProperty(), builder, root);
		if (startPredicate == null)
			return endPredicate;
		else if (endPredicate == null) 
			return startPredicate;
		else {
			return builder.and(startPredicate, endPredicate);
		}
	}

	private static <T, Y extends Comparable<? super Y>> Predicate convertGreaterBoundary(final Boundary<Y> min, final String property,
			final CriteriaBuilder builder, final Root<T> root) {
		if (min == null || min.getValue() == null) {
			return null;
		} else if (min.getBoundaryType() == BoundaryType.INCLUDED) {
			final Y value = min.getValue();
			final Expression<? extends Y> expression = root.<Y>get(property);
			return builder.greaterThanOrEqualTo(expression, value);
		} else {
			final Y value = min.getValue();
			final Expression<? extends Y> expression = root.<Y>get(property);
			return builder.greaterThan(expression, value);
		} 
	}

	private static <T, Y extends Comparable<? super Y>> Predicate convertLessBoundary(final Boundary<Y> max, final String property,
			final CriteriaBuilder builder, final Root<T> root) {
		if (max == null || max.getValue() == null) {
			return null;
		} else if (max.getBoundaryType() == BoundaryType.INCLUDED) {
			final Y value = max.getValue();
			final Expression<? extends Y> expression = root.<Y>get(property);
			return builder.lessThanOrEqualTo(expression, value);
		} else {
			final Y value = max.getValue();
			final Expression<? extends Y> expression = root.<Y>get(property);
			return builder.lessThan(expression, value);
		} 
	}

}
