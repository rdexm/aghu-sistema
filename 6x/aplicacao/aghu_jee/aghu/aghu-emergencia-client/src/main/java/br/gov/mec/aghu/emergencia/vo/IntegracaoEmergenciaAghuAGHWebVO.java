package br.gov.mec.aghu.emergencia.vo;

import java.io.Serializable;

public class IntegracaoEmergenciaAghuAGHWebVO implements Serializable {

	private static final long serialVersionUID = 5696509444357422254L;
	
	private String form;
	private String urlAghWeb;
	private String parametros;
	private Boolean redirecionaPesquisaGestacao = Boolean.FALSE;
	
	public String getForm() {
		return form;
	}
	public void setForm(String form) {
		this.form = form;
	}
	public String getUrlAghWeb() {
		return urlAghWeb;
	}
	public void setUrlAghWeb(String urlAghWeb) {
		this.urlAghWeb = urlAghWeb;
	}
	public String getParametros() {
		return parametros;
	}
	public void setParametros(String parametros) {
		this.parametros = parametros;
	}
	public Boolean getRedirecionaPesquisaGestacao() {
		return redirecionaPesquisaGestacao;
	}
	public void setRedirecionaPesquisaGestacao(Boolean redirecionaPesquisaGestacao) {
		this.redirecionaPesquisaGestacao = redirecionaPesquisaGestacao;
	}
	
}
