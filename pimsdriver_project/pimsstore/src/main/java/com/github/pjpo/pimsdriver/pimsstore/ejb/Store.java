package com.github.pjpo.pimsdriver.pimsstore.ejb;

import java.io.Reader;

import javax.ejb.Local;

@Local
public interface Store {

	public Boolean storePmsiFiles(
			ReaderSupplier rsfResultsReader,
			ReaderSupplier rssResultsReader,
			ReaderSupplier rssGroupsReader);
	
	@FunctionalInterface
	public interface ReaderSupplier {
		public Reader supply() throws Throwable;
	}
	
}
