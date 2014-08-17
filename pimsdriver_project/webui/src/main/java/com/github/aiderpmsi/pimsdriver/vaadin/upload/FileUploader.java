package com.github.aiderpmsi.pimsdriver.vaadin.upload;

import java.io.OutputStream;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.Collection;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.output.NullOutputStream;
import org.apache.commons.io.output.WriterOutputStream;

import com.github.aiderpmsi.pimsdriver.vaadin.utils.aop.ActionEncloser;
import com.github.pjpo.commons.ioutils.WriterToReader;
import com.github.pjpo.pimsdriver.processor.ejb.Parser;
import com.vaadin.ui.Upload.Receiver;

@SuppressWarnings("serial")
public abstract class FileUploader<T extends Parser> implements Receiver {

	/** Default logger */
	private final static Logger LOGGER = Logger
			.getLogger(FileUploader.class.getName());
	
	/** Parser (generally from ejb) */
	private T parser = null;

	/** Collection of errors after async parsing */
	private Future<Collection<String>> futureErrorsUpload = null;

	/** Blocks while an upload exists  */
	private final Semaphore lock;
	
	protected FileUploader(final Semaphore lock) {
		this.lock = lock;
	}
	
	protected void setParser(T parser) {
		this.parser = parser;
	}
	
	protected T getParser() {
		return parser;
	}
	
	@Override
    public OutputStream receiveUpload(String filename,
                                      String mimeType) {
		// LOCKS IF POSSIBLE
		try {
			lock.acquire();
				
			// CREATES THE LINK BETWEEN WRITER AND READER
			final WriterToReader wtr = new WriterToReader();
			// PROCESSES THE GIVEN FILE
			futureErrorsUpload = parser.process(wtr.getReader(), 0L);
			// RETURNS THE WRITER WE HAVE TO WRITE INTO
			return ActionEncloser.execute((throwable) -> "Uploading error",
					() -> new WriterOutputStream(wtr, Charset.forName("UTF-8")));
		} catch (InterruptedException e) {
			// ONLY LOG
			LOGGER.log(Level.WARNING, "Upload interrupted");
			// RETURN VOID OUTPUTSTREAM
			return new NullOutputStream();
		}
    }

	/**
	 * Returns the errors while parsing, blocking while parsing has not finished
	 * @return
	 */
	public Collection<String> getErrors() {
		return ActionEncloser.execute(
				(throwable) -> "Uploading error", () -> futureErrorsUpload.get());
	}

	public String getFiness() {
		return parser.getFiness();
	}
	
	public String getVersion() {
		return parser.getVersion();
	}
	
	public Long getEndPmsiPosition() {
		return parser.getEndPmsiPosition();
	}
	
	public LocalDate getPmsiDate() {
		return parser.getDatePmsi();
	}
	
	public void close() {
		parser.remove();
	}

	public void clean() {
		parser.clean();
	}
}