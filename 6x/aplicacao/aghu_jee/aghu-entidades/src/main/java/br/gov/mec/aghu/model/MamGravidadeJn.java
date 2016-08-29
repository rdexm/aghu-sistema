package br.gov.mec.aghu.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Immutable;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.model.BaseJournal;

@Entity
@SequenceGenerator(name = "mamGrvJnSeq", sequenceName = "AGH.MAM_GRV_JN_SEQ", allocationSize = 1)
@Table(name = "MAM_GRAVIDADES_JN", schema = "AGH")
@Immutable
public class MamGravidadeJn extends BaseJournal {
	private static final long serialVersionUID = 5061201580526718586L;

	private Short seq;
	private String descricao;
	private Short ordem;
	private DominioSituacao indSituacao;
	private Date criadoEm;
	private String atributoVisual;
	private Integer serMatricula;
	private Short serVinCodigo;
	private String campoTela;
	private String icone;
	private Boolean indPermiteSaida;
	private String cor;
	private String codCor;
	private Date tempoEspera;
	private Integer pcrSeq;
	private Boolean indUsoTriagem;

	public MamGravidadeJn() {
	}

	public MamGravidadeJn(Long seqJn, String jnUser, Date jnDateTime, String jnOperation, Short seq) {
	/* ATUALIZADOR JOURNALS - contrutor	this.seqJn = seqJn;
		this.jnUser = jnUser;
		this.jnDateTime = jnDateTime;
		this.jnOperation = jnOperation;*/
		this.seq = seq;
	}

	public MamGravidadeJn(Long seqJn, String jnUser, Date jnDateTime, String jnOperation, Short seq, String descricao, Short ordem,
			DominioSituacao indSituacao, Date criadoEm, String atributoVisual, Integer serMatricula, Short serVinCodigo, String campoTela,
			String icone, Boolean indPermiteSaida, Boolean indUsoTriagem) {
	/* ATUALIZADOR JOURNALS - contrutor	this.seqJn = seqJn;
		this.jnUser = jnUser;
		this.jnDateTime = jnDateTime;
		this.jnOperation = jnOperation;*/
		this.seq = seq;
		this.descricao = descricao;
		this.ordem = ordem;
		this.indSituacao = indSituacao;
		this.criadoEm = criadoEm;
		this.atributoVisual = atributoVisual;
		this.serMatricula = serMatricula;
		this.serVinCodigo = serVinCodigo;
		this.campoTela = campoTela;
		this.icone = icone;
		this.indPermiteSaida = indPermiteSaida;
		this.indUsoTriagem = indUsoTriagem;
	}

	// ATUALIZADOR JOURNALS - ID
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mamGrvJnSeq")
	@Column(name = "SEQ_JN", unique = true, nullable = false)
	@NotNull
	@Override
	public Integer getSeqJn() {
		return super.getSeqJn();
	}

	@Column(name = "SEQ", unique = true, nullable = false)
	public Short getSeq() {
		return this.seq;
	}

	public void setSeq(Short seq) {
		this.seq = seq;
	}

	@Column(name = "DESCRICAO", nullable = false, length = 60)
	@NotNull
	@Length(max = 60)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Column(name = "ORDEM", nullable = false)
	public Short getOrdem() {
		return this.ordem;
	}

	public void setOrdem(Short ordem) {
		this.ordem = ordem;
	}

	@Column(name = "IND_SITUACAO", nullable = false, length = 1)
	@NotNull
	@Enumerated(EnumType.STRING)
	public DominioSituacao getIndSituacao() {
		return this.indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 29)
	@NotNull
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "ATRIBUTO_VISUAL", length = 120)
	@Length(max = 120)
	public String getAtributoVisual() {
		return this.atributoVisual;
	}

	public void setAtributoVisual(String atributoVisual) {
		this.atributoVisual = atributoVisual;
	}

	@Column(name = "SER_MATRICULA", nullable = false)
	@NotNull
	public Integer getSerMatricula() {
		return this.serMatricula;
	}

	public void setSerMatricula(Integer serMatricula) {
		this.serMatricula = serMatricula;
	}

	@Column(name = "SER_VIN_CODIGO", nullable = false)
	@NotNull
	public Short getSerVinCodigo() {
		return this.serVinCodigo;
	}

	public void setSerVinCodigo(Short serVinCodigo) {
		this.serVinCodigo = serVinCodigo;
	}

	@Column(name = "CAMPO_TELA", length = 120)
	@Length(max = 120)
	public String getCampoTela() {
		return this.campoTela;
	}

	public void setCampoTela(String campoTela) {
		this.campoTela = campoTela;
	}

	@Column(name = "ICONE", length = 120)
	@Length(max = 120)
	public String getIcone() {
		return this.icone;
	}

	public void setIcone(String icone) {
		this.icone = icone;
	}

	@Column(name = "IND_PERMITE_SAIDA", nullable = false, length = 1)
	@NotNull
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndPermiteSaida() {
		return this.indPermiteSaida;
	}

	public void setIndPermiteSaida(Boolean indPermiteSaida) {
		this.indPermiteSaida = indPermiteSaida;
	}

	@Column(name = "IND_USO_TRIAGEM", length = 1)
	@NotNull
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndUsoTriagem() {
		return this.indUsoTriagem;
	}

	public void setIndUsoTriagem(Boolean indUsoTriagem) {
		this.indUsoTriagem = indUsoTriagem;
	}
	
	@Column(name = "COR", length = 10)
	@Length(max = 10)
	public String getCor() {
		return this.cor;
	}

	public void setCor(String cor) {
		this.cor = cor;
	}

	@Column(name = "COD_COR", length = 15)
	@Length(max = 15)
	public String getCodCor() {
		return this.codCor;
	}

	public void setCodCor(String codCor) {
		this.codCor = codCor;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "TEMPO_ESPERA", length = 29)
	public Date getTempoEspera() {
		return this.tempoEspera;
	}

	public void setTempoEspera(Date tempoEspera) {
		this.tempoEspera = tempoEspera;
	}
	
	@Column(name = "PCR_SEQ")
	public Integer getPcrSeq() {
		return pcrSeq;
	}

	public void setPcrSeq(Integer pcrSeq) {
		this.pcrSeq = pcrSeq;
	}

	public enum Fields {
		SEQ("seq"),
		SER_VIN_CODIGO("serVinCodigo"),
		SER_MATRICULA("serMatricula"),
		DESCRICAO("descricao"),
		ORDEM("ordem"),
		IND_SITUACAO("indSituacao"),
		CRIADO_EM("criadoEm"),
		ATRIBUTO_VISUAL("atributoVisual"),
		COR("cor"),
		COD_COR("codCor"),
		CAMPO_TELA("campoTela"),
		ICONE("icone"),
		IND_PERMITE_SAIDA("indPermiteSaida"),
		IND_USO_TRIAGEM("indUsoTriagem"),
		TEMPO_ESPERA("tempoEspera"),
		PCR_SEQ("pcrSeq");

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
