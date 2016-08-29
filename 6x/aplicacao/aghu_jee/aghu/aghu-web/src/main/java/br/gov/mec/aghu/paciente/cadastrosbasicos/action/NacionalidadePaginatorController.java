package br.gov.mec.aghu.paciente.cadastrosbasicos.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AipNacionalidades;
import br.gov.mec.aghu.paciente.cadastrosbasicos.business.ICadastrosBasicosPacienteFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * Classe responsável por controlar as ações do listagem de nacionalidades.
 * 
 * @author david.laks
 */

public class NacionalidadePaginatorController extends ActionController implements ActionPaginator {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3735240336688571771L;
	
	private static final String REDIRECIONA_MANTER_NACIONALIDADE = "nacionalidadeCRUD";

	@EJB
	private ICadastrosBasicosPacienteFacade cadastrosBasicosPacienteFacade;
	
	
	@Inject
	private NacionalidadeController nacionalidadeController;

	/**
	 * Atributo utilizado para controlar a exibicao do botao
	 * "incluir nacionalidade"
	 */
	private boolean exibirBotaoIncluirNacionalidade;

	/**
	 * Atributo referente ao campo de filtro de código de nacionalidade na tela
	 * de pesquisa.
	 */
	private Integer codigoPesquisaNacionalidade;

	/**
	 * Atributo referente ao campo de filtro de sigla de nacionalidade na tela
	 * de pesquisa.
	 */
	private String siglaPesquisaNacionalidade;

	/**
	 * Atributo referente ao campo de filtro de descrição de nacionalidade na
	 * tela de pesquisa.
	 */
	private String descricaoPesquisaNacionalidade;

	/**
	 * Atributo referente ao campo de filtro de situação da nacionalidade na
	 * tela de pesquisa.
	 */
	private DominioSituacao situacaoPesquisaNacionalidade;
	
	private AipNacionalidades nacionalidadeSelecionada;
	
	@Inject @Paginator
	private DynamicDataModel<AipNacionalidades> dataModel;
	
	
	@PostConstruct
	public void init(){
		this.begin(conversation);
	}
	
	//@Restrict("#{s:hasPermission('nacionalidade','pesquisar')}")
	public void pesquisar() {
		this.dataModel.reiniciarPaginator();
		this.exibirBotaoIncluirNacionalidade = true;
	}

	public void limparPesquisa() {
		this.codigoPesquisaNacionalidade = null;
		this.siglaPesquisaNacionalidade = null;
		this.descricaoPesquisaNacionalidade = null;
		this.situacaoPesquisaNacionalidade = null;
		this.exibirBotaoIncluirNacionalidade = false;
		this.dataModel.setPesquisaAtiva(false);
	}

	// ### Paginação ###

	@Override
	public Long recuperarCount() {
		Long count = cadastrosBasicosPacienteFacade.pesquisaNacionalidadesCount(
				this.codigoPesquisaNacionalidade,
				this.siglaPesquisaNacionalidade,
				this.descricaoPesquisaNacionalidade,
				this.situacaoPesquisaNacionalidade);

		return count;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<AipNacionalidades> recuperarListaPaginada(
			Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc) {

		List<AipNacionalidades> result = this.cadastrosBasicosPacienteFacade
				.pesquisaNacionalidades(firstResult, maxResults, orderProperty,
						asc, this.codigoPesquisaNacionalidade,
						this.siglaPesquisaNacionalidade,
						this.descricaoPesquisaNacionalidade,
						this.situacaoPesquisaNacionalidade);

		if (result == null) {
			result = new ArrayList<AipNacionalidades>();
		}

		return result;
	}
	
	public void reiniciarPaginator() {
		dataModel.reiniciarPaginator();		
	}
	
	/**
	 * Método chamado na tela de pesquisa de nacionalidade quando o usuário clicar no botão exluir da grid
	 * com a lista de nacionalidades
	 * 
	 * @param Código da nacionalidade a ser removida
	 */
	public void excluir() {
		try {
			this.dataModel.reiniciarPaginator();
			
			//Obtem nacionalidade e remove a mesma
			this.cadastrosBasicosPacienteFacade.excluirNacionalidade(nacionalidadeSelecionada);
			//Exibr mensagem de exclusão com sucesso e fecha janela de confirmação
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_REMOCAO_NACIONALIDADE");
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public String redirecionarInclusao() {
		return REDIRECIONA_MANTER_NACIONALIDADE;
	}
	
	public String editar(){
		nacionalidadeController.setAipNacionalidade(nacionalidadeSelecionada);
		return REDIRECIONA_MANTER_NACIONALIDADE;
	}
	

	// ### GETs e SETs ###

	public boolean isExibirBotaoIncluirNacionalidade() {
		return exibirBotaoIncluirNacionalidade;
	}

	public void setExibirBotaoIncluirNacionalidade(
			boolean exibirBotaoIncluirNacionalidade) {
		this.exibirBotaoIncluirNacionalidade = exibirBotaoIncluirNacionalidade;
	}

	public Integer getCodigoPesquisaNacionalidade() {
		return codigoPesquisaNacionalidade;
	}

	public void setCodigoPesquisaNacionalidade(
			Integer codigoPesquisaNacionalidade) {
		this.codigoPesquisaNacionalidade = codigoPesquisaNacionalidade;
	}

	public String getDescricaoPesquisaNacionalidade() {
		return descricaoPesquisaNacionalidade;
	}

	public void setDescricaoPesquisaNacionalidade(
			String descricaoPesquisaNacionalidade) {
		this.descricaoPesquisaNacionalidade = descricaoPesquisaNacionalidade;
	}

	public String getSiglaPesquisaNacionalidade() {
		return siglaPesquisaNacionalidade;
	}

	public void setSiglaPesquisaNacionalidade(String siglaPesquisaNacionalidade) {
		this.siglaPesquisaNacionalidade = siglaPesquisaNacionalidade;
	}

	public DominioSituacao getSituacaoPesquisaNacionalidade() {
		return situacaoPesquisaNacionalidade;
	}

	public void setSituacaoPesquisaNacionalidade(
			DominioSituacao situacaoPesquisaNacionalidade) {
		this.situacaoPesquisaNacionalidade = situacaoPesquisaNacionalidade;
	}

	public AipNacionalidades getNacionalidadeSelecionada() {
		return nacionalidadeSelecionada;
	}

	public void setNacionalidadeSelecionada(
			AipNacionalidades nacionalidadeSelecionada) {
		this.nacionalidadeSelecionada = nacionalidadeSelecionada;
	}

	public DynamicDataModel<AipNacionalidades> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AipNacionalidades> dataModel) {
		this.dataModel = dataModel;
	}

}
