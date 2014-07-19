package com.github.pjpo.pimsdriver.processor.ejb;

import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.Future;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Asynchronous;
import javax.ejb.Remove;
import javax.ejb.Stateful;

import com.github.aiderpmsi.pims.parser.utils.PimsParserFromReader;
import com.github.aiderpmsi.pims.parser.utils.SimpleParser;
import com.github.aiderpmsi.pims.parser.utils.SimpleParserFactory;
import com.github.pjpo.pimsdriver.processor.RsfLineHandler;

@Stateful
public class RsfParserBean extends ParserBean implements RsfParser {

	/** Default logger */
	@SuppressWarnings("unused")
	private final static Logger LOGGER = Logger
			.getLogger(RsfParserBean.class.getName());

	/** Stores the result of parsing an rsf (can only be accessed in local ejb mode)*/
	private Path rsfResult = null;
	
	@PostConstruct
	private void postConstruct(){
		postConstruct(() -> {
			rsfResult = Files.createTempFile("", "");
		});
	}
	
	@Override
	@Asynchronous
	public Future<Collection<String>> process(Reader reader, Long startPmsiPosition) {
		return process(reader, startPmsiPosition, (errors) -> {
			try (final Writer writer = Files.newBufferedWriter(rsfResult, Charset.forName("UTF-8"), StandardOpenOption.TRUNCATE_EXISTING)) {
				final SimpleParserFactory spf = new SimpleParserFactory();
				// CREATES THE RSF LINE HANDLER
				final RsfLineHandler handler = new RsfLineHandler(startPmsiPosition, writer);
				// CREATES PARSER
				final SimpleParser sp = spf.newParser("rsfheader", Arrays.asList(handler),
						(msg, line) -> errors.add(msg + " at line " + line));
				// PARSES
				sp.parse(new PimsParserFromReader(reader));
				ParsingResult result = new ParsingResult();
				result.finess = handler.getFiness();
				result.version = handler.getVersion();
				result.endPmsiPosition = handler.getPmsiPosition();
				return result;
			}
		});
	}
	
	@Override
	public Reader getReader() {
		return getReader(rsfResult);
	}
	
	@Remove
	public void remove() {
		// DO NOTHING, PREDESTROY WILL BE CALLED AUTOMATICALLY
	}
	
	@PreDestroy
	private void preDestroy(){
		preDestroy(() -> Files.delete(rsfResult));
	}
	
}
