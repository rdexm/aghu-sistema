package br.gov.mec.aghu.prescricaomedica.vo;

import java.io.Serializable;

public class ViaAdministracaoPermitidaUnidadeVO implements Serializable {

	private static final long serialVersionUID = -8207049101534747901L;

	private String sigla;
	private String descricao;
	private Boolean marcarLinha;
	private Boolean selecionadoNaBase;
	
	public String getSigla() {
		return sigla;
	}
	
	public void setSigla(String sigla) {
		this.sigla = sigla;
	}
	
	public String getDescricao() {
		return descricao;
	}
	
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	public Boolean getMarcarLinha() {
		return marcarLinha;
	}
	
	public void setMarcarLinha(Boolean marcarLinha) {
		this.marcarLinha = marcarLinha;
	}
	
	public Boolean getSelecionadoNaBase() {
		return selecionadoNaBase;
	}
	
	public void setSelecionadoNaBase(Boolean selecionadoNaBase) {
		this.selecionadoNaBase = selecionadoNaBase;
	}
	
}
