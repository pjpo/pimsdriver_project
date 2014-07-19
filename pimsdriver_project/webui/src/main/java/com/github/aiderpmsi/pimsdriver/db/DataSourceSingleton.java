package com.github.aiderpmsi.pimsdriver.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.InitialContext;
import javax.servlet.ServletContext;
import com.github.pjpo.pimsdriver.datasource.DataSourceProvider;

public class DataSourceSingleton {

	private static final Logger LOGGER = Logger.getLogger(DataSourceSingleton.class.toString());

	public static synchronized Connection getConnection(final ServletContext context) throws SQLException {
		try {
			InitialContext jndiContext = new InitialContext();
			DataSourceProvider bi = (DataSourceProvider) jndiContext.lookup("java:global/business/datasource-0.0.1-SNAPSHOT/DataSourceProviderBean!com.github.pjpo.pimsdriver.datasource.DataSourceProvider");
			Connection con = bi.getConnection();
			return con;
		} catch (Throwable e) {
			LOGGER.log(Level.SEVERE, "Unable to obtain database connection", e);
			throw new SQLException(e);
		}
	}
	
}
