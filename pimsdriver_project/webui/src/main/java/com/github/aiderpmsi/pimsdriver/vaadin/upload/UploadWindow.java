package com.github.aiderpmsi.pimsdriver.vaadin.upload;

import javax.servlet.ServletContext;

import com.github.aiderpmsi.pimsdriver.vaadin.utils.FileUploader;
import com.vaadin.ui.Button;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class UploadWindow extends Window {

	private static final long serialVersionUID = -2583394688969956613L;
	
	/** Layout */
	private final VerticalLayout layout = new VerticalLayout();
	
	/** Rsf File Picker */
	private final Upload rsfFilePicker;
	
	/** Rsf Progress Bar */
	private final ProgressBar rsfProgressBar;
	
	/** Rss File Picker */
	private final Upload rssFilePicker;

	/** Rss Progress Bar */
	private final ProgressBar rssProgressBar;

	/** OK Button */
    private final Button okButton;

    public UploadWindow(ServletContext context) {
		// TITLE
		super("Ajouter un fichier Pmsi");
		
		// SET VISUAL ASPECT
        setWidth("650px");
        setClosable(true);
        setResizable(true);
        setModal(true);
        setStyleName("addpmsi");
        center();

        // SELECT LAYOUT
        setContent(layout);
        
        // ADD RSF FILE PICKER
        rsfFilePicker = new Upload("RSF", new FileUploader("rsf", this));
        rsfFilePicker.setImmediate(true);
        rsfFilePicker.setButtonCaption("Téléverser");
        layout.addComponent(rsfFilePicker);

        // ADD RSF PROGRESS BAR
        rsfProgressBar = new ProgressBar();
        rsfFilePicker.addProgressListener(
        		(readBytes, contentLength) -> rsfProgressBar.setValue((float)readBytes / (float)contentLength));
        layout.addComponent(rsfProgressBar);
        
        // ADD RSS FILE PICKER
        rssFilePicker = new Upload("RSF", new FileUploader("rsf", this));
        rssFilePicker.setImmediate(true);
        rssFilePicker.setButtonCaption("Téléverser");
        layout.addComponent(rssFilePicker);
        
        // ADD RSF PROGRESS BAR
        rssProgressBar = new ProgressBar();
        rssFilePicker.addProgressListener(
        		(readBytes, contentLength) -> rssProgressBar.setValue((float)readBytes / (float)contentLength));
        layout.addComponent(rssProgressBar);

        // ADD OK BUTTON
        okButton = new Button("Valider");
        okButton.addClickListener(
        		(clickEvent) -> upload());
        layout.addComponent(okButton);
        
	}

    private void upload() {
    	// TODO : DATABASE ACTION
    }
}
