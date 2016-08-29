package br.gov.mec.aghu.model;

// Generated 15/03/2011 13:46:58 by Hibernate Tools 3.2.5.Beta

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
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
@Table(name = "FAT_APAC_AREA_IRRADIADAS", schema = "AGH")
public class FatApacAreaIrradiada extends BaseEntityId<FatApacAreaIrradiadasId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -497298448497857562L;
	private FatApacAreaIrradiadasId id;
	private Integer cidSeq;

	public FatApacAreaIrradiada() {
	}

	public FatApacAreaIrradiada(FatApacAreaIrradiadasId id, Integer cidSeq) {
		this.id = id;
		this.cidSeq = cidSeq;
	}

	@EmbeddedId
	@AttributeOverrides( {
			@AttributeOverride(name = "capAtmNumero", column = @Column(name = "CAP_ATM_NUMERO", nullable = false, precision = 13, scale = 0)),
			@AttributeOverride(name = "capSeqp", column = @Column(name = "CAP_SEQP", nullable = false, precision = 2, scale = 0)),
			@AttributeOverride(name = "seqp", column = @Column(name = "SEQP", nullable = false, precision = 1, scale = 0)) })
	public FatApacAreaIrradiadasId getId() {
		return this.id;
	}

	public void setId(FatApacAreaIrradiadasId id) {
		this.id = id;
	}

	@Column(name = "CID_SEQ", nullable = false, precision = 5, scale = 0)
	public Integer getCidSeq() {
		return this.cidSeq;
	}

	public void setCidSeq(Integer cidSeq) {
		this.cidSeq = cidSeq;
	}

	public enum Fields {

		ID("id"),
		CID_SEQ("cidSeq");

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
		if (!(obj instanceof FatApacAreaIrradiada)) {
			return false;
		}
		FatApacAreaIrradiada other = (FatApacAreaIrradiada) obj;
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
