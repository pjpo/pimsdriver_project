package com.github.pjpo.pimsdriver.processor.ejb;

import java.io.Reader;
import java.util.Collection;
import java.util.concurrent.Future;

import javax.ejb.Local;

@Local
public interface RssParser {

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
	
	public void clean();
	
	public String getFiness();

	public String getVersion();

	public Long getEndPmsiPosition();

	public Reader getGroupsReader();

	public Reader getResultsReader();

}
