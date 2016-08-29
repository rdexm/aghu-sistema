package br.gov.mec.aghu.model;

// Generated 09/04/2012 16:32:53 by Hibernate Tools 3.4.0.CR1

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;


import org.hibernate.annotations.Immutable;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

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
@SequenceGenerator(name="mamAceSq1", sequenceName="AGH.MAM_ACE_SQ1", allocationSize = 1)
@Table(name = "MAM_ALTA_CONTRA_REFERENCIAS", schema = "AGH")
public class MamAltaContraReferencia extends BaseEntitySeq<Short> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6324145683756688332L;
	private Short seq;
	private Integer version;
	private MamAltaSumario mamAltaSumario;
	private String indDestino;
	private String observacao;
	private String descEsp;

	public MamAltaContraReferencia() {
	}

	public MamAltaContraReferencia(Short seq, MamAltaSumario mamAltaSumario, String indDestino) {
		this.seq = seq;
		this.mamAltaSumario = mamAltaSumario;
		this.indDestino = indDestino;
	}

	public MamAltaContraReferencia(Short seq, MamAltaSumario mamAltaSumario, String indDestino, String observacao, String descEsp) {
		this.seq = seq;
		this.mamAltaSumario = mamAltaSumario;
		this.indDestino = indDestino;
		this.observacao = observacao;
		this.descEsp = descEsp;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mamAceSq1")
	@Column(name = "SEQ", unique = true, nullable = false)
	public Short getSeq() {
		return this.seq;
	}

	public void setSeq(Short seq) {
		this.seq = seq;
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
	@JoinColumn(name = "ALS_SEQ", nullable = false)
	public MamAltaSumario getMamAltaSumario() {
		return this.mamAltaSumario;
	}

	public void setMamAltaSumario(MamAltaSumario mamAltaSumario) {
		this.mamAltaSumario = mamAltaSumario;
	}

	@Column(name = "IND_DESTINO", nullable = false, length = 1)
	@Length(max = 1)
	public String getIndDestino() {
		return this.indDestino;
	}

	public void setIndDestino(String indDestino) {
		this.indDestino = indDestino;
	}

	@Column(name = "OBSERVACAO", length = 500)
	@Length(max = 500)
	public String getObservacao() {
		return this.observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	@Column(name = "DESC_ESP", length = 100)
	@Length(max = 100)
	public String getDescEsp() {
		return this.descEsp;
	}

	public void setDescEsp(String descEsp) {
		this.descEsp = descEsp;
	}

	public enum Fields {

		SEQ("seq"),
		VERSION("version"),
		MAM_ALTA_SUMARIO("mamAltaSumario"),
		IND_DESTINO("indDestino"),
		OBSERVACAO("observacao"),
		DESC_ESP("descEsp");

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
		result = prime * result + ((getSeq() == null) ? 0 : getSeq().hashCode());
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
		if (!(obj instanceof MamAltaContraReferencia)) {
			return false;
		}
		MamAltaContraReferencia other = (MamAltaContraReferencia) obj;
		if (getSeq() == null) {
			if (other.getSeq() != null) {
				return false;
			}
		} else if (!getSeq().equals(other.getSeq())) {
			return false;
		}
		return true;
	}
	// ##### GeradorEqualsHashCodeMain #####

}
