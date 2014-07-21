package com.github.pjpo.pimsdriver.pimsstore;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StoreDTO {

	public static void createTempTables (final Connection con) throws SQLException {
		try (final PreparedStatement ps = con.prepareStatement("CREATE TEMPORARY TABLE pmel_temp ( \n"
				+ " pmel_root bigint NOT NULL, \n"
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
				+ "SELECT pmel_root, pmel_position, pmel_parent, pmel_type, pmel_line, pmel_content, pmel_arguments FROM pmel_temp;\n"
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
	
	public static void storeRssInTemp(final Connection con, final Reader rsfReader, final Reader groupsReader) throws IOException, SQLException {
		// CHECK PMSI OFFSET
		final long pmsiOffset = getMaxPmsi(con);
		
		// STORES RSS
		storePmsiInTemp(con, rsfReader, pmsiOffset);
		
		// STORES GROUPS
		storeGroupsInTemp(con, groupsReader, pmsiOffset);
	}


	public void setStatus(final Connection con, final long id, final String status, final String finess,
			Object ... parameters) throws SQLException {
		if ((parameters.length & 1) != 0) {
			// ODD NUMBER OF PARAMETERS
			throw new SQLException("odd number of parameters");
		}

		try (final PreparedStatement ps = con.prepareStatement(
				"UPDATE plud_pmsiupload SET plud_processed = ?::plud_status, plud_finess = ?, "
						+ "plud_arguments = plud_arguments || hstore(?) WHERE plud_id = ?;")) {
			ps.setString(1, status);
			ps.setString(2, finess);
			Array parametersArray = con.createArrayOf("text", parameters);
			ps.setArray(3, parametersArray);
			ps.setLong(4, id);
			ps.execute();
		}
	}

	private static void storePmsiInTemp(final Connection con, final Reader reader, final Long pmsiOffset) throws SQLException, NumberFormatException, IOException {
		// STORES THE ELEMENTS
		try (final BufferedReader br = new BufferedReader(reader);
				final PreparedStatement ps = con.prepareStatement(
				"INSERT INTO pmel_temp(pmel_root, pmel_position, pmel_parent, pmel_line, pmel_content) VALUES (?, ?, ?, ?, ?);")) {
			String line;
			for (Long lineNb = 0L ; (line = br.readLine()) != null ; lineNb = Long.sum(lineNb, 1L)) {
				// FILLS STATEMENTS
				int posInLine = (int) (lineNb % 5);
				if (posInLine == 0L && lineNb != 0L) {
					ps.addBatch();
				}
				if (posInLine == 0 || posInLine == 3) {
					ps.setLong(posInLine + 1, line.startsWith(":") ? Long.parseLong(line.substring(1)) : null);
				} else if (posInLine == 1 || posInLine == 2) {
					ps.setLong(posInLine + 1, line.startsWith(":") ? Long.parseLong(line.substring(1)) + pmsiOffset: null);
				} else if (posInLine == 4) {
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

	public static void storeGroupsInTemp(final Connection con, final Reader reader, final long pmsiOffset) throws SQLException, NumberFormatException, IOException {
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
					ps.setLong(posInLine + 1, line.startsWith(":") ? Long.parseLong(line.substring(1)) + pmsiOffset : null);
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
	
}