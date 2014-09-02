package com.github.pjpo.pimsdriver.pimsstore.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.NONE)
@Entity
@Table(schema="public", name="sdva_rssda_116_view")
public class RssDa {

	@XmlElement
	@Id
	@Column(name = "pmel_id")
	private Long recordId;
	
	@XmlElement
	@Column(name = "pmel_root")
	private Long uploadRecordId;
	
	@XmlElement
	@Column(name = "pmel_position")
	private Long positionInPmsi;

	@XmlElement
	@Column(name = "pmel_parent")
	private Long parentPositionInPmsi;
	
	@XmlElement
	@Column(name = "pmel_line")
	private Long lineInPmsi;

	@XmlElement
	@Column(name = "da")
	private String da;

	public Long getRecordId() {
		return recordId;
	}

	public void setRecordId(Long recordId) {
		this.recordId = recordId;
	}

	public Long getUploadRecordId() {
		return uploadRecordId;
	}

	public void setUploadRecordId(Long uploadRecordId) {
		this.uploadRecordId = uploadRecordId;
	}

	public Long getPositionInPmsi() {
		return positionInPmsi;
	}

	public void setPositionInPmsi(Long positionInPmsi) {
		this.positionInPmsi = positionInPmsi;
	}

	public Long getParentPositionInPmsi() {
		return parentPositionInPmsi;
	}

	public void setParentPositionInPmsi(Long parentPositionInPmsi) {
		this.parentPositionInPmsi = parentPositionInPmsi;
	}

	public Long getLineInPmsi() {
		return lineInPmsi;
	}

	public void setLineInPmsi(Long lineInPmsi) {
		this.lineInPmsi = lineInPmsi;
	}

	public String getDa() {
		return da;
	}

	public void setDa(String da) {
		this.da = da;
	}

}
