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
import br.gov.mec.aghu.dominio.DominioTipoAgenda;
import br.gov.mec.aghu.core.model.BaseJournal;

@Entity
@Table(name = "MPT_TIPO_SESSAO_JN", schema = "AGH")
@SequenceGenerator(name="mptTpsJnSeq", sequenceName="AGH.MPT_TPS_JN_SEQ", allocationSize = 1)
@Immutable
public class MptTipoSessaoJn extends BaseJournal {

	private static final long serialVersionUID = 952418480238372037L;
	
	private Short seq;
	private String descricao;
	private RapServidores servidor;
	private AghUnidadesFuncionais unidadeFuncional;
	private DominioTipoAgenda tipoAgenda;
	private Date tempoFixo;
	private Integer tempoDisponivel;
	private Boolean indApac;
	private Boolean indConsentimento;
	private Boolean indFrequencia;
	private DominioSituacao indSituacao;

	private MptTipoSessao tpsSeq;
	
	public MptTipoSessaoJn() {
		
	}

	public MptTipoSessaoJn(Long seqJn, String jnUser, Date jnDateTime, String jnOperation, Short seq) {
		this.seq = seq;
	}

	public MptTipoSessaoJn(Short seq, String descricao, RapServidores servidor, AghUnidadesFuncionais unidadeFuncional,
			DominioTipoAgenda tipoAgenda, Date tempoFixo, Integer tempoDisponivel, Boolean indApac, Boolean indConsentimento,
			Boolean indFrequencia, DominioSituacao indSituacao) {
		this.seq = seq;
		this.descricao = descricao;
		this.servidor = servidor;
		this.unidadeFuncional = unidadeFuncional;
		this.tipoAgenda = tipoAgenda;
		this.tempoFixo = tempoFixo;
		this.tempoDisponivel = tempoDisponivel;
		this.indApac = indApac;
		this.indConsentimento = indConsentimento;
		this.indFrequencia = indFrequencia;
		this.indSituacao = indSituacao;
	}

	@Id
	@Column(name = "SEQ_JN", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mptTpsJnSeq")
	@Override
	public Integer getSeqJn() {
		return super.getSeqJn();
	}

	@Column(name = "SEQ", nullable = false, precision = 4, scale = 0)
	public Short getSeq() {
		return this.seq;
	}

	public void setSeq(Short seq) {
		this.seq = seq;
	}

	@Column(name = "DESCRICAO", nullable = false, length = 60)
	@Length(max = 60)
	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getServidor() {
		return this.servidor;
	}

	public void setServidor(
			RapServidores servidor) {
		this.servidor = servidor;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "UNF_SEQ", referencedColumnName = "SEQ", nullable = false)
	public AghUnidadesFuncionais getUnidadeFuncional() {
		return this.unidadeFuncional;
	}

	public void setUnidadeFuncional(
			AghUnidadesFuncionais unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}

	@Column(name = "TIPO_AGENDA", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioTipoAgenda getTipoAgenda() {
		return tipoAgenda;
	}

	public void setTipoAgenda(DominioTipoAgenda tipoAgenda) {
		this.tipoAgenda = tipoAgenda;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "TEMPO_FIXO", length = 7)
	public Date getTempoFixo() {
		return tempoFixo;
	}

	public void setTempoFixo(Date tempoFixo) {
		this.tempoFixo = tempoFixo;
	}

	@Column(name = "TEMPO_DISPONIVEL", length = 3)
	public Integer getTempoDisponivel() {
		return tempoDisponivel;
	}

	public void setTempoDisponivel(Integer tempoDisponivel) {
		this.tempoDisponivel = tempoDisponivel;
	}

	@Column(name = "IND_APAC", length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndApac() {
		return indApac;
	}

	public void setIndApac(Boolean indApac) {
		this.indApac = indApac;
	}

	@Column(name = "IND_CONSENTIMENTO", length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndConsentimento() {
		return indConsentimento;
	}

	public void setIndConsentimento(Boolean indConsentimento) {
		this.indConsentimento = indConsentimento;
	}

	@Column(name = "IND_FREQUENCIA", length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndFrequencia() {
		return indFrequencia;
	}

	public void setIndFrequencia(Boolean indFrequencia) {
		this.indFrequencia = indFrequencia;
	}
	
	@Column(name = "IND_SITUACAO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	public enum Fields {

		SEQ("seq"),
		UNF_SEQ("unidadeFuncional.seq"),
		UNID_FUNCIONAL("unidadeFuncional"),
		DESCRICAO("descricao"),
		SERVIDOR_MATRICULA("servidor.id.matricula"),
		SERVIDOR_VIN_CODIGO("servidor.id.vinCodigo"),
		TIPO_AGENDA("tipoAgenda"),
		TEMPO_FIXO("tempoFixo"),
		TEMPO_DISPONIVEL("tempoDisponivel"),
		IND_APAC("indApac"),
		IND_CONSENTIMENTO("indConsentimento"),
		IND_FREQUENCIA("indFrequencia"),
		IND_SITUACAO("indSituacao"),
		RNI_SEQP("rniSeqp"),
		SEQ_JN("seqJn"),
		DATA_ALTERACAO("dataAlteracao"),
		JN_USER("nomeUsuario"),
		TPS_SEQ("tpsSeq");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SEQ", updatable = false, insertable = false)
	public MptTipoSessao getTpsSeq() {
		return tpsSeq;
}

	public void setTpsSeq(MptTipoSessao tpsSeq) {
		this.tpsSeq = tpsSeq;
	}
}
