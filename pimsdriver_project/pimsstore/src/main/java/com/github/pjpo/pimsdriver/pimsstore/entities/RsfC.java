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
@Table(schema="public", name="favc_rsfc_2012_view")
public class RsfC {

	@XmlElement
	@Id
	@Column(name = "pmel_id")
	private Long recordId;
	
	@XmlElement
	@Column(name = "pmel_root")
	private Long uploadRecordId;
	
	@XmlElement
	@Column(name = "dateacte")
	@Temporal(TemporalType.DATE)
	private Date dateacte;
	
	@XmlElement
	@Column(name = "codeacte")
	private String codeacte;
	
	@XmlElement
	@Column(name = "quantite")
	private Long quantite;
	
	@XmlElement
	@Column(name = "montanttotalhonoraire")
	private Long montanttotalhonoraire;

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

	public Date getDateacte() {
		return dateacte;
	}

	public void setDateacte(Date dateacte) {
		this.dateacte = dateacte;
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

	public Long getMontanttotalhonoraire() {
		return montanttotalhonoraire;
	}

	public void setMontanttotalhonoraire(Long montanttotalhonoraire) {
		this.montanttotalhonoraire = montanttotalhonoraire;
	}

}
