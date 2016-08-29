package br.gov.mec.aghu.model;

// Generated 09/04/2012 16:32:53 by Hibernate Tools 3.4.0.CR1

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


import org.hibernate.annotations.Immutable;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoMensagem;
import br.gov.mec.aghu.core.model.BaseJournal;

@Entity
@SequenceGenerator(name="aelEeeJnSeq", sequenceName="AGH.AEL_EEE_JN_SEQ", allocationSize = 1)
@Table(name = "AEL_EXIGENCIA_EXAMES_JN", schema = "AGH")
@Immutable
public class AelExigenciaExameJn extends BaseJournal {

/**
	 * 
	 */
	private static final long serialVersionUID = -6308073857223754402L;
	/* ATUALIZADOR JOURNALS - PROPERTIES	private Long seqJn;
	private String jnUser;
	private Date jnDateTime;
	private String jnOperation;*/
	private Integer seq;
	private Boolean indPedeInternacao;
	private Boolean indPedeAihAssinada;
	private DominioSituacao indSituacao;
	private Date criadoEm;
	private DominioTipoMensagem tipoMensagem;
	private String ufeEmaExaSigla;
	private Integer ufeEmaManSeq;
	private Short ufeUnfSeq;
	private Integer serMatricula;
	private Short serVinCodigo;
	private Short unfSeq;

	public AelExigenciaExameJn() {
	}

	public AelExigenciaExameJn(Long seqJn, String jnUser, Date jnDateTime, String jnOperation, Integer seq, Boolean indPedeInternacao,
			Boolean indPedeAihAssinada, DominioSituacao indSituacao, Date criadoEm, DominioTipoMensagem tipoMensagem, String ufeEmaExaSigla,
			Integer ufeEmaManSeq, Short ufeUnfSeq, Integer serMatricula, Short serVinCodigo, Short unfSeq) {
	/* ATUALIZADOR JOURNALS - contrutor	this.seqJn = seqJn;
		this.jnUser = jnUser;
		this.jnDateTime = jnDateTime;
		this.jnOperation = jnOperation;*/
		this.seq = seq;
		this.indPedeInternacao = indPedeInternacao;
		this.indPedeAihAssinada = indPedeAihAssinada;
		this.indSituacao = indSituacao;
		this.criadoEm = criadoEm;
		this.tipoMensagem = tipoMensagem;
		this.ufeEmaExaSigla = ufeEmaExaSigla;
		this.ufeEmaManSeq = ufeEmaManSeq;
		this.ufeUnfSeq = ufeUnfSeq;
		this.serMatricula = serMatricula;
		this.serVinCodigo = serVinCodigo;
		this.unfSeq = unfSeq;
	}

	// ATUALIZADOR JOURNALS - ID
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "aelEeeJnSeq")
	@Column(name = "SEQ_JN", unique = true, nullable = false)
	//@GeneratedValue(strategy = GenerationType.AUTO, generator = "")
	@Override
	public Integer getSeqJn() {
		return super.getSeqJn();
	}
	// ATUALIZADOR JOURNALS - ID
	
/* ATUALIZADOR JOURNALS - Get / Set	@Id
	@Column(name = "SEQ_JN", unique = true, nullable = false)
	public Long getSeqJn() {
		return this.seqJn;
	}

	public void setSeqJn(Long seqJn) {
		this.seqJn = seqJn;
	}

	@Column(name = "JN_USER", nullable = false, length = 30)
	@Length(max = 30)
	public String getJnUser() {
		return this.jnUser;
	}

	public void setJnUser(String jnUser) {
		this.jnUser = jnUser;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "JN_DATE_TIME", nullable = false, length = 29)
	public Date getJnDateTime() {
		return this.jnDateTime;
	}

	public void setJnDateTime(Date jnDateTime) {
		this.jnDateTime = jnDateTime;
	}

	@Column(name = "JN_OPERATION", nullable = false, length = 3)
	@Length(max = 3)
	public String getJnOperation() {
		return this.jnOperation;
	}

	public void setJnOperation(String jnOperation) {
		this.jnOperation = jnOperation;
	}*/

	@Column(name = "SEQ", nullable = false)
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@Column(name = "IND_PEDE_INTERNACAO", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndPedeInternacao() {
		return this.indPedeInternacao;
	}

	public void setIndPedeInternacao(Boolean indPedeInternacao) {
		this.indPedeInternacao = indPedeInternacao;
	}

	@Column(name = "IND_PEDE_AIH_ASSINADA", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndPedeAihAssinada() {
		return this.indPedeAihAssinada;
	}

	public void setIndPedeAihAssinada(Boolean indPedeAihAssinada) {
		this.indPedeAihAssinada = indPedeAihAssinada;
	}

	@Column(name = "IND_SITUACAO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getIndSituacao() {
		return this.indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 29)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "TIPO_MENSAGEM", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioTipoMensagem getTipoMensagem() {
		return this.tipoMensagem;
	}

	public void setTipoMensagem(DominioTipoMensagem tipoMensagem) {
		this.tipoMensagem = tipoMensagem;
	}

	@Column(name = "UFE_EMA_EXA_SIGLA", nullable = false, length = 5)
	@Length(max = 5)
	public String getUfeEmaExaSigla() {
		return this.ufeEmaExaSigla;
	}

	public void setUfeEmaExaSigla(String ufeEmaExaSigla) {
		this.ufeEmaExaSigla = ufeEmaExaSigla;
	}

	@Column(name = "UFE_EMA_MAN_SEQ", nullable = false)
	public Integer getUfeEmaManSeq() {
		return this.ufeEmaManSeq;
	}

	public void setUfeEmaManSeq(Integer ufeEmaManSeq) {
		this.ufeEmaManSeq = ufeEmaManSeq;
	}

	@Column(name = "UFE_UNF_SEQ", nullable = false)
	public Short getUfeUnfSeq() {
		return this.ufeUnfSeq;
	}

	public void setUfeUnfSeq(Short ufeUnfSeq) {
		this.ufeUnfSeq = ufeUnfSeq;
	}

	@Column(name = "SER_MATRICULA", nullable = false)
	public Integer getSerMatricula() {
		return this.serMatricula;
	}

	public void setSerMatricula(Integer serMatricula) {
		this.serMatricula = serMatricula;
	}

	@Column(name = "SER_VIN_CODIGO", nullable = false)
	public Short getSerVinCodigo() {
		return this.serVinCodigo;
	}

	public void setSerVinCodigo(Short serVinCodigo) {
		this.serVinCodigo = serVinCodigo;
	}

	@Column(name = "UNF_SEQ", nullable = false)
	public Short getUnfSeq() {
		return this.unfSeq;
	}

	public void setUnfSeq(Short unfSeq) {
		this.unfSeq = unfSeq;
	}

	public enum Fields {

	/* ATUALIZADOR JOURNALS - Fields	SEQ_JN("seqJn"),
		JN_USER("jnUser"),
		JN_DATE_TIME("jnDateTime"),
		JN_OPERATION("jnOperation"),*/
		SEQ("seq"),
		IND_PEDE_INTERNACAO("indPedeInternacao"),
		IND_PEDE_AIH_ASSINADA("indPedeAihAssinada"),
		IND_SITUACAO("indSituacao"),
		CRIADO_EM("criadoEm"),
		TIPO_MENSAGEM("tipoMensagem"),
		UFE_EMA_EXA_SIGLA("ufeEmaExaSigla"),
		UFE_EMA_MAN_SEQ("ufeEmaManSeq"),
		UFE_UNF_SEQ("ufeUnfSeq"),
		SER_MATRICULA("serMatricula"),
		SER_VIN_CODIGO("serVinCodigo"),
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
