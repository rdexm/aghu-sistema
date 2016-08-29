package br.gov.mec.aghu.compras.autfornecimento.vo;

import java.io.Serializable;

import br.gov.mec.aghu.model.ScoItemAutorizacaoFornJn;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.model.ScoSolicitacaoServico;


/**
 * 
 * 
 * 
 */
public class ScoItemAutorizacaoFornJnVO implements
		Serializable {

	private static final long serialVersionUID = -260135187653694605L;


	private ScoItemAutorizacaoFornJn itemAutorizacaoForn;
	private ScoSolicitacaoServico solicitacaoServico;
	private ScoSolicitacaoDeCompra solicitacaoCompra;
	private Long seqJn;
	
	

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

	
	public Long getSeqJn() {
		return seqJn;
	}
	public void setSeqJn(Long seqJn) {
		this.seqJn = seqJn;
	}



	public ScoItemAutorizacaoFornJn getItemAutorizacaoForn() {
		return itemAutorizacaoForn;
	}
	public void setItemAutorizacaoForn(ScoItemAutorizacaoFornJn itemAutorizacaoForn) {
		this.itemAutorizacaoForn = itemAutorizacaoForn;
	}


	public enum Fields {
		SCO_ITEM_AUT_FORN_JN("itemAutorizacaoForn"),
		SCO_COMPRA("solicitacaoCompra"),
		SCO_SERVICO("solicitacaoServico"),
		ITEM_SEQ("seqJn");
		
	
	
		private String field;
	
		private Fields(String field) {
			this.field = field;
		}
	
		public String toString() {
			return this.field;
		}
	}
	

}