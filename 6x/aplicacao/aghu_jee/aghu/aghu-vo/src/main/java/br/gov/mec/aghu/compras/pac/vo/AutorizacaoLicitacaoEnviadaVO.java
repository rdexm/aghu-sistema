package br.gov.mec.aghu.compras.pac.vo;

import br.gov.mec.aghu.core.commons.BaseBean;

public class AutorizacaoLicitacaoEnviadaVO implements BaseBean{	
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6308350495469076486L;
	
	
	private String executante;
	private String perfil;
	private String ocorrencia;
	
	
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
	public String getOcorrencia() {
		return ocorrencia;
	}
	public void setOcorrencia(String ocorrencia) {
		this.ocorrencia = ocorrencia;
	}
	

	
}
