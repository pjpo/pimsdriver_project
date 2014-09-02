package com.github.aiderpmsi.pimsdriver.vaadin.utils;

import com.vaadin.ui.Table.Align;

public class LazyColumnType {

	private final String id;
	
	private final Class<?> clazz;
	
	private final String name;
	
	private final Align align;
	
	private final boolean sortable;

	public LazyColumnType(String id, Class<?> clazz, String name, Align align,
			boolean sortable) {
		super();
		this.id = id;
		this.clazz = clazz;
		this.name = name;
		this.align = align;
		this.sortable = sortable;
	}

	public String getId() {
		return id;
	}

	public Class<?> getClazz() {
		return clazz;
	}

	public String getName() {
		return name;
	}

	public Align getAlign() {
		return align;
	}

	public boolean isSortable() {
		return sortable;
	}

	
}
