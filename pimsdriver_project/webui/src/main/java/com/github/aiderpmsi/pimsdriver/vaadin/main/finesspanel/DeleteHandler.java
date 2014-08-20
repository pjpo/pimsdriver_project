package com.github.aiderpmsi.pimsdriver.vaadin.main.finesspanel;

import javax.naming.InitialContext;

import com.github.aiderpmsi.pimsdriver.vaadin.utils.aop.ActionEncloser;
import com.github.pjpo.pimsdriver.pimsstore.ejb.Store;
import com.github.pjpo.pimsdriver.pimsstore.entities.UploadedPmsi;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.event.Action;
import com.vaadin.ui.Notification;

public class DeleteHandler implements Action.Handler {
	
	private static final long serialVersionUID = 1L;

	static final Action ACTION_DELETE = new Action("delete");
	static final Action[] ACTIONS = new Action[] { ACTION_DELETE };
	static final Action[] NO_ACTION = new Action[] {};
	
	private final HierarchicalContainer hc;
	
	private final FinessComponent fp;
	
	public DeleteHandler(final HierarchicalContainer hc, final FinessComponent fp) {
		this.hc = hc;
		this.fp = fp;
	}
	
	public Action[] getActions(Object target, Object sender) {
		// WHEN TARGET IS ONE ITEM ID, CHECK IF IT IS AT DEPTH 3
		if (target != null) {
			Integer depth = (Integer) hc.getContainerProperty(target, "depth").getValue();
			if (depth == 3) {
				return ACTIONS;
			}
		}
		return NO_ACTION;
	}
	
	public void handleAction(Action action, Object sender, Object target) {
		// CHECK THAT THIS TARGET HAS DEPTH 3
		if (target != null) {
			final Integer depth = (Integer) hc.getContainerProperty(target, "depth").getValue();
			if (depth == 3) {
				// GETS THE ASSOCIATED MODEL
				UploadedPmsi model = (UploadedPmsi) hc.getContainerProperty(target, "model").getValue();
				// DELETES THE UPLOAD
				final Store store = (Store) ActionEncloser.execute((throwable) -> "EJB store not found",
						() -> new InitialContext().lookup("java:global/business/pimsstore-0.0.1-SNAPSHOT/StoreBean!com.github.pjpo.pimsdriver.pimsstore.ejb.Store"));
				if (store.deletePmsiUpload(model.getRecordid()) == false) {
					// ERROR WHILE DELETING UPLOAD
					Notification.show("Erreur de suppression du pmsi", Notification.Type.WARNING_MESSAGE);
				} else {
					// GETS PARENT
					Object parentId = hc.getParent(target);
					// REMOVE THIS ITEM
					fp.removeItem(target);
					// REMOVE PARENT ITEMS IF NO CHILDREN...
					removeRecursively(parentId);
				}
			}
		}
	}
	
	private void removeRecursively(Object itemId) {
		// REMOVE THIS ITEM IF DEPTH IS MORE THAN 0 AND HAS NO CHILDREN
		final Integer depth = (Integer) hc.getContainerProperty(itemId, "depth").getValue();
		if (depth != 0 && hc.getChildren(itemId).size() == 0) {
			// GETS PARENT
			Object parentId = hc.getParent(itemId);
			// REMOVE THIS ITEM
			fp.removeItem(itemId);
			// REMOVE PARENT ITEMS IF NO CHILDREN...
			removeRecursively(parentId);
		}
	}
}
