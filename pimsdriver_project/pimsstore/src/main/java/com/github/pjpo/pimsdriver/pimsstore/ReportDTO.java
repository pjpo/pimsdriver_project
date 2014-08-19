package com.github.pjpo.pimsdriver.pimsstore;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Logger;

import com.github.pjpo.pimsdriver.pimsstore.ejb.Navigation;
import com.github.pjpo.pimsdriver.pimsstore.vaadin.DBQueryBuilder;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.util.sqlcontainer.query.OrderBy;

public class ReportDTO {
	
	@SuppressWarnings("unused")
	private static final Logger LOGGER = Logger.getLogger(ReportDTO.class.toString());

	public static LinkedHashMap<String, Long> readPmsiOverview(final Connection con,
			final Navigation.UploadedPmsi model, final String headerName) throws SQLException {

		try (final PreparedStatement ps = con.prepareStatement(
				"WITH RECURSIVE all_lines AS ( \n"
				+ "SELECT pmel_position, pmel_parent, pmel_type \n"
				+ "FROM public.pmel_pmsielement \n"
				+ "WHERE pmel_type = ? AND pmel_root = ? \n"
				+ "UNION \n"
				+ "SELECT pmel.pmel_position, pmel.pmel_parent, pmel.pmel_type \n"
				+ "FROM public.pmel_pmsielement pmel \n"
				+ "JOIN all_lines al \n"
				+ "ON al.pmel_position = pmel.pmel_parent \n"
				+ "WHERE pmel.pmel_root = ? \n"
				+ ") SELECT pmel_type, COUNT(pmel_type) nb  FROM all_lines \n"
				+ "WHERE pmel_type != ? \n"
				+ "GROUP BY pmel_type \n"
				+ "ORDER BY pmel_type;")) {

			// FILLS THE STATEMENT
			ps.setString(1, headerName);
			ps.setLong(2, model.recordid);
			ps.setLong(3, model.recordid);
			ps.setString(4, headerName);

			// EXECUTES THE QUERY AND FILLS THE OVERVIEW
			try (ResultSet rs = ps.executeQuery()) {
			
				final LinkedHashMap<String, Long> overview = new LinkedHashMap<>();
				
				while (rs.next()) {
					overview.put(rs.getString(1), rs.getLong(2));
			}
			return overview;
		}
	}

}
