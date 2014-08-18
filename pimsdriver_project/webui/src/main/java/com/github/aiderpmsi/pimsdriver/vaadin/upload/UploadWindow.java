package com.github.aiderpmsi.pimsdriver.vaadin.upload;

import java.io.Reader;
import java.time.LocalDate;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

import javax.naming.InitialContext;
import javax.servlet.ServletContext;

import com.github.aiderpmsi.pimsdriver.vaadin.utils.aop.ActionEncloser;
import com.github.pjpo.pimsdriver.pimsstore.ejb.Store;
import com.github.pjpo.pimsdriver.processor.ejb.ParsingResult;
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
import com.vaadin.ui.Upload.StartedEvent;
import com.vaadin.ui.Upload.StartedListener;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class UploadWindow extends Window {
	
	/** Default logger */
	@SuppressWarnings("unused")
	private final static Logger LOGGER = Logger
			.getLogger(UploadWindow.class.getName());
	
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

    /** List of pendingUploads */
    private final LinkedBlockingQueue<StartedEvent> pendingUploads = new LinkedBlockingQueue<>(); 
    
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
        
        // SETS ONE PENDING UPLOAD
        StartedListener startedListener = (startedEvent) -> pendingUploads.offer(startedEvent);
        
        // ADD RSF FILE PICKER
        rsfFilePicker = new Upload("RSF : ", new RsfFileUploader("rsf", this));
        rsfFilePicker.addStartedListener(startedListener);
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
        rssFilePicker = new Upload("RSS", new RssFileUploader("rss", this));
        rssFilePicker.addStartedListener(startedListener);
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
    	synchronized(pendingUploads) {
        	// 1 - SYNCHRONIZES PARSING RESULTS WITH RECEIVER, WAITING UPLOAD FINISH IF NEEDED
    		receiver.syncParsingResults();
    		// 2 - GETS PARSING RESULTS
    		final ParsingResult pr = receiver.getParsingResults(); 
			// 2 -VERIFY THAT THE UPLOAD SUCCEDED (1)
	    	if (pr != null && pr.errors != null && pr.errors.size() > 0) {
	    		Notification.show("Mauvais fichier PMSI", Notification.Type.WARNING_MESSAGE);
	    		// REINIT UPLOAD BAR
	    		receiverProgressBar.setValue(0F);
	    	}
    		// 3 - VERIFY THAT THE UPLOAD SUCCEDED (2)
	    	else if (pr == null || pr.errors == null) {
	    		Notification.show("Echec de l'upload", Notification.Type.WARNING_MESSAGE);
	    		// REINIT UPLOAD BAR
	    		receiverProgressBar.setValue(0F);
	    	}
	    	else {
	    		// 4 - VERIFY THAT FINESSES MATCHES
	    		if (finess != null && !finess.equals(pr.finess)) {
	        		// REINIT DOWNLOAD
	    			remove(receiver, receiverProgressBar);
	    			Notification.show("Finess RSF et RSS ne correspondent pas", Notification.Type.WARNING_MESSAGE);
	    		}
	    		// 5 - VERIFY THAT PMSI DATES MATCH
	    		if (pmsiDate != null && !pmsiDate.equals(pr.datePmsi)) {
	    			// REINIT DOWNLOAD
	    			remove(receiver, receiverProgressBar);
	    			Notification.show("Date du pmsi RSF et RSS ne correspondent pas", Notification.Type.WARNING_MESSAGE);
	    		}
	    	}
			// UPDATES FINESS
			updateFiness();
			// UPDATES PMSI DATE
			updatePmsiDate();
			// REMOVES THIS PENDING UPLOAD
			pendingUploads.poll();
    	}
    }
    
    private void updateFiness() {
    	final FileUploader<?> fileUploaders[] = new FileUploader<?>[]
    			{(FileUploader<?>) rsfFilePicker.getReceiver(), (FileUploader<?>) rssFilePicker.getReceiver()};
    	String newFiness = null;
    	for (FileUploader<?> fu : fileUploaders) {
    		if (fu.getParsingResults() != null && fu.getParsingResults().finess != null)
    			newFiness = fu.getParsingResults().finess;
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
    		if (fu.getParsingResults() != null && fu.getParsingResults().datePmsi != null)
    			newPmsiDate = fu.getParsingResults().datePmsi;
    	}
    	if (pmsiDate != newPmsiDate) {
    		pmsiDate = newPmsiDate;
    		datePmsiFeedBack.setValue(pmsiDate == null ? "" : "M" + pmsiDate.getMonthValue() + " Y" + pmsiDate.getYear());
    	}
    }

    private void remove(final FileUploader<?> receiver, final ProgressBar progressBar) {
    	// ONLY ACCEPT TO REMOVE SOMETHING IF PENDINGUPLOADS IS VOID
    	synchronized(pendingUploads) {
    		if (pendingUploads.size() != 0) {
    			Notification.show("Un fichier est en cours de transfert", Notification.Type.WARNING_MESSAGE);
    		} else {
    			((FileUploader<?>) rsfFilePicker.getReceiver()).clean();;
    			progressBar.setValue(0F);
    			updateFiness();
    			updatePmsiDate();
    		}
    	}
	}

    private void upload() {
    	synchronized(pendingUploads) {
    		if (pendingUploads.size() != 0) {
    			Notification.show("Un fichier est en cours de transfert", Notification.Type.WARNING_MESSAGE);
    		} else {
    			final RsfFileUploader rsfFileUploader = (RsfFileUploader) rsfFilePicker.getReceiver();
    			final RssFileUploader rssFileUploader = (RssFileUploader) rssFilePicker.getReceiver();
    			if (rsfFileUploader.getParsingResults() == null || rsfFileUploader.getParsingResults().finess == null) {
    				Notification.show("Au moins un fichier RSF doit être proposé", Notification.Type.WARNING_MESSAGE);
    			} else {
    				final Store store = (Store) ActionEncloser.execute((throwable) -> "EJB store not found",
    						() -> new InitialContext().lookup("java:global/business/pimsstore-0.0.1-SNAPSHOT/StoreBean!com.github.pjpo.pimsdriver.pimsstore.ejb.Store"));
    				store.storePmsiFiles(
    						rsfFileUploader.getParsingResults().datePmsi,
    						rsfFileUploader.getParsingResults().finess,
    						rsfFileUploader.getParsingResults().version,
    						rssFileUploader.getParsingResults() != null ? rssFileUploader.getParsingResults().version : null,
    						() -> rsfFileUploader.openResultReader(),
    							rssFileUploader.getParsingResults() != null ? () -> rssFileUploader.openResultReader() : null, 
    							rssFileUploader.getParsingResults() != null ? () -> rssFileUploader.openGroupsReader() : null);
    			}
    		}
    	}
    }
    
	private static class RsfFileUploader extends FileUploader<RsfParser> {

    	public RsfFileUploader(final String type, final Window window) {
    		setParser((RsfParser) ActionEncloser.execute((throwable) -> "EJB rsf processor not found", 
    				() ->  new InitialContext().lookup("java:global/business/processor-0.0.1-SNAPSHOT/RsfParserBean!com.github.pjpo.pimsdriver.processor.ejb.RsfParser")));
        }
        
    	public Reader openResultReader() {
    		return getParser().getReader();
    	}
    	
    }

	public class RssFileUploader extends FileUploader<RssParser> {

    	public RssFileUploader(final String type, final Window window) {
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
