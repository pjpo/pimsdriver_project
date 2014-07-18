package com.github.aiderpmsi.pimsdriver.vaadin.upload;

import javax.servlet.ServletContext;

import com.github.aiderpmsi.pimsdriver.vaadin.utils.FileUploader;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Window;

public class UploadWindow extends Window {

	private static final long serialVersionUID = -2583394688969956613L;
	
	/** Window Layout */
	private final CssLayout layout = new CssLayout();

	/** Rsf Layout */
	private final CssLayout rsfLayout = new CssLayout();
	
	/** Rsf File Picker */
	private final Upload rsfFilePicker;
	
	/** Rsf Progress Bar */
	private final ProgressBar rsfProgressBar;
	
	/** Rss Layout */
	private final CssLayout rssLayout = new CssLayout();
	
	/** Rss File Picker */
	private final Upload rssFilePicker;

	/** Rss Progress Bar */
	private final ProgressBar rssProgressBar;

	/** Button layout */
	private final CssLayout buttonLayout = new CssLayout();
	
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
        layout.setStyleName("pims-upload-vertical-layout");
        center();

        // SELECT LAYOUT
        setContent(layout);
        
        // ADD RSF FILE PICKER
        rsfFilePicker = new Upload("RSF : ", new FileUploader("rsf", this));
        rsfFilePicker.setImmediate(true);
        rsfFilePicker.setButtonCaption("Téléverser");
        rsfLayout.addComponent(rsfFilePicker);

        // ADD RSF PROGRESS BAR
        rsfProgressBar = new ProgressBar();
        rsfFilePicker.addProgressListener(
        		(readBytes, contentLength) -> rsfProgressBar.setValue((float)readBytes / (float)contentLength));
        rsfLayout.addComponent(rsfProgressBar);
        layout.addComponent(rsfLayout);
        
        // ADD RSS FILE PICKER
        rssFilePicker = new Upload("RSF", new FileUploader("rsf", this));
        rssFilePicker.setImmediate(true);
        rssFilePicker.setButtonCaption("Téléverser");
        rssLayout.addComponent(rssFilePicker);
        
        // ADD RSF PROGRESS BAR
        rssProgressBar = new ProgressBar();
        rssFilePicker.addProgressListener(
        		(readBytes, contentLength) -> rssProgressBar.setValue((float)readBytes / (float)contentLength));
        rssLayout.addComponent(rssProgressBar);
        layout.addComponent(rssLayout);
        
        // ADD OK BUTTON
        okButton = new Button("Valider");
        okButton.addClickListener(
        		(clickEvent) -> upload());
        buttonLayout.addComponent(okButton);
        layout.addComponent(buttonLayout);
        
        addCloseListener((event) -> closeWindow());
	}

    private void closeWindow() {
    	((FileUploader) rsfFilePicker.getReceiver()).close();        	
    	((FileUploader) rssFilePicker.getReceiver()).close();
    }
    
    private void upload() {
    	// TODO : DATABASE ACTION
    }
}
