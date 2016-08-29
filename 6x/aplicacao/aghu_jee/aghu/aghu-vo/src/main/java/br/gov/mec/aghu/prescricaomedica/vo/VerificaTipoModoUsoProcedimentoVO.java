package br.gov.mec.aghu.prescricaomedica.vo;

import java.io.Serializable;


public class VerificaTipoModoUsoProcedimentoVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5091070146824143888L;

	private Boolean exigeQuantidade;
	
	private Boolean retorno;
	
	public VerificaTipoModoUsoProcedimentoVO() {
	}
	
	public VerificaTipoModoUsoProcedimentoVO(Boolean exigeQuantidade, Boolean retorno) {
		this.exigeQuantidade = exigeQuantidade;
		this.retorno = retorno;
	}

	public Boolean getExigeQuantidade() {
		return exigeQuantidade;
	}

	public void setExigeQuantidade(Boolean exigeQuantidade) {
		this.exigeQuantidade = exigeQuantidade;
	}

	public Boolean getRetorno() {
		return retorno;
	}

	public void setRetorno(Boolean retorno) {
		this.retorno = retorno;
	}
	
}
