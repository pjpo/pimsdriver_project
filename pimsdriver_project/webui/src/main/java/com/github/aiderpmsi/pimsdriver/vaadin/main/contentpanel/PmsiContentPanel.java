package com.github.aiderpmsi.pimsdriver.vaadin.main.contentpanel;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map.Entry;

import javax.naming.InitialContext;

import org.vaadin.addons.lazyquerycontainer.LazyQueryContainer;
import org.vaadin.addons.lazyquerycontainer.LazyQueryDefinition;

import com.github.aiderpmsi.pimsdriver.vaadin.main.MenuBar;
import com.github.aiderpmsi.pimsdriver.vaadin.main.SplitPanel;
import com.github.aiderpmsi.pimsdriver.vaadin.main.contentpanel.pmsidetails.PmsiDetailsWindow;
import com.github.aiderpmsi.pimsdriver.vaadin.main.contentpanel.pmsisource.PmsiSourceWindow;
import com.github.aiderpmsi.pimsdriver.vaadin.utils.LazyColumnType;
import com.github.aiderpmsi.pimsdriver.vaadin.utils.LazyTable;
import com.github.aiderpmsi.pimsdriver.vaadin.utils.aop.ActionEncloser;
import com.github.aiderpmsi.pimsdriver.vaadin.utils.aop.ActionHandlerEncloser;
import com.github.pjpo.commons.predicates.And;
import com.github.pjpo.commons.predicates.Compare;
import com.github.pjpo.commons.predicates.Compare.Type;
import com.github.pjpo.commons.predicates.Filter;
import com.github.pjpo.pimsdriver.pimsstore.ejb.Report;
import com.github.pjpo.pimsdriver.pimsstore.entities.RsfA;
import com.github.pjpo.pimsdriver.pimsstore.entities.UploadedPmsi;
import com.vaadin.event.Action;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.Align;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class PmsiContentPanel extends VerticalLayout {

	/** header layout */
	private final VerticalLayout header = new VerticalLayout();
	
	/** body layout */
	private final VerticalLayout body = new VerticalLayout();
	
	private final SplitPanel splitPanel;
	
	private final Report report = (Report) ActionEncloser.execute((throwable) -> "EJB report not found",
			() -> new InitialContext().lookup("java:global/business/pimsstore-0.0.1-SNAPSHOT/ReportBean!com.github.pjpo.pimsdriver.pimsstore.ejb.Report"));

	/** Filter for the selected upload */
	private Filter rootFilter;
	
	private final static SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	
	private final static DecimalFormat moneyFormat =
			new DecimalFormat("+#,##0.00;-#,##0.00", new DecimalFormatSymbols(Locale.FRANCE));

	public PmsiContentPanel(SplitPanel splitPanel) {
		super();
		addStyleName("pims-contentpanel");
	
		// SETS LAYOUT HIERARCHY
		addComponent(header);
		addComponent(body);
		
		// SETS PARENT
		this.splitPanel = splitPanel;
	}
	
	public void setUpload(final UploadedPmsi model) {
		// FIRST, CLEANUP BODY AND HEADER
		body.removeAllComponents();
		header.removeAllComponents();

		// IF STATUS IS NOT NULL, ADD HEADER CONTENT AND SETS FILTER
		if (model != null)  {
			// SETS FILTER
			 rootFilter = new And(new Compare<Long>("uploadRecordId", model.getRecordid(), Type.EQUAL));
			 
			// READ OVERVIEW FOR BOTH RSF AND RSS
			final LinkedHashMap<String, LinkedHashMap<String, Long>> overviews = new LinkedHashMap<>();
			overviews.put("RSF", report.readPmsiOverview(model, "rsfheader"));
			overviews.put("RSS", report.readPmsiOverview(model, "rssheader"));

			// CREATES THE HEADER INFORMATIONS
			fillContentHeader(overviews);
		}
	}
	
	public void show(MenuBar.MenuBarSelected type, UploadedPmsi model) {
		// REMOVE ALL COMPONENTS OF BODY
		body.removeAllComponents();

		// CREATES THE CORRESPONDING TABLE
		switch (type) {
		case sejours:
			body.addComponent(createSejoursTable(type, model));
			break;
		case factures:
			body.addComponent(createFactTable(type, model));
			break;
		}
	}
	
	private Table createSejoursTable(final MenuBar.MenuBarSelected type, final UploadedPmsi model) {
        // RSS MAIN CONTAINER
        final LazyQueryContainer datasContainer = new LazyQueryContainer(
        		new LazyQueryDefinition(false, 1000, "pmel_id"),
        		new SejoursQueryFactory(report, rootFilter));
		
        // COLUMNS DEFINITIONS
        final LazyColumnType[] cols = new LazyColumnType[] {
        		new LazyColumnType("recordId", Long.class, null, null),
        		new LazyColumnType("lineInPmsi", Long.class, "Ligne", Align.RIGHT),
        		new LazyColumnType("numrss", String.class, "Rss", Align.LEFT),
        		new LazyColumnType("numlocalsejour", String.class, "Séjour", Align.LEFT),
        		new LazyColumnType("numrum", String.class, "Rum", Align.LEFT),
        		new LazyColumnType("numunitemedicale", String.class, "Unité", Align.LEFT),
        		new LazyColumnType("ghm", String.class, "GHM", Align.CENTER),
        		new LazyColumnType("ghmcorrige", String.class, "GHM corrigé", Align.CENTER),
        		new LazyColumnType("dp", String.class, "DP", Align.CENTER),
        		new LazyColumnType("dr", String.class, "DR", Align.CENTER),
        		new LazyColumnType("nbseances", String.class, "Séances", Align.RIGHT),
        		new LazyColumnType("dateentree", String.class, "Entrée", Align.CENTER),
        		new LazyColumnType("datesortie", String.class, "Sortie", Align.CENTER)
        };

        final LazyTable table = new LazyTable(cols, Locale.FRANCE, datasContainer);
        table.addFormatter("dateentree", dateFormat);
        table.addFormatter("datesortie", dateFormat);
        
        table.setSelectable(true);
        table.setSizeFull();
        table.setCaption("Séjours");

        return table;
	}

	private Table createFactTable(final MenuBar.MenuBarSelected type, final UploadedPmsi model) {

		// RSFA CONTAINER
        final LazyQueryContainer datasContainer = new LazyQueryContainer(
        		new LazyQueryDefinition(false, 1000, "recordId"),
        		new FacturesQueryFactory(report, rootFilter));

        // COLUMNS DEFINITIONS
        final LazyColumnType[] cols = new LazyColumnType[] {
        		new LazyColumnType("recordId", Long.class, null, null),
        		new LazyColumnType("lineInPmsi", Long.class, "Ligne", Align.RIGHT),
        		new LazyColumnType("numfacture", String.class, "Facture", Align.LEFT),
        		new LazyColumnType("numrss", String.class, "Rss", Align.LEFT),
        		new LazyColumnType("codess", String.class, "Code Sécu", Align.LEFT),
        		new LazyColumnType("sexe", String.class, "Sexe", Align.CENTER),
        		new LazyColumnType("datenaissance", Date.class, "Naissance", Align.CENTER),
        		new LazyColumnType("dateentree", Date.class, "Entrée", Align.CENTER),
        		new LazyColumnType("datesortie", Date.class, "Sortie", Align.CENTER),
        		new LazyColumnType("totalfacturehonoraire", BigDecimal.class, "Honoraires", Align.RIGHT),
        		new LazyColumnType("totalfactureph", BigDecimal.class, "Prestations", Align.RIGHT),
        		new LazyColumnType("etatliquidation", String.class, "Liquidation", Align.RIGHT)
        };
        
        final LazyTable table = new LazyTable(cols, Locale.FRANCE, datasContainer);
        table.addFormatter("dateentree", dateFormat);
        table.addFormatter("datesortie", dateFormat);
        table.addFormatter("totalfacturehonoraire", moneyFormat);
        table.addFormatter("totalfactureph", moneyFormat);
        
        
        table.setSelectable(true);
        table.setSizeFull();
        table.setCaption("Factures");

        // FILLS THE SUMMARY
        final RsfA summary = report.getRsfASummary(Arrays.asList(rootFilter));
        
        table.setFooterVisible(true);
        table.setColumnFooter("totalfacturehonoraire",  moneyFormat.format(summary.getTotalfacturehonoraire()));
        table.setColumnFooter("totalfactureph", moneyFormat.format(summary.getTotalfactureph()));
        
        table.addActionHandler(
        		new ActionHandlerEncloser(ACTIONS[0], (action, sender, target) -> {
        			final Long pmel_position = (Long) datasContainer.getContainerProperty(target, "pmel_position").getValue();
    				String numfacture = (String) datasContainer.getContainerProperty(target, "numfacture").getValue();
        			UI.getCurrent().addWindow(new PmsiDetailsWindow(model.getRecordid(), pmel_position, type, numfacture, getSplitPanel().getRootWindow().getMainApplication().getServletContext()));
        		}));
        table.addActionHandler(
        		new ActionHandlerEncloser(ACTIONS[1], (action, sender, target) -> {
        			final Long pmel_position = (Long) datasContainer.getContainerProperty(target, "pmel_position").getValue();
    				String numfacture = (String) datasContainer.getContainerProperty(target, "numfacture").getValue();
        			UI.getCurrent().addWindow(new PmsiSourceWindow(model.getRecordid(), pmel_position, type, numfacture, getSplitPanel().getRootWindow().getMainApplication().getServletContext()));
        		}));
        return table;
	}
	
	private void fillContentHeader(final LinkedHashMap<String, LinkedHashMap<String, Long>> overviews) {

		for (final Entry<String, LinkedHashMap<String, Long>> overview : overviews.entrySet()) {
			final Layout layout = createContentHeader(
					overview.getKey(), overview.getValue());
			header.addComponent(layout);
		}

	}
	
	private Layout createContentHeader(final String header, final LinkedHashMap<String, Long> entries) {
		final HorizontalLayout layout = new HorizontalLayout();
		layout.addStyleName("pims-contentpanel-header-layout");

		// TITLE
		final Label title = new Label(header);
		title.addStyleName("pims-contentpanel-header-label");
		layout.addComponent(title);
		
		// CONTENT
		final CssLayout contentLayout = new CssLayout();
		contentLayout.addStyleName("pims-contentpanel-header-content-layout");
		for (final Entry<String, Long> entry : entries.entrySet()) {
			Label label = new Label(entry.getKey() + " : " + Long.toString(entry.getValue()));
			label.setSizeUndefined();
			label.addStyleName("pims-contentpanel-header-content-label");
			contentLayout.addComponent(label);
		}
		layout.addComponent(contentLayout);
		return layout;
	}

	public SplitPanel getSplitPanel() {
		return splitPanel;
	}

	private static final Action[] ACTIONS = new Action[] {new Action("détails"), new Action("source")};

}
