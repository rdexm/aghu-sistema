package br.gov.mec.aghu.model;

// Generated 09/04/2012 16:32:53 by Hibernate Tools 3.4.0.CR1

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


import org.hibernate.annotations.Immutable;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.model.BaseJournal;

@Entity
@Table(name = "SCO_TRIBUTOS_GRP_MAT_SERVIC_JN", schema = "AGH")
@Immutable
public class ScoTributoGrpMatServicJn extends BaseJournal {

/**
	 * 
	 */
	private static final long serialVersionUID = 1022348390394970314L;
	/* ATUALIZADOR JOURNALS - PROPERTIES	private Long seqJn;
	private String jnUser;
	private Date jnDateTime;
	private String jnOperation;*/
	private Integer seq;
	private Integer friCodigo;
	private Short gmtCodigo;
	private Integer srvCodigo;
	private String classificacao;
	private Date dtFinalValidade;
	private Date criadoEm;
	private Date alteradoEm;
	private Integer serMatricula;
	private Short serVinCodigo;
	private String indUsoMaterial;

	public ScoTributoGrpMatServicJn() {
	}

	public ScoTributoGrpMatServicJn(Long seqJn, String jnUser, Date jnDateTime, String jnOperation, Integer seq) {
	/* ATUALIZADOR JOURNALS - contrutor	this.seqJn = seqJn;
		this.jnUser = jnUser;
		this.jnDateTime = jnDateTime;
		this.jnOperation = jnOperation;*/
		this.seq = seq;
	}

	public ScoTributoGrpMatServicJn(Long seqJn, String jnUser, Date jnDateTime, String jnOperation, Integer seq, Integer friCodigo,
			Short gmtCodigo, Integer srvCodigo, String classificacao, Date dtFinalValidade, Date criadoEm, Date alteradoEm,
			Integer serMatricula, Short serVinCodigo, String indUsoMaterial) {
	/* ATUALIZADOR JOURNALS - contrutor	this.seqJn = seqJn;
		this.jnUser = jnUser;
		this.jnDateTime = jnDateTime;
		this.jnOperation = jnOperation;*/
		this.seq = seq;
		this.friCodigo = friCodigo;
		this.gmtCodigo = gmtCodigo;
		this.srvCodigo = srvCodigo;
		this.classificacao = classificacao;
		this.dtFinalValidade = dtFinalValidade;
		this.criadoEm = criadoEm;
		this.alteradoEm = alteradoEm;
		this.serMatricula = serMatricula;
		this.serVinCodigo = serVinCodigo;
		this.indUsoMaterial = indUsoMaterial;
	}

	// ATUALIZADOR JOURNALS - ID
	@Id
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

	@Column(name = "FRI_CODIGO")
	public Integer getFriCodigo() {
		return this.friCodigo;
	}

	public void setFriCodigo(Integer friCodigo) {
		this.friCodigo = friCodigo;
	}

	@Column(name = "GMT_CODIGO")
	public Short getGmtCodigo() {
		return this.gmtCodigo;
	}

	public void setGmtCodigo(Short gmtCodigo) {
		this.gmtCodigo = gmtCodigo;
	}

	@Column(name = "SRV_CODIGO")
	public Integer getSrvCodigo() {
		return this.srvCodigo;
	}

	public void setSrvCodigo(Integer srvCodigo) {
		this.srvCodigo = srvCodigo;
	}

	@Column(name = "CLASSIFICACAO", length = 1)
	@Length(max = 1)
	public String getClassificacao() {
		return this.classificacao;
	}

	public void setClassificacao(String classificacao) {
		this.classificacao = classificacao;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DT_FINAL_VALIDADE", length = 29)
	public Date getDtFinalValidade() {
		return this.dtFinalValidade;
	}

	public void setDtFinalValidade(Date dtFinalValidade) {
		this.dtFinalValidade = dtFinalValidade;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", length = 29)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ALTERADO_EM", length = 29)
	public Date getAlteradoEm() {
		return this.alteradoEm;
	}

	public void setAlteradoEm(Date alteradoEm) {
		this.alteradoEm = alteradoEm;
	}

	@Column(name = "SER_MATRICULA")
	public Integer getSerMatricula() {
		return this.serMatricula;
	}

	public void setSerMatricula(Integer serMatricula) {
		this.serMatricula = serMatricula;
	}

	@Column(name = "SER_VIN_CODIGO")
	public Short getSerVinCodigo() {
		return this.serVinCodigo;
	}

	public void setSerVinCodigo(Short serVinCodigo) {
		this.serVinCodigo = serVinCodigo;
	}

	@Column(name = "IND_USO_MATERIAL", length = 1)
	@Length(max = 1)
	public String getIndUsoMaterial() {
		return this.indUsoMaterial;
	}

	public void setIndUsoMaterial(String indUsoMaterial) {
		this.indUsoMaterial = indUsoMaterial;
	}

	public enum Fields {

	/* ATUALIZADOR JOURNALS - Fields	SEQ_JN("seqJn"),
		JN_USER("jnUser"),
		JN_DATE_TIME("jnDateTime"),
		JN_OPERATION("jnOperation"),*/
		SEQ("seq"),
		FRI_CODIGO("friCodigo"),
		GMT_CODIGO("gmtCodigo"),
		SRV_CODIGO("srvCodigo"),
		CLASSIFICACAO("classificacao"),
		DT_FINAL_VALIDADE("dtFinalValidade"),
		CRIADO_EM("criadoEm"),
		ALTERADO_EM("alteradoEm"),
		SER_MATRICULA("serMatricula"),
		SER_VIN_CODIGO("serVinCodigo"),
		IND_USO_MATERIAL("indUsoMaterial");

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
