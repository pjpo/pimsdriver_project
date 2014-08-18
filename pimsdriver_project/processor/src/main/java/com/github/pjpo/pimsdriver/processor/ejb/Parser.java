package com.github.pjpo.pimsdriver.processor.ejb;

import java.io.Reader;
import java.util.concurrent.Future;

public interface Parser {

	/** Parses asynchronously a rsf, and returns list of errors.
	 * Beware to call get on the resulting future before acessing ejb get
	 * else you can get older values (from another parsing)
	 * @param reader
	 * @return structure with informations regarding processing
	 */
	public Future<ParsingResult> process(Reader reader, Long startPmsiPosition);

	public void remove();
}
