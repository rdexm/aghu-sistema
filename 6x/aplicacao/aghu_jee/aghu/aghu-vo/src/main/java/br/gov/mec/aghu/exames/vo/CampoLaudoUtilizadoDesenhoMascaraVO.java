package br.gov.mec.aghu.exames.vo;

import br.gov.mec.aghu.model.AelParametroCamposLaudo;

public class CampoLaudoUtilizadoDesenhoMascaraVO {

	private boolean selecionado;
	private AelParametroCamposLaudo parametroCamposLaudo;

	public CampoLaudoUtilizadoDesenhoMascaraVO(boolean selecionado, AelParametroCamposLaudo parametroCamposLaudo) {
		this.selecionado = selecionado;
		this.parametroCamposLaudo = parametroCamposLaudo;
	}
	
	public boolean isSelecionado() {
		return selecionado;
	}
	public void setSelecionado(boolean selecionado) {
		this.selecionado = selecionado;
	}
	public AelParametroCamposLaudo getParametroCamposLaudo() {
		return parametroCamposLaudo;
	}
	public void setParametroCamposLaudo(AelParametroCamposLaudo parametroCamposLaudo) {
		this.parametroCamposLaudo = parametroCamposLaudo;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((parametroCamposLaudo == null) ? 0 : parametroCamposLaudo.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj){
			return true;
		}
		if (obj == null){
			return false;
		}
		if (getClass() != obj.getClass()){
			return false;
		}
		CampoLaudoUtilizadoDesenhoMascaraVO other = (CampoLaudoUtilizadoDesenhoMascaraVO) obj;
		if (parametroCamposLaudo == null) {
			if (other.parametroCamposLaudo != null){
				return false;
			}
		} else if (!parametroCamposLaudo.equals(other.parametroCamposLaudo)){
			return false;
		}
		return true;
	} 

}
