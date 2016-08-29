package br.gov.mec.aghu.exames.vo;

import br.gov.mec.aghu.model.AelCampoLaudo;


public class ExameDisponivelFluxogramaVO {
	
	private AelCampoLaudo campoLaudo;
	private boolean selecionado;
	
	public AelCampoLaudo getCampoLaudo() {
		return campoLaudo;
	}
	public void setCampoLaudo(AelCampoLaudo campoLaudo) {
		this.campoLaudo = campoLaudo;
	}
	public boolean isSelecionado() {
		return selecionado;
	}
	public void setSelecionado(boolean selecionado) {
		this.selecionado = selecionado;
	}


}
