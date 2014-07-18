package com.github.aiderpmsi.pimsdriver.vaadin.utils;

import java.io.OutputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.concurrent.Future;

import javax.ejb.EJB;

import org.apache.commons.io.output.WriterOutputStream;

import com.github.aiderpmsi.pimsdriver.vaadin.utils.aop.ActionEncloser;
import com.github.pjpo.commons.ioutils.WriterToReader;
import com.github.pjpo.pimsdriver.processor.ejb.RsfParser;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class FileUploader implements Receiver {

	/** Multithreaded pipe between writer to reader */
	private final WriterToReader wtr = new WriterToReader();

	/** EJB For pmsi file parsing */
	@EJB(lookup="java:global/business/processor-0.0.1-SNAPSHOT/RsfParserBean!com.github.pjpo.pimsdriver.processor.ejb.RsfParser")
	private RsfParser rsfp = null;

	/** Collection of errors after async parsing */
	private Future<Collection<String>> futureErrorsUpload = null;

	public FileUploader(String type, Window window) {
		// DO NOTHING, RSFPARSER WILL BE INJECTED
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

	/**
	 * Returns the errors while parsing, blocking while parsing has not finished
	 * @return
	 */
	public Collection<String> getErrors() {
		return ActionEncloser.execute(
				(throwable) -> "Uploading error", () -> futureErrorsUpload.get());
	}

	public String getRsfFiness() {
		return rsfp.getFiness();
	}
	
	public String getRsfVersion() {
		return rsfp.getVersion();
	}
	
	public Long getRsfEndPmsiPosition() {
		return rsfp.getEndPmsiPosition();
	}
	
	public Reader openResultReader() {
		return rsfp.getReader();
	}
	
	public void close() {
		rsfp.remove();
	}
}