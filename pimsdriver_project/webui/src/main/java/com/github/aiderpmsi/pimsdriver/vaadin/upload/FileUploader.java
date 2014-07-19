package com.github.aiderpmsi.pimsdriver.vaadin.upload;

import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.concurrent.Future;

import org.apache.commons.io.output.WriterOutputStream;

import com.github.aiderpmsi.pimsdriver.vaadin.utils.aop.ActionEncloser;
import com.github.pjpo.commons.ioutils.WriterToReader;
import com.github.pjpo.pimsdriver.processor.ejb.Parser;
import com.vaadin.ui.Upload.Receiver;

@SuppressWarnings("serial")
public abstract class FileUploader<T extends Parser> implements Receiver {

	/** Parser (generally from ejb) */
	private T parser = null;

	/** Collection of errors after async parsing */
	private Future<Collection<String>> futureErrorsUpload = null;

	protected void setParser(T parser) {
		this.parser = parser;
	}
	
	protected T getParser() {
		return parser;
	}
	
	@Override
    public OutputStream receiveUpload(String filename,
                                      String mimeType) {
		final WriterToReader wtr = new WriterToReader();
		// PROCESSES THE GIVEN FILE
		futureErrorsUpload = parser.process(wtr.getReader(), 0L);
		// RETURNS THE WRITER WE HAVE TO WRITE INTO
		return ActionEncloser.execute((throwable) -> "Uploading error",
				() -> new WriterOutputStream(wtr, Charset.forName("UTF-8")));
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
	
	public void close() {
		parser.remove();
	}
}