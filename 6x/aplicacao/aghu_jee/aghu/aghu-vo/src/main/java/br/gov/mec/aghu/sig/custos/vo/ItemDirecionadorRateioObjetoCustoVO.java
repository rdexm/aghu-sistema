package br.gov.mec.aghu.sig.custos.vo;

import org.apache.commons.lang3.builder.CompareToBuilder;



public class ItemDirecionadorRateioObjetoCustoVO implements Comparable<ItemDirecionadorRateioObjetoCustoVO>{

	private String descricao;
	private String percentual;
	private String situacao;

	@Override
	public int compareTo(ItemDirecionadorRateioObjetoCustoVO other) {
		return new CompareToBuilder()
				.append(this.getSituacao(), other.getSituacao())
				.append(this.getDescricao(), other.getDescricao())
				.toComparison();
	}
	
	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getPercentual() {
		return percentual;
	}

	public void setPercentual(String percentual) {
		this.percentual = percentual;
	}

	public String getSituacao() {
		return situacao;
	}

	public void setSituacao(String situacao) {
		this.situacao = situacao;
	}
}
