package br.gov.mec.aghu.blococirurgico.cadastroapoio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.blococirurgico.cadastroapoio.business.IBlocoCirurgicoCadastroApoioFacade;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MbcTipoAnestesias;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;


public class PesquisaTiposAnestesiaPaginatorController extends ActionController implements ActionPaginator {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	@Inject @Paginator
	private DynamicDataModel<MbcTipoAnestesias> dataModel;

	/**
	 * 
	 */
	private static final long serialVersionUID = -4615760901841389986L;
	private static final String TIPOS_ANESTESIA_CRUD = "cadastroTiposAnestesia";
	
	@EJB
	private IBlocoCirurgicoCadastroApoioFacade blocoCirurgicoCadastroApoioFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@Inject
	private CadastroTiposAnestesiaController cadastroTiposAnestesiaController;

	// Controla exibição do botão "Novo"
	private boolean exibirBotaoNovo;

	// Campos de filtro
	private Short codigo;
	private String descricaoTipo;
	private DominioSimNao necessitaAnestesia;
	private DominioSimNao tipoCombinado;
	private DominioSituacao situacaoProfissional;
	
	/**
	 * Chamado no inicio de cada conversação
	 */
	public void inicio() {
		// Garante que os resultados da pesquisa serão mantidos ao retonar na tela 
		if(this.dataModel.getPesquisaAtiva()){
			this.pesquisar();
		}
	}

	/**
	 * Método executado ao clicar no botão pesquisar
	 */
	public void pesquisar() {
		this.dataModel.reiniciarPaginator();
		exibirBotaoNovo = true;
	}

	/**
	 * Método executado ao clicar no botão limpar
	 */
	public void limparPesquisa() {
		codigo = null;
		descricaoTipo = null;
		necessitaAnestesia = null;
		tipoCombinado = null;
		situacaoProfissional = null;
		this.exibirBotaoNovo = false;
		this.dataModel.setPesquisaAtiva(false);
	}
	
	public String editar(MbcTipoAnestesias tipoAnestesia){
		cadastroTiposAnestesiaController.setTipoAnestesia(tipoAnestesia);
		return TIPOS_ANESTESIA_CRUD;
	}
	
	public String iniciarInclusao(){
		cadastroTiposAnestesiaController.setTipoAnestesia(null);
		return TIPOS_ANESTESIA_CRUD;
	} 
	
	@Override
	public List<MbcTipoAnestesias> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return this.blocoCirurgicoCadastroApoioFacade.listarTiposAnestesiaFiltro(firstResult, maxResult, orderProperty, asc, 
				codigo, descricaoTipo, (necessitaAnestesia!=null)?necessitaAnestesia.isSim():null, (tipoCombinado!=null)?tipoCombinado.isSim():null, situacaoProfissional);
	}

	@Override
	public Long recuperarCount() {
		return this.blocoCirurgicoCadastroApoioFacade.listarTiposAnestesiaFiltroCount(codigo, descricaoTipo, (necessitaAnestesia!=null)?necessitaAnestesia.isSim():null, (tipoCombinado!=null)?tipoCombinado.isSim():null, situacaoProfissional);
	}

	
	/*
	 * GETs SETs
	 */

	public boolean isExibirBotaoNovo() {
		return exibirBotaoNovo;
	}

	public void setExibirBotaoNovo(boolean exibirBotaoNovo) {
		this.exibirBotaoNovo = exibirBotaoNovo;
	}

	
	public DominioSituacao getSituacaoProfissional() {
		return situacaoProfissional;
	}

	public void setSituacaoProfissional(DominioSituacao situacaoProfissional) {
		this.situacaoProfissional = situacaoProfissional;
	}

	public IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return registroColaboradorFacade;
	}

	public void setRegistroColaboradorFacade(
			IRegistroColaboradorFacade registroColaboradorFacade) {
		this.registroColaboradorFacade = registroColaboradorFacade;
	}

	public Short getCodigo() {
		return codigo;
	}

	public void setCodigo(Short codigo) {
		this.codigo = codigo;
	}

	public String getDescricaoTipo() {
		return descricaoTipo;
	}

	public void setDescricaoTipo(String descricaoTipo) {
		this.descricaoTipo = descricaoTipo;
	}

	public DominioSimNao getNecessitaAnestesia() {
		return necessitaAnestesia;
	}

	public void setNecessitaAnestesia(DominioSimNao necessitaAnestesia) {
		this.necessitaAnestesia = necessitaAnestesia;
	}

	public DominioSimNao getTipoCombinado() {
		return tipoCombinado;
	}

	public void setTipoCombinado(DominioSimNao tipoCombinado) {
		this.tipoCombinado = tipoCombinado;
	}
 


	public DynamicDataModel<MbcTipoAnestesias> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<MbcTipoAnestesias> dataModel) {
	 this.dataModel = dataModel;
	}
}