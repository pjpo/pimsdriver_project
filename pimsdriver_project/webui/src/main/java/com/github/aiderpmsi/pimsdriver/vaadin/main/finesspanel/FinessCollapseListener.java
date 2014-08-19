package com.github.aiderpmsi.pimsdriver.vaadin.main.finesspanel;

import java.util.Collection;

import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.ui.Tree.CollapseEvent;
import com.vaadin.ui.Tree;

@SuppressWarnings("serial")
public class FinessCollapseListener implements Tree.CollapseListener {

	private final HierarchicalContainer hc;
	
	public FinessCollapseListener(final HierarchicalContainer hc) {
		this.hc = hc;
	}

	@Override
	public synchronized void nodeCollapse(final CollapseEvent event) {
		// REMOVE ALL CHILDREN OF THIS COLLAPSING ITEM IF NOT NULL
		@SuppressWarnings("unchecked")
		final Collection<Object> childrenCollection = (Collection<Object>) hc.getChildren(event.getItemId());
		if (childrenCollection != null) {
			for (final Object child : childrenCollection.toArray()) {
				hc.removeItemRecursively(child);
			}
		}
	}

}
