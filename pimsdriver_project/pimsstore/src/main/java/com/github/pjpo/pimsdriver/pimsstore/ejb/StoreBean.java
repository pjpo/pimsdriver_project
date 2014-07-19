package com.github.pjpo.pimsdriver.pimsstore.ejb;

import java.io.Reader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.github.pjpo.pimsdriver.datasource.DataSourceProvider;

@Stateless
public class StoreBean implements Store {

	private static final Logger LOGGER = Logger.getLogger(StoreBean.class.toString());

	@EJB(lookup="java:global/business/datasource-0.0.1-SNAPSHOT/DataSourceProviderBean!com.github.pjpo.pimsdriver.datasource.DataSourceProvider")
	private DataSourceProvider dataSourceProvider;
	
	@Override
	public Boolean storePmsiFiles(final Reader rsfResultsReader,
			final Reader rssResultsReader, final Reader rssGroupsReader) {
		try (final Connection con = dataSourceProvider.getConnection()) {
			return true;
		} catch (Throwable e) {
			LOGGER.log(Level.WARNING, "Error when storing pmsi files", e);
			return null;
		}
	}

}
