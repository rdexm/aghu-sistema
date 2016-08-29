package br.gov.mec.aghu.model;

// Generated 09/04/2012 16:32:53 by Hibernate Tools 3.4.0.CR1

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;


import org.hibernate.annotations.Immutable;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.persistence.BaseEntityId;

/**
 * ================================================================================
 *   ####   #####    ####   ######  #####   ##  ##   ####    ####    ####    #### 
 *  ##  ##  ##  ##  ##      ##      ##  ##  ##  ##  ##  ##  ##  ##  ##  ##  ##  ##
 *  ##  ##  #####    ####   ####    #####   ##  ##  ######  ##      ######  ##  ##
 *  ##  ##  ##  ##      ##  ##      ##  ##   ####   ##  ##  ##  ##  ##  ##  ##  ##
 *   ####   #####    ####   ######  ##  ##    ##    ##  ##   ####   ##  ##   #### 
 * ================================================================================
 *
 * A partir de uma análise originada pela tarefa #19993
 * esta model foi escolhida para ser apenas de leitura
 * no AGHU e por isso foi anotada como Immutable.
 *
 * Entretanto, caso esta entidade seja necessária na construção
 * de uma estória que necessite escrever dados no banco, este
 * comentário e esta anotação pode ser retirada desta model.
 */
@Immutable

@Entity
@Table(name = "AHD_CIDS_HOSPITAL_DIA", schema = "AGH")
public class AhdCidHospitalDia extends BaseEntityId<AhdCidHospitalDiaId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4652793314977493801L;
	private AhdCidHospitalDiaId id;
	private Integer version;
	private AghCid aghCid;
	private AhdHospitaisDia ahdHospitaisDia;
	private String indPrioridadeCid;

	public AhdCidHospitalDia() {
	}

	public AhdCidHospitalDia(AhdCidHospitalDiaId id, AghCid aghCid, AhdHospitaisDia ahdHospitaisDia) {
		this.id = id;
		this.aghCid = aghCid;
		this.ahdHospitaisDia = ahdHospitaisDia;
	}

	public AhdCidHospitalDia(AhdCidHospitalDiaId id, AghCid aghCid, AhdHospitaisDia ahdHospitaisDia, String indPrioridadeCid) {
		this.id = id;
		this.aghCid = aghCid;
		this.ahdHospitaisDia = ahdHospitaisDia;
		this.indPrioridadeCid = indPrioridadeCid;
	}

	@EmbeddedId
	@AttributeOverrides({ @AttributeOverride(name = "cidSeq", column = @Column(name = "CID_SEQ", nullable = false)),
			@AttributeOverride(name = "hodSeq", column = @Column(name = "HOD_SEQ", nullable = false)) })
	public AhdCidHospitalDiaId getId() {
		return this.id;
	}

	public void setId(AhdCidHospitalDiaId id) {
		this.id = id;
	}

	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CID_SEQ", nullable = false, insertable = false, updatable = false)
	public AghCid getAghCid() {
		return this.aghCid;
	}

	public void setAghCid(AghCid aghCid) {
		this.aghCid = aghCid;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "HOD_SEQ", nullable = false, insertable = false, updatable = false)
	public AhdHospitaisDia getAhdHospitaisDia() {
		return this.ahdHospitaisDia;
	}

	public void setAhdHospitaisDia(AhdHospitaisDia ahdHospitaisDia) {
		this.ahdHospitaisDia = ahdHospitaisDia;
	}

	@Column(name = "IND_PRIORIDADE_CID", length = 1)
	@Length(max = 1)
	public String getIndPrioridadeCid() {
		return this.indPrioridadeCid;
	}

	public void setIndPrioridadeCid(String indPrioridadeCid) {
		this.indPrioridadeCid = indPrioridadeCid;
	}

	public enum Fields {

		ID("id"),
		VERSION("version"),
		AGH_CID("aghCid"),
		AHD_HOSPITAIS_DIA("ahdHospitaisDia"),
		IND_PRIORIDADE_CID("indPrioridadeCid");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}


	// ##### GeradorEqualsHashCodeMain #####
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof AhdCidHospitalDia)) {
			return false;
		}
		AhdCidHospitalDia other = (AhdCidHospitalDia) obj;
		if (getId() == null) {
			if (other.getId() != null) {
				return false;
			}
		} else if (!getId().equals(other.getId())) {
			return false;
		}
		return true;
	}
	// ##### GeradorEqualsHashCodeMain #####

}
