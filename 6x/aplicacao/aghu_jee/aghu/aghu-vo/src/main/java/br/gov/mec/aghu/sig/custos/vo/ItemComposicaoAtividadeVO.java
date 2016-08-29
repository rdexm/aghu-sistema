package br.gov.mec.aghu.sig.custos.vo;

import org.apache.commons.lang3.builder.CompareToBuilder;



public class ItemComposicaoAtividadeVO implements Comparable<ItemComposicaoAtividadeVO>{

	private String recurso;
	private String especificacao;
	private String quantidade;
	private String situacao;


	@Override
	public int compareTo(ItemComposicaoAtividadeVO other) {
		//Ordenar por tipo de recurso, situação, descrição
		return new CompareToBuilder()
				.append(this.getRecurso(), other.getRecurso())
				.append(this.getSituacao(), other.getSituacao())
				.append(this.getEspecificacao(), other.getEspecificacao())
				.toComparison();
	}

	public String getRecurso() {
		return recurso;
	}

	public void setRecurso(String recurso) {
		this.recurso = recurso;
	}


	public String getEspecificacao() {
		return especificacao;
	}


	public void setEspecificacao(String especificacao) {
		this.especificacao = especificacao;
	}


	public String getQuantidade() {
		return quantidade;
	}


	public void setQuantidade(String quantidade) {
		this.quantidade = quantidade;
	}


	public String getSituacao() {
		return situacao;
	}


	public void setSituacao(String situacao) {
		this.situacao = situacao;
	}
}
