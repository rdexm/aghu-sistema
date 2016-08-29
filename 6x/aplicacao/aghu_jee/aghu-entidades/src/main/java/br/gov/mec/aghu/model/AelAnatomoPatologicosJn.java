package br.gov.mec.aghu.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
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

import br.gov.mec.aghu.core.model.BaseJournal;

@Entity
@SequenceGenerator(name = "aelLumJnSeq", sequenceName = "AGH.AEL_LUM_JN_SEQ", allocationSize = 1)
@Table(name = "AEL_ANATOMO_PATOLOGICOS_JN")
@Immutable
public class AelAnatomoPatologicosJn extends BaseJournal {

	private static final long serialVersionUID = -3134046864133214464L;

	private Long seq;
	private Long numeroAp;
	private Date criadoEm;
	private AelAtendimentoDiversos atendimentoDiversos;
	private AghAtendimentos atendimento;
	private RapServidores servidor;

	public AelAnatomoPatologicosJn() {
	}

	public AelAnatomoPatologicosJn(final Long seq) {
		super();
		this.seq = seq;
	}

	public AelAnatomoPatologicosJn(final Long seq, final Long numeroAp, final Date criadoEm, final AghAtendimentos atendimento,
			final AelAtendimentoDiversos atendimentoDiversos, final RapServidores servidor) {
		super();
		this.seq = seq;
		this.numeroAp = numeroAp;
		this.criadoEm = criadoEm;
		this.atendimentoDiversos = atendimentoDiversos;
		this.atendimento = atendimento;
		this.servidor = servidor;
	}

	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "aelLumJnSeq")
	@Column(name = "SEQ_JN", unique = true, nullable = false)
	public Integer getSeqJn() {
		return super.getSeqJn();
	}

	@Column(name = "SEQ", nullable = false, precision = 14, scale = 0)
	public Long getSeq() {
		return this.seq;
	}

	public void setSeq(final Long seq) {
		this.seq = seq;
	}

	@Column(name = "NUMERO_AP", precision = 10, scale = 0)
	public Long getNumeroAp() {
		return this.numeroAp;
	}

	public void setNumeroAp(final Long numeroAp) {
		this.numeroAp = numeroAp;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(final Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ATV_SEQ")
	public AelAtendimentoDiversos getAtendimentoDiversos() {
		return atendimentoDiversos;
	}

	public void setAtendimentoDiversos(final AelAtendimentoDiversos atendimentoDiversos) {
		this.atendimentoDiversos = atendimentoDiversos;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ATD_SEQ")
	public AghAtendimentos getAtendimento() {
		return atendimento;
	}

	public void setAtendimento(final AghAtendimentos atendimento) {
		this.atendimento = atendimento;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getServidor() {
		return this.servidor;
	}

	public void setServidor(final RapServidores servidor) {
		this.servidor = servidor;
	}

	public enum Fields {

		SEQ("seq"),
		NUMERO_AP("numeroAp"),
		CRIADO_EM("criadoEm"),
		ATENDIMENTO_DIVERSOS("atendimentoDiversos"),
		ATENDIMENTO("atendimento"),
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
