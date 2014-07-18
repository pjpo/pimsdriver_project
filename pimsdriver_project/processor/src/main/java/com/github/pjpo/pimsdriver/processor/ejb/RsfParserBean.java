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
import java.util.Iterator;
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

	/** Default logger */
	private final static Logger LOGGER = Logger
			.getLogger(RsfParserBean.class.getName());

	/** Stores the result of parsing an rsf (can only be accessed in local ejb mode)*/
	private Path rsfResult = null;
	
	/** Indicates if parsing has been succedded or not */
	private Boolean parsed = false;

	/** Stores the finess of this Pmsi file (if pmsi file parsing has succedded) */
	private String finess = null;
	
	/** Stores the version of this Pmsi file (if pmsi file parsing has succedded) */
	private String version = null;
	
	/** Stores the last position of pmsi element (if pmsi file parsing has succedded) */
	private Long endPmsiPosition = null;
	
	/** Stores list of opened readers (closes them all when quitting) */
	private final LinkedList<Reader> openedReaders = new LinkedList<Reader>();
	
	/** Read Lock object */
	private final Object readLock = new Object();
	
	/** Write Lock object */
	private final Object writeLock = new Object();

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
	public Future<Collection<String>> process(Reader reader, Long startPmsiPosition) {
		synchronized(writeLock) {
			// CLOSES ALL OPENED READER
			closeAllReaders();
			try (final Writer writer = Files.newBufferedWriter(rsfResult, Charset.forName("UTF-8"), StandardOpenOption.TRUNCATE_EXISTING)) {
				final SimpleParserFactory spf = new SimpleParserFactory();
				// CREATES THE RSF LINE HANDLER
				final RsfLineHandler handler = new RsfLineHandler(startPmsiPosition, writer);
				// ERROR COLLECTOR
				final LinkedList<String> errors = new LinkedList<>();
				// CREATES PARSER
				final SimpleParser sp = spf.newParser("rsfheader", Arrays.asList(handler),
						(msg, line) -> errors.add(msg + " at line " + line));
				// PARSES
				sp.parse(new PimsParserFromReader(reader));
				synchronized (readLock) {
					// FILLS THE RESULTS OF RSF PARSING
					setRsfResults(handler.getFiness(), handler.getVersion(), handler.getPmsiPosition());
					// INDICATES RSF HAS BEEN PARSED
					parsed = true;
				}
				// RETURNS THE LIST OF ERRORS
				return new AsyncResult<Collection<String>>(errors);
			} catch (Throwable e) {
				LOGGER.log(Level.SEVERE, "Unable to process Rsf", e);
				// REINITS RSF DATAS
				synchronized (parsed) {
					parsed = false;
					setRsfResults(null, null, null);
				}
				return null;
			}
		}
	}

	@Override
	public String getFiness() {
		synchronized (readLock) {
			if (parsed == false)
				return null;
			else
				return finess;
		}
	}
	
	@Override
	public String getVersion() {
		synchronized (readLock) {
			if (parsed == false)
				return null;
			else
				return version;
		}
	}
	
	@Override
	public Long getEndPmsiPosition() {
		synchronized (readLock) {
			if (parsed = false)
				return null;
			else
				return endPmsiPosition;
		}
	}
	
	@Override
	public Reader getReader() {
		synchronized(writeLock) {
			try {
				final Reader reader = Files.newBufferedReader(rsfResult, Charset.forName("UTF-8"));
				openedReaders.add(reader);
				return reader;
			} catch (IOException e) {
				LOGGER.log(Level.SEVERE, "Unable to read temp file", e);
				return null;
			}
		}
	}
	
	@Remove
	public void remove() {
		// DO NOTHING, PREDESTROY WILL BE CALLED AUTOMATICALLY
	}
	
	@PreDestroy
	private void preDestroy(){
		try {
			// CLOSES ALL OPENED READER
			closeAllReaders();
			// DELETES TMP FILE
			Files.delete(rsfResult);
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Unable to delete temp file", e);
		}
	}
	
	private void setRsfResults(final String finess, final String version, final Long endPmsiPosition) {
		this.finess = finess;
		this.version = version;
		this.endPmsiPosition = endPmsiPosition;
	}

	private void closeAllReaders() {
		for (Iterator<Reader> readerIt = openedReaders.iterator() ; readerIt.hasNext() ; ) {
			final Reader reader = readerIt.next();
			try {
				reader.close();
			} catch (Throwable e) {
				// DO NOTHING, AN EXCEPTION ON A CLOSED READER IS NORMAL
			}
			readerIt.remove();
		}
	}
	
}
