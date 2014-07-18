package com.github.pjpo.pimsdriver.processor.ejb;

import java.io.Reader;
import java.util.Collection;
import java.util.concurrent.Future;

import javax.ejb.Local;

@Local
public interface RsfParser {
	
	public Future<Collection<String>> processRsf(Reader reader);

	public Result getResult();
	
	public static class Result {
		private final String finess;
		private final String version;
		private final Long endPmsiPosition;
		private final Reader reader;
		public Result(String finess, String version, Long endPmsiPosition,
				Reader reader) {
			this.finess = finess;
			this.version = version;
			this.endPmsiPosition = endPmsiPosition;
			this.reader = reader;
		}
		public String getFiness() {
			return finess;
		}
		public String getVersion() {
			return version;
		}
		public Long getEndPmsiPosition() {
			return endPmsiPosition;
		}
		public Reader getReader() {
			return reader;
		}
	}
}
