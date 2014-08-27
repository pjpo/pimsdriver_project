package com.github.pjpo.pimsdriver.datasource;

import java.sql.Connection;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.Startup;
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

	@Resource(mappedName="jdbc/__pimsdriver")
    private DataSource dataSource ;

	@PostConstruct
	public void init() {
		try {
			// TRY TO MIGRATE IF NEEDED
			final Flyway flyway = new Flyway();
			flyway.setDataSource(dataSource);

			flyway.migrate();
		} catch (Throwable e) {
			LOGGER.log(Level.SEVERE, "Impossible to init datasource", e);
		}
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
