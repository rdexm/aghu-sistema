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
@SequenceGenerator(name = "aelLu0JnSeq", sequenceName = "AGH.AEL_LU0_JN_SEQ", allocationSize = 1)
@Table(name = "AEL_TXT_MICRO_LACUNAS_JN", schema = "AGH")
@Immutable
public class AelTxtMicroLacunaJn extends BaseJournal {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8913474919697674531L;
	private Short lu9LuvLuuSeq;
	private Short lu9LuvSeqp;
	private Short lu9Seqp;
	private Short seqp;
	private String textoLacuna;
	private DominioSituacao indSituacao;
	private Date criadoEm;
	private RapServidores servidor;

	public AelTxtMicroLacunaJn() {
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "aelLu0JnSeq")
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
	

	@Column(name = "LU9_LUV_LUU_SEQ", nullable = false, precision = 4, scale = 0)
	public Short getLu9LuvLuuSeq() {
		return lu9LuvLuuSeq;
	}

	public void setLu9LuvLuuSeq(Short lu9LuvLuuSeq) {
		this.lu9LuvLuuSeq = lu9LuvLuuSeq;
	}

	@Column(name = "LU9_LUV_SEQP", nullable = false, precision = 4, scale = 0)
	public Short getLu9LuvSeqp() {
		return lu9LuvSeqp;
	}

	public void setLu9LuvSeqp(Short lu9LuvSeqp) {
		this.lu9LuvSeqp = lu9LuvSeqp;
	}

	@Column(name = "LU9_SEQP", nullable = false, precision = 4, scale = 0)
	public Short getLu9Seqp() {
		return lu9Seqp;
	}

	public void setLu9Seqp(Short lu9Seqp) {
		this.lu9Seqp = lu9Seqp;
	}

	@Column(name = "SEQP", nullable = false, precision = 4, scale = 0)
	public Short getSeqp() {
		return seqp;
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
		AelTxtMicroLacunaJn other = (AelTxtMicroLacunaJn) obj;
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

		LU9_LUV_LUU_SEQ("lu9LuvLuuSeq"),
		LU9_LUV_SEQP("lu9LuvSeqp"),
		LU9_SEQP("lu9Seqp"),
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
