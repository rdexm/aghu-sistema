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

import br.gov.mec.aghu.dominio.DominioIndRespProc;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.model.BaseJournal;

@Entity
@SequenceGenerator(name="mbcPpcJnSeq", sequenceName="AGH.MBC_PPC_JN_SEQ", allocationSize = 1)
@Table(name = "MBC_PROC_ESP_POR_CIRURGIAS_JN", schema = "AGH")
@Immutable
public class MbcProcEspPorCirurgiaJn extends BaseJournal {

	private static final long serialVersionUID = -3738947248081271168L;
	/* ATUALIZADOR JOURNALS - PROPERTIES	private Long seqJn;
	private String jnUser;
	private Date jnDateTime;
	private String jnOperation;*/
	private Integer crgSeq;
	private Integer eprPciSeq;
	private Short eprEspSeq;
	private DominioIndRespProc indRespProc;
	private Integer serMatricula;
	private Short serVinCodigo;
	private DominioSituacao situacao;
	private Date criadoEm;
	private Byte qtd;
	private Boolean indPrincipal;
	private Integer phiSeq;
	private Integer cidSeq;

	public MbcProcEspPorCirurgiaJn() {
	}

	public MbcProcEspPorCirurgiaJn(Long seqJn, String jnUser, Date jnDateTime, String jnOperation, Integer crgSeq, Integer eprPciSeq,
			Short eprEspSeq, DominioIndRespProc indRespProc) {
	/* ATUALIZADOR JOURNALS - contrutor	this.seqJn = seqJn;
		this.jnUser = jnUser;
		this.jnDateTime = jnDateTime;
		this.jnOperation = jnOperation;*/
		this.crgSeq = crgSeq;
		this.eprPciSeq = eprPciSeq;
		this.eprEspSeq = eprEspSeq;
		this.indRespProc = indRespProc;
	}

	public MbcProcEspPorCirurgiaJn(Long seqJn, String jnUser, Date jnDateTime, String jnOperation, Integer crgSeq, Integer eprPciSeq,
			Short eprEspSeq, DominioIndRespProc indRespProc, Integer serMatricula, Short serVinCodigo, DominioSituacao situacao, Date criadoEm, Byte qtd,
			Boolean indPrincipal, Integer phiSeq, Integer cidSeq) {
	/* ATUALIZADOR JOURNALS - contrutor	this.seqJn = seqJn;
		this.jnUser = jnUser;
		this.jnDateTime = jnDateTime;
		this.jnOperation = jnOperation;*/
		this.crgSeq = crgSeq;
		this.eprPciSeq = eprPciSeq;
		this.eprEspSeq = eprEspSeq;
		this.indRespProc = indRespProc;
		this.serMatricula = serMatricula;
		this.serVinCodigo = serVinCodigo;
		this.situacao = situacao;
		this.criadoEm = criadoEm;
		this.qtd = qtd;
		this.indPrincipal = indPrincipal;
		this.phiSeq = phiSeq;
		this.cidSeq = cidSeq;
	}

	// ATUALIZADOR JOURNALS - ID
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mbcPpcJnSeq")
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

	@Column(name = "CRG_SEQ", nullable = false)
	public Integer getCrgSeq() {
		return this.crgSeq;
	}

	public void setCrgSeq(Integer crgSeq) {
		this.crgSeq = crgSeq;
	}

	@Column(name = "EPR_PCI_SEQ", nullable = false)
	public Integer getEprPciSeq() {
		return this.eprPciSeq;
	}

	public void setEprPciSeq(Integer eprPciSeq) {
		this.eprPciSeq = eprPciSeq;
	}

	@Column(name = "EPR_ESP_SEQ", nullable = false)
	public Short getEprEspSeq() {
		return this.eprEspSeq;
	}

	public void setEprEspSeq(Short eprEspSeq) {
		this.eprEspSeq = eprEspSeq;
	}

	@Column(name = "IND_RESP_PROC", nullable = false, length = 4)
	@Enumerated(EnumType.STRING)
	public DominioIndRespProc getIndRespProc() {
		return this.indRespProc;
	}

	public void setIndRespProc(DominioIndRespProc indRespProc) {
		this.indRespProc = indRespProc;
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

	@Column(name = "SITUACAO", length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getSituacao() {
		return this.situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", length = 29)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "QTD")
	public Byte getQtd() {
		return this.qtd;
	}

	public void setQtd(Byte qtd) {
		this.qtd = qtd;
	}

	@Column(name = "IND_PRINCIPAL", length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndPrincipal() {
		return this.indPrincipal;
	}

	public void setIndPrincipal(Boolean indPrincipal) {
		this.indPrincipal = indPrincipal;
	}

	@Column(name = "PHI_SEQ")
	public Integer getPhiSeq() {
		return this.phiSeq;
	}

	public void setPhiSeq(Integer phiSeq) {
		this.phiSeq = phiSeq;
	}

	@Column(name = "CID_SEQ")
	public Integer getCidSeq() {
		return this.cidSeq;
	}

	public void setCidSeq(Integer cidSeq) {
		this.cidSeq = cidSeq;
	}

	public enum Fields {

	/* ATUALIZADOR JOURNALS - Fields	SEQ_JN("seqJn"),
		JN_USER("jnUser"),
		JN_DATE_TIME("jnDateTime"),
		JN_OPERATION("jnOperation"),*/
		CRG_SEQ("crgSeq"),
		EPR_PCI_SEQ("eprPciSeq"),
		EPR_ESP_SEQ("eprEspSeq"),
		IND_RESP_PROC("indRespProc"),
		SER_MATRICULA("serMatricula"),
		SER_VIN_CODIGO("serVinCodigo"),
		SITUACAO("situacao"),
		CRIADO_EM("criadoEm"),
		QTD("qtd"),
		IND_PRINCIPAL("indPrincipal"),
		PHI_SEQ("phiSeq"),
		CID_SEQ("cidSeq");

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
