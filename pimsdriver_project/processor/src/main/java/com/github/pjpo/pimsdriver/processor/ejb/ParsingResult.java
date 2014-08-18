package com.github.pjpo.pimsdriver.processor.ejb;

import java.time.LocalDate;
import java.util.Collection;

public class ParsingResult {
		public Collection<String> errors;
		public LocalDate datePmsi;
		public String finess;
		public String version;
		public Long endPmsiPosition;
	}