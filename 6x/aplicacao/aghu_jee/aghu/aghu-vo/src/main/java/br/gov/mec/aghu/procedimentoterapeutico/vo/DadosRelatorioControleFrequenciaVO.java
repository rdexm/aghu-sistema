package br.gov.mec.aghu.procedimentoterapeutico.vo;

import java.io.Serializable;


public class DadosRelatorioControleFrequenciaVO implements Serializable {

	private static final long serialVersionUID = 508476651630784318L;

	private String nomePaciente;
	private String prontuarioPaciente;
	private String dataDeclaracao;
	private String numeroAPAC;
	private String cpfFormatado;
	private String dataInicioTratamento;
	private String dataFimTratamento;
	private String procedimentoPrincipal;
	private String procedimentoSec1;
	private String procedimentoSec2;
	private String localData;
	private String mesReferencia;
	private String codTabela;
	private String mesDeclaro;
		
	
	public String getMesDeclaro() {
		return mesDeclaro;
	}
	public void setMesDeclaro(String mesDeclaro) {
		this.mesDeclaro = mesDeclaro;
	}
	public String getCodTabela() {
		return codTabela;
	}
	public void setCodTabela(String codTabela) {
		this.codTabela = codTabela;
	}
	public String getNomePaciente() {
		return nomePaciente;
	}
	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}
	public String getProntuarioPaciente() {
		return prontuarioPaciente;
	}
	public void setProntuarioPaciente(String prontuarioPaciente) {
		this.prontuarioPaciente = prontuarioPaciente;
	}
	public String getDataDeclaracao() {
		return dataDeclaracao;
	}
	public void setDataDeclaracao(String dataDeclaracao) {
		this.dataDeclaracao = dataDeclaracao;
	}
	public String getNumeroAPAC() {
		return numeroAPAC;
	}
	public void setNumeroAPAC(String numeroAPAC) {
		this.numeroAPAC = numeroAPAC;
	}
	public String getCpfFormatado() {
		return cpfFormatado;
	}
	public void setCpfFormatado(String cpfFormatado) {
		this.cpfFormatado = cpfFormatado;
	}
	public String getDataInicioTratamento() {
		return dataInicioTratamento;
	}
	public void setDataInicioTratamento(String dataInicioTratamento) {
		this.dataInicioTratamento = dataInicioTratamento;
	}
	public String getDataFimTratamento() {
		return dataFimTratamento;
	}
	public void setDataFimTratamento(String dataFimTratamento) {
		this.dataFimTratamento = dataFimTratamento;
	}
	public String getProcedimentoPrincipal() {
		return procedimentoPrincipal;
	}
	public void setProcedimentoPrincipal(String procedimentoPrincipal) {
		this.procedimentoPrincipal = procedimentoPrincipal;
	}
	public String getProcedimentoSec1() {
		return procedimentoSec1;
	}
	public void setProcedimentoSec1(String procedimentoSec1) {
		this.procedimentoSec1 = procedimentoSec1;
	}
	public String getProcedimentoSec2() {
		return procedimentoSec2;
	}
	public void setProcedimentoSec2(String procedimentoSec2) {
		this.procedimentoSec2 = procedimentoSec2;
	}
	public String getLocalData() {
		return localData;
	}
	public void setLocalData(String localData) {
		this.localData = localData;
	}
	public String getMesReferencia() {
		return mesReferencia;
	}
	public void setMesReferencia(String mesReferencia) {
		this.mesReferencia = mesReferencia;
	}
	
	
}
