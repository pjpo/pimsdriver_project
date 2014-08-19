package com.github.pjpo.pimsdriver.pimsstore.aop;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DTOEncloser {

	private static final Logger LOGGER = Logger.getLogger(DTOEncloser.class.toString());

	public static <T> T execute(final Connection con, final Executor<T> executor) throws Throwable {
		while (true) {
			try {
				final T result = executor.execute();
				con.commit();
				return result;
			} catch (final Throwable e) {
				manageThrowable(con, e);
			}
		}
	}
	
	@FunctionalInterface
	public interface Executor<T> {
		public T execute() throws Throwable;
	}
	
	public static void executeVoid(final Connection con, final VoidExecutor executor) throws Throwable {
		while (true) {
			try {
				executor.execute();
				con.commit();
				break;
			} catch (final Throwable e) {
				manageThrowable(con, e);
			}
		}
	}

	@FunctionalInterface
	public interface VoidExecutor {
		public void execute() throws Throwable;
	}

	private static void manageThrowable(final Connection con, final Throwable e) throws Throwable {
		if (e instanceof SQLException && ((SQLException)e).getSQLState().equals("40001")) {
			// WAS SERIALIZATION EXCEPTION, RETRY AND COMMIT
			LOGGER.log(Level.INFO, "Serialization exception, retrying", e);
		} else {
			// WAS ERROR, ROLLBACK AND RETURN FALSE
			try {
				con.rollback();
				LOGGER.log(Level.INFO, "SQL error, rollback", e);
			} catch (final SQLException e1) {
				e1.addSuppressed(e);
				LOGGER.log(Level.WARNING, "SQL error, but error when rollback", e);
				throw e1;
			}
			throw e;
		}
	}
}
