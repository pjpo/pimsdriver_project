package com.github.pjpo.pimsdriver.pimsstore.ejb;

import java.io.Reader;
import java.sql.Connection;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;


import javax.ejb.EJB;
import javax.ejb.Stateless;


import com.github.pjpo.pimsdriver.datasource.DataSourceProvider;
import com.github.pjpo.pimsdriver.pimsstore.StoreDTO;

@Stateless
public class StoreBean implements Store {

	private static final Logger LOGGER = Logger.getLogger(StoreBean.class.toString());

	@EJB(lookup="java:global/business/datasource-0.0.1-SNAPSHOT/DataSourceProviderBean!com.github.pjpo.pimsdriver.datasource.DataSourceProvider")
	private DataSourceProvider dataSourceProvider;
	
	@Override
	public Boolean storePmsiFiles(
			final LocalDate pmsiDate,
			final String finess,
			final String rsfVersion,
			final String rssVersion,
			final ReaderSupplier rsfResultsReader,
			final ReaderSupplier rssResultsReader,
			final ReaderSupplier rssGroupsReader) {

		try (final Connection con = dataSourceProvider.getConnection()) {
			StoreDTO.execute(con, () -> {
				// CREATES UPLOAD ENTRY
				final Long entryId = StoreDTO.createUploadEntry(con, finess, pmsiDate, rsfVersion, rssVersion);
				// CREATES TEMP TABLES
				StoreDTO.createTempTables(con);
				// ENTER RSF RESULTS
				if (rsfResultsReader != null) {
					try (final Reader rsfReader = rsfResultsReader.supply()) {
						StoreDTO.storeRsfInTemp(con, rsfReader);
					}
				}
				// ENTER RSS RESULTS
				if (rssResultsReader != null && rssGroupsReader != null) {
					try (
							final Reader rssReader = rssResultsReader.supply();
							final Reader groupsReader = rssGroupsReader.supply();
							) {
						StoreDTO.storeRssInTemp(con, rssReader, groupsReader);
					}
				}
				// CREATE DEFINITIVE TABLES
				StoreDTO.createDefinitiveTables(con, entryId);
				// COPY TEMP DATAS INTO DEFINITIVE TABLES
				StoreDTO.copyTempTables(con, entryId);
				// CREATES TABLES STRUCTURES
				StoreDTO.createConstraints(con, entryId);
			}); 
			return true;
		} catch (Throwable e) {
			LOGGER.log(Level.WARNING, "Error when storing pmsi files", e);
			return null;
		}
	}

}
