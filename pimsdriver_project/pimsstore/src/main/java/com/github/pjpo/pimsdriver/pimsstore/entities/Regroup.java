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
@Table(schema = "public", name = "pmgr_pmsigroups")
public class Regroup {

	@XmlElement
	@Id
	@Column(name = "pmgr_id")
	private Long recordId;
	
	@XmlElement
	@Column(name = "pmgr_racine")
	private String racine;
	
	@XmlElement
	@Column(name = "pmgr_modalite")
	private String modalite;
	
	@XmlElement
	@Column(name = "pmgr_gravite")
	private String gravite;
	
	@XmlElement
	@Column(name = "pmgr_erreur")
	private String erreur;

	public Long getRecordId() {
		return recordId;
	}

	public void setRecordId(Long recordId) {
		this.recordId = recordId;
	}

	public String getRacine() {
		return racine;
	}

	public void setRacine(String racine) {
		this.racine = racine;
	}

	public String getModalite() {
		return modalite;
	}

	public void setModalite(String modalite) {
		this.modalite = modalite;
	}

	public String getGravite() {
		return gravite;
	}

	public void setGravite(String gravite) {
		this.gravite = gravite;
	}

	public String getErreur() {
		return erreur;
	}

	public void setErreur(String erreur) {
		this.erreur = erreur;
	}
	
	
}
