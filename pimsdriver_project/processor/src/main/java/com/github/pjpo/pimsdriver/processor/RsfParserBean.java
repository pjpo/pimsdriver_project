package com.github.pjpo.pimsdriver.processor;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.AsyncResult;
import javax.ejb.Stateful;

import com.github.aiderpmsi.pims.parser.utils.PimsParserFromWriter;

@Stateful
public class RsfParserBean implements RsfParser {

	@SuppressWarnings("unused")
	private final static Logger LOGGER = Logger
			.getLogger(RsfParserBean.class.getName());

	private PimsParserFromWriter ppfw = new PimsParserFromWriter();
	
	private Path rsfResult = null;

	@PostConstruct
	public void construct() {
		try {
			rsfResult = Files.createTempFile("", "");
		} catch (Throwable e) {
			LOGGER.log(Level.SEVERE, "Unable to create temp file", e);
		}
	}
	
	@Override
	public Writer getWriter() {
		return ppfw;
	}

	@Override
	public Future<Boolean> processRsf() {
		try {
			
		} catch (Throwable e) {
			LOGGER.log(Level.SEVERE, "Unable to process Rsf", e);
			return null;
		}
	}

	@Override
	public Reader getResult() {
		try {
			return Files.newBufferedReader(rsfResult, Charset.forName("UTF-8"));
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Unable to read temp file", e);
			return null;
		}
	}
	
	@PreDestroy
	public void destroy() {
		try {
			Files.delete(rsfResult);
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Unable to delete temp file", e);
		}
	}

}
