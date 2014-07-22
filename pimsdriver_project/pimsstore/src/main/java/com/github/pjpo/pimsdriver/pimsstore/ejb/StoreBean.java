package com.github.pjpo.pimsdriver.pimsstore.ejb;

import java.io.Reader;
import java.sql.Connection;
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
	public Boolean storePmsiFiles(final ReaderSupplier rsfResultsReader,
			final ReaderSupplier rssResultsReader, final ReaderSupplier rssGroupsReader) {

		try (final Connection con = dataSourceProvider.getConnection()) {
			StoreDTO.execute(con, () -> {
				if (rsfResultsReader != null) {
					try (final Reader rsfReader = rsfResultsReader.supply()) {
						StoreDTO.storeRsfInTemp(con, rsfReader);
					}
				}
				if (rssResultsReader != null && rssGroupsReader != null) {
					try (
							final Reader rssReader = rssResultsReader.supply();
							final Reader groupsReader = rssGroupsReader.supply();
							) {
						StoreDTO.storeRssInTemp(con, rssReader, groupsReader);
					}
				}
			}); 
			return true;
		} catch (Throwable e) {
			LOGGER.log(Level.WARNING, "Error when storing pmsi files", e);
			return null;
		}
	}

}
