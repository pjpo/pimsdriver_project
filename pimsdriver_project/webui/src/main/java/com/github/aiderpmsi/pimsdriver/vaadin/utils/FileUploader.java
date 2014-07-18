package com.github.aiderpmsi.pimsdriver.vaadin.utils;

import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.concurrent.Future;

import javax.naming.InitialContext;

import org.apache.commons.io.output.WriterOutputStream;

import com.github.aiderpmsi.pimsdriver.vaadin.utils.aop.ActionEncloser;
import com.github.pjpo.commons.ioutils.WriterToReader;
import com.github.pjpo.pimsdriver.processor.ejb.RsfParser;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Window;

public class FileUploader implements Receiver {

	private static final long serialVersionUID = 5675725310161340636L;
    
	private Future<Collection<String>> futureErrorsUpload = null;

	private final WriterToReader wtr = new WriterToReader();

	private final RsfParser rsfp;
	
	public FileUploader(String type, Window window) {
		// EJB FOR RESOURCE HANDLING
		rsfp = (RsfParser) ActionEncloser.execute((throwable) -> "Uploader creation failed", () -> {
			return new InitialContext().lookup("java:global/business/processor-0.0.1-SNAPSHOT/RsfParserBean!com.github.pjpo.pimsdriver.processor.ejb.RsfParser");});
    }
    
	@Override
    public OutputStream receiveUpload(String filename,
                                      String mimeType) {
		// PROCESSES THE GIVEN FILE
		futureErrorsUpload = rsfp.processRsf(wtr.getReader());
		// RETURNS THE WRITER WE HAVE TO WRITE INTO
		return ActionEncloser.execute((throwable) -> "Uploading error",
				() -> new WriterOutputStream(wtr, Charset.forName("UTF-8")));
    }

	public Collection<String> getErrors() {
		return ActionEncloser.execute(
				(throwable) -> "Uploading error", () -> futureErrorsUpload.get());
	}
	
	public void close() {
		
	}
}