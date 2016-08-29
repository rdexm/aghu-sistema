package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;


import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.Immutable;

import br.gov.mec.aghu.dominio.DominioSituacaoQualificacao;
import br.gov.mec.aghu.core.model.BaseJournal;

@Entity
@Table(name = "RAP_QUALIFICACOES_JN", schema = "AGH")
@SequenceGenerator(name = "rapQlfJnSeq", sequenceName = "AGH.RAP_QLF_JN_SEQ", allocationSize = 1)

@Immutable
public class RapQualificacaoJn extends BaseJournal implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2782600651795433706L;
	private Integer pesCodigo;
	private Short sequencia;
	private Date dtInicio;
	private Integer iqlCodigo;
	private Integer tqlCodigo;
	private Short tatCodigo;
	private Date dtFim;
	private DominioSituacaoQualificacao situacao;
	private String semestre;
	private Date dtAtualizacao;
	private String nroRegConselho;
	
	public RapQualificacaoJn(){
	}
	
	@Id
	@Column(name = "SEQ_JN", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "rapQlfJnSeq")
	@Override
	public Integer getSeqJn() {
		return super.getSeqJn();
	}

	@Column(name = "PES_CODIGO", length = 9, nullable = false)
	public Integer getPesCodigo() {
		return this.pesCodigo;
	}

	public void setPesCodigo(Integer pesCodigo) {
		this.pesCodigo = pesCodigo;
	}

	@Column(name = "SEQUENCIA", length = 3, nullable = false)
	public Short getSequencia() {
		return this.sequencia;
	}

	public void setSequencia(Short sequencia) {
		this.sequencia = sequencia;
	}

	@Column(name = "DT_INICIO")
	public Date getDtInicio() {
		return this.dtInicio;
	}

	public void setDtInicio(Date dtInicio) {
		this.dtInicio = dtInicio;
	}

	@Column(name = "IQL_CODIGO", length = 5)
	public Integer getIqlCodigo() {
		return this.iqlCodigo;
	}

	public void setIqlCodigo(Integer iqlCodigo) {
		this.iqlCodigo = iqlCodigo;
	}

	@Column(name = "TQL_CODIGO", length = 5)
	public Integer getTqlCodigo() {
		return this.tqlCodigo;
	}

	public void setTqlCodigo(Integer tqlCodigo) {
		this.tqlCodigo = tqlCodigo;
	}

	@Column(name = "TAT_CODIGO", length = 3)
	public Short getTatCodigo() {
		return this.tatCodigo;
	}

	public void setTatCodigo(Short tatCodigo) {
		this.tatCodigo = tatCodigo;
	}

	@Column(name = "DT_FIM")
	public Date getDtFim() {
		return this.dtFim;
	}

	public void setDtFim(Date dtFim) {
		this.dtFim = dtFim;
	}

	@Column(name = "SITUACAO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacaoQualificacao getSituacao() {
		return this.situacao;
	}

	public void setSituacao(DominioSituacaoQualificacao situacao) {
		this.situacao = situacao;
	}

	@Column(name = "SEMESTRE", length = 2)
	public String getSemestre() {
		return this.semestre;
	}

	public void setSemestre(String semestre) {
		this.semestre = semestre;
	}

	@Column(name = "DT_ATUALIZACAO")
	public Date getDtAtualizacao() {
		return this.dtAtualizacao;
	}

	public void setDtAtualizacao(Date dtAtualizacao) {
		this.dtAtualizacao = dtAtualizacao;
	}

	@Column(name = "NRO_REG_CONSELHO", length = 9)
	public String getNroRegConselho() {
		return this.nroRegConselho;
	}

	public void setNroRegConselho(String nroRegConselho) {
		this.nroRegConselho = nroRegConselho;
	}

	// outros

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("seqJn", this.getSeqJn())
				.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((getSeqJn() == null) ? 0 : getSeqJn().hashCode());
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
		RapQualificacaoJn other = (RapQualificacaoJn) obj;
		if (getSeqJn() == null) {
			if (other.getSeqJn() != null) {
				return false;
			}
		} else if (!getSeqJn().equals(other.getSeqJn())) {
			return false;
		}
		return true;
	}

	public enum Fields {
		PES_CODIGO("pesCodigo"), SEQUENCIA("sequencia"), DT_INICIO("dtInicio"), IQL_CODIGO(
				"iqlCodigo"), TQL_CODIGO("tqlCodigo"), TAT_CODIGO("tatCodigo"), DT_FIM(
				"dtFim"), SITUACAO("situacao"), SEMESTRE("semestre"), DT_ATUALIZACAO(
				"dtAtualizacao"), NRO_REG_CONSELHO("nroRegConselho")

		;

		private String field;

		private Fields(String field) {
			this.field = field;
		}

		@Override
		public String toString() {
			return this.field;
		}
	}

}