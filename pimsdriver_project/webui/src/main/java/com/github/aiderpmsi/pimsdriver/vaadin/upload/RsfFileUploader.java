package com.github.aiderpmsi.pimsdriver.vaadin.upload;

import java.io.Reader;
import javax.naming.InitialContext;

import com.github.aiderpmsi.pimsdriver.vaadin.utils.FileUploader;
import com.github.aiderpmsi.pimsdriver.vaadin.utils.aop.ActionEncloser;
import com.github.pjpo.pimsdriver.processor.ejb.RsfParser;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class RsfFileUploader extends FileUploader<RsfParser> {

	public RsfFileUploader(String type, Window window) {
		setParser((RsfParser) ActionEncloser.execute((throwable) -> "EJB rsf processor not found", 
				() ->  new InitialContext().lookup("java:global/business/processor-0.0.1-SNAPSHOT/RsfParserBean!com.github.pjpo.pimsdriver.processor.ejb.RsfParser")));
    }
    
	public Reader openResultReader() {
		return getParser().getReader();
	}
	
}