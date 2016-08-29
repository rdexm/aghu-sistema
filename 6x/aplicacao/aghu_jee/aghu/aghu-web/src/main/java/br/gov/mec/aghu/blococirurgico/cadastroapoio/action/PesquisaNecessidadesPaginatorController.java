package br.gov.mec.aghu.blococirurgico.cadastroapoio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.blococirurgico.cadastroapoio.business.IBlocoCirurgicoCadastroApoioFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MbcNecessidadeCirurgica;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class PesquisaNecessidadesPaginatorController extends ActionController implements ActionPaginator {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	@Inject @Paginator
	private DynamicDataModel<MbcNecessidadeCirurgica> dataModel;

	/**
	 * 
	 */
	private static final long serialVersionUID = -4615760901841389986L;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IBlocoCirurgicoCadastroApoioFacade blocoCirurgicoCadastroApoioFacade;
	
	@Inject
	private CadastroNecessidadesController cadastroNecessidadesController;
	
	private static final String CADASTRO_NECESSIDADE_CRUD = "cadastroNecessidades";
	
	// Controla exibição do botão "Novo"
	private boolean exibirBotaoNovo;

	// Campos de filtro
	private Short codigoNecessidade;
	private AghUnidadesFuncionais unidadeExecutora;
	private String descricaoNecessidade;
	private DominioSituacao situacaoNecessidade;
	private DominioSimNao requerDescricao;
	
	private MbcNecessidadeCirurgica itemExclusao;
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
		// Limpa filtro
		codigoNecessidade=null;
		unidadeExecutora=null;
		descricaoNecessidade=null;
		situacaoNecessidade = null;
		requerDescricao=null;
		
		this.exibirBotaoNovo = false;
		this.dataModel.setPesquisaAtiva(false);
	}
	
	public String editar(MbcNecessidadeCirurgica necessidade){
		cadastroNecessidadesController.setNecessidade(necessidade);
		return CADASTRO_NECESSIDADE_CRUD;
	}
	
	
	public void excluir(){
		try{	
			blocoCirurgicoCadastroApoioFacade.removerMbcNecessidadeCirurgica(itemExclusao);
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_REMOCAO_NECESSIDADE");
			
			itemExclusao=null;
			this.dataModel.reiniciarPaginator();
			exibirBotaoNovo=true;
			
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public String iniciarInclusao(){
		cadastroNecessidadesController.setNecessidade(new MbcNecessidadeCirurgica());
		return CADASTRO_NECESSIDADE_CRUD;
	} 
	
	
	@Override
	public List<MbcNecessidadeCirurgica> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return this.blocoCirurgicoCadastroApoioFacade.listarNecessidadesFiltro(firstResult, maxResult, orderProperty, asc, this.codigoNecessidade, this.descricaoNecessidade, this.unidadeExecutora, this.situacaoNecessidade, (this.requerDescricao!=null)?this.requerDescricao.isSim():null);
	}

	@Override
	public Long recuperarCount() {
		return this.blocoCirurgicoCadastroApoioFacade.listarNecessidadesFiltroCount(this.codigoNecessidade, this.descricaoNecessidade, this.unidadeExecutora, this.situacaoNecessidade, (this.requerDescricao!=null)?this.requerDescricao.isSim():null);
	}

	/**
	 * Obtem unidade funcional ativa executora de cirurgias
	 */
	public List<AghUnidadesFuncionais> obterUnidadeExecutora(String filtro) {
		return this.returnSGWithCount(this.aghuFacade.pesquisarUnidadeFuncionalPorSeqDescricao((String)filtro, true),obterUnidadeExecutoraCount(filtro));
	}
	
    public Long obterUnidadeExecutoraCount(String filtro) {
        return this.aghuFacade.pesquisarUnidadeFuncionalPorSeqDescricaoCount((String)filtro, true);
    }

	/*
	 * Acessadores
	 */

	public boolean isExibirBotaoNovo() {
		return exibirBotaoNovo;
	}

	public void setExibirBotaoNovo(boolean exibirBotaoNovo) {
		this.exibirBotaoNovo = exibirBotaoNovo;
	}

	public Short getCodigoNecessidade() {
		return codigoNecessidade;
	}

	public void setCodigoNecessidade(Short codigoNecessidade) {
		this.codigoNecessidade = codigoNecessidade;
	}

	public AghUnidadesFuncionais getUnidadeExecutora() {
		return unidadeExecutora;
	}

	public void setUnidadeExecutora(AghUnidadesFuncionais unidadeExecutora) {
		this.unidadeExecutora = unidadeExecutora;
	}

	public String getDescricaoNecessidade() {
		return descricaoNecessidade;
	}

	public void setDescricaoNecessidade(String descricaoNecessidade) {
		this.descricaoNecessidade = descricaoNecessidade;
	}

	public DominioSituacao getSituacaoNecessidade() {
		return situacaoNecessidade;
	}

	public void setSituacaoNecessidade(DominioSituacao situacaoNecessidade) {
		this.situacaoNecessidade = situacaoNecessidade;
	}

	public DominioSimNao getRequerDescricao() {
		return requerDescricao;
	}

	public void setRequerDescricao(DominioSimNao requerDescricao) {
		this.requerDescricao = requerDescricao;
	}

	public MbcNecessidadeCirurgica getItemExclusao() {
		return itemExclusao;
	}

	public void setItemExclusao(MbcNecessidadeCirurgica itemExclusao) {
		this.itemExclusao = itemExclusao;
	}
 


	public DynamicDataModel<MbcNecessidadeCirurgica> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<MbcNecessidadeCirurgica> dataModel) {
	 this.dataModel = dataModel;
	}
}