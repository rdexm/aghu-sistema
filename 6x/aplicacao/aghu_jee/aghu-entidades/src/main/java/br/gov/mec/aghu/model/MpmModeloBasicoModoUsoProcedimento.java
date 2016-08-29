package br.gov.mec.aghu.model;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;


import org.apache.commons.lang3.builder.ToStringBuilder;

import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.persistence.BaseEntityId;

@Entity
@Table(name = "MPM_MOD_BASIC_MODO_USO_PROCEDS", schema = "AGH")

public class MpmModeloBasicoModoUsoProcedimento extends BaseEntityId<MpmModeloBasicoModoUsoProcedimentoId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4513580012856603379L;
	private MpmModeloBasicoModoUsoProcedimentoId id;
	private MpmTipoModoUsoProcedimento tipoModoUsoProcedimento;
	private MpmModeloBasicoProcedimento modeloBasicoProcedimento;
	private RapServidores servidor;
	private Short quantidade;
	private Integer version;

	private enum ModBasicModoUsoProcedsExceptionCode implements
			BusinessExceptionCode {
		MPM_UPR_CK1
	}

	public MpmModeloBasicoModoUsoProcedimento() {
	}

	public MpmModeloBasicoModoUsoProcedimento(
			MpmModeloBasicoModoUsoProcedimentoId id,
			MpmTipoModoUsoProcedimento tipoModoUsoProcedimento,
			MpmModeloBasicoProcedimento modeloBasicoProcedimento,
			RapServidores servidor) {
		this.id = id;
		this.tipoModoUsoProcedimento = tipoModoUsoProcedimento;
		this.modeloBasicoProcedimento = modeloBasicoProcedimento;
		this.servidor = servidor;
	}

	public MpmModeloBasicoModoUsoProcedimento(
			MpmModeloBasicoModoUsoProcedimentoId id,
			MpmTipoModoUsoProcedimento tipoModoUsoProcedimento,
			MpmModeloBasicoProcedimento modeloBasicoProcedimento,
			RapServidores servidor, Short quantidade) {
		this.id = id;
		this.tipoModoUsoProcedimento = tipoModoUsoProcedimento;
		this.modeloBasicoProcedimento = modeloBasicoProcedimento;
		this.servidor = servidor;
		this.quantidade = quantidade;
	}

	@EmbeddedId
	@AttributeOverrides( {
			@AttributeOverride(name = "modeloBasicoPrescricaoSeq", column = @Column(name = "MBP_MDB_SEQ", nullable = false, precision = 5, scale = 0)),
			@AttributeOverride(name = "modeloBasicoProcedimentoSeq", column = @Column(name = "MBP_SEQ", nullable = false, precision = 4, scale = 0)),
			@AttributeOverride(name = "tipoModoUsoProcedimentoSeq", column = @Column(name = "TUP_PED_SEQ", nullable = false, precision = 4, scale = 0)),
			@AttributeOverride(name = "tipoModoUsoSeqp", column = @Column(name = "TUP_SEQP", nullable = false, precision = 3, scale = 0)) })
	public MpmModeloBasicoModoUsoProcedimentoId getId() {
		return this.id;
	}

	public void setId(MpmModeloBasicoModoUsoProcedimentoId id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "TUP_PED_SEQ", referencedColumnName = "PED_SEQ", nullable = false, insertable = false, updatable = false),
			@JoinColumn(name = "TUP_SEQP", referencedColumnName = "SEQP", nullable = false, insertable = false, updatable = false) })
	public MpmTipoModoUsoProcedimento getTipoModoUsoProcedimento() {
		return tipoModoUsoProcedimento;
	}

	public void setTipoModoUsoProcedimento(
			MpmTipoModoUsoProcedimento tipoModoUsoProcedimento) {
		this.tipoModoUsoProcedimento = tipoModoUsoProcedimento;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "MBP_MDB_SEQ", referencedColumnName = "MDB_SEQ", nullable = false, insertable = false, updatable = false),
			@JoinColumn(name = "MBP_SEQ", referencedColumnName = "SEQ", nullable = false, insertable = false, updatable = false) })
	public MpmModeloBasicoProcedimento getModeloBasicoProcedimento() {
		return modeloBasicoProcedimento;
	}

	public void setModeloBasicoProcedimento(
			MpmModeloBasicoProcedimento modeloBasicoProcedimento) {
		this.modeloBasicoProcedimento = modeloBasicoProcedimento;
	}

	/**
	 * @return the servidor
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getServidor() {
		return this.servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	@Column(name = "QUANTIDADE", precision = 3, scale = 0)
	public Short getQuantidade() {
		return this.quantidade;
	}

	public void setQuantidade(Short quantidade) {
		this.quantidade = quantidade;
	}

	// outros
	@Transient
	public String getDescricaoEditada() {
		StringBuffer descricao = new StringBuffer();
		if (this.getTipoModoUsoProcedimento() != null) {
			descricao.append(this.getTipoModoUsoProcedimento().getDescricao());
		}
		if (this.quantidade != null) {
			descricao.append(' ').append(this.getQuantidade());
		}
		if (this.tipoModoUsoProcedimento.getUnidadeMedidaMedica() != null) {
			descricao.append(' ')
					.append(this.getTipoModoUsoProcedimento()
								  .getUnidadeMedidaMedica().getDescricao()).append(" ; ");
		}
		 // else{
		// descricao += " ; ";
		// }
		return descricao.toString();
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
		MpmModeloBasicoModoUsoProcedimento other = (MpmModeloBasicoModoUsoProcedimento) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("id", this.getId()).toString();
	}

	public enum Fields {
		ID("id"), MBP_MDB_SEQ("id.modeloBasicoPrescricaoSeq"), MBP_SEQ(
				"id.modeloBasicoProcedimentoSeq"), TUP_PED_SEQ(
				"id.tipoModoUsoProcedimentoSeq"), TUP_SEQP("id.tipoModoUsoSeqp"), 
				TIPO_MODO_USO_PROCEDIMENTO("tipoModoUsoProcedimento"), 
				TIPO_MOD_USO_PROCEDIMENTO_PED_SEQ("tipoModoUsoProcedimento.id.pedSeq"),
				TIPO_MOD_USO_PROCEDIMENTO_SEQP("tipoModoUsoProcedimento.id.seqp"),
				MODELO_BASICO_PROCEDIMENTO("modeloBasicoProcedimento"), 
				SERVIDOR("servidor"), QUANTIDADE("quantidade");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

	@PrePersist
	@PreUpdate
	@SuppressWarnings("unused")
	private void validacoes() {

		if (!(this.quantidade == null || (this.quantidade != null && this.quantidade > 0))) {
			throw new BaseRuntimeException(
					ModBasicModoUsoProcedsExceptionCode.MPM_UPR_CK1);
		}
	}
	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
	

}
