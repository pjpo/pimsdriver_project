package com.github.pjpo.pimsdriver.processor.ejb;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.PostActivate;
import javax.ejb.PrePassivate;
import javax.ejb.Stateful;

import com.github.pjpo.pimsdriver.processor.ErrorCatcher;
import com.github.pjpo.pimsdriver.processor.ErrorCatcher.Executor;

@Stateful
public class RsfParserBean implements RsfParser {
	
	private Path rsfFile = null;

	private Path rsfResult = null;
	
	private String finess = null;
	
	private String version = null;
	
	private Long endPmsiPosition = null;

	private final static Logger LOGGER = Logger
			.getLogger(RsfParserBean.class.getName());
	
	@PostActivate
	public void construct() {
		try {
			rsfFile = Files.createTempFile("", "");
			rsfFile = Files.createTempFile("", "");
		} catch (Throwable e) {
			if (rsfFile != null) {
				e = ErrorCatcher.execute(new Executor() {
					@Override public void execute() throws Throwable { Files.deleteIfExists(rsfFile); rsfFile = null; }}
				, e);
			}
			if (rsfResult != null) {
				e = ErrorCatcher.execute(new Executor() {
					@Override public void execute() throws Throwable { Files.deleteIfExists(rsfResult); rsfResult = null; }}
				, e);
			}
			LOGGER.log(Level.SEVERE, "Unable to create temp file", e);
		}
	}

	@Override
	public Writer getWriter() {
		try {
			return Files.newBufferedWriter(rsfFile, Charset.forName("UTF-8"));
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Unable to create writer", e);
			return null;
		}
	}

	@Override
	public Future<Collection<String>> processRsf() {
		return null;
	}

	@Override
	public Result getResult() {
		return null;
	}
	
	@PrePassivate
	public void destroy() {
		Throwable e = null;
		if (rsfFile != null) {
			e = ErrorCatcher.execute(new Executor() {
				@Override public void execute() throws Throwable { Files.deleteIfExists(rsfFile); rsfFile = null; }}
			, e);
		}
		if (rsfResult != null) {
			e = ErrorCatcher.execute(new Executor() {
				@Override public void execute() throws Throwable { Files.deleteIfExists(rsfResult); rsfResult = null; }}
			, e);
		}
		LOGGER.log(Level.SEVERE, "Unable to create temp file", e);
	}

}
