package com.github.aiderpmsi.pimsdriver.vaadin.upload;

import java.io.Reader;
import java.time.LocalDate;
import java.util.Collection;
import java.util.concurrent.locks.ReentrantLock;

import javax.naming.InitialContext;
import javax.servlet.ServletContext;

import com.github.aiderpmsi.pimsdriver.vaadin.utils.aop.ActionEncloser;
import com.github.pjpo.pimsdriver.pimsstore.ejb.Store;
import com.github.pjpo.pimsdriver.processor.ejb.RsfParser;
import com.github.pjpo.pimsdriver.processor.ejb.RssParser;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Notification;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.TextField;
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
	
	/** Rsf Delete Image */
	private final Image rsfDeleteImage;
	
	/** Rss Layout */
	private final CssLayout rssLayout = new CssLayout();
	
	/** Rss File Picker */
	private final Upload rssFilePicker;

	/** Rss Progress Bar */
	private final ProgressBar rssProgressBar;

	/** Rss Delete Image */
	private final Image rssDeleteImage;

	/** Finess layout */
	private final CssLayout finessLayout = new CssLayout();

	/** Finess feedback info */
	private final TextField finessFeedBack;
	
	/** Finess Property */
	private String finess = null;
	
	/** Pmsi Date Layout */
	private final CssLayout datePmsiLayout = new CssLayout();
	
	/** Pmsi date feedback info */
	private final TextField datePmsiFeedBack;
	
	/** Pmsi Date */
	private LocalDate pmsiDate = null;
	
	/** Button layout */
	private final CssLayout buttonLayout = new CssLayout();
	
	/** OK Button */
    private final Button okButton;

    /** Prevent 2 files beeing uploaded together */
    private final ReentrantLock lock = new ReentrantLock(true);
    
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
        rsfFilePicker = new Upload("RSF : ", new RsfFileUploader("rsf", this, lock));
        rsfFilePicker.setImmediate(true);
        rsfFilePicker.setButtonCaption("Téléverser");
        rsfLayout.addComponent(rsfFilePicker);

        // ADD RSF PROGRESS BAR
        rsfProgressBar = new ProgressBar();
        rsfFilePicker.addProgressListener(
        		(readBytes, contentLength) -> rsfProgressBar.setValue((float)readBytes / (float)contentLength));
        rsfLayout.addComponent(rsfProgressBar);
        
        // ADD RSF DELETE ICON
        rsfDeleteImage = new Image(null, new ThemeResource("img/delete-button.png"));
        rsfDeleteImage.addStyleName("pims-upload-delete-image");
        rsfDeleteImage.addClickListener((click) -> remove((FileUploader<?>) rsfFilePicker.getReceiver(), rsfProgressBar));
        rsfLayout.addComponent(rsfDeleteImage);
        layout.addComponent(rsfLayout);
        
        // ADD RSS FILE PICKER
        rssFilePicker = new Upload("RSS", new RssFileUploader("rss", this, lock));
        rssFilePicker.setImmediate(true);
        rssFilePicker.setButtonCaption("Téléverser");
        rssLayout.addComponent(rssFilePicker);
        
        // ADD RSS PROGRESS BAR
        rssProgressBar = new ProgressBar();
        rssFilePicker.addProgressListener(
        		(readBytes, contentLength) -> rssProgressBar.setValue((float)readBytes / (float)contentLength));
        rssLayout.addComponent(rssProgressBar);

        // ADD RSS DELETE ICON
        rssDeleteImage = new Image(null, new ThemeResource("img/delete-button.png"));
        rssDeleteImage.addStyleName("pims-upload-delete-image");
        rssDeleteImage.addClickListener((click) -> remove((FileUploader<?>) rssFilePicker.getReceiver(), rssProgressBar));
        rssLayout.addComponent(rssDeleteImage);
        layout.addComponent(rssLayout);
        
        // ADD FINESS FEEDBACK TEXTFIELD
        finessFeedBack = new TextField("Finess :", "");
        finessFeedBack.setEnabled(false);
        finessLayout.addComponent(finessFeedBack);
        layout.addComponent(finessLayout);
        
        // ADD PMSI DATE FEEDBACK TEXTFIELD
        datePmsiFeedBack = new TextField("Date PMSI :", "");
        datePmsiFeedBack.setEnabled(false);
        datePmsiLayout.addComponent(datePmsiFeedBack);
        layout.addComponent(datePmsiLayout);

        // ADD OK BUTTON
        okButton = new Button("Valider");
        okButton.addClickListener((clickEvent) -> upload());
        buttonLayout.addComponent(okButton);
        layout.addComponent(buttonLayout);
        
        // UPLOAD FINISHED
        rsfFilePicker.addFinishedListener(
        		(event) -> pmsiUploaded((FileUploader<?>) rsfFilePicker.getReceiver(), rsfProgressBar));
        rssFilePicker.addFinishedListener(
        		(event) -> pmsiUploaded((FileUploader<?>) rssFilePicker.getReceiver(), rssProgressBar));
        
        // CLOSE WINDOW EVENT
        addCloseListener((event) -> closeWindow());
	}

	private void closeWindow() {
    	((FileUploader<?>) rsfFilePicker.getReceiver()).close();        	
    	((FileUploader<?>) rssFilePicker.getReceiver()).close();
    }
    
    /**
     * Called when an rsf has just been uploaded.
     * @param receiver
     * @param reference
     * @param receiverProgressBar
     */
    private void pmsiUploaded(final FileUploader<?> receiver, final ProgressBar receiverProgressBar) {
    	try {
	    	// 2 -WAIT UPLOAD FINISH
	    	final Collection<String> errors = receiver.getErrors();
			// 3 -VERIFY THAT THE UPLOAD SUCCEDED
	    	if (errors.size() > 0) {
	    		Notification.show("Mauvais fichier PMSI", Notification.Type.WARNING_MESSAGE);
	    		// REINIT UPLOAD BAR
	    		receiverProgressBar.setValue(0F);
	    	} else {
	    		// 4 - VERIFY THAT FINESSES MATCHES
	    		if (finess != null && !finess.equals(receiver.getFiness())) {
	        		// REINIT DOWNLOAD
	    			remove(receiver, receiverProgressBar);
	    			Notification.show("Finess RSF et RSS ne correspondent pas", Notification.Type.WARNING_MESSAGE);
	    		}
	    		// 5 - VERIFY THAT PMSI DATES MATCH
	    		if (pmsiDate != null && !pmsiDate.equals(receiver.getPmsiDate())) {
	    			// REINIT DOWNLOAD
	    			remove(receiver, receiverProgressBar);
	    			Notification.show("Date du pmsi RSF et RSS ne correspondent pas", Notification.Type.WARNING_MESSAGE);
	    		}
	    	}
			// UPDATES FINESS
			updateFiness();
			// UPDATES PMSI DATE
			updatePmsiDate();
    	} finally {
    		lock.unlock();
    	}
    }
    
    private void updateFiness() {
    	final FileUploader<?> fileUploaders[] = new FileUploader<?>[]
    			{(FileUploader<?>) rsfFilePicker.getReceiver(), (FileUploader<?>) rssFilePicker.getReceiver()};
    	String newFiness = null;
    	for (FileUploader<?> fu : fileUploaders) {
    		if (fu.getFiness() != null)
    			newFiness = fu.getFiness();
    	}
    	if (finess != newFiness) {
    		finess = newFiness;
    		finessFeedBack.setValue(finess == null ? "" : finess);
    	}
    }
    
    private void updatePmsiDate() {
    	final FileUploader<?> fileUploaders[] = new FileUploader<?>[]
    			{(FileUploader<?>) rsfFilePicker.getReceiver(), (FileUploader<?>) rssFilePicker.getReceiver()};
    	LocalDate newPmsiDate = null;
    	for (FileUploader<?> fu : fileUploaders) {
    		if (fu.getPmsiDate() != null)
    			newPmsiDate = fu.getPmsiDate();
    	}
    	if (pmsiDate != newPmsiDate) {
    		pmsiDate = newPmsiDate;
    		datePmsiFeedBack.setValue(pmsiDate == null ? "" : "M" + pmsiDate.getMonthValue() + " Y" + pmsiDate.getYear());
    	}
    }

    private void remove(final FileUploader<?> receiver, final ProgressBar progressBar) {
    	((FileUploader<?>) rsfFilePicker.getReceiver()).clean();
    	progressBar.setValue(0F);
    	updateFiness();
	}

    private void upload() {
    	final RsfFileUploader rsfFileUploader = (RsfFileUploader) rsfFilePicker.getReceiver();
    	final RssFileUploader rssFileUploader = (RssFileUploader) rssFilePicker.getReceiver();
    	if (rsfFileUploader.getFiness() == null) {
    		Notification.show("Au moins un fichier RSF doit être proposé", Notification.Type.WARNING_MESSAGE);
    	} else {
    		final Store store = (Store) ActionEncloser.execute((throwable) -> "EJB Store not found",
    				() -> new InitialContext().lookup("java:global/business/pimsstore-0.0.1-SNAPSHOT/StoreBean!com.github.pjpo.pimsdriver.pimsstore.ejb.Store"));
    		store.storePmsiFiles(
    				rsfFileUploader.getPmsiDate(),
    				rsfFileUploader.getFiness(),
    				rsfFileUploader.getVersion(),
    				rssFileUploader.getVersion(),
    				() -> rsfFileUploader.openResultReader(),
    				rssFileUploader.getFiness() != null ? () -> rssFileUploader.openResultReader() : null, 
    				rssFileUploader.getFiness() != null ? () -> rssFileUploader.openGroupsReader() : null);
    	}
    }
    
    @SuppressWarnings("serial")
	private static class RsfFileUploader extends FileUploader<RsfParser> {

    	public RsfFileUploader(final String type, final Window window, final ReentrantLock lock) {
    		super(lock);
    		setParser((RsfParser) ActionEncloser.execute((throwable) -> "EJB rsf processor not found", 
    				() ->  new InitialContext().lookup("java:global/business/processor-0.0.1-SNAPSHOT/RsfParserBean!com.github.pjpo.pimsdriver.processor.ejb.RsfParser")));
        }
        
    	public Reader openResultReader() {
    		return getParser().getReader();
    	}
    	
    }

    @SuppressWarnings("serial")
	public class RssFileUploader extends FileUploader<RssParser> {

    	public RssFileUploader(final String type, final Window window, final ReentrantLock lock) {
    		super(lock);
    		setParser((RssParser) ActionEncloser.execute((throwable) -> "EJB rss processor not found", 
    				() ->  new InitialContext().lookup("java:global/business/processor-0.0.1-SNAPSHOT/RssParserBean!com.github.pjpo.pimsdriver.processor.ejb.RssParser")));
        }
        
    	public Reader openResultReader() {
    		return getParser().getResultsReader();
    	}
    	
    	public Reader openGroupsReader() {
    		return getParser().getGroupsReader();
    	}
    }
}
