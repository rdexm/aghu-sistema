package br.gov.mec.aghu.compras.pac.vo;

import br.gov.mec.aghu.core.commons.BaseBean;

public class FasesLicitacaoVO implements BaseBean{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3381240212117481844L;
	
	private String inicioEstadoLicitacao;
	private String fimEstadoLicitacao;
	private String estadoLicitacao;
	private String complemento;
	
	
	public String getInicioEstadoLicitacao() {
		return inicioEstadoLicitacao;
	}
	public void setInicioEstadoLicitacao(String inicioEstadoLicitacao) {
		this.inicioEstadoLicitacao = inicioEstadoLicitacao;
	}
	public String getFimEstadoLicitacao() {
		return fimEstadoLicitacao;
	}
	public void setFimEstadoLicitacao(String fimEstadoLicitacao) {
		this.fimEstadoLicitacao = fimEstadoLicitacao;
	}
	public String getEstadoLicitacao() {
		return estadoLicitacao;
	}
	public void setEstadoLicitacao(String estadoLicitacao) {
		this.estadoLicitacao = estadoLicitacao;
	}
	public String getComplemento() {
		return complemento;
	}
	public void setComplemento(String complemento) {
		this.complemento = complemento;
	}
}
