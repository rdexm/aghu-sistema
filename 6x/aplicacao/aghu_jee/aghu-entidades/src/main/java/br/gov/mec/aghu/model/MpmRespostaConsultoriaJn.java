package br.gov.mec.aghu.model;

// Generated 14/09/2010 17:49:55 by Hibernate Tools 3.2.5.Beta

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;


import org.hibernate.annotations.Immutable;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.model.BaseJournal;

@Entity
@Table(name = "MPM_RESPOSTA_CONSULTORIAS_JN", schema = "AGH")
@SequenceGenerator(name = "mpmRecJnSeq", sequenceName = "AGH.MPM_REC_JN_SEQ", allocationSize = 1)
@Immutable
public class MpmRespostaConsultoriaJn extends BaseJournal implements
		java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4027935675043569525L;
	private Integer scnAtdSeq;
	private Integer scnSeq;
	private Short trcSeq;
	private Date criadoEm;
	private Integer serMatricula;
	private Short serVinCodigo;
	private String indSituacao;
	private String descricao;

	public MpmRespostaConsultoriaJn() {
	}
	
	@Id
	@Column(name = "SEQ_JN", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mpmRecJnSeq")
	@Override
	public Integer getSeqJn() {
		return super.getSeqJn();
	}

	@Column(name = "SCN_ATD_SEQ", nullable = false, precision = 7, scale = 0)
	public Integer getScnAtdSeq() {
		return this.scnAtdSeq;
	}

	public void setScnAtdSeq(Integer scnAtdSeq) {
		this.scnAtdSeq = scnAtdSeq;
	}

	@Column(name = "SCN_SEQ", nullable = false, precision = 8, scale = 0)
	public Integer getScnSeq() {
		return this.scnSeq;
	}

	public void setScnSeq(Integer scnSeq) {
		this.scnSeq = scnSeq;
	}

	@Column(name = "TRC_SEQ", nullable = false, precision = 3, scale = 0)
	public Short getTrcSeq() {
		return this.trcSeq;
	}

	public void setTrcSeq(Short trcSeq) {
		this.trcSeq = trcSeq;
	}

	@Column(name = "CRIADO_EM", nullable = false, length = 7)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
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

	@Column(name = "IND_SITUACAO", length = 1)
	@Length(max = 1)
	public String getIndSituacao() {
		return this.indSituacao;
	}

	public void setIndSituacao(String indSituacao) {
		this.indSituacao = indSituacao;
	}

	@Column(name = "DESCRICAO", length = 0)
	@Length(max = 0)
	@Lob
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
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
		MpmRespostaConsultoriaJn other = (MpmRespostaConsultoriaJn) obj;
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

		SCN_ATD_SEQ("scnAtdSeq"),
		SCN_SEQ("scnSeq"),
		TRC_SEQ("trcSeq"),
		CRIADO_EM("criadoEm"),
		SER_MATRICULA("serMatricula"),
		SER_VIN_CODIGO("serVinCodigo"),
		IND_SITUACAO("indSituacao"),
		DESCRICAO("descricao");

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
