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
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.model.BaseJournal;

@Entity
@SequenceGenerator(name = "aelLo2JnSeq", sequenceName = "AGH.AEL_LO2_JN_SEQ", allocationSize = 1)
@Table(name = "AEL_TXT_DIAG_LACUNAS_JN")
@Immutable
public class AelTxtDiagLacunasJn extends BaseJournal {


	/**
	 * 
	 */
	private static final long serialVersionUID = -8258085635121874599L;
	private Short lo1LujLuhSeq;
	private Short lo1LujSeqp;
	private Short lo1Seqp;
	private Short seqp;
	private String textoLacuna;
	private DominioSituacao indSituacao;
	private Date criadoEm;
	private RapServidores servidor;

	public AelTxtDiagLacunasJn() {
	}

	public AelTxtDiagLacunasJn(final Short lo1LujLuhSeq, final Short lo1LujSeqp, final Short lo1Seqp, final Short seqp, 
			final DominioSituacao indSituacao, final Date criadoEm, final RapServidores servidor, final String textoLacuna) {
		super();
		this.lo1LujLuhSeq = lo1LujLuhSeq;
		this.lo1LujSeqp = lo1LujSeqp;
		this.lo1Seqp = lo1Seqp;
		this.seqp = seqp;
		this.indSituacao = indSituacao;
		this.criadoEm = criadoEm;
		this.servidor = servidor;
		this.textoLacuna = textoLacuna;
	}

	public AelTxtDiagLacunasJn(final Short lo1LujLuhSeq, final Short lo1LujSeqp, final Short lo1Seqp, final Short seqp, 
			final DominioSituacao indSituacao, final Date criadoEm, final RapServidores servidor) {
		super();
		this.lo1LujLuhSeq = lo1LujLuhSeq;
		this.lo1LujSeqp = lo1LujSeqp;
		this.lo1Seqp = lo1Seqp;
		this.seqp = seqp;
		this.indSituacao = indSituacao;
		this.criadoEm = criadoEm;
		this.servidor = servidor;
	}

	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "aelLo2JnSeq")
	@Column(name = "SEQ_JN", unique = true, nullable = false)
	public Integer getSeqJn() {
		return super.getSeqJn();
	}

	@Column(name = "LO1_LUJ_LUH_SEQ", nullable = false, precision = 4, scale = 0)
	public Short getLo1LujLuhSeq() {
		return this.lo1LujLuhSeq;
	}

	public void setLo1LujLuhSeq(final Short lo1LujLuhSeq) {
		this.lo1LujLuhSeq = lo1LujLuhSeq;
	}

	@Column(name = "LO1_LUJ_SEQP", nullable = false, precision = 4, scale = 0)
	public Short getLo1LujSeqp() {
		return this.lo1LujSeqp;
	}

	public void setLo1LujSeqp(final Short lo1LujSeqp) {
		this.lo1LujSeqp = lo1LujSeqp;
	}

	@Column(name = "LO1_SEQP", nullable = false, precision = 4, scale = 0)
	public Short getLo1Seqp() {
		return this.lo1Seqp;
	}

	public void setLo1Seqp(final Short lo1Seqp) {
		this.lo1Seqp = lo1Seqp;
	}

	@Column(name = "SEQP", nullable = false, precision = 4, scale = 0)
	public Short getSeqp() {
		return this.seqp;
	}

	public void setSeqp(final Short seqp) {
		this.seqp = seqp;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(final RapServidores servidor) {
		this.servidor = servidor;
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
	@Column(name = "CRIADO_EM", nullable = false)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(final Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "TEXTO_LACUNA", length = 500)
	@Length(max = 500)
	public String getTextoLacuna() {
		return this.textoLacuna;
	}

	public void setTextoLacuna(final String textoLacuna) {
		this.textoLacuna = textoLacuna;
	}

	public enum Fields {

		LO1_LUJ_LUH_SEQ("lo1LujLuhSeq"),
		LO1_LUJ_SEQP("lo1LujSeqp"),
		LO1_SEQP("lo1Seqp"),
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
