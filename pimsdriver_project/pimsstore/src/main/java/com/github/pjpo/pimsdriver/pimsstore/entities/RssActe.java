package com.github.pjpo.pimsdriver.pimsstore.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.NONE)
@Entity
@Table(schema="public", name="sava_rssacte_116_view")
public class RssActe {

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
	@Column(name = "daterealisation")
	@Temporal(TemporalType.DATE)
	private Date daterealisation;
	
	@XmlElement
	@Column(name = "codeccam")
	private String codeccam;

	@XmlElement
	@Column(name = "phase")
	private String phase;
	
	@XmlElement
	@Column(name = "activite")
	private String activite;
	
	@XmlElement
	@Column(name = "nbacte")
	private Integer nbacte;

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

	public Date getDaterealisation() {
		return daterealisation;
	}

	public void setDaterealisation(Date daterealisation) {
		this.daterealisation = daterealisation;
	}

	public String getCodeccam() {
		return codeccam;
	}

	public void setCodeccam(String codeccam) {
		this.codeccam = codeccam;
	}

	public String getPhase() {
		return phase;
	}

	public void setPhase(String phase) {
		this.phase = phase;
	}

	public String getActivite() {
		return activite;
	}

	public void setActivite(String activite) {
		this.activite = activite;
	}

	public Integer getNbacte() {
		return nbacte;
	}

	public void setNbacte(Integer nbacte) {
		this.nbacte = nbacte;
	}

}
