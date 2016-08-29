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
@Table(name = "AEL_GRP_MICRO_LACUNAS_JN", schema = "AGH")
@Immutable
public class AelGrpMicroLacunaJn extends BaseJournal {

	/**
	 * 
	 */
	private static final long serialVersionUID = -843865246785366233L;
	
	private Short luvLuuSeq;
	private Short luvSeqp;
	private Short seqp;
	private String lacuna;
	private DominioSituacao indSituacao;
	private Date criadoEm;
	private RapServidores servidor;

	public AelGrpMicroLacunaJn() {
	}


	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "aelLu0JnSeq")
	@Column(name = "SEQ_JN", unique = true, nullable = false)
	@Override
	public Integer getSeqJn() {
		return super.getSeqJn();
	}
	
	@Column(name = "LACUNA", nullable = false, length = 10)
	public String getLacuna() {
		return this.lacuna;
	}

	public void setLacuna(final String lacuna) {
		this.lacuna = lacuna;
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
	
	@Column(name = "LUV_LUU_SEQ", nullable = false, precision = 4, scale = 0)
	public Short getLuvLuuSeq() {
		return this.luvLuuSeq;
	}

	public void setLuvLuuSeq(Short luvLuuSeq) {
		this.luvLuuSeq = luvLuuSeq;
	}

	@Column(name = "LUV_SEQP", nullable = false, precision = 4, scale = 0)
	public Short getLuvSeqp() {
		return this.luvSeqp;
	}

	public void setLuvSeqp(Short luvSeqp) {
		this.luvSeqp = luvSeqp;
	}

	@Column(name = "SEQP", nullable = false, precision = 4, scale = 0)
	public Short getSeqp() {
		return this.seqp;
	}	

	public void setSeqp(Short seqp) {
		this.seqp = seqp;
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
		AelGrpMicroLacunaJn other = (AelGrpMicroLacunaJn) obj;
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

		LUV_LUU_SEQ("luvLuuSeq"),
		LUV_SEQP("luvSeqp"),
		SEQP("seqp"),
		LACUNA("lacuna"),
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
