package com.github.pjpo.pimsdriver.pimsstore.ejb;

import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.ejb.Local;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.util.sqlcontainer.query.OrderBy;

@Local
public interface Navigation {

	public List<String> getFinesses();
	
	public List<LocalDate> getPmsiDates(String finess);
	
	public List<UploadedPmsi> getUploadedPmsi(final List<Filter> filters, final List<OrderBy> orders,
			final Integer first, final Integer rows);
	
	/**
	 * Represents an upload after the upload (with some more items set than PmsiUploadElementmodel)
	 * @author jpc
	 *
	 */
	@XmlAccessorType(XmlAccessType.NONE)
	public static class UploadedPmsi {

		/** Primary key */
		@XmlElement
		public Long recordid;
		
		/** Date of data upload */
		@XmlElement
		public Date dateenvoi;
				
		/** ID of the RSF OID */
		public Long rsfoid;
		
		/** ID of the RSS OID */
		public Long rssoid;

		/** Attributes */
		@XmlElement
		public HashMap<String, String> attributes;

		/** Pmsi Date. Must be not null*/
		@NotNull
		public LocalDate pmsiDate;

		/** Finess Value. Must be non null */
		@NotNull
		public String finess;

		public Long getRecordid() {
			return recordid;
		}

		public void setRecordid(Long recordid) {
			this.recordid = recordid;
		}

		public Date getDateenvoi() {
			return dateenvoi;
		}

		public void setDateenvoi(Date dateenvoi) {
			this.dateenvoi = dateenvoi;
		}

		public Long getRsfoid() {
			return rsfoid;
		}

		public void setRsfoid(Long rsfoid) {
			this.rsfoid = rsfoid;
		}

		public Long getRssoid() {
			return rssoid;
		}

		public void setRssoid(Long rssoid) {
			this.rssoid = rssoid;
		}

		public HashMap<String, String> getAttributes() {
			return attributes;
		}

		public void setAttributes(HashMap<String, String> attributes) {
			this.attributes = attributes;
		}

		public LocalDate getPmsiDate() {
			return pmsiDate;
		}

		public void setPmsiDate(LocalDate pmsiDate) {
			this.pmsiDate = pmsiDate;
		}

		public String getFiness() {
			return finess;
		}

		public void setFiness(String finess) {
			this.finess = finess;
		}
		
	}

}
