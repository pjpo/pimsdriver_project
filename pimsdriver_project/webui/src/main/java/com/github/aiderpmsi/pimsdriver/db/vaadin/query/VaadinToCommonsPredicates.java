package com.github.aiderpmsi.pimsdriver.db.vaadin.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.github.pjpo.commons.predicates.And;
import com.github.pjpo.commons.predicates.Between;
import com.github.pjpo.commons.predicates.Compare;
import com.github.pjpo.commons.predicates.Filter;
import com.github.pjpo.commons.predicates.IsNull;
import com.github.pjpo.commons.predicates.Like;
import com.github.pjpo.commons.predicates.Not;
import com.github.pjpo.commons.predicates.Or;
import com.github.pjpo.commons.predicates.Between.BoundaryType;
import com.github.pjpo.commons.predicates.Compare.Type;
import com.github.pjpo.commons.predicates.OrderBy;
import com.github.pjpo.commons.predicates.OrderBy.Order;
import com.vaadin.data.util.filter.Compare.Operation;
import com.vaadin.data.util.filter.SimpleStringFilter;

public class VaadinToCommonsPredicates {

	public static List<Filter> convertFilters(com.vaadin.data.Container.Filter... vaadinFilters) {
		return convertFilters(Arrays.asList(vaadinFilters));
	}

	public static List<Filter> convertFilters(Collection<com.vaadin.data.Container.Filter> vaadinFilters) {
		final ArrayList<Filter> commonsFilters = new ArrayList<Filter>(vaadinFilters.size());
		for (com.vaadin.data.Container.Filter vaadinFilter : vaadinFilters) {
			Filter commonsFilter = convertFilter(vaadinFilter);
			if (commonsFilter != null) {
				commonsFilters.add(convertFilter(vaadinFilter));
			}
		}
		return commonsFilters;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static Filter convertFilter(com.vaadin.data.Container.Filter vaadinFilter) {
		if (vaadinFilter instanceof com.vaadin.data.util.filter.And) {
			com.vaadin.data.util.filter.And and = (com.vaadin.data.util.filter.And) vaadinFilter;
			return new And(convertFilters(and.getFilters()).toArray(new Filter[and.getFilters().size()]));
		} else if (vaadinFilter instanceof com.vaadin.data.util.filter.Or) {
			com.vaadin.data.util.filter.Or or = (com.vaadin.data.util.filter.Or) vaadinFilter;
			return new Or(convertFilters(or.getFilters()).toArray(new Filter[or.getFilters().size()]));
		} else if (vaadinFilter instanceof com.vaadin.data.util.filter.Like) {
			com.vaadin.data.util.filter.Like like = (com.vaadin.data.util.filter.Like) vaadinFilter;
			return new Like(like.getPropertyId().toString(), like.getValue(), like.isCaseSensitive());
		} else if (vaadinFilter instanceof com.vaadin.data.util.filter.Between) {
			com.vaadin.data.util.filter.Between between = (com.vaadin.data.util.filter.Between) vaadinFilter;
			return new Between(between.getPropertyId().toString(), BoundaryType.INCLUDED, (Comparable) between.getStartValue(),
					 BoundaryType.INCLUDED, (Comparable) between.getEndValue());
		} else if (vaadinFilter instanceof com.vaadin.data.util.filter.Compare) {
			com.vaadin.data.util.filter.Compare compare = (com.vaadin.data.util.filter.Compare) vaadinFilter;
			return new Compare(compare.getPropertyId().toString(), (Comparable) compare.getValue(),
					compare.getOperation() == Operation.EQUAL ? Type.EQUAL :
						compare.getOperation() == Operation.GREATER ? Type.GREATER :
							compare.getOperation() == Operation.GREATER_OR_EQUAL ? Type.GREATER_OR_EQUAL :
								compare.getOperation() == Operation.LESS ? Type.LESS : Type.LESS_OR_EQUAL);
		} else if (vaadinFilter instanceof com.vaadin.data.util.filter.Not) {
			com.vaadin.data.util.filter.Not not = (com.vaadin.data.util.filter.Not) vaadinFilter;
			return new Not(convertFilter(not.getFilter()));
		} else if (vaadinFilter instanceof com.vaadin.data.util.filter.IsNull) {
			com.vaadin.data.util.filter.IsNull isNull = (com.vaadin.data.util.filter.IsNull) vaadinFilter;
			return new IsNull(isNull.getPropertyId().toString());
		} else if (vaadinFilter instanceof SimpleStringFilter) {
			SimpleStringFilter ssFilter = (SimpleStringFilter) vaadinFilter;
			return new Like(ssFilter.getPropertyId().toString(), "%" + ssFilter.getFilterString() + "%", false);
		} else {
			return null;
		}
	}
	
	public static List<OrderBy> convertOrderBys(Object[] vaadinOrders, boolean... ascendings) {
		return convertOrderBys(Arrays.asList(vaadinOrders), ascendings);
	}
	
	public static List<OrderBy> convertOrderBys(Collection<Object> vaadinOrders, boolean[] ascendings) {
		int i = 0;
		final List<OrderBy> orders = new ArrayList<OrderBy>(vaadinOrders.size());
		for (Object vaadinOrder : vaadinOrders) {
			boolean ascending = (i < ascendings.length) ? ascendings[i] : true;
			orders.add(new OrderBy(vaadinOrder.toString(), ascending ? Order.ASC : Order.DESC));
			i++;
		}
		return orders;
	}
}
