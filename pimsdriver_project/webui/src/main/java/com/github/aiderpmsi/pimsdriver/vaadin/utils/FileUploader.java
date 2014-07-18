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
	
	public FileUploader(String type, Window window) {
    }
    
	@Override
    public OutputStream receiveUpload(String filename,
                                      String mimeType) {
		// EJB FOR RESOURCE HANDLING
		return ActionEncloser.execute((throwable) -> "Uploading error",
				() -> {
					InitialContext jndiContext = new InitialContext();
					RsfParser rsfp = (RsfParser) jndiContext.lookup("java:global/business/processor-0.0.1-SNAPSHOT/RsfParserBean!com.github.pjpo.pimsdriver.processor.ejb.RsfParser");
					futureErrorsUpload = rsfp.processRsf(wtr.getReader());	
					return new WriterOutputStream(wtr, Charset.forName("UTF-8"));
				});
    }

	public Collection<String> getErrors() {
		return ActionEncloser.execute(
				(throwable) -> "Uploading error", () -> futureErrorsUpload.get());
	}
}