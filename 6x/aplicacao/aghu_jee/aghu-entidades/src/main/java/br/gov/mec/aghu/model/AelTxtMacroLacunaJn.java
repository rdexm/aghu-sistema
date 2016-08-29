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
@SequenceGenerator(name = "aelLo4JnSeq", sequenceName = "AGH.AEL_LO4_JN_SEQ", allocationSize = 1)
@Table(name = "AEL_TXT_MACRO_LACUNAS_JN", schema = "AGH")
@Immutable
public class AelTxtMacroLacunaJn extends BaseJournal {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1174103387938804305L;
	
	private Short lo3LufLubSeq;
	private Short lo3LufSeqp;
	private Short lo3Seqp;
	private Short seqp;
	private String textoLacuna;
	private DominioSituacao indSituacao;
	private Date criadoEm;
	private RapServidores servidor;

	public AelTxtMacroLacunaJn() {
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "aelLo4JnSeq")
	@Column(name = "SEQ_JN", unique = true, nullable = false)
	@Override
	public Integer getSeqJn() {
		return super.getSeqJn();
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
	
	@Column(name = "LO3_LUF_LUB_SEQ", nullable = false, precision = 4, scale = 0)
	public Short getLo3LufLubSeq() {
		return this.lo3LufLubSeq;
	}

	public void setLo3LufLubSeq(Short lo3LufLubSeq) {
		this.lo3LufLubSeq = lo3LufLubSeq;
	}

	@Column(name = "LO3_LUF_SEQP", nullable = false, precision = 4, scale = 0)
	public Short getLo3LufSeqp() {
		return this.lo3LufSeqp;
	}

	public void setLo3LufSeqp(Short lo3LufSeqp) {
		this.lo3LufSeqp = lo3LufSeqp;
	}

	@Column(name = "LO3_SEQP", nullable = false, precision = 4, scale = 0)
	public Short getLo3Seqp() {
		return this.lo3Seqp;
	}

	public void setLo3Seqp(Short lo3Seqp) {
		this.lo3Seqp = lo3Seqp;
	}

	@Column(name = "SEQP", nullable = false, precision = 4, scale = 0)
	public Short getSeqp() {
		return this.seqp;
	}

	public void setSeqp(Short seqp) {
		this.seqp = seqp;
	}

	@Column(name = "TEXTO_LACUNA", length = 500)
	public String getTextoLacuna() {
		return textoLacuna;
	}

	public void setTextoLacuna(String textoLacuna) {
		this.textoLacuna = textoLacuna;
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
		AelTxtMacroLacunaJn other = (AelTxtMacroLacunaJn) obj;
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

		LO3_LUF_LUB_SEQ("lo3LufLubSeq"),
		LO3_LUF_SEQP("lo3LufSeqp"),
		LO3_SEQP("lo3Seqp"),
		SEQP("seqp"),
		TEXTO_LACUNA("textoLacuna"),
		IND_SITUACAO("indSituacao"),
		CRIADO_EM("criadoEm"),
		SERVIDOR("servidor");

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
