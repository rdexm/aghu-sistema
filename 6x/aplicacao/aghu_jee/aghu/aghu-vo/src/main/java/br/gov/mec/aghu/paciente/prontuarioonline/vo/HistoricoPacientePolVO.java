package br.gov.mec.aghu.paciente.prontuarioonline.vo;

import java.util.List;


public class HistoricoPacientePolVO {

	private String grupo;
	private List<SubHistorioPacientePolVO> subHistorico;
	private Boolean hasDataFim;
	
	public String getGrupo() {
		return grupo;
	}
	public void setGrupo(String grupo) {
		this.grupo = grupo;
	}

	public List<SubHistorioPacientePolVO> getSubHistorico() {
		return subHistorico;
	}
	public void setSubHistorico(List<SubHistorioPacientePolVO> subHistorico) {
		this.subHistorico = subHistorico;
	}
	public Boolean getHasDataFim() {
		return hasDataFim;
	}
	public void setHasDataFim(Boolean hasDataFim) {
		this.hasDataFim = hasDataFim;
	}
}
