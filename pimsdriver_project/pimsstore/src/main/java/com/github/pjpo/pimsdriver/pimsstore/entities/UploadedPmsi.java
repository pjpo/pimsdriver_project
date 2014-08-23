package com.github.pjpo.pimsdriver.pimsstore.entities;

import java.time.LocalDate;
import java.util.Date;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * Represents an upload after the upload (with some more items set than PmsiUploadElementmodel)
 * @author jpc
 *
 */
@XmlAccessorType(XmlAccessType.NONE)
@Entity
@Table(schema="public", name = "plud_pmsiupload")
public class UploadedPmsi {

	/** Primary key */
	@XmlElement
	@Id
	@Column(name = "plud_id")
	private Long recordid;
	
	/** Finess Value. Must be non null */
	@NotNull
	@Column(name = "plud_finess")
	private String finess;

	/** Pmsi Year */
	@Column(name = "plud_year")
	private Integer pmsiYear;
	
	/** Pmsi Month */
	@Column(name= "plud_month")
	private Integer pmsiMonth;
	
	/** Date of data upload */
	@XmlElement
	@Column(name = "plud_dateenvoi")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateenvoi;
			
	/** Attributes */
	@XmlElement
	@Column(name = "plud_arguments")
	@Convert(converter = HstoreConverter.class)
	private Map<String, String> attributes;

	public Long getRecordid() {
		return recordid;
	}

	public void setRecordid(Long recordid) {
		this.recordid = recordid;
	}

	public String getFiness() {
		return finess;
	}

	public void setFiness(String finess) {
		this.finess = finess;
	}

	public Integer getPmsiYear() {
		return pmsiYear;
	}

	public void setPmsiYear(Integer pmsiYear) {
		this.pmsiYear = pmsiYear;
	}

	public Integer getPmsiMonth() {
		return pmsiMonth;
	}

	public void setPmsiMonth(Integer pmsiMonth) {
		this.pmsiMonth = pmsiMonth;
	}

	public Date getDateenvoi() {
		return dateenvoi;
	}

	public void setDateenvoi(Date dateenvoi) {
		this.dateenvoi = dateenvoi;
	}

	public Map<String, String> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, String> attributes) {
		this.attributes = attributes;
	}

	public LocalDate getPmsiDate() {
		return LocalDate.of(pmsiYear, pmsiMonth, 1);
	}

	public void setPmsiDate(final LocalDate pmsiDate) {
		pmsiYear = pmsiDate.getYear();
		pmsiMonth = pmsiDate.getMonthValue();
	}
}
