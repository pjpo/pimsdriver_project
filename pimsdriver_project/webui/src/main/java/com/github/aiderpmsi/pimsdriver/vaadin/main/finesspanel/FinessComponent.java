package com.github.aiderpmsi.pimsdriver.vaadin.main.finesspanel;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;

import com.github.aiderpmsi.pimsdriver.vaadin.main.SplitPanel;
import com.github.pjpo.pimsdriver.pimsstore.ejb.Navigation;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.ui.Tree;

public class FinessComponent extends Tree {

	/** Generated serial id */
	private static final long serialVersionUID = 5192397393504372354L;
	
	private final SplitPanel splitPanel;
	
	@SuppressWarnings("serial")
	private final HierarchicalContainer hierarchicalContainer = new HierarchicalContainer() {{
		// SETS THE ELEMENTS IN THE CONTAINER
		addContainerProperty("caption", String.class, "");
		addContainerProperty("finess", String.class, null);
		addContainerProperty("depth", Integer.class, null);
		addContainerProperty("pmsidate", LocalDate.class, null);
		addContainerProperty("model", Navigation.UploadedPmsi.class, null);
		// CREATES ROOT NODE
		createContainerItemNode(this, new FinessContainerModel("finess", null, null, null, null));
	}};
		
	public FinessComponent(final SplitPanel splitPanel) {
		super();
		this.splitPanel = splitPanel;

		// SETS THE CONTAINER ORIGIN
		setContainerDataSource(hierarchicalContainer);
		setItemCaptionPropertyId("caption"); // PROPERTY ID USED AS CAPTION FOR EACH ELEMENT IN TREE 
		setImmediate(true); // MODIFICATIONS SHOULD BE SHOWED IMMEDIATELY
		
		// ADDS THE LISTENERS
		final ExpandListener el = new FinessExpandListener(hierarchicalContainer, this);
		final CollapseListener cl = new FinessCollapseListener(hierarchicalContainer);
		final ItemClickListener icl = new ItemClickListener(getSplitPanel().getRootWindow(), hierarchicalContainer);

		addExpandListener(el);
		addCollapseListener(cl);
		addItemClickListener(icl);

		// ADDS THE ACTION HANDLERS
		addActionHandler(new DeleteHandler(hierarchicalContainer, this));
	}

	@SuppressWarnings("unchecked")
	public final Object createContainerItemNode(final HierarchicalContainer hc, final FinessContainerModel fcm) {
		
		final Object itemId = hc.addItem();
		
		for (Method getter : fcm.getClass().getDeclaredMethods()) {
			final String getterName = getter.getName();
			if (getter.getParameterCount() == 0 && getterName.length() > 3 && getterName.startsWith("get")) {
				// WE ARE IN AN GETTER, RETRIEVE THE NAME PROPERTY
				final StringBuilder propertyNameBuilder = new StringBuilder(getterName.substring(3, 4).toLowerCase());
				propertyNameBuilder.append(getterName.substring(4));
				final String propertyName = propertyNameBuilder.toString(); 
				// SETS THE CORRESPONDING PROPERTY
				try {
					hc.getContainerProperty(itemId, propertyName).setValue(getter.invoke(fcm));
				} catch (IllegalAccessException | ReadOnlyException | IllegalArgumentException | InvocationTargetException e) {
					throw new IllegalArgumentException(e);
				}
			}
		}

		return itemId;
	}

	public SplitPanel getSplitPanel() {
		return splitPanel;
	}
	
	public void removeContainerItem(final HierarchicalContainer hc, final Object itemId) {

		// GETS THE PARENT
		final Object parentId = hc.getParent(itemId);

		// REMOVE THIS ITEM
		hc.removeItemRecursively(itemId);

		// RECURSIVELY REMOVE PARENT ITEM IF IT HAS NO CHILDREN (AND IT IS NOT ROOT)
		if (parentId != null && !hc.isRoot(parentId)) {
			if (hc.getChildren(parentId).size()  == 0) {
				removeContainerItem(hc, parentId);
				// CONTINUE RECURSION
			}
		} else {
			// STOP RECURSION
		}
	}

	public static class FinessContainerModel {
		private String caption = "";
		private String finess = null;
		private Integer depth = null;
		private LocalDate pmsiDate = null;
		private Navigation.UploadedPmsi model = null;
		public FinessContainerModel(final String caption, final String finess,
				final Integer depth, final LocalDate pmsiDate, 
				final Navigation.UploadedPmsi model) {
			this.caption = caption;
			this.finess = finess;
			this.depth = depth;
			this.pmsiDate = pmsiDate;
			this.model = model;
		}
		public String getCaption() {
			return caption;
		}
		public void setCaption(final String caption) {
			this.caption = caption;
		}
		public String getFiness() {
			return finess;
		}
		public void setFiness(final String finess) {
			this.finess = finess;
		}
		public Integer getDepth() {
			return depth;
		}
		public void setDepth(final Integer depth) {
			this.depth = depth;
		}
		public LocalDate getPmsiDate() {
			return pmsiDate;
		}
		public void setPmsiDate(final LocalDate pmsiDate) {
			this.pmsiDate = pmsiDate;
		}
		public Navigation.UploadedPmsi getModel() {
			return model;
		}
		public void setModel(final Navigation.UploadedPmsi model) {
			this.model = model;
		}
		
	}

}
