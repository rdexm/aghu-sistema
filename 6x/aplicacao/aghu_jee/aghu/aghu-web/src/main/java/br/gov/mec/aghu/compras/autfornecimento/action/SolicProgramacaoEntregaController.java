package br.gov.mec.aghu.compras.autfornecimento.action;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import br.gov.mec.aghu.suprimentos.vo.ParcelaItemAutFornecimentoVO.TipoProgramacaoEntrega;
import br.gov.mec.aghu.core.action.ActionController;

public class SolicProgramacaoEntregaController extends ActionController{

	private static final long serialVersionUID = 6701682671726146682L;
	
	@Inject
	private ProgrEntregaItensAfController progrEntregaItensAfController;
	
	private Boolean mostraModalCentroCusto;
	private Boolean mostraModalVerbaGestao;
	private Boolean mostraModalCentroCustoSolicitacao;
	private Boolean mostraModalVerbaGestaoSolicitacao;
	private Boolean mostraModalNegativaAlteracaoVB;
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	public void gerarNovaSolicicao(){

		progrEntregaItensAfController.adicionarNaGrade(TipoProgramacaoEntrega.GERAR_SOLICITACAO);
		
		setMostraModalCentroCusto(Boolean.FALSE);
	}
	
	public void gerarSolicitacaoItemPacPropostaComplemento(){
		progrEntregaItensAfController.adicionarNaGrade(TipoProgramacaoEntrega.GERAR_TUDO);
		
		setMostraModalVerbaGestao(Boolean.FALSE);
	}
	
	public void alterarCentroCustoSolicitacaoInformada(){
		progrEntregaItensAfController.adicionarNaGrade(TipoProgramacaoEntrega.ALTERAR_SOLICITACAO);
		
		setMostraModalCentroCustoSolicitacao(Boolean.FALSE);
	}
	
	public void naoAlterarCentroCustoSolicitacaoInformada(){
		progrEntregaItensAfController.adicionarNaGrade(null);
		
		setMostraModalCentroCustoSolicitacao(Boolean.FALSE);
	}
	
	public void alterarVerbaGestaoSolicitacaoInformada(){
		progrEntregaItensAfController.adicionarNaGrade(TipoProgramacaoEntrega.ALTERAR_SOLICITACAO);
		
		setMostraModalVerbaGestaoSolicitacao(Boolean.FALSE);
	}

	public void gerarItemPacPropostaComplemento(){
		progrEntregaItensAfController.adicionarNaGrade(TipoProgramacaoEntrega.GERAR_ITEM_PAC_AF);
		
		setMostraModalNegativaAlteracaoVB(Boolean.FALSE);
	}

	public Boolean getMostraModalCentroCusto() {
		return mostraModalCentroCusto;
	}

	public void setMostraModalCentroCusto(Boolean mostraModalCentroCusto) {
		this.mostraModalCentroCusto = mostraModalCentroCusto;
	}

	public Boolean getMostraModalVerbaGestao() {
		return mostraModalVerbaGestao;
	}

	public void setMostraModalVerbaGestao(Boolean mostraModalVerbaGestao) {
		this.mostraModalVerbaGestao = mostraModalVerbaGestao;
	}

	public Boolean getMostraModalCentroCustoSolicitacao() {
		return mostraModalCentroCustoSolicitacao;
	}

	public void setMostraModalCentroCustoSolicitacao(
			Boolean mostraModalCentroCustoSolicitacao) {
		this.mostraModalCentroCustoSolicitacao = mostraModalCentroCustoSolicitacao;
	}

	public Boolean getMostraModalVerbaGestaoSolicitacao() {
		return mostraModalVerbaGestaoSolicitacao;
	}

	public void setMostraModalVerbaGestaoSolicitacao(
			Boolean mostraModalVerbaGestaoSolicitacao) {
		this.mostraModalVerbaGestaoSolicitacao = mostraModalVerbaGestaoSolicitacao;
	}

	public Boolean getMostraModalNegativaAlteracaoVB() {
		return mostraModalNegativaAlteracaoVB;
	}

	public void setMostraModalNegativaAlteracaoVB(
			Boolean mostraModalNegativaAlteracaoVB) {
		if (mostraModalNegativaAlteracaoVB){
			setMostraModalVerbaGestaoSolicitacao(false);
		}
		
		this.mostraModalNegativaAlteracaoVB = mostraModalNegativaAlteracaoVB;
	}
	

}
