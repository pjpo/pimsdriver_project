package com.github.pjpo.pimsdriver.pimsstore;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import com.github.pjpo.pimsdriver.pimsstore.ejb.Navigation.UploadedPmsi;
import com.github.pjpo.pimsdriver.pimsstore.vaadin.DBQueryBuilder;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.util.sqlcontainer.query.OrderBy;

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

	public static List<UploadedPmsi> getUploadedPmsi(final Connection con, final List<Filter> filters,
			final List<OrderBy> orders, final Integer first, final Integer rows) throws SQLException {

		// IN THIS QUERY, IT IS NOT POSSIBLE TO STORE THE QUERY (CAN CHANGE AT EVERY CALL)
		final StringBuilder query = new StringBuilder(
				"SELECT plud_id, plud_finess, plud_year, plud_month, "
				+ "plud_dateenvoi, hstore_to_array(plud_arguments) "
				+ "FROM plud_pmsiupload ");
		
		// PREPARES THE LIST OF ARGUMENTS FOR THIS QUERY
		final List<Object> queryArgs = new ArrayList<>();

		// CREATES THE FILTERS, THE ORDERS AND FILLS THE ARGUMENTS
		query.append(DBQueryBuilder.getWhereStringForFilters(filters, queryArgs)).
			append(DBQueryBuilder.getOrderStringForOrderBys(orders, queryArgs));
		
		// OFFSET AND LIMIT
		if (first != null)
			query.append(" OFFSET ").append(first.toString()).append(" ");
		if (rows != null && rows != 0)
			query.append(" LIMIT ").append(rows.toString()).append(" ");
		
		// CREATES THE DB STATEMENT
		try (PreparedStatement ps = con.prepareStatement(query.toString())) {

			for (int i = 0 ; i < queryArgs.size() ; i++) {
				ps.setObject(i + 1, queryArgs.get(i));
			}

			// EXECUTES THE QUERY
			try (final ResultSet rs = ps.executeQuery()) {
		
				// LIST OF ELEMENTS
				final List<UploadedPmsi> upeltslist = new ArrayList<>();
			
				// FILLS THE LIST OF ELEMENTS
				while (rs.next()) {
					// BEAN FOR THIS ITEM
					final UploadedPmsi element = new UploadedPmsi();

					// FILLS THE BEAN
					element.recordid = rs.getLong(1);
					element.finess = rs.getString(2);
					element.pmsiDate = LocalDate.of(rs.getInt(3), rs.getInt(4), 1);
					element.dateenvoi = rs.getTimestamp(5);
					final Object[] array = (Object[]) rs.getArray(6).getArray();
					element.attributes = new HashMap<String, String>();
					for (int i = 0 ; i < array.length ; i = i + 2) {
						element.attributes.put((String) array[i], (String) array[i + 1]);
					}
				
				// ADDS THE BEAN TO THE ELEMENTS
				upeltslist.add(element);

				}
				return upeltslist;
			}
		}
	}

}
