package br.gov.mec.aghu.paciente.cadastrosbasicos.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.model.AipTituloLogradouros;
import br.gov.mec.aghu.paciente.cadastrosbasicos.business.ICadastrosBasicosPacienteFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;


public class TituloLogradouroPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = 5728898839793467486L;
	private static final String REDIRECT_MANTER_TITULO_LOGRADOURO = "tituloLogradouroCRUD";

	@EJB
	private ICadastrosBasicosPacienteFacade cadastrosBasicosPacienteFacade;

	private AipTituloLogradouros tituloLogradouro = new AipTituloLogradouros();
	
	@Inject
	private TituloLogradouroController tituloLogradouroController;
	
	@Inject @Paginator
	private DynamicDataModel<AipTituloLogradouros> dataModel;
	
	private AipTituloLogradouros tituloLogradouroSelecionado;

	@PostConstruct
	public void init(){
		this.begin(this.conversation);
	}
	
	public void pesquisar(){
		reiniciarPaginator();
	}

	public void reiniciarPaginator(){
		dataModel.reiniciarPaginator();
	}
	
	@Override
	public Long recuperarCount() {
		return cadastrosBasicosPacienteFacade.obterTituloLogradouroCount(tituloLogradouro.getCodigo(), tituloLogradouro.getDescricao());
	}

	@Override
	public List<AipTituloLogradouros> recuperarListaPaginada(Integer firstResult, Integer maxResult,
			String orderProperty, boolean asc) {
		return cadastrosBasicosPacienteFacade.pesquisarTituloLogradouro(firstResult, maxResult, tituloLogradouro.getCodigo(), tituloLogradouro.getDescricao()); 
	}
	
	public String iniciarInclusao(){
		return REDIRECT_MANTER_TITULO_LOGRADOURO;
	}

	public String editar(){
		tituloLogradouroController.iniciarEdicao(tituloLogradouroSelecionado);
		return REDIRECT_MANTER_TITULO_LOGRADOURO;
	}
	
	public void excluir() {
		try{
			
			this.cadastrosBasicosPacienteFacade.removerTituloLogradouro(this.tituloLogradouroSelecionado);
			
			this.apresentarMsgNegocio(Severity.INFO,
					"MENSAGEM_SUCESSO_REMOCAO_TITULO_LOGRADOURO", this.tituloLogradouroSelecionado.getDescricao());
			this.tituloLogradouroSelecionado = null;
			
			this.reiniciarPaginator();
		}
		catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void limparCampos() {
		tituloLogradouro.setCodigo(null);
		tituloLogradouro.setDescricao("");
		this.dataModel.setPesquisaAtiva(false);
	}
	
	//SET's e GET's
	
	public AipTituloLogradouros getTituloLogradouro() {
		return tituloLogradouro;
	}

	public void setTituloLogradouro(AipTituloLogradouros tituloLogradouros) {
		this.tituloLogradouro = tituloLogradouros;
	}

	public DynamicDataModel<AipTituloLogradouros> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AipTituloLogradouros> dataModel) {
		this.dataModel = dataModel;
	}

	public AipTituloLogradouros getTituloLogradouroSelecionado() {
		return tituloLogradouroSelecionado;
	}

	public void setTituloLogradouroSelecionado(
			AipTituloLogradouros tituloLogradouroSelecionado) {
		this.tituloLogradouroSelecionado = tituloLogradouroSelecionado;
	}	
}