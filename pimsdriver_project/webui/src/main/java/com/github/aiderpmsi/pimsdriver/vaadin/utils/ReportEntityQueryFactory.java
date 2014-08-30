package com.github.aiderpmsi.pimsdriver.vaadin.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.vaadin.addons.lazyquerycontainer.Query;
import org.vaadin.addons.lazyquerycontainer.QueryDefinition;
import org.vaadin.addons.lazyquerycontainer.QueryFactory;

import com.github.aiderpmsi.pimsdriver.db.vaadin.query.VaadinToCommonsPredicates;
import com.github.pjpo.commons.predicates.Filter;
import com.github.pjpo.commons.predicates.OrderBy;
import com.github.pjpo.pimsdriver.pimsstore.ejb.Report;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItem;

public class ReportEntityQueryFactory<T> implements QueryFactory {

	private static final Logger LOGGER = Logger.getLogger(ReportEntityQueryFactory.class.toString());

	final private Report report;
	
	final private Filter rootFilter;
	
	final private Class<T> clazz;
	
	public ReportEntityQueryFactory(
			final Class<T> clazz,
			final Report report,
			final Filter rootFilter) {
		this.report = report;
		this.rootFilter = rootFilter;
		this.clazz = clazz;
	}
	
	@Override
	public Query constructQuery(final QueryDefinition qd) {
		final List<Filter> filters = VaadinToCommonsPredicates.convertFilters(qd.getFilters());
		filters.add(rootFilter);
		List<OrderBy> orders = VaadinToCommonsPredicates.convertOrderBys(
				qd.getSortablePropertyIds(), qd.getSortPropertyAscendingStates());
		
		return new Query() {
			@Override public int size() {
				return report.getSize(clazz, filters).intValue();
			}
			
			@Override public void saveItems(List<Item> arg0, List<Item> arg1, List<Item> arg2) {
				throw new UnsupportedOperationException();
			}
			
			@Override
			public List<Item> loadItems(final int first, final int count) {
				final List<Item> items = new ArrayList<Item>(count);
				for (T bean : report.getList(clazz, filters, orders, first, count)) {
					items.add(new BeanItem<T>(bean));
				}
				return items;
			}
			
			@Override public boolean deleteAllItems() {
				throw new UnsupportedOperationException();
			}
			
			@Override public Item constructItem() {
				try {
					return new BeanItem<T>(clazz.newInstance());
				} catch (Exception e) {
					LOGGER.log(Level.WARNING, "Creation of bean impossible, bean has no default conctructor", e);
					return null;
				}
			}
		};

	}

}
