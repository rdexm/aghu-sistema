package br.gov.mec.aghu.faturamento.vo;

import java.util.Date;

public class ContasPreenchimentoLaudosVO {

	private Integer codPaciente;

	private Integer prontuario;

	private String paciente;

	private Integer conta;

	private Integer phiRealizado;

	private Long ssmRealizado;

	private Long ssmSolicitado;

	private String usuario;

	private Date dtAlta;

	private String uti;

	private String acomp;

	private String mProced;

	private String pMaior;

	private String excluCrit;

	private String des;

	private Short unfSeq;

	private Short nrQuarto;

	private String leitoId;

	public Integer getCodPaciente() {
		return codPaciente;
	}

	public void setCodPaciente(Integer codPaciente) {
		this.codPaciente = codPaciente;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public String getPaciente() {
		return paciente;
	}

	public void setPaciente(String paciente) {
		this.paciente = paciente;
	}

	public Integer getConta() {
		return conta;
	}

	public void setConta(Integer conta) {
		this.conta = conta;
	}

	public Integer getPhiRealizado() {
		return phiRealizado;
	}

	public void setPhiRealizado(Integer phiRealizado) {
		this.phiRealizado = phiRealizado;
	}

	public Long getSsmRealizado() {
		return ssmRealizado;
	}

	public void setSsmRealizado(Long ssmRealizado) {
		this.ssmRealizado = ssmRealizado;
	}

	public Long getSsmSolicitado() {
		return ssmSolicitado;
	}

	public void setSsmSolicitado(Long ssmSolicitado) {
		this.ssmSolicitado = ssmSolicitado;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public Date getDtAlta() {
		return dtAlta;
	}

	public void setDtAlta(Date dtAlta) {
		this.dtAlta = dtAlta;
	}

	public String getUti() {
		return uti;
	}

	public void setUti(String uti) {
		this.uti = uti;
	}

	public String getAcomp() {
		return acomp;
	}

	public void setAcomp(String acomp) {
		this.acomp = acomp;
	}

	public String getmProced() {
		return mProced;
	}

	public void setmProced(String mProced) {
		this.mProced = mProced;
	}

	public String getpMaior() {
		return pMaior;
	}

	public void setpMaior(String pMaior) {
		this.pMaior = pMaior;
	}

	public String getExcluCrit() {
		return excluCrit;
	}

	public void setExcluCrit(String excluCrit) {
		this.excluCrit = excluCrit;
	}

	public String getDes() {
		return des;
	}

	public void setDes(String des) {
		this.des = des;
	}

	public Short getUnfSeq() {
		return unfSeq;
	}

	public void setUnfSeq(Short unfSeq) {
		this.unfSeq = unfSeq;
	}

	public Short getNrQuarto() {
		return nrQuarto;
	}

	public void setNrQuarto(Short nrQuarto) {
		this.nrQuarto = nrQuarto;
	}

	public String getLeitoId() {
		return leitoId;
	}

	public void setLeitoId(String leitoId) {
		this.leitoId = leitoId;
	}

}