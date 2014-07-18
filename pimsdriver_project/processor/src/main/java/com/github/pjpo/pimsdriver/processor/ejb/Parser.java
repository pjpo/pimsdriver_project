package com.github.pjpo.pimsdriver.processor.ejb;

import java.io.Reader;
import java.util.Collection;
import java.util.concurrent.Future;

public interface Parser {

	/** Parses asynchronously a rsf, and returns list of errors.
	 * Beware to call get on the resulting future before acessing ejb get
	 * else you can get older values (from another parsing)
	 * @param reader
	 * @return
	 */
	public Future<Collection<String>> process(Reader reader, Long startPmsiPosition);

	/**
	 * Calls stateful bean removal
	 */
	public void remove();
	
	public String getFiness();

	String getVersion();

	Long getEndPmsiPosition();

}
