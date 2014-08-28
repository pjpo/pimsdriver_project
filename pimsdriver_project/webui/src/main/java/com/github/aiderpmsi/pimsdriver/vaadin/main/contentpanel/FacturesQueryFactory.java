package com.github.aiderpmsi.pimsdriver.vaadin.main.contentpanel;

import java.util.ArrayList;
import java.util.List;

import org.vaadin.addons.lazyquerycontainer.Query;
import org.vaadin.addons.lazyquerycontainer.QueryDefinition;
import org.vaadin.addons.lazyquerycontainer.QueryFactory;

import com.github.aiderpmsi.pimsdriver.db.vaadin.query.VaadinToCommonsPredicates;
import com.github.pjpo.commons.predicates.Filter;
import com.github.pjpo.commons.predicates.OrderBy;
import com.github.pjpo.pimsdriver.pimsstore.ejb.Report;
import com.github.pjpo.pimsdriver.pimsstore.entities.RsfA;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItem;

public class FacturesQueryFactory implements QueryFactory {
	
	final private Report report;
	
	final private Filter rootFilter;
	
	public FacturesQueryFactory(
			final Report report,
			final Filter rootFilter) {
		this.report = report;
		this.rootFilter = rootFilter;
	}
	
	@Override
	public Query constructQuery(final QueryDefinition qd) {
		final List<Filter> filters = VaadinToCommonsPredicates.convertFilters(qd.getFilters());
		filters.add(rootFilter);
		List<OrderBy> orders = VaadinToCommonsPredicates.convertOrderBys(
				qd.getSortablePropertyIds(), qd.getSortPropertyAscendingStates());
		
		return new Query() {
			@Override public int size() {
				return report.getRsfASize(filters).intValue();
			}
			
			@Override public void saveItems(List<Item> arg0, List<Item> arg1, List<Item> arg2) {
				throw new UnsupportedOperationException();
			}
			
			@Override
			public List<Item> loadItems(final int first, final int count) {
				final List<Item> items = new ArrayList<Item>(count);
				for (RsfA bean : report.getRsfAList(filters, orders, first, count)) {
					items.add(new BeanItem<RsfA>(bean));
				}
				return items;
			}
			
			@Override public boolean deleteAllItems() {
				throw new UnsupportedOperationException();
			}
			
			@Override public Item constructItem() {
				return new BeanItem<RsfA>(new RsfA());
			}
		};

	}

}
