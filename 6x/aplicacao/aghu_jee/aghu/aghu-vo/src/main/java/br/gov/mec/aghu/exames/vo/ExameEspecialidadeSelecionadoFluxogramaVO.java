package br.gov.mec.aghu.exames.vo;

import java.io.Serializable;

import br.gov.mec.aghu.model.AelEspecialidadeCampoLaudo;

public class ExameEspecialidadeSelecionadoFluxogramaVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2114860136019732273L;
	private Integer seqCampoLaudo;
	private AelEspecialidadeCampoLaudo especialidadeCampoLaudo;
	private boolean selecionado;
	
	public Integer getSeqCampoLaudo() {
		return seqCampoLaudo;
	}
	public void setSeqCampoLaudo(Integer seqCampoLaudo) {
		this.seqCampoLaudo = seqCampoLaudo;
	}
	public AelEspecialidadeCampoLaudo getEspecialidadeCampoLaudo() {
		return especialidadeCampoLaudo;
	}
	public void setEspecialidadeCampoLaudo(AelEspecialidadeCampoLaudo especialidadeCampoLaudo) {
		this.especialidadeCampoLaudo = especialidadeCampoLaudo;
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
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ExameEspecialidadeSelecionadoFluxogramaVO other = (ExameEspecialidadeSelecionadoFluxogramaVO) obj;
		if (seqCampoLaudo == null) {
			if (other.seqCampoLaudo != null) {
				return false;
			}
		} else if (!seqCampoLaudo.equals(other.seqCampoLaudo)) {
			return false;
		}
		return true;
	}
	
	
	

}
