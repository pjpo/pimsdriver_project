package com.github.aiderpmsi.pimsdriver.vaadin.main.finesspanel;

import com.github.aiderpmsi.pimsdriver.vaadin.main.RootWindow;
import com.github.pjpo.pimsdriver.pimsstore.entities.UploadedPmsi;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.event.ItemClickEvent;

@SuppressWarnings("serial")
public class ItemClickListener implements ItemClickEvent.ItemClickListener {

	private final RootWindow rootWindow;
	
	private final HierarchicalContainer hc;
		
	public ItemClickListener(final RootWindow rootWindow, final HierarchicalContainer hc) {
		this.rootWindow = rootWindow;
		this.hc = hc;
	}

	@Override
	public void itemClick(ItemClickEvent event) {

		// GETS THE EVENT NODE DEPTH
		Integer eventDepth =
				(Integer) hc.getContainerProperty(
						event.getItemId(), "depth").getValue();

		// DEPTH AT 3 MEANS AN UPLOAD HAS BEEN SELECTED
		if (eventDepth == 3) {
			//  PREVENT GUIUI THAT AN UPLOAD HAS BEEN SELECTED
			rootWindow.setUploadSelected((UploadedPmsi) hc.getContainerProperty(event.getItemId(), "model").getValue());
		}
		// SOMETHING ELSE HAS BEEN SELECTED, PREVENT THE MAIN WINDOW
		else {
			rootWindow.setUploadSelected(null);
		}
		
	}

}
