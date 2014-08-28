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
@Table(schema="public", name = "smva_rssmain_116_view")
public class RssMain {

	@XmlElement
	@Id
	@Column(name = "pmel_id")
	private Long recordId;
	
	@XmlElement
	@Column(name = "pmel_position")
	private Long positionInPmsi;
	
	@XmlElement
	@Column(name = "pmel_line")
	private Long lineInPmsi;
	
	@XmlElement
	@Column(name = "numrss")
	private String numrss;
	
	@XmlElement
	@Column(name = "numlocalsejour")
	private String numlocalsejour;
	
	@XmlElement
	@Column(name = "numrum")
	private String numrum;
	
	@XmlElement
	@Column(name = "numunitemedicale")
	private String numunitemedicale;
	
	@XmlElement
	@Column(name = "ghm")
	private String ghm;
	
	@XmlElement
	@Column(name = "ghmcorrige")
	private String ghmcorrige;

	@XmlElement
	@Column(name = "dp")
	private String dp;
	
	@XmlElement
	@Column(name = "dr")
	private String dr;
	
	@XmlElement
	@Column(name = "nbseances")
	private Integer nbseances;
	
	@XmlElement
	@Column(name = "dateentree")
	@Temporal(TemporalType.DATE)
	private Date dateentree;
	
	@XmlElement
	@Column(name = "datesortie")
	@Temporal(TemporalType.DATE)
	private Date datesortie;

	public Long getRecordId() {
		return recordId;
	}

	public void setRecordId(Long recordId) {
		this.recordId = recordId;
	}

	public Long getPositionInPmsi() {
		return positionInPmsi;
	}

	public void setPositionInPmsi(Long positionInPmsi) {
		this.positionInPmsi = positionInPmsi;
	}

	public Long getLineInPmsi() {
		return lineInPmsi;
	}

	public void setLineInPmsi(Long lineInPmsi) {
		this.lineInPmsi = lineInPmsi;
	}

	public String getNumrss() {
		return numrss;
	}

	public void setNumrss(String numrss) {
		this.numrss = numrss;
	}

	public String getNumlocalsejour() {
		return numlocalsejour;
	}

	public void setNumlocalsejour(String numlocalsejour) {
		this.numlocalsejour = numlocalsejour;
	}

	public String getNumrum() {
		return numrum;
	}

	public void setNumrum(String numrum) {
		this.numrum = numrum;
	}

	public String getNumunitemedicale() {
		return numunitemedicale;
	}

	public void setNumunitemedicale(String numunitemedicale) {
		this.numunitemedicale = numunitemedicale;
	}

	public String getGhm() {
		return ghm;
	}

	public void setGhm(String ghm) {
		this.ghm = ghm;
	}

	public String getGhmcorrige() {
		return ghmcorrige;
	}

	public void setGhmcorrige(String ghmcorrige) {
		this.ghmcorrige = ghmcorrige;
	}

	public String getDp() {
		return dp;
	}

	public void setDp(String dp) {
		this.dp = dp;
	}

	public String getDr() {
		return dr;
	}

	public void setDr(String dr) {
		this.dr = dr;
	}

	public Integer getNbseances() {
		return nbseances;
	}

	public void setNbseances(Integer nbseances) {
		this.nbseances = nbseances;
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

	
}