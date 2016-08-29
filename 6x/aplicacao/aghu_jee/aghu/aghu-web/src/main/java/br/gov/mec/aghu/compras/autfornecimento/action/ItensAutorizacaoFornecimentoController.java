package br.gov.mec.aghu.compras.autfornecimento.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.compras.autfornecimento.business.IAutFornecimentoFacade;
import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;
import br.gov.mec.aghu.core.action.ActionController;


public class ItensAutorizacaoFornecimentoController extends ActionController{

	private static final String ASSINAR_AUTORIZACAO_FORNECIMENTO = "assinarAutorizacaoFornecimento";

	private static final long serialVersionUID = -6005769024181822853L;
	
	@EJB
	protected IAutFornecimentoFacade autFornecimentoFacade;
	
	@Inject
	protected EnvioEmailAssinaturaAFController envioEmailAssinaturaAFController;

	// utilizados na tela de visualização dos itens da AF
	private ScoAutorizacaoForn autFornecimento;
	private List<ScoItemAutorizacaoForn> listaItensAF;
	private Integer numeroAf;
	private Short numeroComplemento;
	
	// controle voltar da tela de itens da AF
	private String voltarPara;
	
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	
	public void iniciar(){
	 

	 

		setAutFornecimento(autFornecimentoFacade.obterAfByNumero(numeroAf));
		setListaItensAF(autFornecimento.getItensAutorizacaoForn());
	
	
	}
	
	
	public String getStringTruncada(String str, Integer tamanhoMaximo) {
		if (str.length() > tamanhoMaximo) {
			str = str.substring(0, tamanhoMaximo) + "...";
		}
		return str;
	}
	
	public String voltar() {
		return this.voltarPara;
	}
	
	public String voltarAssinarAf(){
		return ASSINAR_AUTORIZACAO_FORNECIMENTO;
	}
	
	
	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}
	
	public ScoAutorizacaoForn getAutFornecimento() {
		return autFornecimento;
	}

	public void setAutFornecimento(ScoAutorizacaoForn autFornecimento) {
		this.autFornecimento = autFornecimento;
	}
	
	public List<ScoItemAutorizacaoForn> getListaItensAF() {
		return listaItensAF;
	}
	
	public void setListaItensAF(List<ScoItemAutorizacaoForn> listaItensAF) {
		this.listaItensAF = listaItensAF;
	}

	public Integer getNumeroAf() {
		return numeroAf;
	}

	public void setNumeroAf(Integer numeroAf) {
		this.numeroAf = numeroAf;
	}


	public Short getNumeroComplemento() {
		return numeroComplemento;
	}


	public void setNumeroComplemento(Short numeroComplemento) {
		this.numeroComplemento = numeroComplemento;
	}
		
}
