package br.gov.mec.aghu.compras.vo;

import java.util.Date;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.ScoGrupoServico;
import br.gov.mec.aghu.model.ScoPontoParadaSolicitacao;
import br.gov.mec.aghu.model.ScoServico;

public class LoteSolicitacaoServicoVO {

	ScoPontoParadaSolicitacao pontoParada;	
	Integer numeroSolicitacaoServico;
	Date dataSolicitacaoServico;
	ScoGrupoServico grupoServico;
	ScoServico servico;
	FccCentroCustos centroCustoSolicitante;
	FccCentroCustos centroCustoAplicacao;
	DominioSimNao solicitacaoPrioritaria;
	DominioSimNao solicitacaoUrgente;
	Boolean devolvidasENaoDevolvidas;
	boolean exibirScSolicitante;
	Date dataInicioAutorizacaoSolicitacaoServico;
	Date dataFimAutorizacaoSolicitacaoServico;
	DominioSimNao solicitacaoAutorizada;
	
	public ScoPontoParadaSolicitacao getPontoParada() {
		return pontoParada;
	}
	public void setPontoParada(ScoPontoParadaSolicitacao pontoParada) {
		this.pontoParada = pontoParada;
	}
	public Integer getNumeroSolicitacaoServico() {
		return numeroSolicitacaoServico;
	}
	public void setNumeroSolicitacaoServico(Integer numeroSolicitacaoServico) {
		this.numeroSolicitacaoServico = numeroSolicitacaoServico;
	}
	public Date getDataSolicitacaoServico() {
		return dataSolicitacaoServico;
	}
	public void setDataSolicitacaoServico(Date dataSolicitacaoServico) {
		this.dataSolicitacaoServico = dataSolicitacaoServico;
	}
	public ScoGrupoServico getGrupoServico() {
		return grupoServico;
	}
	public void setGrupoServico(ScoGrupoServico grupoServico) {
		this.grupoServico = grupoServico;
	}
	public ScoServico getServico() {
		return servico;
	}
	public void setServico(ScoServico servico) {
		this.servico = servico;
	}
	public FccCentroCustos getCentroCustoSolicitante() {
		return centroCustoSolicitante;
	}
	public void setCentroCustoSolicitante(FccCentroCustos centroCustoSolicitante) {
		this.centroCustoSolicitante = centroCustoSolicitante;
	}
	public FccCentroCustos getCentroCustoAplicacao() {
		return centroCustoAplicacao;
	}
	public void setCentroCustoAplicacao(FccCentroCustos centroCustoAplicacao) {
		this.centroCustoAplicacao = centroCustoAplicacao;
	}
	public DominioSimNao getSolicitacaoPrioritaria() {
		return solicitacaoPrioritaria;
	}
	public void setSolicitacaoPrioritaria(DominioSimNao solicitacaoPrioritaria) {
		this.solicitacaoPrioritaria = solicitacaoPrioritaria;
	}
	public DominioSimNao getSolicitacaoUrgente() {
		return solicitacaoUrgente;
	}
	public void setSolicitacaoUrgente(DominioSimNao solicitacaoUrgente) {
		this.solicitacaoUrgente = solicitacaoUrgente;
	}
	public Boolean getDevolvidasENaoDevolvidas() {
		return devolvidasENaoDevolvidas;
	}
	public void setDevolvidasENaoDevolvidas(Boolean devolvidasENaoDevolvidas) {
		this.devolvidasENaoDevolvidas = devolvidasENaoDevolvidas;
	}
	
	public boolean isExibirScSolicitante() {
		return exibirScSolicitante;
	}
	public void setExibirScSolicitante(boolean exibirScSolicitante) {
		this.exibirScSolicitante = exibirScSolicitante;
	}
	public Date getDataInicioAutorizacaoSolicitacaoServico() {
		return dataInicioAutorizacaoSolicitacaoServico;
	}
	public void setDataInicioAutorizacaoSolicitacaoServico(
			Date dataInicioAutorizacaoSolicitacaoServico) {
		this.dataInicioAutorizacaoSolicitacaoServico= dataInicioAutorizacaoSolicitacaoServico;
	}
	public Date getDataFimAutorizacaoSolicitacaoServico() {
		return dataFimAutorizacaoSolicitacaoServico;
	}
	public void setDataFimAutorizacaoSolicitacaoServico(
			Date dataFimAutorizacaoSolicitacaoServico) {
		this.dataFimAutorizacaoSolicitacaoServico= dataFimAutorizacaoSolicitacaoServico;
	}
	public DominioSimNao getSolicitacaoAutorizada() {
		return solicitacaoAutorizada;
	}
	public void setSolicitacaoAutorizada(DominioSimNao solicitacaoAutorizada) {
		this.solicitacaoAutorizada = solicitacaoAutorizada;
	}


}
