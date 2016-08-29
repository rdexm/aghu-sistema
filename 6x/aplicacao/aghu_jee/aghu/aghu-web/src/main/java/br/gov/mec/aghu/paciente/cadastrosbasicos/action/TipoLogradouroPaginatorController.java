package br.gov.mec.aghu.paciente.cadastrosbasicos.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.model.AipTipoLogradouros;
import br.gov.mec.aghu.paciente.cadastrosbasicos.business.ICadastrosBasicosPacienteFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;


public class TipoLogradouroPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = -1782480620016633629L;
	private static final String REDIRECT_MANTER_TIPO_LOGRADOURO = "tipoLogradouroCRUD";

	@EJB
	private ICadastrosBasicosPacienteFacade cadastrosBasicosPacienteFacade;

	private AipTipoLogradouros tipoLogradouro = new AipTipoLogradouros();
	
	private AipTipoLogradouros tipoLogradouroSelecionado;

	@Inject
	private TipoLogradouroController tipoLogradouroController;
	
	@Inject @Paginator
	private DynamicDataModel<AipTipoLogradouros> dataModel;
	
	
	@PostConstruct
	public void init(){
		this.begin(this.conversation);
	}
	
	public void pesquisar(){
		reiniciarPaginator();
	}
	
	public String iniciarInclusao(){
		return REDIRECT_MANTER_TIPO_LOGRADOURO;
	}

	public String editar(){
		tipoLogradouroController.iniciarEdicao(tipoLogradouroSelecionado);
		return REDIRECT_MANTER_TIPO_LOGRADOURO;
	}
	
	public void reiniciarPaginator() {
		this.dataModel.reiniciarPaginator();
		
	}
	@Override
	public Long recuperarCount() {
		return cadastrosBasicosPacienteFacade.obterTipoLogradouroCount(tipoLogradouro.getAbreviatura(), tipoLogradouro.getDescricao());
	}

	@Override
	public List<AipTipoLogradouros> recuperarListaPaginada(Integer firstResult, Integer maxResult,
			String orderProperty, boolean asc) {
		return cadastrosBasicosPacienteFacade.pesquisarTipoLogradouro(firstResult, maxResult, tipoLogradouro.getAbreviatura(), tipoLogradouro.getDescricao());
	}
	
	public void excluir() {
		try{
			this.cadastrosBasicosPacienteFacade.removerTipoLogradouro(this.tipoLogradouroSelecionado);
			this.apresentarMsgNegocio(Severity.INFO,
					"MENSAGEM_SUCESSO_REMOCAO_TIPO_LOGRADOURO", this.tipoLogradouroSelecionado.getDescricao());
			this.tipoLogradouroSelecionado = null;
			
			this.reiniciarPaginator();
		}
		catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void limparCampos() {
		tipoLogradouro.setAbreviatura("");
		tipoLogradouro.setDescricao("");
		this.dataModel.setPesquisaAtiva(false);
	}
	//SET's e GET's
	
	public AipTipoLogradouros getTipoLogradouro() {
		return tipoLogradouro;
	}

	public void setTipoLogradouro(AipTipoLogradouros tipoLogradouros) {
		this.tipoLogradouro = tipoLogradouros;
	}

	public AipTipoLogradouros getTipoLogradouroSelecionado() {
		return tipoLogradouroSelecionado;
	}

	public void setTipoLogradouroSelecionado(
			AipTipoLogradouros tipoLogradouroSelecionado) {
		this.tipoLogradouroSelecionado = tipoLogradouroSelecionado;
	}

	public DynamicDataModel<AipTipoLogradouros> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AipTipoLogradouros> dataModel) {
		this.dataModel = dataModel;
	}
	
}