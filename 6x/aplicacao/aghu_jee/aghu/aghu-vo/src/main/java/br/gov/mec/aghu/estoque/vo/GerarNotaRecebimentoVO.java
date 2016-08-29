package br.gov.mec.aghu.estoque.vo;

import br.gov.mec.aghu.dominio.DominioSituacaoAutorizacaoFornecimento;
import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;
import br.gov.mec.aghu.model.ScoUnidadeMedida;

public class GerarNotaRecebimentoVO {
	
	private ScoItemAutorizacaoForn itemAutorizacaoForn;
	private ScoAutorizacaoForn autorizacaoForn;
	//private ScoMaterial material;
	//private ScoServico servico;
	
	public ScoItemAutorizacaoForn getItemAutorizacaoForn() {
		return itemAutorizacaoForn;
	}
	public void setItemAutorizacaoForn(ScoItemAutorizacaoForn itemAutorizacaoForn) {
		this.itemAutorizacaoForn = itemAutorizacaoForn;
	}
	public ScoAutorizacaoForn getAutorizacaoForn() {
		return autorizacaoForn;
	}
	public void setAutorizacaoForn(ScoAutorizacaoForn autorizacaoForn) {
		this.autorizacaoForn = autorizacaoForn;
	}
	
	private Integer autorizacaoNumero;
	private Short complemento;
	private DominioSituacaoAutorizacaoFornecimento situacao;
	private Integer itemNumero;
	private String fornecedor;
	private Integer codigoMaterialServico;
	private String materialServico;
	private Integer saldo;
	private ScoUnidadeMedida unidade;


	public Integer getAutorizacaoNumero() {
		this.autorizacaoNumero = this.autorizacaoForn.getNumero();
		return autorizacaoNumero;
	}
	public void setAutorizacaoNumero(Integer autorizacaoNumero) {
		this.autorizacaoNumero = autorizacaoNumero;
	}
	
	public Short getComplemento() {
		this.complemento = this.autorizacaoForn.getNroComplemento();
		return complemento;
	}
	public void setComplemento(Short complemento) {
		this.complemento = complemento;
	}
	
	public DominioSituacaoAutorizacaoFornecimento getSituacao() {
		this.situacao = this.autorizacaoForn.getSituacao();
		return situacao;
	}
	
	public void setSituacao(DominioSituacaoAutorizacaoFornecimento situacao) {
		this.situacao = situacao;
	}

	public Integer getItemNumero() {
		this.itemNumero = this.itemAutorizacaoForn.getId().getNumero();
		return itemNumero;
	}
	public void setItemNumero(Integer itemNumero) {
		this.itemNumero = itemNumero;
	}
	public String getFornecedor() {
		this.fornecedor = this.itemAutorizacaoForn.getItemPropostaFornecedor().getPropostaFornecedor().getFornecedor().getNomeFantasia();
		return fornecedor;
	}
	public void setFornecedor(String fornecedor) {
		this.fornecedor = fornecedor;
	}
	
	public Integer getCodigoMaterialServico() {
		return codigoMaterialServico;
	}
	
	public void setCodigoMaterialServico(Integer codigoMaterialServico) {
		this.codigoMaterialServico = codigoMaterialServico;
	}
	
	public String getMaterialServico() {
		return materialServico;
	}
	
	public void setMaterialServico(String materialServico) {
		this.materialServico = materialServico;
	}
	
	
	public Integer getSaldo() {
		Integer qtdeSolicitada = this.itemAutorizacaoForn.getQtdeSolicitada() != null ? this.itemAutorizacaoForn.getQtdeSolicitada() : 0;
		Integer qtdeRecebida = this.itemAutorizacaoForn.getQtdeRecebida() != null ? this.itemAutorizacaoForn.getQtdeRecebida() : 0;
		this.saldo = qtdeSolicitada - qtdeRecebida;
		return saldo;
	}
	public void setSaldo(Integer saldo) {
		this.saldo = saldo;
	}
	public ScoUnidadeMedida getUnidade() {
		return unidade;
	}
	public void setUnidade(ScoUnidadeMedida unidade) {
		this.unidade = unidade;
	}	

}
