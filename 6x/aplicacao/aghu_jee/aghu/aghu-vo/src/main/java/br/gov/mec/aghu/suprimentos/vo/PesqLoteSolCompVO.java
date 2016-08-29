package br.gov.mec.aghu.suprimentos.vo;

import java.util.Date;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.FsoVerbaGestao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoGrupoMaterial;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoPontoParadaSolicitacao;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;

public class PesqLoteSolCompVO {

	ScoPontoParadaSolicitacao pontoParada;
	Integer numeroSolicitacaoCompra;
	Date dataSolicitacaoCompra, dataInicioSolicitacaoCompra, dataFimSolicitacaoCompra;
	ScoGrupoMaterial grupoMaterial;
	ScoMaterial material;
	FccCentroCustos centroCustoSolicitante;
	FccCentroCustos centroCustoAplicacao;
	ScoSolicitacaoDeCompra solicitacaoCompra;
	DominioSimNao solicitacaoPrioritaria;
	DominioSimNao solicitacaoUrgente;
	DominioSimNao solicitacaoAutorizada;
	DominioSimNao solicitacaoExcluida;
	DominioSimNao solicitacaoDevolvida;
	DominioSimNao repAutomatica;
	DominioSimNao matEstocavel;
	RapServidores filtroComprador;
	DominioSimNao indContrato;
	FsoVerbaGestao verbaGestao;
	Date dataInicioAnaliseSolicitacaoCompra;
	Date dataFimAnaliseSolicitacaoCompra;
	Date dataInicioAutorizacaoSolicitacaoCompra;
	Date dataFimAutorizacaoSolicitacaoCompra;
	Date dataUltimaExecucao;
	private boolean exibirScSolicitante;
	Boolean filtroVazio;
	
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
	public ScoPontoParadaSolicitacao getPontoParada() {
		return pontoParada;
	}
	public void setPontoParada(ScoPontoParadaSolicitacao pontoParada) {
		this.pontoParada = pontoParada;
	}
	public Integer getNumeroSolicitacaoCompra() {
		return numeroSolicitacaoCompra;
	}
	public void setNumeroSolicitacaoCompra(Integer numeroSolicitacaoCompra) {
		this.numeroSolicitacaoCompra = numeroSolicitacaoCompra;
	}
	public Date getDataSolicitacaoCompra() {
		return dataSolicitacaoCompra;
	}
	public void setDataSolicitacaoCompra(Date dataSolicitacaoCompra) {
		this.dataSolicitacaoCompra = dataSolicitacaoCompra;
	}
	public Date getDataInicioSolicitacaoCompra() {
		return dataInicioSolicitacaoCompra;
	}
	public void setDataInicioSolicitacaoCompra(Date dataInicioSolicitacaoCompra) {
		this.dataInicioSolicitacaoCompra = dataInicioSolicitacaoCompra;
	}
	public Date getDataFimSolicitacaoCompra() {
		return dataFimSolicitacaoCompra;
	}
	public void setDataFimSolicitacaoCompra(Date dataFimSolicitacaoCompra) {
		this.dataFimSolicitacaoCompra = dataFimSolicitacaoCompra;
	}
	public ScoGrupoMaterial getGrupoMaterial() {
		return grupoMaterial;
	}
	public void setGrupoMaterial(ScoGrupoMaterial grupoMaterial) {
		this.grupoMaterial = grupoMaterial;
	}
	public ScoMaterial getMaterial() {
		return material;
	}
	public void setMaterial(ScoMaterial material) {
		this.material = material;
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
	public DominioSimNao getSolicitacaoAutorizada() {
		return solicitacaoAutorizada;
	}
	public void setSolicitacaoAutorizada(DominioSimNao solicitacaoAutorizada) {
		this.solicitacaoAutorizada = solicitacaoAutorizada;
	}
	public DominioSimNao getSolicitacaoExcluida() {
		return solicitacaoExcluida;
	}
	public void setSolicitacaoExcluida(DominioSimNao solicitacaoExcluida) {
		this.solicitacaoExcluida = solicitacaoExcluida;
	}
	public DominioSimNao getSolicitacaoDevolvida() {
		return solicitacaoDevolvida;
	}
	public void setSolicitacaoDevolvida(DominioSimNao solicitacaoDevolvida) {
		this.solicitacaoDevolvida = solicitacaoDevolvida;
	}
	public DominioSimNao getRepAutomatica() {
		return repAutomatica;
	}
	public void setRepAutomatica(DominioSimNao repAutomatica) {
		this.repAutomatica = repAutomatica;
	}
	public DominioSimNao getMatEstocavel() {
		return matEstocavel;
	}
	public void setMatEstocavel(DominioSimNao matEstocavel) {
		this.matEstocavel = matEstocavel;
	}
	public RapServidores getFiltroComprador() {
		return filtroComprador;
	}
	public void setFiltroComprador(RapServidores filtroComprador) {
		this.filtroComprador = filtroComprador;
	}
	public FsoVerbaGestao getVerbaGestao() {
		return verbaGestao;
	}
	public void setVerbaGestao(FsoVerbaGestao verbaGestao) {
		this.verbaGestao = verbaGestao;
	}
	public Date getDataInicioAnaliseSolicitacaoCompra() {
		return dataInicioAnaliseSolicitacaoCompra;
	}
	public void setDataInicioAnaliseSolicitacaoCompra(
			Date dataInicioAnaliseSolicitacaoCompra) {
		this.dataInicioAnaliseSolicitacaoCompra = dataInicioAnaliseSolicitacaoCompra;
	}
	public Date getDataFimAnaliseSolicitacaoCompra() {
		return dataFimAnaliseSolicitacaoCompra;
	}
	public void setDataFimAnaliseSolicitacaoCompra(
			Date dataFimAnaliseSolicitacaoCompra) {
		this.dataFimAnaliseSolicitacaoCompra = dataFimAnaliseSolicitacaoCompra;
	}
	public Date getDataInicioAutorizacaoSolicitacaoCompra() {
		return dataInicioAutorizacaoSolicitacaoCompra;
	}
	public void setDataInicioAutorizacaoSolicitacaoCompra(
			Date dataInicioAutorizacaoSolicitacaoCompra) {
		this.dataInicioAutorizacaoSolicitacaoCompra = dataInicioAutorizacaoSolicitacaoCompra;
	}
	public Date getDataFimAutorizacaoSolicitacaoCompra() {
		return dataFimAutorizacaoSolicitacaoCompra;
	}
	public void setDataFimAutorizacaoSolicitacaoCompra(
			Date dataFimAutorizacaoSolicitacaoCompra) {
		this.dataFimAutorizacaoSolicitacaoCompra = dataFimAutorizacaoSolicitacaoCompra;
	}
	public Date getDataUltimaExecucao() {
		return dataUltimaExecucao;
	}
	public void setDataUltimaExecucao(Date dataUltimaExecucao) {
		this.dataUltimaExecucao = dataUltimaExecucao;
	}
	public Boolean getFiltroVazio() {
		return filtroVazio;
	}
	public void setFiltroVazio(Boolean filtroVazio) {
		this.filtroVazio = filtroVazio;
	}
	public DominioSimNao getIndContrato() {
		return indContrato;
	}
	public void setIndContrato(DominioSimNao indContrato) {
		this.indContrato = indContrato;
	}
	public boolean isExibirScSolicitante() {
		return exibirScSolicitante;
	}
	public void setExibirScSolicitante(boolean exibirScSolicitante) {
		this.exibirScSolicitante = exibirScSolicitante;
	}
}
