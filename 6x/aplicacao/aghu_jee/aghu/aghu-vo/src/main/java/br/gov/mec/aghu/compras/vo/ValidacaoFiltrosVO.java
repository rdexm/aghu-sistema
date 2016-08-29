package br.gov.mec.aghu.compras.vo;



public class ValidacaoFiltrosVO {
	
	private boolean visualizaCamposSolicitacao = true;
	private boolean habilitaServico = false;
	private boolean desabilitaCurvaABC = false;
	private boolean habilitaEstocavel = true;
	private boolean habilitaCP = false;
	private boolean visualizaVencimento = false;
	
	public void setHabilitaServico(boolean habilitaServico) {
		this.habilitaServico = habilitaServico;
	}
	public boolean isHabilitaServico() {
		return habilitaServico;
	}
	public void setDesabilitaCurvaABC(boolean desabilitaCurvaABC) {
		this.desabilitaCurvaABC = desabilitaCurvaABC;
	}
	public boolean isDesabilitaCurvaABC() {
		return desabilitaCurvaABC;
	}
	public void setVisualizaCamposSolicitacao(boolean visualizaCamposSolicitacao) {
		this.visualizaCamposSolicitacao = visualizaCamposSolicitacao;
	}
	public boolean isVisualizaCamposSolicitacao() {
		return visualizaCamposSolicitacao;
	}
	public void setHabilitaEstocavel(boolean habilitaEstocavel) {
		this.habilitaEstocavel = habilitaEstocavel;
	}
	public boolean isHabilitaEstocavel() {
		return habilitaEstocavel;
	}
	public void setHabilitaCP(boolean habilitaCP) {
		this.habilitaCP = habilitaCP;
	}
	public boolean isHabilitaCP() {
		return habilitaCP;
	}
	public void setVisualizaVencimento(boolean visualizaVencimento) {
		this.visualizaVencimento = visualizaVencimento;
	}
	public boolean isVisualizaVencimento() {
		return visualizaVencimento;
	}

}
