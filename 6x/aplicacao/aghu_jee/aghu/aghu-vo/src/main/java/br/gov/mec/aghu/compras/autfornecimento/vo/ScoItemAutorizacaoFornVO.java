package br.gov.mec.aghu.compras.autfornecimento.vo;

import java.io.Serializable;

import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.model.ScoSolicitacaoServico;


/**
 * 
 * 
 * 
 */
public class ScoItemAutorizacaoFornVO implements
		Serializable {

	private static final long serialVersionUID = -260135187653694605L;


	private ScoItemAutorizacaoForn itemAutorizacaoForn;
	private ScoSolicitacaoServico solicitacaoServico;
	private ScoSolicitacaoDeCompra solicitacaoCompra;
	private ScoAutorizacaoForn autorizacaoForn;
	private Integer numeroItem;
	
	
	public ScoItemAutorizacaoForn getItemAutorizacaoForn() {
		return itemAutorizacaoForn;
	}
	
	public void setItemAutorizacaoForn(ScoItemAutorizacaoForn itemAutorizacaoForn) {
		this.itemAutorizacaoForn = itemAutorizacaoForn;
	}
	public ScoSolicitacaoServico getSolicitacaoServico() {
		return solicitacaoServico;
	}
	public void setSolicitacaoServico(ScoSolicitacaoServico solicitacaoServico) {
		this.solicitacaoServico = solicitacaoServico;
	}
	public ScoSolicitacaoDeCompra getSolicitacaoCompra() {
		return solicitacaoCompra;
	}
	public void setSolicitacaoCompra(ScoSolicitacaoDeCompra solicitacaoCompra) {
		this.solicitacaoCompra = solicitacaoCompra;
	}

	
	public ScoAutorizacaoForn getAutorizacaoForn() {
		return autorizacaoForn;
	}

	public void setAutorizacaoForn(ScoAutorizacaoForn autorizacaoForn) {
		this.autorizacaoForn = autorizacaoForn;
	}


	public Integer getNumeroItem() {
		return numeroItem;
	}

	public void setNumeroItem(Integer numeroItem) {
		this.numeroItem = numeroItem;
	}


	public enum Fields {
		SCO_ITEM_AUT_FORN("itemAutorizacaoForn"),
		SCO_COMPRA("solicitacaoCompra"),
		SCO_SERVICO("solicitacaoServico"),
		NUMERO_ITEM("numeroItem"),
		AUT_FORN("autorizacaoForn");
		
	
	
		private String field;
	
		private Fields(String field) {
			this.field = field;
		}
	
		public String toString() {
			return this.field;
		}
	}
	

}