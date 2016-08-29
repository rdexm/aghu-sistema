package br.gov.mec.aghu.sig.custos.vo;

import org.apache.commons.lang3.builder.CompareToBuilder;



public class ItemClienteObjetoCustoVO implements Comparable<ItemClienteObjetoCustoVO>{

	private String cliente;
	private String direcionador;
	private String peso;
	private String situacao;


	@Override
	public int compareTo(ItemClienteObjetoCustoVO other) {
		return new CompareToBuilder()
				.append(this.getSituacao(), other.getSituacao())
				.append(this.getCliente(), other.getCliente())
				.toComparison();
	}
	
	public String getCliente() {
		return cliente;
	}

	public void setCliente(String cliente) {
		this.cliente = cliente;
	}

	public String getPeso() {
		return peso;
	}

	public void setPeso(String peso) {
		this.peso = peso;
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
