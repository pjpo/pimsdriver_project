package com.github.pjpo.pimsdriver.processor.ejb;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.AsyncResult;

import com.github.pjpo.pimsdriver.processor.ErrorCatcher.Executor;

public abstract class ParserBean implements Parser {

	/** Default logger */
	private final static Logger LOGGER = Logger
			.getLogger(ParserBean.class.getName());

	/** Indicates if parsing has been succedded or not */
	protected Boolean parsed = false;

	/** Stores the finess of this Pmsi file (if pmsi file parsing has succedded) */
	protected String finess = null;
	
	/** Stores the version of this Pmsi file (if pmsi file parsing has succedded) */
	protected String version = null;
	
	/** Stores the last position of pmsi element (if pmsi file parsing has succedded) */
	protected Long endPmsiPosition = null;
	
	/** Stores list of opened readers (closes them all when quitting) */
	protected final LinkedList<Reader> openedReaders = new LinkedList<Reader>();
	
	/** Read Lock object */
	protected final Object readLock = new Object();
	
	/** Write Lock object */
	protected final Object writeLock = new Object();

	protected void postConstruct(Executor executor) {
		try {
			executor.execute();
		} catch (Throwable e) {
			LOGGER.log(Level.SEVERE, "EJB construction failed", e);
		}
	}
	
	protected Future<Collection<String>> process(Reader reader, Long startPmsiPosition, ParsingExecutor parser) {
		synchronized(writeLock) {
			try {
				// CLOSES ALL OPENED READER
				closeAllReaders();
				// ERROR COLLECTOR
				final LinkedList<String> errors = new LinkedList<>();
				// PARSES
				final ParsingResult pr = parser.parse(errors);
				if (pr == null)
					throw new IOException("Error while parsing pmsi file");
				// BE SURE TO CLEANUP WHOLE READER If AN ERROR HAPPENED
				while (reader.skip(65536L) != 0) {}
				// SETS THE RESULT OF PARSING
				setResults(true, pr.finess, pr.version, pr.endPmsiPosition);
				// RETURNS THE LIST OF ERRORS
				return new AsyncResult<Collection<String>>(errors);
			} catch (Throwable e) {
				LOGGER.log(Level.SEVERE, "Unable to process Pmsi : ", e);
				// REINITS RSF DATAS
				setResults(false, null, null, null);
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
	
	protected Reader getReader(Path path) {
		synchronized(writeLock) {
			try {
				final Reader reader = Files.newBufferedReader(path, Charset.forName("UTF-8"));
				openedReaders.add(reader);
				return reader;
			} catch (IOException e) {
				LOGGER.log(Level.SEVERE, "Unable to read temp file", e);
				return null;
			}
		}
	}
	
	protected void preDestroy(Executor executor){
		try {
			// CLOSES ALL OPENED READER
			closeAllReaders();
			executor.execute();
		} catch (Throwable e) {
			LOGGER.log(Level.SEVERE, "Unable to delete temp file", e);
		}
	}
	
	private void setResults(final Boolean parsed, final String finess, final String version, final Long endPmsiPosition) {
		synchronized (readLock) {
			this.parsed = parsed;
			this.finess = finess;
			this.version = version;
			this.endPmsiPosition = endPmsiPosition;
		}
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
	
	protected static class ParsingResult {
		public String finess;
		public String version;
		public Long endPmsiPosition;
	}
	
	@FunctionalInterface
	protected static interface ParsingExecutor {
		public ParsingResult parse(Collection<String> errorCollector) throws Throwable;
	}
}
