package br.gov.mec.aghu.model;

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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


import org.hibernate.annotations.Immutable;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.model.BaseJournal;

@Entity
@Table(name = "AFA_TIPO_USO_MDTOS_JN", schema = "AGH")
@SequenceGenerator(name = "afaTumJnSeq", sequenceName = "AGH.AFA_TUM_JN_SEQ", allocationSize = 1)

@Immutable
public class AfaTipoUsoMdtoJn extends BaseJournal implements
		java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1583526691781523478L;
	private String sigla;
	private Integer gupSeq;
	private Integer serMatricula;
	private Short serVinCodigo;
	private String descricao;
	private Boolean indAntimicrobiano;
	private Boolean indExigeJustificativa;
	private Boolean indAvaliacao;
	private Boolean indExigeDuracaoSolicitada;
	private Date criadoEm;
	private DominioSituacao indSituacao;
	private Boolean indControlado;
	private Boolean indQuimioterapico;

	public AfaTipoUsoMdtoJn() {
	}
	
	@Id
	@Column(name = "SEQ_JN", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "afaTumJnSeq")
	@Override
	public Integer getSeqJn() {
		return super.getSeqJn();
	}

	@Column(name = "SIGLA", unique = true, nullable = false, length = 2)
	@Length(max = 2)
	public String getSigla() {
		return this.sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	@Column(name = "GUP_SEQ", nullable = false, precision = 3, scale = 0)
	public Integer getGupSeq() {
		return this.gupSeq;
	}

	public void setGupSeq(Integer gupSeq) {
		this.gupSeq = gupSeq;
	}

	@Column(name = "SER_MATRICULA", precision = 7, scale = 0)
	public Integer getSerMatricula() {
		return this.serMatricula;
	}

	public void setSerMatricula(Integer serMatricula) {
		this.serMatricula = serMatricula;
	}

	@Column(name = "SER_VIN_CODIGO", precision = 3, scale = 0)
	public Short getSerVinCodigo() {
		return this.serVinCodigo;
	}

	public void setSerVinCodigo(Short serVinCodigo) {
		this.serVinCodigo = serVinCodigo;
	}

	@Column(name = "DESCRICAO", nullable = false, length = 60)
	@Length(max = 60)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Column(name = "IND_ANTIMICROBIANO", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndAntimicrobiano() {
		return this.indAntimicrobiano;
	}

	public void setIndAntimicrobiano(Boolean indAntimicrobiano) {
		this.indAntimicrobiano = indAntimicrobiano;
	}

	@Column(name = "IND_EXIGE_JUSTIFICATIVA", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndExigeJustificativa() {
		return this.indExigeJustificativa;
	}

	public void setIndExigeJustificativa(Boolean indExigeJustificativa) {
		this.indExigeJustificativa = indExigeJustificativa;
	}

	@Column(name = "IND_AVALIACAO", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndAvaliacao() {
		return this.indAvaliacao;
	}

	public void setIndAvaliacao(Boolean indAvaliacao) {
		this.indAvaliacao = indAvaliacao;
	}

	@Column(name = "IND_EXIGE_DURACAO_SOLICITADA", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndExigeDuracaoSolicitada() {
		return this.indExigeDuracaoSolicitada;
	}

	public void setIndExigeDuracaoSolicitada(Boolean indExigeDuracaoSolicitada) {
		this.indExigeDuracaoSolicitada = indExigeDuracaoSolicitada;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "CRIADO_EM", nullable = false, length = 7)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "IND_SITUACAO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getIndSituacao() {
		return this.indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	@Column(name = "IND_CONTROLADO", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndControlado() {
		return this.indControlado;
	}

	public void setIndControlado(Boolean indControlado) {
		this.indControlado = indControlado;
	}

	@Column(name = "IND_QUIMIOTERAPICO", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndQuimioterapico() {
		return this.indQuimioterapico;
	}

	public void setIndQuimioterapico(Boolean indQuimioterapico) {
		this.indQuimioterapico = indQuimioterapico;
	}

	/**
	 * FIELDS
	 * 
	 * @author bsoliveira
	 * 
	 */
	public enum Fields {

		ID("seqJn"), NOME_USUARIO("nomeUsuario"), DATA_ALTERACAO(
				"dataAlteracao"), OPERACAO("operacao"), SIGLA("sigla"), SERVIDOR_MATRICULA(
				"serMatricula"), SERVIDOR_VIN_CODIGO("serVinCodigo"), DESCRICAO(
				"descricao"), IND_ANTIMICROBIANO("indAntimicrobiano"), IND_EXIGE_JUSTIFICATIVA(
				"indExigeJustificativa"), IND_AVALIACAO("indAvaliacao"), IND_EXIGE_DURACAO_SOLICITADA(
				"indExigeDuracaoSolicitada"), CRIADO_EM("criadoEm"), IND_SITUACAO(
				"indSituacao"), IND_CONTROLADO("indControlado"), IND_QUIMIOTERAPICO(
				"indQuimioterapico");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}

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
		AfaTipoUsoMdtoJn other = (AfaTipoUsoMdtoJn) obj;
		if (getSeqJn() == null) {
			if (other.getSeqJn() != null) {
				return false;
			}
		} else if (!getSeqJn().equals(other.getSeqJn())) {
			return false;
		}
		return true;
	}
}
