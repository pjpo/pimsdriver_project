package com.github.pjpo.pimsdriver.processor.ejb;

import java.io.Reader;
import java.io.Writer;
import javax.ejb.Local;

import com.github.pjpo.pimsdriver.processor.ejb.RsfParser.ObservableValue.ObservableListener;

@Local
public interface RsfParser {

	public Writer getStoreWriter();
	
	public ObservableValue<Float> processRsf(ObservableListener<Float> observer);

	public Result getResult();
	
	public static class ObservableValue<R> {
		private R value;
		private final ObservableListener<R> observer;
		public ObservableValue(R value, ObservableListener<R> observer) {
			this.value = value;
			this.observer = observer;
		}
		public R getValue() {
			return value;
		}
		public void setValue(R value) {
			observer.valueChanged(this.value, value);
			this.value = value;
		}
		public static interface ObservableListener<R> {
			public void valueChanged(R oldValue, R newValue);
		}
	}
	
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
