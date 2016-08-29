package br.gov.mec.aghu.compras.autfornecimento.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.compras.autfornecimento.business.IAutFornecimentoFacade;
import br.gov.mec.aghu.compras.autfornecimento.vo.PesquisaItensPendentesPacVO;
import br.gov.mec.aghu.compras.pac.business.IPacFacade;
import br.gov.mec.aghu.model.ScoLicitacao;
import br.gov.mec.aghu.core.action.ActionController;


public class ItensPendentesPacController extends ActionController {

	private static final String ASSINAR_AUTORIZACAO_FORNECIMENTO = "assinarAutorizacaoFornecimento";

	private static final long serialVersionUID = -101869066447890002L;

	@EJB
	protected IAutFornecimentoFacade autFornecimentoFacade;
	
	@EJB
	protected IPacFacade pacFacade;

	private Integer lctNumero;
	private List<PesquisaItensPendentesPacVO> itensPendentesPacVOs;
	private ScoLicitacao licitacao;

	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	public void inicio() {
	 

	 

		this.licitacao = this.pacFacade.obterLicitacao(this.lctNumero);
			this.pesquisar();
	
	}
	

	public void pesquisar() {
		this.itensPendentesPacVOs = this.autFornecimentoFacade.pesquisarItemLicitacaoPorNumeroLicitacao(this.lctNumero);
	}

	
	public String voltar(){
		return ASSINAR_AUTORIZACAO_FORNECIMENTO;
	}
	
	public void setItensPendentesPacVOs(List<PesquisaItensPendentesPacVO> itensPendentesPacVOs) {
		this.itensPendentesPacVOs = itensPendentesPacVOs;
	}

	public List<PesquisaItensPendentesPacVO> getItensPendentesPacVOs() {
		return itensPendentesPacVOs;
	}

	public void setLctNumero(Integer lctNumero) {
		this.lctNumero = lctNumero;
	}

	public Integer getLctNumero() {
		return lctNumero;
	}

	public void setLicitacao(ScoLicitacao licitacao) {
		this.licitacao = licitacao;
	}

	public ScoLicitacao getLicitacao() {
		return licitacao;
	}

}
