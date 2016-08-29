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
@Table(name = "AHD_HOSPITAIS_DIA_JN", schema = "AGH")
@Immutable
public class AhdHospitalDiaJn extends BaseJournal {

/**
	 * 
	 */
	private static final long serialVersionUID = 8717237392584871722L;
	/* ATUALIZADOR JOURNALS - PROPERTIES	private Long seqJn;
	private String jnUser;
	private Date jnDateTime;
	private String jnOperation;*/
	private Integer seq;
	private Integer pacCodigo;
	private Date dthrInicio;
	private Date dthrTermino;
	private Short espSeq;
	private Integer serMatricula;
	private Short serVinCodigo;
	private Short unfSeq;
	private String tamCodigo;
	private Integer serMatriculaProfessor;
	private Short serVinProfessor;
	private String indPacienteEmAtendimento;
	private Short iphPhoSeq;
	private Integer iphSeq;
	private Short cspCnvCodigo;
	private Short cspSeq;
	private String justificativaAltDel;
	private Short tciSeq;

	public AhdHospitalDiaJn() {
	}

	public AhdHospitalDiaJn(Long seqJn, String jnUser, Date jnDateTime, String jnOperation, Integer seq) {
	/* ATUALIZADOR JOURNALS - contrutor	this.seqJn = seqJn;
		this.jnUser = jnUser;
		this.jnDateTime = jnDateTime;
		this.jnOperation = jnOperation;*/
		this.seq = seq;
	}

	public AhdHospitalDiaJn(Long seqJn, String jnUser, Date jnDateTime, String jnOperation, Integer seq, Integer pacCodigo,
			Date dthrInicio, Date dthrTermino, Short espSeq, Integer serMatricula, Short serVinCodigo, Short unfSeq, String tamCodigo,
			Integer serMatriculaProfessor, Short serVinProfessor, String indPacienteEmAtendimento, Short iphPhoSeq, Integer iphSeq,
			Short cspCnvCodigo, Short cspSeq, String justificativaAltDel, Short tciSeq) {
	/* ATUALIZADOR JOURNALS - contrutor	this.seqJn = seqJn;
		this.jnUser = jnUser;
		this.jnDateTime = jnDateTime;
		this.jnOperation = jnOperation;*/
		this.seq = seq;
		this.pacCodigo = pacCodigo;
		this.dthrInicio = dthrInicio;
		this.dthrTermino = dthrTermino;
		this.espSeq = espSeq;
		this.serMatricula = serMatricula;
		this.serVinCodigo = serVinCodigo;
		this.unfSeq = unfSeq;
		this.tamCodigo = tamCodigo;
		this.serMatriculaProfessor = serMatriculaProfessor;
		this.serVinProfessor = serVinProfessor;
		this.indPacienteEmAtendimento = indPacienteEmAtendimento;
		this.iphPhoSeq = iphPhoSeq;
		this.iphSeq = iphSeq;
		this.cspCnvCodigo = cspCnvCodigo;
		this.cspSeq = cspSeq;
		this.justificativaAltDel = justificativaAltDel;
		this.tciSeq = tciSeq;
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

	@Column(name = "PAC_CODIGO")
	public Integer getPacCodigo() {
		return this.pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_INICIO", length = 29)
	public Date getDthrInicio() {
		return this.dthrInicio;
	}

	public void setDthrInicio(Date dthrInicio) {
		this.dthrInicio = dthrInicio;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_TERMINO", length = 29)
	public Date getDthrTermino() {
		return this.dthrTermino;
	}

	public void setDthrTermino(Date dthrTermino) {
		this.dthrTermino = dthrTermino;
	}

	@Column(name = "ESP_SEQ")
	public Short getEspSeq() {
		return this.espSeq;
	}

	public void setEspSeq(Short espSeq) {
		this.espSeq = espSeq;
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

	@Column(name = "UNF_SEQ")
	public Short getUnfSeq() {
		return this.unfSeq;
	}

	public void setUnfSeq(Short unfSeq) {
		this.unfSeq = unfSeq;
	}

	@Column(name = "TAM_CODIGO", length = 3)
	@Length(max = 3)
	public String getTamCodigo() {
		return this.tamCodigo;
	}

	public void setTamCodigo(String tamCodigo) {
		this.tamCodigo = tamCodigo;
	}

	@Column(name = "SER_MATRICULA_PROFESSOR")
	public Integer getSerMatriculaProfessor() {
		return this.serMatriculaProfessor;
	}

	public void setSerMatriculaProfessor(Integer serMatriculaProfessor) {
		this.serMatriculaProfessor = serMatriculaProfessor;
	}

	@Column(name = "SER_VIN_PROFESSOR")
	public Short getSerVinProfessor() {
		return this.serVinProfessor;
	}

	public void setSerVinProfessor(Short serVinProfessor) {
		this.serVinProfessor = serVinProfessor;
	}

	@Column(name = "IND_PACIENTE_EM_ATENDIMENTO", length = 1)
	@Length(max = 1)
	public String getIndPacienteEmAtendimento() {
		return this.indPacienteEmAtendimento;
	}

	public void setIndPacienteEmAtendimento(String indPacienteEmAtendimento) {
		this.indPacienteEmAtendimento = indPacienteEmAtendimento;
	}

	@Column(name = "IPH_PHO_SEQ")
	public Short getIphPhoSeq() {
		return this.iphPhoSeq;
	}

	public void setIphPhoSeq(Short iphPhoSeq) {
		this.iphPhoSeq = iphPhoSeq;
	}

	@Column(name = "IPH_SEQ")
	public Integer getIphSeq() {
		return this.iphSeq;
	}

	public void setIphSeq(Integer iphSeq) {
		this.iphSeq = iphSeq;
	}

	@Column(name = "CSP_CNV_CODIGO")
	public Short getCspCnvCodigo() {
		return this.cspCnvCodigo;
	}

	public void setCspCnvCodigo(Short cspCnvCodigo) {
		this.cspCnvCodigo = cspCnvCodigo;
	}

	@Column(name = "CSP_SEQ")
	public Short getCspSeq() {
		return this.cspSeq;
	}

	public void setCspSeq(Short cspSeq) {
		this.cspSeq = cspSeq;
	}

	@Column(name = "JUSTIFICATIVA_ALT_DEL", length = 240)
	@Length(max = 240)
	public String getJustificativaAltDel() {
		return this.justificativaAltDel;
	}

	public void setJustificativaAltDel(String justificativaAltDel) {
		this.justificativaAltDel = justificativaAltDel;
	}

	@Column(name = "TCI_SEQ")
	public Short getTciSeq() {
		return this.tciSeq;
	}

	public void setTciSeq(Short tciSeq) {
		this.tciSeq = tciSeq;
	}

	public enum Fields {

	/* ATUALIZADOR JOURNALS - Fields	SEQ_JN("seqJn"),
		JN_USER("jnUser"),
		JN_DATE_TIME("jnDateTime"),
		JN_OPERATION("jnOperation"),*/
		SEQ("seq"),
		PAC_CODIGO("pacCodigo"),
		DTHR_INICIO("dthrInicio"),
		DTHR_TERMINO("dthrTermino"),
		ESP_SEQ("espSeq"),
		SER_MATRICULA("serMatricula"),
		SER_VIN_CODIGO("serVinCodigo"),
		UNF_SEQ("unfSeq"),
		TAM_CODIGO("tamCodigo"),
		SER_MATRICULA_PROFESSOR("serMatriculaProfessor"),
		SER_VIN_PROFESSOR("serVinProfessor"),
		IND_PACIENTE_EM_ATENDIMENTO("indPacienteEmAtendimento"),
		IPH_PHO_SEQ("iphPhoSeq"),
		IPH_SEQ("iphSeq"),
		CSP_CNV_CODIGO("cspCnvCodigo"),
		CSP_SEQ("cspSeq"),
		JUSTIFICATIVA_ALT_DEL("justificativaAltDel"),
		TCI_SEQ("tciSeq");

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
