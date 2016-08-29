package br.gov.mec.aghu.exames.vo;

import br.gov.mec.aghu.model.AelServidorCampoLaudo;

public class ExameSelecionadoFluxogramaVO {
	
	private Integer seqCampoLaudo;
	private AelServidorCampoLaudo servidorCampoLaudo;
	private boolean selecionado;
	private boolean removerLista;
	
	public Integer getSeqCampoLaudo() {
		return seqCampoLaudo;
	}
	public void setSeqCampoLaudo(Integer seqCampoLaudo) {
		this.seqCampoLaudo = seqCampoLaudo;
	}
	public AelServidorCampoLaudo getServidorCampoLaudo() {
		return servidorCampoLaudo;
	}
	public void setServidorCampoLaudo(AelServidorCampoLaudo servidorCampoLaudo) {
		this.servidorCampoLaudo = servidorCampoLaudo;
	}
	public boolean isSelecionado() {
		return selecionado;
	}
	public void setSelecionado(boolean selecionado) {
		this.selecionado = selecionado;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((seqCampoLaudo == null) ? 0 : seqCampoLaudo.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null){
			return false;
		}
		if (getClass() != obj.getClass()){
			return false;
		}
		ExameSelecionadoFluxogramaVO other = (ExameSelecionadoFluxogramaVO) obj;
		if (seqCampoLaudo == null) {
			if (other.seqCampoLaudo != null){
				return false;
			}
		} else if (!seqCampoLaudo.equals(other.seqCampoLaudo)){
			return false;
		}
		return true;
	}
	public boolean isRemoverLista() {
		return removerLista;
	}
	public void setRemoverLista(boolean removerLista) {
		this.removerLista = removerLista;
	}
	
	
	

}
