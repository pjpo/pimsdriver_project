package com.github.pjpo.pimsdriver.datasource;

import java.sql.Connection;

import javax.ejb.Remote;

/**
 * Session Bean implementation class DataSourceProvider
 */
@Remote
public interface DataSourceProvider {

	public Connection getConnection();

}
