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
@SequenceGenerator(name = "aelLo1JnSeq", sequenceName = "AGH.AEL_LO1_JN_SEQ", allocationSize = 1) 
@Table(name = "AEL_GRP_DIAG_LACUNAS_JN")
@Immutable
public class AelGrpDiagLacunasJn extends BaseJournal {

	private static final long serialVersionUID = -4106094843072703124L;

	private Short lujLuhSeq;
	private Short lujSeqp;
	private Short seqp;
	private String lacuna;
	private DominioSituacao indSituacao;
	private Date criadoEm;
	private RapServidores servidor;

	public AelGrpDiagLacunasJn() {
	}

	public AelGrpDiagLacunasJn(final Short lujLuhSeq, final Short lujSeqp, final Short seqp, 
			final DominioSituacao indSituacao, final Date criadoEm, final RapServidores servidor, final String lacuna) {
		super();
		this.lujLuhSeq = lujLuhSeq;
		this.lujSeqp = lujSeqp;
		this.seqp = seqp;
		this.indSituacao = indSituacao;
		this.criadoEm = criadoEm;
		this.servidor = servidor;
		this.lacuna = lacuna;
	}

	public AelGrpDiagLacunasJn(final Short lujLuhSeq, final Short lujSeqp, final Short seqp, 
			final DominioSituacao indSituacao, final Date criadoEm, final RapServidores servidor) {
		super();
		this.lujLuhSeq = lujLuhSeq;
		this.lujSeqp = lujSeqp;
		this.seqp = seqp;
		this.indSituacao = indSituacao;
		this.criadoEm = criadoEm;
		this.servidor = servidor;
	}

	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "aelLo1JnSeq")
	@Column(name = "SEQ_JN", unique = true, nullable = false)
	public Integer getSeqJn() {
		return super.getSeqJn();
	}

	@Column(name = "LUJ_LUH_SEQ", nullable = false, precision = 4, scale = 0)
	public Short getLujLuhSeq() {
		return this.lujLuhSeq;
	}

	public void setLujLuhSeq(final Short lujLuhSeq) {
		this.lujLuhSeq = lujLuhSeq;
	}

	@Column(name = "LUJ_SEQP", nullable = false, precision = 4, scale = 0)
	public Short getLujSeqp() {
		return this.lujSeqp;
	}

	public void setLujSeqp(final Short lujSeqp) {
		this.lujSeqp = lujSeqp;
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

	@Column(name = "LACUNA", length = 10)
	@Length(max = 10)
	public String getLacuna() {
		return this.lacuna;
	}

	public void setLacuna(final String lacuna) {
		this.lacuna = lacuna;
	}

	public enum Fields {

		LUJ_LUH_SEQ("lujLuhSeq"),
		LUJ_SEQP("lujSeqp"),
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
