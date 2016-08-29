package br.gov.mec.aghu.model;

// Generated 17/10/2011 16:43:13 by Hibernate Tools 3.4.0.CR1

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;


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
@Table(name = "EPE_PRESC_CUID_EXAMES", schema = "AGH")
public class EpePrescCuidExame extends BaseEntityId<EpePrescCuidExameId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7841736547914306677L;
	private EpePrescCuidExameId id;
	private Integer version;
	private EpePrescricoesCuidados prescricaoCuidado;

	public EpePrescCuidExame() {
	}

	public EpePrescCuidExame(EpePrescCuidExameId id) {
		this.id = id;
	}

	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "prcSeq", column = @Column(name = "PRC_SEQ", nullable = false, precision = 8, scale = 0)),
			@AttributeOverride(name = "prcAtdSeq", column = @Column(name = "PRC_ATD_SEQ", nullable = false, precision = 9, scale = 0)),
			@AttributeOverride(name = "cexCuiSeq", column = @Column(name = "CEX_CUI_SEQ", nullable = false, precision = 4, scale = 0)),
			@AttributeOverride(name = "cexEmaExaSigla", column = @Column(name = "CEX_EMA_EXA_SIGLA", nullable = false, length = 5)),
			@AttributeOverride(name = "cexEmaManSeq", column = @Column(name = "CEX_EMA_MAN_SEQ", nullable = false, precision = 5, scale = 0)) })
	public EpePrescCuidExameId getId() {
		return this.id;
	}

	public void setId(EpePrescCuidExameId id) {
		this.id = id;
	}

	@Version
	@Column(name = "VERSION", nullable = false, precision = 9, scale = 0)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "PRC_ATD_SEQ", referencedColumnName = "ATD_SEQ", nullable = false, insertable = false, updatable = false),
			@JoinColumn(name = "PRC_SEQ", referencedColumnName = "SEQ", nullable = false, insertable = false, updatable = false) })
	public EpePrescricoesCuidados getPrescricaoCuidado() {
		return this.prescricaoCuidado;
	}

	public void setPrescricaoCuidado(
			EpePrescricoesCuidados prescricaoCuidado) {
		this.prescricaoCuidado = prescricaoCuidado;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		if (getClass() != obj.getClass()) {
			return false;
		}
		EpePrescCuidExame other = (EpePrescCuidExame) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}


	public enum Fields {

		ID("id"),
		PRESCRICAO_CUIDADO("prescricaoCuidado");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}

}
