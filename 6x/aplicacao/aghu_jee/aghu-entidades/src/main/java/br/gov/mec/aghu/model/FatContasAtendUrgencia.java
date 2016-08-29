package br.gov.mec.aghu.model;

// Generated 15/03/2011 13:46:58 by Hibernate Tools 3.2.5.Beta

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


import org.hibernate.annotations.Immutable;

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
@Table(name = "FAT_CONTAS_ATEND_URGENCIA", schema = "AGH")
public class FatContasAtendUrgencia extends BaseEntityId<FatContasAtendUrgenciaId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8804384209764895525L;
	private FatContasAtendUrgenciaId id;
	private FatContasHospitalares contaHospitalar;

	public FatContasAtendUrgencia() {
	}

	public FatContasAtendUrgencia(FatContasAtendUrgenciaId id, FatContasHospitalares contaHospitalar) {
		this.id = id;
		this.contaHospitalar = contaHospitalar;
	}

	@EmbeddedId
	@AttributeOverrides( {
			@AttributeOverride(name = "cthSeq", column = @Column(name = "CTH_SEQ", nullable = false, precision = 8, scale = 0)),
			@AttributeOverride(name = "atuSeq", column = @Column(name = "ATU_SEQ", nullable = false, precision = 8, scale = 0)) })
	public FatContasAtendUrgenciaId getId() {
		return this.id;
	}

	public void setId(FatContasAtendUrgenciaId id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CTH_SEQ", nullable = false, insertable = false, updatable = false)
	public FatContasHospitalares getContaHospitalar() {
		return contaHospitalar;
	}

	public void setContaHospitalar(FatContasHospitalares contaHospitalar) {
		this.contaHospitalar = contaHospitalar;
	}
	public enum Fields {

		ID("id"),
		CONTA_HOSPITALAR("contaHospitalar");

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
		if (!(obj instanceof FatContasAtendUrgencia)) {
			return false;
		}
		FatContasAtendUrgencia other = (FatContasAtendUrgencia) obj;
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
