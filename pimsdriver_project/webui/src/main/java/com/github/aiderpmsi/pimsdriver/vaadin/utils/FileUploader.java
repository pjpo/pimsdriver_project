package com.github.aiderpmsi.pimsdriver.vaadin.utils;

import java.io.OutputStream;
import java.nio.charset.Charset;

import javax.naming.InitialContext;
import org.apache.commons.io.output.WriterOutputStream;

import com.github.aiderpmsi.pimsdriver.vaadin.utils.aop.ActionEncloser;
import com.github.pjpo.pimsdriver.processor.RsfParser;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Window;

public class FileUploader implements Receiver {

	private static final long serialVersionUID = 5675725310161340636L;
    
	public FileUploader(String type, Window window) {
    }
    
	@Override
    public OutputStream receiveUpload(String filename,
                                      String mimeType) {
		// EJB FOR RESOURCE HANDLING
		return ActionEncloser.execute((throwable) -> "Uploading error",
				() -> {
					InitialContext jndiContext = new InitialContext();
					RsfParser rsfp = (RsfParser) jndiContext.lookup("java:global/business/processor-0.0.1-SNAPSHOT/RsfParserBean!com.github.pjpo.pimsdriver.processor.RsfParserBean");
					return new WriterOutputStream(rsfp.getWriter(), Charset.forName("UTF-8"));
				});
    }

}