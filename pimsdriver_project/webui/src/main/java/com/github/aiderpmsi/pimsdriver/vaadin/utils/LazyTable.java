package com.github.aiderpmsi.pimsdriver.vaadin.utils;

import java.text.Format;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Locale;

import org.vaadin.addons.lazyquerycontainer.LazyQueryContainer;

import com.vaadin.data.Property;
import com.vaadin.ui.Table;

@SuppressWarnings("serial")
public class LazyTable extends Table {
	
	private final HashMap<Object, Format> customFormats = new HashMap<>();
	
	public LazyTable(final LazyColumnType[] columns, final Locale locale, final LazyQueryContainer c) {
		setLocale(locale);
		
		final LinkedList<String> visibleColumns = new LinkedList<>();
		final LinkedList<String> columnHeaders = new LinkedList<>();
		final LinkedList<Align> columnAlignements = new LinkedList<>();
		
		for (final LazyColumnType column : columns) {
			c.addContainerProperty(column.getId(), column.getClazz(), null, true, column.isSortable());
			if (column.getName() != null) {
				visibleColumns.add(column.getId());
				columnHeaders.add(column.getName());
				columnAlignements.add(column.getAlign());
			}
		}

		setContainerDataSource(c);
		setVisibleColumns(visibleColumns.toArray());
		setColumnHeaders(columnHeaders.toArray(new String[columnHeaders.size()]));
		setColumnAlignments(columnAlignements.toArray(new Align[columnAlignements.size()]));
	}

	public void addFormatter(final Object colId, final Format format) {
		customFormats.put(colId, format);
	}
	
	@Override
    protected String formatPropertyValue(final Object rowId, final Object colId,
            final Property<?> property) {
		final Object v = property.getValue();
        if (customFormats.containsKey(colId)) {
        	final Format format = customFormats.get(colId);
        	return format.format(v);
        } else {
        	return super.formatPropertyValue(rowId, colId, property);
        }
    }
	
	

}
