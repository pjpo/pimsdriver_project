package com.github.pjpo.pimsdriver.pimsstore.ejb;

import java.io.Reader;

import javax.ejb.Local;

@Local
public interface Store {

	public Boolean storePmsiFiles(
			Reader rsfResultsReader,
			Reader rssResultsReader,
			Reader rssGroupsReader);
	
}
