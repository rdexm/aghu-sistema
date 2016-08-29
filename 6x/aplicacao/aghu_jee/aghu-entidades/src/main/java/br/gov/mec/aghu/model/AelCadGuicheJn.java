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

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.model.BaseJournal;

@SuppressWarnings({"PMD.AghuUsoIndevidoDaEnumDominioSimNaoEntity"})
@Entity
// @ORADB AGH.AELT_CGUJ_BRI
@SequenceGenerator(name = "aelCguJnSeq", sequenceName = "AGH.AEL_CGU_JN_SEQ", allocationSize = 1)
@Table(name = "AEL_CAD_GUICHES_JN", schema = "AGH")
@Immutable
public class AelCadGuicheJn extends BaseJournal {

	private static final long serialVersionUID = 1427564533201256549L;
	
	private Short seq;
	private String descricao;
	private DominioSimNao ocupado;
	private DominioSituacao indSituacao;
	private Date criadoEm;
	private RapServidores servidor;
	private Short unfSeq;

	public AelCadGuicheJn() {
	}

	public AelCadGuicheJn(final Short seq, final String descricao, final DominioSimNao ocupado, final DominioSituacao indSituacao,
			final Date criadoEm, final RapServidores servidor) {
		super();
		this.seq = seq;
		this.descricao = descricao;
		this.ocupado = ocupado;
		this.indSituacao = indSituacao;
		this.criadoEm = criadoEm;
		this.servidor = servidor;
	}

	public AelCadGuicheJn(final Short seq, final String descricao, final DominioSimNao ocupado, final DominioSituacao indSituacao,
			final Date criadoEm, final RapServidores servidor, final Short unfSeq) {
		super();
		this.seq = seq;
		this.descricao = descricao;
		this.ocupado = ocupado;
		this.indSituacao = indSituacao;
		this.criadoEm = criadoEm;
		this.servidor = servidor;
		this.unfSeq = unfSeq;
	}

	@Column(name = "SEQ", unique = true, nullable = false, precision = 4, scale = 0)
	public Short getSeq() {
		return this.seq;
	}

	public void setSeq(final Short seq) {
		this.seq = seq;
	}

	@Column(name = "DESCRICAO", nullable = false, length = 60)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(final String descricao) {
		this.descricao = descricao;
	}

	@Column(name = "OCUPADO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSimNao getOcupado() {
		return this.ocupado;
	}

	public void setOcupado(final DominioSimNao ocupado) {
		this.ocupado = ocupado;
	}

	@Column(name = "IND_SITUACAO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getIndSituacao() {
		return this.indSituacao;
	}

	public void setIndSituacao(final DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "CRIADO_EM", nullable = false, length = 7)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(final Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Override
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "aelCguJnSeq")
	@Column(name = "SEQ_JN", unique = true, nullable = false)
	public Integer getSeqJn() {
		return super.getSeqJn();
	}

	public void setUnfSeq(final Short unfSeq) {
		this.unfSeq = unfSeq;
	}

	@Column(name = "UNF_SEQ")
	public Short getUnfSeq() {
		return unfSeq;
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

	public enum Fields {

		SEQ("seq"),
		DESCRICAO("descricao"),
		OCUPADO("ocupado"),
		IND_SITUACAO("indSituacao"),
		CRIADO_EM("criadoEm"),
		SERVIDOR("servidor"),
		UNF_SEQ("unfSeq");

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
