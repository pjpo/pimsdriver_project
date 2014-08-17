package com.github.pjpo.pimsdriver.pimsstore.ejb;

import java.io.Reader;
import java.time.LocalDate;

import javax.ejb.Local;

@Local
public interface Store {

	public Boolean storePmsiFiles(
			LocalDate pmsiDate,
			String finess,
			String rsfVersion,
			String rssVersion,
			ReaderSupplier rsfResultsReader,
			ReaderSupplier rssResultsReader,
			ReaderSupplier rssGroupsReader);
	
	@FunctionalInterface
	public interface ReaderSupplier {
		public Reader supply() throws Throwable;
	}
	
}
