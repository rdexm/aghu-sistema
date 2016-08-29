package br.gov.mec.aghu.blococirurgico.portalplanejamento.vo;

import java.io.Serializable;
import java.util.Date;

public class EscalaPortalPlanejamentoCirurgiasVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1426434886279913890L;

	private String nomePaciente;
	private String prontuario;
	private String regime;
	private String regimeCodigo;
	private String procedimento;
	private Date tempoSala;
	private String comentario;
	private String equipe;
	private String titulo;
	private String nomePacienteCompleto;

	private Byte intervaloEscala;
	private Date prevInicio;
	private Date prevFim;
	
	private Integer agdSeq;
	private Short espSeq;
	private Short unfSeq;
	private Integer pucSerMatricula;
	private Short pucSerVinCodigo;
	private Short pucUnfSeq;
	private String pucFuncProf;
	private Date dtAgenda;
	private Short sciUnfSeq;
	private Short sciSeqp;
	private Integer pciSeq;
	private Boolean indGeradoSistema;
	
	private Boolean overbooking;
	private Boolean escala;
	private Boolean editavel;
	private Boolean planejado;
	private Date dtHrInicioOverbooking;
	private Date dtHrFimOverbooking;
	private Short cspCnvCodigo;
	private Byte cspSeq;
	private Integer pacCodigo;
	private Boolean indPrecaucaoEspecial;
	private Short qtdeProc;
	private String textoToolTip;
	
	
	public String getNomePaciente() {
		return nomePaciente;
	}
	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}
	public String getProntuario() {
		return prontuario;
	}
	public void setProntuario(String prontuario) {
		this.prontuario = prontuario;
	}
	public String getRegime() {
		return regime;
	}
	public void setRegime(String regime) {
		this.regime = regime;
	}
	public String getProcedimento() {
		return procedimento;
	}
	public void setProcedimento(String procedimento) {
		this.procedimento = procedimento;
	}
	public Date getTempoSala() {
		return tempoSala;
	}
	public void setTempoSala(Date tempoSala) {
		this.tempoSala = tempoSala;
	}
	public String getComentario() {
		return comentario;
	}
	public void setComentario(String comentario) {
		this.comentario = comentario;
	}
	public Date getPrevInicio() {
		return prevInicio;
	}
	public void setPrevInicio(Date prevInicio) {
		this.prevInicio = prevInicio;
	}
	public Date getPrevFim() {
		return prevFim;
	}
	public void setPrevFim(Date prevFim) {
		this.prevFim = prevFim;
	}
	public Integer getAgdSeq() {
		return agdSeq;
	}
	public void setAgdSeq(Integer agdSeq) {
		this.agdSeq = agdSeq;
	}
	public Short getEspSeq() {
		return espSeq;
	}
	public void setEspSeq(Short espSeq) {
		this.espSeq = espSeq;
	}
	public Short getUnfSeq() {
		return unfSeq;
	}
	public void setUnfSeq(Short unfSeq) {
		this.unfSeq = unfSeq;
	}
	public Integer getPucSerMatricula() {
		return pucSerMatricula;
	}
	public void setPucSerMatricula(Integer pucSerMatricula) {
		this.pucSerMatricula = pucSerMatricula;
	}
	public Short getPucSerVinCodigo() {
		return pucSerVinCodigo;
	}
	public void setPucSerVinCodigo(Short pucSerVinCodigo) {
		this.pucSerVinCodigo = pucSerVinCodigo;
	}
	public Short getPucUnfSeq() {
		return pucUnfSeq;
	}
	public void setPucUnfSeq(Short pucUnfSeq) {
		this.pucUnfSeq = pucUnfSeq;
	}
	public String getPucFuncProf() {
		return pucFuncProf;
	}
	public void setPucFuncProf(String pucFuncProf) {
		this.pucFuncProf = pucFuncProf;
	}
	public Date getDtAgenda() {
		return dtAgenda;
	}
	public void setDtAgenda(Date dtAgenda) {
		this.dtAgenda = dtAgenda;
	}
	public Short getSciUnfSeq() {
		return sciUnfSeq;
	}
	public void setSciUnfSeq(Short sciUnfSeq) {
		this.sciUnfSeq = sciUnfSeq;
	}
	public Short getSciSeqp() {
		return sciSeqp;
	}
	public void setSciSeqp(Short sciSeqp) {
		this.sciSeqp = sciSeqp;
	}
	public Integer getPciSeq() {
		return pciSeq;
	}
	public void setPciSeq(Integer pciSeq) {
		this.pciSeq = pciSeq;
	}
	public Boolean getOverbooking() {
		return overbooking;
	}
	public void setOverbooking(Boolean overbooking) {
		this.overbooking = overbooking;
	}
	public Boolean getEscala() {
		return escala;
	}
	public void setEscala(Boolean escala) {
		this.escala = escala;
	}
	public Boolean getEditavel() {
		return editavel;
	}
	public void setEditavel(Boolean editavel) {
		this.editavel = editavel;
	}
	public Byte getIntervaloEscala() {
		return intervaloEscala;
	}
	public void setIntervaloEscala(Byte intervaloEscala) {
		this.intervaloEscala = intervaloEscala;
	}
	public Date getDtHrInicioOverbooking() {
		return dtHrInicioOverbooking;
	}
	public void setDtHrInicioOverbooking(Date dtHrInicioOverbooking) {
		this.dtHrInicioOverbooking = dtHrInicioOverbooking;
	}
	public Date getDtHrFimOverbooking() {
		return dtHrFimOverbooking;
	}
	public void setDtHrFimOverbooking(Date dtHrFimOverbooking) {
		this.dtHrFimOverbooking = dtHrFimOverbooking;
	}
	public String getEquipe() {
		return equipe;
	}
	public void setEquipe(String equipe) {
		this.equipe = equipe;
	}
	public String getTitulo() {
		return titulo;
	}
	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}
	public void setPlanejado(Boolean planejado) {
		this.planejado = planejado;
	}
	public Boolean getPlanejado() {
		return planejado;
	}
	public void setIndGeradoSistema(Boolean indGeradoSistema) {
		this.indGeradoSistema = indGeradoSistema;
	}
	public Boolean getIndGeradoSistema() {
		return indGeradoSistema;
	}
	public Byte getCspSeq() {
		return cspSeq;
	}
	public void setCspSeq(Byte cspSeq) {
		this.cspSeq = cspSeq;
	}
	public Short getCspCnvCodigo() {
		return cspCnvCodigo;
	}
	public void setCspCnvCodigo(Short cspCnvCodigo) {
		this.cspCnvCodigo = cspCnvCodigo;
	}
	public Integer getPacCodigo() {
		return pacCodigo;
	}
	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}
	public Boolean getIndPrecaucaoEspecial() {
		return indPrecaucaoEspecial;
	}
	public void setIndPrecaucaoEspecial(Boolean indPrecaucaoEspecial) {
		this.indPrecaucaoEspecial = indPrecaucaoEspecial;
	}
	public Short getQtdeProc() {
		return qtdeProc;
	}
	public void setQtdeProc(Short qtdeProc) {
		this.qtdeProc = qtdeProc;
	}
	public void setTextoToolTip(String textoToolTip) {
		this.textoToolTip = textoToolTip;
	}
	public String getTextoToolTip() {
		return textoToolTip;
	}
	public String getRegimeCodigo() {
		return regimeCodigo;
	}
	public void setRegimeCodigo(String regimeCodigo) {
		this.regimeCodigo = regimeCodigo;
	}
	public String getNomePacienteCompleto() {
		return nomePacienteCompleto;
	}
	public void setNomePacienteCompleto(String nomePacienteCompleto) {
		this.nomePacienteCompleto = nomePacienteCompleto;
	}
}
