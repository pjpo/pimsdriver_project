package com.github.pjpo.pimsdriver.pimsstore;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.util.logging.Logger;

public class StoreDTO {

	@SuppressWarnings("unused")
	private static final Logger LOGGER = Logger.getLogger(StoreDTO.class.toString());

	public static long createUploadEntry(final Connection con, final String finess, final LocalDate datePmsi,
			final String rsfVersion, final String rssVersion) throws SQLException {
		try (final PreparedStatement ps = con.prepareStatement("INSERT INTO plud_pmsiupload("
				+ "plud_finess, plud_year, plud_month, "
				+ "plud_dateenvoi, plud_arguments) "
				+ "VALUES (?, ?, ?, "
				+ "transaction_timestamp(), hstore('rsfversion'::text, ?::text) || hstore('rssversion'::text, ?::text)) "
				+ "RETURNING plud_id;")) {

			ps.setString(1, finess);
			ps.setInt(2, datePmsi.getYear());
			ps.setInt(3, datePmsi.getMonthValue());
			ps.setString(4, rsfVersion);
			ps.setString(5, rssVersion);
				
			// EXECUTE QUERY
			try (final ResultSet rs = ps.executeQuery()) {
				rs.next();
				return rs.getLong(1);
			}
			
		}
	}

	
	public static void createTempTables (final Connection con) throws SQLException {
		try (final PreparedStatement ps = con.prepareStatement("CREATE TEMPORARY TABLE pmel_temp ( \n"
				+ " pmel_position bigint NOT NULL, \n"
				+ " pmel_parent bigint, \n"
				+ " pmel_type character varying NOT NULL, \n"
				+ " pmel_line bigint NOT NULL, \n"
				+ " pmel_content character varying NOT NULL, \n"
				+ " pmel_arguments hstore NOT NULL DEFAULT hstore('')\n"
				+ ") ON COMMIT DROP;"
				+ "CREATE TEMPORARY TABLE pmgr_temp ( \n"
				+ " pmel_position bigint NOT NULL, \n"
				+ " pmgr_racine character varying NOT NULL, \n"
				+ " pmgr_modalite character varying NOT NULL, \n"
				+ " pmgr_gravite character varying NOT NULL, \n"
				+ " pmgr_erreur character varying NOT NULL \n"
				+ ") ON COMMIT DROP;")) {
			ps.execute();
		}
	}

	public static void copyTempTables(final Connection con, final long id) throws SQLException {
		try (final PreparedStatement ps = con.prepareStatement(
				"INSERT INTO pmel.pmel_" + id + " (pmel_root, pmel_position, pmel_parent, pmel_type, pmel_line, pmel_content, pmel_arguments) \n"
				+ "SELECT " + id + ", pmel_position, pmel_parent, pmel_type, pmel_line, pmel_content, pmel_arguments FROM pmel_temp;\n"
				+ "INSERT INTO pmgr.pmgr_" + id + " (pmel_id, pmel_root, pmgr_racine, pmgr_modalite, pmgr_gravite, pmgr_erreur) \n"
				+ "SELECT pmel.pmel_id, " + id + ", pmgr.pmgr_racine, pmgr.pmgr_modalite, pmgr.pmgr_gravite, pmgr.pmgr_erreur \n"
				+ "FROM pmgr_temp pmgr \n"
				+ "JOIN pmel.pmel_" + id + " pmel ON \n"
				+ "pmgr.pmel_position = pmel.pmel_position;")) {
		
			ps.execute();
		}
	}

	public static void createDefinitiveTables(final Connection con, final Long sendingId) throws SQLException {
		final String sendingIdString = sendingId.toString();
		try (final PreparedStatement ps = con.prepareStatement("CREATE TABLE pmel.pmel_" +
				sendingIdString + " () INHERITS (public.pmel_pmsielement);\n" +
				"CREATE TABLE pmgr.pmgr_" + sendingIdString + " () INHERITS (public.pmgr_pmsigroups);")) {
			ps.execute();
		}
	}
	
	public static void createConstraints(final Connection con, final long id) throws SQLException {
		try (final PreparedStatement ps = con.prepareStatement("ALTER TABLE pmel.pmel_" + id + '\n'
					+ "ADD CONSTRAINT pmel_inherited_" + id + "_pkey PRIMARY KEY (pmel_id);\n"
					+ "ALTER TABLE pmel.pmel_" + id + '\n'
					+ "ADD CONSTRAINT pmel_inherited_" + id + "_line_unique UNIQUE (pmel_position);\n"
					+ "ALTER TABLE pmel.pmel_" + id + '\n'
					+ "ADD CONSTRAINT pmel_inherited_" + id + "_pmel_parent_fkey FOREIGN KEY (pmel_parent)\n"
					+ "REFERENCES pmel.pmel_" + id + " (pmel_position) MATCH SIMPLE\n"
					+ "ON UPDATE NO ACTION ON DELETE NO ACTION DEFERRABLE INITIALLY DEFERRED;\n"
					+ "ALTER TABLE pmel.pmel_" + id + '\n'
					+ "ADD CONSTRAINT pmel_inherited_" + id + "_pmel_root_fkey FOREIGN KEY (pmel_root)\n"
					+ "REFERENCES public.plud_pmsiupload (plud_id) MATCH SIMPLE\n"
					+ "ON UPDATE NO ACTION ON DELETE NO ACTION DEFERRABLE INITIALLY DEFERRED;\n"
					+ "CREATE INDEX pmel_inherited_" + id + "_pmel_position_idx ON pmel.pmel_" + id + " USING btree (pmel_position);\n"
					+ "ALTER TABLE pmel.pmel_" + id + '\n'
					+ "ADD CONSTRAINT pmel_inherited_" + id + "_root_check CHECK (pmel_root = " + id + ") NO INHERIT;"
					+ "CREATE INDEX pmel_inherited_" + id + "_pmel_rsfb_numrss_idx ON pmel.pmel_" + id + " (substring(pmel_content from 11 for 20)) WHERE pmel_type = 'rsfb';\n"
					+ "CREATE INDEX pmel_inherited_" + id  + "_pmel_rssmain_numrss_idx ON pmel.pmel_" + id + " (substring(pmel_content from 28 for 20)) WHERE pmel_type = 'rssmain';\n"
					+ "ALTER TABLE pmgr.pmgr_" + id + '\n'
					+ " ADD CONSTRAINT pmgr_inherited_" + id + "_root_check CHECK (pmel_root = " + id + ") NO INHERIT;\n"
					+ "ALTER TABLE pmgr.pmgr_" + id + '\n'
					+ " ADD CONSTRAINT pmgr_inherited_" + id + "_pkey PRIMARY KEY (pmgr_id);\n"
					+ "ALTER TABLE pmgr.pmgr_" + id + '\n'
					+ " ADD CONSTRAINT pmgr_inherited_" + id + "_pmel_id_unique UNIQUE (pmel_id);\n"
					+ "ALTER TABLE pmgr.pmgr_" + id + '\n'
					+ " ADD CONSTRAINT pmgr_inherited_" + id + "_pmel_root_fkey FOREIGN KEY (pmel_root)\n"
					+ " REFERENCES public.plud_pmsiupload (plud_id) MATCH SIMPLE\n"
					+ " ON UPDATE NO ACTION ON DELETE NO ACTION DEFERRABLE INITIALLY DEFERRED;\n"
					+ "ALTER TABLE pmgr.pmgr_" + id + '\n'
					+ " ADD CONSTRAINT pmgr_inherited_" + id + "_pmel_id_fkey FOREIGN KEY (pmel_id)\n"
					+ " REFERENCES pmel.pmel_" + id + " (pmel_id) MATCH SIMPLE\n"
					+ " ON UPDATE NO ACTION ON DELETE NO ACTION DEFERRABLE INITIALLY DEFERRED;\n")) {
			ps.execute();
		}
	}

	public static void storeRsfInTemp(final Connection con, final Reader rsfReader) throws IOException, SQLException {
		// CHECK PMSI OFFSET
		final long pmsiOffset = getMaxPmsi(con);
		
		// STORES RSF
		storePmsiInTemp(con, rsfReader, pmsiOffset);
	}
	
	public static void storeRssInTemp(final Connection con, final Reader rssReader, final Reader groupsReader) throws IOException, SQLException {
		// CHECK PMSI OFFSET
		final long pmsiOffset = getMaxPmsi(con);
		
		// STORES RSS
		storePmsiInTemp(con, rssReader, pmsiOffset);
		
		// STORES GROUPS
		storeGroupsInTemp(con, groupsReader, pmsiOffset);
	}

	private static void storePmsiInTemp(final Connection con, final Reader reader, final Long pmsiOffset) throws SQLException, NumberFormatException, IOException {
		// STORES THE ELEMENTS
		try (final BufferedReader br = new BufferedReader(reader);
				final PreparedStatement ps = con.prepareStatement(
				"INSERT INTO pmel_temp(pmel_position, pmel_parent, pmel_type, pmel_line, pmel_content) VALUES (?, ?, ?, ?, ?);")) {
			String line;
			for (Long lineNb = 0L ; (line = br.readLine()) != null ; lineNb = Long.sum(lineNb, 1L)) {
				// FILLS STATEMENTS
				int posInLine = (int) (lineNb % 5);
				
				if (posInLine == 0L && lineNb != 0L) {
					ps.addBatch();
				}
				
				if (posInLine == 0) {
					ps.setLong(posInLine + 1, Long.sum(Long.parseLong(line), pmsiOffset));
				} else if (posInLine == 1) {
					if (line.startsWith(":")) {
						ps.setLong(posInLine + 1, Long.sum(Long.parseLong(line.substring(1)), pmsiOffset));
					} else {
						ps.setNull(posInLine + 1, Types.BIGINT);
					}
				} else if (posInLine == 2 || posInLine == 4) {
					ps.setString(posInLine + 1, line);
				} else if (posInLine == 3) {
					ps.setLong(posInLine + 1, Long.parseLong(line));
				}
				
				// SENDS THE BATCH EACH 1000 ROWS
				if (lineNb != 0L && lineNb % 1000 == 0) {
					ps.executeBatch();
				}
			}
			// SENDS REMAINING BATCH
			ps.executeBatch();
			
		}
	}
	
	private static long getMaxPmsi(final Connection con) throws SQLException {
		try (final PreparedStatement checkPs = con.prepareStatement("SELECT MAX(pmel_position) FROM pmel_temp;")) {
			
			// FIRST CHECK THE MAX PMEL_POSITION AS AN OFFSET
			if (checkPs.execute()) {
				final ResultSet rs = checkPs.getResultSet();
				if (rs.next()) {
					final long result = rs.getLong(1);
					return rs.wasNull() ? 0L : result;
				}
			}
			return 0L;
			
		}
	}

	private static void storeGroupsInTemp(final Connection con, final Reader reader, final long pmsiOffset) throws SQLException, NumberFormatException, IOException {
		try (final BufferedReader br = new BufferedReader(reader);
				final PreparedStatement ps = con.prepareStatement(
				"INSERT INTO pmgr_temp(pmel_position, pmgr_racine, pmgr_modalite, pmgr_gravite, pmgr_erreur) VALUES (?, ?, ?, ?, ?);")) {
			String line;
			for (Long lineNb = 0L ; (line = br.readLine()) != null ; lineNb = Long.sum(lineNb, 1L)) {
				// FILLS STATEMENTS
				int posInLine = (int) (lineNb % 5);
				if (posInLine == 0L && lineNb != 0L) {
					ps.addBatch();
				}
				if (posInLine == 0) {
					if (line.startsWith(":")) {
						ps.setLong(posInLine + 1, Long.sum(Long.parseLong(line.substring(1)),  pmsiOffset));
					} else {
						ps.setNull(posInLine + 1, Types.BIGINT);
					}
				} else if (posInLine < 4) {
					ps.setString(posInLine + 1, line.startsWith(":") ? line.substring(1) : null);
				}
				
				// SENDS THE BATCH EACH 1000 ROWS
				if (lineNb % 1000 == 0) {
					ps.executeBatch();
				}
			}
			// SENDS REMAINING BATCH
			ps.executeBatch();
			
		}
	}
	
	public static void execute(final Connection con, final Executor executor) throws Throwable {
		while (true) {
			try {
				executor.execute();
				con.commit();
			} catch (final Throwable e) {
				if (e instanceof SQLException && ((SQLException)e).getSQLState().equals("40001")) {
					// WAS SERIALIZATION EXCEPTION, RETRY AND COMMIT
				} else {
					// WAS ERROR, ROLLBACK AND RETURN FALSE
					try {
						con.rollback();
					} catch (final SQLException e1) {
						e1.addSuppressed(e);
						throw e1;
					}
					throw e;
				}
			}
		}
	}

	@FunctionalInterface
	public interface Executor {
		public void execute() throws Throwable;
	}
	
}
