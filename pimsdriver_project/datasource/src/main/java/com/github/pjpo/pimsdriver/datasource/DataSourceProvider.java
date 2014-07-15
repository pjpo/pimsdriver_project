package com.github.pjpo.pimsdriver.datasource;

import java.sql.Connection;

import javax.ejb.Local;

/**
 * Session Bean implementation class DataSourceProvider
 */
@Local
public interface DataSourceProvider {

	public Connection getConnection();

}
