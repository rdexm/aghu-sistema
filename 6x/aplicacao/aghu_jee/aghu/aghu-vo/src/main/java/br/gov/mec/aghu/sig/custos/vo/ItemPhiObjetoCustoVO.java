package br.gov.mec.aghu.sig.custos.vo;

import org.apache.commons.lang3.builder.CompareToBuilder;



public class ItemPhiObjetoCustoVO implements Comparable<ItemPhiObjetoCustoVO>{

	private String codigo;
	private String descricao;
	private String situacao;

	@Override
	public int compareTo(ItemPhiObjetoCustoVO other) {
		return new CompareToBuilder()
				.append(this.getSituacao(), other.getSituacao())
				.append(this.getCodigo(), other.getCodigo())
				.append(this.getDescricao(), other.getDescricao())
				.toComparison();
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getSituacao() {
		return situacao;
	}

	public void setSituacao(String situacao) {
		this.situacao = situacao;
	}
}
