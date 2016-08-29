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
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Immutable;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.model.BaseJournal;

/**
 * The persistent class for the AEL_TXT_DESC_MATS_JN database table.
 * 
 */
@Entity
@Table(name = "AEL_TXT_DESC_MATS_JN", schema = "AGH")
@SequenceGenerator(name = "aelLdaJnSeq", sequenceName = "AGH.AEL_LDA_JN_SQ1",  allocationSize = 1)
@Immutable
public class AelTxtDescMatsJn extends BaseJournal {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8547678173334106817L;
	
	private Short gtmSeq;
	private Short seqp;
	private String descricao;
	private DominioSituacao indSituacao;
	private Date criadoEm;
	private RapServidores servidor;
	private String apelido;

	public AelTxtDescMatsJn() {
	}


	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "aelLdaJnSeq")
	@Column(name = "SEQ_JN", unique = true, nullable = false)
	@NotNull
	@Override
	public Integer getSeqJn() {
		return super.getSeqJn();
	}
	
	@Column(name = "GTM_SEQ", nullable = false, precision = 4, scale = 0)
	public Short getGtmSeq() {
		return this.gtmSeq;
	}

	public void setGtmSeq(Short gtmSeq) {
		this.gtmSeq = gtmSeq;
	}

	@Column(name = "SEQP", nullable = false, precision = 4, scale = 0)
	public Short getSeqp() {
		return this.seqp;
	}


	public void setSeqp(Short seqp) {
		this.seqp = seqp;
	}

	@Column(name = "IND_SITUACAO", nullable = false, length = 1)
	@NotNull
	@Enumerated(EnumType.STRING)
	public DominioSituacao getIndSituacao() {
		return this.indSituacao;
	}

	public void setIndSituacao(final DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 7)
	@NotNull
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(final Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable=false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable=false) })
	@NotNull
	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(final RapServidores servidor) {
		this.servidor = servidor;
	}
	
	@Column(name = "DESCRICAO", nullable = false, length = 2000)
	@NotNull
	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}	

	@Column(name = "APELIDO", length = 50)
	@NotNull
	public String getApelido() {
		return apelido;
	}

	public void setApelido(String apelido) {
		this.apelido = apelido;
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
		AelTxtDescMatsJn other = (AelTxtDescMatsJn) obj;
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

		GTM_SEQ("gtmSeq"),
		SEQP("seqp"),
		DESCRICAO("descricao"),
		IND_SITUACAO("indSituacao"),
		CRIADO_EM("criadoEm"),
		SERVIDOR("servidor"),
		APELIDO("apelido");

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