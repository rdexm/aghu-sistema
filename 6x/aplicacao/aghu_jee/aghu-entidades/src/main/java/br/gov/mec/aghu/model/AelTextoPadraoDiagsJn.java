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
@SequenceGenerator(name = "aelLuJnSeq", sequenceName = "AGH.AEL_LUJ_JN_SEQ", allocationSize = 1)
@Table(name = "AEL_TEXTO_PADRAO_DIAGS_JN")
@Immutable
public class AelTextoPadraoDiagsJn extends BaseJournal {

	private static final long serialVersionUID = -8709881769764509152L;

	private Short luhSeq;
	private Short seqp;
	private String descricao;
	private String apelido;
	private DominioSituacao indSituacao;
	private Date criadoEm;
	private RapServidores servidor;

	public AelTextoPadraoDiagsJn() {
	}

	public AelTextoPadraoDiagsJn(
			final Short luhSeq, final Short seqp, final String descricao, final DominioSituacao indSituacao, final Date criadoEm,
			final RapServidores servidor, final String apelido) {
		super();
		this.luhSeq = luhSeq;
		this.seqp = seqp;
		this.descricao = descricao;
		this.indSituacao = indSituacao;
		this.criadoEm = criadoEm;
		this.servidor = servidor;
		this.apelido = apelido;
	}

	public AelTextoPadraoDiagsJn(
			final Short luhSeq, final Short seqp, final String descricao, final DominioSituacao indSituacao, final Date criadoEm,
			final RapServidores servidor) {
		super();
		this.luhSeq = luhSeq;
		this.seqp = seqp;
		this.descricao = descricao;
		this.indSituacao = indSituacao;
		this.criadoEm = criadoEm;
		this.servidor = servidor;
	}

	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "aelLuJnSeq")
	@Column(name = "SEQ_JN", unique = true, nullable = false)
	public Integer getSeqJn() {
		return super.getSeqJn();
	}

	@Column(name = "LUH_SEQ", nullable = false, precision = 4, scale = 0)
	public Short getLuhSeq() {
		return this.luhSeq;
	}

	public void setLuhSeq(final Short luhSeq) {
		this.luhSeq = luhSeq;
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

	@Column(name = "DESCRICAO", nullable = false, length = 2000)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(final String descricao) {
		this.descricao = descricao;
	}

	@Column(name = "APELIDO", length = 50)
	@Length(max = 50)
	public String getApelido() {
		return this.apelido;
	}

	public void setApelido(final String apelido) {
		this.apelido = apelido;
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
	public enum Fields {

		LUH_SEQ("luhSeq"),
		SEQP("seqp"),
		DESCRICAO("descricao"),
		APELIDO("apelido"),
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
