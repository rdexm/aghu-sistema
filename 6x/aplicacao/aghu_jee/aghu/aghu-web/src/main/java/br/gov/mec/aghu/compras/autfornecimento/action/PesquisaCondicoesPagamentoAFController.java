package br.gov.mec.aghu.compras.autfornecimento.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.compras.autfornecimento.business.IAutFornecimentoFacade;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.model.ScoCondicaoPagamentoPropos;
import br.gov.mec.aghu.model.ScoFormaPagamento;
import br.gov.mec.aghu.model.ScoParcelasPagamento;
import br.gov.mec.aghu.core.action.ActionController;


public class PesquisaCondicoesPagamentoAFController extends ActionController {

	private static final long serialVersionUID = 2017820965341911735L;
	
	// Parâmetros

	private Integer numeroAf;
	private Short complementoAf;
	private Short sequenciaAlteracao;
	private String origem;
	
	// Dependências
	
	@EJB
	private IAutFornecimentoFacade autFornecimentoFacade;
	
	@EJB
	private IComprasFacade comprasFacade;
	
	// Dados

	private ScoCondicaoPagamentoPropos condPgto;
	private List<ScoParcelasPagamento> parcelas;
	
	// Métodos

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	/**
	 * Obtem condição de pagamento.
	 */
	public void iniciar() {
	 

	 

		condPgto = autFornecimentoFacade.obterCondPgtoAutorizacaoFornJn(numeroAf, complementoAf, sequenciaAlteracao);
		parcelas = comprasFacade.obterParcelas(condPgto);
	
	}
	
	
	/**
	 * Obtem descrição da forma de pagamento.
	 */
	public String getFormaPagamento() {
		ScoFormaPagamento formaPgto = condPgto.getFormaPagamento();
		return String.format("%d - %s", formaPgto.getCodigo(), formaPgto.getDescricao());
	}
	
	public String voltar(){
		return origem;
	}

	// Getters/Setters

	public Integer getNumeroAf() {
		return numeroAf;
	}

	public void setNumeroAf(Integer numeroAf) {
		this.numeroAf = numeroAf;
	}

	public Short getComplementoAf() {
		return complementoAf;
	}

	public void setComplementoAf(Short complementoAf) {
		this.complementoAf = complementoAf;
	}

	public Short getSequenciaAlteracao() {
		return sequenciaAlteracao;
	}

	public void setSequenciaAlteracao(Short sequenciaAlteracao) {
		this.sequenciaAlteracao = sequenciaAlteracao;
	}

	public String getOrigem() {
		return origem;
	}

	public void setOrigem(String origem) {
		this.origem = origem;
	}

	public ScoCondicaoPagamentoPropos getCondPgto() {
		return condPgto;
	}

	public List<ScoParcelasPagamento> getParcelas() {
		return parcelas;
	}
}