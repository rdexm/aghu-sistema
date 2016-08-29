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
@Table(name = "MBC_SOLIC_CIRG_POS_ESCALAS_JN", schema = "AGH")
@Immutable
public class MbcSolicCirgPosEscalaJn extends BaseJournal {

/**
	 * 
	 */
	private static final long serialVersionUID = 6473876964458253345L;
	/* ATUALIZADOR JOURNALS - PROPERTIES	private Long seqJn;
	private String jnUser;
	private Date jnDateTime;
	private String jnOperation;*/
	private Integer seq;
	private Date criadoEm;
	private Integer serMatricula;
	private Short serVinCodigo;
	private Date data;
	private Short unfSeq;
	private Integer pacCodigo;
	private Short espSeq;
	private Short cspCnvCodigo;
	private Short cspSeq;
	private Integer eprPciSeq;
	private Short eprEspSeq;
	private Integer pucSerMatricula;
	private Short pucSerVinCodigo;
	private Short pucUnfSeq;
	private String pucIndFuncaoProf;
	private Date horaPrevInicio;
	private Short tempoPrevHoras;
	private Short tempoPrevMinutos;
	private String medicoSolicitante;
	private String indSolicAtendida;
	private String solicitacoesEspeciais;
	private Date dthrSolicAtendida;
	private Integer serMatriculaAtende;
	private Short serVinCodigoAtende;
	private Date dthrInclusaoLista;
	private String indSolicExcluida;
	private Date dthrSolicExcluida;
	private Integer serMatriculaExclui;
	private Short serVinCodigoExclui;
	private String justifExclusao;
	private Integer speSeq;

	public MbcSolicCirgPosEscalaJn() {
	}

	public MbcSolicCirgPosEscalaJn(Long seqJn, String jnUser, Date jnDateTime, String jnOperation, Integer seq) {
	/* ATUALIZADOR JOURNALS - contrutor	this.seqJn = seqJn;
		this.jnUser = jnUser;
		this.jnDateTime = jnDateTime;
		this.jnOperation = jnOperation;*/
		this.seq = seq;
	}

	@SuppressWarnings({"PMD.ExcessiveParameterList"})
	public MbcSolicCirgPosEscalaJn(Long seqJn, String jnUser, Date jnDateTime, String jnOperation, Integer seq, Date criadoEm,
			Integer serMatricula, Short serVinCodigo, Date data, Short unfSeq, Integer pacCodigo, Short espSeq, Short cspCnvCodigo,
			Short cspSeq, Integer eprPciSeq, Short eprEspSeq, Integer pucSerMatricula, Short pucSerVinCodigo, Short pucUnfSeq,
			String pucIndFuncaoProf, Date horaPrevInicio, Short tempoPrevHoras, Short tempoPrevMinutos, String medicoSolicitante,
			String indSolicAtendida, String solicitacoesEspeciais, Date dthrSolicAtendida, Integer serMatriculaAtende,
			Short serVinCodigoAtende, Date dthrInclusaoLista, String indSolicExcluida, Date dthrSolicExcluida,
			Integer serMatriculaExclui, Short serVinCodigoExclui, String justifExclusao, Integer speSeq) {
	/* ATUALIZADOR JOURNALS - contrutor	this.seqJn = seqJn;
		this.jnUser = jnUser;
		this.jnDateTime = jnDateTime;
		this.jnOperation = jnOperation;*/
		this.seq = seq;
		this.criadoEm = criadoEm;
		this.serMatricula = serMatricula;
		this.serVinCodigo = serVinCodigo;
		this.data = data;
		this.unfSeq = unfSeq;
		this.pacCodigo = pacCodigo;
		this.espSeq = espSeq;
		this.cspCnvCodigo = cspCnvCodigo;
		this.cspSeq = cspSeq;
		this.eprPciSeq = eprPciSeq;
		this.eprEspSeq = eprEspSeq;
		this.pucSerMatricula = pucSerMatricula;
		this.pucSerVinCodigo = pucSerVinCodigo;
		this.pucUnfSeq = pucUnfSeq;
		this.pucIndFuncaoProf = pucIndFuncaoProf;
		this.horaPrevInicio = horaPrevInicio;
		this.tempoPrevHoras = tempoPrevHoras;
		this.tempoPrevMinutos = tempoPrevMinutos;
		this.medicoSolicitante = medicoSolicitante;
		this.indSolicAtendida = indSolicAtendida;
		this.solicitacoesEspeciais = solicitacoesEspeciais;
		this.dthrSolicAtendida = dthrSolicAtendida;
		this.serMatriculaAtende = serMatriculaAtende;
		this.serVinCodigoAtende = serVinCodigoAtende;
		this.dthrInclusaoLista = dthrInclusaoLista;
		this.indSolicExcluida = indSolicExcluida;
		this.dthrSolicExcluida = dthrSolicExcluida;
		this.serMatriculaExclui = serMatriculaExclui;
		this.serVinCodigoExclui = serVinCodigoExclui;
		this.justifExclusao = justifExclusao;
		this.speSeq = speSeq;
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

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", length = 29)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
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

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATA", length = 29)
	public Date getData() {
		return this.data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	@Column(name = "UNF_SEQ")
	public Short getUnfSeq() {
		return this.unfSeq;
	}

	public void setUnfSeq(Short unfSeq) {
		this.unfSeq = unfSeq;
	}

	@Column(name = "PAC_CODIGO")
	public Integer getPacCodigo() {
		return this.pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

	@Column(name = "ESP_SEQ")
	public Short getEspSeq() {
		return this.espSeq;
	}

	public void setEspSeq(Short espSeq) {
		this.espSeq = espSeq;
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

	@Column(name = "EPR_PCI_SEQ")
	public Integer getEprPciSeq() {
		return this.eprPciSeq;
	}

	public void setEprPciSeq(Integer eprPciSeq) {
		this.eprPciSeq = eprPciSeq;
	}

	@Column(name = "EPR_ESP_SEQ")
	public Short getEprEspSeq() {
		return this.eprEspSeq;
	}

	public void setEprEspSeq(Short eprEspSeq) {
		this.eprEspSeq = eprEspSeq;
	}

	@Column(name = "PUC_SER_MATRICULA")
	public Integer getPucSerMatricula() {
		return this.pucSerMatricula;
	}

	public void setPucSerMatricula(Integer pucSerMatricula) {
		this.pucSerMatricula = pucSerMatricula;
	}

	@Column(name = "PUC_SER_VIN_CODIGO")
	public Short getPucSerVinCodigo() {
		return this.pucSerVinCodigo;
	}

	public void setPucSerVinCodigo(Short pucSerVinCodigo) {
		this.pucSerVinCodigo = pucSerVinCodigo;
	}

	@Column(name = "PUC_UNF_SEQ")
	public Short getPucUnfSeq() {
		return this.pucUnfSeq;
	}

	public void setPucUnfSeq(Short pucUnfSeq) {
		this.pucUnfSeq = pucUnfSeq;
	}

	@Column(name = "PUC_IND_FUNCAO_PROF", length = 3)
	@Length(max = 3)
	public String getPucIndFuncaoProf() {
		return this.pucIndFuncaoProf;
	}

	public void setPucIndFuncaoProf(String pucIndFuncaoProf) {
		this.pucIndFuncaoProf = pucIndFuncaoProf;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "HORA_PREV_INICIO", length = 29)
	public Date getHoraPrevInicio() {
		return this.horaPrevInicio;
	}

	public void setHoraPrevInicio(Date horaPrevInicio) {
		this.horaPrevInicio = horaPrevInicio;
	}

	@Column(name = "TEMPO_PREV_HORAS")
	public Short getTempoPrevHoras() {
		return this.tempoPrevHoras;
	}

	public void setTempoPrevHoras(Short tempoPrevHoras) {
		this.tempoPrevHoras = tempoPrevHoras;
	}

	@Column(name = "TEMPO_PREV_MINUTOS")
	public Short getTempoPrevMinutos() {
		return this.tempoPrevMinutos;
	}

	public void setTempoPrevMinutos(Short tempoPrevMinutos) {
		this.tempoPrevMinutos = tempoPrevMinutos;
	}

	@Column(name = "MEDICO_SOLICITANTE", length = 120)
	@Length(max = 120)
	public String getMedicoSolicitante() {
		return this.medicoSolicitante;
	}

	public void setMedicoSolicitante(String medicoSolicitante) {
		this.medicoSolicitante = medicoSolicitante;
	}

	@Column(name = "IND_SOLIC_ATENDIDA", length = 1)
	@Length(max = 1)
	public String getIndSolicAtendida() {
		return this.indSolicAtendida;
	}

	public void setIndSolicAtendida(String indSolicAtendida) {
		this.indSolicAtendida = indSolicAtendida;
	}

	@Column(name = "SOLICITACOES_ESPECIAIS", length = 500)
	@Length(max = 500)
	public String getSolicitacoesEspeciais() {
		return this.solicitacoesEspeciais;
	}

	public void setSolicitacoesEspeciais(String solicitacoesEspeciais) {
		this.solicitacoesEspeciais = solicitacoesEspeciais;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_SOLIC_ATENDIDA", length = 29)
	public Date getDthrSolicAtendida() {
		return this.dthrSolicAtendida;
	}

	public void setDthrSolicAtendida(Date dthrSolicAtendida) {
		this.dthrSolicAtendida = dthrSolicAtendida;
	}

	@Column(name = "SER_MATRICULA_ATENDE")
	public Integer getSerMatriculaAtende() {
		return this.serMatriculaAtende;
	}

	public void setSerMatriculaAtende(Integer serMatriculaAtende) {
		this.serMatriculaAtende = serMatriculaAtende;
	}

	@Column(name = "SER_VIN_CODIGO_ATENDE")
	public Short getSerVinCodigoAtende() {
		return this.serVinCodigoAtende;
	}

	public void setSerVinCodigoAtende(Short serVinCodigoAtende) {
		this.serVinCodigoAtende = serVinCodigoAtende;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_INCLUSAO_LISTA", length = 29)
	public Date getDthrInclusaoLista() {
		return this.dthrInclusaoLista;
	}

	public void setDthrInclusaoLista(Date dthrInclusaoLista) {
		this.dthrInclusaoLista = dthrInclusaoLista;
	}

	@Column(name = "IND_SOLIC_EXCLUIDA", length = 1)
	@Length(max = 1)
	public String getIndSolicExcluida() {
		return this.indSolicExcluida;
	}

	public void setIndSolicExcluida(String indSolicExcluida) {
		this.indSolicExcluida = indSolicExcluida;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_SOLIC_EXCLUIDA", length = 29)
	public Date getDthrSolicExcluida() {
		return this.dthrSolicExcluida;
	}

	public void setDthrSolicExcluida(Date dthrSolicExcluida) {
		this.dthrSolicExcluida = dthrSolicExcluida;
	}

	@Column(name = "SER_MATRICULA_EXCLUI")
	public Integer getSerMatriculaExclui() {
		return this.serMatriculaExclui;
	}

	public void setSerMatriculaExclui(Integer serMatriculaExclui) {
		this.serMatriculaExclui = serMatriculaExclui;
	}

	@Column(name = "SER_VIN_CODIGO_EXCLUI")
	public Short getSerVinCodigoExclui() {
		return this.serVinCodigoExclui;
	}

	public void setSerVinCodigoExclui(Short serVinCodigoExclui) {
		this.serVinCodigoExclui = serVinCodigoExclui;
	}

	@Column(name = "JUSTIF_EXCLUSAO", length = 500)
	@Length(max = 500)
	public String getJustifExclusao() {
		return this.justifExclusao;
	}

	public void setJustifExclusao(String justifExclusao) {
		this.justifExclusao = justifExclusao;
	}

	@Column(name = "SPE_SEQ")
	public Integer getSpeSeq() {
		return this.speSeq;
	}

	public void setSpeSeq(Integer speSeq) {
		this.speSeq = speSeq;
	}

	public enum Fields {

	/* ATUALIZADOR JOURNALS - Fields	SEQ_JN("seqJn"),
		JN_USER("jnUser"),
		JN_DATE_TIME("jnDateTime"),
		JN_OPERATION("jnOperation"),*/
		SEQ("seq"),
		CRIADO_EM("criadoEm"),
		SER_MATRICULA("serMatricula"),
		SER_VIN_CODIGO("serVinCodigo"),
		DATA("data"),
		UNF_SEQ("unfSeq"),
		PAC_CODIGO("pacCodigo"),
		ESP_SEQ("espSeq"),
		CSP_CNV_CODIGO("cspCnvCodigo"),
		CSP_SEQ("cspSeq"),
		EPR_PCI_SEQ("eprPciSeq"),
		EPR_ESP_SEQ("eprEspSeq"),
		PUC_SER_MATRICULA("pucSerMatricula"),
		PUC_SER_VIN_CODIGO("pucSerVinCodigo"),
		PUC_UNF_SEQ("pucUnfSeq"),
		PUC_IND_FUNCAO_PROF("pucIndFuncaoProf"),
		HORA_PREV_INICIO("horaPrevInicio"),
		TEMPO_PREV_HORAS("tempoPrevHoras"),
		TEMPO_PREV_MINUTOS("tempoPrevMinutos"),
		MEDICO_SOLICITANTE("medicoSolicitante"),
		IND_SOLIC_ATENDIDA("indSolicAtendida"),
		SOLICITACOES_ESPECIAIS("solicitacoesEspeciais"),
		DTHR_SOLIC_ATENDIDA("dthrSolicAtendida"),
		SER_MATRICULA_ATENDE("serMatriculaAtende"),
		SER_VIN_CODIGO_ATENDE("serVinCodigoAtende"),
		DTHR_INCLUSAO_LISTA("dthrInclusaoLista"),
		IND_SOLIC_EXCLUIDA("indSolicExcluida"),
		DTHR_SOLIC_EXCLUIDA("dthrSolicExcluida"),
		SER_MATRICULA_EXCLUI("serMatriculaExclui"),
		SER_VIN_CODIGO_EXCLUI("serVinCodigoExclui"),
		JUSTIF_EXCLUSAO("justifExclusao"),
		SPE_SEQ("speSeq");

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
