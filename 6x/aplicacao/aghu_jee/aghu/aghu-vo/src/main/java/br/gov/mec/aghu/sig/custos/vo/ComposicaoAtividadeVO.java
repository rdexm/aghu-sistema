package br.gov.mec.aghu.sig.custos.vo;

import java.util.List;


public class ComposicaoAtividadeVO implements Comparable<ComposicaoAtividadeVO>{

	private String nome;
	private String tipo;
	private String nomeCentroCusto;
	private String situacao;
	
	private List<ItemComposicaoAtividadeVO> itensComposicao;

	@Override
	public int compareTo(ComposicaoAtividadeVO other) {
		return this.getNome().compareToIgnoreCase(other.getNome());        
	}
	
	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getNomeCentroCusto() {
		return nomeCentroCusto;
	}

	public void setNomeCentroCusto(String nomeCentroCusto) {
		this.nomeCentroCusto = nomeCentroCusto;
	}

	public String getSituacao() {
		return situacao;
	}

	public void setSituacao(String situacao) {
		this.situacao = situacao;
	}

	public List<ItemComposicaoAtividadeVO> getItensComposicao() {
		return itensComposicao;
	}

	public void setItensComposicao(List<ItemComposicaoAtividadeVO> itensComposicao) {
		this.itensComposicao = itensComposicao;
	}

	
	
}
