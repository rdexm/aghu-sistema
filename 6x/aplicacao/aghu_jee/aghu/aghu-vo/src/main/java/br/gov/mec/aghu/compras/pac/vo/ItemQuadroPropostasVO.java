package br.gov.mec.aghu.compras.pac.vo;

import java.io.Serializable;
import java.util.List;

public class ItemQuadroPropostasVO implements Serializable, Comparable<ItemQuadroPropostasVO>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5771266023242748926L;

	private Integer numItem;
	private Integer numSolicitacao;
	private String descSolicitacao;
	private String indMenorPreco;
	private Integer numMaterialServico;
	private String nomeMaterialServico;
	private String descMaterialServico;
	private Integer qtdSolicitada;
	private String unidadeSolicitada;	
	private List<PropostaFornecedorVO> listaProposta;
	private String tipoSolicitacao;

	public Integer getNumItem() {
		return numItem;
	}

	public void setNumItem(Integer numItem) {
		this.numItem = numItem;
	}

	public Integer getNumSolicitacao() {
		return numSolicitacao;
	}

	public void setNumSolicitacao(Integer numSolicitacao) {
		this.numSolicitacao = numSolicitacao;
	}

	public String getNomeMaterialServico() {
		return nomeMaterialServico;
	}

	public void setNomeMaterialServico(String nomeMaterialServico) {
		this.nomeMaterialServico = nomeMaterialServico;
	}

	public String getDescSolicitacao() {
		return descSolicitacao;
	}

	public void setDescSolicitacao(String descSolicitacao) {
		this.descSolicitacao = descSolicitacao;
	}

	public String getIndMenorPreco() {
		return indMenorPreco;
	}

	public void setIndMenorPreco(String indMenorPreco) {
		this.indMenorPreco = indMenorPreco;
	}

	public Integer getNumMaterialServico() {
		return numMaterialServico;
	}

	public void setNumMaterialServico(Integer numMaterialServico) {
		this.numMaterialServico = numMaterialServico;
	}

	public String getDescMaterialServico() {
		return descMaterialServico;
	}

	public void setDescMaterialServico(String descMaterialServico) {
		this.descMaterialServico = descMaterialServico;
	}

	public Integer getQtdSolicitada() {
		return qtdSolicitada;
	}

	public void setQtdSolicitada(Integer qtdSolicitada) {
		this.qtdSolicitada = qtdSolicitada;
	}

	public String getUnidadeSolicitada() {
		return unidadeSolicitada;
	}

	public void setUnidadeSolicitada(String unidadeSolicitada) {
		this.unidadeSolicitada = unidadeSolicitada;
	}

	public List<PropostaFornecedorVO> getListaProposta() {
		return listaProposta;
	}

	public void setListaProposta(List<PropostaFornecedorVO> listaProposta) {
		this.listaProposta = listaProposta;
	}
	
	public String getTipoSolicitacao() {
		return tipoSolicitacao;
	}

	public void setTipoSolicitacao(String tipoSolicitacao) {
		this.tipoSolicitacao = tipoSolicitacao;
	}

	@Override
	public int compareTo(ItemQuadroPropostasVO o) {
		return this.numItem.compareTo(o.numItem);

	}

	
}
