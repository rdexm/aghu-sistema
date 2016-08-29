package br.gov.mec.aghu.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


import br.gov.mec.aghu.dominio.DominioOrigemDocsDigitalizados;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.model.BaseJournal;

@Entity
@Table(name = "AIP_ORIGEM_DOC_GEDS_JN", schema = "AGH")
@SequenceGenerator(name = "AipOrigemDocGEDSJnSequence", sequenceName = "AGH.AIP_ODG_JN_SEQ")
public class AipOrigemDocGEDsJn extends BaseJournal {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4572107606639151413L;


	private Date criadoEm;
	private DominioSituacao indSituacao;
	private DominioOrigemDocsDigitalizados origem;
	private String referencia;
	private Integer seq;
	private RapServidores servidor;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false)
	public Date getCriadoEm() {
		return criadoEm;
	}

	@Column(name = "IND_SITUACAO",  length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}
	@Column(name = "ORIGEM", nullable = false, length = 60)
	@Enumerated(EnumType.STRING)
	public DominioOrigemDocsDigitalizados getOrigem() {
		return origem;
	}

	@Column(name = "REFERENCIA",  length = 60)
	public String getReferencia() {
		return referencia;
	}

	@Column(name = "SEQ", nullable = false, precision = 5, scale = 0)
	public Integer getSeq() {
		return seq;
	}
	
	@Id
	@Column(name = "SEQ_JN", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "AipOrigemDocGEDSJnSequence")
	@Override
	public Integer getSeqJn() {
		return super.getSeqJn();
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getServidor() {
		return servidor;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	public void setOrigem(DominioOrigemDocsDigitalizados origem) {
		this.origem = origem;
	}

	public void setReferencia(String referencia) {
		this.referencia = referencia;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}
	

	public enum Fields {
		SEQ("seq"),
		IND_SITUACAO("indSituacao"),
		REFERENCIA("referencia"),
		ORIGEM("origem"),
		SERVIDOR("servidor"),
		SER_MATRICULA_CADASTRO("servidor.id.matricula"),
		SER_VIN_CODIGO_CADASTRO("servidor.id.vinCodigo"),
		CRIADO_EM("criadoEm");
		
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
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
		if (!(obj instanceof AipOrigemDocGEDsJn)) {
			return false;
		}
		AipOrigemDocGEDsJn other = (AipOrigemDocGEDsJn) obj;
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
