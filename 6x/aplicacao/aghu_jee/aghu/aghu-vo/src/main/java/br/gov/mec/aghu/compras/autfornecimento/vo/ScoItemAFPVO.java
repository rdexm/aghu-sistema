package br.gov.mec.aghu.compras.autfornecimento.vo;

import java.io.Serializable;

import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;
import br.gov.mec.aghu.model.ScoProgEntregaItemAutorizacaoFornecimento;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.model.ScoSolicitacaoServico;


/**
 * 
 * 
 * 
 */
public class ScoItemAFPVO implements 	Serializable {

	private static final long serialVersionUID = -260135187653694605L;


	private ScoItemAutorizacaoForn itemAutorizacaoForn;
	private ScoSolicitacaoServico solicitacaoServico;
	private ScoSolicitacaoDeCompra solicitacaoCompra;
	private ScoAutorizacaoForn autorizacaoForn;
	private Integer numeroItem;
	
	private ScoProgEntregaItemAutorizacaoFornecimento itemAutorizacaoFornPedido;
	private Integer iafAfnNumero;
	private Integer iafNumero;
	private Integer numeroParcela;
	private Integer seqItemAfp;
	
	
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


	public ScoProgEntregaItemAutorizacaoFornecimento getItemAutorizacaoFornPedido() {
		return itemAutorizacaoFornPedido;
	}

	public void setItemAutorizacaoFornPedido(
			ScoProgEntregaItemAutorizacaoFornecimento itemAutorizacaoFornPedido) {
		this.itemAutorizacaoFornPedido = itemAutorizacaoFornPedido;
	}


	public Integer getIafAfnNumero() {
		return iafAfnNumero;
	}

	public void setIafAfnNumero(Integer iafAfnNumero) {
		this.iafAfnNumero = iafAfnNumero;
	}

	public Integer getIafNumero() {
		return iafNumero;
	}

	public void setIafNumero(Integer iafNumero) {
		this.iafNumero = iafNumero;
	}

	public Integer getNumeroParcela() {
		return numeroParcela;
	}

	public void setNumeroParcela(Integer numeroParcela) {
		this.numeroParcela = numeroParcela;
	}

	public Integer getSeqItemAfp() {
		return seqItemAfp;
	}

	public void setSeqItemAfp(Integer seqItemAfp) {
		this.seqItemAfp = seqItemAfp;
	}


	public enum Fields {
		SCO_ITEM_AUT_FORN("itemAutorizacaoForn"),
		SCO_COMPRA("solicitacaoCompra"),
		SCO_SERVICO("solicitacaoServico"),
		NUMERO_ITEM("numeroItem"),
		AUT_FORN("autorizacaoForn"),
		SCO_ITEM_AFP ("itemAutorizacaoFornPedido"),
		IAF_AFN_NUMERO("iafAfnNumero"),
		IAF_NUMERO("iafNumero"),
		SEQ_ITEM_AFP("seqItemAfp"),
		NUMERO_PARCELA("numeroParcela");
		
	
	
		private String field;
	
		private Fields(String field) {
			this.field = field;
		}
	
		public String toString() {
			return this.field;
		}
	}
	

}