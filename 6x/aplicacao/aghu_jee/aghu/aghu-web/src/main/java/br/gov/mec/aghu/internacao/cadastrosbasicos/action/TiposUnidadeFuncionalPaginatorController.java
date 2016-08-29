package br.gov.mec.aghu.internacao.cadastrosbasicos.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.casca.model.Perfil;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.AghTiposUnidadeFuncional;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * Classe responsável por controlar as ações da listagem de tipo de unidade funcional.
 */



public class TiposUnidadeFuncionalPaginatorController  extends ActionController implements ActionPaginator {


	/**
	 * 
	 */
	private static final long serialVersionUID = -1712613170642413556L;

	

	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;

	

	/**
	 * Atributo referente ao campo de filtro de código de tipo de unidade funcional na tela
	 * de pesquisa.
	 */
	private Integer codigoPesquisaTiposUnidadeFuncional;

	/**
	 * Atributo referente ao campo de filtro de descrição de tipo de unidade funncional na
	 * tela de pesquisa.
	 */
	private String descricaoPesquisaTiposUnidadeFuncional;
	
	@Inject @Paginator
	private DynamicDataModel<Perfil> dataModel;	
	
	private AghTiposUnidadeFuncional tipoUnidadeFuncional;
	
	private final String PAGE_CAD_TIPO_UNID_FUNC = "tiposUnidadeFuncionalCRUD";


	@PostConstruct
	public void init() {
		begin(conversation, true);	
		tipoUnidadeFuncional = new AghTiposUnidadeFuncional();
	}

	
	public void pesquisar() {
		this.dataModel.reiniciarPaginator();		
	}

	public void limparPesquisa() {
		this.codigoPesquisaTiposUnidadeFuncional = null;
		this.descricaoPesquisaTiposUnidadeFuncional = null;	
		this.dataModel.limparPesquisa();
	}

	/**
	 * Método que realiza a ação do botão excluir na tela de Pesquisa de
	 * Tipo de unidade funcional.
	 */
	public void excluir() {
				
		
		try {
			if (tipoUnidadeFuncional.getCodigo() != null) {
				this.cadastrosBasicosInternacaoFacade.removerTiposUnidadeFuncional(tipoUnidadeFuncional.getCodigo());
				apresentarMsgNegocio(Severity.INFO,
						"MENSAGEM_SUCESSO_REMOCAO_TIPOUNIDADEFUNCIONAL",
						tipoUnidadeFuncional.getDescricao());
			} else {
				apresentarMsgNegocio(Severity.INFO,
						"MENSAGEM_ERRO_REMOCAO_TIPOUNIDADEFUNCIONAL_INVALIDA");
			}

			this.codigoPesquisaTiposUnidadeFuncional = null;
			this.dataModel.reiniciarPaginator();
		} catch (ApplicationBusinessException e) {			
			apresentarExcecaoNegocio(e);
		}
	}
	
	public String novo(){		
		this.setTipoUnidadeFuncional(null);
		return PAGE_CAD_TIPO_UNID_FUNC;
	}
	public String editar(){		
		return PAGE_CAD_TIPO_UNID_FUNC;
	}

	// ### Paginação ###

	@Override
	public Long recuperarCount() {
		return cadastrosBasicosInternacaoFacade.pesquisaTiposUnidadeFuncionalCount(
				this.codigoPesquisaTiposUnidadeFuncional,
				this.descricaoPesquisaTiposUnidadeFuncional);
		
	}


	@Override
	public List<AghTiposUnidadeFuncional> recuperarListaPaginada(
			Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc) {

		return this.cadastrosBasicosInternacaoFacade
				.pesquisaTiposUnidadeFuncional(firstResult, maxResults, orderProperty,
						asc, this.codigoPesquisaTiposUnidadeFuncional,
						this.descricaoPesquisaTiposUnidadeFuncional);		
		
		
	}

	// ### GETs e SETs ###

	
	public Integer getCodigoPesquisaTiposUnidadeFuncional() {
		return codigoPesquisaTiposUnidadeFuncional;
	}

	public void setCodigoPesquisaTiposUnidadeFuncional(
			Integer codigoPesquisaTiposUnidadeFuncional) {
		this.codigoPesquisaTiposUnidadeFuncional = codigoPesquisaTiposUnidadeFuncional;
	}

	public String getDescricaoPesquisaTiposUnidadeFuncional() {
		return descricaoPesquisaTiposUnidadeFuncional;
	}

	public void setDescricaoPesquisaTiposUnidadeFuncional(
			String descricaoPesquisaTiposUnidadeFuncional) {
		this.descricaoPesquisaTiposUnidadeFuncional = descricaoPesquisaTiposUnidadeFuncional;
	}

	public AghTiposUnidadeFuncional getTipoUnidadeFuncional() {
		return tipoUnidadeFuncional;
	}

	public void setTipoUnidadeFuncional(
			AghTiposUnidadeFuncional tipoUnidadeFuncional) {
		this.tipoUnidadeFuncional = tipoUnidadeFuncional;
	}

	public DynamicDataModel<Perfil> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<Perfil> dataModel) {
		this.dataModel = dataModel;
	}
	
}
