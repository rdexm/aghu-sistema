package br.gov.mec.aghu.sig.custos.vo;

import org.apache.commons.lang3.builder.CompareToBuilder;



public class ItemComposicaoPorRecursoObjetoCustoVO implements Comparable<ItemComposicaoPorRecursoObjetoCustoVO>{

	private String tipo;
	private String recurso;
	private String quantidade;
	private String atividade;
	private String direcionador;
	private String situacao;

	@Override
	public int compareTo(ItemComposicaoPorRecursoObjetoCustoVO other) {
		return new CompareToBuilder()
				.append(this.getTipo(), other.getTipo())
				.append(this.getSituacao(), other.getSituacao())
				.append(this.getRecurso(), other.getRecurso())
				.toComparison();
	}
	
	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getRecurso() {
		return recurso;
	}

	public void setRecurso(String recurso) {
		this.recurso = recurso;
	}

	public String getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(String quantidade) {
		this.quantidade = quantidade;
	}

	public String getAtividade() {
		return atividade;
	}

	public void setAtividade(String atividade) {
		this.atividade = atividade;
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
