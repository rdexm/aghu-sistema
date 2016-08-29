package br.gov.mec.aghu.exames.pesquisa.vo;

import java.util.Date;
import java.util.List;


public class FluxogramaLaborarorialVO {

	private List<FluxogramaLaborarorialDadosVO> dadosFluxograma;
	private List<Date> datasExibicao;

	
	public List<FluxogramaLaborarorialDadosVO> getDadosFluxograma() {
		return dadosFluxograma;
	}
	public void setDadosFluxograma(List<FluxogramaLaborarorialDadosVO> dadosFluxograma) {
		this.dadosFluxograma = dadosFluxograma;
	}
	public List<Date> getDatasExibicao() {
		return datasExibicao;
	}
	public void setDatasExibicao(List<Date> datasExibicao) {
		this.datasExibicao = datasExibicao;
	}
}