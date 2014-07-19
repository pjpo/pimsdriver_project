package com.github.aiderpmsi.pimsdriver.vaadin.upload;

import java.io.Reader;

import javax.naming.InitialContext;

import com.github.aiderpmsi.pimsdriver.vaadin.utils.FileUploader;
import com.github.aiderpmsi.pimsdriver.vaadin.utils.aop.ActionEncloser;
import com.github.pjpo.pimsdriver.processor.ejb.RssParser;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class RssFileUploader extends FileUploader<RssParser> {

	public RssFileUploader(String type, Window window) {
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