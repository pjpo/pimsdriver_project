package com.github.pjpo.pimsdriver.processor.ejb;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
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

	/** Stores list of opened readers (closes them all when quitting) */
	protected final LinkedList<Reader> openedReaders = new LinkedList<Reader>();
	
	/** Lock object */
	protected final Object lock = new Object();

	protected void postConstruct(Executor executor) {
		try {
			executor.execute();
		} catch (Throwable e) {
			LOGGER.log(Level.SEVERE, "EJB construction failed", e);
		}
	}
	
	protected Future<ParsingResult> process(Reader reader, Long startPmsiPosition, ParsingExecutor parser) {
		synchronized(lock) {
			try {
				// CLOSES ALL OPENED READER
				closeAllReaders();

				// PARSES
				final ParsingResult pr = parser.parse();

				if (pr == null)
					throw new IOException("Error while parsing pmsi file");
				
				// BE SURE TO CLEANUP WHOLE READER If AN ERROR HAPPENED
				while (reader.skip(65536L) != 0) {}
				
				// RETURNS THE RESULT OF PROCESSING OF ERRORS
				return new AsyncResult<ParsingResult>(pr);
			} catch (Throwable e) {
				LOGGER.log(Level.SEVERE, "Unable to process Pmsi : ", e);
				return null;
			}
		}
	}
	
	protected Reader getReader(Path path) {
		synchronized(lock) {
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
	
	@FunctionalInterface
	protected static interface ParsingExecutor {
		public ParsingResult parse() throws Throwable;
	}
}
