package br.gov.mec.aghu.compras.pac.vo;

import java.util.List;

import br.gov.mec.aghu.core.commons.BaseBean;

public class ScoLicitacaoCadastradaImpressaoVO implements BaseBean{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8935731238791618698L;
	
	
	private String dataGeracao;
	private String codigoCliente;
	private String codigoIdentificador;
	private String ied;
	private String licitacao;
	private String processo;
	private String identificadorLicitacaoBB;
	private String dtPubl;
	private String dtIniEntgProp;
	private String dtFimEntgProp;
	private String dataAbertProp;
	private String dataIniPregao;
	private String descricao;
	private String ocorrencia;
	
	private List<AutorizacaoLicitacaoEnviadaVO> listaAutorizacoesLicitacao;
	private List<LotesLicitacaoVO> listaLotesLicitacao;
	
	
	public ScoLicitacaoCadastradaImpressaoVO(){
		
	}
	
	public String getDataGeracao() {
		return dataGeracao;
	}

	public void setDataGeracao(String dataGeracao) {
		this.dataGeracao = dataGeracao;
	}

	public String getCodigoCliente() {
		return codigoCliente;
	}

	public void setCodigoCliente(String codigoCliente) {
		this.codigoCliente = codigoCliente;
	}

	public String getCodigoIdentificador() {
		return codigoIdentificador;
	}

	public void setCodigoIdentificador(String codigoIdentificador) {
		this.codigoIdentificador = codigoIdentificador;
	}

	public String getIed() {
		return ied;
	}

	public void setIed(String ied) {
		this.ied = ied;
	}

	public String getLicitacao() {
		return licitacao;
	}

	public void setLicitacao(String licitacao) {
		this.licitacao = licitacao;
	}

	public String getProcesso() {
		return processo;
	}

	public void setProcesso(String processo) {
		this.processo = processo;
	}	

	public String getDtPubl() {
		return dtPubl;
	}

	public void setDtPubl(String dtPubl) {
		this.dtPubl = dtPubl;
	}

	public String getDtIniEntgProp() {
		return dtIniEntgProp;
	}

	public void setDtIniEntgProp(String dtIniEntgProp) {
		this.dtIniEntgProp = dtIniEntgProp;
	}

	public String getDtFimEntgProp() {
		return dtFimEntgProp;
	}

	public void setDtFimEntgProp(String dtFimEntgProp) {
		this.dtFimEntgProp = dtFimEntgProp;
	}

	public String getDataAbertProp() {
		return dataAbertProp;
	}

	public void setDataAbertProp(String dataAbertProp) {
		this.dataAbertProp = dataAbertProp;
	}

	public String getDataIniPregao() {
		return dataIniPregao;
	}

	public void setDataIniPregao(String dataIniPregao) {
		this.dataIniPregao = dataIniPregao;
	}	

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getIdentificadorLicitacaoBB() {
		return identificadorLicitacaoBB;
	}

	public void setIdentificadorLicitacaoBB(String identificadorLicitacaoBB) {
		this.identificadorLicitacaoBB = identificadorLicitacaoBB;
	}

	public String getOcorrencia() {
		return ocorrencia;
	}

	public void setOcorrencia(String ocorrencia) {
		this.ocorrencia = ocorrencia;
	}

	public List<AutorizacaoLicitacaoEnviadaVO> getListaAutorizacoesLicitacao() {
		return listaAutorizacoesLicitacao;
	}

	public void setListaAutorizacoesLicitacao(
			List<AutorizacaoLicitacaoEnviadaVO> listaAutorizacoesLicitacao) {
		this.listaAutorizacoesLicitacao = listaAutorizacoesLicitacao;
	}

	public List<LotesLicitacaoVO> getListaLotesLicitacao() {
		return listaLotesLicitacao;
	}

	public void setListaLotesLicitacao(List<LotesLicitacaoVO> listaLotesLicitacao) {
		this.listaLotesLicitacao = listaLotesLicitacao;
	}	
	
}
