package com.github.aiderpmsi.pimsdriver.vaadin.main;

import com.github.pjpo.pimsdriver.pimsstore.entities.UploadedPmsi;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;

@SuppressWarnings("serial")
public class RootWindow extends CssLayout {

	private final Label header;
	
	private final MenuBar menuBar;

	private final SplitPanel splitPanel;

	private final MainApplication mainApplication;

	public RootWindow(MainApplication mainApplication) {
		super();
		this.mainApplication = mainApplication;

		addStyleName("pims-main-layout");

		header = new Label("pimsdriver");
		header.addStyleName("pims-main-header");
		addComponent(header);
		
		menuBar = new MenuBar(this);
		addComponent(menuBar);

		splitPanel = new SplitPanel(this);
		addComponent(splitPanel);
	}
	
	public void setUploadSelected(final UploadedPmsi model) {
		splitPanel.getContentPanel().setUpload(model);
		menuBar.setUpload(model);
	}
	
	public void setMenuNavigationSelected(final UploadedPmsi model, final MenuBar.MenuBarSelected type) {
		splitPanel.getContentPanel().show(type, model);
	}

	public Label getHeader() {
		return header;
	}

	public MenuBar getMenuBar() {
		return menuBar;
	}

	public SplitPanel getSplitPanel() {
		return splitPanel;
	}

	public MainApplication getMainApplication() {
		return mainApplication;
	}
	
}
