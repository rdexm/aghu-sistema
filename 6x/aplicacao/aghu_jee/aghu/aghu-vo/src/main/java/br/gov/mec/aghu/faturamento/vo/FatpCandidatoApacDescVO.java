package br.gov.mec.aghu.faturamento.vo;

import java.util.Date;

public class FatpCandidatoApacDescVO {

	private String 		msgLog;
	private Date 		dtReferencia;
	private Date 		dtAnterior;
	private Character 	aparelho;
	private Integer 	Idade ;
	private Integer 	contador;
	private Integer 	pacAnterior;
	private Date 		maxData;
	private Boolean 	incluiu;
	private String		candidato;
	private Date		dtIniRealizado;
	private Date 		cpeDtFim;
	private Date		dtCirg;
	private Character 	teveApacAparelho;
	private Date 		dtFimApacAparelho;
	private	Long 		quantidade;
	private	Character 	indRep;
	private Integer 	qtdeRep;
	private Character 	cirurgia;
	private String 		retorno;
	private Character 	existeCaract;
	private Integer 	qtdeApac;
	private Integer 	qtdeApacAno;
	
	public FatpCandidatoApacDescVO(){
		this.dtFimApacAparelho = null;
		this.teveApacAparelho = 'N';
		this.dtCirg = null;
		this.quantidade = (long) 0;
		this.indRep = 'N';
		this.setCirurgia('N');
		this.retorno = null;
	}

	public String getMsgLog() {
		return msgLog;
	}
	public void setMsgLog(String msgLog) {
		this.msgLog = msgLog;
	}
	public Date getDtReferencia() {
		return dtReferencia;
	}
	public void setDtReferencia(Date dtReferencia) {
		this.dtReferencia = dtReferencia;
	}
	public Date getDtAnterior() {
		return dtAnterior;
	}
	public void setDtAnterior(Date dtAnterior) {
		this.dtAnterior = dtAnterior;
	}

	public Character getAparelho() {
		return aparelho;
	}
	public void setAparelho(Character aparelho) {
		this.aparelho = aparelho;
	}
	public Integer getIdade() {
		return Idade;
	}
	public void setIdade(Integer idade) {
		Idade = idade;
	}

	public Integer getContador() {
		return contador;
	}
	public void setContador(Integer contador) {
		this.contador = contador;
	}
	public Integer getPacAnterior() {
		return pacAnterior;
	}
	public void setPacAnterior(Integer pacAnterior) {
		this.pacAnterior = pacAnterior;
	}
	public Date getMaxData() {
		return maxData;
	}
	public void setMaxData(Date maxData) {
		this.maxData = maxData;
	}
	public Boolean getIncluiu() {
		return incluiu;
	}
	public void setIncluiu(Boolean incluiu) {
		this.incluiu = incluiu;
	}
	public String getCandidato() {
		return candidato;
	}
	public void setCandidato(String candidato) {
		this.candidato = candidato;
	}

	public Date getDtIniRealizado() {
		return dtIniRealizado;
	}
	public void setDtIniRealizado(Date dtIniRealizado) {
		this.dtIniRealizado = dtIniRealizado;
	}

	public Date getCpeDtFim() {
		return cpeDtFim;
	}
	public void setCpeDtFim(Date cpeDtFim) {
		this.cpeDtFim = cpeDtFim;
	}
	public Date getDtCirg() {
		return dtCirg;
	}
	public void setDtCirg(Date dtCirg) {
		this.dtCirg = dtCirg;
	}
	public Character getTeveApacAparelho() {
		return teveApacAparelho;
	}
	public void setTeveApacAparelho(Character teveApacAparelho) {
		this.teveApacAparelho = teveApacAparelho;
	}
	public Date getDtFimApacAparelho() {
		return dtFimApacAparelho;
	}
	public void setDtFimApacAparelho(Date dtFimApacAparelho) {
		this.dtFimApacAparelho = dtFimApacAparelho;
	}
	public Long getQuantidade() {
		return quantidade;
	}
	public void setQuantidade(Long quant) {
		this.quantidade = quant;
	}
	public Character getIndRep() {
		return indRep;
	}
	public void setIndRep(Character indRep) {
		this.indRep = indRep;
	}
	public Integer getQtdeRep() {
		return qtdeRep;
	}
	public void setQtdeRep(Integer qtdeRep) {
		this.qtdeRep = qtdeRep;
	}

	public Character getCirurgia() {
		return cirurgia;
	}

	public void setCirurgia(Character cirurgia) {
		this.cirurgia = cirurgia;
	}

	public String getRetorno() {
		return retorno;
	}

	public void setRetorno(String retorno) {
		this.retorno = retorno;
	}

	public Character getExisteCaract() {
		return existeCaract;
	}

	public void setExisteCaract(Character existeCaract) {
		this.existeCaract = existeCaract;
	}

	public Integer getQtdeApac() {
		return qtdeApac;
	}

	public void setQtdeApac(Integer qtdeApac) {
		this.qtdeApac = qtdeApac;
	}

	public Integer getQtdeApacAno() {
		return qtdeApacAno;
	}

	public void setQtdeApacAno(Integer qtdeApacAno) {
		this.qtdeApacAno = qtdeApacAno;
	}
	
}
