package br.gov.mec.aghu.faturamento.vo;

import java.util.List;

public class RelatorioContasApresentasEspecialidadeVO {

	private Integer totalAihs;
	private String totalGeral;
	private List<ContaApresentadaEspecialidadeVO> listaContaApresentaEspecialidade;
	
	public Integer getTotalAihs() {
		return totalAihs;
	}
	public void setTotalAihs(Integer totalAihs) {
		this.totalAihs = totalAihs;
	}
	public String getTotalGeral() {
		return totalGeral;
	}
	public void setTotalGeral(String totalGeral) {
		this.totalGeral = totalGeral;
	}
	public List<ContaApresentadaEspecialidadeVO> getListaContaApresentaEspecialidade() {
		return listaContaApresentaEspecialidade;
	}
	public void setListaContaApresentaEspecialidade(List<ContaApresentadaEspecialidadeVO> listaContaApresentaEspecialidade) {
		this.listaContaApresentaEspecialidade = listaContaApresentaEspecialidade;
	}
	
	
}