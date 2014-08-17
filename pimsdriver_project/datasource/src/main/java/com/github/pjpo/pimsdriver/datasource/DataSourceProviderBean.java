package com.github.pjpo.pimsdriver.datasource;

import java.sql.Connection;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.flywaydb.core.Flyway;

/**
 * Session Bean implementation class DataSourceProviderBean
 */
@Singleton
@Startup
public class DataSourceProviderBean implements DataSourceProvider {

	private final static Logger LOGGER = Logger
			.getLogger(DataSourceProviderBean.class.getName());

	private DataSource dataSource = null;

	@PostConstruct
	public void init() {
		try {
			InitialContext jndiContext = new InitialContext();
			Object lookupObject = jndiContext
					.lookup("jdbc/__pimsdriver");
			if (lookupObject instanceof DataSource) {
				dataSource = (DataSource) lookupObject;

				// TRY TO MIGRATE IF NEEDED
				final Flyway flyway = new Flyway();
				flyway.setDataSource(dataSource);

				flyway.migrate();
			}
		} catch (Throwable e) {
			LOGGER.log(Level.SEVERE, "Impossible to init datasource", e);
		}
	}

	@PreDestroy
	public void stop() {
		dataSource = null;
	}

	@Override
	public Connection getConnection() {
		if (dataSource == null) {
			return null;
		} else {
			try {
				return dataSource.getConnection();
			} catch (Throwable e) {
				LOGGER.log(Level.SEVERE, "Impossible to generate connection from datasource", e);
				return null;
			}
		}
	}

}
