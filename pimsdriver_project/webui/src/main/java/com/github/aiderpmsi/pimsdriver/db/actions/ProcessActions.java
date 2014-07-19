package com.github.aiderpmsi.pimsdriver.db.actions;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;

import com.github.aiderpmsi.pimsdriver.db.DataSourceSingleton;
import com.github.aiderpmsi.pimsdriver.dto.ProcessorDTO;
import com.github.aiderpmsi.pimsdriver.dto.model.UploadedPmsi;
import com.github.aiderpmsi.pimsdriver.dto.model.UploadedPmsi.Status;

public class ProcessActions extends DbAction {
	
	public ProcessActions(final ServletContext context) {
		super(context);
	}

	@SuppressWarnings("unused")
	public Boolean processPmsi(UploadedPmsi element) throws ActionException {

		// GETS THE DB CONNECTION
		try (Connection con = DataSourceSingleton.getConnection(getServletContext())) {

			// DTO
			try (ProcessorDTO dto = new ProcessorDTO(con, getServletContext())) {

				try {
					// CREATES THE TEMP TABLES
					dto.createTempTables();
					
					// PROCESS PMSIS
					
					// COPY RSF FILE
					String rssVersion = null;

					// COPY RSS FILES
					String rsfVersion = null;
					
					String finess = null;
					
					// CREATE THE DEFINITIVE PARTITION TABLES TO INSERT TEMP DATAS
					dto.createDefinitiveTables(element.recordid);
		
					// COPY TEMP TABLES INTO DEFINITIVE TABLES
					dto.copyTempTables(element.recordid);
					
					// CREATE CONSTRAINTS ON PMEL AND PMGR TABLES
					dto.createContraints(element.recordid);
		
					// UPDATE STATUS AND REAL FINESS
					List<String> parameters = new ArrayList<>();
					parameters.add("rsfversion");
					parameters.add(rsfVersion);
					
					if (rssVersion != null) { parameters.add("rssversion");parameters.add(rssVersion); }
					
					dto.setStatus(element.recordid, Status.successed, finess, parameters.toArray());
					
					// EVERYTHING WENT FINE, COMMIT
					con.commit();
				} catch (SQLException e) {
					// IF THE EXCEPTION IS DUE TO A SERIALIZATION EXCEPTION, WE HAVE TO RETRY THIS TREATMENT
					if (e instanceof SQLException && ((SQLException) e).getSQLState().equals("40001")) {
						// ROLLBACK, BUT RETRY LATER (DO NOT UPDATE STATUS)
						con.rollback();
						
						return false;
					} else {
						// IF WE HAVE AN ERROR, ROLLBACK TRANSACTION AND STORE THE REASON FOR THE FAILURE
						con.rollback();

						dto.setStatus(element.recordid, Status.failed,
								element.finess, "error", e.getMessage() == null ? e.getClass().toString() : e.getMessage());

						con.commit();
						
						return false;
					}
				}
			}
		} catch (SQLException e) {
			if (e.getSQLState().equals("40001")) {
				// IF SERIALISATION EXCEPTION, TRY LATER
			} else {
				// ANY OTHER AEXCEPTION HAS TO BE THROWN
				throw new ActionException(e);
			}
		}
		return true;
	}

}