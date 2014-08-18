package com.github.aiderpmsi.pimsdriver.vaadin.upload;

import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.concurrent.Future;
import java.util.logging.Logger;

import org.apache.commons.io.output.WriterOutputStream;

import com.github.aiderpmsi.pimsdriver.vaadin.utils.aop.ActionEncloser;
import com.github.pjpo.commons.ioutils.WriterToReader;
import com.github.pjpo.pimsdriver.processor.ejb.Parser;
import com.github.pjpo.pimsdriver.processor.ejb.ParsingResult;
import com.vaadin.ui.Upload.Receiver;

@SuppressWarnings("serial")
public abstract class FileUploader<T extends Parser> implements Receiver {

	/** Default logger */
	@SuppressWarnings("unused")
	private final static Logger LOGGER = Logger
			.getLogger(FileUploader.class.getName());
	
	/** Parser (generally from ejb) */
	private T parser = null;

	/** future of async parsing */
	private Future<ParsingResult> parsingResultFuture = null;

	/** last sync parsing results */
	private ParsingResult parsingResult = null;
	
	protected void setParser(T parser) {
		this.parser = parser;
	}
	
	protected T getParser() {
		return parser;
	}
		
	@Override
    public OutputStream receiveUpload(String filename,
                                      String mimeType) {
		// CREATES THE LINK BETWEEN WRITER AND READER
		final WriterToReader wtr = new WriterToReader();
		// PROCESSES THE GIVEN FILE
		parsingResultFuture = parser.process(wtr.getReader(), 0L);
		// RETURNS THE WRITER WE HAVE TO WRITE INTO
		return ActionEncloser.execute((throwable) -> "Uploading error",
				() -> new WriterOutputStream(wtr, Charset.forName("UTF-8")));
    }

	/**
	 * Syncs parsing results with parser, waiting parsing to finish
	 */
	public void syncParsingResults() {
		parsingResult = ActionEncloser.execute((throwable) -> "Uploading error", () -> parsingResultFuture.get());
	}

	/**
	 * Returns the parsing results
	 * @return
	 */
	public ParsingResult getParsingResults() {
		return parsingResult;
	}

	public void clean() {
		parsingResult = null;
	}
	
	public void close() {
		parser.remove();
	}
}