package com.github.pjpo.pimsdriver.pimsstore;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class NavigationDTO {
	
	@SuppressWarnings("unused")
	private static final Logger LOGGER = Logger.getLogger(NavigationDTO.class.toString());

	
	public static List<String> getFinesses(final Connection con) throws SQLException {
		try (final PreparedStatement ps = con.prepareStatement(
				"SELECT DISTINCT(plud_finess) FROM plud_pmsiupload ORDER BY plud_finess ASC;")) {

			// RETURNED LIST
			final ArrayList<String> finesses = new ArrayList<String>();
			
			// EXECUTE QUERY
			try (final ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					finesses.add(rs.getString(1));
				}
			}
			return finesses;
		}
	}
	
	public static List<LocalDate> getPmsiDates(final Connection con, final String finess) throws SQLException {
		try (final PreparedStatement ps = con.prepareStatement(
				"SELECT DISTINCT ON (plud_year, plud_month) plud_year, plud_month FROM plud_pmsiupload"
				+ " WHERE plud_finess = ? ORDER BY plud_year DESC, plud_month DESC;")) {

			// FILLS THE STATEMENT
			ps.setString(1, finess);

			// RETURNED LIST
			final ArrayList<LocalDate> pmsiDates = new ArrayList<>();
			
			// EXECUTES THE QUERY AND FILLS THE LIST OF YM
			try (ResultSet rs = ps.executeQuery()) {
			
				while (rs.next()) {
					final LocalDate pmsiDate = LocalDate.of(rs.getInt(1), rs.getInt(2), 1);
					pmsiDates.add(pmsiDate);
				}
				
			}

			return pmsiDates;
		}
	}

}
