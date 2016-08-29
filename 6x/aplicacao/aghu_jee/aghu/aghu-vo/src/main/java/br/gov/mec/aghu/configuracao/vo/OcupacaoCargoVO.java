package br.gov.mec.aghu.configuracao.vo;

import java.io.Serializable;

public class OcupacaoCargoVO implements Serializable {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1291979650000268418L;
	
	
	Integer codigoOcupacao;
	String descricaoOcupacao;
	String cargoCodigo;
	
	public Integer getCodigoOcupacao() {
		return codigoOcupacao;
	}
	public void setCodigoOcupacao(Integer codigoOcupacao) {
		this.codigoOcupacao = codigoOcupacao;
	}
	public String getDescricaoOcupacao() {
		return descricaoOcupacao;
	}
	public void setDescricaoOcupacao(String descricaoOcupacao) {
		this.descricaoOcupacao = descricaoOcupacao;
	}
	public String getCargoCodigo() {
		return cargoCodigo;
	}
	public void setCargoCodigo(String cargoCodigo) {
		this.cargoCodigo = cargoCodigo;
	}

}
