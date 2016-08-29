package br.gov.mec.aghu.parametrosistema.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.model.AghSistemas;
import br.gov.mec.aghu.parametrosistema.business.IParametroSistemaFacade;
import br.gov.mec.aghu.parametrosistema.vo.AghSistemaVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;



public class ParametroSistemaListPaginatorController extends ActionController implements ActionPaginator {
	
	private static final long serialVersionUID = -2440797237327832130L;

	private static final String PAGE_CADASTRAR_SISTEMA = "parametroSistemaCRUD";
	
	
	private AghSistemas sistemaFiltro;
	
	private AghSistemaVO sistemaSelecionado;
	
	@EJB @SuppressWarnings("cdi-ambiguous-dependency")
	private IParametroSistemaFacade parametroSistemaFacade;

	
	
	@Inject @Paginator
	private DynamicDataModel<AghSistemaVO> dataModel;		
	
	
	@PostConstruct
	protected void init() {
		begin(conversation, true);
		this.setSistemaFiltro(new AghSistemas());
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see br.gov.mec.aghu.action.AGHUPaginatorController#recuperarCount()
	 */
	@Override
	public Long recuperarCount() {
		return parametroSistemaFacade.pesquisaParametroSistemaListCount(this.getSistemaFiltro().getSigla(), this.getSistemaFiltro().getNome());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.gov.mec.aghu.action.AGHUPaginatorController#recuperarListaPaginada
	 * (java.lang.Integer, java.lang.Integer, java.lang.String, boolean)
	 */
	@Override
	public List<AghSistemaVO> recuperarListaPaginada(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc) {
		return this.parametroSistemaFacade.pesquisaParametroSistemaList(
				firstResult, maxResult, orderProperty, asc, this.getSistemaFiltro()
						.getSigla(), this.getSistemaFiltro().getNome());
	}

	/**
	 * Método responsável para ação do botão 'Pesquisar'.
	 */
	public void pesquisar() {
		this.getDataModel().reiniciarPaginator();
		
		parametroSistemaFacade.obterTodosParametros();
		
	}

	/**
	 * Método que limpa os campos da tela de pesquisa.
	 */
	public void limparPesquisa() {
		this.setSistemaFiltro(new AghSistemas());
		this.getDataModel().limparPesquisa();
	}
	
	/**
	 * Método que realiza a ação do botão excluir na tela de 
	 * Pesquisa de Manter Parametro de Sistema.
	 * 
	 */
	public void excluir() {
		try {
			if (this.getSistemaSelecionado() != null && this.getSistemaSelecionado().getSigla() != null) {
				this.parametroSistemaFacade.excluirAghSistema(this.getSistemaSelecionado().getSigla());
				
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EXCLUSAO_PARAMETRO_SISTEMA", getSistemaSelecionado().getNome());
				this.getDataModel().reiniciarPaginator();
			}
			
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		} 
	}
	
	
	public String editar() {
		
		return PAGE_CADASTRAR_SISTEMA;		
	}
	

	
	public DynamicDataModel<AghSistemaVO> getDataModel() {
		return dataModel;
	}
	
	public void setDataModel(DynamicDataModel<AghSistemaVO> dataModel) {
		this.dataModel = dataModel;
	}

	public AghSistemas getSistemaFiltro() {
		return sistemaFiltro;
	}

	public void setSistemaFiltro(AghSistemas sistemaFiltro) {
		this.sistemaFiltro = sistemaFiltro;
	}

	public AghSistemaVO getSistemaSelecionado() {
		return sistemaSelecionado;
	}

	public void setSistemaSelecionado(AghSistemaVO s) {
		this.sistemaSelecionado = s;
	}
	
}
