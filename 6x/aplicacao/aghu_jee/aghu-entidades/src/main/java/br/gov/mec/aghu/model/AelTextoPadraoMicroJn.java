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


import org.hibernate.annotations.Immutable;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.model.BaseJournal;

@Entity
@SequenceGenerator(name = "aelLuvJnSeq", sequenceName = "AGH.AEL_LUV_JN_SEQ", allocationSize = 1)
@Table(name = "AEL_TEXTO_PADRAO_MICROS_JN", schema = "AGH")
@Immutable
public class AelTextoPadraoMicroJn extends BaseJournal {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1587356026494077884L;
	
	private Short luuSeq;
	private Short seqp;
	private String descricao;
	private DominioSituacao indSituacao;
	private Date criadoEm;
	private RapServidores servidor;
	private String apelido;

	public AelTextoPadraoMicroJn() {
	}


	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "aelLuvJnSeq")
	@Column(name = "SEQ_JN", unique = true, nullable = false)
	@Override
	public Integer getSeqJn() {
		return super.getSeqJn();
	}
	
	@Column(name = "LUU_SEQ", nullable = false, precision = 4, scale = 0)
	public Short getLuuSeq() {
		return this.luuSeq;
	}

	public void setLuuSeq(Short luuSeq) {
		this.luuSeq = luuSeq;
	}

	@Column(name = "SEQP", nullable = false, precision = 4, scale = 0)
	public Short getSeqp() {
		return this.seqp;
	}	
	
	public void setSeqp(Short seqp) {
		this.seqp = seqp;
	}
	
	@Column(name = "IND_SITUACAO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getIndSituacao() {
		return this.indSituacao;
	}

	public void setIndSituacao(final DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 7)
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
	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(final RapServidores servidor) {
		this.servidor = servidor;
	}
	
	@Column(name = "DESCRICAO", nullable = false, length = 2000)
	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}	

	@Column(name = "APELIDO", length = 50)
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
		AelTextoPadraoMicroJn other = (AelTextoPadraoMicroJn) obj;
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

		LUU_SEQ("luuSeq"),
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
