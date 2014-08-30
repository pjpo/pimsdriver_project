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
@Table(schema="public", name="fava_rsfa_2012_view")
public class RsfA {

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
	@Column(name = "numfacture")
	private String numfacture;

	@XmlElement
	@Column(name = "numrss")
	private String numrss;

	@XmlElement
	@Column(name = "codess")
	private String codess;
		
	@XmlElement
	@Column(name = "sexe")
	private String sexe;

	@XmlElement
	@Column(name = "datenaissance")
	@Temporal(TemporalType.DATE)
	private Date datenaissance;
		
	@XmlElement
	@Column(name = "dateentree")
	@Temporal(TemporalType.DATE)
	private Date dateentree;
	
	@XmlElement
	@Column(name = "datesortie")
	@Temporal(TemporalType.DATE)
	private Date datesortie;

	@XmlElement
	@Column(name = "totalfacturehonoraire")
	private Long totalfacturehonoraire;

	@XmlElement
	@Column(name = "totalfactureph")
	private Long totalfactureph;
		
	@XmlElement
	@Column(name = "etatliquidation")
	private String etatliquidation;

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

	public String getNumfacture() {
		return numfacture;
	}

	public void setNumfacture(String numfacture) {
		this.numfacture = numfacture;
	}

	public String getNumrss() {
		return numrss;
	}

	public void setNumrss(String numrss) {
		this.numrss = numrss;
	}

	public String getCodess() {
		return codess;
	}

	public void setCodess(String codess) {
		this.codess = codess;
	}

	public String getSexe() {
		return sexe;
	}

	public void setSexe(String sexe) {
		this.sexe = sexe;
	}

	public Date getDatenaissance() {
		return datenaissance;
	}

	public void setDatenaissance(Date datenaissance) {
		this.datenaissance = datenaissance;
	}

	public Date getDateentree() {
		return dateentree;
	}

	public void setDateentree(Date dateentree) {
		this.dateentree = dateentree;
	}

	public Date getDatesortie() {
		return datesortie;
	}

	public void setDatesortie(Date datesortie) {
		this.datesortie = datesortie;
	}

	public Long getTotalfacturehonoraire() {
		return totalfacturehonoraire;
	}

	public void setTotalfacturehonoraire(Long totalfacturehonoraire) {
		this.totalfacturehonoraire = totalfacturehonoraire;
	}

	public Long getTotalfactureph() {
		return totalfactureph;
	}

	public void setTotalfactureph(Long totalfactureph) {
		this.totalfactureph = totalfactureph;
	}

	public String getEtatliquidation() {
		return etatliquidation;
	}

	public void setEtatliquidation(String etatliquidation) {
		this.etatliquidation = etatliquidation;
	}

}
