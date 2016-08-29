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
 * The persistent class for the AEL_GRP_DESC_MAT_LACUNAS_JN database table.
 * 
 */
@Entity
@Table(name="AEL_GRP_DESC_MAT_LACUNAS_JN", schema = "AGH")
@SequenceGenerator(name = "aelGmlJnSq1", sequenceName = "AGH.AEL_GML_JN_SQ1", allocationSize = 1)
@Immutable
public class AelGrpDescMatLacunasJn extends BaseJournal {

	/**
	 * 
	 */
	private static final long serialVersionUID = -843865246785366233L;
	
	private Short gtmSeq;
	private Short ldaSeq;
	private Short seqp;
	private String lacuna;
	private DominioSituacao indSituacao;
	private Date criadoEm;
	private RapServidores servidor;

	public AelGrpDescMatLacunasJn() {
	}


	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "aelGmlJnSq1")
	@Column(name = "SEQ_JN", unique = true, nullable = false)
	@NotNull
	@Override
	public Integer getSeqJn() {
		return super.getSeqJn();
	}
	
	@Column(name = "LACUNA", nullable = false, length = 10)
	@NotNull
	public String getLacuna() {
		return this.lacuna;
	}

	public void setLacuna(final String lacuna) {
		this.lacuna = lacuna;
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
	
	@Column(name = "GTM_SEQ", nullable = false, precision = 4, scale = 0)
	public Short getGtmSeq() {
		return this.gtmSeq;
	}

	

	@Column(name = "LDA_SEQ", nullable = false, precision = 4, scale = 0)
	public Short getLdaSeq() {
		return this.ldaSeq;
	}


	@Column(name = "SEQP", nullable = false, precision = 4, scale = 0)
	public Short getSeqp() {
		return this.seqp;
	}
	
	public void setGtmSeq(Short gtmSeq) {
		this.gtmSeq = gtmSeq;
	}


	public void setLdaSeq(Short ldaSeq) {
		this.ldaSeq = ldaSeq;
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
		AelGrpDescMatLacunasJn other = (AelGrpDescMatLacunasJn) obj;
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
		LDA_SEQ("ldaSeq"),
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
