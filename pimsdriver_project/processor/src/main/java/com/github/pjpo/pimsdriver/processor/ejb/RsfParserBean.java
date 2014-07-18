package com.github.pjpo.pimsdriver.processor.ejb;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.Remove;
import javax.ejb.Stateful;

import com.github.aiderpmsi.pims.parser.utils.PimsParserFromReader;
import com.github.aiderpmsi.pims.parser.utils.SimpleParser;
import com.github.aiderpmsi.pims.parser.utils.SimpleParserFactory;
import com.github.pjpo.pimsdriver.processor.RsfLineHandler;

@Stateful
public class RsfParserBean implements RsfParser {

	private final static Logger LOGGER = Logger
			.getLogger(RsfParserBean.class.getName());

	private Path rsfResult = null;
	
	private String finess = null;
	
	private String version = null;
	
	private Long endPmsiPosition = null;
	
	@PostConstruct
	private void postConstruct(){
		try {
			rsfResult = Files.createTempFile("", "");
		} catch (Throwable e) {
			LOGGER.log(Level.SEVERE, "Unable to create temp file", e);
		}
	}
	
	@Override
	@Asynchronous
	public Future<Collection<String>> processRsf(Reader reader) {
		try (final Writer writer = Files.newBufferedWriter(rsfResult, Charset.forName("UTF-8"), StandardOpenOption.TRUNCATE_EXISTING)) {
			final SimpleParserFactory spf = new SimpleParserFactory();
			// CREATES THE RSF LINE HANDLER
			final RsfLineHandler handler = new RsfLineHandler(0, writer);
			// ERROR COLLECTOR
			final LinkedList<String> errors = new LinkedList<>();
			// CREATES PARSER
			final SimpleParser sp = spf.newParser("rsfheader", Arrays.asList(handler),
					(msg, line) -> errors.add(msg + " at line " + line));
			// PARSES
			sp.parse(new PimsParserFromReader(reader));
			// FILLS THE RESULTS OF RSF PARSING
			finess = handler.getFiness();
			version = handler.getVersion();
			endPmsiPosition = handler.getPmsiPosition();
			return new AsyncResult<Collection<String>>(errors);
		} catch (Throwable e) {
			LOGGER.log(Level.SEVERE, "Unable to process Rsf", e);
			return null;
		}
	}

	@Override
	public Result getResult() {
		try {
			Result result = new Result(finess, version, endPmsiPosition, Files.newBufferedReader(rsfResult, Charset.forName("UTF-8")));
			return result;
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Unable to read temp file", e);
			return null;
		}
	}
	
	@Remove
	public void remove() {
		// DO NOTHING, PREDESTROY WILL BE CALLED AUTOMATICALLY
	}
	
	@PreDestroy
	private void preDestroy(){
		try {
			Files.delete(rsfResult);
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Unable to delete temp file", e);
		}
	}

}
