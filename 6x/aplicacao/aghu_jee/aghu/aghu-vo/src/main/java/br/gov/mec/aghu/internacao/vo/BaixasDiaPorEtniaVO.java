package br.gov.mec.aghu.internacao.vo;

import java.util.List;

public class BaixasDiaPorEtniaVO {

	private int id;
	private String descricao;
	private List<BaixasDiaVO> listaBaixasPorEtnia;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public List<BaixasDiaVO> getListaBaixasPorEtnia() {
		return listaBaixasPorEtnia;
	}

	public void setListaBaixasPorEtnia(List<BaixasDiaVO> listaBaixasPorEtnia) {
		this.listaBaixasPorEtnia = listaBaixasPorEtnia;
	}
	
}
