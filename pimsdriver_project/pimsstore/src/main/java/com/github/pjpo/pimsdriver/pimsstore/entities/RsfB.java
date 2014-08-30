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
@Table(schema="public", name="favb_rsfb_2012_view")
public class RsfB {

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
	@Column(name = "datedebutsejour")
	@Temporal(TemporalType.DATE)
	private Date datedebutsejour;
	
	@XmlElement
	@Column(name = "datefinsejour")
	@Temporal(TemporalType.DATE)
	private Date datefinsejour;
	
	@XmlElement
	@Column(name = "codeacte")
	private String codeacte;
	
	@XmlElement
	@Column(name = "quantite")
	private Long quantite;
	
	@XmlElement
	@Column(name = "numghs")
	private String numghs;
	
	@XmlElement
	@Column(name = "montanttotaldepense")
	private Long montanttotaldepense;

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

	public Date getDatedebutsejour() {
		return datedebutsejour;
	}

	public void setDatedebutsejour(Date datedebutsejour) {
		this.datedebutsejour = datedebutsejour;
	}

	public Date getDatefinsejour() {
		return datefinsejour;
	}

	public void setDatefinsejour(Date datefinsejour) {
		this.datefinsejour = datefinsejour;
	}

	public String getCodeacte() {
		return codeacte;
	}

	public void setCodeacte(String codeacte) {
		this.codeacte = codeacte;
	}

	public Long getQuantite() {
		return quantite;
	}

	public void setQuantite(Long quantite) {
		this.quantite = quantite;
	}

	public String getNumghs() {
		return numghs;
	}

	public void setNumghs(String numghs) {
		this.numghs = numghs;
	}

	public Long getMontanttotaldepense() {
		return montanttotaldepense;
	}

	public void setMontanttotaldepense(Long montanttotaldepense) {
		this.montanttotaldepense = montanttotaldepense;
	}

}
