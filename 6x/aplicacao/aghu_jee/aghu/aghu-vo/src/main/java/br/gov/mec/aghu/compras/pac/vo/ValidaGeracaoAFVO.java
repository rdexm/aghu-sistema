package br.gov.mec.aghu.compras.pac.vo;

import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;

public class ValidaGeracaoAFVO {
	
	private ScoAutorizacaoForn ultimaAutorizacaoFornecimentoGerada;
	private ScoItemAutorizacaoForn ultimoItemAutorizacaoFornecimento;
	private Integer vIafNumero;
	private Integer nroComplemento;
	private Integer qtdAfsGerada;
	public ScoAutorizacaoForn getUltimaAutorizacaoFornecimentoGerada() {
		return ultimaAutorizacaoFornecimentoGerada;
	}
	public void setUltimaAutorizacaoFornecimentoGerada(
			ScoAutorizacaoForn ultimaAutorizacaoFornecimentoGerada) {
		this.ultimaAutorizacaoFornecimentoGerada = ultimaAutorizacaoFornecimentoGerada;
	}
	public Integer getvIafNumero() {
		return vIafNumero;
	}
	public void setvIafNumero(Integer vIafNumero) {
		this.vIafNumero = vIafNumero;
	}
	public Integer getNroComplemento() {
		return nroComplemento;
	}
	public void setNroComplemento(Integer nroComplemento) {
		this.nroComplemento = nroComplemento;
	}
	public Integer getQtdAfsGerada() {
		return qtdAfsGerada;
	}
	public void setQtdAfsGerada(Integer qtdAfsGerada) {
		this.qtdAfsGerada = qtdAfsGerada;
	}
	public ScoItemAutorizacaoForn getUltimoItemAutorizacaoFornecimento() {
		return ultimoItemAutorizacaoFornecimento;
	}
	public void setUltimoItemAutorizacaoFornecimento(
			ScoItemAutorizacaoForn ultimoItemAutorizacaoFornecimento) {
		this.ultimoItemAutorizacaoFornecimento = ultimoItemAutorizacaoFornecimento;
	}
	
	

}
