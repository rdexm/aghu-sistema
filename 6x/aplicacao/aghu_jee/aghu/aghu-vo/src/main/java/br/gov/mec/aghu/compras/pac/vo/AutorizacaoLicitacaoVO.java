package br.gov.mec.aghu.compras.pac.vo;

import br.gov.mec.aghu.core.commons.BaseBean;

public class AutorizacaoLicitacaoVO implements BaseBean{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3381240212117481844L;
	
	private String inicioAutorizacao;
	private String fimAutorizacao;
	private String executante;
	private String perfil;
	
	
	public String getInicioAutorizacao() {
		return inicioAutorizacao;
	}
	public void setInicioAutorizacao(String inicioAutorizacao) {
		this.inicioAutorizacao = inicioAutorizacao;
	}
	public String getFimAutorizacao() {
		return fimAutorizacao;
	}
	public void setFimAutorizacao(String fimAutorizacao) {
		this.fimAutorizacao = fimAutorizacao;
	}
	public String getExecutante() {
		return executante;
	}
	public void setExecutante(String executante) {
		this.executante = executante;
	}
	public String getPerfil() {
		return perfil;
	}
	public void setPerfil(String perfil) {
		this.perfil = perfil;
	}
	

	
}
