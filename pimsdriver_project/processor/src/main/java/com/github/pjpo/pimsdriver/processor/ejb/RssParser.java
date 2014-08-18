package com.github.pjpo.pimsdriver.processor.ejb;

import java.io.Reader;
import javax.ejb.Local;

@Local
public interface RssParser extends Parser {

	public Reader getGroupsReader();

	public Reader getResultsReader();

}
