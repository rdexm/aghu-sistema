package br.gov.mec.aghu.sig.custos.vo;

import org.apache.commons.lang3.builder.CompareToBuilder;



public class ItemComposicaoPorAtividadeObjetoCustoVO implements Comparable<ItemComposicaoPorAtividadeObjetoCustoVO>{

	private String item;
	private String centroCusto;
	private String direcionador;
	private String situacao;

	@Override
	public int compareTo(ItemComposicaoPorAtividadeObjetoCustoVO other) {
		return new CompareToBuilder()
				.append(this.getSituacao(), other.getSituacao())
				.append(this.getItem(), other.getItem())
				.toComparison();
	}
	
	public String getItem() {
		return item;
	}

	public void setItem(String item) {
		this.item = item;
	}
	
	public String getCentroCusto() {
		return centroCusto;
	}

	public void setCentroCusto(String centroCusto) {
		this.centroCusto = centroCusto;
	}

	public String getDirecionador() {
		return direcionador;
	}

	public void setDirecionador(String direcionador) {
		this.direcionador = direcionador;
	}

	public String getSituacao() {
		return situacao;
	}

	public void setSituacao(String situacao) {
		this.situacao = situacao;
	}
}
