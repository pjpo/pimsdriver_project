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
import com.github.pjpo.pimsdriver.processor.ErrorCatcher;
import com.github.pjpo.pimsdriver.processor.GroupHandler;
import com.github.pjpo.pimsdriver.processor.RssLineHandler;

@Stateful
public class RssParserBean extends ParserBean implements RssParser {

	/** Default logger */
	@SuppressWarnings("unused")
	private final static Logger LOGGER = Logger
			.getLogger(RssParserBean.class.getName());

	/** Stores the result of parsing an rss (can only be accessed in local ejb mode)*/
	private Path rssResult = null;
	
	/** Stores the result of grouping an rss (can only be accessed in local ejb mode)*/
	private Path rssGroups = null;

	@PostConstruct
	private void postConstruct(){
		postConstruct(() -> {
			Throwable e = null;
			e = ErrorCatcher.execute(() -> rssResult = Files.createTempFile("", ""), e); 
			e = ErrorCatcher.execute(() -> rssGroups = Files.createTempFile("", ""), e);
			if (e != null) throw e;
		});
	}
	
	@Override
	@Asynchronous
	public Future<Collection<String>> process(Reader reader, Long startPmsiPosition) {
		return process(reader, startPmsiPosition, (errors) -> {
			try (
					final Writer resultsWriter = Files.newBufferedWriter(rssResult, Charset.forName("UTF-8"), StandardOpenOption.TRUNCATE_EXISTING);
					final Writer groupsWriter = Files.newBufferedWriter(rssGroups, Charset.forName("UTF-8"), StandardOpenOption.TRUNCATE_EXISTING);
					) {
				final SimpleParserFactory spf = new SimpleParserFactory();
				// CREATES THE RSS LINE HANDLER
				final RssLineHandler handler = new RssLineHandler(startPmsiPosition, resultsWriter);
				// CREATES THE RSS GROUPER
				final GroupHandler groupHandler = new GroupHandler(startPmsiPosition, groupsWriter);
				// CREATES PARSER
				final SimpleParser sp = spf.newParser("rssheader", Arrays.asList(handler, groupHandler),
						(msg, line) -> errors.add(msg + " at line " + line));
				// PARSES
				sp.parse(new PimsParserFromReader(reader));
				final ParsingResult result = new ParsingResult();
				result.finess = handler.getFiness();
				result.version = handler.getVersion();
				result.endPmsiPosition = handler.getPmsiPosition();
				return result;
			}
		});
	}
	
	@Override
	public Reader getResultsReader() {
		return getReader(rssResult);
	}
	
	@Override
	public Reader getGroupsReader() {
		return getReader(rssGroups);
	}

	@Remove
	public void remove() {
		// DO NOTHING, PREDESTROY WILL BE CALLED AUTOMATICALLY
	}
	
	@PreDestroy
	private void preDestroy(){
		preDestroy(() -> {
			Throwable e = ErrorCatcher.execute(() -> Files.delete(rssResult), null);
			e = ErrorCatcher.execute(() -> Files.delete(rssGroups), e);
			if (e != null) throw e;
		});
	}
	
	@Override
	public void clean() {
		clean(() -> {
			Throwable e = ErrorCatcher.execute(() -> Files.write(rssResult, new byte[0]), null);
			e = ErrorCatcher.execute(() -> Files.write(rssGroups, new byte[0]), e);
			if (e != null) throw e;
		});
	}

}
