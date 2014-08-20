package com.github.aiderpmsi.pimsdriver.vaadin.main.finesspanel;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.naming.InitialContext;

import com.github.aiderpmsi.pimsdriver.vaadin.main.finesspanel.FinessComponent.FinessContainerModel;
import com.github.aiderpmsi.pimsdriver.vaadin.utils.aop.ActionEncloser;
import com.github.pjpo.commons.predicates.And;
import com.github.pjpo.commons.predicates.Compare;
import com.github.pjpo.commons.predicates.Filter;
import com.github.pjpo.commons.predicates.Compare.Type;
import com.github.pjpo.commons.predicates.OrderBy;
import com.github.pjpo.commons.predicates.OrderBy.Order;
import com.github.pjpo.pimsdriver.pimsstore.ejb.Navigation;
import com.github.pjpo.pimsdriver.pimsstore.entities.UploadedPmsi;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Tree;
import com.vaadin.ui.Tree.ExpandEvent;

@SuppressWarnings("serial")
public class FinessExpandListener implements Tree.ExpandListener {

	private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/YYYY HH:mm:ss");
	
	private final HierarchicalContainer hc;
	
	private final FinessComponent fp;

	private final Navigation navigation;
	
	public FinessExpandListener(final HierarchicalContainer hc, final FinessComponent fp) {
		this.hc = hc;
		this.fp = fp;
		this.navigation = (Navigation) ActionEncloser.execute((throwable) -> "EJB navigation not found",
				() -> new InitialContext().lookup("java:global/business/pimsstore-0.0.1-SNAPSHOT/NavigationBean!com.github.pjpo.pimsdriver.pimsstore.ejb.Navigation"));
	}
	
	@Override
	public synchronized void nodeExpand(final ExpandEvent event) {

		// GETS CONTENT OF THIS ITEM
		final Integer depth = (Integer) hc.getContainerProperty(event.getItemId(), "depth").getValue();
		final String finess = (String) hc.getContainerProperty(event.getItemId(), "finess").getValue();
		final LocalDate pmsiDate = (LocalDate) hc.getContainerProperty(event.getItemId(), "pmsiDate").getValue();
		
		ActionEncloser.executeVoid( (exception) -> "Impossible de charger l'arbre des téléversements",
				() -> {
				switch (depth) {
				case 0:
					// OPEN A ROOT NODE : SHOW LIST OF FINESSES
					createFinessNodes(hc, event.getItemId()); break;
				case 1:
					// OPEN A FINESS NODE : SHOW PMSI DATES
					createPmsiDateNodes(hc, event.getItemId(), finess); break;
				case 2:
					// OPEN A PMSI DATE NODE : SHOW UPLOAD DATES
					createUploadNodes(hc, event.getItemId(), finess, pmsiDate); break;
				default:
					throw new Exception();
				}});
	}
	
	private void createFinessNodes(final HierarchicalContainer hc, final Object itemId) throws Exception {
		final List<String> finesses = navigation.getFinesses();
		if (finesses == null) {
			throw new Exception();
		} else {
			final FinessContainerModel containerModel = new FinessContainerModel(null, null, new Integer(1), null, null);
			for (final String finess : navigation.getFinesses()) {
				// CREATES THE NODE
				containerModel.setCaption(finess);
				containerModel.setFiness(finess);
				final Object newItemId = fp.createContainerItemNode(hc, containerModel);
				// ATTACHES THE NODE TO ITS PARENT
				hc.setParent(newItemId, itemId);
			}
		}
	}
	
	private void createPmsiDateNodes(final HierarchicalContainer hc, final Object itemId, final String finess) throws Exception {
		final List<LocalDate> pmsiDates = navigation.getPmsiDates(finess);
		if (pmsiDates == null) {
			throw new Exception();
		} else {
			final FinessContainerModel containerModel = new FinessContainerModel(null, finess, new Integer(2), null, null);
			
			if (pmsiDates.size() == 0) {
				// IF THERE IS NO DATE FOR THIS FINESS, IT MEANS THIS ITEM DOESN'T EXIST ANYMORE, REMOVE IT
				fp.removeContainerItem(hc, itemId);
				
				// SHOW THAT THIS ITEM DOESN'T EXIST ANYMORE
				Notification.show("Le finess sélectionné n'existe plus", Notification.Type.WARNING_MESSAGE);
			} else {
				// SOME UPLOADS EXIST, CREATE THE CORRESPONDING NODES
				for (final LocalDate pmsiDate : pmsiDates) {
					// SETS THE ENTRY ELEMENTS
					containerModel.setCaption(pmsiDate.getYear() + " M" + pmsiDate.getMonthValue());
					containerModel.setPmsiDate(pmsiDate);
					// CREATES THE NODE
					final Object newItemId = fp.createContainerItemNode(hc, containerModel);
					// ATTACHES THE NODE
					hc.setParent(newItemId, itemId);
				}
			}
		}
	}

	public void createUploadNodes(final HierarchicalContainer hc, final Object itemId,
			final String finess, final LocalDate pmsiDate) throws Exception {
		final FinessContainerModel containerModel = new FinessContainerModel(null, finess, new Integer(3), pmsiDate, null);

		// CREATES THE QUERY FILTER
		final List<Filter> filters = new ArrayList<>(1);
		filters.add(new And(
				new Compare<String>("finess", String.class, finess, Type.EQUAL),
				new Compare<Integer>("pmsiYear", Integer.class, pmsiDate.getYear(), Type.EQUAL),
				new Compare<Integer>("pmsiMonth", Integer.class, pmsiDate.getMonthValue(), Type.EQUAL)));
		
		// CREATE THE QUERY ORDER BY
		final List<OrderBy> orderBys = new ArrayList<>(1);
		orderBys.add(new OrderBy("dateenvoi", Order.ASC));

		// LOAD THE ITEMS
		final List<UploadedPmsi> ups = navigation.getUploadedPmsi(filters, orderBys, null, null);

		// IF UPS IS NULL, EXCEPTION!
		if (ups == null) {
			throw new Exception();
		}
		// IF WE HAVE NO RESULT, IT MEANS THIS ITEM DOESN'T EXIST ANYMORE, REMOVE IT FROM THE TREE
		else if (ups.size() == 0) {
			fp.removeContainerItem(hc, itemId);
			// SHOW THAT THIS ITEM DOESN'T EXIST ANYMORE
			Notification.show("L'élément sélectionné n'existe plus", Notification.Type.WARNING_MESSAGE);
		} else {
			// WE HAVE TO ADD THESE ITEMS TO THE TREE
			for (final UploadedPmsi model : ups) {
				// SETS THE ENTRY ELEMENTS
				containerModel.setCaption(sdf.format(model.dateenvoi));
				containerModel.setModel(model);
				// CREATES THE NODE
				final Object newItemId = fp.createContainerItemNode(hc, containerModel);
				// ATTACHES THE NODE
				hc.setParent(newItemId, itemId);
				hc.setChildrenAllowed(newItemId, false);
			}
		}
	}
}